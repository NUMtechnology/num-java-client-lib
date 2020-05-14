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

import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import uk.num.numlib.exc.NumInvalidParameterException;

import java.net.IDN;
import java.util.Arrays;

public abstract class BaseLookupGenerator implements LookupGenerator {

    public static final String NUM_PROTOCOL = "num://";

    public static final String HTTPS_PROTOCOL = "https://";

    public static final String HTTP_PROTOCOL = "http://";

    protected final String numId;

    protected String branch;

    protected String domain;

    public BaseLookupGenerator(final @NonNull String numId) {
        this.numId = numId;
    }

    /**
     * Convert a domain name or URL into a normalised name by removing 'www.' and any trailing '.'.
     *
     * @param numId java.lang.String The Not Null domain name string or URL.
     * @return A normalised java.lang.String domain name.
     */
    protected static String normaliseDomainName(@NonNull final String numId) {
        if (numId.startsWith("http")) {
            final String host = parseNumUriString(numId).getDomain();
            return normaliseDomainName(host);
        }

        String result = numId;
        if (result.startsWith("www.")) {
            result = result.substring(4);
        }

        if (result.startsWith(StringConstants.DOMAIN_SEPARATOR)) {
            result = result.substring(1);
        }

        if (result.endsWith(StringConstants.DOMAIN_SEPARATOR)) {
            result = result.substring(0, result.length() - 1);
        }

        if (!StringUtils.isAsciiPrintable(result)) {
            final String[] parts = result.split("\\.");
            for (int i = 0; i < parts.length; i++) {
                if (!StringUtils.isAsciiPrintable(parts[i])) {
                    parts[i] = IDN.toASCII(parts[i]);
                }
            }
            result = StringUtils.join(parts, StringConstants.DOMAIN_SEPARATOR);
        }
        return result;
    }

    /**
     * Accept the 'path' part of a URL and convert it to a format for use in NUM Protocol Queries.
     * I.e. split by '/' and '.', reverse the results and join with '.', prefix with an underscore and
     * replace all spaces by underscores.
     *
     * @param path java.lang.String the path part of the URL - i.e. the result of URL.getPath()
     * @return java.lang.String the normalised path.
     */
    protected static String normalisePath(final String path) {
        String result = "";
        if (path != null && !path.isEmpty()) {
            String[] pathComponents = path.split("[/]");
            ArrayUtils.reverse(pathComponents);
            if (pathComponents.length > 0 && pathComponents[0].contains(StringConstants.DOMAIN_SEPARATOR)) {
                pathComponents[0] = "";// Ignore the first item (i.e. last item before it was reversed) if it contains a '.' character
            }
            for (int i = 0; i < pathComponents.length; i++) {
                if (!StringUtils.isAsciiPrintable(pathComponents[i])) {
                    pathComponents[i] = IDN.toASCII(pathComponents[i]);
                }
            }
            if (pathComponents.length > 0) {
                result = StringUtils.join(pathComponents, StringConstants.DOMAIN_SEPARATOR);
                result = result.replaceAll(" ", "_");
                if (result.startsWith(StringConstants.DOMAIN_SEPARATOR)) {
                    result = result.substring(1);
                }
                if (result.endsWith(StringConstants.DOMAIN_SEPARATOR)) {
                    result = result.substring(0, result.length() - 1);
                }
            }
        }
        return result;
    }

    /**
     * Attempt to parse a NUM URI String into its components.
     *
     * @param possibleNumUri a String
     * @return NumUriComponents
     */
    public static NumUriComponents parseNumUriString(@NonNull final String possibleNumUri) {

        String withoutProtocol = StringUtils.removeStartIgnoreCase(possibleNumUri, HTTP_PROTOCOL);
        withoutProtocol = StringUtils.removeStartIgnoreCase(withoutProtocol, HTTPS_PROTOCOL);
        withoutProtocol = StringUtils.removeStartIgnoreCase(withoutProtocol, NUM_PROTOCOL);

        // Split into path components and the domain and module number parts
        final String[] urlAndParams = withoutProtocol.split("\\?");
        final String[] parts = urlAndParams[0].split("/");
        final String[] domainAndModuleNumber = parts[0].split(":");

        // The module number is a single digit if not specified in the NUM URI.
        int moduleNumber = 0;
        if (domainAndModuleNumber.length > 1) {
            try {
                moduleNumber = Integer.parseInt(domainAndModuleNumber[1]);
            } catch (final Exception e) {
                // Defaults to 0
            }
        }

        String path = "/";
        if (parts.length > 1) {
            final String[] tailParts = Arrays.copyOfRange(parts, 1, parts.length);
            path = "/" + String.join("/", tailParts);
        }
        final String params = (urlAndParams.length > 1) ? urlAndParams[1] : null;
        return new NumUriComponents(domainAndModuleNumber[0], moduleNumber, path, params);
    }

    @Override
    public String getIndependentLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        final String result = getRootIndependentLocation(moduleNumber);
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

    @Override
    public String getHostedLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        final String result = getRootHostedLocation(moduleNumber);
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

    @Override
    public boolean isDomainRoot() {
        return branch == null;
    }

    /**
     * Convert "foo/bar" into "bar.foo"
     *
     * @param s the branch part of a location specifier
     * @return the transformed branch part
     */
    protected String transformBranch(final String s) {
        if (s == null || s.equals("/")) {
            return null;
        }
        final int i = s.indexOf("/");
        final String[] split = s.substring(i + 1)
                .split("/");
        ArrayUtils.reverse(split);
        for (int j = 0; j < split.length; j++) {
            if (!StringUtils.isAsciiPrintable(split[j])) {
                split[j] = IDN.toASCII(split[j]);
            }
        }
        return String.join(StringConstants.DOMAIN_SEPARATOR, split);
    }

    @Override
    public String getPopulatorLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        if (branch != null) {
            return null;
        }
        return moduleNumber +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                domain +
                StringConstants.POPULATOR_SERVICE_SUFFIX +
                StringConstants.DOMAIN_SEPARATOR;
    }

    @Override
    public String getRootIndependentLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        return moduleNumber +
                StringConstants.UTILITY_MODULE_PREFIX +
                domain +
                StringConstants.DOMAIN_SEPARATOR;
    }

    @Override
    public String getRootHostedLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        return moduleNumber +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                domain +
                HashUtils.hash3(domain) +
                StringConstants.HOSTED_RECORD_SUFFIX +
                StringConstants.DOMAIN_SEPARATOR;

    }

    /**
     * Note - this method does not return a trailing dot
     *
     * @return String
     */
    @Override
    public String getRootIndependentLocationNoModuleNumber(final TrailingDot addTrailingDot) {
        if (addTrailingDot == TrailingDot.ADD_TRAILING_DOT) {
            return StringConstants.UTILITY_MODULE_PREFIX_NO_START_DOT +
                    domain + StringConstants.DOMAIN_SEPARATOR;
        } else {
            return StringConstants.UTILITY_MODULE_PREFIX_NO_START_DOT +
                    domain;
        }
    }

    /**
     * Note - this method does not return a trailing dot
     *
     * @return String
     */
    @Override
    public String getRootHostedLocationNoModuleNumber(final TrailingDot addTrailingDot) {
        if (addTrailingDot == TrailingDot.ADD_TRAILING_DOT) {
            return StringConstants.DOMAIN_NAME_PREFIX +
                    domain +
                    HashUtils.hash3(domain) +
                    StringConstants.HOSTED_RECORD_SUFFIX + StringConstants.DOMAIN_SEPARATOR;
        } else {
            return StringConstants.DOMAIN_NAME_PREFIX +
                    domain +
                    HashUtils.hash3(domain) +
                    StringConstants.HOSTED_RECORD_SUFFIX;
        }
    }

}
