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

package uk.num.numlib.internal.dns;

import org.xbill.DNS.Record;
import uk.num.numlib.exc.*;

/**
 * This interface defines the contract for the DNS Service provider.
 *
 * @author tonywalmsley
 */
public interface DNSServices {

    /**
     * Concatenate an array of TXT record values to a single String
     *
     * @param records The array of Records
     * @return The concatenated result.
     * @throws RrSetNoHeadersException    on error
     * @throws RrSetHeaderFormatException on error
     * @throws RrSetIncompleteException   on error
     */
    String rebuildTXTRecordContent(final Record[] records) throws RrSetNoHeadersException, RrSetHeaderFormatException,
                                                                  RrSetIncompleteException;

    /**
     * Get a NUM record from DNS.
     *
     * @param query         The NUM formatted DNS query.
     * @param timeoutMillis The number of milliseconds to wait for a response.
     * @return An array of Records
     * @throws NumNotImplementedException    on error
     * @throws NumInvalidDNSQueryException   on error
     * @throws NumNoRecordAvailableException if a CNAME or SPF record is received instead of a TXT record
     */
    Record[] getRecordFromDnsNoCache(String query, int timeoutMillis) throws
                                                                      NumNotImplementedException,
                                                                      NumInvalidDNSQueryException,
                                                                      NumNoRecordAvailableException;

}
