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

import de.telekom.voice.cli.connector.dto.AccessTokenDto;
import de.telekom.voice.cli.connector.dto.LoginUserRequestDto;
import de.telekom.voice.cli.connector.dto.NicknamesDto;
import de.telekom.voice.cli.connector.dto.UserInfoDto;
import de.telekom.voice.cli.connector.exception.ApiException;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface UserAPI extends TokenAPI {
    @RequestLine("POST /api/v1/login")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json; charset=utf-8"
    })
    AccessTokenDto loginUser(LoginUserRequestDto request) throws ApiException;

    @RequestLine("GET /api/v1")
    @Headers({
            "Accept: application/json",
            "Authorization: Bearer {token}"
    })
    UserInfoDto userInfo(@Param("token") String accessToken) throws ApiException;

    @RequestLine("GET /api/v1/nickname")
    @Headers({
            "Accept: application/json",
            "Authorization: Bearer {token}"
    })
    NicknamesDto.NicknameDto getNickname(@Param("token") String accessToken) throws ApiException;
}
