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
import uk.num.numlib.internal.ctx.AppContext;
import uk.num.numlib.internal.util.NonBlankString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ModuleDNSQueriesTest2 {
    private static final AppContext appContext = new AppContext();
    private static final List<String[]> testData = new ArrayList<>();

    static {
        // @formatter:off
        //----------------------------------------------------------------------------------------------------------------------------------
        //                         TYPE      LOCATION      DOMAIN/URL/EMAIL                       EXPECTED RESULT
        //----------------------------------------------------------------------------------------------------------------------------------
        testData.add(new String[]{"Domain", "Independent", "numexample.com",                "1._num.numexample.com."});
        testData.add(new String[]{"Domain", "Independent", "numexample.com/",               "1._num.numexample.com."});
        testData.add(new String[]{"Domain", "Independent", "numexample.com/foo",            "foo.1._num.numexample.com."});
        testData.add(new String[]{"Domain", "Independent", "www.numexample.com",            "1._num.numexample.com."});

        testData.add(new String[]{"Domain", "Hosted",      "numexample.com",                "1._numexample.com.c.7.m.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "numexample.com/",               "1._numexample.com.c.7.m.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "numexample.com/foo",            "foo.1._numexample.com.c.7.m.num.net."});
        testData.add(new String[]{"Domain", "Hosted",      "www.numexample.com",            "1._numexample.com.c.7.m.num.net."});

        testData.add(new String[]{"Domain", "Populator",   "numexample.com",                "1._numexample.com.populator.num.net."});
        testData.add(new String[]{"Domain", "Populator",   "numexample.com/",               "1._numexample.com.populator.num.net."});
        testData.add(new String[]{"Domain", "Populator",   "numexample.com/foo",            null});
        testData.add(new String[]{"Domain", "Populator",   "www.numexample.com",            "1._numexample.com.populator.num.net."});


        testData.add(new String[]{"URL", "Independent",    "http://www.numexample.com/",                                          "1._num.numexample.com."});
        testData.add(new String[]{"URL", "Independent",    "http://www.numexample.com",                                           "1._num.numexample.com."});
        testData.add(new String[]{"URL", "Independent",    "http://www.numexample.com/foo",                                       "foo.1._num.numexample.com."});
        testData.add(new String[]{"URL", "Independent",    "http://www.numexample.com/bar/foo/page.htm?param=123",                "foo.bar.1._num.numexample.com."});

        testData.add(new String[]{"URL", "Hosted",         "http://www.numexample.com/",                                          "1._numexample.com.c.7.m.num.net."});
        testData.add(new String[]{"URL", "Hosted",         "http://www.numexample.com",                                           "1._numexample.com.c.7.m.num.net."});
        testData.add(new String[]{"URL", "Hosted",         "http://www.numexample.com/foo",                                       "foo.1._numexample.com.c.7.m.num.net."});
        testData.add(new String[]{"URL", "Hosted",         "http://www.numexample.com/bar/foo/page.htm?param=123",                "foo.bar.1._numexample.com.c.7.m.num.net."});

        testData.add(new String[]{"URL", "Populator",      "http://www.numexample.com/",                                          "1._numexample.com.populator.num.net."});
        testData.add(new String[]{"URL", "Populator",      "http://www.numexample.com",                                           "1._numexample.com.populator.num.net."});
        testData.add(new String[]{"URL", "Populator",      "http://www.numexample.com/foo",                                       null});
        testData.add(new String[]{"URL", "Populator",      "http://www.numexample.com/bar/foo/page.htm?param=123",                null});



        testData.add(new String[]{"Email", "Independent",  "john.smith@example.com",                 "1._john.smith.e._num.example.com."});
        testData.add(new String[]{"Email", "Independent",  "john.smith@example.com/",                "1._john.smith.e._num.example.com."});
        testData.add(new String[]{"Email", "Independent",  "john.smith@example.com/foo/bar",         "bar.foo.1._john.smith.e._num.example.com."});

        testData.add(new String[]{"Email", "Hosted",       "john.smith@example.com",                "1._john.smith.e._example.com.9.h.1.num.net."});
        testData.add(new String[]{"Email", "Hosted",       "john.smith@example.com/",               "1._john.smith.e._example.com.9.h.1.num.net."});
        testData.add(new String[]{"Email", "Hosted",       "john.smith@example.com/foo/bar",        "bar.foo.1._john.smith.e._example.com.9.h.1.num.net."});

        testData.add(new String[]{"Email", "Populator",    "john.smith@example.com",                null});
        testData.add(new String[]{"Email", "Populator",    "john.smith@example.com/",               null});
        testData.add(new String[]{"Email", "Populator",    "john.smith@example.com/foo/bar",        null});
        // @formatter:on
    }

    @Test
    public void tests() {
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
            final ModuleDNSQueries m = new ModuleDNSQueries(NonBlankString.of("1"), NonBlankString.of(testData.address));
            m.initialise(appContext);
            String actual = "";

            switch (testData.location) {
                case "Independent":
                    actual = m.getIndependentRecordLocation();
                    break;
                case "Hosted":
                    actual = m.getHostedRecordLocation();
                    break;
                case "Populator":
                    actual = m.getPopulatorLocation();
                    break;
            }

            if ((testData.expectedResult == null && actual == null) || (actual != null && actual.equals(testData.expectedResult))) {
                pass = true;
            } else {
                message = "Expected: " + testData.expectedResult + ", but found: " + actual;
            }
        } catch (Throwable e) {
            if (testData.expectedResult == null) {
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
        public final String address;
        public final String expectedResult;

        TestData(final String[] data) {
            type = data[0];
            location = data[1];
            address = data[2];
            expectedResult = data[3];
        }
    }
}