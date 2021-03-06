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

package uk.num.numlib.internal.modl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * A response from the populator service.
 *
 * @author tonywalmsley
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public final class PopulatorResponse {

    public static final int VALID_TXT_RECORD_CODE = 999;

    /**
     * Error object
     */
    @JsonProperty("@error")
    private PopulatorResponseRecord error;

    /**
     * Status object
     */
    @JsonProperty("@status")
    private PopulatorResponseRecord status;

    /**
     * The NUM record returned by the DNS responder if it has one less than 2 minutes old.
     */
    private String numRecord;


    public boolean isValid() {
        if (status == null && error == null) {
            return true;
        }
        if (status != null) {
            final int code = status.getCode();
            if (code < 1 || (code > 3 && code != VALID_TXT_RECORD_CODE)) {
                return true;
            }
        }
        if (error != null) {
            final int code = error.getCode();
            return code < 100 || code > 104;
        }
        return false;
    }

}
