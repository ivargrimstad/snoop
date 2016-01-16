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
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.YAMLException;
import eu.agilejava.snoop.SnoopConfigurationException;
import eu.agilejava.snoop.annotation.Snoop;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * CDI Producer for SnoopServiceClient.
 *
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
@ApplicationScoped
public class SnoopProducer {

    private static final Logger LOGGER = Logger.getLogger("eu.agilejava.snoop");

    private Map<String, Object> snoopConfig = Collections.EMPTY_MAP;

    /**
     * Creates a SnoopServiceClient for the named service.
     *
     * @param ip The injection point
     * @return a configured snoop service client
     */
    @Snoop
    @Produces
    @Dependent
    public SnoopServiceClient lookup(InjectionPoint ip) {

        final String applicationName = ip.getAnnotated().getAnnotation(Snoop.class).serviceName();

        LOGGER.config(() -> "producing " + applicationName);

        String serviceUrl = "http://" + readProperty("snoopService", snoopConfig);
        LOGGER.config(() -> "Service URL: " + serviceUrl);

        return new SnoopServiceClient.Builder(applicationName)
                .serviceUrl(serviceUrl)
                .build();
    }

    private String readProperty(final String key, Map<String, Object> snoopConfig) {

        String property = Optional.ofNullable(System.getProperty(key))
                .orElseGet(() -> {
                    String envProp = Optional.ofNullable(System.getenv(key))
                            .orElseGet(() -> {
                                String confProp = Optional.ofNullable(snoopConfig.get(key))
                                        .orElseThrow(() -> {
                                            return new SnoopConfigurationException(key + " must be configured either in application.yml or as env or system property");
                                        })
                                        .toString();
                                return confProp;
                            });
                    return envProp;
                });

        return property;
    }

    /**
     * Initializes the producer with the Snoop configuration properties.
     */
    @PostConstruct
    private void init() {

        try {
            Yaml yaml = new Yaml();
            Map<String, Object> props = (Map<String, Object>) yaml.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/snoop.yml"));

            snoopConfig = (Map<String, Object>) props.get("snoop");

        } catch (YAMLException e) {
            LOGGER.config(() -> "No configuration file. Using env properties.");
        }
    }
}
