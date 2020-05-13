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
import uk.num.numlib.internal.ctx.AppContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command line utility to convert numIds to NUM queries
 */
@Log4j2
public class Interactive {

    private static final AppContext appContext = new AppContext();

    public static void main(String[] args) {
        do {
            try {
                System.out.print("Enter a NUM ID ('q' to quit): ");

                final String s = getLine();
                checkExit(s);
                System.out.println("Input = " + s);
                final ModuleDNSQueries m = new ModuleDNSQueries(1, s);
                m.initialise(appContext);

                System.out.println(m.getIndependentRecordLocation());
                System.out.println(m.getHostedRecordLocation());
                System.out.println(m.getPopulatorLocation());
            } catch (final Exception e) {
                // Ignore - its logged internally
            }
        } while (true);
    }

    private static void checkExit(final String s) {
        if (s.equalsIgnoreCase("q")) {
            System.exit(0);
        }
    }

    private static String getLine() throws IOException {
        final BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        return buffer.readLine();
    }

}