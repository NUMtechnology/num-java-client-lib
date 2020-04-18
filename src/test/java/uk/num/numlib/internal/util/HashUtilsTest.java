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

import org.junit.Assert;
import org.junit.Test;

public class HashUtilsTest {

    @Test
    public void hash_01() {
        final String formattedResult = HashUtils.hash1("test");
        Assert.assertEquals("Incorrect formatted value", ".j", formattedResult);
    }

    @Test
    public void hash_02() {
        final String formattedResult = HashUtils.hash2("test");
        Assert.assertEquals("Incorrect formatted value", ".r.j", formattedResult);
    }

    @Test
    public void hash_03() {
        final String formattedResult = HashUtils.hash3("test");
        Assert.assertEquals("Incorrect formatted value", ".w.r.j", formattedResult);
    }

    @Test
    public void hash_04() {
        final String formattedResult = HashUtils.hash("test", 1);
        Assert.assertEquals("Incorrect formatted value", ".j", formattedResult);
    }

    @Test
    public void hash_05() {
        final String formattedResult = HashUtils.hash("test", 2);
        Assert.assertEquals("Incorrect formatted value", ".r.j", formattedResult);
    }

    @Test
    public void hash_06() {
        final String formattedResult = HashUtils.hash("test", 3);
        Assert.assertEquals("Incorrect formatted value", ".w.r.j", formattedResult);
    }

    @Test
    public void hash_07() {
        final String formattedResult = HashUtils.domainAndHash("numexample.com");
        Assert.assertEquals("Incorrect formatted value", "numexample.com.c.7.m", formattedResult);
    }
}