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

import lombok.Getter;
import lombok.Setter;

import java.security.Key;

/**
 * A default no-op implementation of the NumAPICallbacks.
 *
 * @author tonywalmsley
 */
@Getter
@Setter
public class NumAPICallbacksDefaultHandler implements NumAPICallbacks {
    private boolean isSignedDNSSEC;
    private Location location;
    private Key key;
    private String algorithm;
    private String result;

    /**
     * Called when the query result is available.
     * <p>
     * Note: This may be called multiple times if the result if from the NUM scraping service,
     * because more detailed records might become available after the initial quick response.
     * <p>
     * Clients should provide their own implementation of this method to receive those additional responses.
     *
     * @param result the lookup result JSON String
     */
    public void setResult(final String result) {
        this.result = result;
    }

    /**
     * Called if an error occurs while retrieving or processing the NUM recocrd.
     *
     * @param error The error String.
     */
    @Override
    public void setErrorResult(final String error) {
        // OVERRIDE THIS
    }

    /**
     * The type of location the NUM record was received from.
     *
     * @return Location::INDEPENDENT, Location::HOSTED, Location::POPULATED
     */
    @Override
    public Location receivedFrom() {
        return location;
    }
}
