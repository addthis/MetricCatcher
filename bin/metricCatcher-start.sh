#!/bin/sh
METRICCATCHER_HOME=../`dirname $0`
JAVA=/usr/java/latest/bin/java
nohup $JAVA -Dlog4j.configuration=$METRICCATCHER_HOME/conf/log4j.properties -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8089 -Dcom.sun.management.jmxremote.authenticate=false -jar lib/metricCatcher-jar-with-dependencies.jar -c $METRICCATCHER_HOME/conf/config.properties $@ &> $METRICCATCHER_HOME/logs/metricCatcher.log &
