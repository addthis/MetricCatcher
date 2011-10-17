#!/bin/sh
JAVA=/usr/java/latest/bin/java
nohup $JAVA -jar lib/metricCatcher-jar-with-dependencies.jar -Dlog4j.configuration=conf/log4j.properties $@ &> $(dirname $0)/../logs/metricCatcher.log &
