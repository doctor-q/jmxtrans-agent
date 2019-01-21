package org.jmxtrans.agent;

import org.jmxtrans.agent.util.io.ClasspathResource;
import org.jmxtrans.agent.util.io.Resource;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RollingFileOutputWriterTest {
    @Test
    public void testWriteQueryResultMulti() throws IOException {
        Path defaultLog = Paths.get("jmxtrans-agent.data");
        Files.deleteIfExists(defaultLog);
        Resource resource = new ClasspathResource("classpath:jmxtrans-config-rolling-multi-test.xml");
        JmxTransConfigurationLoader loader = new JmxTransConfigurationXmlLoader(resource);
        new JmxTransExporter(loader).collectAndExport();
        new JmxTransExporter(loader).collectAndExport();
        List<String> lines = Files.readAllLines(defaultLog, Charset.defaultCharset());
        assertThat(lines, hasSize(5));
        assertThat(lines, contains(
                containsString("jvm.thread"), containsString("os.systemLoadAverage"),
                isEmptyString(),
                containsString("jvm.thread"), containsString("os.systemLoadAverage")
        ));
        Files.deleteIfExists(defaultLog);
    }

    @Test
    public void testWriteQueryResultSingle() throws IOException {
        Path defaultLog = Paths.get("jmxtrans-agent.data");
        Files.deleteIfExists(defaultLog);
        Resource resource = new ClasspathResource("classpath:jmxtrans-config-rolling-single-test.xml");
        JmxTransConfigurationLoader loader = new JmxTransConfigurationXmlLoader(resource);
        new JmxTransExporter(loader).collectAndExport();
        new JmxTransExporter(loader).collectAndExport();
        List<String> lines = Files.readAllLines(defaultLog, Charset.defaultCharset());
        assertThat(lines, hasSize(2));
        assertThat(lines, contains(
                allOf(containsString("jvm.thread="), containsString("os.systemLoadAverage=")),
                allOf(containsString("jvm.thread="), containsString("os.systemLoadAverage="))
        ));
        Files.deleteIfExists(defaultLog);
    }
}
