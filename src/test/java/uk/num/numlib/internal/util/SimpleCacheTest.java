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

public class SimpleCacheTest {

    /**
     * This simple test covers all aspects of the SimpleCache.
     */
    @Test
    public void test_1() {
        // Create the cache with a short timeout
        final SimpleCache<String, String> cache = new SimpleCache<>();
        cache.setTimeToLive(10);

        // Put something in the cache.
        final String key1 = "key1";
        final String value1 = "value1";
        cache.put(key1, value1);

        // Make sure the item is returned correctly
        final String result1 = cache.get(key1);
        Assert.assertSame(value1, result1);

        // Make sure the cached item expires.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check that the cached item is not available because it has expired.
        final String result2 = cache.get(key1);
        Assert.assertNull(result2);
    }
}