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

package uk.num.numlib.api;

import org.junit.Assert;

import java.security.Key;

public class TestCallbackHandlerExpectingSuccessForMultipartRecords implements NumAPICallbacks {
    private String json = null;

    public String getJson() {
        return json;
    }

    @Override
    public void setErrorResult(final String error) {
        Assert.fail("Unexpected error: " + error);
    }

    @Override
    public boolean isSignedDNSSEC() {
        return false;
    }

    @Override
    public void setSignedDNSSEC(boolean signedDNSSEC) {

    }

    @Override
    public Location receivedFrom() {
        return null;
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public Key getKey() {
        return null;
    }

    @Override
    public void setKey(Key key) {

    }

    @Override
    public String getResult() {
        return this.json;
    }

    @Override
    public void setResult(final String json) {
        this.json = json;
        Assert.assertNotNull(json);
        Assert.assertTrue(json.trim()
                .length() > 0);
        Assert.assertTrue("Missing expected value.", json.contains("part_1"));
        Assert.assertTrue("Missing expected value.", json.contains("part_2"));
        Assert.assertTrue("Missing expected value.", json.contains("part_3"));
        Assert.assertTrue("Missing expected value.", json.contains("part_4"));
    }
}
