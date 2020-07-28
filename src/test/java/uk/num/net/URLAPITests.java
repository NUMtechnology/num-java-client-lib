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

package uk.num.net;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.num.numlib.api.NumAPICallbacks;
import uk.num.numlib.internal.dns.DummyDNSServices;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class URLAPITests {

    private static final String EXPECTED_SUCCESS_MODULE_0 = "{\n" +
            "  \"o\" : {\n" +
            "    \"n\" : \"NUM Example Co\",\n" +
            "    \"c\" : [ {\n" +
            "      \"t\" : 441270123456\n" +
            "    }, {\n" +
            "      \"tw\" : \"numexampletweets\"\n" +
            "    } ]\n" +
            "  }\n" +
            "}";

    private static final String EXPECTED_SUCCESS = "{\n" +
            "  \"organisation\" : {\n" +
            "    \"name\" : \"NUM Example Co\",\n" +
            "    \"contacts\" : [ {\n" +
            "      \"telephone\" : {\n" +
            "        \"value\" : \"441270123456\",\n" +
            "        \"object_type\" : \"method\",\n" +
            "        \"object_display_name\" : \"Telephone\",\n" +
            "        \"description_default\" : \"Call\",\n" +
            "        \"prefix\" : \"tel://\",\n" +
            "        \"method_type\" : \"core\"\n" +
            "      }\n" +
            "    }, {\n" +
            "      \"twitter\" : {\n" +
            "        \"value\" : \"numexampletweets\",\n" +
            "        \"object_type\" : \"method\",\n" +
            "        \"object_display_name\" : \"Twitter\",\n" +
            "        \"description_default\" : \"View Twitter profile\",\n" +
            "        \"prefix\" : \"https://www.twitter.com/\",\n" +
            "        \"method_type\" : \"3p\",\n" +
            "        \"value_prefix\" : \"@\",\n" +
            "        \"controller\" : \"twitter.com\"\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"object_type\" : \"entity\",\n" +
            "    \"object_display_name\" : \"Organisation\",\n" +
            "    \"description_default\" : \"View Organisation\"\n" +
            "  }\n" +
            "}";

    private static final String EXPECTED_SUCCESS_SHOW_PARAMS = "{\n" +
            "  \"C\" : \"gb\",\n" +
            "  \"organisation\" : {\n" +
            "    \"name\" : \"NUM Example Co\",\n" +
            "    \"contacts\" : [ {\n" +
            "      \"telephone\" : {\n" +
            "        \"value\" : \"441270123456\",\n" +
            "        \"object_type\" : \"method\",\n" +
            "        \"object_display_name\" : \"Telephone\",\n" +
            "        \"description_default\" : \"Call\",\n" +
            "        \"prefix\" : \"tel://\",\n" +
            "        \"method_type\" : \"core\"\n" +
            "      }\n" +
            "    }, {\n" +
            "      \"twitter\" : {\n" +
            "        \"value\" : \"numexampletweets\",\n" +
            "        \"object_type\" : \"method\",\n" +
            "        \"object_display_name\" : \"Twitter\",\n" +
            "        \"description_default\" : \"View Twitter profile\",\n" +
            "        \"prefix\" : \"https://www.twitter.com/\",\n" +
            "        \"method_type\" : \"3p\",\n" +
            "        \"value_prefix\" : \"@\",\n" +
            "        \"controller\" : \"twitter.com\"\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"object_type\" : \"entity\",\n" +
            "    \"object_display_name\" : \"Organisation\",\n" +
            "    \"description_default\" : \"View Organisation\"\n" +
            "  }\n" +
            "}";

    @BeforeClass
    public static void beforeClass() {
        NumProtocolSupport.init();
        NUMURLConnection.setDnsServices(new DummyDNSServices());
    }

    @Test
    public void testLoadViaUriSuccess_1() throws IOException {
        final URL url = NumProtocolSupport.toUrl("num://numexample.com:1/?C=gb&L=en");
        final String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        Assert.assertEquals(EXPECTED_SUCCESS, json);
    }

    @Test
    public void testLoadViaUriSuccess_2() throws IOException {
        final URL url = NumProtocolSupport.toUrl("num://joe.bloggs@joebloggs.com:1/?C=gb&L=en");
        final String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        Assert.assertEquals(EXPECTED_SUCCESS, json);
    }

    @Test
    public void testLoadViaUriSuccess_ModuleZero() throws IOException {
        final URL url = NumProtocolSupport.toUrl("num://joe.bloggs@joebloggs.com/?C=gb&L=en");
        final String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        Assert.assertEquals(EXPECTED_SUCCESS_MODULE_0, json);
    }

    @Test(expected = IOException.class)
    public void testLoadViaUriFail() throws IOException {
        final URL url = NumProtocolSupport.toUrl("num://some.user@num.uk:1/personal/data?C=gb&L=en");
        final String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        Assert.assertEquals("test", json);
    }

    @Test
    public void testLoadViaUriSuccessShowParams() throws IOException {
        final URL url = NumProtocolSupport.toUrl("num://numexample.com:1/?C=gb&_L=en");
        final NUMURLConnection connection = new NUMURLConnection(url);
        connection.setRequestProperty(NUMURLConnection.HIDE_PARAMS, "false");
        final String json = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        Assert.assertEquals(EXPECTED_SUCCESS_SHOW_PARAMS, json);

        Assert.assertFalse(connection.isDnsSecSigned());
        Assert.assertEquals(NumAPICallbacks.Location.INDEPENDENT, connection.getLocation());
    }

}
