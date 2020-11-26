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

package de.telekom.voice.cli;

import de.telekom.voice.cli.cli.Console;
import de.telekom.voice.cli.cli.ConsoleImpl;
import de.telekom.voice.cli.cli.JLineTerminal;
import de.telekom.voice.cli.cli.JavaTerminal;
import de.telekom.voice.cli.cli.Terminal;
import de.telekom.voice.cli.environment.Environment;
import de.telekom.voice.cli.environment.EnvironmentFactory;
import de.telekom.voice.cli.environment.PasswordDatabase;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Console console = null;

        try {

            Options options = new Options(args);

            if (options.HELP.isPresent()) {
                options.printHelp();
                return;
            }

            String apiKey = options.APIKEY.get();
            if (StringUtils.isBlank(apiKey)) {
                throw new ParseException("No API key provided");
            }
            String idmClientId = options.IDM_CLIENT_ID.get();
            if (StringUtils.isBlank(idmClientId)) {
                throw new ParseException("No IDM Client ID provided");
            }

            Environment environment = buildEnvironment(idmClientId);
            PasswordDatabase passwordDatabase = loadPasswordDatabase();

            console = buildConsole(passwordDatabase);
            new CliClient(environment, apiKey, console, passwordDatabase).run();
        } catch (ParseException e) {
            LOGGER.error("Exception while parsing commandline parameters", e);
        } finally {
            if (console != null) {
                console.close();
            }
        }
    }

    private static Console buildConsole(PasswordDatabase passwordDatabase) {
        Terminal terminal;
        try {
            terminal = new JLineTerminal();
        } catch (IllegalStateException e) {
            LOGGER.warn("Failed to use JLine as terminal, falling back to vanilla Java terminal", e);
            terminal = new JavaTerminal();
        }

        return new ConsoleImpl(terminal, passwordDatabase);
    }

    private static Environment buildEnvironment(String idmClientId) {
        return EnvironmentFactory.createProduction(idmClientId);
    }

    private static PasswordDatabase loadPasswordDatabase() {
        try {
            return PasswordDatabase.loadFromFile();
        } catch (IOException e) {
            LOGGER.error("Failed to load password database", e);
            return PasswordDatabase.empty();
        }
    }
}
