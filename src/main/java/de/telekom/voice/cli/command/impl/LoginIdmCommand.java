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

package de.telekom.voice.cli.command.impl;

import de.telekom.voice.cli.Context;
import de.telekom.voice.cli.cli.Console;
import de.telekom.voice.cli.cli.UserInterruptedException;
import de.telekom.voice.cli.command.Command;
import de.telekom.voice.cli.command.ExecutionException;
import de.telekom.voice.cli.command.helper.ExceptionHelper;
import de.telekom.voice.cli.connector.Connector;
import de.telekom.voice.cli.connector.dto.AccessTokenDto;
import de.telekom.voice.cli.connector.dto.LoginUserRequestDto;
import de.telekom.voice.cli.connector.exception.ApiException;
import de.telekom.voice.cli.environment.IdmConfig;
import de.telekom.voice.cli.oauth2.OAuth2Client;
import de.telekom.voice.cli.oauth2.OAuth2ErrorDecoder;
import de.telekom.voice.cli.oauth2.OAuth2Exception;
import de.telekom.voice.cli.oauth2.OAuth2TokenDto;
import feign.Feign;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.slf4j.Slf4jLogger;

import java.util.function.Consumer;

public class LoginIdmCommand implements Command {

    private final Console console;
    private final Connector connector;
    private final IdmConfig idmConfig;
    private final OAuth2Client oauth2Client;
    private final ExceptionHelper exceptionHelper;

    public LoginIdmCommand(Console console, Connector connector, IdmConfig idmConfig, ExceptionHelper exceptionHelper) {
        this.console = console;
        this.connector = connector;
        this.idmConfig = idmConfig;
        this.exceptionHelper = exceptionHelper;
        this.oauth2Client = buildOAuth2Client(idmConfig.getTokenUrl());
    }

    static OAuth2Client buildOAuth2Client(String tokenUrl) {
        JacksonDecoder decoder = new JacksonDecoder();
        return Feign.builder()
                .encoder(new FormEncoder())
                .decoder(decoder)
                .logger(new Slf4jLogger())
                .errorDecoder(new OAuth2ErrorDecoder(decoder))
                .target(OAuth2Client.class, tokenUrl);
    }

    @Override
    public String getName() {
        return "login-idm";
    }

    @Override
    public Arguments getArgs() {
        return Arguments.NONE;
    }

    @Override
    public AuthenticationNeed getAuthenticationNeed() {
        return AuthenticationNeed.NO;
    }

    @Override
    public void invoke(Context context, String args) throws ExecutionException, UserInterruptedException {
        runWithIdmUserCredentials(console, oauth2Client, idmConfig,
                idmToken -> doLoginWithExternalToken(context, idmToken, connector, console, exceptionHelper)
        );
    }

    static void runWithIdmUserCredentials(Console console, OAuth2Client oauth2Client, IdmConfig idmConfig, Consumer<OAuth2TokenDto> idmTokenConsumer) throws ExecutionException, UserInterruptedException {
        String username = console.askUsername();
        if (username == null) {
            return;
        }

        String password = console.askPassword();
        if (password == null) {
            return;
        }

        try {
            OAuth2TokenDto token = oauth2Client.tokenPasswordCredentials("password", idmConfig.getClientId(), idmConfig.getScope(), username, password);
            idmTokenConsumer.accept(token);
        } catch (OAuth2Exception e) {
            console.printFormat("Could not get OAuth2 token: %s", e.getMessage());
            console.printLine();
        }
    }

    static void doLoginWithExternalToken(Context context, OAuth2TokenDto externalToken, Connector connector, Console console, ExceptionHelper exceptionHelper) {
        AccessTokenDto result;
        try {
            LoginUserRequestDto request = new LoginUserRequestDto(null, externalToken.getAccessToken());
            result = connector.getUserAPI().loginUser(request);
        } catch (ApiException e) {
            exceptionHelper.handleException(e, context);
            return;
        }
        context.setAccessToken(result.getToken());
        console.printFormat("Logged in with IDM");
        console.printLine();
    }

    @Override
    public String getHelpText() {
        return "login-idm - Performs an Telekom IDM login with username and password";
    }
}
