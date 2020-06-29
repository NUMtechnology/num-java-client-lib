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

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import uk.num.numlib.api.NumAPICallbacks;
import uk.num.numlib.internal.ctx.NumAPIContextBase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ModuleDNSQueriesRedirectionTest {

    private static final List<String[]> testData = new ArrayList<>();

    static {
        // @formatter:off
        //----------------------------------------------------------------------------------------------------------------------------------
        //                         TYPE      LOCATION      REDIRECT DOMAIN/URL/EMAIL                       EXPECTED RESULT
        //----------------------------------------------------------------------------------------------------------------------------------
        testData.add(new String[]{"Domain", "Independent", "new",   "redir.numexample.com",                "new.1._num.redir.numexample.com."});
        testData.add(new String[]{"Domain", "Independent", "/new",  "redir.numexample.com",                "new.1._num.redir.numexample.com."});
        testData.add(new String[]{"Domain", "Independent", "../new",  "redir.numexample.com",              "error"});
        testData.add(new String[]{"Domain", "Independent", "../../new", "redir.numexample.com",            "error"});
        testData.add(new String[]{"Domain", "Independent", "test.com:1", "redir.numexample.com",            "1._num.test.com."});
        testData.add(new String[]{"Domain", "Independent", "test.com:1/", "redir.numexample.com",            "1._num.test.com."});
        testData.add(new String[]{"Domain", "Independent", "test.com:3/", "redir.numexample.com",            "3._num.test.com."});
        testData.add(new String[]{"Domain", "Independent", "test.com:1/c/b/a", "redir.numexample.com",            "a.b.c.1._num.test.com."});
        testData.add(new String[]{"Domain", "Independent", "test.com:3/c/b/a", "redir.numexample.com",            "a.b.c.3._num.test.com."});

        testData.add(new String[]{"Domain", "Hosted",      "new",   "redir.numexample.com",                "new.1._redir.numexample.com.u.b.s.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "/new",  "redir.numexample.com",                "new.1._redir.numexample.com.u.b.s.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "../new",  "redir.numexample.com",              "error"});
        testData.add(new String[]{"Domain", "Hosted",      "../../new", "redir.numexample.com",            "error"});
        testData.add(new String[]{"Domain", "Hosted",      "test.com:1", "redir.numexample.com",            "1._test.com.v.4.b.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "test.com:1/", "redir.numexample.com",            "1._test.com.v.4.b.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "test.com:3/", "redir.numexample.com",            "3._test.com.v.4.b.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "test.com:1/c/b/a", "redir.numexample.com",            "a.b.c.1._test.com.v.4.b.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "test.com:3/c/b/a", "redir.numexample.com",            "a.b.c.3._test.com.v.4.b.num.net."});

        testData.add(new String[]{"URL",    "Independent", "new",   "http://redir.numexample.com",         "new.1._num.redir.numexample.com."});
        testData.add(new String[]{"URL",    "Independent", "/new",  "http://redir.numexample.com",         "new.1._num.redir.numexample.com."});
        testData.add(new String[]{"URL",    "Independent", "../new",  "http://redir.numexample.com",       "error"});
        testData.add(new String[]{"URL",    "Independent", "../../new", "http://redir.numexample.com",     "error"});
        testData.add(new String[]{"URL",    "Independent", "test.com:1", "http://redir.numexample.com",            "1._num.test.com."});
        testData.add(new String[]{"URL",    "Independent", "test.com:1/", "http://redir.numexample.com",            "1._num.test.com."});
        testData.add(new String[]{"URL",    "Independent", "test.com:3/", "http://redir.numexample.com",            "3._num.test.com."});
        testData.add(new String[]{"URL",    "Independent", "test.com:1/c/b/a", "http://redir.numexample.com",            "a.b.c.1._num.test.com."});
        testData.add(new String[]{"URL",    "Independent", "test.com:3/c/b/a", "http://redir.numexample.com",            "a.b.c.3._num.test.com."});

        testData.add(new String[]{"URL",    "Hosted",      "new",   "http://redir.numexample.com",         "new.1._redir.numexample.com.u.b.s.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "/new",  "http://redir.numexample.com",         "new.1._redir.numexample.com.u.b.s.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "../new",  "http://redir.numexample.com",       "error"});
        testData.add(new String[]{"URL",    "Hosted",      "../../new", "http://redir.numexample.com",     "error"});
        testData.add(new String[]{"URL",    "Hosted",      "test.com:1", "http://redir.numexample.com",            "1._test.com.v.4.b.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "test.com:1/", "http://redir.numexample.com",            "1._test.com.v.4.b.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "test.com:3/", "http://redir.numexample.com",            "3._test.com.v.4.b.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "test.com:1/c/b/a", "http://redir.numexample.com",            "a.b.c.1._test.com.v.4.b.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "test.com:3/c/b/a", "http://redir.numexample.com",            "a.b.c.3._test.com.v.4.b.num.net."});

        testData.add(new String[]{"URL",    "Independent", "new",   "http://redir.numexample.com/foo/bar",         "new.bar.foo.1._num.redir.numexample.com."});
        testData.add(new String[]{"URL",    "Independent", "/new",  "http://redir.numexample.com/foo/bar",         "new.1._num.redir.numexample.com."});
        testData.add(new String[]{"URL",    "Independent", "../new",  "http://redir.numexample.com/foo/bar",       "new.foo.1._num.redir.numexample.com."});
        testData.add(new String[]{"URL",    "Independent", "../../new", "http://redir.numexample.com/foo/bar",     "new.1._num.redir.numexample.com."});
        testData.add(new String[]{"URL",    "Independent", "../new/../bar", "http://redir.numexample.com/foo/bar", "error"});
        testData.add(new String[]{"URL",    "Independent", "../../../new","http://redir.numexample.com/foo/bar",   "error"});

        testData.add(new String[]{"URL",    "Hosted",      "new",   "http://redir.numexample.com/foo/bar",         "new.bar.foo.1._redir.numexample.com.u.b.s.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "/new",  "http://redir.numexample.com/foo/bar",         "new.1._redir.numexample.com.u.b.s.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "../new",  "http://redir.numexample.com/foo/bar",       "new.foo.1._redir.numexample.com.u.b.s.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "../../new", "http://redir.numexample.com/foo/bar",     "new.1._redir.numexample.com.u.b.s.num.net."});
        testData.add(new String[]{"URL",    "Hosted",      "../new/../bar", "http://redir.numexample.com/foo/bar", "error"});
        testData.add(new String[]{"URL",    "Hosted",      "../../../new","http://redir.numexample.com/foo/bar",   "error"});

        testData.add(new String[]{"Email",  "Independent", "new",   "john.smith@numexample.com",           "new.1._john.smith.e._num.numexample.com."});
        testData.add(new String[]{"Email",  "Independent", "/new",  "john.smith@numexample.com",           "new.1._john.smith.e._num.numexample.com."});
        testData.add(new String[]{"Email",  "Independent", "../new",  "john.smith@numexample.com",         "error"});
        testData.add(new String[]{"Email",  "Independent", "../../new", "john.smith@numexample.com",       "error"});
        testData.add(new String[]{"Email",  "Independent", "jane.doe@test.com:1", "http://redir.numexample.com",            "1._jane.doe.e._num.test.com."});
        testData.add(new String[]{"Email",  "Independent", "jane.doe@test.com:1/", "http://redir.numexample.com",            "1._jane.doe.e._num.test.com."});
        testData.add(new String[]{"Email",  "Independent", "jane.doe@test.com:3/", "http://redir.numexample.com",            "3._jane.doe.e._num.test.com."});
        testData.add(new String[]{"Email",  "Independent", "jane.doe@test.com:1/c/b/a", "http://redir.numexample.com",            "a.b.c.1._jane.doe.e._num.test.com."});
        testData.add(new String[]{"Email",  "Independent", "jane.doe@test.com:3/c/b/a", "http://redir.numexample.com",            "a.b.c.3._jane.doe.e._num.test.com."});

        testData.add(new String[]{"Email",  "Hosted",      "new",   "john.smith@numexample.com",           "new.1._john.smith.e._numexample.com.c.7.m.num.net."});
        testData.add(new String[]{"Email",  "Hosted",      "/new",  "john.smith@numexample.com",           "new.1._john.smith.e._numexample.com.c.7.m.num.net."});
        testData.add(new String[]{"Email",  "Hosted",      "../new",  "john.smith@numexample.com",         "error"});
        testData.add(new String[]{"Email",  "Hosted",      "../../new", "john.smith@numexample.com",       "error"});
        testData.add(new String[]{"Email",  "Hosted",      "jane.doe@test.com:1", "redir.numexample.com",            "1._jane.doe.e._test.com.v.4.b.num.net."});
        testData.add(new String[]{"Email",  "Hosted",      "jane.doe@test.com:1/", "redir.numexample.com",            "1._jane.doe.e._test.com.v.4.b.num.net."});
        testData.add(new String[]{"Email",  "Hosted",      "jane.doe@test.com:3/", "redir.numexample.com",            "3._jane.doe.e._test.com.v.4.b.num.net."});
        testData.add(new String[]{"Email",  "Hosted",      "jane.doe@test.com:1/c/b/a", "redir.numexample.com",            "a.b.c.1._jane.doe.e._test.com.v.4.b.num.net."});
        testData.add(new String[]{"Email",  "Hosted",      "jane.doe@test.com:3/c/b/a", "redir.numexample.com",            "a.b.c.3._jane.doe.e._test.com.v.4.b.num.net."});
        // @formatter:on
    }

    @Test
    public void testEmailBranchRelativeRedirectTwoLevels() {
        final List<TestResult> failures = testData.stream()
                .map(TestData::new)
                .map(this::test)
                .filter(x -> !x.pass)
                .collect(Collectors.toList());

        failures.forEach(System.out::println);
        if (failures.size() > 0) {
            Assert.fail(String.format("%d tests failed.", failures.size()));
        }
    }

    private TestResult test(final TestData testData) {
        boolean pass = false;
        String message = "";
        try {
            final ModuleDNSQueries moduleDNSQueries = new ModuleDNSQueries(1, testData.address);
            moduleDNSQueries.initialise();
            final NumAPIContextBase ctx = new NumAPIContextBase();
            ctx.setModuleDNSQueries(moduleDNSQueries);
            ctx.setLocation((testData.location.equals("Independent") ? NumAPICallbacks.Location.INDEPENDENT : NumAPICallbacks.Location.HOSTED));
            ctx.handleQueryRedirect(testData.redirect);

            if (testData.location.equals("Independent")) {
                final String actual = moduleDNSQueries.getIndependentRecordLocation();
                if (testData.expectedResult.equals(actual)) {
                    pass = true;
                } else {
                    message = "Expected: " + testData.expectedResult + ", but found: " + actual;
                }
            } else if (testData.location.equals("Hosted")) {
                final String actual = moduleDNSQueries.getHostedRecordLocation();
                if (testData.expectedResult.equals(actual)) {
                    pass = true;
                } else {
                    message = "Expected: " + testData.expectedResult + ", but found: " + actual;
                }
            }
        } catch (Throwable e) {
            if (testData.expectedResult.equals("error")) {
                pass = true;
            } else {
                log.error("Exception", e);
                message = e.getMessage();
            }
        }

        return new TestResult(testData, pass, message);
    }

    @RequiredArgsConstructor
    @ToString
    private static class TestResult {

        public final TestData testData;

        public final boolean pass;

        public final String message;

    }

    @ToString
    private static class TestData {

        public final String type;

        public final String location;

        public final String redirect;

        public final String address;

        public final String expectedResult;

        TestData(final String[] data) {
            type = data[0];
            location = data[1];
            redirect = data[2];
            address = data[3];
            expectedResult = data[4];
        }

    }

}