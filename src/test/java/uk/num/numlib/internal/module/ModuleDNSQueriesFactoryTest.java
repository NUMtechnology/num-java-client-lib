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

public class ModuleDNSQueriesFactoryTest {

    private static final ModuleFactory moduleFactory = new ModuleFactory();

    @Test(expected = Exception.class)
    public void getInstance1() throws NumInvalidParameterException {
        moduleFactory.getInstance(-1, null);
    }

    @Test(expected = Exception.class)
    public void getInstance2() throws Exception {
        moduleFactory.getInstance(-1, null);
    }

    @Test(expected = Exception.class)
    public void getInstance3() throws Exception {
        moduleFactory.getInstance(-1, null);
    }

    @Test(expected = Exception.class)
    public void getInstance4() throws Exception {
        moduleFactory.getInstance(1, null);
    }

    @Test(expected = Exception.class)
    public void getInstance5() throws Exception {
        moduleFactory.getInstance(1, "");
    }

    @Test(expected = Exception.class)
    public void getInstance6() throws Exception {
        moduleFactory.getInstance(1, "  ");
    }

    @Test
    public void getInstance7() throws Exception {
        final ModuleDNSQueries m1 = moduleFactory.getInstance(1, "numexample.com");
        final ModuleDNSQueries m2 = moduleFactory.getInstance(1, "numexample.com");
        final ModuleDNSQueries m3 = moduleFactory.getInstance(2, "numexample.com");
        final ModuleDNSQueries m4 = moduleFactory.getInstance(1, "example.com");
        Assert.assertNotNull("m1 is invalid.", m1);
        Assert.assertNotNull("m2 is invalid.", m2);
        Assert.assertNotNull("m3 is invalid.", m3);
        Assert.assertNotNull("m4 is invalid.", m4);

        Assert.assertEquals("Bad ModuleDNSQueries field.", 1, m1.getModuleId());
        Assert.assertEquals("Bad ModuleDNSQueries field.", 1, m2.getModuleId());
        Assert.assertEquals("Bad ModuleDNSQueries field.", 2, m3.getModuleId());
        Assert.assertEquals("Bad ModuleDNSQueries field.", 1, m4.getModuleId());

        Assert.assertSame("Should be the same object.", m1, m2);
        Assert.assertNotSame("Should be different objects.", m1, m3);
        Assert.assertNotSame("Should be different objects.", m1, m4);
        Assert.assertNotSame("Should be different objects.", m2, m3);
        Assert.assertNotSame("Should be different objects.", m2, m4);
        Assert.assertNotSame("Should be different objects.", m3, m4);
    }

}