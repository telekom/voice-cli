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

import de.telekom.voice.cli.connector.dto.TokenDto;
import de.telekom.voice.cli.connector.dto.TokensDto;
import de.telekom.voice.cli.connector.exception.ApiException;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * Offered by CTS and User Management.
 */
public interface TokenAPI {
    @RequestLine("POST /api/v1/token/oauth2")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json; charset=utf-8",
            "Authorization: Bearer {token}"
    })
    void addToken(@Param("token") String accessToken, TokenDto payload) throws ApiException;

    @RequestLine("DELETE /api/v1/token/{id}")
    @Headers({
            "Accept: application/json",
            "Authorization: Bearer {token}"
    })
    void deleteToken(@Param("token") String accessToken, @Param("id") String tokenId) throws ApiException;

    @RequestLine("GET /api/v1/token")
    @Headers({
            "Accept: application/json",
            "Authorization: Bearer {token}"
    })
    TokensDto listAllTokens(@Param("token") String accessToken) throws ApiException;
}
