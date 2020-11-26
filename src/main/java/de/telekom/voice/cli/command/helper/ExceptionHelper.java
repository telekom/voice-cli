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

package de.telekom.voice.cli.command.helper;

import de.telekom.voice.cli.Context;
import de.telekom.voice.cli.cli.Console;
import de.telekom.voice.cli.connector.dto.ErrorDto;
import de.telekom.voice.cli.connector.dto.InvokeResultDto;
import de.telekom.voice.cli.connector.exception.ApiException;
import de.telekom.voice.cli.connector.exception.ErrorException;
import de.telekom.voice.cli.connector.exception.InvokeException;
import de.telekom.voice.cli.connector.exception.UnknownApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class ExceptionHelper {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHelper.class);

    private final Console console;
    private final SkillResultHelper skillResultHelper;
    private final SessionHelper sessionHelper;

    public ExceptionHelper(Console console, SkillResultHelper skillResultHelper, SessionHelper sessionHelper) {
        this.console = console;
        this.skillResultHelper = skillResultHelper;
        this.sessionHelper = sessionHelper;
    }

    public void handleException(@Nullable Throwable e, Context context) {
        if (e == null) {
            return;
        }

        if (e instanceof ApiException) {
            LOGGER.debug("Trace ID: {}", ((ApiException) e).getTraceId());
        }

        if (e instanceof ErrorException) {
            handleErrorException((ErrorException) e);
        } else if (e instanceof InvokeException) {
            handleInvokeException((InvokeException) e, context);
        } else if (e instanceof UnknownApiException) {
            handleUnknownException((UnknownApiException) e);
        } else {
            LOGGER.error("Exception occurred", e);
        }
    }

    private void handleUnknownException(UnknownApiException e) {
        console.printLine("Something went wrong:");
        console.printFormat("  HTTP status: %d", e.getHttpStatus());
        console.printLine();
        console.printFormat("  Reason: %s", e.getReason());
        console.printLine();
    }

    private void handleInvokeException(InvokeException e, Context context) {
        InvokeResultDto dto = e.getInvokeResponse().getDto();

        console.printLine("Invoke failed with " + dto.getStatus() + " [HTTP " + e.getHttpStatusCode() + "] :(");
        skillResultHelper.printSkillResponse(context, e.getInvokeResponse(), console);
        sessionHelper.handleSession(e.getInvokeResponse(), context);
    }

    private void handleErrorException(ErrorException errorException) {
        ErrorDto error = errorException.getErrorDto();
        console.printLine("Something went wrong:");
        console.printFormat("  Code: %s [HTTP Status %d]", error.getCode(), errorException.getHttpStatusCode());
        console.printLine();
        console.printFormat("  Message: %s", error.getMessage());
        console.printLine();
    }

}
