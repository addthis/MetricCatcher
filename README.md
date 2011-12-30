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
Hale's [Metrics](http://github.com/codahale/metrics) provides  in languages that
aren't Java.  It is very useful for things like web applications that don't have
any in-memory persistance (i.e. process-per-request).

# Talking to MetricCatcher

MetricCatcher accepts JSON on UDP port 1420 by default.

## A metric

Each metric has a name, type, timestamp, and value. See Coda Hale's [Metrics
documentation]() for details on the available metric types.

TODO link to Coda Hale's types doc ^^^^^^^^^^^^^^^^

Histogram Metrics have an additional attribute, whether or not they are biased.
See the documention for Coda Hale's Metrics for further details.

    TODO example of single metric from GDoc

## A set of metrics

MetricCatcher expects a list of individual metrics as described above.

    TODO example of series of metrics from GDoc

# Administrivia

## Author

MetricCatcher was written by [Drew Stephens](http://dinomite.net)
<drew@clearspring.com> of [Clearspring](http://clearspring.com).

## License

MetricCatcher is released under the Apache License Version 2.0.

## Bugs & so forth

Please reqport bugs or request new features at the GitHub page for
MetricCatcher: http://github.com/clearspring/MetricCatcher

## Jobs

When this was written, Clearpsirng was hiring; even if the blame on this line is
from long ago, we probably still are.  Check out http://clearspring.com/jobs if
you're intersted in doing webapps, working with Big Data, and like smart, fun
coworkers.  Clearspring is based just outside of Washington, DC (Tysons Corner)
and has offices in New York, Los Angeles, and beyond.
