package org.jmxtrans.agent.influxdb;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InfluxMetricTest {

    private static final String NOTNULL = "";
    private static final List<InfluxTag> NOTNULLLIST = Collections.emptyList();


    @Test
    public void testGetValue() {
        assertEquals("\"simple\"", new InfluxMetric(NOTNULL, NOTNULLLIST, "simple", 0).getValue());
        assertEquals("\"\\\"quoted\\\"\"", new InfluxMetric(NOTNULL, NOTNULLLIST, "\"quoted\"", 0).getValue());
        assertEquals("\"multi\\\\nline\"", new InfluxMetric(NOTNULL, NOTNULLLIST, "multi\\nline", 0).getValue());
    }

}
