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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.telekom.voice.cli.Context;
import de.telekom.voice.cli.cli.Console;
import de.telekom.voice.cli.command.Command;
import de.telekom.voice.cli.command.ExecutionException;
import de.telekom.voice.cli.command.helper.ExceptionHelper;
import de.telekom.voice.cli.connector.Connector;
import de.telekom.voice.cli.connector.TokenAPI;
import de.telekom.voice.cli.connector.dto.TokenDto;
import de.telekom.voice.cli.connector.exception.ApiException;
import de.telekom.voice.cli.connector.exception.ErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class PutTokenCommand implements Command {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PutTokenCommand.class);

    private final Console console;
    private final Connector connector;
    private final ObjectMapper objectMapper;
    private final ExceptionHelper exceptionHelper;

    public PutTokenCommand(Console console, Connector connector, ObjectMapper objectMapper, ExceptionHelper exceptionHelper) {
        this.console = console;
        this.connector = connector;
        this.objectMapper = objectMapper;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public String getName() {
        return "put-token";
    }

    @Override
    public Arguments getArgs() {
        return Arguments.REQUIRED;
    }

    @Override
    public AuthenticationNeed getAuthenticationNeed() {
        return AuthenticationNeed.YES;
    }

    @Override
    public void invoke(Context context, String args) throws ExecutionException {
        putToken(console, exceptionHelper, connector.getUserAPI(), objectMapper, context, args);
    }

    static void putToken(Console console, ExceptionHelper exceptionHelper, TokenAPI api, ObjectMapper objectMapper, Context context, String args) {
        String[] parts = args.split(" ", 2);
        File tokenFile = new File(parts[0]).getAbsoluteFile();
        console.printFormat("Reading token from file %s", tokenFile);
        console.printLine();

        TokenDto tokenDto;
        try {
            tokenDto = objectMapper.readValue(tokenFile, TokenDto.class);
        } catch (IOException e) {
            LOGGER.error("Failed to read token file", e);
            return;
        }

        if (parts.length == 2 && StringUtils.isNotBlank(parts[1])) {
            Duration lifetime = Duration.ofSeconds(Integer.parseInt(parts[1]));
            tokenDto = new TokenDto(tokenDto.getTokenId(), tokenDto.getSkillId(),
                    tokenDto.getAccessToken(), tokenDto.getRefreshToken(),
                    Instant.now().plus(lifetime).toString()
            );
            console.printFormat("Adjusted token valid until to %s", tokenDto.getValidUntil());
            console.printLine();
        }

        console.printFormat("Adding token: %s", tokenDto.toString());
        console.printLine();

        try {
            api.addToken(context.getAccessToken(), tokenDto);
        } catch (ErrorException e) {
            if (e.getErrorDto().getCode().equals("TokenAlreadyExists")) {
                if (!deleteAndAddToken(console, exceptionHelper, api, context, tokenDto)) {
                    return;
                }
            } else {
                exceptionHelper.handleException(e, context);
                return;
            }
        } catch (ApiException e) {
            exceptionHelper.handleException(e, context);
            return;
        }
        console.printLine("Successfully added token");
    }

    private static boolean deleteAndAddToken(Console console, ExceptionHelper exceptionHelper, TokenAPI api, Context context, TokenDto tokenDto) {
        console.printLine("Token already exists, deleting existing and trying again...");
        try {
            api.deleteToken(context.getAccessToken(), tokenDto.getTokenId());
            api.addToken(context.getAccessToken(), tokenDto);
        } catch (ApiException innerE) {
            exceptionHelper.handleException(innerE, context);
            return false;
        }
        return true;
    }

    @Override
    public String getHelpText() {
        return getName() + " [file] [lifetime in secs] - Puts the token loaded from the given file in the user management, with optional lifetime";
    }
}
