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
import de.telekom.voice.cli.environment.PasswordDatabase;

public class ConsoleImpl implements Console {
    private static final String PASSWORD_ACCESS_PREFIX = "!";
    private static final String PASSWORD_LIST_COMMAND = "?";
    private static final char PASSWORD_MASK = '*';

    private final Terminal terminal;
    private final PasswordDatabase passwordDatabase;

    public ConsoleImpl(Terminal terminal, PasswordDatabase passwordDatabase) {
        this.terminal = terminal;
        this.passwordDatabase = passwordDatabase;
    }

    @Override
    public String askString(String prompt) throws ExecutionException, UserInterruptedException {
        terminal.print(prompt);
        terminal.println(": ");
        terminal.flush();
        return terminal.readLine();
    }

    @Override
    public String askUsername() throws ExecutionException, UserInterruptedException {
        terminal.println("Username: ");
        terminal.flush();
        return readUsername();
    }

    @Override
    public String askPassword() throws ExecutionException, UserInterruptedException {
        terminal.println("Password: ");
        terminal.flush();
        return readPassword();
    }

    @Override
    public String readUsername() throws ExecutionException, UserInterruptedException {
        while (true) {
            String line = terminal.readLine();
            if (line != null && line.startsWith(PASSWORD_ACCESS_PREFIX)) {
                int num = parseNumFromCredentialPrompt(line);
                if (num == -1) {
                    continue;
                }
                String username = passwordDatabase.getUsername(num - 1);
                printFormat("  using '%s' from password database", username);
                printLine();
                return username;
            } else if (line != null && line.equals(PASSWORD_LIST_COMMAND)) {
                passwordDatabase.printUsernames(this);
            } else {
                return line;
            }
        }
    }

    @Override
    public String readPassword() throws ExecutionException, UserInterruptedException {
        while (true) {
            String line = terminal.readLine(PASSWORD_MASK);
            if (line != null && line.startsWith(PASSWORD_ACCESS_PREFIX)) {
                int num = parseNumFromCredentialPrompt(line);
                if (num == -1) {
                    continue;
                }
                String password = passwordDatabase.getPassword(num - 1);
                printFormat("  using '%s' from password database", star(password));
                printLine();
                return password;
            } else if (line != null && line.equals(PASSWORD_LIST_COMMAND)) {
                passwordDatabase.printUsernames(this);
            } else {
                return line;
            }
        }
    }

    private int parseNumFromCredentialPrompt(String line) {
        int num;
        try {
            num = Integer.parseInt(line.substring(1).trim());
        } catch (NumberFormatException e) {
            printLine("Invalid number.");
            return -1;
        }

        if (num < 1 || num > passwordDatabase.size()) {
            printFormat("The database only contains %d entries. Use ? to list them.", passwordDatabase.size());
            printLine();
            return -1;
        }

        return num;
    }

    private String star(String password) {
        StringBuilder builder = new StringBuilder(password.length());

        for (int i = 0; i < password.length(); i++) {
            builder.append('*');
        }

        return builder.toString();
    }

    @Override
    public String readLine() throws ExecutionException, UserInterruptedException {
        return terminal.readLine();
    }

    @Override
    public void printLine(String text) {
        terminal.println(text);
        terminal.flush();
    }

    @Override
    public void printFormat(String text, Object... args) {
        terminal.printf(text, args);
        terminal.flush();
    }

    @Override
    public void printLine() {
        terminal.println();
        terminal.flush();
    }

    @Override
    public void close() {
        terminal.close();
    }

    @Override
    public void setCommandsSupplier(CommandsSupplier commandsSupplier) {
        terminal.setCommandsSupplier(commandsSupplier);
    }
}
