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

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * Registers with Snoop and gives heartbeats every 10 second.
 *
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
@ClientEndpoint
@Singleton
public class SnoopApplicationClient {

   private static final Logger LOGGER = Logger.getLogger("eu.agilejava.snoop");
   private static final String LOOKUP_ENDPOINT = "lookup";
   private static final String STATUS_ENDPOINT = "snoopstatus/";

   private String serviceUrl;

   @PostConstruct
   private void init() {

   }

   public void lookup(final String applicationName) {
      
      sendMessage(LOOKUP_ENDPOINT, applicationName);

   }

   /**
    * Handles incoming message.
    *
    * @param session The WebSocket session
    * @param message The message
    */
   @OnMessage
   public void onMessage(Session session, String message) {
      LOGGER.config(() -> "Message: " + message);
      
   }


   /**
    * Sends message to the WebSocket server.
    *
    * @param endpoint The server endpoint
    * @param msg The message
    * @return a return message
    */
   private String sendMessage(String endpoint, String msg) {
      
      LOGGER.config(() -> "Sending message: " + msg);

      String returnValue = "-1";
      try {
         WebSocketContainer container = ContainerProvider.getWebSocketContainer();
         String uri = serviceUrl + endpoint;
         Session session = container.connectToServer(this, URI.create(uri));
         session.getBasicRemote().sendText(msg != null ? msg : "");
         returnValue = session.getId();

      } catch (DeploymentException | IOException ex) {
         LOGGER.warning(ex.getMessage());
      }

      return returnValue;
   }
}
