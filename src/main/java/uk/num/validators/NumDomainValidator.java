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

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Validate Domain Strings.
 * Note: Use NumUriValidator instead if the module number is known.
 */
public class NumDomainValidator {

    /**
     * Domain Regex
     */
    public static final Pattern NUM_DOMAIN_REGEX = Pattern.compile("^(([^.\\s\f\t\r\b]+?\\.)*?([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b]+?\\.)([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b]+?))\\.??$");

    /**
     * @see "https://en.wikipedia.org/wiki/Domain_name#Domain_name_syntax"
     */
    public static final int MAX_DOMAIN_NAME_LENGTH = 253;

    public static final int MAX_LABEL_LENGTH = 63;

    /**
     * All methods are static
     */
    private NumDomainValidator() {
    }

    /**
     * Sometimes nulls are considered valid.
     *
     * @param domain a String
     * @return a ValidationResult
     */
    public static ValidationResult validateAcceptingNullAsValid(final String domain) {
        if (domain == null) {
            return ValidationResult.VALID_NO_ERRORS;
        }
        return validate(domain);
    }

    /**
     * Validate a domain.
     *
     * @param domain a String
     * @return a ValidationResult
     */
    public static ValidationResult validate(final String domain) {
        final ValidationResult result = new ValidationResult();

        try {
            if (domain == null) {
                result.addMessage(ValidationResult.ErrorCode.NULL_UNACCEPTABLE, "domain");
            } else {
                // Max length
                if (domain.getBytes().length > MAX_DOMAIN_NAME_LENGTH) {
                    result.addMessage(ValidationResult.ErrorCode.DOMAIN_NAME_TOO_LONG, domain);
                }

                Arrays.stream(domain.split("\\."))
                        .forEach(label -> {
                            //
                            // Check label lengths and invalid characters
                            //
                            if (label.getBytes().length == 0) {
                                result.addMessage(ValidationResult.ErrorCode.ZERO_LENGTH_LABEL, domain);
                            } else if (label.getBytes().length > MAX_LABEL_LENGTH) {
                                result.addMessage(ValidationResult.ErrorCode.LABEL_TOO_LONG, label);
                            }

                            if (label.contains(" ")) {
                                result.addMessage(ValidationResult.ErrorCode.SPACES_IN_LABEL, label);
                            }
                            if (label.contains("\f")) {
                                result.addMessage(ValidationResult.ErrorCode.FORM_FEED_IN_LABEL, label);
                            }
                            if (label.contains("\t")) {
                                result.addMessage(ValidationResult.ErrorCode.TAB_IN_LABEL, label);
                            }
                            if (label.contains("\r")) {
                                result.addMessage(ValidationResult.ErrorCode.CARRIAGE_RETURN_IN_LABEL, label);
                            }
                            if (label.contains("\b")) {
                                result.addMessage(ValidationResult.ErrorCode.BACKSPACE_IN_LABEL, label);
                            }
                            if (label.contains("\n")) {
                                result.addMessage(ValidationResult.ErrorCode.NEWLINE_IN_LABEL, label);
                            }
                        });

                //
                // Catch any other errors using the regex
                //
                if (!NUM_DOMAIN_REGEX.matcher(domain)
                        .matches()) {
                    result.addMessage(ValidationResult.ErrorCode.PATTERN_MISMATCH, domain);
                }
            }
        } catch (final Exception e) {
            result.addMessage(ValidationResult.ErrorCode.EXCEPTION_MESSAGE, e.getMessage());
        }

        return result;
    }

}
