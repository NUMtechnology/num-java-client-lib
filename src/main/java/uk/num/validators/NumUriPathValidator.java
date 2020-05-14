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

package uk.num.validators;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Validate the path part of a NUM URI
 */
public class NumUriPathValidator {

    /**
     * Path regex
     */
    public static final Pattern NUM_PATH_REGEX = Pattern.compile("^(/[^;,/?:@&=+$.\\s]+?)*?/??$");

    /**
     * Path components are converted to domain name labels so have the same length restriction.
     */
    public static final int MAX_PATH_PART_LENGTH = NumDomainValidator.MAX_LABEL_LENGTH;

    /**
     * All methods are static
     */
    private NumUriPathValidator() {
    }

    /**
     * Sometimes null values are considered valid.
     *
     * @param path a String
     * @return ValidationResult
     */
    public static ValidationResult validateAcceptingNullAsValid(final String path) {
        if (path == null) {
            return ValidationResult.VALID_NO_ERRORS;
        }
        return validate(path);
    }

    /**
     * Validate a URI path.
     *
     * @param path a String
     * @return ValidationResult
     */
    public static ValidationResult validate(final String path) {
        final ValidationResult result = new ValidationResult();

        try {
            if (path == null) {
                result.addMessage(ValidationResult.ErrorCode.NULL_UNACCEPTABLE, "path");
            } else {

                // Check for leading slash
                if (!path.startsWith("/")) {
                    result.addMessage(ValidationResult.ErrorCode.PATH_MUST_START_WITH_SLASH, path);
                }

                // Split into path components
                final String[] pathComponents = StringUtils.removeStart(path, "/")
                        .split("/");

                // Check each path component
                Arrays.stream(pathComponents)
                        .forEach(pathComponent -> {
                            if (pathComponent.getBytes().length == 0) {
                                result.addMessage(ValidationResult.ErrorCode.ZERO_LENGTH_PATH_COMPONENT, path);
                            }
                            if (pathComponent.getBytes().length > MAX_PATH_PART_LENGTH) {
                                result.addMessage(ValidationResult.ErrorCode.PATH_COMPONENT_TOO_LONG, pathComponent);
                            }
                        });

                // Catch any other errors using the regex
                if (!NUM_PATH_REGEX.matcher(path)
                        .matches()) {
                    result.addMessage(ValidationResult.ErrorCode.PATTERN_MISMATCH, path);
                }
            }
        } catch (final Exception e) {
            result.addMessage(ValidationResult.ErrorCode.EXCEPTION_MESSAGE, e.getMessage());
        }

        return result;
    }

}
