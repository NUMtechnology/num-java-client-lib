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

package uk.num.numlib.internal.module;

import lombok.extern.log4j.Log4j2;
import uk.num.numlib.exc.NumInvalidParameterException;
import uk.num.numlib.internal.ctx.AppContext;
import uk.num.numlib.internal.util.SimpleCache;

import java.net.MalformedURLException;

/**
 * A factory for ModuleDNSQuery objects.
 *
 * @author tonywalmsley
 */
@Log4j2
public final class ModuleFactory {

    /**
     * A cache for module/NUM ID combinations.
     */
    private final SimpleCache<String, ModuleDNSQueries> moduleMap = new SimpleCache<>();

    /**
     * Create and initialise a ModuleDNSQueries object or use a cached object.
     *
     * @param appContext   the AppContext
     * @param moduleNumber the module name string, e.g. "1"
     * @param numId        the NUM ID to be queried for a NUM record.
     * @return a ModuleDNSQueries object
     * @throws MalformedURLException on error
     */
    public ModuleDNSQueries getInstance(final AppContext appContext, final int moduleNumber, final String numId) throws
                                                                                                                 MalformedURLException,
                                                                                                                 NumInvalidParameterException {
        ModuleDNSQueries result;

        final String key = moduleNumber + "_" + numId;
        // Critical section - we're reading then updating moduleMap, which is a potential race condition
        synchronized (moduleMap) {
            result = moduleMap.get(key);
            if (result == null) {
                result = new ModuleDNSQueries(moduleNumber, numId);

                // Initialisation as a separate step since its an 'expensive' operation. Allows us to create lots of
                // Modules if necessary but then only initialise the ones we use.
                result.initialise(appContext);

                // Do this last in case there's an exception so we don't store an invalid ModuleDNSQueries object
                moduleMap.put(key, result);
                log.trace("Cached a new set of ModuleDNSQueries.");
            } else {
                log.trace("Using cached ModuleDNSQueries.");
            }
        }
        return result;
    }

}
