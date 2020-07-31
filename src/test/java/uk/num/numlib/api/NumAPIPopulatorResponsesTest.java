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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.num.numlib.internal.dns.DummyDNSServices;
import uk.num.numlib.internal.util.PopulatorRetryConfig;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Log4j2
public class NumAPIPopulatorResponsesTest {
    private final String[] numIds = {
            "populator.response.1.com:1",
            "populator.response.2.com:1",
            "populator.response.3.com:1",
            "populator.response.4.com:1",
            "populator.response.100.com:1",
            "populator.response.101.com:1",
            "populator.response.txt.com:1"
    };

    /**
     * Reduce the retry delays during unit tests
     */
    @BeforeClass
    public static void beforeClass() {
        PopulatorRetryConfig.RETRY_DELAYS = new int[]{10, 10, 10, 10, 10, 10, 10, 10};
        PopulatorRetryConfig.ERROR_RETRY_DELAYS = new int[]{10, 10};
    }

    @Test
    public void test_00_long_test() {
        final String numId = numIds[0];
        log.info("Trying: " + numId);
        try {
            runQuery(numId, 60);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception.", "uk.num.numlib.exc.NumNoRecordAvailableException: Cannot retrieve NUM record from any location.", e.getLocalizedMessage());
        }

    }

    @Test
    public void test_01_long_test() {
        final String numId = numIds[1];
        log.info("Trying: " + numId);
        try {
            runQuery(numId, 60);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception.", "uk.num.numlib.exc.NumNoRecordAvailableException: Cannot retrieve NUM record from any location.", e.getLocalizedMessage());
        }

    }

    @Test
    public void test_02_long_test() {
        final String numId = numIds[2];
        log.info("Trying: " + numId);
        try {
            runQuery(numId, 60);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception.", "uk.num.numlib.exc.NumNoRecordAvailableException: Cannot retrieve NUM record from any location.", e.getLocalizedMessage());
        }

    }

    @Test
    public void test_03_long_test() {
        final String numId = numIds[3];
        log.info("Trying: " + numId);
        try {
            runQuery(numId, 60);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception.", "uk.num.numlib.exc.NumInvalidPopulatorResponseCodeException: Bad response received from the populator service.", e.getLocalizedMessage());
        }

    }

    @Test
    public void test_04_long_test() {
        final String numId = numIds[4];
        log.info("Trying: " + numId);
        try {
            runQuery(numId, 60);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception.", "uk.num.numlib.exc.NumNoRecordAvailableException: Cannot retrieve NUM record from any location.", e.getLocalizedMessage());
        }

    }

    @Test
    public void test_05_long_test() {
        final String numId = numIds[5];
        log.info("Trying: " + numId);
        try {
            runQuery(numId, 60);
            Assert.fail("Expected an exception.");
        } catch (Exception e) {
            Assert.assertEquals("Incorrect exception.", "uk.num.numlib.exc.NumPopulatorErrorException: Records for this domain can't be populated.", e.getLocalizedMessage());
        }

    }

    /**
     * This test simulates the situation in which the DNS Responder has a valid TXT record less than 2 minutes old so it
     * returns that instead of a _status or _error value.
     */
    @Test
    public void test_06_TXT_response_test() throws Exception {
        final String numId = numIds[6];
        log.info("Trying: " + numId);
        runQuery(numId, 60);
    }

    public void runQuery(final String numId, final int timeoutSeconds) throws Exception {
        //
        // Create the NumAPI Object
        //
        final NumAPI numAPI = new NumAPIImpl(new DummyDNSServices(), "0");
        //
        // Create a module context. The NumAPI will retrieve the module config from modules.numprotocol.com
        // Domain name can be a simple domain name, URL, email address, or sub-domain
        //
        final NumAPIContext ctx = numAPI.begin(numId, 1000);
        ctx.setPopulatorQueryRequired(true);
        //
        // Get the required user variables - the client must populate values obtained from the user or from its own config data
        //
        final UserVariable[] userVariables = ctx.getRequiredUserVariables();
        //
        // Set the required user variables obtained from the user
        //
        for (final UserVariable ruv : userVariables) {
            if (ruv.getKey()
                    .equals("_C")) {
                ruv.setValue("gb");
            }
            if (ruv.getKey()
                    .equals("_L")) {
                ruv.setValue("en");
            }
        }
        ctx.setRequiredUserVariables(userVariables);
        //
        // Create a callback object to handle messages from the API
        // (Implement your own NumAPICallbacks object or extend NumAPICallbacksDefaultHandler and
        // override methods as necessary)
        //
        final NumAPICallbacks handler = new TestCallbackHandlerExpectingSuccess();
        //
        // Retrieve the NUM record - all responses will be sent to the handler.
        // 1. Re-interpret the module config now that we have the user variables.
        // 2. Get the NUM record from DNS (either independent, hosted, or populator)
        // 2. Prepend the RCF to the NUM record from DNS as a *LOAD entry (i.e. the module URL)
        // 3. Run the resulting record through the Java MODL Interpreter and make the results available to the client via the handler.
        //
        final Future<String> future = numAPI.retrieveNumRecord(ctx, handler, 1000);
        final String json = future.get(timeoutSeconds, TimeUnit.SECONDS);
        //
        // Process the resulting record as required by the client application.
        // (in the 'handler' object implementation)
        //

        // Cancel any outstanding DNS requests
        numAPI.shutdown();
    }
}
