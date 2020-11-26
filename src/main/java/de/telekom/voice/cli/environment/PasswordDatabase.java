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

package de.telekom.voice.cli.environment;

import de.telekom.voice.cli.cli.Console;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages username and password pairs.
 */
public class PasswordDatabase {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordDatabase.class);

    private static final String PASSWORD_FILE = "passwords.txt";

    private final List<Entry> entries;

    public PasswordDatabase(List<Entry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public List<String> getUsernames() {
        return entries.stream().map(Entry::getUsername).collect(Collectors.toList());
    }

    public String getUsername(int index) {
        return entries.get(index).getUsername();
    }

    public String getPassword(int index) {
        return entries.get(index).getPassword();
    }

    public int size() {
        return entries.size();
    }

    public void printUsernames(Console console) {
        List<String> usernames = getUsernames();
        if (usernames.isEmpty()) {
            console.printLine("You have no passwords in the database.");
            console.printLine("Create a filed called 'passwords.txt'. Each line contains a username password pair separated by space.");
        } else {
            int num = 1;
            console.printLine("Available passwords:");
            for (String username : usernames) {
                console.printFormat("  %d: %s", num, username);
                console.printLine();
                num++;
            }
        }
    }

    public static PasswordDatabase loadFromFile() throws IOException {
        Path passwordFile = Paths.get(PASSWORD_FILE);
        if (!Files.exists(passwordFile)) {
            return PasswordDatabase.empty();
        }

        try (BufferedReader reader = Files.newBufferedReader(passwordFile)) {
            String line;
            LinkedList<Entry> entries = new LinkedList<>();
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                String[] parts = StringUtils.split(line, " ", 2);
                if (parts.length != 2) {
                    throw new IllegalStateException(String.format("The entry in line %d is malformed. Separate username and password with a space", lineNumber));
                }

                entries.add(new Entry(parts[0], parts[1]));
            }
            return new PasswordDatabase(entries);
        }
    }

    public static PasswordDatabase empty() {
        return new PasswordDatabase(Collections.emptyList());
    }

    private static class Entry {
        private final String username;
        private final String password;

        Entry(String username, String password) {
            this.username = username;
            this.password = password;
        }

        String getUsername() {
            return username;
        }

        String getPassword() {
            return password;
        }
    }
}
