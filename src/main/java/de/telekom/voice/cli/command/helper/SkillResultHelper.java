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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.telekom.voice.cli.Context;
import de.telekom.voice.cli.cli.Console;
import de.telekom.voice.cli.connector.InvokeResponse;
import de.telekom.voice.cli.connector.dto.InvokeResultDto;

import javax.annotation.Nullable;
import java.util.Map;

public class SkillResultHelper {
    private final ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.INDENT_OUTPUT);

    public void printSkillResponse(Context context, InvokeResponse result, Console console) {
        InvokeResultDto dto = result.getDto();
        printSkillResponse(context, dto, console);
    }

    public void printSkillResponse(Context context, @Nullable InvokeResultDto dto, Console console) {
        if (dto == null) {
            console.printLine("Got result: null");
            return;
        }

        console.printFormat("Server: %s '%s'", dto.getSession().isFinished() ? "TELL" : "ASK", dto.getText());
        console.printLine();

        if (dto.getSkill() != null && dto.getSkill().getLocal()) {
            console.printLine("Skill requests local execution");
        }

        if (dto.getSkill() != null && !dto.getSkill().getData().isEmpty()) {
            console.printLine("Machine-readable skill result:");
            for (Map.Entry<String, Object> data : dto.getSkill().getData().entrySet()) {
                console.printFormat("  %s: %s", data.getKey(), data.getValue());
                console.printLine();
            }
        }

        if (context.getOptions().isDebug()) {
            printDebugJson(console, dto);
        }
    }

    private void printDebugJson(Console console, InvokeResultDto dto) {
        try {
            console.printFormat("Debug JSON: %s", mapper.writeValueAsString(dto));
            console.printLine();
        } catch (JsonProcessingException e) {
            // Ignore
        }
    }
}
