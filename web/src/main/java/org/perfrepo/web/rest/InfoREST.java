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
package org.perfrepo.web.rest;

import org.perfrepo.web.rest.logging.Logged;
import org.perfrepo.web.service.ApplicationConfiguration;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/info")
@RequestScoped
public class InfoREST {

   @Inject
   private ApplicationConfiguration applicationConfiguration;

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Path("/version")
   @Logged
   public Response get() {
      return Response.ok(applicationConfiguration.getPerfRepoVersion()).build();
   }
}