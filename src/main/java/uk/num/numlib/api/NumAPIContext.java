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

/**
 * The context state for a given module and NUM ID combination
 *
 * @author tonywalmsley
 */
public interface NumAPIContext {

    /**
     * Get the user variables.
     *
     * @return UserVariable[]
     */
    UserVariable[] getRequiredUserVariables();

    /**
     * Update the required user variables with values obtained from the client.
     *
     * @param userVariables The UserVariable array with the value fields populated.
     */
    void setRequiredUserVariables(final UserVariable[] userVariables);

    /**
     * @return true if the library should query the Populator
     */
    boolean isPopulatorQueryRequired();

    /**
     * @param populatorQueryRequired true if the library should query the Populator
     */
    void setPopulatorQueryRequired(final boolean populatorQueryRequired);

    /**
     * @return the location the record was retrieved from.
     */
    NumAPICallbacks.Location getLocation();

    /**
     * Used internally to set the NUM record retrieval location.
     *
     * @param location the NumAPICallbacks.Location
     */
    void setLocation(final NumAPICallbacks.Location location);

    /**
     * @return true if the record was DNSSEC signed.
     */
    boolean isDnsSecSigned();

}
