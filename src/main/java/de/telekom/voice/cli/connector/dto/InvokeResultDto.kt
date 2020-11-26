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

package de.telekom.voice.cli.connector.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.jetbrains.annotations.NotNull

/**
 * Contains a result of a skill invocation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class InvokeResultDto(
        val text: String? = null,
        val stt: STTResultDto? = null,
        val sttCandidates: STTResultsDto? = null,
        val intent: InvokeIntentDto? = null,
        val skill: SkillResultDto? = null,
        val cardId: String? = null,

        @get:NotNull
        val session: SessionDto? = null,
        val status: String? = null
) {
    data class InvokeIntentDto(
            val intent: String? = null,
            val entities: Map<String, List<String>>? = null
    )
}
