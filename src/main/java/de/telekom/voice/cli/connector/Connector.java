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

package de.telekom.voice.cli.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.telekom.voice.cli.connector.feign.ContentTypeAwareDecoder;
import de.telekom.voice.cli.connector.feign.ContentTypeAwareEncoder;
import de.telekom.voice.cli.connector.feign.CustomErrorDecoder;
import de.telekom.voice.cli.environment.Environment;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.apache.http.client.HttpClient;

public class Connector {

    public static final String USER_AGENT = "telekom-voice-cli";


    private final HistoryAPI historyAPI;
    private final InvokeAPI invokeAPI;
    private final DomainAPI domainAPI;
    private final UserAPI userAPI;

    public Connector(Environment.BaseURLs baseUrls, String apiKey, ObjectMapper mapper, HttpClient client) {
        // builders should not be reused once properties are changed
        Feign.Builder builder = getFeignBuilder(apiKey, mapper, client);

        historyAPI = builder.target(HistoryAPI.class, baseUrls.getCviUrl());
        invokeAPI = builder.target(InvokeAPI.class, baseUrls.getCviUrl());
        domainAPI = builder.target(DomainAPI.class, baseUrls.getCviUrl());
        userAPI = builder.target(UserAPI.class, baseUrls.getUserUrl());
    }

    public HistoryAPI getHistoryAPI() {
        return historyAPI;
    }

    public InvokeAPI getInvokeAPI() {
        return invokeAPI;
    }

    public UserAPI getUserAPI() {
        return userAPI;
    }

    public DomainAPI getDomainAPI() {
        return domainAPI;
    }

    private static Feign.Builder getFeignBuilder(String apiKey, ObjectMapper mapper, HttpClient client) {
        return Feign.builder()
                .logLevel(Logger.Level.FULL)
                .logger(new Slf4jLogger())
                .requestInterceptor(new ApiKeyRequestInterceptor(apiKey))
                .encoder(new ContentTypeAwareEncoder(new JacksonEncoder(mapper), new Encoder.Default()))
                .decoder(new ContentTypeAwareDecoder(new JacksonDecoder(mapper), new Decoder.Default()))
                .errorDecoder(new CustomErrorDecoder(mapper))
                .client(new ApacheHttpClient(client));
    }
}
