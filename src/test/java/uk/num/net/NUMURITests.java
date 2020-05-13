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

package uk.num.net;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class NUMURITests {

    private final String[] validUriStrings = {
            "test_$*&.example.com",
            "numexample.com",
            "_test.example.com",
            "test_123.example.com",
            "test.test.",
            "a.b.c.d.e.f.g.h",
            "例.例",
            "john.smith@numexample.com",
            "john.smith@numexample.com:0",
            "john.smith@numexample.com:10",
            "john.smith@numexample.com:1/home",
            "num://john.smith@numexample.com:1/",
            "num://john.smith@numexample.com:12/test",
            "test@test.test",
            "test with spaces@test.test",
            "test with spaces@test.test:10",
            "test with spaces@test.test:1/test",
            "num://test with spaces@test.test:1/test",
            "num://test with spaces@test.test",
            "test=test@gmail.com",
            "e226c478-c284-4c57-8187-95bfe204dbe7.com",
            "me@test.com",
            "num://test.com:123456/test"
    };

    private final String[] invalidUriStrings = {
            "example.t_l_d",
            "test_domain.com",
            "例",
            "test...test",
            "test..test",
            ".bad",
            "bad.",
            "test@test",
            "test@test@test.test",
            "test\ntest@test.com",
            "test\ttest@test.com",
            "test\rtest@test.com",
            "test\btest@test.com",
            "test\ftest@test.com",
            "test\ntesttest.com",
            "test\ttesttest.com",
            "test\rtesttest.com",
            "test\btesttest.com",
            "test\ftesttest.com",
            "num://test:a/a",
            "num://test:-1/a",
            "num://test:-1000/a"
    };

    @BeforeClass
    public static void beforeClass() {
        NumProtocolSupport.init();
    }

    @Test
    public void testValidURIs() {
        final List<String> errors = new ArrayList<>();
        for (final String uri : validUriStrings) {
            try {
                NumProtocolSupport.toUrl(uri);
            } catch (MalformedURLException e) {
                errors.add("URI: " + StringEscapeUtils.escapeJava(uri) + " is rejected when it should be accepted.");
            }
        }
        if (errors.size() > 0) {
            errors.forEach(System.out::println);
            Assert.fail("There are errors");
        }
    }

    @Test
    public void testInvalidURIs() {
        final List<String> errors = new ArrayList<>();
        for (final String uri : invalidUriStrings) {
            try {
                NumProtocolSupport.toUrl(uri);
                errors.add("URI: " + StringEscapeUtils.escapeJava(uri) + " is accepted when it should be rejected.");
            } catch (MalformedURLException e) {
                // expected
            }
        }
        if (errors.size() > 0) {
            errors.forEach(System.out::println);
            Assert.fail("There are errors");
        }
    }

}
