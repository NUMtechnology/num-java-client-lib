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
import org.junit.Assert;
import org.junit.Test;
import uk.num.numlib.exc.NumInvalidParameterException;
import uk.num.numlib.internal.ctx.AppContext;
import uk.num.numlib.internal.util.NonBlankString;

@Log4j2
public class ModuleDNSQueriesTest3 {
    private static final AppContext appContext = new AppContext();

    @Test
    public void testEmailDistribution_1() throws NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(NonBlankString.of("1"), NonBlankString.of("john.smith@numexample.com"));
        queries.setEmailRecordDistributionLevels(appContext, 1);
        Assert.assertEquals("1._john.smith.3.e._num.numexample.com.", queries.getIndependentRecordLocation());
        Assert.assertEquals("1._john.smith.3.e._numexample.com.c.7.m.num.net.", queries.getHostedRecordLocation());
    }

    @Test
    public void testEmailDistribution_2() throws NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(NonBlankString.of("1"), NonBlankString.of("john.smith@numexample.com"));
        queries.setEmailRecordDistributionLevels(appContext, 2);
        Assert.assertEquals("1._john.smith.6.3.e._num.numexample.com.", queries.getIndependentRecordLocation());
        Assert.assertEquals("1._john.smith.6.3.e._numexample.com.c.7.m.num.net.", queries.getHostedRecordLocation());
    }

    @Test
    public void testEmailDistribution_3() throws NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(NonBlankString.of("1"), NonBlankString.of("john.smith@numexample.com"));
        queries.setEmailRecordDistributionLevels(appContext, 3);
        Assert.assertEquals("1._john.smith.d.6.3.e._num.numexample.com.", queries.getIndependentRecordLocation());
        Assert.assertEquals("1._john.smith.d.6.3.e._numexample.com.c.7.m.num.net.", queries.getHostedRecordLocation());
    }
}