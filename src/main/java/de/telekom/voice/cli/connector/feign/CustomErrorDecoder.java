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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.telekom.voice.cli.connector.InvokeResponse;
import de.telekom.voice.cli.connector.dto.ErrorResultDto;
import de.telekom.voice.cli.connector.dto.InvokeResultDto;
import de.telekom.voice.cli.connector.exception.ErrorException;
import de.telekom.voice.cli.connector.exception.InvokeException;
import de.telekom.voice.cli.connector.exception.TechnicalApiException;
import de.telekom.voice.cli.connector.exception.UnknownApiException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class CustomErrorDecoder implements ErrorDecoder {

    public static final String HEADER_SVH_TRACE_ID = "x-svh-traceid";

    private final ObjectMapper mapper;

    public CustomErrorDecoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private Exception handleError(int status, String json, String traceId) {
        ErrorResultDto errorResultDto = tryDeserialize(json, ErrorResultDto.class);
        if (errorResultDto != null && errorResultDto.getError() != null) {
            return new ErrorException(status, errorResultDto.getError(), traceId);
        }

        InvokeResultDto invokeResultDto = tryDeserialize(json, InvokeResultDto.class);
        if (invokeResultDto != null && invokeResultDto.getStatus() != null && invokeResultDto.getSession() != null && invokeResultDto.getText() != null) {
            return new InvokeException(status, InvokeResponse.fromDto(invokeResultDto));
        }

        return new UnknownApiException(status, json, traceId);
    }

    private <T> T tryDeserialize(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Exception decode(String s, Response response) {
        try {
            String traceId = findTraceIdInHeader(response.headers());
            String json = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
            return handleError(response.status(), json, traceId);
        } catch (IOException e) {
            return new TechnicalApiException("IO exception in error handling", e);
        }
    }

    @Nullable
    private static String findTraceIdInHeader(Map<String, Collection<String>> headers) {
        Collection<String> headerValues = headers.get(HEADER_SVH_TRACE_ID);
        if (headerValues != null && !headerValues.isEmpty()) {
            return headerValues.iterator().next();
        }
        return null;
    }
}
