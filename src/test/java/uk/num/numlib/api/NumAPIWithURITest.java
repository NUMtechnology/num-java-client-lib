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

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.num.net.NUMURLConnection;
import uk.num.net.NumProtocolSupport;
import uk.num.numlib.exc.NumInvalidDNSHostException;
import uk.num.numlib.exc.NumInvalidParameterException;
import uk.num.numlib.exc.NumInvalidRedirectException;
import uk.num.numlib.internal.dns.DummyDNSServices;
import uk.num.numlib.internal.util.PopulatorRetryConfig;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Log4j2
public class NumAPIWithURITest {

    /**
     * Reduce the retry delays during unit tests
     */
    @BeforeClass
    public static void beforeClass() {
        PopulatorRetryConfig.RETRY_DELAYS = new int[]{10, 10, 10, 10, 10, 10, 10, 10};
        PopulatorRetryConfig.ERROR_RETRY_DELAYS = new int[]{10, 10};

        NumProtocolSupport.init();
        NUMURLConnection.setDnsServices(new DummyDNSServices());
    }

    @Test
    public void test_00() throws Throwable {
        final String numId = "numexample.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_01() throws Throwable {
        final String numId = "hosted-numexample.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_03() throws Throwable {
        final String numId = "pop-numexample.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_04() throws Throwable {
        final String numId = "independent@numexample.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_05() throws Throwable {
        final String numId = "hosted@numexample.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_08() throws Throwable {
        final String numId = "http://independent.numexample.com:1/path/";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_09() throws Throwable {
        final String numId = "http://hosted.numexample.com:1/path/";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_12_root_query_redirect_success() throws Throwable {
        final String numId = "redirectme1.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_13_root_query_redirect_success() throws Throwable {
        final String numId = "redirectme2.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_14_root_query_relative_redirect_fail() throws Throwable {
        final String numId = "redirectme3.com:1";
        log.info("Trying: " + numId);
        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }

    }

    @Test
    public void test_15_root_query_redirect_to_self_fail() {
        final String numId = "redirectme4.com:1";
        log.info("Trying: " + numId);
        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception:", "uk.num.numlib.exc.NumInvalidRedirectException: Cannot redirect back to the same location.", e.getCause()
                    .getMessage());
        }

    }

    @Test
    public void test_16_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@email.redirect1.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_17_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@email.redirect2.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_18_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@email.redirect3.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_19_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@email.redirect4.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }

    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_20_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user.name@email.redirect5.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_21_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@email.redirect6.com:1";
        log.info("Trying: " + numId);
        runQuery(numId);
    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_22_email_branch_query_redirect_too_far_fail() throws Throwable {
        final String numId = "user@email.redirect7.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_23_url_branch_query_redirect_success() throws Throwable {
        final String numId = "http://url.redirect1.com:1/sales/";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_24_url_branch_query_redirect_success() {
        final String numId = "http://url.redirect2.com:1/sales/";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
        } catch (Exception e) {
            Assert.fail("Unexpected exception.");
        }
    }

    @Test
    public void test_25_url_branch_query_redirect_success() throws Throwable {
        final String numId = "http://url.redirect3.com:1/sales/";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_26_url_branch_query_redirect_success() throws Throwable {
        final String numId = "http://url.redirect4.com:1/sales/";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_27_url_branch_query_redirect_success() throws Throwable {
        final String numId = "http://url.redirect5.com:1/sales/";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_28_url_branch_query_redirect_to_self_fail() {
        final String numId = "http://url.redirect6.com:1/sales/";
        log.info("Trying: " + numId);
        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception:", "uk.num.numlib.exc.NumInvalidRedirectException: Cannot redirect back to the same location.", e.getCause()
                    .getMessage());
        }

    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_29_url_branch_query_redirect_too_far_fail() throws Throwable {
        final String numId = "http://url.redirect7.com:1/sales/";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_30_lookup_root_redirect_success() throws Throwable {
        final String numId = "lookup.root.redirect1.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_31_lookup_root_redirect_too_many_redirects() {
        final String numId = "lookup.root.redirect2.com:1";
        log.info("Trying: " + numId);
        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception:", "uk.num.numlib.exc.NumMaximumRedirectsExceededException", e.getCause()
                    .getMessage());
        }

    }

    @Test
    public void test_32_root_query_redirect_success() throws Throwable {
        final String numId = "hosted.redirectme1.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_33_root_query_redirect_success() throws Throwable {
        final String numId = "hosted.redirectme2.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_34_root_query_relative_redirect_fail() throws Throwable {
        final String numId = "hosted.redirectme3.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_35_root_query_redirect_to_self_fail() {
        final String numId = "hosted.redirectme4.com:1";
        log.info("Trying: " + numId);
        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception:", "uk.num.numlib.exc.NumInvalidRedirectException: Cannot redirect back to the same location.", e.getCause()
                    .getMessage());
        }

    }

    @Test
    public void test_36_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@hosted.email.redirect1.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_37_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@hosted.email.redirect2.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
        } catch (final Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_38_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@hosted.email.redirect3.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_39_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@hosted.email.redirect4.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
        } catch (final Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_40_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user.name@hosted.email.redirect5.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
        } catch (final Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_41_email_branch_query_redirect_success() throws Throwable {
        final String numId = "user@hosted.email.redirect6.com:1";
        log.info("Trying: " + numId);
        runQuery(numId);
    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_42_email_branch_query_redirect_too_far_fail() throws Throwable {
        final String numId = "user@hosted.email.redirect7.com:1";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
        } catch (final Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_43_url_branch_query_redirect_success() throws Throwable {
        final String numId = "http://hosted.url.redirect1.com:1/sales/";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_44_url_branch_query_redirect_success() {
        final String numId = "http://hosted.url.redirect2.com:1/sales/";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
        } catch (Exception e) {
            Assert.fail("Unexpected exception.");
        }
    }

    @Test
    public void test_45_url_branch_query_redirect_success() throws Throwable {
        final String numId = "http://hosted.url.redirect3.com:1/sales/";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_46_url_branch_query_redirect_success() throws Throwable {
        final String numId = "http://hosted.url.redirect4.com:1/sales/";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_47_url_branch_query_redirect_success() throws Throwable {
        final String numId = "http://hosted.url.redirect5.com:1/sales/";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }

    }

    @Test
    public void test_48_url_branch_query_redirect_to_self_fail() {
        final String numId = "http://hosted.url.redirect6.com:1/sales/";
        log.info("Trying: " + numId);
        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception:", "uk.num.numlib.exc.NumInvalidRedirectException: Cannot redirect back to the same location.", e.getCause()
                    .getMessage());
        }

    }

    @Test(expected = NumInvalidRedirectException.class)
    public void test_49_url_branch_query_redirect_too_far_fail() throws Throwable {
        final String numId = "http://hosted.url.redirect7.com:1/sales/";
        log.info("Trying: " + numId);

        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            throw e.getCause()
                    .getCause();
        }
    }

    @Test
    public void test_50_lookup_root_redirect_success() throws Throwable {
        final String numId = "lookup.root.hosted.redirect1.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);
    }

    @Test
    public void test_51_lookup_root_redirect_too_many_redirects() {
        final String numId = "lookup.root.hosted.redirect2.com:1";
        log.info("Trying: " + numId);
        try {
            runQuery(numId);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception:", "uk.num.numlib.exc.NumMaximumRedirectsExceededException", e.getCause()
                    .getMessage());
        }

    }


    @Test(expected = NumInvalidDNSHostException.class)
    public void test_72_unknown_host() throws Throwable {
        new NumAPIImpl(new DummyDNSServices(), "gibberish.gibber");
        Assert.fail("Expected a NumInvalidDNSHostException");
    }

    @Test(expected = NumInvalidParameterException.class)
    public void test_73_no_dns_hosts_1() throws Throwable {
        new NumAPIImpl(null);
        Assert.fail("Expected a NumInvalidParameterException");
    }

    @Test(expected = NumInvalidParameterException.class)
    public void test_74_no_dns_hosts_2() throws Throwable {
        new NumAPIImpl(new String[]{});
        Assert.fail("Expected a NumInvalidParameterException");
    }

    @Test
    public void test_75_single_dns_hosts() throws Throwable {
        new NumAPIImpl(new String[]{"0"});
    }

    @Test
    public void test_76_multiple_dns_hosts() throws Throwable {
        new NumAPIImpl(new String[]{"8.8.8.8", "1.1.1.1"});
    }

    @Test
    public void test_77_multiple_dns_hosts_empty() throws Throwable {
        new NumAPIImpl(new String[]{"", ""});
    }

    @Test(expected = NumInvalidDNSHostException.class)
    public void test_78_single_dns_host_empty() throws Throwable {
        new NumAPIImpl(new String[]{""});
        Assert.fail("Expected a NumInvalidDNSHostException");
    }

    @Test(expected = NumInvalidDNSHostException.class)
    public void test_79_single_dns_host_invalid() throws Throwable {
        new NumAPIImpl(new String[]{"gibberish.gibber"});
        Assert.fail("Expected a NumInvalidDNSHostException");
    }

    @Test
    public void test_80_dns_host_and_port_valid() throws Throwable {
        new NumAPIImpl("1.1.1.1", 53);
    }

    @Test(expected = NumInvalidDNSHostException.class)
    public void test_81_dns_host_and_port_invalid() throws Throwable {
        new NumAPIImpl("gibberish.gibber", 53);
        Assert.fail("Expected a NumInvalidDNSHostException");
    }

    @Test
    public void test_82_set_top_level_zone() throws Throwable {
        final NumAPIImpl api = new NumAPIImpl("1.1.1.1", 53);
        api.setTopLevelZone("example.com");

        // Set it back again so it doesn't mess up the other tests.
        api.setTopLevelZone("num.uk");
    }

    @Test(expected = NumInvalidParameterException.class)
    public void test_83_set_top_level_zone_invalid() throws Throwable {
        final NumAPIImpl api = new NumAPIImpl("1.1.1.1", 53);
        api.setTopLevelZone("");
        Assert.fail("Expected a NumInvalidParameterException.");
    }

    @Test
    public void test_90_email_distribution_independent_1_level_success() throws Throwable {
        final String numId = "john.smith@dist1.email.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_91_email_distribution_independent_2_level_success() throws Throwable {
        final String numId = "john.smith@dist2.email.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_92_email_distribution_independent_3_level_success() throws Throwable {
        final String numId = "john.smith@dist3.email.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_93_email_distribution_hosted_1_level_success() throws Throwable {
        final String numId = "john.smith@dist1.hosted.email.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_94_email_distribution_hosted_2_level_success() throws Throwable {
        final String numId = "john.smith@dist2.hosted.email.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_95_email_distribution_hosted_3_level_success() throws Throwable {
        final String numId = "john.smith@dist3.hosted.email.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test(expected = Exception.class)
    public void test_96_email_distribution_independent_fail() throws Throwable {
        final String numId = "john.smith@dist-fail.email.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test(expected = Exception.class)
    public void test_97_email_distribution_hosted_fail() throws Throwable {
        final String numId = "john.smith@dist-fail.hosted.email.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    @Test
    public void test_98_rrset_test() throws Throwable {
        final String numId = "multi.com:1";
        log.info("Trying: " + numId);

        runQuery(numId);

    }

    private void runQuery(final String numId) throws IOException {
        final URL url = NumProtocolSupport.toUrl(numId);

        final NUMURLConnection connection = new NUMURLConnection(url);
        connection.setRequestProperty(NUMURLConnection.USE_POPULATOR, "true");

        final String json = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

        final TestCallbackHandlerExpectingSuccess callbacks = new TestCallbackHandlerExpectingSuccess();
        callbacks.setResult(json);
    }
}
