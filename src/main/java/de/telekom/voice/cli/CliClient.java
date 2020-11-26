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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.telekom.voice.cli.base.ObjectMapperFactory;
import de.telekom.voice.cli.cli.Console;
import de.telekom.voice.cli.cli.UserInterruptedException;
import de.telekom.voice.cli.command.Command;
import de.telekom.voice.cli.command.ExecutionException;
import de.telekom.voice.cli.command.helper.ArgumentHelper;
import de.telekom.voice.cli.command.helper.ContextOptionsHelper;
import de.telekom.voice.cli.command.helper.ExceptionHelper;
import de.telekom.voice.cli.command.helper.FileHelper;
import de.telekom.voice.cli.command.helper.MetadataHelper;
import de.telekom.voice.cli.command.helper.SessionHelper;
import de.telekom.voice.cli.command.helper.SkillResultHelper;
import de.telekom.voice.cli.command.impl.HelpCommand;
import de.telekom.voice.cli.command.impl.InvokeAudioCommand;
import de.telekom.voice.cli.command.impl.InvokeIntentCommand;
import de.telekom.voice.cli.command.impl.InvokeTextCommand;
import de.telekom.voice.cli.command.impl.ListHistoryEntriesCommand;
import de.telekom.voice.cli.command.impl.ListTokensCommand;
import de.telekom.voice.cli.command.impl.LoginIdmCommand;
import de.telekom.voice.cli.command.impl.NicknameCommand;
import de.telekom.voice.cli.command.impl.PrintPasswordsCommand;
import de.telekom.voice.cli.command.impl.PutTokenCommand;
import de.telekom.voice.cli.command.impl.QuitCommand;
import de.telekom.voice.cli.command.impl.SetAccessTokenCommand;
import de.telekom.voice.cli.command.impl.SetDomainPreferenceCommand;
import de.telekom.voice.cli.command.impl.StoredCommandsCommand;
import de.telekom.voice.cli.command.impl.UserInfoCommand;
import de.telekom.voice.cli.connector.Connector;
import de.telekom.voice.cli.environment.Environment;
import de.telekom.voice.cli.environment.PasswordDatabase;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CliClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliClient.class);
    private static final String REPEAT_LAST_COMMAND = "!!";
    private static final String REPEAT_LAST_COMMAND_N = "**";
    private static final Duration MAXIMUM_CONNECTION_LIFETIME = Duration.ofMinutes(1);

    private final Console console;

    private final Map<String, Command> commands = new HashMap<>();
    private final Context context;
    private final ExceptionHelper exceptionHelper;

    @Nullable
    private String lastCommand;

    public CliClient(Environment environment, String apiKey, Console console, PasswordDatabase passwordDatabase) {
        this.context = new Context(this::execute);
        this.console = console;

        // Register commands tab completion callback
        this.console.setCommandsSupplier(this::supplyCommands);

        ObjectMapper mapper = ObjectMapperFactory.create();

        // Helper
        SessionHelper sessionHelper = new SessionHelper();
        SkillResultHelper skillResultHelper = new SkillResultHelper();
        exceptionHelper = new ExceptionHelper(console, skillResultHelper, sessionHelper);
        ArgumentHelper argumentHelper = new ArgumentHelper();
        MetadataHelper metadataHelper = new MetadataHelper();
        FileHelper fileHelper = new FileHelper();

        // Connectors
        HttpClient httpClient = createHttpClient();

        Connector connector = new Connector(environment.getBaseUrls(), apiKey, mapper, httpClient);

        // Commands
        registerCommand(new QuitCommand());
        registerCommand(new HelpCommand(commands.values(), console));
        registerCommand(new StoredCommandsCommand(console, argumentHelper));
        registerCommand(new InvokeTextCommand(connector, console, skillResultHelper, sessionHelper, metadataHelper, exceptionHelper));
        registerCommand(new InvokeAudioCommand(connector, console, skillResultHelper, fileHelper, sessionHelper, metadataHelper, exceptionHelper));
        registerCommand(new LoginIdmCommand(console, connector, environment.getIdmConfig(), exceptionHelper));
        registerCommand(new InvokeIntentCommand(connector, console, skillResultHelper, sessionHelper, metadataHelper, exceptionHelper));
        registerCommand(new PrintPasswordsCommand(console, passwordDatabase));
        registerCommand(new SetAccessTokenCommand(console));
        registerCommand(new PutTokenCommand(console, connector, mapper, exceptionHelper));
        registerCommand(new ListTokensCommand(console, connector, exceptionHelper));
        registerCommand(new ListHistoryEntriesCommand(connector, console, exceptionHelper));
        registerCommand(new UserInfoCommand(console, connector, exceptionHelper));
        registerCommand(new NicknameCommand(console, connector, exceptionHelper));
        registerCommand(new SetDomainPreferenceCommand(console, connector, exceptionHelper));
    }

    private HttpClient createHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setMaxConnPerRoute(Integer.MAX_VALUE)
                .setMaxConnTotal(Integer.MAX_VALUE)
                .setKeepAliveStrategy((response, ctx) -> MAXIMUM_CONNECTION_LIFETIME.toMillis())
                .setUserAgent(Connector.USER_AGENT);
        return builder.build();
    }


    private Iterable<Command> supplyCommands() {
        return commands.values();
    }

    private void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    /**
     * This pattern is used to get the number of executions for "repeat last command"
     * Matches `**`, `   **   `, `**    222`, etc.
     * Group 1 contains the number of executions if found
     */
    private static final Pattern REGEX_REPEAT_LAST_WITH_COUNT = Pattern.compile(String.format("(?:\\s*)(?:\\%s)(?:\\s*)(\\d*)(?:\\s*)", REPEAT_LAST_COMMAND_N));

    public void run() {
        loadOptions();

        console.printLine("Welcome to the Voice CLI! Type 'help' to see all available commands.");
        context.setRunning(true);

        long commandCounter = 1;

        while (context.isRunning()) {
            String input;
            try {
                input = console.askString(String.format("[%d] Command", commandCounter));
                commandCounter++;
            } catch (ExecutionException e) {
                LOGGER.error("Failed to ask for command", e);
                break;
            } catch (UserInterruptedException e) {
                // User wants to quit
                break;
            }
            if (input == null) {
                continue;
            }

            int numberOfExecutions = 1;
            Matcher lastCommandMatcher = REGEX_REPEAT_LAST_WITH_COUNT.matcher(input);
            if (input.equals(REPEAT_LAST_COMMAND) || lastCommandMatcher.matches()) {
                if (lastCommand == null) {
                    console.printLine("You haven't issued a command yet");
                    console.printLine();
                    continue;
                }
                input = lastCommand;

                if (lastCommandMatcher.group(1) != null && !lastCommandMatcher.group(1).isEmpty()) {
                    numberOfExecutions = Integer.parseUnsignedInt(lastCommandMatcher.group(1));
                }
            } else {
                lastCommand = input;
            }

            try {
                for (int i = 0; i < numberOfExecutions; i++) {
                    if (numberOfExecutions > 1) {
                        console.printLine(String.format("Execution %d / %d: ", i + 1, numberOfExecutions));
                    }

                    execute(input);
                }
            } catch (RuntimeException e) {
                LOGGER.error("Unhandled exception while invoking command", e);
            } catch (UserInterruptedException e) {
                // User wants to quit
                break;
            }
            console.printLine();
        }
        console.printLine("Goodbye!");
    }

    private void loadOptions() {
        ContextOptionsHelper helper = new ContextOptionsHelper();
        Context.Options options = helper.loadFromFile(Paths.get("options.json"));
        context.setOptions(options);
    }

    private void execute(String input) throws UserInterruptedException {
        String[] parts = input.split(" ", 2);
        String command = parts[0].trim();
        String args = parts.length > 1 ? parts[1].trim() : null;
        if (args != null && args.isEmpty()) {
            args = null;
        }

        Command commandObject = commands.get(command);
        if (commandObject == null) {
            console.printFormat("Unknown command '%s' - try 'help'", command);
            console.printLine();
        } else {
            if (commandObject.getArgs() == Command.Arguments.REQUIRED && args == null) {
                console.printFormat("You can't invoke '%s' without arguments", command);
                console.printLine();
                console.printFormat("Usage: %s", commandObject.getHelpText());
                console.printLine();
                return;
            }
            if (commandObject.getArgs() == Command.Arguments.NONE && args != null) {
                console.printFormat("Command '%s' doesn't take arguments", command);
                console.printLine();
                console.printFormat("Usage: %s", commandObject.getHelpText());
                console.printLine();
                return;
            }

            if (commandObject.getAuthenticationNeed() == Command.AuthenticationNeed.YES && !context.hasAccessToken()) {
                console.printLine("You have to be logged in");
                return;
            }

            try {
                commandObject.invoke(context, args);
            } catch (ExecutionException e) {
                console.printLine(e.getMessage());
                exceptionHelper.handleException(e.getCause(), context);
            }
        }
    }
}
