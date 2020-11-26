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

package de.telekom.voice.cli.cli;

import de.telekom.voice.cli.command.Command;
import de.telekom.voice.cli.command.ExecutionException;
import org.jline.reader.Candidate;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class JLineTerminal implements Terminal {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JLineTerminal.class);

    private final org.jline.terminal.Terminal terminal;
    private final LineReader lineReader;
    @Nullable
    private CommandsSupplier commandsSupplier;

    public JLineTerminal() {
        try {
            terminal = TerminalBuilder.builder().system(true).jna(true).build();
            lineReader = LineReaderBuilder.builder().terminal(terminal).completer(this::completeCommand).build();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build JLine terminal", e);
        }
    }

    /**
     * Is called from JLine if it needs tab completion.
     */
    private void completeCommand(LineReader lineReader, ParsedLine parsedLine, List<Candidate> candidates) {
        if (commandsSupplier == null) {
            return;
        }

        for (Command command : commandsSupplier.getCommands()) {
            candidates.add(new Candidate(command.getName()));
        }
    }

    @Override
    public void print(String text) {
        terminal.writer().print(text);
    }

    @Override
    public void println(String text) {
        terminal.writer().println(text);
    }

    @Override
    public void println() {
        terminal.writer().println();
    }

    @Override
    public void printf(String format, Object... args) {
        terminal.writer().printf(format, args);
    }

    @Override
    public void flush() {
        terminal.writer().flush();
    }

    @Override
    public String readLine() throws ExecutionException, UserInterruptedException {
        try {
            return lineReader.readLine();
        } catch (UserInterruptException e) {
            throw new UserInterruptedException(e);
        } catch (EndOfFileException e) {
            return null;
        }
    }

    @Override
    public String readLine(char mask) throws ExecutionException, UserInterruptedException {
        try {
            return lineReader.readLine(mask);
        } catch (UserInterruptException e) {
            throw new UserInterruptedException(e);
        } catch (EndOfFileException e) {
            return null;
        }

    }

    @Override
    public void close() {
        try {
            terminal.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close terminal", e);
        }
    }

    @Override
    public void setCommandsSupplier(@Nullable CommandsSupplier commandsSupplier) {
        this.commandsSupplier = commandsSupplier;
    }
}
