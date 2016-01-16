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

import java.util.Optional;
import java.util.logging.Logger;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;

/**
 * Client API for calling services registered with Snoop.
 *
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
public class SnoopServiceClient {

    private static final Logger LOGGER = Logger.getLogger("eu.agilejava.snoop");
    private static final String DEFAULT_BASE_URI = "http://localhost:8080/snoop-service/";

    private final String applicationName;
    private final String serviceUrl;

    static final class Builder {

        private final String applicationName;
        private String serviceUrl = DEFAULT_BASE_URI;

        Builder(final String applicationName) {
            this.applicationName = applicationName;
        }

        Builder serviceUrl(final String serviceUrl) {
            this.serviceUrl = serviceUrl;
            return this;
        }

        SnoopServiceClient build() {
            return new SnoopServiceClient(this);
        }
    }

    private SnoopServiceClient(final Builder builder) {
        this.applicationName = builder.applicationName;
        this.serviceUrl = builder.serviceUrl;
        LOGGER.info(() -> "client created for " + applicationName);
    }

    /**
     * Locator to get the service root for the service registered with Snoop.
     *
     * Use this method if the convenience methods simpleXXX are not sufficient or to avoid the extra call to Snoop for
     * every request.
     *
     * @return the serviceRoot
     *
     * @throws SnoopServiceUnavailableException if service is not available
     */
    public WebTarget getServiceRoot() throws SnoopServiceUnavailableException {

        SnoopConfig snoopConfig = getConfigFromSnoop();
        LOGGER.fine(() -> "looking up service for " + applicationName);

        return ClientBuilder.newClient()
                .target(snoopConfig.getServiceHome())
                .path(snoopConfig.getServiceRoot());
    }

    /**
     * Convenience method for making a simple GET request on a resource.
     *
     * Calling this method will result in a call to Snoop to retrieve the current configuration for the service in
     * addition to the actual GET request.
     *
     * @param resourcePath The relative path to the resource
     * @return an optional response that is empty if the service is unavailable.
     */
    public Optional<Response> simpleGet(String resourcePath) {

        Optional<Response> returnValue = Optional.empty();

        try {

            returnValue = Optional.of(getServiceRoot()
                    .path(resourcePath)
                    .request()
                    .get());

        } catch (SnoopServiceUnavailableException e) {
            LOGGER.warning(() -> "Service unavailable for " + applicationName);
        }

        return returnValue;
    }

    /**
     * Convenience method for making a simple DELETE request on a resource.
     *
     * Calling this method will result in a call to Snoop to retrieve the current configuration for the service in
     * addition to the actual DELETE request.
     *
     * @param resourcePath The relative path to the resource
     * @return an optional response that is empty if the service is unavailable.
     */
    public Optional<Response> simpleDelete(String resourcePath) {

        Optional<Response> returnValue = Optional.empty();

        try {

            returnValue = Optional.of(getServiceRoot()
                    .path(resourcePath)
                    .request()
                    .delete());

        } catch (SnoopServiceUnavailableException e) {
            LOGGER.warning(() -> "Service unavailable for " + applicationName);
        }

        return returnValue;
    }

    /**
     * Convenience method for making a simple PUT request on a resource.
     *
     * Calling this method will result in a call to Snoop to retrieve the current configuration for the service in
     * addition to the actual PUT request.
     *
     * @param resourcePath The relative path to the resource
     * @param resource The changes made to this resource
     * @return an optional response that is empty if the service is unavailable.
     */
    public Optional<Response> simplePut(String resourcePath, Object resource) {

        Optional<Response> returnValue = Optional.empty();

        try {

            returnValue = Optional.of(getServiceRoot()
                    .path(resourcePath)
                    .request()
                    .put(Entity.entity(resource, APPLICATION_JSON)));

        } catch (SnoopServiceUnavailableException e) {
            LOGGER.warning(() -> "Service unavailable for " + applicationName);
        }

        return returnValue;
    }

    /**
     * Convenience method for making a simple POST request on a resource.
     *
     * Calling this method will result in a call to Snoop to retrieve the current configuration for the service in
     * addition to the actual POST request.
     *
     * @param resourcePath The relative path to the resource
     * @param resource The new resource
     * @return an optional response that is empty if the service is unavailable.
     */
    public Optional<Response> simplePost(String resourcePath, Object resource) {

        Optional<Response> returnValue = Optional.empty();

        try {

            returnValue = Optional.of(getServiceRoot()
                    .path(resourcePath)
                    .request()
                    .post(Entity.entity(resource, APPLICATION_JSON)));

        } catch (SnoopServiceUnavailableException e) {
            LOGGER.warning(() -> "Service unavailable for " + applicationName);
        }

        return returnValue;
    }

    private SnoopConfig getConfigFromSnoop() throws SnoopServiceUnavailableException {

        try {
            Response response = ClientBuilder.newClient()
                    .target(serviceUrl)
                    .path("api")
                    .path("services")
                    .path(applicationName)
                    .request(APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                return response.readEntity(SnoopConfig.class);
            } else {
                throw new SnoopServiceUnavailableException("Response from \"" + serviceUrl + "\"=" + response.getStatus());
            }

        } catch (ProcessingException e) {
            throw new SnoopServiceUnavailableException(e);
        }
    }
}
