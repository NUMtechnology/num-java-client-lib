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

import org.junit.Test;
import uk.num.numlib.exc.NumInvalidParameterException;

import java.net.MalformedURLException;

public class ModuleDNSQueriesValidationTests {

    @Test
    public void testGetPath_1() throws MalformedURLException, NumInvalidParameterException {

        final int moduleNumber = 1;
        final String numIdAndPath1 = "john.smith@example.com/testing";
        final String numIdAndPath2 = "john.smith@testdomain例.com/test1例/test2例/test3例";
        // Create an object to generate the independent and hosted names
        checkDomainNameLengths(moduleNumber, numIdAndPath1);
        System.out.println();
        checkDomainNameLengths(moduleNumber, numIdAndPath2);
    }

    private void checkDomainNameLengths(final int moduleNumber, final String numIdAndPath1) throws
                                                                                            MalformedURLException,
                                                                                            NumInvalidParameterException {
        final ModuleDNSQueries queries = new ModuleDNSQueries(moduleNumber, numIdAndPath1);
        queries.initialise();

        System.out.println(queries.getRootHostedRecordLocation());
        // Get the independent and hosted names
        final String hosted = queries.getHostedRecordLocation();
        final String independent = queries.getIndependentRecordLocation();

        // Check the hosted name
        final int hostedLength = hosted.length() - 1;// To account for the terminating dot.
        System.out.println(hosted + " is " + hostedLength + " bytes long");
        if (hostedLength > 253) {
            System.out.println(hosted + " is too long.");
        }

        // Check the independent name
        final int independentLength = independent.length() - 1;
        System.out.println(independent + " is " + independentLength + " bytes long");
        if (independentLength > 253) {
            System.out.println(independent + " is too long.");
        }
    }

}
