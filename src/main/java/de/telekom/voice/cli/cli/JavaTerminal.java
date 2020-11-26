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

import de.telekom.voice.cli.command.ExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JavaTerminal implements Terminal {
    private final BufferedReader reader;
    private final PrintWriter writer;

    public JavaTerminal() {
        reader = new BufferedReader(new InputStreamReader(System.in, UTF_8));
        writer = new PrintWriter(System.out);
    }

    @Override
    public void print(String text) {
        writer.print(text);
    }

    @Override
    public void println(String text) {
        writer.println(text);
    }

    @Override
    public void println() {
        writer.println();
    }

    @Override
    public void printf(String format, Object... args) {
        writer.printf(format, args);
    }

    @Override
    public void flush() {
        writer.flush();
    }

    @Override
    public String readLine() throws ExecutionException {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new ExecutionException("Failed to read from terminal", e);
        }
    }

    @Override
    public String readLine(char mask) throws ExecutionException {
        return readLine();
    }

    @Override
    public void close() {
        // Nothing to do here
    }

    @Override
    public void setCommandsSupplier(CommandsSupplier commandsSupplier) {
        // Nothing to do here
    }
}
