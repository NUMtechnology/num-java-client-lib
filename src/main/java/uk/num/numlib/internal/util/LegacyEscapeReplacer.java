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

package uk.num.numlib.internal.util;

import java.util.function.Function;

/**
 * Replace Legacy DNS Escape sequences
 * see https://app.clubhouse.io/num/story/2639/handling-escaped-characters-in-dns-responses
 */
public class LegacyEscapeReplacer implements Function<String, String> {
    /**
     * Applies this function to the given argument.
     *
     * @param numRecord the function argument
     * @return the function result
     */
    @Override
    public String apply(final String numRecord) {
        if (numRecord == null) {
            return null;
        }
        return numRecord.replaceAll("\\\\;", ";")
                .replaceAll("\\\\ ", " ");
    }
}
