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

import java.util.Collection;
import java.util.Comparator;

public class HelpCommand implements Command {
    private final Collection<Command> commands;
    private final Console console;

    public HelpCommand(Collection<Command> commands, Console console) {
        this.console = console;
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "help";
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
    public void invoke(Context context, String args) {
        String searchWord = args;

        if (searchWord == null) {
            console.printLine("!! - Repeats the last used command");
            console.printLine("** [n] - Repeats the last used command N times");
        }

        this.commands.stream()
                .filter(command -> searchWord == null || command.getName().contains(searchWord))
                .sorted(Comparator.comparing(Command::getName))
                .forEach(command -> console.printLine(command.getHelpText()));
    }

    @Override
    public String getHelpText() {
        return "help [searchWord] - Prints this help. If the optional [searchWord] is given, it only prints commands that contain it in their name.";
    }
}
