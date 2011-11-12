#!/bin/sh
METRICCATCHER_HOME=`dirname $0`/../
JAVA=/usr/java/latest/bin/java
if [ ! -f $JAVA ]; then
    echo "java not found at $JAVA (set it within $0)"
    exit 1
fi
nohup $JAVA -Dlog4j.configuration=$METRICCATCHER_HOME/conf/log4j.properties -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8089 -Dcom.sun.management.jmxremote.authenticate=false -jar $METRICCATCHER_HOME/lib/metricCatcher-jar-with-dependencies.jar -c $METRICCATCHER_HOME/conf/config.properties $@ &> $METRICCATCHER_HOME/logs/metricCatcher.log &
