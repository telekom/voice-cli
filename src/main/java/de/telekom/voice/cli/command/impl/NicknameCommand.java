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
import de.telekom.voice.cli.connector.dto.NicknamesDto;
import de.telekom.voice.cli.connector.exception.ApiException;

public class NicknameCommand implements Command {
    private final Console console;
    private final Connector connector;
    private final ExceptionHelper exceptionHelper;

    public NicknameCommand(Console console, Connector connector, ExceptionHelper exceptionHelper) {
        this.console = console;
        this.connector = connector;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public String getName() {
        return "nickname";
    }

    @Override
    public Arguments getArgs() {
        return Arguments.NONE;
    }

    @Override
    public AuthenticationNeed getAuthenticationNeed() {
        return AuthenticationNeed.YES;
    }

    @Override
    public void invoke(Context context, String args) throws ExecutionException, UserInterruptedException {
        NicknamesDto.NicknameDto nicknameDto;
        try {
            nicknameDto = connector.getUserAPI().getNickname(context.getAccessToken());
        } catch (ApiException e) {
            exceptionHelper.handleException(e, context);
            return;
        }
        console.printLine("Your nickname is " + nicknameDto.getNickname());
    }

    @Override
    public String getHelpText() {
        return getName() + " - Displays user's nickname'";
    }
}
