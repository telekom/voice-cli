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
import de.telekom.voice.cli.connector.Connector;
import de.telekom.voice.cli.connector.dto.HistoryDirection;
import de.telekom.voice.cli.connector.dto.HistoryEntriesDto;
import de.telekom.voice.cli.connector.exception.ApiException;

import java.time.Instant;

public class ListHistoryEntriesCommand implements Command {
    private final Connector connector;
    private final Console console;
    private final ExceptionHelper exceptionHelper;

    public ListHistoryEntriesCommand(Connector connector, Console console, ExceptionHelper exceptionHelper) {
        this.connector = connector;
        this.console = console;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public String getName() {
        return "list-history";
    }

    @Override
    public Arguments getArgs() {
        return Arguments.OPTIONAL;
    }

    @Override
    public AuthenticationNeed getAuthenticationNeed() {
        return AuthenticationNeed.YES;
    }

    @Override
    public void invoke(Context context, String args) {

        int limit = 5;
        Instant fromDate = Instant.now();

        if (args != null) {
            String[] parts = args.split(" ", 2);

            limit = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                fromDate = Instant.parse(parts[1]);
            }
        }
        String accessToken = context.getAccessToken();

        HistoryEntriesDto historyEntries;
        try {
            historyEntries = connector.getHistoryAPI().listHistoryEntries(accessToken, fromDate.toString(), HistoryDirection.BEFORE, limit);
        } catch (ApiException e) {
            exceptionHelper.handleException(e, context);
            return;
        }

        if (historyEntries.getEntries().isEmpty()) {
            console.printLine("No history entries found.");
        } else {
            console.printFormat("The last %d (limit %d) history entries are:\n", historyEntries.getEntries().size(), limit);
            historyEntries.getEntries().forEach(entry -> console.printLine(entry.toString()));
        }
    }

    @Override
    public String getHelpText() {
        return getName() + " [limit] [fromDate] - List recent Event History entries, with optional limit (default 5) and optional fromDate (default now)";
    }
}
