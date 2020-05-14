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

public class NumUriValidator {

    public static final String PROTOCOL_SEPARATOR = "://";

    public static final String NUM_PROTOCOL_PREFIX = "num://";

    public ValidationResult validateAcceptingNullAsValid(final String uri) {
        if (uri == null) {
            return ValidationResult.VALID_NO_ERRORS;
        }
        return validate(uri);
    }

    public ValidationResult validate(final String uri) {
        final ValidationResult result = new ValidationResult();

        try {
            if (uri == null) {
                result.addMessage(ValidationResult.ErrorCode.NULL_UNACCEPTABLE, "uri");
            } else {
                final String withoutProtocol;

                if (uri.contains(PROTOCOL_SEPARATOR)) {
                    if (!uri.toLowerCase()
                            .startsWith(NUM_PROTOCOL_PREFIX)) {
                        result.addMessage(ValidationResult.ErrorCode.INVALID_NUM_PROTOCOL_PREFIX, StringUtils.substringBefore(uri, NUM_PROTOCOL_PREFIX));
                    }
                    withoutProtocol = StringUtils.substringAfter(uri, PROTOCOL_SEPARATOR);
                } else {
                    withoutProtocol = uri;
                }

                final String[] parts = withoutProtocol.split("/");
                final String[] domainAndModuleNumber = parts[0].split(":");

                if (domainAndModuleNumber[0].contains("@")) {
                    result.merge(new NumEmailAddressValidator().validate(domainAndModuleNumber[0]));
                } else {
                    result.merge(new NumDomainValidator().validate(domainAndModuleNumber[0]));
                }

                if (domainAndModuleNumber.length == 2) {
                    result.merge(new NumModuleNumberValidator().validate(domainAndModuleNumber[1]));
                }

                if (domainAndModuleNumber.length > 2) {
                    result.addMessage(ValidationResult.ErrorCode.TOO_MANY_COLONS, parts[0]);
                }

                if (parts.length > 1) {
                    final String[] tailParts = Arrays.copyOfRange(parts, 1, parts.length);
                    final String path = "/" + String.join("/", tailParts);
                    result.merge(new NumUriPathValidator().validate(path));
                }
            }
        } catch (final Exception e) {
            result.addMessage(ValidationResult.ErrorCode.EXCEPTION_MESSAGE, e.getMessage());
        }

        return result;
    }

}
