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
package eu.agilejava.snoop.eureka.scan;

import eu.agilejava.snoop.eureka.annotation.EnableEurekaClient;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

/**
 * CDI Extension that scans for @EnableEurkeaClient annotations.
 * 
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
public class SnoopEurekaScannerExtension implements Extension {

   private static final Logger LOGGER = Logger.getLogger("eu.agilejava.snoop");

   private String applicationName;
   private boolean eurekaEnabled;

   void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
      LOGGER.config("Scanning for Eureka clients");
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {

      SnoopEurekaExtensionHelper.setApplicationName(applicationName);
      SnoopEurekaExtensionHelper.isEurekaEnabled(eurekaEnabled);
      LOGGER.config("Finished scanning for Eureka clients");
   }

   <T> void processAnnotatedType(@Observes @WithAnnotations(EnableEurekaClient.class) ProcessAnnotatedType<T> pat) {
      LOGGER.config(() -> "Found @EnableEurekaClient annotated class: " + pat.getAnnotatedType().getJavaClass().getName());
      eurekaEnabled = true;
      applicationName = pat.getAnnotatedType().getAnnotation(EnableEurekaClient.class).applicationName();
      LOGGER.config(() -> "Eureka application name is: " + applicationName);
   }
}
