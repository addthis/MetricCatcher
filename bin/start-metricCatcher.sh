#!/bin/sh
# Copyright (C) 2012 Clearspring Technologies, Inc.⋅
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
METRICCATCHER_HOME=`dirname $0`/../

# Use JAVA_HOME if set, otherwise look for java in PATH
if [ -n "$JAVA_HOME" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=java
fi

nohup $JAVA -Dlog4j.configuration=$METRICCATCHER_HOME/conf/log4j.properties -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8089 -Dcom.sun.management.jmxremote.authenticate=false -jar $METRICCATCHER_HOME/lib/metricCatcher-jar-with-dependencies.jar -c $METRICCATCHER_HOME/conf/config.properties $@ &> $METRICCATCHER_HOME/logs/metricCatcher.log &
