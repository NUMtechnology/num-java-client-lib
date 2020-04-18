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

import uk.num.numlib.exc.NumInvalidParameterException;

/**
 * Uitility to guarantee a populated String value - it cannot be just spaces
 */
public class NonBlankString {
    public final String value;

    /**
     * Private - user the of() static method.
     *
     * @param value the String value
     */
    private NonBlankString(final String value) {
        this.value = value;
    }

    /**
     * @param value a String
     * @return a NonEmptyString if the input value is present and not blank
     * @throws NumInvalidParameterException if the input is null or blank.
     */
    public static NonBlankString of(final String value) throws NumInvalidParameterException {
        if (value == null || value.trim()
                .length() == 0) {
            throw new NumInvalidParameterException("value cannot be null or blank");
        }
        return new NonBlankString(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
