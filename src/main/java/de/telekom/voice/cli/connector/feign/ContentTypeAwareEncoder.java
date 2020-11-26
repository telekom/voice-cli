/*-
 * #%L
 * Voice CLI
 * %%
 * Copyright (C) 2020 Deutsche Telekom AG
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package de.telekom.voice.cli.connector.feign;

import feign.RequestTemplate;
import feign.codec.Encoder;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Feign uses the given encoder always, regardless of the Content-Type.
 * <p>
 * This encoder supports multiple encoders which are selected on the Content-Type header.
 */
public class ContentTypeAwareEncoder implements Encoder {
    private static final String JSON = "application/json";

    private final Encoder jsonEncoder;
    private final Encoder defaultEncoder;

    public ContentTypeAwareEncoder(Encoder jsonEncoder, Encoder defaultEncoder) {
        this.jsonEncoder = jsonEncoder;
        this.defaultEncoder = defaultEncoder;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {
        String contentType = getContentType(template);
        if (contentType != null && contentType.startsWith(JSON)) {
            jsonEncoder.encode(object, bodyType, template);
            return;
        }

        defaultEncoder.encode(object, bodyType, template);
    }

    @Nullable
    private String getContentType(RequestTemplate request) {
        Collection<String> contentTypes = request.headers().get("Content-Type");
        if (contentTypes == null || contentTypes.isEmpty()) {
            return null;
        }

        return contentTypes.iterator().next();
    }
}
