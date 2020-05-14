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

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import uk.num.numlib.internal.util.DomainLookupGenerator;
import uk.num.numlib.internal.util.LookupGenerator;

import java.util.Arrays;

/**
 * Full validation of NUM URIs
 */
public class NumUriValidator {

    /**
     * Used to detect whether a URI includes the protocol or not.
     */
    public static final String PROTOCOL_SEPARATOR = "://";

    /**
     * The expected NUM protocol prefix is present. Defaults to this if not present.
     */
    public static final String NUM_PROTOCOL_PREFIX = "num://";

    /**
     * All methods are static
     */
    private NumUriValidator() {
    }

    /**
     * Build a NUM URI from the parts and validate it.
     *
     * @param domain       a non-null domain String
     * @param moduleNumber a non-negative module number int
     * @param path         a non-null path String
     * @return ValidationResult
     */
    public static ValidationResult validate(@NonNull final String domain, final int moduleNumber, @NonNull final String path) {
        return validate(new LookupGenerator.NumUriComponents(domain, moduleNumber, path, null).toString());
    }

    /**
     * Fully validate a NUM URI, including checks to make sure that the independent and hosted domain names are within
     * the maximum length of a domain name.
     * <p>
     * Uses the other validators to help.
     *
     * @param uri String
     * @return ValidationResult
     */
    public static ValidationResult validate(final String uri) {
        final ValidationResult result = new ValidationResult();

        try {
            if (uri == null) {
                result.addMessage(ValidationResult.ErrorCode.NULL_UNACCEPTABLE, "uri");
            } else {
                // This will be the URI without the protocol - we don't need the protocol after this initial check.
                final String withoutProtocol;

                // If the protocol is present it must be a specific value.
                if (uri.contains(PROTOCOL_SEPARATOR)) {
                    if (!uri.toLowerCase()
                            .startsWith(NUM_PROTOCOL_PREFIX)) {
                        result.addMessage(ValidationResult.ErrorCode.INVALID_NUM_PROTOCOL_PREFIX, StringUtils.substringBefore(uri, NUM_PROTOCOL_PREFIX));
                    }
                    withoutProtocol = StringUtils.substringAfter(uri, PROTOCOL_SEPARATOR);
                } else {
                    withoutProtocol = uri;
                }

                // Split into path components and the domain and module number parts
                final String[] parts = withoutProtocol.split("/");
                final String[] domainAndModuleNumber = parts[0].split(":");

                // Validate as an email address or domain name
                if (domainAndModuleNumber[0].contains("@")) {
                    result.merge(NumEmailAddressValidator.validate(domainAndModuleNumber[0]));
                } else {
                    result.merge(NumDomainValidator.validate(domainAndModuleNumber[0]));
                }

                // Check the module number if present.
                if (domainAndModuleNumber.length == 2) {
                    result.merge(NumModuleNumberValidator.validate(domainAndModuleNumber[1]));
                }

                // There should be at most 1 colon before the path
                if (domainAndModuleNumber.length > 2) {
                    result.addMessage(ValidationResult.ErrorCode.TOO_MANY_COLONS, parts[0]);
                }

                // The module number is a single digit if not specified in the NUM URI.
                int moduleNumber = 0;
                if (domainAndModuleNumber.length > 1) {
                    try {
                        moduleNumber = Integer.parseInt(domainAndModuleNumber[1]);
                    } catch (final Exception e) {
                        // Defaults to 0
                    }
                }

                // Use a LookupGenerator to set up the hosted and independent domain names so they can be length-checked.
                // TODO: Remove NumProtocolSupport from the LookupGenerators so they don't throw MalformedUrlExceptions
                final DomainLookupGenerator lookupGenerator = new DomainLookupGenerator(domainAndModuleNumber[0]);

                //
                // DEPENDENCY on DomainLookupGenerator from another area of the library - this would need to be
                // factored out if the validation code is going to become a separate library.
                //
                final String hostedLocation = lookupGenerator.getHostedLocation(moduleNumber);
                if (hostedLocation.getBytes().length > NumDomainValidator.MAX_DOMAIN_NAME_LENGTH) {
                    result.addMessage(ValidationResult.ErrorCode.HOSTED_DOMAIN_NAME_TOO_LONG, hostedLocation);
                }

                final String independentLocation = lookupGenerator.getIndependentLocation(moduleNumber);
                if (independentLocation.getBytes().length > NumDomainValidator.MAX_DOMAIN_NAME_LENGTH) {
                    result.addMessage(ValidationResult.ErrorCode.INDEPENDENT_DOMAIN_NAME_TOO_LONG, independentLocation);
                }

                // Merge the path components and use a NumUriPathValidator to check it for errors.
                if (parts.length > 1) {
                    final String[] tailParts = Arrays.copyOfRange(parts, 1, parts.length);
                    final String path = "/" + String.join("/", tailParts);
                    result.merge(NumUriPathValidator.validate(path));
                }
            }
        } catch (final Exception e) {
            result.addMessage(ValidationResult.ErrorCode.EXCEPTION_MESSAGE, e.getMessage());
        }

        return result;
    }

}
