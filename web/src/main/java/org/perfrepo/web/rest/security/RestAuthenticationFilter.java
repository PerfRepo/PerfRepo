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
package org.perfrepo.web.rest.security;

import org.jboss.resteasy.util.Base64;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filter used to authenticate rest requests
 * The request should contain basic authentication header
 *
 * @author Pavel Drozd
 */
@WebFilter(filterName = "RestAuthenticationFilter", urlPatterns = {"/rest/*"})
public class RestAuthenticationFilter implements Filter {

   /**
    * @see Filter#init(FilterConfig)
    */
   @Override
   public void init(FilterConfig filterConfig) throws ServletException {

   }

   /**
    * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
    */
   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                                    ServletException {
      HttpServletRequest req = (HttpServletRequest) request;
      String basicLogin = req.getHeader("Authorization");
      String username = null;
      String password = null;
      if ((basicLogin != null) && (basicLogin.indexOf("Basic") != -1)) {
         String loginPassword = new String(Base64.decode(basicLogin.substring("Basic ".length()).trim()));
         username = loginPassword.substring(0, loginPassword.indexOf(":"));
         password = loginPassword.substring(loginPassword.indexOf(":") + 1);
         try {
            if (req.getUserPrincipal() == null) {
               req.login(username, password);
            }
            chain.doFilter(request, response);
         } catch (Exception ex) {
            throw new org.perfrepo.web.security.SecurityException("securityException.wrongCredentials", ex);
         }
      } else {
         throw new org.perfrepo.web.security.SecurityException("securityException.wrongCredentials");
      }
   }

   /**
    * @see Filter#destroy()
    */
   @Override
   public void destroy() {

   }
}
