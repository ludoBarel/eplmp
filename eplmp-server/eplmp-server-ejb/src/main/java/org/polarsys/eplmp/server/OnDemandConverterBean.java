/*******************************************************************************
  * Copyright (c) 2017 DocDoku.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    DocDoku - initial API and implementation
  *******************************************************************************/
package org.polarsys.eplmp.server;

import org.polarsys.eplmp.core.common.BinaryResource;
import org.polarsys.eplmp.core.common.User;
import org.polarsys.eplmp.core.document.DocumentIteration;
import org.polarsys.eplmp.core.exceptions.*;
import org.polarsys.eplmp.core.product.PartIteration;
import org.polarsys.eplmp.core.security.UserGroupMapping;
import org.polarsys.eplmp.core.services.*;
import org.polarsys.eplmp.server.converters.OnDemandConverter;
import org.polarsys.eplmp.server.dao.BinaryResourceDAO;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.util.Locale;


/**
 * Resource Getter
 */
@Stateless(name = "OnDemandConverterBean")
public class OnDemandConverterBean implements IOnDemandConverterManagerLocal {

    @Inject
    @Any
    private Instance<OnDemandConverter> documentResourceGetters;

    @Inject
    private IDocumentManagerLocal documentService;

    @Inject
    private IProductManagerLocal productService;

    @Inject
    private IContextManagerLocal contextManager;

    @Inject
    private IUserManagerLocal userManager;

    @PersistenceContext
    private EntityManager em;

    @Override
    public InputStream getDocumentConvertedResource(String outputFormat, BinaryResource binaryResource)
            throws WorkspaceNotFoundException, UserNotActiveException, UserNotFoundException, ConvertedResourceException, WorkspaceNotEnabledException {

        Locale locale = getCallerLocale(binaryResource);
        BinaryResourceDAO binaryResourceDAO = new BinaryResourceDAO(locale, em);
        DocumentIteration docI = binaryResourceDAO.getDocumentHolder(binaryResource);
        OnDemandConverter selectedOnDemandConverter = selectOnDemandConverter(outputFormat, binaryResource);

        if (selectedOnDemandConverter != null) {
            return selectedOnDemandConverter.getConvertedResource(outputFormat, binaryResource, docI, locale);
        }

        return null;
    }

    @Override
    public InputStream getPartConvertedResource(String outputFormat, BinaryResource binaryResource)
            throws WorkspaceNotFoundException, UserNotActiveException, UserNotFoundException, ConvertedResourceException, WorkspaceNotEnabledException {

        Locale locale = getCallerLocale(binaryResource);
        BinaryResourceDAO binaryResourceDAO = new BinaryResourceDAO(locale, em);
        PartIteration partIteration = binaryResourceDAO.getPartHolder(binaryResource);
        OnDemandConverter selectedOnDemandConverter = selectOnDemandConverter(outputFormat, binaryResource);

        if (selectedOnDemandConverter != null) {
            return selectedOnDemandConverter.getConvertedResource(outputFormat, binaryResource, partIteration, locale);
        }

        return null;
    }

    private OnDemandConverter selectOnDemandConverter(String outputFormat, BinaryResource binaryResource) {
        OnDemandConverter selectedOnDemandConverter = null;
        for (OnDemandConverter onDemandConverter : documentResourceGetters) {
            if (onDemandConverter.canConvert(outputFormat, binaryResource)) {
                selectedOnDemandConverter = onDemandConverter;
                break;
            }
        }
        return selectedOnDemandConverter;
    }

    private Locale getCallerLocale(BinaryResource binaryResource) throws UserNotFoundException, WorkspaceNotFoundException, UserNotActiveException, WorkspaceNotEnabledException {
        Locale locale;

        if (contextManager.isCallerInRole(UserGroupMapping.REGULAR_USER_ROLE_ID)) {
            User user = userManager.whoAmI(binaryResource.getWorkspaceId());
            locale = new Locale(user.getLanguage());
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

}
