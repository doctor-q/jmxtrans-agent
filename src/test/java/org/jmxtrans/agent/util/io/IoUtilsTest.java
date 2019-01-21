/*
 * Copyright (c) 2010-2016 the original author or authors
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
package org.jmxtrans.agent.util.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class IoUtilsTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private String createFileUrl(String filePath) {
        // On windows machines we must replace backslashes with forward slashes and add a preceding slash.
        String res = filePath.replace('\\', '/');
        if (!res.startsWith("/")) {
            res = "/" + res;
        }
        return "file://" + res;
    }

    @Test
    public void getFileAsDocumentFromFile() throws Exception {
        Path xmlFile = tmp.newFile("b.xml").toPath();

        Files.write(xmlFile, "<parent></parent>".getBytes(StandardCharsets.UTF_8));

        String filePath = xmlFile.toString();

        System.out.println(filePath);
        Document document = IoUtils.getFileAsDocument(new FileResource(xmlFile.toFile()));
        System.out.println(document.getDocumentElement());
        assertThat(document, notNullValue());
    }

    @Test
    public void getFileAsDocumentFromClasspath() throws Exception {
        Document document = IoUtils.getFileAsDocument(new ClasspathResource("classpath:org/jmxtrans/agent/util/io/a.xml"));
        assertThat(document, notNullValue());
    }
}