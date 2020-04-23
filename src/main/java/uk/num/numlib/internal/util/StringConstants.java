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

/**
 * Constants defined by the NUM Protocol Specification
 *
 * @author tonywalmsley
 */
public final class StringConstants {
    public static final String DOMAIN_SEPARATOR = ".";
    public static final String URL_PATH_SEPARATOR = "/";
    public static final String URL_PATH_UP = "..";
    public static final String URL_PATH_HERE = ".";

    /**
     * The top level zone to use.
     */
    public static final String TOP_LEVEL_ZONE = "num.net";

    /**
     * The top level zone to use.
     */
    public static final String POPULATOR_TOP_LEVEL_ZONE = "populator.num.net";

    /**
     * Used to indicate a requirement for distributing email records across DNS zone files.
     *
     * @return a String
     */
    public String ZONE_DISTRIBUTION_RECORD_PREFIX() {
        return "_n=1;zd=";
    }

    /**
     * Accessor
     *
     * @return the DOMAIN_NAME_PREFIX value.
     */
    public String DOMAIN_NAME_PREFIX() {
        return "_";
    }

    /**
     * /**
     * Accessor
     *
     * @return the DOMAIN_NAME_PREFIX value.
     */
    public String EMAIL_DOMAIN_SEPARATOR() {
        return "e";
    }

    /**
     * Accessor
     *
     * @return the UTILITY_MODULE_PREFIX value.
     */
    public String UTILITY_MODULE_PREFIX() {
        return "._num.";
    }

    /**
     * Accessor
     *
     * @return the HOSTED_RECORD_SUFFIX value.
     */
    public String HOSTED_RECORD_SUFFIX() {
        return StringConstants.DOMAIN_SEPARATOR + TOP_LEVEL_ZONE;
    }

    /**
     * Accessor
     *
     * @return the POPULATOR_SERVICE_SUFFIX value.
     */
    public String POPULATOR_SERVICE_SUFFIX() {
        return StringConstants.DOMAIN_SEPARATOR + POPULATOR_TOP_LEVEL_ZONE;
    }

}
