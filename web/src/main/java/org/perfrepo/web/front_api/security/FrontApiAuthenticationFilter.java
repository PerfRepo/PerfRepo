package org.perfrepo.web.front_api.security;

import org.jboss.resteasy.util.Base64;
import org.perfrepo.web.util.MessageUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "FrontApiAuthenticationFilter", urlPatterns = {"/api/*"})
public class FrontApiAuthenticationFilter implements Filter {

   @Override
   public void init(FilterConfig filterConfig) throws ServletException {

   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
           ServletException {
      HttpServletRequest req = (HttpServletRequest) request;
      String basicLogin = req.getHeader("Authorization");
      String username;
      String password;
      if ((basicLogin != null) && (basicLogin.indexOf("Basic") != -1)) {
         String loginPassword = new String(Base64.decode(basicLogin.substring("Basic ".length()).trim()));
         username = loginPassword.substring(0, loginPassword.indexOf(":"));
         password = loginPassword.substring(loginPassword.indexOf(":") + 1);
         try {
            if (req.getUserPrincipal() == null) {
               req.login(username, password);
            }
         } catch (Exception ex) {
            unauthorized((HttpServletResponse) response,
                    MessageUtils.getMessage(new org.perfrepo.web.security.SecurityException("securityException.wrongCredentials", ex)));
         }
         chain.doFilter(request, response);
      } else {
         unauthorized((HttpServletResponse) response,
                 MessageUtils.getMessage(new org.perfrepo.web.security.SecurityException("securityException.wrongCredentials")));
      }
   }

   private void unauthorized(HttpServletResponse httpResponse, String message) throws IOException {
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
   }

   @Override
   public void destroy() {

   }
}
