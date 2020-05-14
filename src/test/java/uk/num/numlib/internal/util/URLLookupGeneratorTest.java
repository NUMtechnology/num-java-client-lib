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

import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class URLLookupGeneratorTest {

    @Test
    public void testConstructor1() throws MalformedURLException {
        final URLLookupGenerator urlLookupGenerator = new URLLookupGenerator("http://numexample.com");
        assertEquals("numexample.com", urlLookupGenerator.domain);
        assertNull(urlLookupGenerator.branch);
    }

    @Test
    public void testConstructor2() throws MalformedURLException {
        final URLLookupGenerator urlLookupGenerator = new URLLookupGenerator("http://numexample.com/foo");
        assertEquals("numexample.com", urlLookupGenerator.domain);
        assertEquals("foo", urlLookupGenerator.branch);
    }

    @Test
    public void testConstructor3() throws MalformedURLException {
        final URLLookupGenerator urlLookupGenerator = new URLLookupGenerator("http://numexample.com/foo/bar");
        assertEquals("numexample.com", urlLookupGenerator.domain);
        assertEquals("bar.foo", urlLookupGenerator.branch);
    }

}