/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.service.exceptions;

import org.perfrepo.web.util.MessageUtils;

import javax.ejb.ApplicationException;

/**
 * Exception in service layer.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@ApplicationException(rollback = true)
public class ServiceException extends Exception {

   private static final long serialVersionUID = 4888320719223847688L;

   private String keyToResourceBundle;
   private Object[] params;

   /**
    * @param keyToResourceBundle used for GUI
    * @param params
    */
   public ServiceException(String keyToResourceBundle, String... params) {
      this(keyToResourceBundle, null, params);
   }

   /**
    * @param keyToResourceBundle used for GUI
    * @param params
    */
   public ServiceException(String keyToResourceBundle, Throwable cause, String... params) {
      super(MessageUtils.getMessage(keyToResourceBundle, params), cause);
      this.keyToResourceBundle = keyToResourceBundle;
      this.params = params;
   }

   public Object[] getParams() {
      return params;
   }

   public String getKeyToResourceBundle() {
      return keyToResourceBundle;
   }
}
