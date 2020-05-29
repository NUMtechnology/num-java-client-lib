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

import java.util.regex.Pattern;

/**
 * Validate email addresses
 */
public class NumEmailAddressValidator {

    /**
     * Email address regex
     */
    public static final Pattern NUM_EMAIL_REGEX = Pattern.compile("^(?!\\s)[^@\f\t\r\b\n]+?(?<!\\s)@(([^.\\s\f\t\r\b\n]+?\\.)*?([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b\n]+?\\.)([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b\n]+?))\\.??$");

    /**
     * @see "https://en.wikipedia.org/wiki/Email_address#Syntax"
     */
    public static final int MAX_LOCAL_PART_LENGTH = 64;

    /**
     * All methods are static
     */
    private NumEmailAddressValidator() {
    }

    /**
     * Sometimes null values are considered valid.
     *
     * @param emailAddress a String
     * @return a ValidationResult
     */
    public static ValidationResult validateAcceptingNullAsValid(final String emailAddress) {
        if (emailAddress == null) {
            return ValidationResult.VALID_NO_ERRORS;
        }
        return validate(emailAddress);
    }

    /**
     * Validate an email address.
     *
     * @param emailAddress a String
     * @return a ValidationResult
     */
    public static ValidationResult validate(final String emailAddress) {
        final ValidationResult result = new ValidationResult();

        try {
            if (emailAddress == null) {
                result.addMessage(ValidationResult.ErrorCode.NULL_UNACCEPTABLE, "emailAddress");
            } else {

                // Check for a single @
                final String[] parts = emailAddress.split("@");
                if (parts.length < 2) {
                    result.addMessage(ValidationResult.ErrorCode.NO_AT_SYMBOL, emailAddress);
                }
                if (parts.length > 2) {
                    result.addMessage(ValidationResult.ErrorCode.TOO_MANY_AT_SYMBOLS, emailAddress);
                }

                final String localPart = parts[0];
                final String domain = parts[1];

                // Use the domain validator
                result.merge(NumDomainValidator.validate(domain));

                // Check the local part
                if (localPart.getBytes().length > MAX_LOCAL_PART_LENGTH) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_TOO_LONG, localPart);
                }

                if (localPart.getBytes().length == 0) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_IS_EMPTY, localPart);
                }

                if (localPart.contains("..")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_DOUBLE_DOT, localPart);
                }
                if (localPart.startsWith(".")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_STARTS_WITH_DOT, localPart);
                }
                if (localPart.endsWith(".")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_ENDS_WITH_DOT, localPart);
                }
                if (localPart.contains("\n")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_NEWLINE, localPart);
                }
                if (localPart.contains("\r")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_CARRIAGE_RETURN, localPart);
                }
                if (localPart.contains("\t")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_TAB, localPart);
                }
                if (localPart.contains("\b")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_BACKSPACE, localPart);
                }
                if (localPart.contains("\f")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_FORMFEED, localPart);
                }
                if (localPart.contains("\\")) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_CONTAINS_BACKSLASH, localPart);
                }

                // Catch any other errors using the regex
                if (!NUM_EMAIL_REGEX.matcher(emailAddress)
                        .matches()) {
                    result.addMessage(ValidationResult.ErrorCode.PATTERN_MISMATCH, emailAddress);
                }
            }
        } catch (final Exception e) {
            result.addMessage(ValidationResult.ErrorCode.EXCEPTION_MESSAGE, e.getMessage());
        }

        return result;
    }

}
