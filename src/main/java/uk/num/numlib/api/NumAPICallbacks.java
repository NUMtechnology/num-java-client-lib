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

package uk.num.numlib.api;

import java.security.Key;

/**
 * This interface defines the callback contract for the NumAPI.
 *
 * @author tonywalmsley
 */
public interface NumAPICallbacks {
    /**
     * Called if an error occurs while retrieving or processing the NUM recocrd.
     *
     * @param error The error String.
     */
    void setErrorResult(final String error);

    /**
     * Check whether the record was DNSSEC signed.
     *
     * @return true if the record was DNSSEC signed.
     */
    boolean isSignedDNSSEC();

    /**
     * Set a flag to indicate whether the record was DNSSEC signed or not.
     *
     * @param signedDNSSEC true if the record was DNSSEC signed
     */
    void setSignedDNSSEC(final boolean signedDNSSEC);

    /**
     * The type of location the NUM record was received from.
     *
     * @return Location::INDEPENDENT, Location::HOSTED, Location::POPULATED
     */
    Location receivedFrom();

    /**
     * Tell the library user where the record was retrieved from.
     *
     * @param location Location::INDEPENDENT, Location::HOSTED, Location::POPULATED
     */
    void setLocation(final Location location);

    /**
     * The decryption key from the client application.
     *
     * @return a Key
     */
    Key getKey();

    /**
     * The decryption key set by the client application
     *
     * @param key a Key that matches the algorithm returned by the getAlgorithm() method.
     */
    void setKey(final Key key);

    /**
     * Accessor for the JSON result.
     *
     * @return the JSON result
     */
    String getResult();

    /**
     * Called when the result is available from DNS
     *
     * @param json The JSON String containing the result.
     */
    void setResult(final String json);


    /**
     * The type of location the NUM record was received from
     */
    enum Location {INDEPENDENT, HOSTED, POPULATOR, STOP}
}
