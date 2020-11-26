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

package de.telekom.voice.cli.command.helper;

import de.telekom.voice.cli.Context;
import de.telekom.voice.cli.connector.InvokeResponse;
import de.telekom.voice.cli.connector.dto.InvokeResultDto;
import de.telekom.voice.cli.connector.dto.SessionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class SessionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHelper.class);

    private static final String SESSION_ID_KEY = "session-id";

    public void handleSession(InvokeResponse result, Context context) {
        if (result == null) {
            return;
        }
        handleSession(result.getDto(), context);
    }

    public void handleSession(InvokeResultDto dto, Context context) {
        if (dto == null || dto.getSession() == null) {
            return;
        }

        SessionDto session = dto.getSession();
        handleSession(session.isFinished(), session.getId(), context);
    }


    public void handleSession(boolean finished, String sessionId, Context context) {
        if (finished) {
            if (context.removeData(SESSION_ID_KEY)) {
                LOGGER.debug("Session finished");
            }
        } else {
            LOGGER.debug("Current session id is {}", sessionId);
            context.setData(SESSION_ID_KEY, sessionId);
        }
    }

    @Nullable
    public String getCurrentSessionId(Context context) {
        return context.getData(SESSION_ID_KEY, String.class);
    }
}
