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

package org.jmxtrans.agent.properties;

import org.jmxtrans.agent.util.Preconditions2;
import org.jmxtrans.agent.util.io.IoRuntimeException;
import org.jmxtrans.agent.util.io.Resource;
import org.jmxtrans.agent.util.io.ResourceFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * {@link PropertiesLoader} based on a {@link Resource}.
 *
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 * @author Kristoffer Erlandsson
 */
public class ResourcePropertiesLoader implements PropertiesLoader {

    private final Resource resource;

    public ResourcePropertiesLoader(@Nonnull Resource resource) {
        this.resource = Preconditions2.checkNotNull(resource, "resources");
    }

    public ResourcePropertiesLoader(@Nonnull String resourcesPath) {
        this.resource = ResourceFactory.newResource(resourcesPath);
    }

    @Override
    public Map<String, String> loadProperties() {
        Properties properties = new Properties();

        try (InputStream in = resource.getInputStream()) {
            properties.load(in);
        } catch (IOException e) {
            throw new IoRuntimeException("Exception loading properties from " + resource, e);
        }

        Map<String, String> result = new HashMap<>(properties.size());
        for (String key : properties.stringPropertyNames()) {
            result.put(key, properties.getProperty(key));
        }
        return result;
    }

    @Override
    public String toString() {
        return "ResourcePropertiesLoader{" +
                "resource=" + resource +
                '}';
    }

    @Override
    public String getDescription() {
        return "PropertiesLoader for " + resource.getDescription();
    }
}
