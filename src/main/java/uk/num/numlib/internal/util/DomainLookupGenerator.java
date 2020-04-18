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
import uk.num.net.NumProtocolSupport;
import uk.num.numlib.internal.ctx.AppContext;

import java.net.MalformedURLException;
import java.net.URL;

public class DomainLookupGenerator extends BaseLookupGenerator implements LookupGenerator {

    public DomainLookupGenerator(final AppContext appContext, final @NonNull String numId) throws
                                                                                           MalformedURLException {
        super(appContext, numId);
        // We can now handle NUM IDs as NUM URLs
        final URL url = NumProtocolSupport.toUrl(numId);
        assert url != null;
        domain = normaliseDomainName(url.getHost());
        branch = url.getPath();
        if (branch != null && ((branch.equals("/") || branch.equals("")))) {
            branch = null;
        } else {
            branch = transformBranch(normalisePath(branch));
        }
    }
}
