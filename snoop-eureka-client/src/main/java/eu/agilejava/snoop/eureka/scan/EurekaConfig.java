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

/**
 * Eureka client configuration.
 * 
 * @author Ivar Grimstad (ivar.grimstad@gmail.com)
 */
public class EurekaConfig {

   private String hostName;
   private String app;
   private String ipAddr;
   private int port;
   private int securePort;
   private String vipAddress;
   private String secureVipAddress;
   private String status;
   private String homePageUrl;
   private String statusPageUrl;
   private String healthCheckUrl;
   private DataCenterInfo dataCenterInfo;

   public class DataCenterInfo {

      public String getName() {
         return "MyOwn";
      }
   }

   public String getHostName() {
      return hostName;
   }

   public void setHostName(String hostName) {
      this.hostName = hostName;
   }

   public String getApp() {
      return app;
   }

   public void setApp(String app) {
      this.app = app;
   }

   public String getIpAddr() {
      return ipAddr;
   }

   public void setIpAddr(String ipAddr) {
      this.ipAddr = ipAddr;
   }

   public int getPort() {
      return port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public int getSecurePort() {
      return securePort;
   }

   public void setSecurePort(int securePort) {
      this.securePort = securePort;
   }

   public String getVipAddress() {
      return vipAddress;
   }

   public void setVipAddress(String vipAddress) {
      this.vipAddress = vipAddress;
   }

   public String getSecureVipAddress() {
      return secureVipAddress;
   }

   public void setSecureVipAddress(String secureVipAddress) {
      this.secureVipAddress = secureVipAddress;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getHomePageUrl() {
      return homePageUrl;
   }

   public void setHomePageUrl(String homePageUrl) {
      this.homePageUrl = homePageUrl;
   }

   public String getStatusPageUrl() {
      return statusPageUrl;
   }

   public void setStatusPageUrl(String statusPageUrl) {
      this.statusPageUrl = statusPageUrl;
   }

   public String getHealthCheckUrl() {
      return healthCheckUrl;
   }

   public void setHealthCheckUrl(String healthCheckUrl) {
      this.healthCheckUrl = healthCheckUrl;
   }

   public DataCenterInfo getDataCenterInfo() {
      return new DataCenterInfo();
   }
}
