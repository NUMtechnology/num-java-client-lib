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

import java.util.HashMap;
import java.util.Map;

/**
 * A very basic cache with a timeout mechanism.
 *
 * @param <K> The class that the keys will be instances of.
 * @param <V> The class that the values will be instances of.
 * @author tonywalmsley
 */
public final class SimpleCache<K, V> {

    /**
     * The default cache timeout.
     */
    private static final int TEN_MINUTES = 1000 * 60 * 10;

    /**
     * The actual cache is a HashMap of K,CacheEntry pairs.
     */
    private final Map<K, CacheEntry<V>> cache = new HashMap<>();

    /**
     * The overridable cache timeout - defaults to 10 minutes.
     */
    private int millis = TEN_MINUTES;

    /**
     * Default Constructor
     */
    public SimpleCache() {
    }

    /**
     * Override the default cache timeour. Can be set at any time but only affects items cached after the value is set.
     *
     * @param millis The number of milliseconds until the cached item should be considered stale.
     */
    public void setTimeToLive(int millis) {
        this.millis = millis;
    }

    /**
     * Add an item to the cache.
     *
     * @param key   The key of class K
     * @param value The value of class V
     */
    public void put(K key, V value) {
        final CacheEntry<V> entry = new CacheEntry<>(value, millis + System.currentTimeMillis());
        cache.put(key, entry);
    }

    /**
     * Get an item from the cache.
     *
     * @param key The key of class K
     * @return null if the item is not present or has expired, otherwise the value of class V
     */
    public V get(K key) {
        final CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.expiry < System.currentTimeMillis()) {

            // Remove the expired item
            if (entry != null) {
                cache.remove(key);
            }
            return null;
        }
        return entry.value;
    }

    /**
     * The CacheEntry holds the cached value and the expiry time of the item.
     *
     * @param <V> The class of the values to be cached.
     */
    private static class CacheEntry<V> {
        private final V value;
        private final long expiry;

        /**
         * Constructor.
         *
         * @param value  The value to be cached.
         * @param expiry The expiry time of the cached item.
         */
        CacheEntry(V value, long expiry) {
            this.value = value;
            this.expiry = expiry;
        }
    }
}
