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

package uk.num.numlib.api;

import lombok.NonNull;
import uk.num.numlib.exc.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Future;

public interface NumAPI {
    /**
     * Tell dnsjava to use TCP and not UDP.
     *
     * @param flag true to use TCP only.
     */
    void setTCPOnly(boolean flag);

    /**
     * Override the top-level zone from 'num.net' to 'myzone.com' for example.
     *
     * @param zone The top level zone to use for DNS lookups. Replaces the default of 'num.net'
     * @throws NumInvalidParameterException if the zone is null or empty
     */
    void setTopLevelZone(String zone) throws NumInvalidParameterException;

    /**
     * Initialise a new NumAPIContextBase object for a specific module/NUM ID combination.
     * The returned context object can be used to obtain the list of required user variables that must be set
     * before moving on to retrieveNumRecord().
     *
     * @param numAddress    E.g. `domain:module/path` or `user@domain:module/path` module is optional and defaults to 1
     * @param timeoutMillis the timeout in milliseconds to wait for responses from DNS.
     * @return a new NumAPIContextBase object.
     * @throws NumBadModuleIdException         on error
     * @throws MalformedURLException           on error
     * @throws NumInvalidParameterException    on error
     * @throws NumBadRecordException           on error
     * @throws NumDNSQueryException            on error
     * @throws NumInvalidDNSQueryException     on error
     * @throws RrSetHeaderFormatException      on error
     * @throws RrSetNoHeadersException         on error
     * @throws RrSetIncompleteException        on error
     */
    NumAPIContext begin(@NonNull final String numAddress, int timeoutMillis) throws
                                                                             NumBadModuleIdException,
                                                                             NumInvalidParameterException,
                                                                             NumBadRecordException,
                                                                             NumDNSQueryException,
                                                                             NumInvalidDNSQueryException,
                                                                             RrSetNoHeadersException,
                                                                             RrSetHeaderFormatException,
                                                                             RrSetIncompleteException,
                                                                             MalformedURLException;

    /**
     * Initialise a new NumAPIContextBase object for a specific module/NUM ID combination.
     * The returned context object can be used to obtain the list of required user variables that must be set
     * before moving on to retrieveNumRecord().
     *
     * @param numAddress    A NUM-specific URL, E.g. `domain:module/path` or `user@domain:module/path` module is optional and defaults to 1
     * @param timeoutMillis the timeout in milliseconds to wait for responses from DNS.
     * @return a new NumAPIContextBase object.
     * @throws MalformedURLException        on error
     * @throws NumInvalidParameterException on error
     */
    NumAPIContext begin(@NonNull final URL numAddress, final int timeoutMillis) throws
                                                                                NumInvalidParameterException,
                                                                                MalformedURLException;

    /**
     * This method uses the module context and the supplied Required User Variable values to obtain a fully expanded
     * JSON object from DNS. The supplied handler will be notified when the results are available or an error occurs.
     * <p>
     * Note: Calling `get()` on the resulting Future _may_ take up to 20 seconds to return a result if it is waiting
     * for improved results from the scraper service. Better to poll the `handler.getResult()` method periodically for
     * a more responsive interface.
     *
     * @param ctx           The context object returned by the begin() method.
     * @param handler       a handler object to receive the JSON results or processing errors.
     * @param timeoutMillis the maximum duration of each DNS request, the total wait time could be up to 4 times this value.
     * @return A Future object
     */
    Future<String> retrieveNumRecord(NumAPIContext ctx, NumAPICallbacks handler, int timeoutMillis);

    /**
     * Stop any outstanding DNS queries still in the Executor.
     */
    void shutdown();
}
