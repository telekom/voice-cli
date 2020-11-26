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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Environment {
    private final BaseURLs baseUrls;
    private final IdmConfig idmConfig;

    public Environment(BaseURLs baseUrls, IdmConfig idmConfig) {
        this.baseUrls = baseUrls;
        this.idmConfig = idmConfig;
    }

    public BaseURLs getBaseUrls() {
        return baseUrls;
    }

    public IdmConfig getIdmConfig() {
        return idmConfig;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("baseUrls", baseUrls)
                .append("idmConfig", idmConfig)
                .toString();
    }

    public static final class BaseURLs {
        private final String cviUrl;
        private final String userUrl;

        public BaseURLs(String cviUrl, String userUrl) {
            this.cviUrl = cviUrl;
            this.userUrl = userUrl;
        }

        public String getCviUrl() {
            return cviUrl;
        }

        public String getUserUrl() {
            return userUrl;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("cviUrl", cviUrl)
                    .append("userUrl", userUrl)
                    .toString();
        }
    }
}
