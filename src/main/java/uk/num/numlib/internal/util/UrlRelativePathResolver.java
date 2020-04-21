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

import org.apache.commons.lang3.StringUtils;
import uk.num.numlib.exc.RelativePathException;

import java.util.Stack;

/**
 * Convert a path of the form `/a/b/c/../../d/../e/../..` etc to a resolved path.
 */
public final class UrlRelativePathResolver {

    /**
     * Convert a path of the form `/a/b/c/../../d/../e/../..` etc to a resolved path.
     *
     * @param base     the base path that we're moving relative to
     * @param redirect the relative path - can include `../` sections
     * @return the resolved path
     * @throws RelativePathException when the redirect is beyond the root of the base path
     */
    public static String resolve(final String base, final String redirect) throws RelativePathException {
        final String SEP = StringConstants.URL_PATH_SEPARATOR;// Makes the code easier to read

        // Normalise the base and redirect paths
        String basePath = (base == null) ? "" : StringUtils.removeEnd(base, SEP);

        String redirectPath = (redirect == null) ? "" : redirect;

        if (redirectPath.startsWith(SEP)) {
            basePath = redirect;
            redirectPath = "";
        }

        // Make a full path including the `../` sections
        final String path = (redirectPath.length() > 0) ? basePath + SEP + redirectPath : basePath;

        // Use a stack to determine the resolved path
        final String[] parts = path.split(SEP);
        final Stack<String> pathStack = new Stack<>();

        // Add each non-empty part to the stack, or pop an item, or throw if the stack is empty when popping
        for (final String part : parts) {
            if (part.equals(StringConstants.URL_PATH_UP)) {
                if (pathStack.size() == 0) {
                    throw new RelativePathException("Cannot redirect beyond root");
                }
                pathStack.pop();
            } else if (!part.equals(StringConstants.URL_PATH_HERE)) {
                if (part.length() > 0) {
                    pathStack.push(part);
                }
            }
        }

        // Rebuild the path to a resolved path
        return SEP + String.join(SEP, pathStack);
    }
}
