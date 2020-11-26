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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.telekom.voice.cli.cli.UserInterruptedException;
import de.telekom.voice.cli.command.ExecutionException;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Context {
    private boolean running;
    @Nullable
    private String accessToken;
    @Nullable
    private String platformRefreshToken;
    @Nullable
    private String username;
    private final Map<String, Object> data = new HashMap<>();
    private final CommandExecutor commandExecutor;
    private Options options;

    public Context(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Nullable
    public String getAccessToken() {
        return accessToken;
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public void setAccessToken(@Nullable String accessToken) {
        this.accessToken = accessToken;
    }

    public void setPlatformRefreshToken(@Nullable String platformRefreshToken) {
        this.platformRefreshToken = platformRefreshToken;
    }

    @Nullable
    public String getPlatformRefreshToken() {
        return platformRefreshToken;
    }

    public void execute(String input) throws ExecutionException, UserInterruptedException {
        commandExecutor.execute(input);
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    @Nullable
    public <T> T getData(String key, Class<T> clazz) {
        return clazz.cast(data.get(key));
    }

    public boolean removeData(String key) {
        return data.remove(key) != null;
    }

    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        if (options.getSerialNumber() != null) {
            metadata.put("serialNumber", options.getSerialNumber());
        }
        if (options.getDynamicDeviceMetadata() != null) {
            metadata.put("data", options.getDynamicDeviceMetadata());
        }
        return metadata;
    }

    @Nullable
    public String getDeviceCapabilities() {
        return options.getCapabilities();
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }


    interface CommandExecutor {
        void execute(String input) throws ExecutionException, UserInterruptedException;
    }

    public static class Options {
        public static final Options DEFAULT = new Options(null, null, null, null, false, null);

        @Nullable
        private final String serialNumber;

        @Nullable
        private final Map<String, String> dynamicDeviceMetadata;

        @Nullable
        private final String wakeUpWord;

        @Nullable
        private final String capabilities;

        private final boolean debug;

        private final boolean enablePartialTranscriptions;

        @JsonCreator
        public Options(
                @JsonProperty("serialNumber") @Nullable String serialNumber,
                @JsonProperty("dynamicDeviceMetadata") @Nullable Map<String, String> dynamicDeviceMetadata,
                @JsonProperty("wakeUpWord") @Nullable String wakeUpWord,
                @JsonProperty("capabilities") @Nullable String capabilities,
                @JsonProperty("debug") boolean debug,
                @JsonProperty("enablePartialTranscriptions") @Nullable Boolean enablePartialTranscriptions) {
            this.serialNumber = serialNumber;
            this.dynamicDeviceMetadata = dynamicDeviceMetadata;
            this.wakeUpWord = wakeUpWord;
            this.capabilities = capabilities;
            this.debug = debug;
            this.enablePartialTranscriptions = enablePartialTranscriptions == null ? false : enablePartialTranscriptions;
        }

        @Nullable
        public String getSerialNumber() {
            return serialNumber;
        }

        @Nullable
        public String getWakeUpWord() {
            return wakeUpWord;
        }

        @Nullable
        public String getCapabilities() {
            return capabilities;
        }

        public boolean isDebug() {
            return debug;
        }

        public boolean isEnablePartialTranscriptions() {
            return enablePartialTranscriptions;
        }

        public Map<String, String> getDynamicDeviceMetadata() {
            return dynamicDeviceMetadata;
        }
    }
}
