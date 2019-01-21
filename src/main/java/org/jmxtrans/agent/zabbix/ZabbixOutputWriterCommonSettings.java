/*
 * Copyright (c) 2010-2013 the original author or authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.jmxtrans.agent.zabbix;

import org.jmxtrans.agent.util.net.HostAndPort;

import java.util.Map;

import static org.jmxtrans.agent.util.ConfigurationUtils.getInt;
import static org.jmxtrans.agent.util.ConfigurationUtils.getString;

/**
 * Setting keys and default values common for Graphite Output writers.
 */
public class ZabbixOutputWriterCommonSettings {

    public static final String SETTING_HOST = "host";
    public static final String SETTING_PORT = "port";
    public static final int SETTING_PORT_DEFAULT_VALUE = 2003;
    public static final String SETTING_SERVER_NAME = "serverName";
    public static final String SETTING_BATCH_SIZE = "batchSize";
    public static final int SETTING_BATCH_SIZE_DEFAULT_VALUE = 1000;

    private ZabbixOutputWriterCommonSettings() {
    }

    public static HostAndPort getHostAndPort(Map<String, String> settings) {
        return new HostAndPort(getString(settings, SETTING_HOST),
                getInt(settings, SETTING_PORT, SETTING_PORT_DEFAULT_VALUE));
    }

    public static String getConfiguredHostName(Map<String, String> settings) {
        return getString(settings, SETTING_SERVER_NAME, null);
    }

    public static int getMetricBatchSize(Map<String, String> settings) {
        return getInt(settings, SETTING_BATCH_SIZE, SETTING_BATCH_SIZE_DEFAULT_VALUE);
    }
}
