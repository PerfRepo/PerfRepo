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
package org.perfrepo.web.security;

import org.perfrepo.model.Entity;
import org.perfrepo.web.service.exceptions.UnauthorizedException;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;

@Secured
@Interceptor
public class SecurityInterceptor {

   @Inject
   private AuthorizationService authorizationService;

   @AroundInvoke
   public Object invoke(InvocationContext ctx) throws Exception {
      Object[] params = ctx.getParameters();

      if (params.length > 0) {
         Annotation[][] paramAnnotations = ctx.getMethod().getParameterAnnotations();
         for (int i = 0; i < params.length; i++) {
            AuthEntity authAnnotation = containsAuthAnnotation(paramAnnotations[i]);
            if (authAnnotation != null) {
               Entity<?> entity = (Entity<?>) params[i];
               if (!authorizationService.isUserAuthorizedFor(authAnnotation.accessType(), entity)) {
                  throw new UnauthorizedException(authAnnotation.messageKey(), authAnnotation.messageArgs());
               }
            }
         }
      }
      return ctx.proceed();
   }

   private AuthEntity containsAuthAnnotation(Annotation[] annotations) {
      for (Annotation annotation: annotations) {
         if (annotation.annotationType().equals(AuthEntity.class)) {
            return (AuthEntity) annotation;
         }
      }

      return null;
   }
}
