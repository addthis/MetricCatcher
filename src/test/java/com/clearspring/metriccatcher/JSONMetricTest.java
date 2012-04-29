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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Timer;

public class JSONMetricTest {
    JSONMetric  jsonMetric;

    @Before
    public void setUp() throws Exception {
        jsonMetric = new JSONMetric();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetType_MetricType() {
        jsonMetric.setType("gauge");
        assertEquals(MetricType.GAUGE, jsonMetric.getType());
    }

    @Test
    public void testSetType_TypeName() {
        jsonMetric.setType("gauge");
        assertEquals(MetricType.GAUGE, jsonMetric.getType());
    }

    @Test
    public void testGetMetricClass_Gauge() {
        jsonMetric.setType("gauge");
        assertEquals(Gauge.class, jsonMetric.getMetricClass());
    }

    @Test
    public void testGetMetricClass_Counter() {
        jsonMetric.setType("counter");
        assertEquals(Counter.class, jsonMetric.getMetricClass());
    }

    @Test
    public void testGetMetricClass_Meter() {
        jsonMetric.setType("meter");
        assertEquals(Meter.class, jsonMetric.getMetricClass());
    }

    @Test
    public void testGetMetricClass_BiasedHistogram() {
        jsonMetric.setType("biased");
        assertEquals(Histogram.class, jsonMetric.getMetricClass());
    }

    @Test
    public void testGetMetricClass_UniformHistogram() {
        jsonMetric.setType("uniform");
        assertEquals(Histogram.class, jsonMetric.getMetricClass());
    }

    @Test
    public void testGetMetricClass_Timer() {
        jsonMetric.setType("timer");
        assertEquals(Timer.class, jsonMetric.getMetricClass());
    }
}
