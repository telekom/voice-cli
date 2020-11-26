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
import de.telekom.voice.cli.command.helper.ArgumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class StoredCommandsCommand implements Command {
    private static final String COMMANDS_FILE = "commands.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(StoredCommandsCommand.class);

    private final Console console;
    private final List<String> commands;
    private final ArgumentHelper argumentHelper;

    public StoredCommandsCommand(Console console, ArgumentHelper argumentHelper) {
        this.console = console;
        this.argumentHelper = argumentHelper;
        commands = loadCommands();
    }

    private List<String> loadCommands() {
        Path file = Paths.get(COMMANDS_FILE);

        if (!Files.exists(file)) {
            return Collections.emptyList();
        }

        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            LOGGER.error("Failed to load stored commands from {}", file.toAbsolutePath());
            LOGGER.error("Details", e);
            return Collections.emptyList();
        }
    }

    @Override
    public String getName() {
        return "!";
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
        if (args == null) {
            if (commands.isEmpty()) {
                console.printLine("No stored commands found. Create a file named commands.txt and define your commands there");
            }

            // If invoked without argument, print known arguments
            int i = 1;
            for (String command : commands) {
                console.printFormat("%d: %s", i, command);
                console.printLine();
                i += 1;
            }
            return;
        }

        Integer num = argumentHelper.parseInteger(console, args);
        if (num == null) {
            return;
        }

        if (num < 1 || num > commands.size()) {
            console.printFormat("Can't issue command %d, only %d commands available", num, commands.size());
            console.printLine();
            return;
        }

        String command = commands.get(num - 1);
        console.printFormat("Issuing command '%s'", command);
        console.printLine();

        context.execute(command);
    }

    @Override
    public String getHelpText() {
        return "! [n] - Executes the stored command with number [n]. If [n] is omitted, print all stored commands";
    }
}
