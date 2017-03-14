package org.perfrepo.web.rest.security;

import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@WebFilter(filterName = "RestApiAuthenticationFilter", urlPatterns = {"/rest/json/*"})
public class RestApiAuthenticationFilter implements Filter {

   @Inject
   private Storage storage;

   @Override
   public void init(FilterConfig filterConfig) throws ServletException {

   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
           ServletException {

      HttpServletRequest req = (HttpServletRequest) request;
      String authorization = req.getHeader("Authorization");

      // TODO add support for Basic
      // TODO remove this and exclude this url from filter
      if (((HttpServletRequest) request).getRequestURI().equals("/rest/json/authentication")) {

          chain.doFilter(request, response);
          return;
      }

      if (authorization != null && authorization.contains("Bearer ")) {
         String token = authorization.substring("Bearer ".length()).trim();

         if (storage.token().tokenExists(token)) {
            chain.doFilter(request, response);
         } else {
            unauthorized((HttpServletResponse) response, "Invalid credentials.");
         }

      } else {
         unauthorized((HttpServletResponse) response, "Invalid credentials.");
      }
   }

   private void unauthorized(HttpServletResponse httpResponse, String message) throws IOException {
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
   }

   @Override
   public void destroy() {

   }
}
