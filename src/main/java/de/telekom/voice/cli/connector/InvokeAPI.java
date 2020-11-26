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

import de.telekom.voice.cli.connector.dto.IntentRequestDto;
import de.telekom.voice.cli.connector.dto.InvokeResultDto;
import de.telekom.voice.cli.connector.dto.STTRequestDto;
import de.telekom.voice.cli.connector.exception.ApiException;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import javax.annotation.Nullable;

public interface InvokeAPI {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json; charset=utf-8",
            "Authorization: Bearer {token}",
            "X-Client-Metadata: {metadata}",
            "X-Client-Capabilities: {deviceCapabilities}"
    })
    @RequestLine("POST /dm/api/v1/invoke/text/json?intent={intent}&skill={skill}&sessionId={sessionId}")
    InvokeResultDto textJson(@Param("token") String accessToken, @Param("metadata") String metadata, @Param("deviceCapabilities") String deviceCapabilities,
                             @Param("intent") boolean intent, @Param("skill") boolean skill,
                             @Nullable @Param("sessionId") String sessionId, STTRequestDto request) throws ApiException;

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json; charset=utf-8",
            "Authorization: Bearer {token}",
            "X-Client-Metadata: {metadata}",
            "X-Client-Capabilities: {deviceCapabilities}"
    })
    @RequestLine("POST /dm/api/v1/invoke/intent/json?intent={intent}&skill={skill}&sessionId={sessionId}")
    InvokeResultDto intentJson(@Param("token") String accessToken, @Param("metadata") String metadata, @Param("deviceCapabilities") String deviceCapabilities,
                               @Param("intent") boolean intent, @Param("skill") boolean skill,
                               @Nullable @Param("sessionId") String sessionId, IntentRequestDto request) throws ApiException;

    @Headers({
            "Accept: application/json",
            "Content-Type: audio/wav",
            "Authorization: Bearer {token}",
            "X-Client-Metadata: {metadata}",
            "X-Client-Capabilities: {deviceCapabilities}",
            "X-WakeUp-Phrase: {wakeUpPhrase}"
    })
    @RequestLine("POST /dm/api/v1/invoke/audio/json?intent={intent}&skill={skill}&sessionId={sessionId}")
    InvokeResultDto audioJson(@Param("token") String accessToken, @Param("metadata") String metadata, @Param("deviceCapabilities") String deviceCapabilities,
                              @Nullable @Param("wakeUpPhrase") String wakeUpPhrase,
                              @Param("intent") boolean intent, @Param("skill") boolean skill,
                              @Nullable @Param("sessionId") String sessionId,
                              byte[] audioData) throws ApiException;
}
