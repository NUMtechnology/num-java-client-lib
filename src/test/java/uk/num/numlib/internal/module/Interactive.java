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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * Command line utility to convert numIds to NUM queries
 */
@Log4j2
public class Interactive {

    public static void main(final String[] args) {
        boolean shouldRun;
        do {
            System.out.print("Enter a NUM ID ('q' to quit):> ");
            shouldRun = getAndProcessUserInput();
        } while (shouldRun);
    }

    private static boolean getAndProcessUserInput() {
        return getLine().map(Interactive::showLine)
                .map(Interactive::getDnsQueries)
                .map(Interactive::showQueries)
                .orElse(false);
    }

    private static ModuleDNSQueries getDnsQueries(final String s) {
        try {
            final ModuleDNSQueries m = new ModuleDNSQueries(1, s);
            m.initialise();
            return m;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Optional<String> getLine() {
        try {
            final BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            final String line = buffer.readLine();
            if (!line.equalsIgnoreCase("q")) {
                return Optional.of(line);
            }
        } catch (final Exception e) {
            System.err.println(e.getMessage());
        }
        return Optional.empty();
    }

    private static String showLine(final String s) {
        System.out.println("Input = " + s);
        return s;
    }

    private static boolean showQueries(final ModuleDNSQueries m) {
        System.out.println(m.getIndependentRecordLocation());
        System.out.println(m.getHostedRecordLocation());
        System.out.println(m.getPopulatorLocation());
        return true;
    }

}