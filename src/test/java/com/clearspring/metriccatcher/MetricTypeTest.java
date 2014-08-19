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

import com.yammer.metrics.core.Histogram;

public class MetricTypeTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFromName() {
        MetricType res = MetricType.fromName("meter");
        assertEquals(MetricType.METER, res);
    }

    @Test
    public void testFromName_Invalid() {
        MetricType res = MetricType.fromName("foo");
        assertEquals(null, res);
    }

    @Test
    public void testKlass() {
        assertEquals(Histogram.class, MetricType.HISTOGRAM_BIASED.getKlass());
    }
}
