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

import lombok.NonNull;
import lombok.Value;
import uk.num.numlib.exc.NumInvalidParameterException;

public interface LookupGenerator {

    String getIndependentLocation(final int moduleId) throws NumInvalidParameterException;

    String getHostedLocation(final int moduleId) throws NumInvalidParameterException;

    boolean isDomainRoot();

    String getPopulatorLocation(final int moduleId) throws NumInvalidParameterException;

    String getRootIndependentLocation(final int moduleId) throws NumInvalidParameterException;

    String getRootHostedLocation(final int moduleId) throws NumInvalidParameterException;

    String getRootIndependentLocationNoModuleNumber(final TrailingDot addTrailingDot);

    String getRootHostedLocationNoModuleNumber(final TrailingDot addTrailingDot);

    enum TrailingDot {
        ADD_TRAILING_DOT, NO_TRAILING_DOT
    }

    @Value
    class NumUriComponents {

        @NonNull
        String domain;

        int moduleNumber;

        @NonNull
        String path;

        String params;// E.g. "a=b&c=d"

        @Override
        public String toString() {
            if (params == null) {
                return "num://" + domain + ":" + moduleNumber + path;
            } else {
                return "num://" + domain + ":" + moduleNumber + path + "?" + params;
            }
        }

    }

}
