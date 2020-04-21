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

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;

/**
 * Hash function for domain names.
 *
 * @author tonywalmsley
 */
public final class HashUtils {
    /**
     * Generate a SHA1 hash and base36 encode it, then return the first 3 chars separated by '.' and prefixed by '.'
     * The parameter is not checked and the caller must supply a correct value.
     *
     * @param normalisedDomain java.lang.String the normalised domain name.
     * @param levels           the number of hash levels
     * @return java.lang.String the hash value.
     */
    public static String hash(final String normalisedDomain, final int levels) {
        switch (levels) {
            case 1:
                return hash1(normalisedDomain);
            case 2:
                return hash2(normalisedDomain);
            case 3:
                return hash3(normalisedDomain);
            default:
                // Other validation should mean this never happens, but just in case
                throw new RuntimeException("Invalid number of levels in Zone Distribution Record: " + levels);
        }
    }

    public static String domainAndHash(final String domain) {
        return domain + hash3(domain);
    }

    /**
     * Generate a SHA1 hash and base36 encode it, then return the first char separated by '.' and prefixed by '.'
     * The parameter is not checked and the caller must supply a correct value.
     *
     * @param normalisedDomain java.lang.String the normalised domain name.
     * @return java.lang.String the hash value.
     */
    public static String hash1(final String normalisedDomain) {
        final String result = hashString(normalisedDomain);
        return StringConstants.DOMAIN_SEPARATOR + result.charAt(0);
    }

    /**
     * Generate a SHA1 hash and base36 encode it, then return the first 2 chars separated by '.' and prefixed by '.'
     * The parameter is not checked and the caller must supply a correct value.
     *
     * @param normalisedDomain java.lang.String the normalised domain name.
     * @return java.lang.String the hash value.
     */
    public static String hash2(final String normalisedDomain) {
        final String result = hashString(normalisedDomain);
        return String.format(".%s.%s", result.charAt(1), result.charAt(0));
    }

    /**
     * Hash a String and convert it to Base36
     *
     * @param s the String to be hashed
     * @return the Base36 Hash of the String
     */
    private static String hashString(final String s) {
        return new BigInteger(1, DigestUtils.sha1(s)).toString(36);
    }

    /**
     * Generate a SHA1 hash and base36 encode it, then return the first 3 chars separated by '.' and prefixed by '.'
     * The parameter is not checked and the caller must supply a correct value.
     *
     * @param normalisedDomain java.lang.String the normalised domain name.
     * @return java.lang.String the hash value.
     */
    public static String hash3(final String normalisedDomain) {
        final String result = hashString(normalisedDomain);
        return String.format(".%s.%s.%s", result.charAt(2), result.charAt(1), result.charAt(0));
    }

}
