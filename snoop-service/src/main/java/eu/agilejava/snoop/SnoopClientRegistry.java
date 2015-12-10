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
package eu.agilejava.snoop;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Singleton;
import static java.util.Calendar.getInstance;

/**
 * Register of clients that have registered themselves. Automatically disregarded after a minute without heartbeat.
 *
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
@Singleton
public class SnoopClientRegistry {

   private static final Logger LOGGER = Logger.getLogger("eu.agilejava.snoop");

   private final Map<String, Long> clients = new ConcurrentHashMap<>();
   private final Map<String, SnoopConfig> clientConfigurations = new ConcurrentHashMap<>();

   public void register(final SnoopConfig client) {
      Calendar now = getInstance();
      clients.put(client.getServiceName(), now.getTimeInMillis());
      clientConfigurations.put(client.getServiceName(), client);

      LOGGER.config(() -> "Client: " + client.getServiceName() + " registered up at " + now.getTime());
   }

   public void deRegister(final String clientId) {
      clients.remove(clientId);
      clientConfigurations.remove(clientId);

      LOGGER.warning(() -> "Client: " + clientId + " deregistered at " + Calendar.getInstance().getTime());
   }

   public Set<String> getClients() {

      return clients.keySet().stream()
              .filter(c -> clients.get(c) > System.currentTimeMillis() - 60000)
              .collect(Collectors.toSet());
   }
   
   public Collection<SnoopConfig> getServiceConfigs() {
      return clientConfigurations.values();
   }

   public Optional<SnoopConfig> getClientConfig(String clientId) {

      if (getClients().contains(clientId)) {

         return Optional.ofNullable(clientConfigurations.get(clientId));

      } else {
         return Optional.empty();
      }
   }
}
