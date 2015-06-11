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

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;

/**
 *
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
public class SnoopConfig {
   
    private String applicationName;
   private String applicationHome;
   private String applicationServiceRoot;

   public String getApplicationName() {
      return applicationName;
   }

   public void setApplicationName(String applicationName) {
      this.applicationName = applicationName;
   }

   public String getApplicationHome() {
      return applicationHome;
   }

   public void setApplicationHome(String applicationHome) {
      this.applicationHome = applicationHome;
   }

   public String getApplicationServiceRoot() {
      return applicationServiceRoot;
   }

   public void setApplicationServiceRoot(String applicationServiceRoot) {
      this.applicationServiceRoot = applicationServiceRoot;
   }

   public String toJSON() {

      Writer w = new StringWriter();
      try (JsonGenerator generator = Json.createGenerator(w)) {

         generator.writeStartObject()
                 .write("applicationName", applicationName)
                 .write("applicationHome", applicationHome)
                 .write("applicationServiceRoot", applicationServiceRoot)
                 .writeEnd();
      }

      return w.toString();
   }

   public static SnoopConfig fromJSON(String json) {

      SnoopConfig config = new SnoopConfig();

      try (JsonReader reader = Json.createReader(new StringReader(json))) {

         JsonObject configJson = reader.readObject();

         config.setApplicationName(configJson.getString("applicationName"));
         config.setApplicationHome(configJson.getString("applicationHome"));
         config.setApplicationServiceRoot(configJson.getString("applicationServiceRoot"));
      }

      return config;
   }
}
