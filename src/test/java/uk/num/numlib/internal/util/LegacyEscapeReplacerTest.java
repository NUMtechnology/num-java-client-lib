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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LegacyEscapeReplacerTest {

    private final LegacyEscapeReplacer replacer = new LegacyEscapeReplacer();

    @Test
    public void testNoEscapeSequences() {
        final String result = replacer.apply("nothing to replace here");
        assertEquals("nothing to replace here", result);
    }

    @Test
    public void testNull() {
        final String result = replacer.apply(null);
        assertNull(result);
    }

    @Test
    public void testEmpty() {
        final String result = replacer.apply("");
        assertEquals("", result);
    }

    @Test
    public void testEscapes() {
        final String result = replacer.apply("\\;\\ \\;\\ \\;\\ ");
        assertEquals("; ; ; ", result);
    }
}