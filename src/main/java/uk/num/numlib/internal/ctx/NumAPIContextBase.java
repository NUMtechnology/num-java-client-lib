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

package uk.num.numlib.internal.ctx;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import uk.num.net.NumProtocolSupport;
import uk.num.numlib.api.NumAPICallbacks;
import uk.num.numlib.api.NumAPIContext;
import uk.num.numlib.api.UserVariable;
import uk.num.numlib.exc.*;
import uk.num.numlib.internal.module.ModuleDNSQueries;
import uk.num.numlib.internal.util.UrlRelativePathResolver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A base class implementation of NumAPIContext.
 *
 * @author tonywalmsley
 */
@Log4j2
public class NumAPIContextBase implements NumAPIContext {

    private static final int MAX_NUM_REDIRECTS = 3;

    /**
     * true if the library should query the Populator
     */
    @Getter
    @Setter
    private boolean populatorQueryRequired = false;

    /**
     * Needs to be set by the client
     */
    private UserVariable[] requiredUserVariables;

    /**
     * The DNS query Strings for the current module and NUM ID.
     */
    @Getter
    @Setter
    private ModuleDNSQueries moduleDNSQueries;

    /**
     * Count redirects so we don't redirect forever.
     */
    private int redirectCount = 0;

    /**
     * The location currently being checked for a NUM record.
     */
    @Getter
    @Setter
    private NumAPICallbacks.Location location;

    /**
     * Count redirects and return the current number of redirects.
     *
     * @return the current number of redirects
     */
    private int incrementRedirectCount() {
        this.redirectCount++;
        return this.redirectCount;
    }

    /**
     * Get the query location based on the current location that is being tried.
     *
     * @return a DNS query string for the current location.
     */
    public String getRecordLocation() {
        switch (location) {
            case INDEPENDENT:
                return moduleDNSQueries.getIndependentRecordLocation();
            case HOSTED:
                return moduleDNSQueries.getHostedRecordLocation();
            case POPULATOR:
                return moduleDNSQueries.getPopulatorLocation();
            default:
                return "STOP";
        }
    }

    /**
     * Update the relevant query for the supplied redirect
     *
     * @param redirect the supplied redirect
     * @throws NumMaximumRedirectsExceededException on Error
     * @throws NumInvalidDNSQueryException          on Error
     * @throws NumInvalidRedirectException          on Error
     */
    public void handleQueryRedirect(final String redirect) throws
                                                           NumMaximumRedirectsExceededException,
                                                           NumInvalidDNSQueryException,
                                                           NumInvalidRedirectException {
        log.info("Query Redirected to: {}", redirect);
        int redirectCount = incrementRedirectCount();
        if (redirectCount >= MAX_NUM_REDIRECTS) {
            log.error("Maximum Redirects Exceeded. (max={})", MAX_NUM_REDIRECTS);
            throw new NumMaximumRedirectsExceededException();
        }

        if (redirect.matches("^.*:[0-9]+/?.*$")) {
            try {
                handleNumUriRedirect(NumProtocolSupport.toUrl(redirect));
            } catch (final MalformedURLException | NumInvalidParameterException e) {
                throw new NumInvalidRedirectException(e);
            }
        } else {
            switch (location) {
                case INDEPENDENT:
                    handleIndependentQueryRedirect(redirect);
                case HOSTED:
                    handleHostedQueryRedirect(redirect);
                default:
                    break;
            }
        }
    }

    private void handleNumUriRedirect(final URL numUri) throws NumInvalidParameterException {
        final int moduleNumber = Math.max(numUri.getPort(), 0);
        final String userInfo = numUri.getUserInfo();
        final String host = numUri.getHost();
        final String path = numUri.getPath();
        final String numId = (StringUtils.isEmpty(userInfo)) ? host + path : userInfo + "@" + host + path;
        moduleDNSQueries.setModuleId(moduleNumber);
        moduleDNSQueries.setNumId(numId);
        moduleDNSQueries.initialise();
    }

    /**
     * Update the hosted query for the supplied redirect
     *
     * @param redirectTo the supplied redirect
     * @throws NumInvalidDNSQueryException on error
     * @throws NumInvalidRedirectException on error
     */
    private void handleHostedQueryRedirect(final String redirectTo) throws
                                                                    NumInvalidDNSQueryException,
                                                                    NumInvalidRedirectException {
        final String hostedRecordPath = moduleDNSQueries.getHostedRecordPath();
        try {
            moduleDNSQueries.redirectHostedPath(UrlRelativePathResolver.resolve(hostedRecordPath, redirectTo));
        } catch (final RelativePathException e) {
            throw new NumInvalidRedirectException(e);
        }
    }

    /**
     * Update the independent query for the supplied redirect
     *
     * @param redirectTo the supplied redirect
     * @throws NumInvalidDNSQueryException on error
     * @throws NumInvalidRedirectException on error
     */
    private void handleIndependentQueryRedirect(final String redirectTo) throws
                                                                         NumInvalidRedirectException,
                                                                         NumInvalidDNSQueryException {
        final String independentRecordPath = moduleDNSQueries.getIndependentRecordPath();
        try {
            moduleDNSQueries.redirectIndependentPath(UrlRelativePathResolver.resolve(independentRecordPath, redirectTo));
        } catch (final RelativePathException e) {
            throw new NumInvalidRedirectException(e);
        }
    }

    /**
     * Get the user variables.
     *
     * @return UserVariable[]
     */
    @Override
    public UserVariable[] getRequiredUserVariables() {
        if (requiredUserVariables == null) {
            return new UserVariable[0];
        }
        return requiredUserVariables;
    }

    /**
     * Update the required user variables with values obtained from the client.
     *
     * @param uv The RequiredUserVariable array with the value fields populated.
     */
    @Override
    public void setRequiredUserVariables(final UserVariable[] uv) {
        this.requiredUserVariables = uv;
    }

}
