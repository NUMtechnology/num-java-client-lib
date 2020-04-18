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

import junit.framework.TestCase;
import org.junit.Test;
import uk.num.numlib.exc.RelativePathException;

public class UrlRelativePathResolverTest {
    @Test
    public void testResolveNull() throws RelativePathException {
        TestCase.assertEquals("/", UrlRelativePathResolver.resolve(null, null));
    }

    @Test
    public void testResolveEmptyString() throws RelativePathException {
        TestCase.assertEquals("/", UrlRelativePathResolver.resolve("", ""));
    }

    @Test
    public void testResolveSuccess_1() throws RelativePathException {
        TestCase.assertEquals("/level1", UrlRelativePathResolver.resolve("", "level1"));
    }

    @Test
    public void testResolveSuccess_2() throws RelativePathException {
        TestCase.assertEquals("/level1", UrlRelativePathResolver.resolve("/", "level1"));
    }

    @Test
    public void testResolveSuccess_3_1() throws RelativePathException {
        TestCase.assertEquals("/level1/level2", UrlRelativePathResolver.resolve("/level1", "level2"));
    }

    @Test
    public void testResolveSuccess_3_2() throws RelativePathException {
        TestCase.assertEquals("/level1/level2", UrlRelativePathResolver.resolve("level1", "level2"));
    }

    @Test
    public void testResolveSuccess_4() throws RelativePathException {
        TestCase.assertEquals("/level1/level2/level3", UrlRelativePathResolver.resolve("/level1/level2/", "level3"));
    }

    @Test
    public void testResolveSuccess_5() throws RelativePathException {
        TestCase.assertEquals("/level1", UrlRelativePathResolver.resolve("/level1/level2/", ".."));
    }

    @Test
    public void testResolveSuccess_6() throws RelativePathException {
        TestCase.assertEquals("/", UrlRelativePathResolver.resolve("/level1/level2/", "../.."));
    }

    @Test
    public void testResolveSuccess_7() throws RelativePathException {
        TestCase.assertEquals("/", UrlRelativePathResolver.resolve("/level1/level2/", "/"));
    }

    @Test
    public void testResolveSuccess_8() throws RelativePathException {
        TestCase.assertEquals("/level1/lev2again", UrlRelativePathResolver.resolve("/level1/level2/", "../lev2/../lev3/../lev2again/"));
    }

    @Test
    public void testResolveSuccess_9() throws RelativePathException {
        TestCase.assertEquals("/lev1again", UrlRelativePathResolver.resolve("/level1/level2/", "../../lev2/../lev3/../lev2again/../lev1again"));
    }

    @Test
    public void testResolveSuccess_10() throws RelativePathException {
        TestCase.assertEquals("/", UrlRelativePathResolver.resolve("/level1/level2", "level3/../../.."));
    }

    @Test
    public void testResolveSuccess_11() throws RelativePathException {
        TestCase.assertEquals("/", UrlRelativePathResolver.resolve("/level1/level2", "./level3/../../.."));
    }

    @Test
    public void testResolveSuccess_12() throws RelativePathException {
        TestCase.assertEquals("/", UrlRelativePathResolver.resolve("/level1/level2", "./level3/.././../.."));
    }

    @Test
    public void testResolveSuccess_13() throws RelativePathException {
        TestCase.assertEquals("/", UrlRelativePathResolver.resolve("/level1/level2", "./level3/..//../.."));
    }

    @Test
    public void testResolveSuccess_14() throws RelativePathException {
        TestCase.assertEquals("/level1/level2", UrlRelativePathResolver.resolve("/level1/level2", "./././././/////"));
    }

    @Test
    public void testResolveSuccess_15() throws RelativePathException {
        TestCase.assertEquals("/level1/level2", UrlRelativePathResolver.resolve("//level1/level2", "./././././/////"));
    }

    @Test
    public void testResolveSuccess_16() throws RelativePathException {
        TestCase.assertEquals("/level1/level2", UrlRelativePathResolver.resolve("/./level1/level2", "./././././/////"));
    }

    @Test
    public void testResolveSuccess_17() throws RelativePathException {
        TestCase.assertEquals("/level1/level2", UrlRelativePathResolver.resolve("/./level1/level2", ""));
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_1() throws RelativePathException {
        UrlRelativePathResolver.resolve("", "..");
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_2() throws RelativePathException {
        UrlRelativePathResolver.resolve("", "/..");
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_3() throws RelativePathException {
        UrlRelativePathResolver.resolve("", "../..");
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_4() throws RelativePathException {
        UrlRelativePathResolver.resolve("/", "../..");
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_5() throws RelativePathException {
        UrlRelativePathResolver.resolve("/level1", "../..");
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_6() throws RelativePathException {
        UrlRelativePathResolver.resolve("level1", "../..");
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_7() throws RelativePathException {
        UrlRelativePathResolver.resolve("/level1/level2", "level3/../../../level1again/../../oops");
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_8() throws RelativePathException {
        UrlRelativePathResolver.resolve("/..", "");
    }

    @Test(expected = RelativePathException.class)
    public void testResolveFail_9() throws RelativePathException {
        UrlRelativePathResolver.resolve("..", "");
    }

}