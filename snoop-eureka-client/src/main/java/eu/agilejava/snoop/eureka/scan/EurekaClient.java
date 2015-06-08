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

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Registers with Eureka and gives heartbeats every 10 second.
 * 
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
@Singleton
@Startup
public class EurekaClient {

   private static final Logger LOGGER = Logger.getLogger("eu.agilejava.snoop");
   private static final String DEFAULT_SERVICE_URI = "http://localhost:8761/eureka/";

   private String applicationName;
   private String serviceUrl;
   private String applicationHome;
   
   @Resource
   private TimerService timerService;

   @PostConstruct
   private void init() {

      LOGGER.config("Checking if snoop eureka is enabled");
      LOGGER.config(() -> "YES: " + SnoopEurekaExtensionHelper.isEurekaEnabled());

      if (SnoopEurekaExtensionHelper.isEurekaEnabled()) {

         readProperties();

         EurekaConfig eurekaConfig = new EurekaConfig();
         eurekaConfig.setHostName(applicationName);
         eurekaConfig.setApp(applicationName);
         eurekaConfig.setIpAddr("localhost");
         eurekaConfig.setPort(8080);
         eurekaConfig.setStatus("UP");
         eurekaConfig.setHomePageUrl(applicationHome);
         
         Entity<InstanceConfig> entity = Entity.entity(new InstanceConfig(eurekaConfig), MediaType.APPLICATION_JSON);

         Response response = ClientBuilder.newClient()
                 .target(serviceUrl + "apps/" + applicationName)
                 .request()
                 .post(entity);

         LOGGER.config(() -> "POST resulted in: " + response.getStatus() + ", " + response.getEntity());

         ScheduleExpression schedule = new ScheduleExpression();
         schedule.second("*/10").minute("*").hour("*").start(Calendar.getInstance().getTime());

         TimerConfig config = new TimerConfig();
         config.setPersistent(false);

         Timer timer = timerService.createCalendarTimer(schedule, config);

         LOGGER.config(() -> timer.getSchedule().toString());

      } else {
         LOGGER.config("Snoop Eureka is not enabled. Use @EnableEurekaClient!");
      }
   }

   @Timeout
   public void health(Timer timer) {
      LOGGER.config(() -> "health update: " + Calendar.getInstance().getTime());
      LOGGER.config(() -> "Next: " + timer.getNextTimeout());

      EurekaConfig eurekaConfig = new EurekaConfig();
      eurekaConfig.setStatus("UP");
      Entity<InstanceConfig> entity = Entity.entity(new InstanceConfig(eurekaConfig), MediaType.APPLICATION_JSON);

      Response response = ClientBuilder.newClient()
              .target(serviceUrl + "apps/" + applicationName + "/" + applicationName)
              .request()
              .put(entity);

      LOGGER.config(() -> "PUT resulted in: " + response.getStatus() + ", " + response.getEntity());

   }

   @PreDestroy
   private void deregister() {

      Response response = ClientBuilder.newClient()
              .target(serviceUrl + "apps/" + applicationName + "/" + applicationName)
              .request()
              .delete();

      LOGGER.config(() -> "DELETE resulted in: " + response.getStatus() + ", " + response.getEntity());
   }

   private void readProperties() {
      Yaml yaml = new Yaml();
      Map<String, Object> props = (Map<String, Object>) yaml.load(this.getClass().getResourceAsStream("/application.yml"));

      Map<String, String> snoopConfig = (Map<String, String>) props.get("snoop");

      applicationName = snoopConfig.get("applicationName");
      applicationHome = snoopConfig.get("applicationHome");

      LOGGER.config(() -> "application name: " + applicationName);
      
      Map<String, Object> eurekaProps = (Map<String, Object>) props.get("eureka");
      Map<String, Object> eurekaClientProps = (Map<String, Object>) eurekaProps.get("client");
      Map<String, String> eurekaServiceProps = (Map<String, String>) eurekaClientProps.get("serviceUrl");

      serviceUrl = eurekaServiceProps.get("deafaultZone") != null ? snoopConfig.get("defaultZone") : DEFAULT_SERVICE_URI;
   }
}
