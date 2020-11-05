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

import lombok.NonNull;
import org.xbill.DNS.DohResolver;

/**
 * Hides the underlying DoH resolver library
 */
public class NumDohResolver {

    final DohResolver resolver; // Package scope

    public NumDohResolver(@NonNull final String dohServiceUrl) {
        resolver = new DohResolver(dohServiceUrl);
    }

}
