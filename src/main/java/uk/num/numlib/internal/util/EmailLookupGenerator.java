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
import uk.num.numlib.exc.NumInvalidParameterException;

public final class EmailLookupGenerator extends BaseLookupGenerator implements LookupGenerator {

    public final String localPart;

    public EmailLookupGenerator(final @NonNull String numId) {
        super(numId);

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
    public String getIndependentLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        final String result = getRootIndependentLocation(moduleNumber);
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

    @Override
    public String getHostedLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        final String result = getRootHostedLocation(moduleNumber);
        if (branch == null) {
            return result;
        } else {
            return branch + StringConstants.DOMAIN_SEPARATOR + result;
        }
    }

    @Override
    public String getPopulatorLocation(final int moduleId) {
        return null;// Always
    }

    @Override
    public String getRootIndependentLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        return moduleNumber +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.DOMAIN_NAME_PREFIX +
                localPart +
                StringConstants.DOMAIN_SEPARATOR +
                StringConstants.EMAIL_DOMAIN_SEPARATOR +
                StringConstants.UTILITY_MODULE_PREFIX +
                domain + StringConstants.DOMAIN_SEPARATOR;
    }

    @Override
    public String getRootHostedLocation(final int moduleNumber) throws NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        return moduleNumber +
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

    public String getDistributedIndependentLocation(final int moduleNumber, final int levels) throws
                                                                                              NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        final String emailLocalPartHash = HashUtils.hash(localPart, levels);
        final String result = moduleNumber +
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

    public String getDistributedHostedLocation(final int moduleNumber, final int levels) throws
                                                                                         NumInvalidParameterException {
        if (moduleNumber < 0) {
            throw new NumInvalidParameterException("Module number should be >= 0 but is: " + moduleNumber);
        }
        final String emailLocalPartHash = HashUtils.hash(localPart, levels);
        final String result = moduleNumber +
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
