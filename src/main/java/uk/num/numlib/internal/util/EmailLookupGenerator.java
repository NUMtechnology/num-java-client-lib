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
import uk.num.numlib.internal.ctx.AppContext;

public final class EmailLookupGenerator extends BaseLookupGenerator implements LookupGenerator {

    public final String localPart;

    public EmailLookupGenerator(final AppContext appContext, final @NonNull String numId) {
        super(appContext, numId);

        final int atIndex = numId.indexOf('@');
        localPart = numId.substring(0, atIndex);

        final int slashIndex = numId.indexOf("/");
        if (slashIndex > -1) {
            domain = normaliseDomainName(numId.substring(atIndex + 1, slashIndex));
            branch = transformBranch(numId.substring(slashIndex));
        } else {
            domain = normaliseDomainName(numId.substring(atIndex + 1));
        }
    }

    @Override
    public String getIndependentLocation(final String moduleId) {
        final String result = getRootIndependentLocation(moduleId);
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

    @Override
    public String getHostedLocation(final String moduleId) {
        final String result = getRootHostedLocation(moduleId);
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

    @Override
    public String getPopulatorLocation(final String moduleId) {
        return null;// Always
    }

    @Override
    public String getRootIndependentLocation(final String moduleId) {
        return moduleId +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                localPart +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.EMAIL_DOMAIN_SEPARATOR +
                StringConstants.UTILITY_MODULE_PREFIX +
                domain + StringConstants.DOMAIN_SEPARATOR;
    }

    @Override
    public String getRootHostedLocation(final String moduleId) {
        return moduleId +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                localPart +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.EMAIL_DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                domain + HashUtils.hash3(domain) +
                StringConstants.HOSTED_RECORD_SUFFIX +
                StringConstants.DOMAIN_SEPARATOR;
    }

    public String getDistributedIndependentLocation(final String moduleId, final int levels) {
        final String emailLocalPartHash = HashUtils.hash(localPart, levels);
        final String result = moduleId +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                localPart +
                emailLocalPartHash +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.EMAIL_DOMAIN_SEPARATOR +
                StringConstants.UTILITY_MODULE_PREFIX +
                domain + StringConstants.DOMAIN_SEPARATOR;
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

    public String getDistributedHostedLocation(final String moduleId, final int levels) {
        final String emailLocalPartHash = HashUtils.hash(localPart, levels);
        final String result = moduleId +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                localPart +
                emailLocalPartHash +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.EMAIL_DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                domain + HashUtils.hash3(domain) +
                StringConstants.HOSTED_RECORD_SUFFIX +
                StringConstants.DOMAIN_SEPARATOR;
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

}
