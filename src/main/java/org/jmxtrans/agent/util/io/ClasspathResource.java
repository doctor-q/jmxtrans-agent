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
 */

package org.jmxtrans.agent.util.io;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * {@link Resource} for {@code classpath://} path.
 *
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class ClasspathResource extends AbstractResource implements Resource {

    private final String path;

    private final transient ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public ClasspathResource(@Nonnull String path) {
        if (path.startsWith("classpath://")) {
            path = path.substring("classpath://".length());
        } else if (path.startsWith("classpath")) {
            path = path.substring("classpath:".length());
        }
        this.path = path;
    }


    @Override
    public boolean exists() {
        return classLoader.getResource(path) != null;
    }

    @Nonnull
    @Override
    public URL getURL() {
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new NullPointerException("No resource '" + path + "' found in classloader " + classLoader);
        }
        return resource;
    }

    @Nonnull
    @Override
    public File getFile() {
        URI uri = getURI();
        try {
            return new File(uri);
        } catch (RuntimeException e) {
            throw new FileNotFoundRuntimeException("Resource '" + uri + "' can not be resolved as a file", e);
        }
    }

    @Nonnull
    @Override
    public InputStream getInputStream() {
        InputStream resourceAsStream = classLoader.getResourceAsStream(path);
        if (resourceAsStream == null) {
            throw new FileNotFoundRuntimeException(this.toString());
        }
        return resourceAsStream;
    }

    @Override
    public String getDescription() {
        return "Classpath resource: " + path;
    }
}
