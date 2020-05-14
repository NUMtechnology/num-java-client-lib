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

public class NumEmailAddressValidator {

    public static final Pattern NUM_EMAIL_REGEX = Pattern.compile("^(?!\\s)[^@\f\t\r\b\n]+?(?<!\\s)@(([^.\\s\f\t\r\b\n]+?\\.)*?([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b\n]+?\\.)([^!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~\\s\f\t\r\b\n]+?))\\.??$");

    public static final int MAX_LOCAL_PART_LENGTH = 64;

    public ValidationResult validateAcceptingNullAsValid(final String emailAddress) {
        if (emailAddress == null) {
            return ValidationResult.VALID_NO_ERRORS;
        }
        return validate(emailAddress);
    }

    public ValidationResult validate(final String emailAddress) {
        final ValidationResult result = new ValidationResult();

        try {
            if (emailAddress == null) {
                result.addMessage(ValidationResult.ErrorCode.NULL_UNACCEPTABLE, "emailAddress");
            } else {
                final String[] parts = emailAddress.split("@");
                if (parts.length < 2) {
                    result.addMessage(ValidationResult.ErrorCode.NO_AT_SYMBOL, emailAddress);
                }
                if (parts.length > 2) {
                    result.addMessage(ValidationResult.ErrorCode.TOO_MANY_AT_SYMBOLS, emailAddress);
                }

                final String localPart = parts[0];
                final String domain = parts[1];

                result.merge(new NumDomainValidator().validate(domain));

                if (localPart.getBytes().length > MAX_LOCAL_PART_LENGTH) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_TOO_LONG, localPart);
                }

                if (localPart.getBytes().length == 0) {
                    result.addMessage(ValidationResult.ErrorCode.LOCAL_PART_OF_EMAIL_IS_EMPTY, localPart);
                }

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