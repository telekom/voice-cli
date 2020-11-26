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
import de.telekom.voice.cli.command.Command;
import de.telekom.voice.cli.command.helper.ExceptionHelper;
import de.telekom.voice.cli.command.helper.MetadataHelper;
import de.telekom.voice.cli.command.helper.SessionHelper;
import de.telekom.voice.cli.command.helper.SkillResultHelper;
import de.telekom.voice.cli.connector.Connector;
import de.telekom.voice.cli.connector.InvokeResponse;
import de.telekom.voice.cli.connector.dto.STTRequestDto;
import de.telekom.voice.cli.connector.exception.ApiException;

public class InvokeTextCommand implements Command {
    private final Connector connector;
    private final Console console;
    private final SkillResultHelper resultHelper;
    private final SessionHelper sessionHelper;
    private final MetadataHelper metadataHelper;
    private final ExceptionHelper exceptionHelper;

    public InvokeTextCommand(Connector connector, Console console, SkillResultHelper resultHelper, SessionHelper sessionHelper, MetadataHelper metadataHelper, ExceptionHelper exceptionHelper) {
        this.connector = connector;
        this.console = console;
        this.resultHelper = resultHelper;
        this.sessionHelper = sessionHelper;
        this.metadataHelper = metadataHelper;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public String getName() {
        return "invoke-text";
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
    public void invoke(Context context, String args) {
        InvokeResponse response;
        try {
            response = InvokeResponse.fromDto(connector.getInvokeAPI().textJson(context.getAccessToken(), metadataHelper.serialize(context.getMetadata()), context.getDeviceCapabilities(), true, true, sessionHelper.getCurrentSessionId(context), new STTRequestDto(args)));
        } catch (ApiException e) {
            exceptionHelper.handleException(e, context);
            return;
        }

        sessionHelper.handleSession(response, context);
        resultHelper.printSkillResponse(context, response, console);
    }

    @Override
    public String getHelpText() {
        return "invoke-text [text] - Invoke with [text]";
    }
}
