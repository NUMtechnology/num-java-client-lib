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
import uk.num.numlib.exc.NumInvalidParameterException;
import uk.num.numlib.internal.ctx.AppContext;

import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class DomainLookupGeneratorTest {

    private static final AppContext appContext = new AppContext();

    @Test
    public void testConstructor1() throws MalformedURLException {
        final DomainLookupGenerator domainLookupGenerator = new DomainLookupGenerator(appContext, "numexample.com");
        assertEquals("numexample.com", domainLookupGenerator.domain);
        assertNull(domainLookupGenerator.branch);
    }

    @Test
    public void testConstructor2() throws MalformedURLException {
        final DomainLookupGenerator domainLookupGenerator = new DomainLookupGenerator(appContext, "numexample.com/foo");
        assertEquals("numexample.com", domainLookupGenerator.domain);
        assertEquals("foo", domainLookupGenerator.branch);
    }

    @Test
    public void testConstructor3() throws MalformedURLException {
        final DomainLookupGenerator domainLookupGenerator = new DomainLookupGenerator(appContext, "numexample.com/foo/bar");
        assertEquals("numexample.com", domainLookupGenerator.domain);
        assertEquals("bar.foo", domainLookupGenerator.branch);
    }

    @Test
    public void testConstructor4() throws MalformedURLException {
        final DomainLookupGenerator domainLookupGenerator = new DomainLookupGenerator(appContext, "testdomain例.com/test1例");
        assertEquals("xn--testdomain-4y5p.com", domainLookupGenerator.domain);
        assertEquals("xn--test1-9d3h", domainLookupGenerator.branch);
    }

    @Test
    public void testConstructor5() throws MalformedURLException, NumInvalidParameterException {
        final DomainLookupGenerator domainLookupGenerator = new DomainLookupGenerator(appContext, "testdomain例.com/test1例/test2例/test3例");
        assertEquals("xn--testdomain-4y5p.com", domainLookupGenerator.domain);
        assertEquals("xn--test3-9d3h.xn--test2-9d3h.xn--test1-9d3h", domainLookupGenerator.branch);
        assertEquals("xn--test3-9d3h.xn--test2-9d3h.xn--test1-9d3h.1._num.xn--testdomain-4y5p.com.", domainLookupGenerator.getIndependentLocation(1));
        assertEquals("xn--test3-9d3h.xn--test2-9d3h.xn--test1-9d3h.1._xn--testdomain-4y5p.com.b.5.m.num.net.", domainLookupGenerator.getHostedLocation(1));
    }

    @Test
    public void testConstructor6() throws MalformedURLException, NumInvalidParameterException {
        final DomainLookupGenerator domainLookupGenerator = new DomainLookupGenerator(appContext, "testdomain例.com");
        assertEquals("xn--testdomain-4y5p.com", domainLookupGenerator.domain);
        assertEquals("1._num.xn--testdomain-4y5p.com.", domainLookupGenerator.getIndependentLocation(1));
        assertEquals("1._xn--testdomain-4y5p.com.b.5.m.num.net.", domainLookupGenerator.getHostedLocation(1));
        assertEquals("1._xn--testdomain-4y5p.com.populator.num.net.", domainLookupGenerator.getPopulatorLocation(1));
    }

}