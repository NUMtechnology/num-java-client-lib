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

import uk.num.numlib.util.BaseLookupGenerator;
import uk.num.numlib.util.LookupGenerator;
import uk.num.validators.NumUriValidator;
import uk.num.validators.ValidationResult;

import java.lang.reflect.Method;
import java.net.*;

/**
 * Add NUM Protocol Support
 */
public final class NumProtocolSupport {

    /*
     * Add a NumStreamHandlerFactory for the `num` protocol
     */
    static {
        final NumStreamHandlerFactory factory = new NumStreamHandlerFactory();
        try {
            URL.setURLStreamHandlerFactory(factory);
        } catch (final Throwable e) {
            System.out.println("There is an existing protocol handler registered - checking for embedded Tomcat...");
            if (hasEmbeddedTomcat()) {
                try {
                    final Class<?> aClass = Class.forName("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory");
                    final Method getInstance = aClass.getMethod("getInstance");
                    final Object instance = getInstance.invoke(null);

                    final Method addUserFactory = aClass.getMethod("addUserFactory", URLStreamHandlerFactory.class);
                    addUserFactory.invoke(instance, factory);
                    System.out.println("Added the NUM protocol handler with the TomcatURLStreamHandlerFactory.");
                } catch (final Throwable ex) {
                    System.err.println("There is an existing protocol handler registered - cannot register the NUM protocol handler with the TomcatURLStreamHandlerFactory.");
                    System.err.println("Error: " + ex.getMessage());
                }
            } else {
                System.err.println("There is an existing protocol handler registered - cannot register the NUM protocol handler.");
            }
        }
    }

    private static boolean hasEmbeddedTomcat() {
        try {
            Class.forName("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory");
            return true;
        } catch (final Throwable e) {
            return false;
        }
    }

    /**
     * Create a `num` protocol URL
     *
     * @param numAddress the NUM ID
     * @return a URL
     * @throws MalformedURLException on error
     */
    public static URL toUrl(final String numAddress) throws MalformedURLException {

        final LookupGenerator.NumUriComponents components = BaseLookupGenerator.parseNumUriString(numAddress);

        final ValidationResult validationResult = NumUriValidator.validate(components.getDomain(), components.getModuleNumber(), components.getPath());
        if (!validationResult.isValid()) {
            throw new MalformedURLException(numAddress);
        }

        return new URL(components.toString());
    }

    /**
     * Force the class to be loaded and the static initializer to be executed
     */
    public static void init() {
        // Nothing to do - this just forces the static method to initialise the protocol
    }

    /**
     * A URLStreamHandlerFactory for the NUM protocol
     */
    private static class NumStreamHandlerFactory implements URLStreamHandlerFactory {

        @Override
        public URLStreamHandler createURLStreamHandler(final String protocol) {
            if ("num".equalsIgnoreCase(protocol)) {
                return new URLStreamHandler() {

                    @Override
                    protected URLConnection openConnection(final URL u) {
                        return new NUMURLConnection(u);
                    }
                };
            }
            return null;
        }

    }

}
