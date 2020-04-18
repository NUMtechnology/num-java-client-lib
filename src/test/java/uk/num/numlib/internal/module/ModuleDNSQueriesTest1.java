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

import org.junit.Assert;
import org.junit.Test;
import uk.num.numlib.exc.NumInvalidParameterException;
import uk.num.numlib.internal.util.NonBlankString;

public class ModuleDNSQueriesTest1 {

    @Test(expected = Exception.class)
    public void initialise1() {
        new ModuleDNSQueries(null, null);
    }

    @Test(expected = Exception.class)
    public void initialise2() throws Exception {
        new ModuleDNSQueries(NonBlankString.of(""), null);
    }

    @Test(expected = Exception.class)
    public void initialise3() throws Exception {
        new ModuleDNSQueries(NonBlankString.of("  "), null);
    }

    @Test(expected = Exception.class)
    public void initialise4() throws Exception {
        new ModuleDNSQueries(NonBlankString.of("1"), null);
    }

    @Test(expected = Exception.class)
    public void initialise5() throws Exception {
        new ModuleDNSQueries(NonBlankString.of("1"), NonBlankString.of(""));
    }

    @Test(expected = Exception.class)
    public void initialise6() throws Exception {
        new ModuleDNSQueries(NonBlankString.of("1"), NonBlankString.of(" "));
    }

    @Test
    public void getModuleId() throws NumInvalidParameterException {
        final ModuleDNSQueries m = new ModuleDNSQueries(NonBlankString.of("1"), NonBlankString.of("numexample.com"));
        Assert.assertEquals("ModuleDNSQueries Id not set correctly", "1", m.getModuleId());
    }
}