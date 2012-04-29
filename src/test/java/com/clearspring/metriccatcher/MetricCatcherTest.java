/*
 * Copyright (C) 2012 Clearspring Technologies, Inc.⋅
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearspring.metriccatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.util.LRUMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.Timer;

public class MetricCatcherTest {
    MetricCatcher metricCatcher;
    JSONMetric jsonMetric;
    String metricName;
    DatagramSocket sendingSocket;
    DatagramSocket listeningSocket;
    Map<String, Metric> metricCache;
    InetAddress localhost;

    @Before
    public void setUp() throws Exception {
        metricCache = new LRUMap<String, Metric>(10, 10);
        listeningSocket = new DatagramSocket();
        metricCatcher = new MetricCatcher(listeningSocket, metricCache);

        jsonMetric = new JSONMetric();
        jsonMetric.setType("meter");
        // The Metrics class caches created metrics; we want fresh ones
        metricName = "foo.bar.baz.metric" + Math.random();
        jsonMetric.setName(metricName);
        jsonMetric.setTimestamp(((int)System.currentTimeMillis() / 1000));

        sendingSocket = new DatagramSocket();
        localhost = InetAddress.getByName("127.0.0.1");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateMetric() {
        Metric metric = metricCatcher.createMetric(jsonMetric);

        assertEquals(Meter.class, metric.getClass());

        Meter meterMetric = ((Meter)metric);
        // All metrics are in minutes :-( plz2fix
        assertEquals(TimeUnit.MINUTES, meterMetric.rateUnit());
    }

    @Test
    public void testCreateMetric_ShortName() {
        String name = "test" + Math.random();
        jsonMetric.setName(name);
        Metric metric = metricCatcher.createMetric(jsonMetric);
        Map<MetricName, Metric> allMetrics = Metrics.defaultRegistry().allMetrics();

        MetricName metricName = new MetricName(name, "", "");
        assertTrue(allMetrics.containsKey(metricName));

        Metric actual = allMetrics.get(metricName);
        assertEquals(metric, actual);
    }

    @Test
    public void testUpdateMetric() {
        Meter metric = (Meter)metricCatcher.createMetric(jsonMetric);

        metricCatcher.updateMetric(metric, 1);
        assertEquals(1, metric.count());
    }

    @Test
    public void testUpdateMetric_MultipleUpdates() {
        Meter metric = (Meter)metricCatcher.createMetric(jsonMetric);

        int count = 7;
        for (int x = 0; x < 7; x++)
            metricCatcher.updateMetric(metric, 1);

        assertEquals(count, metric.count());
    }

    @Test
    public void testUpdateMetric_Meter_MarkWithZeroHasNoEffect() {
        Meter metric = (Meter)metricCatcher.createMetric(jsonMetric);
        metricCatcher.updateMetric(metric, 0);
        assertEquals(0, metric.count());
    }

    @Test
    public void testUpdateMetric_Counter_Increment() {
        jsonMetric.setType("counter");
        Counter metric = (Counter)metricCatcher.createMetric(jsonMetric);

        metricCatcher.updateMetric(metric, 7);
        assertEquals(7, metric.count());
    }

    @Test
    public void testUpdateMetric_Counter_IncrementMultipleTimes() {
        jsonMetric.setType("counter");
        Counter metric = (Counter)metricCatcher.createMetric(jsonMetric);

        int count = 7;
        for (int x = 0; x < 7; x++)
            metricCatcher.updateMetric(metric, 1);

        assertEquals(count, metric.count());
    }

    @Test
    public void testUpdateMetric_Counter_Decrement() {
        jsonMetric.setType("counter");
        Counter metric = (Counter)metricCatcher.createMetric(jsonMetric);

        metricCatcher.updateMetric(metric, -7);
        assertEquals(-7, metric.count());
    }

    @Test
    public void testUpdateMetric_Counter_Clear() {
        jsonMetric.setType("counter");
        Counter metric = (Counter)metricCatcher.createMetric(jsonMetric);

        metricCatcher.updateMetric(metric, 1);
        assertEquals(1, metric.count());

        metricCatcher.updateMetric(metric, 0);
        assertEquals(0, metric.count());
    }

    @Test
    public void testUpdateMetric_Histogram_Biased() {
        jsonMetric.setType("biased");
        Histogram metric = (Histogram)metricCatcher.createMetric(jsonMetric);

        metricCatcher.updateMetric(metric, 1);
        assertEquals(1, metric.count());
    }

    @Test
    public void testUpdateMetric_Histogram_MultipleUpdates() {
        jsonMetric.setType("biased");
        Histogram metric = (Histogram)metricCatcher.createMetric(jsonMetric);

        int count = 7;
        for (int x = 0; x < 7; x++)
            metricCatcher.updateMetric(metric, 1);

        assertEquals(count, metric.count());
    }

    @Test
    public void testRun() throws IOException, InterruptedException {
        String json = "[" +
                         "{\"name\":\"" + metricName + "\"," +
                          "\"value\":1," +
                          "\"type\":\"counter\"," +
                          "\"timestamp\":1316647781}" +
                      "]";
        byte[] jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        metricCatcher.start();
        Thread.sleep(500);
        metricCatcher.shutdown();

        assertTrue(metricCache.containsKey(metricName));
    }

    @Test
    public void testRun_LongTimestamp() throws IOException, InterruptedException {
        String json = "[" +
                         "{\"name\":\"" + metricName + "\"," +
                          "\"value\":1," +
                          "\"type\":\"counter\"," +
                          "\"timestamp\":1316647781.712494}" +
                      "]";
        byte[] jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        metricCatcher.start();
        Thread.sleep(500);
        metricCatcher.shutdown();

        assertTrue(metricCache.containsKey(metricName));
    }

    @Test
    public void testRun_DottedName() throws IOException, InterruptedException {
        metricName = "foo.bar." + metricName;
        String json = "[" +
                         "{\"name\":\"" + metricName + "\"," +
                          "\"value\":1," +
                          "\"type\":\"counter\"," +
                          "\"timestamp\":1316647781}" +
                      "]";
        byte[] jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        metricCatcher.start();
        Thread.sleep(500);
        metricCatcher.shutdown();

        assertTrue(metricCache.containsKey(metricName));
    }

    @Test
    public void testRun_TimerMetric() throws IOException, InterruptedException {
        double minValue = 0.32097400;
        double maxValue = 11111173;
        String json = "[" +
                         "{\"name\":\"" + metricName + "\"," +
                          "\"value\":" + minValue + "," +
                          "\"type\":\"timer\"," +
                          "\"timestamp\":1316647781}," +
                         "{\"name\":\"" + metricName + "\"," +
                          "\"value\":" + maxValue + "," +
                          "\"type\":\"timer\"," +
                          "\"timestamp\":1316647781}" +
                      "]";
        byte[] jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        metricCatcher.start();
        Thread.sleep(500);
        metricCatcher.shutdown();

        double minval = ((Timer)metricCache.get(metricName)).min();
        assertEquals(minValue, minval, 1);
        assertEquals(maxValue, ((Timer)metricCache.get(metricName)).max(), 1);
    }

    @Test
    public void testRun_MultipleUpdatePackets() throws IOException, InterruptedException {
        String json;
        byte[] jsonBytes;
        long timestamp = System.currentTimeMillis() / 1000L;

        json = "[" +
                    "{\"name\":\"" + metricName +
                    "\",\"value\":1," +
                    "\"type\":\"counter\"," +
                    "\"timestamp\":" + timestamp++ + "}" +
                "]";
        jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        json = "[" +
                    "{\"name\":\"" + metricName +
                    "\",\"value\":1," +
                    "\"type\":\"counter\"," +
                    "\"timestamp\":" + timestamp++ + "}" +
               "]";
        jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        metricCatcher.start();
        Thread.sleep(500);
        metricCatcher.shutdown();

        assertEquals(2, ((Counter)metricCache.get(metricName)).count());
    }

    @Test
    public void testRun_MultipleUpdatePackets_Histograms() throws IOException, InterruptedException {
        String json;
        byte[] jsonBytes;
        double minVal = 2;
        double maxVal = 7;
        long timestamp = System.currentTimeMillis() / 1000L;

        json = "[" +
                    "{\"name\":\"" + metricName +
                    "\",\"value\":" + maxVal + "," +
                    "\"type\":\"biased\"," +
                    "\"timestamp\":" + timestamp++ + "}" +
                "]";
        jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        json = "[" +
                    "{\"name\":\"" + metricName +
                    "\",\"value\":" + minVal + "," +
                    "\"type\":\"biased\"," +
                    "\"timestamp\":" + timestamp++ + "}" +
               "]";
        jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        metricCatcher.start();
        Thread.sleep(500);
        metricCatcher.shutdown();

        assertEquals(2, ((Histogram)metricCache.get(metricName)).count());
        assertEquals(minVal, ((Histogram)metricCache.get(metricName)).min(), 0);
        assertEquals(maxVal, ((Histogram)metricCache.get(metricName)).max(), 0);
    }

    @Test
    public void testRun_MultipleUpdatesInOnePacket() throws IOException, InterruptedException {
        String secondMetricName = metricName + "2";
        String json = "[" +
                           "{\"name\":\"" + metricName +
                           "\",\"value\":1," +
                           "\"type\":\"counter\"," +
                           "\"timestamp\":1316647781}," +
                           "{\"name\":\"" + secondMetricName +
                           "\",\"value\":7," +
                           "\"type\":\"meter\"," +
                           "\"timestamp\":1316647781}" +
                      "]";
        byte[] jsonBytes = json.getBytes();
        sendingSocket.send(new DatagramPacket(jsonBytes, jsonBytes.length, localhost, listeningSocket.getLocalPort()));

        metricCatcher.start();
        Thread.sleep(500);
        metricCatcher.shutdown();

        assertEquals(1, ((Counter)metricCache.get(metricName)).count());
        assertEquals(7, ((Meter)metricCache.get(secondMetricName)).count());
    }
}
