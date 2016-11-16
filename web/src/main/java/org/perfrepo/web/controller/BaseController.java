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
package org.perfrepo.web.controller;

import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.util.MessageUtils;
import org.richfaces.component.SortOrder;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for controllers.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class BaseController implements Serializable {

   private static final long serialVersionUID = -1616863465068425778L;

   private Map<String, SortOrder> sortsOrders = new HashMap<String, SortOrder>();

   private static final String SORT_PROPERTY_PARAMETER = "sortProperty";
   public static final String SESSION_MESSAGES_KEY = "sessionMessages";

   protected static final Severity ERROR = FacesMessage.SEVERITY_ERROR;
   protected static final Severity INFO = FacesMessage.SEVERITY_INFO;

   private boolean multipleSorting = false;

   private List<String> sortPriorities = new ArrayList<String>();

   protected ExternalContext externalContext() {
      return FacesContext.getCurrentInstance().getExternalContext();
   }

   public Map<String, String> getRequestParams() {
      return externalContext().getRequestParameterMap();
   }

   protected String getRequestParam(String name) {
      return getRequestParams().get(name);
   }

   protected Long getRequestParamLong(String name) {
      if (getRequestParams().get(name) == null) {
         return null;
      }

      return new Long(getRequestParams().get(name));
   }

   public String getRequestParam(String name, String defaultValue) {
      String ret = getRequestParam(name);
      if (ret == null) {
         return defaultValue;
      } else {
         return ret;
      }
   }

   public Map<String, SortOrder> getSortsOrders() {
      return sortsOrders;
   }

   public void setSortsOrders(Map<String, SortOrder> sortsOrders) {
      this.sortsOrders = sortsOrders;
   }

   public boolean isMultipleSorting() {
      return multipleSorting;
   }

   public void setMultipleSorting(boolean multipleSorting) {
      this.multipleSorting = multipleSorting;
   }

   public List<String> getSortPriorities() {
      return sortPriorities;
   }

   public void setSortPriorities(List<String> sortPriorities) {
      this.sortPriorities = sortPriorities;
   }

   public void sort() {
      String property = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(SORT_PROPERTY_PARAMETER);
      if (property != null) {
         SortOrder currentPropertySortOrder = sortsOrders.get(property);
         if (multipleSorting) {
            if (!sortPriorities.contains(property)) {
               sortPriorities.add(property);
            }
         } else {
            sortsOrders.clear();
         }
         if (currentPropertySortOrder == null || currentPropertySortOrder.equals(SortOrder.descending)) {
            sortsOrders.put(property, SortOrder.ascending);
         } else {
            sortsOrders.put(property, SortOrder.descending);
         }
      }
   }

   protected void addSessionMessage(Severity severity, String msgKey, Object... params) {
      String msg = MessageUtils.getMessage(msgKey, params);
      addSessionMessage(new FacesMessage(severity, msg, msg));
   }

   protected void addSessionMessage(FacesMessage facesMessage) {
      Map<String, Object> sm = externalContext().getSessionMap();
      @SuppressWarnings("unchecked")
      List<FacesMessage> sessionMsgs = (List<FacesMessage>) sm.get(SESSION_MESSAGES_KEY);
      if (sessionMsgs == null) {
         sessionMsgs = new ArrayList<FacesMessage>();
         sm.put(SESSION_MESSAGES_KEY, sessionMsgs);
      }
      sessionMsgs.add(facesMessage);
   }

   protected void reloadSessionMessages() {
      Map<String, Object> sm = externalContext().getSessionMap();
      @SuppressWarnings("unchecked")
      List<FacesMessage> sessionMsgs = (List<FacesMessage>) sm.get(SESSION_MESSAGES_KEY);
      if (sessionMsgs != null) {
         for (FacesMessage msg : sessionMsgs) {
            FacesContext.getCurrentInstance().addMessage(null, msg);
         }
      }
      sm.remove(SESSION_MESSAGES_KEY);
   }

   protected void redirectWithMessage(String relPath, Severity severity, String msgKey, Object... params) {
      FacesContext fc = FacesContext.getCurrentInstance();
      addSessionMessage(severity, msgKey, params);
      try {
         externalContext().redirect(externalContext().getRequestContextPath() + relPath);
      } catch (IOException e) {
         throw new RuntimeException("Error while redirecting", e);
      }
      fc.renderResponse();
   }

   protected void redirect(String relPath) {
      FacesContext fc = FacesContext.getCurrentInstance();
      try {
         externalContext().redirect(externalContext().getRequestContextPath() + relPath);
      } catch (IOException e) {
         throw new RuntimeException("Error while redirecting", e);
      }
      fc.renderResponse();
   }

   protected void addMessage(Severity severity, String msgKey, Object... params) {
      FacesContext fc = FacesContext.getCurrentInstance();
      String message = MessageUtils.getMessage(msgKey, params);
      fc.addMessage(null, new FacesMessage(severity, message, message));
   }

   protected void addMessage(ServiceException e) {
      FacesContext fc = FacesContext.getCurrentInstance();
      String message = MessageUtils.getMessage(e);
      fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
   }

   protected void addMessage(org.perfrepo.web.security.SecurityException e) {
      FacesContext fc = FacesContext.getCurrentInstance();
      String message = MessageUtils.getMessage(e);
      fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
   }

   protected void addSessionMessage(ServiceException e) {
      String message = MessageUtils.getMessage(e);
      addSessionMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
   }
}
