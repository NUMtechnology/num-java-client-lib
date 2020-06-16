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

import lombok.extern.log4j.Log4j2;
import org.xbill.DNS.*;
import uk.num.numlib.exc.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A default implementation of the DNSServices interface.
 *
 * @author tonywalmsley
 */
@Log4j2
public class DNSServicesDefaultImpl implements DNSServices {

    public static final String MATCH_MULTIPART_RECORD_FRAGMENT = "(^\\d+\\|.*)|(\\d+\\/\\d+\\|_n=\\d+;.*)";

    /**
     * Is a record an SPF or CNAME record?
     */
    private static final Predicate<Record> isCNAMEOrSPFRecord = (r) -> r.rdataToString()
            .startsWith("v=spf")
            || r.rdataToString()
            .startsWith("\"v=spf") || r.getType() == Type.CNAME || r.getType() == Type.SPF;

    /**
     * Concatenate an array of TXT record values to a single String
     *
     * @param records The array of Records
     * @return The concatenated result.
     * @throws RrSetNoHeadersException    on error
     * @throws RrSetHeaderFormatException on error
     * @throws RrSetIncompleteException   on error
     */
    @Override
    public String rebuildTXTRecordContent(final Record[] records)
            throws RrSetNoHeadersException, RrSetHeaderFormatException, RrSetIncompleteException {
        assert records != null && records.length > 0;
        StringBuilder buffer = new StringBuilder();

        Map<Integer, String> ordered = new HashMap<>();

        for (final Record r : records) {
            TXTRecord record = (TXTRecord) r;
            List dataParts = record.getStrings();

            StringBuilder mergedDataParts = new StringBuilder();
            for (Object part : dataParts) {
                mergedDataParts.append(part.toString());
            }

            String data = mergedDataParts.toString();

            if (data.matches(MATCH_MULTIPART_RECORD_FRAGMENT)) {
                final int pipeIndex = data.indexOf("|");
                final String[] parts = new String[2];
                parts[0] = data.substring(0, pipeIndex);
                parts[1] = data.substring(pipeIndex + 1);

                final String substring = data.substring(parts[0].length() + 1);
                if (parts[0].contains("/")) {
                    ordered.put(0, substring);

                    String[] firstParts = parts[0].split("/");

                    if (firstParts.length == 2) {
                        try {
                            int total = Integer.parseInt(firstParts[1]);

                            if (total != records.length) {
                                // incomplete set
                                throw new RrSetIncompleteException(
                                        "Parts and records length do not match, expected $total - $records");
                            }

                        } catch (NumberFormatException ex) {
                            throw new RrSetHeaderFormatException(
                                    "Could not parse total parts ${firstParts[1]}");
                        }

                    } else {
                        throw new RrSetHeaderFormatException(
                                "First part should only contain 1 \"/\", format is incorrect!");
                    }
                } else {
                    try {
                        int index = Integer.parseInt(parts[0]) - 1;

                        ordered.put(index, substring);

                    } catch (NumberFormatException ex) {
                        throw new RrSetHeaderFormatException("Could not parse index ${parts[0]}");
                    }

                }
            } else {
                if (records.length == 1) {
                    ordered.put(0, data);
                } else {
                    throw new RrSetNoHeadersException(
                            "Found a record with no header in multi-part records - $records");
                }
            }
        }

        for (int i = 0; i < ordered.size(); i++) {
            buffer.append(ordered.get(i));
        }

        final String result = buffer.toString();
        log.debug("Rebuilt DNS records: {}", result);
        return result;
    }

    /**
     * Get a NUM record from DNS without caching.
     *
     * @param query         The NUM formatted DNS query.
     * @param timeoutMillis The number of milliseconds to wait for a response.
     * @return An array of Records
     * @throws NumInvalidDNSQueryException   on error
     * @throws NumNoRecordAvailableException if a CNAME or SPF record is received
     *                                       instead of a TXT record
     */
    @Override
    public Record[] getRecordFromDnsNoCache(final String query, final int timeoutMillis)
            throws NumInvalidDNSQueryException, NumNoRecordAvailableException {
        assert timeoutMillis > 0;

        // Return the cached value if we have one.
        Record[] records;

        // No cached value so look it up in DNS
        final Resolver resolver = Lookup.getDefaultResolver();
        resolver.setTimeout(timeoutMillis / 1000, timeoutMillis % 1000);
        resolver.setIgnoreTruncation(false);

        try {
            final Record queryTxtRecord = Record.newRecord(new Name(query), Type.TXT, DClass.IN);
            final Message queryMessage = Message.newQuery(queryTxtRecord);
            log.debug("Sending DNS Query: {}", queryMessage);

            final Message response = resolver.send(queryMessage);
            log.debug("Received DNS Response: {}", response);

            if (response.getRcode() == Rcode.NOERROR) {
                records = response.getSectionArray(Section.ANSWER);
                if (Arrays.stream(records)
                        .anyMatch(isCNAMEOrSPFRecord)) {
                    throw new NumNoRecordAvailableException("Received CNAME or SPF record instead of TXT record.");
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error querying for NUM record.", e);
            throw new NumInvalidDNSQueryException("Invalid DNS query: " + query);
        }

        return records;
    }

}
