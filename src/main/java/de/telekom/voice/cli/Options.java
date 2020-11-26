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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

@SuppressWarnings("squid:S00116") // suppress that option fields are ALL_CAPITALS
public final class Options {

    private final CommandLine commandLine;
    private final org.apache.commons.cli.Options allOptions = new org.apache.commons.cli.Options();

    public Options(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        commandLine = parser.parse(allOptions, args);
    }

    void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("voice-cli", allOptions);
    }

    final OptItem HELP = addOption("h", "help", false, "Prints this help");
    final OptItem APIKEY = addOption("a", "apikey", true, "Sets the API key");
    final OptItem IDM_CLIENT_ID = addOption("c", "idm-client-id", true, "Sets the Telekom IDM Client ID");

    public final class OptItem {
        private final String longOpt;

        private OptItem(String longOpt) {
            this.longOpt = longOpt;
        }

        String get() {
            return commandLine.getOptionValue(longOpt);
        }

        boolean isPresent() {
            return commandLine.hasOption(longOpt);
        }
    }

    private OptItem addOption(String opt, String longOpt, boolean hasArg, String description) {
        allOptions.addOption(opt, longOpt, hasArg, description);
        return new OptItem(longOpt);
    }

}
