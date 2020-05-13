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
import uk.num.numlib.exc.NumInvalidDNSQueryException;
import uk.num.numlib.exc.NumInvalidParameterException;
import uk.num.numlib.exc.NumInvalidRedirectException;
import uk.num.numlib.internal.ctx.AppContext;

import java.net.MalformedURLException;

@Log4j2
public class ModuleDNSQueriesTest4 {

    private static final AppContext appContext = new AppContext();

    @Test
    public void testGetPath_1() throws NumInvalidDNSQueryException,
                                       MalformedURLException, NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(1, "test.numexample.com/sales/manager");
        queries.initialise(appContext);
        final String path = queries.getHostedRecordPath();
        Assert.assertEquals("/sales/manager", path);
    }

    @Test
    public void testGetPath_2() throws NumInvalidDNSQueryException,
                                       MalformedURLException, NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(1, "test.numexample.com");
        queries.initialise(appContext);
        final String path = queries.getHostedRecordPath();
        Assert.assertEquals("/", path);
    }

    @Test
    public void testGetPath_3() throws NumInvalidDNSQueryException,
                                       MalformedURLException, NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(1, "test.numexample.com/");
        queries.initialise(appContext);
        final String path = queries.getHostedRecordPath();
        Assert.assertEquals("/", path);
    }

    @Test
    public void testRedirectHostedPath_1() throws MalformedURLException, NumInvalidRedirectException,
                                                  NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(1, "test.numexample.com");
        queries.initialise(appContext);
        queries.redirectHostedPath("/test1/test2");
        final String path = queries.getHostedRecordLocation();
        Assert.assertEquals("test2.test1.1._test.numexample.com.y.m.p.num.net.", path);
    }

    @Test
    public void testRedirectHostedPath_2() throws MalformedURLException, NumInvalidRedirectException,
                                                  NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(1, "test.numexample.com/");
        queries.initialise(appContext);
        queries.redirectHostedPath("/test1/test2");
        final String path = queries.getHostedRecordLocation();
        Assert.assertEquals("test2.test1.1._test.numexample.com.y.m.p.num.net.", path);
    }

    @Test
    public void testRedirectHostedPath_3() throws MalformedURLException, NumInvalidRedirectException,
                                                  NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(1, "test.numexample.com/test");
        queries.initialise(appContext);
        queries.redirectHostedPath("/test1/test2");
        final String path = queries.getHostedRecordLocation();
        Assert.assertEquals("test2.test1.1._test.numexample.com.y.m.p.num.net.", path);
    }

}