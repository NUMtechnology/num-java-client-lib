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
import org.apache.commons.lang3.StringUtils;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;
import uk.num.numlib.dns.DNSServicesDefaultImpl;
import uk.num.numlib.util.HashUtils;
import uk.num.numlib.util.StringConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class DummyDNSServices extends DNSServicesDefaultImpl {

    private static final Map<String, String[]> dns = new HashMap<>();

    static {
        dns.put("1._num.catchall1.numtest.com", new String[]{"jahdgfkjsdgh"});
        dns.put("1._" + HashUtils.domainAndHash("catchall1.numtest.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});

        dns.put("1._num.catchall2.numtest.com", new String[]{
                "2|e Co;c[t=441270123456",
                "1/3|_n=1;o(n=NUM Exampl",
                "3|;fb=example])"
        });

        dns.put("0._joe.bloggs.e._num.joebloggs.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("1._num.numexample.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("1._joe.bloggs.e._" + HashUtils.domainAndHash("joebloggs.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("1._num.xn--num-xc0e.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("1._" + HashUtils.domainAndHash("xn--num-xc0e.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("1._xn--num-xc0e.com.populator.num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("1._independent.e._num.numexample.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("path.1._num.independent.numexample.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("path.1._" + HashUtils.domainAndHash("hosted.numexample.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("path.1._pop.numexample.com.populator.num.net", new String[]{"status_(code=1;description=Check the populated zone.)"});
        dns.put("1._" + HashUtils.domainAndHash("hosted-numexample.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("1._hosted.e._" + HashUtils.domainAndHash("numexample.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("pop.1._numexample.com.populator.num.net", new String[]{"error_(code=100;description=Server Failure.)"});
        dns.put("1._pop-numexample.com.populator.num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});

        dns.put("redirected.1._num.redirectme1.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});
        dns.put("redirected.1._num.redirectme2.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets])"});

        dns.put("redirected.1._user.e._" + HashUtils.domainAndHash("email.redirect3.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("user.1._user.e._" + HashUtils.domainAndHash("email.redirect6.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("redirected.1._user.e._num.email.redirect1.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("1._num.email.redirect2.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._num.email.redirect3.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._num.email.redirect4.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._num.email.redirect5.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("redirected.sales.1._num.url.redirect1.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("1._num.url.redirect2.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._num.url.redirect3.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.sales.1._num.url.redirect4.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.sales.1._num.url.redirect5.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._num.lookup.root.redirect1.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=redirected;"});
        dns.put("1._num.lookup.root.redirect2.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=redirected1;"});

        dns.put("redirected.1._num.lookup.root.redirect1.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected1.1._num.lookup.root.redirect2.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=redirected2"});
        dns.put("redirected2.redirected1.1._num.lookup.root.redirect2.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=redirected3"});
        dns.put("redirected3.redirected2.redirected1.1._num.lookup.root.redirect2.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("redirected.1._" + HashUtils.domainAndHash("hosted.redirectme1.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._" + HashUtils.domainAndHash("hosted.redirectme2.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});


        dns.put("user.1._user.e._" + HashUtils.domainAndHash("hosted.email.redirect6.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("redirected.1._user.e._" + HashUtils.domainAndHash("hosted.email.redirect1.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("1._" + HashUtils.domainAndHash("hosted.email.redirect2.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._user.e._" + HashUtils.domainAndHash("hosted.email.redirect3.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._" + HashUtils.domainAndHash("hosted.email.redirect4.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._" + HashUtils.domainAndHash("hosted.email.redirect5.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});


        dns.put("redirected.sales.1._" + HashUtils.domainAndHash("hosted.url.redirect1.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("1._" + HashUtils.domainAndHash("hosted.url.redirect2.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._" + HashUtils.domainAndHash("hosted.url.redirect3.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._" + HashUtils.domainAndHash("hosted.url.redirect4.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.sales.1._" + HashUtils.domainAndHash("hosted.url.redirect5.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected.1._num.url.redirect4.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});


        dns.put("1._" + HashUtils.domainAndHash("lookup.root.hosted.redirect1.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=redirected;"});
        dns.put("1._" + HashUtils.domainAndHash("lookup.root.hosted.redirect2.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=redirected1;"});

        dns.put("redirected.1._" + HashUtils.domainAndHash("lookup.root.hosted.redirect1.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});
        dns.put("redirected1.1._" + HashUtils.domainAndHash("lookup.root.hosted.redirect2.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=/redirected2;"});
        dns.put("redirected2.1._" + HashUtils.domainAndHash("lookup.root.hosted.redirect2.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=/redirected3;"});
        dns.put("redirected3.1._" + HashUtils.domainAndHash("lookup.root.hosted.redirect2.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=/redirected4;"});


        dns.put("1._populator.response.1.com.populator.num.net", new String[]{"status_=(code=1;description=Check the populated zone.)"});
        dns.put("1._populator.response.2.com.populator.num.net", new String[]{"status_=(code=2;description=Check the independent zone.)"});
        dns.put("1._populator.response.3.com.populator.num.net", new String[]{"status_=(code=3;description=Check the hosted zone.)"});
        dns.put("1._populator.response.4.com.populator.num.net", new String[]{"status_=(code=4;description=Invalid code.)"});
        dns.put("1._populator.response.100.com.populator.num.net", new String[]{"error_=(code=100;description=Server Failure.)"});
        dns.put("1._populator.response.101.com.populator.num.net", new String[]{"error_=(code=101;description=Records for this domain can't be populated.)"});
        dns.put("1._populator.response.txt.com.populator.num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._john.smith.e._num.dist1.email.com", new String[]{"_n=1;zd=1"}); // Can't handle wildcards in DummyDNS, so hard-coding the lookup
        dns.put("1._john.smith.3.e._num.dist1.email.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._john.smith.e._num.dist2.email.com", new String[]{"_n=1;zd=2"}); // Can't handle wildcards in DummyDNS, so hard-coding the lookup
        dns.put("1._john.smith.6.3.e._num.dist2.email.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._john.smith.e._num.dist3.email.com", new String[]{"_n=1;zd=3"}); // Can't handle wildcards in DummyDNS, so hard-coding the lookup
        dns.put("1._john.smith.d.6.3.e._num.dist3.email.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._john.smith.e._num.dist1.hosted.email.com", new String[]{"_n=1;zd=1"}); // Can't handle wildcards in DummyDNS, so hard-coding the lookup
        dns.put("1._john.smith.3.e._" + HashUtils.domainAndHash("dist1.hosted.email.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._john.smith.e._num.dist2.hosted.email.com", new String[]{"_n=1;zd=2"}); // Can't handle wildcards in DummyDNS, so hard-coding the lookup
        dns.put("1._john.smith.6.3.e._" + HashUtils.domainAndHash("dist2.hosted.email.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._john.smith.e._num.dist3.hosted.email.com", new String[]{"_n=1;zd=3"}); // Can't handle wildcards in DummyDNS, so hard-coding the lookup
        dns.put("1._john.smith.d.6.3.e._" + HashUtils.domainAndHash("dist3.hosted.email.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);"});

        // Example taken from the NUM spec.
        dns.put("1._num.multi.com", new String[]{
                "2|e Co;c[t=441270123456",
                "1/4|_n=1;o(n=NUM Exampl",
                "3|;tw=numexampletweets])",
                "4|" // Make sure we can handle empty parts
        });

        // Example taken from https://app.clubhouse.io/num/story/2314/num-records-being-incorrectly-interpreted-as-rrsets
        dns.put("1._" + HashUtils.domainAndHash("yingdegroup.co.uk") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co;s=YIng De .;c[yt=channel/UC_2AkX5oawMWJ-iX16-9Wgw;li=in;tw=yingdegroup;t=HO:441612661093;t=|:441612093815;t=441612093816;u=yingdegroup.co.uk;u=%T.c%:%U.c%yingdegroup.co.uk]);facebook=View Facebook Profile object_display_name"});


        dns.put("1._num.absolute.redirect1.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=`basic.record.numexample.com:1/redirected1`;"});
        dns.put("redirected1.1._num.basic.record.numexample.com", new String[]{"_n=1;o(n=NUM Example Co Absolute Redirect ;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._num.absolute.hosted.redirect1.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=`basic.hosted.record.numexample.com:1/redirected1`;"});
        dns.put("redirected1.1._" + HashUtils.domainAndHash("basic.hosted.record.numexample.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co Absolute Redirect Hosted;c[t=441270123456;tw=numexampletweets]);"});

        dns.put("1._jane.doe.e._num.janedoe.com", new String[]{"_n=1;o(n=NUM Example Co;c[t=441270123456;tw=numexampletweets]);@R=`email.hosted.record.numexample.com:1/redirected1`"});
        dns.put("redirected1.1._" + HashUtils.domainAndHash("email.hosted.record.numexample.com") + ".num.net", new String[]{"_n=1;o(n=NUM Example Co Absolute Email Redirect Hosted;c[t=441270123456;tw=numexampletweets]);"});

    }

    /**
     * Get a NUM record from DNS.
     *
     * @param query         The NUM formatted DNS query.
     * @param timeoutMillis The number of milliseconds to wait for a response.
     * @return An array of Records
     */
    @Override
    public Record[] getRecordFromDnsNoCache(final String query, final int timeoutMillis) {

        log.info("DUMMY DNS QUERY: {}", query);
        final String noDotQuery = (query.endsWith(StringConstants.DOMAIN_SEPARATOR)) ? StringUtils.removeEnd(query, StringConstants.DOMAIN_SEPARATOR) : query;
        Record[] records;
        final String[] values = dns.get(noDotQuery);

        if (values != null && values.length > 0) {
            records = Arrays.stream(values)
                    .map(s -> {
                        try {
                            return new TXTRecord(Name.fromString(query), 0, 0, s);
                        } catch (TextParseException e) {
                            log.error("Error parsing Name object", e);
                        }
                        return null;
                    })
                    .collect(Collectors.toList())
                    .toArray(new Record[]{});
        } else {
            log.info("No DNS entry for: {}", noDotQuery);
            records = new Record[]{};
        }

        return records;
    }

}
