[MetricCatcher](http://github.com/clearspring/MetricCatcher) is a bookkeeping agent for application metrics.  It
utilizes Coda Hale's [Metrics](http://github.com/codahale/metrics) package to provide languages that aren't Java (or
aren't long-running) with the easy-to-use tracking & advanced maths of Metrics.

If you have a Java app and are tracking its performance, you're likely using Coda Hale's Metrics package, which provides
convenient objects for counting happenings in your application.  In other languages you don't have the option of using
this great library, and in web apps that start up a new process for each request, simply keeping the persistent data to
enable metrics like this is a hassle.  That's where MetricCatcher comes in—simply toss values at MetricCatcher and it
will create corresponding Metric objects, allowing your non-Java app to take advantage of Coda Hale's fancy maths.
Metrics in a Java application can be viewed with [Jconsole](http://openjdk.java.net/tools/svc/jconsole/) (or even
better, [VisualVM](http://visualvm.java.net)), but to really realize the power of tracking your application,
MetricCatcher can pump its data into [Graphite](http://graphite.wikidot.com) or
[Ganglia](http://ganglia.sourceforge.net).

# Running MetricCatcher

Grab MetricCatcher from the Clearspring [GitHub repository for MetricCatcher](http://github.com/clearspring/MetricCatcher)

The only configuration that MetricCatcher requires is the location of your Ganglia or Graphite server, which can be
defined in the `conf/config.properties` of the distribution.  MetricCatcher will send metrics to whichever metrics
servers are defined.  Starting & stopping MetricCatcher can be done using the included scripts in the `bin` directory.

# Getting Data In

MetricCatcher listens for JSON on UDP port 1420 for metrics to track--simply feed it lists of Metrics objects, each of
which must have a name, type, timestamp, and value.  MetricCatcher supports all of the types that Coda Hale's [Metrics
provides](http://metrics.codahale.com/getting-started.html).  Note that histograms are either biased (favor more recent
data) or uniform (weight all data equally) and are referred to as such.  The JSON format looks like this:

    [
        {
            "name":"namespace.metric.name",
            "value":numeric_value,
            "type":"[gauge|counter|meter|biased|uniform|timer]",
            "timestamp":unix_time.millis
        }
    ]

# Where Data Goes

MetricCatcher sends data on to the metrics-collecting agents defined in the `config.properties` file, so go check out
your Graphite or Ganglia server to see the results.
