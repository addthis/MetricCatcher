# Description

MetricCatcher is a bookkeeping agent for application metrics.  It utilizes
Coda Hale's [Metrics](http://github.com/codahale/metrics) package to provide
languages that aren't Java with the easy-to-use tracking & advanced maths of
Metrics.

## What it does

MetricCatcher takes in JSON about things you want to track in your application
and forwards the data on to Graphite or Ganglia for storage & visualization.

## Method of operation

MetricCatcher utilizes Coda Hale's [Metrics](http://github.com/codahale/metrics)
for its own storage of information, statistical analysis, and sending
information along to Graphite or Ganglia.  When MetricCatcher first receives a
metric, it creates the corresponding Java object.  Subsequent messages referring
to a metric of the same name update or increment that metric as appropriate.
Metrics are sent on to the configured metrics server, Ganglia or Graphite, once
very minute.

## Why you might care

We wrote MetricCatcher in order to take advantage of the statistics that Coda
Hale's [Metrics](http://github.com/codahale/metrics) provides in languages that
aren't Java.  It is very useful for things like web applications that don't have
any in-memory persistance (i.e. process-per-request).

## Running MetricCatcher

Builds of MetricCatcher can be found on [Sonatype's OSS repository](https://oss.sonatype.org/content/repositories/releases/com/clearspring/metriccatcher/) or the [The Central Repository](http://search.maven.org/#search|ga|1|a%3A%22metriccatcher%22).  Grab the latest [-dist.zip](http://search.maven.org/remotecontent?filepath=com/clearspring/metriccatcher/0.1.1/metriccatcher-0.1.1-dist.zip) and unpack it.  Uncomment the type of server you'd like MetricCatcher ot send metrics to in conf/config.properties and set the appropriate hostname.  Can start MetricCatcher using the included bin/start-metricCatcher.sh script and then check logs/metriccatcher.log to see if it had any issues starting up.  At the default DEBUG loglevel, MetricCatcher logs every time it gets a metric update, making debugging a cinch.

# Talking to MetricCatcher

MetricCatcher accepts JSON on UDP port 1420 by default.

## A metric

Each metric has a name, type, timestamp, and value. See Coda Hale's [Metrics
documentation](http://metrics.codahale.com/getting-started.html) for details on
the available metric types.  Histograms are either biased (favor more recent
data) or uniform (weight all data equally) and are referred to as such.

    {
        "name":"namespace.metric.name",
        "value":numeric_value,
        "type":"[gauge|counter|meter|biased|uniform|timer]",
        "timestamp":unix_time.millis
    }

## A set of metrics

MetricCatcher expects a list of individual metrics as described above.

    [
        {"name":"foo","value":7,"type":"gauge","timestamp":1320682297.6631},
        {"name":"bar","value":77,"type":"meter","timestamp":1320682297.6631}
    ]

## More info

- [A blog post introducing MetricCatcher](http://www.addthis.com/blog/2012/01/05/advanced-metrics-tracking-for-webapps/)
- [Phetric](https://github.com/clearspring/Phetric): a PHP library for talking to MetricCatcher
- Coda Hale's [Metrics](http://github.com/codahale/metrics): the center of MetricCatcher

# Administrivia

## Author

MetricCatcher was written by [Drew Stephens](http://dinomite.net)
`<drew@dinomite.net>` when at Clearspring. Clearspring is now [AddThis](http://addthis.com).

## License

MetricCatcher is released under the Apache License Version 2.0.  See
[Apache](http://www.apache.org/licenses/LICENSE-2.0) or the LICENSE file
in this distribution for details

## Bugs & so forth

Please report bugs or request new features at the GitHub page for
MetricCatcher: http://github.com/clearspring/MetricCatcher

## Jobs

When this was written, AddThis was hiring; even if the blame on this line is
from long ago, we probably still are.  Check out http://addthis.com/careers if
you're intersted in doing webapps, working with Big Data, and like smart, fun
coworkers.  Clearspring is based just outside of Washington, DC (Tysons Corner)
and has offices in New York, Los Angeles, and beyond.
