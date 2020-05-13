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

package uk.num.numlib.internal.util;

import org.junit.Test;
import uk.num.numlib.internal.ctx.AppContext;

import static org.junit.Assert.*;

public class EmailLookupGeneratorTest {

    private static final AppContext appContext = new AppContext();

    @Test
    public void testConstructor1() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com");
        assertEquals("numexample.com", emailLookupGenerator.domain);
        assertEquals("john.smith", emailLookupGenerator.localPart);
        assertNull(emailLookupGenerator.branch);
    }

    @Test
    public void testConstructor2() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com/foo");
        assertEquals("numexample.com", emailLookupGenerator.domain);
        assertEquals("john.smith", emailLookupGenerator.localPart);
        assertEquals("foo", emailLookupGenerator.branch);
    }

    @Test
    public void testConstructor3() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com/foo/bar");
        assertEquals("numexample.com", emailLookupGenerator.domain);
        assertEquals("john.smith", emailLookupGenerator.localPart);
        assertEquals("bar.foo", emailLookupGenerator.branch);
    }

    @Test
    public void testZoneDistribution1_1() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com");
        final String location = emailLookupGenerator.getDistributedIndependentLocation(1, 1);
        assertEquals("1._john.smith.3.e._num.numexample.com.", location);

    }

    @Test
    public void testZoneDistribution2_1() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com/foo/bar");
        final String location = emailLookupGenerator.getDistributedIndependentLocation(1, 1);
        assertEquals("bar.foo.1._john.smith.3.e._num.numexample.com.", location);

    }

    @Test
    public void testZoneDistribution3_1() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com");
        final String location = emailLookupGenerator.getDistributedHostedLocation(1, 1);
        assertEquals("1._john.smith.3.e._numexample.com.c.7.m.num.net.", location);

    }

    @Test
    public void testZoneDistribution4_1() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com/foo/bar");
        final String location = emailLookupGenerator.getDistributedHostedLocation(1, 1);
        assertEquals("bar.foo.1._john.smith.3.e._numexample.com.c.7.m.num.net.", location);

    }

    @Test
    public void testZoneDistribution5_2() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com");
        final String location = emailLookupGenerator.getDistributedIndependentLocation(1, 2);
        assertEquals("1._john.smith.6.3.e._num.numexample.com.", location);

    }

    @Test
    public void testZoneDistribution6_2() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com/foo/bar");
        final String location = emailLookupGenerator.getDistributedIndependentLocation(1, 2);
        assertEquals("bar.foo.1._john.smith.6.3.e._num.numexample.com.", location);

    }

    @Test
    public void testZoneDistribution7_2() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com");
        final String location = emailLookupGenerator.getDistributedHostedLocation(1, 2);
        assertEquals("1._john.smith.6.3.e._numexample.com.c.7.m.num.net.", location);

    }

    @Test
    public void testZoneDistribution8_2() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com/foo/bar");
        final String location = emailLookupGenerator.getDistributedHostedLocation(1, 2);
        assertEquals("bar.foo.1._john.smith.6.3.e._numexample.com.c.7.m.num.net.", location);

    }

    @Test
    public void testZoneDistribution9_3() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com");
        final String location = emailLookupGenerator.getDistributedIndependentLocation(1, 3);
        assertEquals("1._john.smith.d.6.3.e._num.numexample.com.", location);

    }

    @Test
    public void testZoneDistribution10_3() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com/foo/bar");
        final String location = emailLookupGenerator.getDistributedIndependentLocation(1, 3);
        assertEquals("bar.foo.1._john.smith.d.6.3.e._num.numexample.com.", location);

    }

    @Test
    public void testZoneDistribution11_3() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com");
        final String location = emailLookupGenerator.getDistributedHostedLocation(1, 3);
        assertEquals("1._john.smith.d.6.3.e._numexample.com.c.7.m.num.net.", location);

    }

    @Test
    public void testZoneDistribution12_3() {
        final EmailLookupGenerator emailLookupGenerator = new EmailLookupGenerator(appContext, "john.smith@numexample.com/foo/bar");
        final String location = emailLookupGenerator.getDistributedHostedLocation(1, 3);
        assertEquals("bar.foo.1._john.smith.d.6.3.e._numexample.com.c.7.m.num.net.", location);

    }

}