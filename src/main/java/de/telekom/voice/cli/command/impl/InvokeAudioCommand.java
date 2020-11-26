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
import de.telekom.voice.cli.command.helper.FileHelper;
import de.telekom.voice.cli.command.helper.MetadataHelper;
import de.telekom.voice.cli.command.helper.SessionHelper;
import de.telekom.voice.cli.command.helper.SkillResultHelper;
import de.telekom.voice.cli.connector.Connector;
import de.telekom.voice.cli.connector.InvokeResponse;
import de.telekom.voice.cli.connector.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class InvokeAudioCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeAudioCommand.class);

    private final Connector connector;
    private final Console console;
    private final SkillResultHelper resultHelper;
    private final FileHelper fileHelper;
    private final SessionHelper sessionHelper;
    private final MetadataHelper metadataHelper;
    private final ExceptionHelper exceptionHelper;

    public InvokeAudioCommand(Connector connector, Console console, SkillResultHelper resultHelper, FileHelper fileHelper, SessionHelper sessionHelper, MetadataHelper metadataHelper, ExceptionHelper exceptionHelper) {
        this.connector = connector;
        this.console = console;
        this.resultHelper = resultHelper;
        this.fileHelper = fileHelper;
        this.sessionHelper = sessionHelper;
        this.metadataHelper = metadataHelper;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public String getName() {
        return "invoke-audio";
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
        Path file = Paths.get(args);
        byte[] audio = fileHelper.readFile(file, console);
        if (audio == null) {
            return;
        }

        InvokeResponse response;
        try {
            response = InvokeResponse.fromDto(connector.getInvokeAPI().audioJson(context.getAccessToken(), metadataHelper.serialize(context.getMetadata()), context.getDeviceCapabilities(), context.getOptions().getWakeUpWord(), true, true, sessionHelper.getCurrentSessionId(context), audio));
        } catch (ApiException e) {
            exceptionHelper.handleException(e, context);
            return;
        }

        sessionHelper.handleSession(response, context);
        resultHelper.printSkillResponse(context, response, console);
    }

    @Override
    public String getHelpText() {
        return "invoke-audio [file] - Invoke with an audio [file]";
    }
}
