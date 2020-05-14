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

import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class LookupGeneratorTest {

    public static final String EXPECTED_INDEPENDENT = "1._num.numexample.com.";

    public static final String EXPECTED_HOSTED = "1._numexample.com.c.7.m.num.net.";

    public static final String EXPECTED_POPULATOR = "1._numexample.com.populator.num.net.";

    @Test
    public void consistencyTest1() throws MalformedURLException, NumInvalidParameterException {
        final String s1 = new DomainLookupGenerator("numexample.com").getHostedLocation(1);
        final String s2 = new DomainLookupGenerator("www.numexample.com").getHostedLocation(1);
        final String s3 = new DomainLookupGenerator("numexample.com/").getHostedLocation(1);
        final String s4 = new DomainLookupGenerator("www.numexample.com/").getHostedLocation(1);
        final String s5 = new URLLookupGenerator("http://numexample.com").getHostedLocation(1);
        final String s6 = new URLLookupGenerator("https://numexample.com").getHostedLocation(1);
        final String s7 = new URLLookupGenerator("http://www.numexample.com").getHostedLocation(1);
        final String s8 = new URLLookupGenerator("https://www.numexample.com").getHostedLocation(1);
        final String s9 = new URLLookupGenerator("http://numexample.com/").getHostedLocation(1);
        final String s10 = new URLLookupGenerator("https://numexample.com/").getHostedLocation(1);
        final String s11 = new URLLookupGenerator("http://www.numexample.com/").getHostedLocation(1);
        final String s12 = new URLLookupGenerator("https://www.numexample.com/").getHostedLocation(1);

        assertEquals(EXPECTED_HOSTED, s1);
        assertEquals(EXPECTED_HOSTED, s2);
        assertEquals(EXPECTED_HOSTED, s3);
        assertEquals(EXPECTED_HOSTED, s4);
        assertEquals(EXPECTED_HOSTED, s5);
        assertEquals(EXPECTED_HOSTED, s6);
        assertEquals(EXPECTED_HOSTED, s7);
        assertEquals(EXPECTED_HOSTED, s8);
        assertEquals(EXPECTED_HOSTED, s9);
        assertEquals(EXPECTED_HOSTED, s10);
        assertEquals(EXPECTED_HOSTED, s11);
        assertEquals(EXPECTED_HOSTED, s12);
    }

    @Test
    public void consistencyTest2() throws MalformedURLException, NumInvalidParameterException {
        final String s1 = new DomainLookupGenerator("numexample.com").getIndependentLocation(1);
        final String s2 = new DomainLookupGenerator("www.numexample.com").getIndependentLocation(1);
        final String s3 = new DomainLookupGenerator("numexample.com/").getIndependentLocation(1);
        final String s4 = new DomainLookupGenerator("www.numexample.com/").getIndependentLocation(1);
        final String s5 = new URLLookupGenerator("http://numexample.com").getIndependentLocation(1);
        final String s6 = new URLLookupGenerator("https://numexample.com").getIndependentLocation(1);
        final String s7 = new URLLookupGenerator("http://www.numexample.com").getIndependentLocation(1);
        final String s8 = new URLLookupGenerator("https://www.numexample.com").getIndependentLocation(1);
        final String s9 = new URLLookupGenerator("http://numexample.com/").getIndependentLocation(1);
        final String s10 = new URLLookupGenerator("https://numexample.com/").getIndependentLocation(1);
        final String s11 = new URLLookupGenerator("http://www.numexample.com/").getIndependentLocation(1);
        final String s12 = new URLLookupGenerator("https://www.numexample.com/").getIndependentLocation(1);

        assertEquals(EXPECTED_INDEPENDENT, s1);
        assertEquals(EXPECTED_INDEPENDENT, s2);
        assertEquals(EXPECTED_INDEPENDENT, s3);
        assertEquals(EXPECTED_INDEPENDENT, s4);
        assertEquals(EXPECTED_INDEPENDENT, s5);
        assertEquals(EXPECTED_INDEPENDENT, s6);
        assertEquals(EXPECTED_INDEPENDENT, s7);
        assertEquals(EXPECTED_INDEPENDENT, s8);
        assertEquals(EXPECTED_INDEPENDENT, s9);
        assertEquals(EXPECTED_INDEPENDENT, s10);
        assertEquals(EXPECTED_INDEPENDENT, s11);
        assertEquals(EXPECTED_INDEPENDENT, s12);
    }

    @Test
    public void consistencyTest3() throws MalformedURLException, NumInvalidParameterException {
        final String s1 = new DomainLookupGenerator("numexample.com").getPopulatorLocation(1);
        final String s2 = new DomainLookupGenerator("www.numexample.com").getPopulatorLocation(1);
        final String s3 = new DomainLookupGenerator("numexample.com/").getPopulatorLocation(1);
        final String s4 = new DomainLookupGenerator("www.numexample.com/").getPopulatorLocation(1);
        final String s5 = new URLLookupGenerator("http://numexample.com").getPopulatorLocation(1);
        final String s6 = new URLLookupGenerator("https://numexample.com").getPopulatorLocation(1);
        final String s7 = new URLLookupGenerator("http://www.numexample.com").getPopulatorLocation(1);
        final String s8 = new URLLookupGenerator("https://www.numexample.com").getPopulatorLocation(1);
        final String s9 = new URLLookupGenerator("http://numexample.com/").getPopulatorLocation(1);
        final String s10 = new URLLookupGenerator("https://numexample.com/").getPopulatorLocation(1);
        final String s11 = new URLLookupGenerator("http://www.numexample.com/").getPopulatorLocation(1);
        final String s12 = new URLLookupGenerator("https://www.numexample.com/").getPopulatorLocation(1);

        assertEquals(EXPECTED_POPULATOR, s1);
        assertEquals(EXPECTED_POPULATOR, s2);
        assertEquals(EXPECTED_POPULATOR, s3);
        assertEquals(EXPECTED_POPULATOR, s4);
        assertEquals(EXPECTED_POPULATOR, s5);
        assertEquals(EXPECTED_POPULATOR, s6);
        assertEquals(EXPECTED_POPULATOR, s7);
        assertEquals(EXPECTED_POPULATOR, s8);
        assertEquals(EXPECTED_POPULATOR, s9);
        assertEquals(EXPECTED_POPULATOR, s10);
        assertEquals(EXPECTED_POPULATOR, s11);
        assertEquals(EXPECTED_POPULATOR, s12);
    }

}