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
import uk.num.numlib.internal.ctx.AppContext;

import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URL;

abstract class BaseLookupGenerator implements LookupGenerator {

    protected final String numId;

    protected final AppContext appContext;

    protected String branch;

    protected String domain;

    public BaseLookupGenerator(@NonNull final AppContext appContext, final @NonNull String numId) {
        this.numId = numId;
        this.appContext = appContext;
    }

    /**
     * Convert a domain name or URL into a normalised name by removing 'www.' and any trailing '.'.
     *
     * @param numId java.lang.String The Not Null domain name string or URL.
     * @return A normalised java.lang.String domain name.
     */
    protected static String normaliseDomainName(@NonNull final String numId) {
        if (numId.startsWith("http")) {
            try {
                final URL url = new URL(numId);
                final String host = url.getHost();
                return normaliseDomainName(host);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid URL: " + numId);
            }
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

    @Override
    public String getIndependentLocation(final int moduleId) {
        final String result = getRootIndependentLocation(moduleId);
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

    @Override
    public String getHostedLocation(final int moduleId) {
        final String result = getRootHostedLocation(moduleId);
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
    public String getPopulatorLocation(final int moduleId) {
        if (branch != null) {
            return null;
        }
        return moduleId +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                domain +
                StringConstants.POPULATOR_SERVICE_SUFFIX +
                StringConstants.DOMAIN_SEPARATOR;
    }

    @Override
    public String getRootIndependentLocation(final int moduleId) {
        return moduleId +
                StringConstants.UTILITY_MODULE_PREFIX +
                domain +
                StringConstants.DOMAIN_SEPARATOR;
    }

    @Override
    public String getRootHostedLocation(final int moduleId) {
        return moduleId +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                domain +
                HashUtils.hash3(domain) +
                StringConstants.HOSTED_RECORD_SUFFIX +
                StringConstants.DOMAIN_SEPARATOR;

    }

}
