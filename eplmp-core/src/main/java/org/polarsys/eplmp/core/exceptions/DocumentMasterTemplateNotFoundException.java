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

package org.polarsys.eplmp.core.exceptions;

import java.text.MessageFormat;
import java.util.Locale;

/**
 *
 * @author Florent Garin
 */
public class DocumentMasterTemplateNotFoundException extends EntityNotFoundException {
    private final String mDocMTemplateId;

    public DocumentMasterTemplateNotFoundException(String pMessage) {
        super(pMessage);
        mDocMTemplateId=null;
    }

    public DocumentMasterTemplateNotFoundException(Locale pLocale, String pDocMTemplateID) {
        this(pLocale, pDocMTemplateID, null);
    }

    public DocumentMasterTemplateNotFoundException(Locale pLocale, String pDocMTemplateId, Throwable pCause) {
        super(pLocale, pCause);
        mDocMTemplateId=pDocMTemplateId;
    }

    @Override
    public String getLocalizedMessage() {
        String message = getBundleDefaultMessage();
        return MessageFormat.format(message,mDocMTemplateId);     
    }
}
