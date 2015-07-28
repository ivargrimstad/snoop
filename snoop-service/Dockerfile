FROM jboss/wildfly:9.0.1.Final
MAINTAINER Ivar Grimstad (ivar.grimstad@gmail.com)

ADD ./target/snoop-service-1.3.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/
ADD ./src/main/wildfly/standalone-snoop.xml /opt/jboss/wildfly/standalone/configuration/

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-snoop.xml", "-b", "0.0.0.0"]
