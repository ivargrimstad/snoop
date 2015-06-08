#!/bin/bash

docker exec $1 /opt/jboss/wildfly/bin/jboss-cli.sh --connect "/subsystem=logging/console-handler=CONSOLE:change-log-level(level="CONFIG")"
docker exec $1 /opt/jboss/wildfly/bin/jboss-cli.sh --connect "/subsystem=logging/logger=eu.agilejava.snoop:add(level=CONFIG)"
