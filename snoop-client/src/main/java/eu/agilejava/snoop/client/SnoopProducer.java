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
package eu.agilejava.snoop.client;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import eu.agilejava.snoop.annotation.Snoop;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
@ApplicationScoped
public class SnoopProducer {

   private static final String DEFAULT_BASE_HOST = "localhost:8080/snoop-service/";
   private static final Logger LOGGER = Logger.getLogger("eu.agilejava.snoop");

   private String serviceUrl;

   @Snoop
   @Produces
   @Dependent
   public SnoopDiscoveryClient lookup(InjectionPoint ip) {

      final String applicationName = ip.getAnnotated().getAnnotation(Snoop.class).applicationName();

      LOGGER.config(() -> "producing " + applicationName);

      return new SnoopDiscoveryClient.Builder(applicationName)
              .serviceUrl(serviceUrl)
              .build();
   }

   @PostConstruct
   private void init() {

      Yaml yaml = new Yaml();
      Map<String, Object> props = (Map<String, Object>) yaml.load(this.getClass().getResourceAsStream("/application.yml"));

      Map<String, String> snoopConfig = (Map<String, String>) props.get("snoop");

      serviceUrl = "http://" + (snoopConfig.get("serviceHost") != null ? snoopConfig.get("serviceHost") : DEFAULT_BASE_HOST);

      LOGGER.config(() -> "Service URL: " + serviceUrl);

   }

}
