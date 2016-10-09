/*
 * The MIT License
 *
 * Copyright 2015 Ivar Grimstad (ivar.grimstad@gmail.com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.agilejava.snoop.api;

import eu.agilejava.snoop.SnoopClientRegistry;
import eu.agilejava.snoop.SnoopConfig;
import java.util.Collection;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;

/**
 *
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
@Path("services")
public class ServicesResource {

   @EJB
   private SnoopClientRegistry snoopClientRegistry;

   @GET
   @Produces(APPLICATION_JSON)
   public Response all() {

      final Collection<SnoopConfig> serviceConfigs = snoopClientRegistry.getServiceConfigs();

      return Response.ok(new GenericEntity<Collection<SnoopConfig>>(serviceConfigs) {})
              .header("Access-Control-Allow-Origin", "*").build();
   }

   @GET
   @Produces(APPLICATION_JSON)
   @Path("{serviceId}")
   public Response lookup(@PathParam("serviceId") String serviceId) {

      return Response.ok(snoopClientRegistry.getClientConfig(serviceId)
              .orElseThrow(NotFoundException::new)).build();
   }
}
