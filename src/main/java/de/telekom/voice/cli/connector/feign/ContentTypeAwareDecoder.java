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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * Feign uses the given decoder always, regardless of the Content-Type.
 * <p>
 * This decoder supports multiple decoders which are selected on the Content-Type header.
 */
public class ContentTypeAwareDecoder implements Decoder {
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static final String JSON = "application/json";

    private final Decoder jsonDecoder;
    private final Decoder defaultDecoder;

    public ContentTypeAwareDecoder(Decoder jsonDecoder, Decoder defaultDecoder) {
        this.jsonDecoder = jsonDecoder;
        this.defaultDecoder = defaultDecoder;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response.body() == null) {
            return null;
        }
        if (type.equals(byte[].class)) {
            return IOUtils.toByteArray(response.body().asInputStream());
        }
        if (type.equals(Response.class)) {
            return response;
        }

        String contentType = getContentType(response);

        if (contentType != null && contentType.startsWith(JSON)) {
            return decodeJson(response, type);
        }

        if (type.equals(String.class)) {
            return IOUtils.toString(response.body().asReader(StandardCharsets.UTF_8));
        }
        return defaultDecoder.decode(response, type);
    }

    private Object decodeJson(Response response, Type type) throws IOException {
        if (type.equals(String.class)) {
            return prettifyJson(response.body().asInputStream());
        }

        return jsonDecoder.decode(response, type);
    }

    private String prettifyJson(InputStream body) throws IOException {
        JsonNode node = objectMapper.readTree(body);
        return objectMapper.writeValueAsString(node);
    }

    @Nullable
    private String getContentType(Response response) {
        Collection<String> contentTypes = response.headers().get("Content-Type");
        if (contentTypes == null || contentTypes.isEmpty()) {
            return null;
        }

        return contentTypes.iterator().next();
    }
}
