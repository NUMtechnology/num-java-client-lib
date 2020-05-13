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

import org.junit.Assert;
import org.junit.Test;
import uk.num.numlib.internal.ctx.AppContext;

public class PunycodeTests {

    private static final AppContext appContext = new AppContext();

    /**
     * Convert unicode to text for the independent record
     */
    @Test
    public void test_01() throws Exception {
        final LookupGenerator utils = new DomainLookupGenerator(appContext, "num例.com");

        final String result = utils.getIndependentLocation(1);
        Assert.assertNotNull("Bad result.", result);
        Assert.assertEquals("Incorrect result.", "1._num.xn--num-xc0e.com.", result);
    }

    /**
     * Convert unicode to text for the hosted record
     */
    @Test
    public void test_02() throws Exception {
        final LookupGenerator utils = new DomainLookupGenerator(appContext, "num例.com");

        final String result = utils.getHostedLocation(1);
        Assert.assertNotNull("Bad result.", result);
        Assert.assertEquals("Incorrect result.", "1._xn--num-xc0e.com.n.f.5.num.net.", result);
    }

    /**
     * Convert unicode to text for the pre-populated record
     */
    @Test
    public void test_04() throws Exception {
        final LookupGenerator utils = new DomainLookupGenerator(appContext, "num例.com");

        final String result = utils.getPopulatorLocation(1);
        Assert.assertNotNull("Bad result.", result);
        Assert.assertEquals("Incorrect result.", "1._xn--num-xc0e.com.populator.num.net.", result);
    }

    /**
     * Convert unicode to text for the independent record using an email branch lookup
     */
    @Test
    public void test_05() {
        final LookupGenerator utils = new EmailLookupGenerator(appContext, "xi@num例.com");

        final String result = utils.getIndependentLocation(1);
        Assert.assertNotNull("Bad result.", result);
        Assert.assertEquals("Incorrect result.", "1._xi.e._num.xn--num-xc0e.com.", result);
    }

    /**
     * Convert unicode to text for the hosted record an email branch lookup
     */
    @Test
    public void test_06() {
        final LookupGenerator utils = new EmailLookupGenerator(appContext, "xi@num例.com");

        final String result = utils.getHostedLocation(1);
        Assert.assertNotNull("Bad result.", result);
        Assert.assertEquals("Incorrect result.", "1._xi.e._xn--num-xc0e.com.n.f.5.num.net.", result);
    }

    /**
     * Convert unicode to text for the independent record using a URL branch lookup
     */
    @Test
    public void test_09() throws Exception {
        final LookupGenerator utils = new URLLookupGenerator(appContext, "http://www.num例.com/sales/index.html");

        final String result = utils.getIndependentLocation(1);
        Assert.assertNotNull("Bad result.", result);
        Assert.assertEquals("Incorrect result.", "sales.1._num.xn--num-xc0e.com.", result);

    }

    /**
     * Convert unicode to text for the hosted record a URL branch lookup
     */
    @Test
    public void test_10() throws Exception {
        final LookupGenerator utils = new URLLookupGenerator(appContext, "http://www.num例.com/sales/index.html");

        final String result = utils.getHostedLocation(1);
        Assert.assertNotNull("Bad result.", result);
        Assert.assertEquals("Incorrect result.", "sales.1._xn--num-xc0e.com.n.f.5.num.net.", result);
    }

}
