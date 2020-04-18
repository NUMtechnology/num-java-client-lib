/*
 *    Copyright 2020 NUM Technology Ltd
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package uk.num.net;

import org.apache.commons.lang3.StringUtils;

import java.net.*;
import java.util.regex.Pattern;

/**
 * Add NUM Protocol Support
 * <p>
 */
public class NumProtocolSupport {

    public static final String NUM_PROTOCOL = "num://";
    public static final String HTTPS_PROTOCOL = "https://";
    public static final String HTTP_PROTOCOL = "http://";
    public static final Pattern NUM_DOMAIN_REGEX = Pattern.compile("^(([^.\\s\f\t\r\b]+?\\.)*?([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b]+?\\.)([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b]+?))\\.??$");
    public static final Pattern NUM_PATH_REGEX = Pattern.compile("^(/[^;,/?:@&=+$.\\s]+?)*?/??$");
    public static final Pattern NUM_EMAIL_REGEX = Pattern.compile("^[^@\\s\f\t\r\b\\\\]{1}[^@\n\f\t\r\b\\\\]+?[^@\\s\f\t\r\b\\\\]{1}@(([^.\\s\f\t\r\b\\\\]+?\\.)*?([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b]+?\\.)([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b]+?))\\.??$");

    /*
     * Add a NumStreamHandlerFactory for the `num` protocol
     */
    static {
        URL.setURLStreamHandlerFactory(new NumStreamHandlerFactory());
    }

    /**
     * Create a `num` protocol URL
     *
     * @param numAddress the NUM ID
     * @return a URL
     * @throws MalformedURLException on error
     */
    public static URL toUrl(final String numAddress) throws MalformedURLException {
        final URL result;
        final String lowerCase = numAddress.toLowerCase();

        if (lowerCase
                .startsWith(HTTPS_PROTOCOL)) {
            result = new URL(NUM_PROTOCOL + StringUtils.removeStart(numAddress, HTTPS_PROTOCOL));
        } else if (lowerCase
                .startsWith(HTTP_PROTOCOL)) {
            result = new URL(NUM_PROTOCOL + StringUtils.removeStart(numAddress, HTTP_PROTOCOL));
        } else if (!numAddress.startsWith(NUM_PROTOCOL)) {
            result = new URL(NUM_PROTOCOL + numAddress);
        } else {
            result = new URL(numAddress);
        }

        if (!NUM_DOMAIN_REGEX.matcher(result.getHost())
                .matches()) {
            throw new MalformedURLException("Invalid domain URI for the NUM protocol: " + numAddress);
        }

        if (!NUM_PATH_REGEX.matcher(result.getPath())
                .matches()) {
            throw new MalformedURLException("Invalid Path in the URI: " + numAddress);
        }

        if (StringUtils.isNotEmpty(result.getUserInfo())) {
            final String email = result.getUserInfo() + "@" + result.getHost();
            if (!NUM_EMAIL_REGEX.matcher(email)
                    .matches()) {
                throw new MalformedURLException("Invalid email URI for the NUM protocol: " + numAddress);
            }
        }

        return result;
    }

    /**
     * Force the class to be loaded and the static initializer to be executed
     */
    public static void init() {
        // Nothing to do - this just forces the static method to initialise the protocol
    }

    /**
     * A URLStreamHandlerFactory for the NUM protocol
     */
    private static class NumStreamHandlerFactory implements URLStreamHandlerFactory {

        @Override
        public URLStreamHandler createURLStreamHandler(final String protocol) {
            if ("num".equalsIgnoreCase(protocol)) {
                return new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(final URL u) {
                        return new NUMURLConnection(u);
                    }
                };
            }
            return null;
        }
    }
}
