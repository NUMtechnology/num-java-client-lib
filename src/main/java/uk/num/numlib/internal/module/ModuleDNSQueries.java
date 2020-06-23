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

package uk.num.numlib.internal.module;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import uk.num.numlib.exc.NumInvalidDNSQueryException;
import uk.num.numlib.exc.NumInvalidParameterException;
import uk.num.numlib.exc.NumInvalidRedirectException;
import uk.num.numlib.internal.ctx.AppContext;
import uk.num.numlib.util.*;

/**
 * Class to hold the DNS query strings for a module and NUM ID combination.
 *
 * @author tonywalmsley
 */
@Log4j2
public final class ModuleDNSQueries {

    /**
     * The module ID, e.g. "1"
     */
    @Getter
    private final int moduleId;

    /**
     * The NUM ID to be queried.
     */
    @NonNull
    private final String numId;

    /**
     * The independent record query
     */
    @Getter
    private String independentRecordLocation;

    /**
     * The root independent record query - used when generating redirects
     */
    @Getter
    private String rootIndependentRecordLocation;

    /**
     * The hosted record query.
     */
    @Getter
    private String hostedRecordLocation;

    /**
     * The root hosted record query - used when generating redirects.
     */
    @Getter
    private String rootHostedRecordLocation;

    /**
     * The populator query
     */
    @Getter
    private String populatorLocation;

    /**
     * The root/branch query flag.
     */
    @Getter
    private boolean rootQuery = true;

    /**
     * Constructor
     *
     * @param moduleNumber the module ID string
     * @param numId        the NUM ID.
     * @throws NumInvalidParameterException on error
     */
    public ModuleDNSQueries(final int moduleNumber, @NonNull final String numId) throws NumInvalidParameterException {
        if (StringUtils.isAllBlank(numId)) {
            throw new NumInvalidParameterException(numId);
        }
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("ModuleNumber should be >= 0 but is: " + moduleNumber);
        }
        log.debug("ModuleDNSQueries({}, {})", moduleNumber, numId);
        this.moduleId = moduleNumber;
        this.numId = numId;
    }

    /**
     * Build the DNS query Strings and set the root/branch flag.
     *
     * @throws NumInvalidParameterException on error
     */
    public void initialise() throws NumInvalidParameterException {
        log.trace("initialise()");

        // Create a suitable LookupGenerator based on the type of the record specifier
        final LookupGenerator lookupGenerator;
        if (numId.contains("@")) {
            lookupGenerator = new EmailLookupGenerator(numId);
        } else if (numId.startsWith("http")) {
            lookupGenerator = new URLLookupGenerator(numId);
        } else {
            lookupGenerator = new DomainLookupGenerator(numId);
        }

        independentRecordLocation = lookupGenerator.getIndependentLocation(moduleId);
        rootIndependentRecordLocation = lookupGenerator.getRootIndependentLocation(moduleId);
        hostedRecordLocation = lookupGenerator.getHostedLocation(moduleId);
        rootHostedRecordLocation = lookupGenerator.getRootHostedLocation(moduleId);
        rootQuery = lookupGenerator.isDomainRoot();
        if (rootQuery) {
            populatorLocation = lookupGenerator.getPopulatorLocation(moduleId);
        }
    }


    /**
     * A Zone Distribution Record has been found so we need to update the email lookups accordingly.
     *
     * @param appContext the AppContext
     * @param levels     the number of levels to use for zone distribution
     * @throws NumInvalidParameterException on error
     */
    public void setEmailRecordDistributionLevels(final AppContext appContext, final int levels) throws
                                                                                                NumInvalidParameterException {
        if (numId.contains("@")) {
            // This only applies to email NUM IDs
            final EmailLookupGenerator generator = new EmailLookupGenerator(numId);
            independentRecordLocation = generator.getDistributedIndependentLocation(moduleId, levels);
            hostedRecordLocation = generator.getDistributedHostedLocation(moduleId, levels);
        } else {
            log.warn("Attempt to distribute a non-email lookup using a Zone Distribution Record.");
        }
    }

    /**
     * Extract the 'path' portion of the hosted record
     *
     * @return a path of the form '/a/b/c'
     * @throws NumInvalidDNSQueryException if the
     */
    public String getHostedRecordPath() throws NumInvalidDNSQueryException {
        final int index = hostedRecordLocation.indexOf(rootHostedRecordLocation);
        if (index > -1) {
            return toPath(hostedRecordLocation.substring(0, index));
        }
        throw new NumInvalidDNSQueryException(String.format("Invalid hosted record location: %s", hostedRecordLocation));
    }

    /**
     * Extract the 'path' portion of the independent record
     *
     * @return a path of the form '/a/b/c'
     * @throws NumInvalidDNSQueryException if the
     */
    public String getIndependentRecordPath() throws NumInvalidDNSQueryException {
        final int index = independentRecordLocation.indexOf(rootIndependentRecordLocation);
        if (index > -1) {
            return toPath(independentRecordLocation.substring(0, index));
        }
        throw new NumInvalidDNSQueryException(String.format("Invalid independent record location: %s", independentRecordLocation));
    }

    /**
     * Convert a domain path to a URL path, e.g. `manager.sales` becomes `/sales/manager`
     *
     * @param domainPath a String
     * @return a URL path String
     */
    private String toPath(@NonNull final String domainPath) {
        if (domainPath.contains(StringConstants.DOMAIN_SEPARATOR)) {
            final String[] parts = domainPath.split("\\.");
            ArrayUtils.reverse(parts);
            return StringConstants.URL_PATH_SEPARATOR + String.join(StringConstants.URL_PATH_SEPARATOR, parts);
        }
        return StringConstants.URL_PATH_SEPARATOR + domainPath;
    }

    /**
     * Set the hosted record to the specified path
     *
     * @param path the path String
     * @throws NumInvalidRedirectException if the redirect attempts to redirect outside the root record
     */
    public void redirectHostedPath(final String path) throws NumInvalidRedirectException {
        final String newLocation = ("/".equals(path)) ? rootHostedRecordLocation : fromPath(path) + StringConstants.DOMAIN_SEPARATOR + rootHostedRecordLocation;
        if (newLocation.equals(hostedRecordLocation)) {
            throw new NumInvalidRedirectException("Cannot redirect back to the same location.");
        }
        this.hostedRecordLocation = newLocation;
    }

    /**
     * Set the independent record to the specified path
     *
     * @param path the path String
     * @throws NumInvalidRedirectException if the redirect attempts to redirect outside the root record
     */
    public void redirectIndependentPath(final String path) throws NumInvalidRedirectException {
        final String newLocation = ("/".equals(path)) ? rootIndependentRecordLocation : fromPath(path) + StringConstants.DOMAIN_SEPARATOR + rootIndependentRecordLocation;
        if (newLocation.equals(independentRecordLocation)) {
            throw new NumInvalidRedirectException("Cannot redirect back to the same location.");
        }
        this.independentRecordLocation = newLocation;
    }

    /**
     * Convert a URL path to a domain path , e.g. `/sales/manager` becomes `manager.sales`
     *
     * @param path a String
     * @return a domain path String
     */
    private String fromPath(final String path) {
        if (path.contains(StringConstants.URL_PATH_SEPARATOR)) {
            final String[] parts = path.split(StringConstants.URL_PATH_SEPARATOR);
            ArrayUtils.reverse(parts);
            final String[] partsWithoutEmptyStrings = ArrayUtils.removeAllOccurences(parts, "");
            return String.join(StringConstants.DOMAIN_SEPARATOR, partsWithoutEmptyStrings);
        }
        return path;
    }

}
