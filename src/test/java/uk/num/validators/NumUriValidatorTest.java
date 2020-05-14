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

package uk.num.validators;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.num.net.NumProtocolSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Log4j2
public class NumUriValidatorTest {

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
            "num://test:-1000/a",
            "thislabeltoolongthislabeltoolongthislabeltoolongthislabeltoolong.test.com",
            "thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.thisdomainistoolong.test.com",
            "this label has a space in it.test.com",
            "http://bad.protocol.com",
            "http://too.many.colons.com::0/path",
            "http://zero.length.path.component.com/path//path",
            "http://too.long.path.component.com/pathtoolongpathtoolongpathtoolongpathtoolongpathtoolongpathtoolong",
    };

    @BeforeClass
    public static void beforeClass() {
        NumProtocolSupport.init();
    }

    @Test
    public void testValidURIs() {
        final List<String> errors = new ArrayList<>();
        for (final String uri : validUriStrings) {
            final ValidationResult validationResult = NumUriValidator.validate(uri);
            if (!validationResult.isValid()) {
                final String error = "URI: " + StringEscapeUtils.escapeJava(uri) + " is rejected when it should be accepted.";
                errors.add(error);
                errors.addAll(validationResult.getErrors()
                        .stream()
                        .map(e -> "\t" + e)
                        .collect(Collectors.toList()));
                errors.add("");
            }
        }
        if (errors.size() > 0) {
            errors.forEach(System.out::println);
            fail("There are errors");
        }
    }

    @Test
    public void testInvalidURIs() {
        final List<String> errors = new ArrayList<>();
        for (final String uri : invalidUriStrings) {
            final ValidationResult validationResult = NumUriValidator.validate(uri);
            if (validationResult.isValid()) {
                final String error = "URI: " + StringEscapeUtils.escapeJava(uri) + " is accepted when it should be rejected.";
                errors.add(error);
                errors.addAll(validationResult.getErrors()
                        .stream()
                        .map(e -> "\t" + e)
                        .collect(Collectors.toList()));
                errors.add("");
            }
        }
        if (errors.size() > 0) {
            errors.forEach(System.out::println);
            fail("There are errors");
        }
    }

    @Test
    public void testNullUriValidation() {
        assertFalse(NumUriValidator.validate(null)
                .isValid());
    }

    @Test
    public void testNullDomainValidation() {
        assertFalse(NumDomainValidator.validate(null)
                .isValid());
    }

    @Test
    public void testNullEmailValidation() {
        assertFalse(NumEmailAddressValidator.validate(null)
                .isValid());
    }

    @Test
    public void testEmailNoAtSymbolValidation() {
        assertFalse(NumEmailAddressValidator.validate("joe.bloggs_example.com")
                .isValid());
    }

    @Test
    public void testEmailLocalPartTooLongValidation() {
        assertFalse(NumEmailAddressValidator.validate("thispartistoolongthispartistoolongthispartistoolongthispartistoolong@example.com")
                .isValid());
    }

    @Test
    public void testEmailEmptyLocalPartValidation() {
        assertFalse(NumEmailAddressValidator.validate("@example.com")
                .isValid());
    }

    @Test
    public void testNullPathValidation() {
        assertFalse(NumUriPathValidator.validate(null)
                .isValid());
    }

    @Test
    public void testPathNotStartingWithSlashValidation() {
        assertFalse(NumUriPathValidator.validate("noslash/at_start")
                .isValid());
    }

}