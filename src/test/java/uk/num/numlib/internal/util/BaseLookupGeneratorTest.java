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
import static uk.num.numlib.internal.util.LookupGenerator.TrailingDot.*;

public class BaseLookupGeneratorTest {

    @Test
    public void testGetRootHostedLocationNoModuleNumber() throws MalformedURLException {
        final DomainLookupGenerator lookupGenerator = new DomainLookupGenerator("example.com");
        assertEquals("_example.com.9.h.1.num.net", lookupGenerator.getRootHostedLocationNoModuleNumber(NO_TRAILING_DOT));
    }

    @Test
    public void testGetRootIndependentLocationNoModuleNumber() throws MalformedURLException {
        final DomainLookupGenerator lookupGenerator = new DomainLookupGenerator("example.com");
        assertEquals("_num.example.com", lookupGenerator.getRootIndependentLocationNoModuleNumber(NO_TRAILING_DOT));
    }

    @Test
    public void testGetRootHostedLocationNoModuleNumberWithTrailingDot() throws MalformedURLException {
        final DomainLookupGenerator lookupGenerator = new DomainLookupGenerator("example.com");
        assertEquals("_example.com.9.h.1.num.net.", lookupGenerator.getRootHostedLocationNoModuleNumber(ADD_TRAILING_DOT));
    }

    @Test
    public void testGetRootIndependentLocationNoModuleNumberWithTrailingDot() throws MalformedURLException {
        final DomainLookupGenerator lookupGenerator = new DomainLookupGenerator("example.com");
        assertEquals("_num.example.com.", lookupGenerator.getRootIndependentLocationNoModuleNumber(ADD_TRAILING_DOT));
    }

}