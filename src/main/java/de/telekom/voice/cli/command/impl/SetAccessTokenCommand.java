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

public class SetAccessTokenCommand implements Command {
    private final Console console;

    public SetAccessTokenCommand(Console console) {
        this.console = console;
    }

    @Override
    public String getName() {
        return "set-access-token";
    }

    @Override
    public Arguments getArgs() {
        return Arguments.OPTIONAL;
    }

    @Override
    public AuthenticationNeed getAuthenticationNeed() {
        return AuthenticationNeed.NO;
    }

    @Override
    public void invoke(Context context, String args) throws ExecutionException, UserInterruptedException {
        String accessToken = args != null ? args : console.askString("Access token");
        if (accessToken == null) {
            return;
        }

        context.setAccessToken(accessToken);
        context.setUsername("[unknown]");
    }

    @Override
    public String getHelpText() {
        return getName() + " - Sets the access token to simulate a login";
    }
}
