#!/bin/sh
nohup java -jar lib/metricCatcher-jar-with-dependencies.jar -Dlog4j.configuration=conf/log4j.properties $@ &> $(dirname $0)/../logs/metricCatcher.log &
