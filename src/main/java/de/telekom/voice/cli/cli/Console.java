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

import javax.annotation.Nullable;

public interface Console {
    @Nullable
    String askString(String prompt) throws ExecutionException, UserInterruptedException;

    @Nullable
    String askUsername() throws ExecutionException, UserInterruptedException;

    @Nullable
    String askPassword() throws ExecutionException, UserInterruptedException;

    @Nullable
    String readUsername() throws ExecutionException, UserInterruptedException;

    @Nullable
    String readPassword() throws ExecutionException, UserInterruptedException;

    @Nullable
    String readLine() throws ExecutionException, UserInterruptedException;

    void printLine(String text);

    void printFormat(String text, Object... args);

    void printLine();

    void close();

    void setCommandsSupplier(CommandsSupplier commandsSupplier);
}
