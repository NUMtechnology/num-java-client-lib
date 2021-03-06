/*
 * Copyright 2020 NUM Technology Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.num.numlib.api;

import static uk.num.numlib.api.NumAPICallbacks.Location.HOSTED;
import static uk.num.numlib.api.NumAPICallbacks.Location.INDEPENDENT;
import static uk.num.numlib.api.NumAPICallbacks.Location.POPULATOR;
import static uk.num.numlib.api.NumAPICallbacks.Location.STOP;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import uk.num.net.NumProtocolSupport;
import uk.num.numlib.dns.DNSServices;
import uk.num.numlib.dns.DNSServicesDefaultImpl;
import uk.num.numlib.exc.NumBadRecordException;
import uk.num.numlib.exc.NumInvalidDNSHostException;
import uk.num.numlib.exc.NumInvalidDNSQueryException;
import uk.num.numlib.exc.NumInvalidParameterException;
import uk.num.numlib.exc.NumInvalidPopulatorResponseCodeException;
import uk.num.numlib.exc.NumInvalidRedirectException;
import uk.num.numlib.exc.NumMaximumRedirectsExceededException;
import uk.num.numlib.exc.NumNoRecordAvailableException;
import uk.num.numlib.exc.NumPopulatorErrorException;
import uk.num.numlib.exc.RrSetHeaderFormatException;
import uk.num.numlib.exc.RrSetIncompleteException;
import uk.num.numlib.exc.RrSetNoHeadersException;
import uk.num.numlib.internal.ctx.NumAPIContextBase;
import uk.num.numlib.internal.modl.ModlServices;
import uk.num.numlib.internal.modl.NumLookupRedirect;
import uk.num.numlib.internal.modl.PopulatorResponse;
import uk.num.numlib.internal.module.ModuleDNSQueries;
import uk.num.numlib.internal.module.ModuleFactory;
import uk.num.numlib.internal.util.LegacyEscapeReplacer;
import uk.num.numlib.internal.util.PopulatorRetryConfig;
import uk.num.numlib.util.StringConstants;

/**
 * This is the main class for using the num-client-library. Use the default constructor to use the DNS servers configured on your local machine, or override these by supplying a
 * specific DNS host domain name using the alternative constructor.
 *
 * @author tonywalmsley
 */
@Log4j2
public final class NumAPIImpl implements NumAPI {

  public static final String MATCH_NUM_RECORDS = "(@n=[0-9]+;.*)|(^\\d+\\|.*)|(\\d+/\\d+\\|@n=\\d+;.*)";

  private final ModuleFactory moduleFactory = new ModuleFactory();

  private final LegacyEscapeReplacer legacyEscapeReplacer;

  /**
   * Services for running the MODL Interpreter
   */
  private final ModlServices modlServices;

  /**
   * Supports running DNS queries asynchronously.
   */
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  /**
   * Services for accessing DNS and processing the resulting records.
   */
  private DNSServices dnsServices;

  private String modulesLocation = "https://modules.numprotocol.com/";

  /**
   * Default constructor to initialise the default DNS services and MODL services.
   */
  public NumAPIImpl() {
    log.info("enter - NumAPI()");
    dnsServices = new DNSServicesDefaultImpl();
    modlServices = new ModlServices();
    log.info("NumAPI object created.");
    legacyEscapeReplacer = new LegacyEscapeReplacer();
  }

  /**
   * Alternative constructor used to provide a NumDohResolver.
   *
   * @param resolver The NumDohResolver, e.g. `new NumDohResolver("https://dns.google/dns-query")`.
   */
  public NumAPIImpl(final NumDohResolver resolver) {
    this();
    dnsServices = new DNSServicesDefaultImpl(resolver.resolver);

    log.info("enter - NumAPI(resolver)");
    Lookup.setDefaultResolver(resolver.resolver);
    log.info("NumAPI object created.");
  }

  /**
   * Alternative constructor used to override the default DNS hosts. Unit tests rely on this constructor.
   *
   * @param dnsHost     The DNS host to override the defaults configured for the local machine.
   * @param dnsServices used to inject dummy DNS services for testing.
   * @throws NumInvalidDNSHostException on error
   */
  public NumAPIImpl(final DNSServices dnsServices, final String dnsHost) throws NumInvalidDNSHostException {
    this();
    this.dnsServices = dnsServices;

    log.info("enter - NumAPI({})", dnsHost);
    try {
      if (!StringUtils.isEmpty(dnsHost)) {
        final ExtendedResolver resolver = new ExtendedResolver(new String[] {dnsHost});
        Lookup.setDefaultResolver(resolver);
      }
    } catch (UnknownHostException e) {
      log.error("UnknownHostException", e);
      throw new NumInvalidDNSHostException("Invalid DNS host.", e);
    }
    log.info("NumAPI object created.");
  }

  /**
   * Support multiple DNS hosts.
   *
   * @param dnsHosts The DNS host String arraymto override the defaults configured for the local machine.
   * @throws NumInvalidDNSHostException   on error
   * @throws NumInvalidParameterException on error
   */
  public NumAPIImpl(final String[] dnsHosts) throws NumInvalidDNSHostException, NumInvalidParameterException {
    this();
    log.info("enter - NumAPI({})", Arrays.toString(dnsHosts));
    if (dnsHosts == null || dnsHosts.length == 0) {
      log.error("No DNS hosts supplied.");
      throw new NumInvalidParameterException("No DNS hosts supplied.");
    }
    try {
      final ExtendedResolver resolver = new ExtendedResolver(dnsHosts);
      Lookup.setDefaultResolver(resolver);
      this.dnsServices = new DNSServicesDefaultImpl(resolver);
    } catch (UnknownHostException e) {
      log.error("UnknownHostException", e);
      throw new NumInvalidDNSHostException("Invalid DNS host.", e);
    }
    log.info("NumAPI object created.");
  }

  /**
   * Alternative constructor used to override the default DNS host and port.
   *
   * @param dnsHost The DNS host to override the defaults configured for the local machine.
   * @param port    The port to use on the DNS host.
   * @throws NumInvalidDNSHostException on error
   */
  public NumAPIImpl(final String dnsHost, final int port) throws NumInvalidDNSHostException {
    this();
    log.info("NumAPI({}, {})", dnsHost, port);
    try {
      final ExtendedResolver resolver = new ExtendedResolver(new String[] {dnsHost});
      Lookup.setDefaultResolver(resolver);
    } catch (UnknownHostException e) {
      log.error("UnknownHostException", e);
      throw new NumInvalidDNSHostException("Invalid DNS host.", e);
    }
    log.info("NumAPI object created.");
  }

  /**
   * Tell dnsjava to use TCP and not UDP.
   *
   * @param flag true to use TCP only.
   */
  @Override
  public void setTCPOnly(final boolean flag) {
    log.info("Use TCP only : {}", flag);
    Lookup.getDefaultResolver().setTCP(flag);
  }

  /**
   * Initialise a new NumAPIContextBase object for a specific module/NUM ID combination. The returned context object can be used to obtain the list of required user variables that
   * must be set before moving on to retrieveNumRecord().
   *
   * @param numAddress    E.g. `domain:module/path` or `user@domain:module/path` module is optional and defaults to 1
   * @param timeoutMillis the timeout in milliseconds to wait for responses from DNS.
   * @return a new NumAPIContextBase object.
   * @throws MalformedURLException on error
   */
  @Override
  public NumAPIContext begin(@NonNull final String numAddress, final int timeoutMillis) throws MalformedURLException, NumInvalidParameterException {
    final URL url = NumProtocolSupport.toUrl(numAddress);
    return begin(url, timeoutMillis);
  }

  /**
   * Initialise a new NumAPIContextBase object for a specific module/NUM ID combination. The returned context object can be used to obtain the list of required user variables that
   * must be set before moving on to retrieveNumRecord().
   *
   * @param numAddress    E.g. `domain:module/path` or `user@domain:module/path` module is optional and defaults to 1
   * @param timeoutMillis the timeout in milliseconds to wait for responses from DNS.
   * @return a new NumAPIContextBase object.
   * @throws MalformedURLException on error
   */
  @Override
  public NumAPIContext begin(@NonNull final URL numAddress, final int timeoutMillis) throws MalformedURLException, NumInvalidParameterException {
    if (!"num".equalsIgnoreCase(numAddress.getProtocol())) {
      throw new MalformedURLException("The URL protocol must be 'num'");
    }
    // Build the numId without the module number since module is separated out internally.
    final String path = (StringUtils.isBlank(numAddress.getPath())) ? "/" : numAddress.getPath();
    final String numId = (numAddress.getUserInfo() != null) ? numAddress.getUserInfo() + "@" + numAddress.getHost() + path : numAddress.getHost() + path;

    // Default to module (port) 0
    final int moduleNumber = (numAddress.getPort() > -1) ? numAddress.getPort() : 0;

    log.info("enter - begin({}, {}, {})", moduleNumber, numId, timeoutMillis);
    assert timeoutMillis > 0;

    // Create the context object and the validated ModuleDNSQueries object.
    final NumAPIContextBase ctx = new NumAPIContextBase();

    final ModuleDNSQueries moduleDNSQueries = moduleFactory.getInstance(moduleNumber, numId);
    ctx.setModuleDNSQueries(moduleDNSQueries);

    log.info("exit - begin()");
    return ctx;
  }

  /**
   * This method uses the module context and the supplied Required User Variable values to obtain a fully expanded JSON object from DNS. The supplied handler will be notified when
   * the results are available or an error occurs.
   *
   * @param ctx           The context object returned by the begin() method.
   * @param handler       a handler object to receive the JSON results or processing errors.
   * @param timeoutMillis the maximum duration of each DNS request, the total wait time could be up to 4 times this value.
   * @return A Future object
   */
  @Override
  public Future<String> retrieveNumRecord(final NumAPIContext ctx, final NumAPICallbacks handler, final int timeoutMillis) {
    return retrieveNumRecord(ctx, handler, timeoutMillis, true);
  }

  /**
   * This method uses the module context and the supplied Required User Variable values to obtain a fully expanded JSON object from DNS. The supplied handler will be notified when
   * the results are available or an error occurs.
   *
   * @param ctx           The context object returned by the begin() method.
   * @param handler       a handler object to receive the JSON results or processing errors.
   * @param timeoutMillis the maximum duration of each DNS request, the total wait time could be up to 4 times this value.
   * @param interpret     true if the result should be JSON, false for MODL
   * @return A Future object
   */
  @Override
  public Future<String> retrieveNumRecord(final NumAPIContext ctx, final NumAPICallbacks handler, final int timeoutMillis, final boolean interpret) {
    log.info("retrieveNumRecord()");
    assert ctx != null;
    assert handler != null;

    // 1. Re-interpret the module config now that we have the user variables.
    // 2. Get the NUM record from DNS (either independent, hosted, or populator)
    // 2. Prepend the RCF to the NUM record from DNS as a *LOAD entry (i.e. the module URL)
    // 3. Run the resulting record through the Java MODL Interpreter and make the results available
    // to the client via the handler.

    // Do the rest of the operation asynchronously.
    // This submits a Callable object, so exceptions should be reported to the user when they call
    // the get() method on the Future object.
    log.info("Submitting background query.");
    final Future<String> future = executor.submit(() -> {
      final String result = numLookup(ctx, handler, timeoutMillis, interpret);
      if (result == null) {
        log.info("Unable to retrieve a NUM record.");
        handler.setLocation(null);
        ctx.setLocation(null);
        return null;
      } else {
        handler.setResult(result);
        handler.setLocation(ctx.getLocation());
        handler.setSignedDNSSEC(ctx.isDnsSecSigned());
        // handler.setSignedDNSSEC(ctx.isDnsSecSigned());
        return result;
      }
    });
    log.info("Background query running.");
    return future;
  }

  /**
   * Main lookup method with fairly complex state behaviour to handle the various lookup locations and retry scenarios.
   *
   * @param ctx           the NumAPIContext
   * @param handler       the NumAPICallbacks
   * @param timeoutMillis the timeoutMillis
   * @param interpret     true if the result should be JSON, false for MODL
   * @return a NUM record String
   * @throws NumBadRecordException                    on error
   * @throws NumInvalidRedirectException              on error
   * @throws NumInvalidDNSQueryException              on error
   * @throws NumMaximumRedirectsExceededException     on error
   * @throws NumNoRecordAvailableException            on error
   * @throws NumPopulatorErrorException               on error
   * @throws NumInvalidPopulatorResponseCodeException on error
   * @throws RrSetIncompleteException                 on error
   * @throws RrSetHeaderFormatException               on error
   * @throws RrSetNoHeadersException                  on error
   */
  private String numLookup(final NumAPIContext ctx, final NumAPICallbacks handler, final int timeoutMillis, final boolean interpret)
      throws NumBadRecordException, NumInvalidRedirectException, NumInvalidDNSQueryException, NumMaximumRedirectsExceededException, NumNoRecordAvailableException,
      NumPopulatorErrorException, NumInvalidPopulatorResponseCodeException, RrSetHeaderFormatException, RrSetIncompleteException, RrSetNoHeadersException {
    final NumAPIContextBase context = (NumAPIContextBase) ctx;
    context.setLocation(INDEPENDENT);
    log.info("Trying the INDEPENDENT location.");
    do {
      try {
        // Attempt to get the record from DNS
        String numRecord = getNumRecord(timeoutMillis, context);

        // Ignore catch-all TXT records that aren't NUM records
        if (numRecord != null && !numRecord.matches(MATCH_NUM_RECORDS)) {
          numRecord = null;
        }

        // Handle a possible email zone distribution record.
        final Optional<String> maybeZDR = Optional.ofNullable(numRecord).filter(isZoneDistributionRecord());

        if (maybeZDR.isPresent()) {
          final Optional<String> maybeNewLookupResult = maybeZDR.map(removeZDRPrefix()).map(Integer::parseInt).map(checkRangeAndLogErrors(context, numRecord))
              .filter(validZDRRange()).map(handleZoneDistributionRecord(context)).map(s -> {
                try {
                  return getNumRecord(timeoutMillis, context);
                } catch (final Exception e) {
                  log.error("Failed in lookup: {}", context.getRecordLocation());
                }
                return null;
              });

          numRecord = maybeNewLookupResult.orElse(null);
        }

        // If that failed then try the hosted record.
        if (numRecord == null) {
          log.info("Lookup returned no result.");
          switch (context.getLocation()) {
            case INDEPENDENT:
              if (context.getModuleDNSQueries().getModuleId() != 0) {
                log.info("Trying the HOSTED location.");
                context.setLocation(HOSTED);
              } else {
                log.info("Module 0 skipping the HOSTED location.");
                context.setLocation(STOP);
                return null;
              }
              break;
            case HOSTED:
              // Only if configured, is a root quiery, and isn't module 0
              if ((context.isPopulatorQueryRequired() && context.getModuleDNSQueries().isRootQuery())) {
                log.info("Trying the POPULATOR location.");
                context.setLocation(POPULATOR);
              } else {
                log.info("Not configured to use the POPULATOR location.");
                context.setLocation(STOP);
                return null;
              }
              // fall through to the POPULATOR
            case POPULATOR:
              log.info("Trying the POPULATOR.");
              final String fromPopulator = getNumRecordFromPopulator(timeoutMillis, context, handler);
              if (interpret) {
                handler.setResult(interpretNumRecord(fromPopulator, context, timeoutMillis));
              } else {
                handler.setResult(fromPopulator);
              }
              return handler.getResult();
            case STOP:
            default:
              return null;
          }
        } else {
          if (interpret) {
            handler.setResult(interpretNumRecord(numRecord, context, timeoutMillis));
          } else {
            handler.setResult(numRecord);
          }
          return handler.getResult();
        }
      } catch (final NumLookupRedirect numLookupRedirect) {
        context.setLocation(INDEPENDENT);
        context.handleQueryRedirect(numLookupRedirect.getRedirect());
      }
    } while (true);
  }

  /**
   * Handle Zone Distribution Records for emails
   *
   * @param context the NumAPIContextBase
   * @return a Consumer of Integers
   */
  private Function<Integer, String> handleZoneDistributionRecord(final NumAPIContextBase context) {
    return n -> {
      log.info("Handling a Zone Distribution Record for {}", context.getRecordLocation());
      try {
        context.getModuleDNSQueries().setEmailRecordDistributionLevels(n);
      } catch (final NumInvalidParameterException e) {
        log.error("Invalid parameter.", e);
      }
      return context.getRecordLocation();
    };
  }

  /**
   * Range check for Zone Distribution Records
   *
   * @return a Predicate
   */
  private Predicate<Integer> validZDRRange() {
    return n -> n > 0 && n <= 3;
  }

  /**
   * A pass-through function to log an error if the Zone Distribution Record value is out of range
   *
   * @param context   the NumAPIContextBase
   * @param numRecord the ZDR value
   * @return a pass-through function
   */
  private Function<Integer, Integer> checkRangeAndLogErrors(final NumAPIContextBase context, final String numRecord) {
    return n -> {
      if (n < 1 || n > 3) {
        log.error("Invalid Zone Distribution Record number of levels in '{}' when looking up '{}'", numRecord, context.getRecordLocation());
      }
      return n;
    };
  }

  /**
   * A function to remove the ZDR prefix just leaving the numeric value at the end.
   *
   * @return the de-prefixed ZDR
   */
  private Function<String, String> removeZDRPrefix() {
    return s -> s.substring(StringConstants.ZONE_DISTRIBUTION_RECORD_PREFIX.length());
  }

  /**
   * A predicate to check whether a NUM record is a ZDR or not.
   *
   * @return a Predicate
   */
  private Predicate<String> isZoneDistributionRecord() {
    return s -> s.startsWith(StringConstants.ZONE_DISTRIBUTION_RECORD_PREFIX);
  }

  /**
   * Try retrieving a record from the populator
   *
   * @param timeoutMillis The timeout
   * @param context       The context obtained from the NumAPI.begin() method
   * @param handler       the handler for asynchronous responses
   * @return The String result or null
   * @throws NumPopulatorErrorException               on error
   * @throws NumNoRecordAvailableException            on error
   * @throws NumInvalidPopulatorResponseCodeException on error
   * @throws NumBadRecordException                    on error
   * @throws NumInvalidDNSQueryException              on error
   * @throws RrSetIncompleteException                 on error
   * @throws RrSetHeaderFormatException               on error
   * @throws RrSetNoHeadersException                  on error
   */
  private String getNumRecordFromPopulator(int timeoutMillis, final NumAPIContextBase context, final NumAPICallbacks handler)
      throws NumPopulatorErrorException, NumNoRecordAvailableException, NumInvalidPopulatorResponseCodeException, NumBadRecordException, NumInvalidDNSQueryException,
      RrSetHeaderFormatException, RrSetIncompleteException, RrSetNoHeadersException {
    log.info("getNumRecordFromPopulator()");
    final String recordLocation = context.getModuleDNSQueries().getPopulatorLocation();
    if (recordLocation == null) {
      return null;
    }
    log.info("Querying the populator service: {}", recordLocation);

    String numRecord = null;
    while (numRecord == null) {
      numRecord = getNumRecordNoCache(timeoutMillis, recordLocation, context);
      if (numRecord == null) {
        // This is unrecoverable, we should get @status or @error object.
        break;
      }

      log.info("Response from Populator: {}.", numRecord);
      // Parse the MODL response
      final PopulatorResponse response = modlServices.interpretPopulatorResponse(numRecord);
      if (response.isValid()) {
        throw new NumInvalidPopulatorResponseCodeException("Bad response received from the populator service.");
      }
      // Handle the @status response codes
      if (response.getStatus() != null) {
        numRecord = handlePopulatorStatusCodes(timeoutMillis, context, response, handler);
      }
      // Handle the @error response codes
      if (response.getError() != null) {
        if (response.getError().getCode() == 100) {// Enter the populated zone retry loop
          log.error("NUM Populator error: {}, {}", response.getError().getCode(), response.getError().getDescription());
          try {
            int i = 0;
            while (i < PopulatorRetryConfig.ERROR_RETRIES) {
              log.info("Sleeping for {} seconds.", PopulatorRetryConfig.ERROR_RETRY_DELAYS[i]);
              TimeUnit.MILLISECONDS.sleep(PopulatorRetryConfig.ERROR_RETRY_DELAYS[i]);
              log.info("Retrying...");
              numRecord = getNumRecord(timeoutMillis, context);

              final PopulatorResponse retryResponse = modlServices.interpretPopulatorResponse(numRecord);
              if (retryResponse.getStatus() != null) {
                return handlePopulatorStatusCodes(timeoutMillis, context, retryResponse, handler);
              }
              i++;
            }
          } catch (final InterruptedException e) {
            log.error("Interrupted", e);
          }
          log.error("Cannot retrieve NUM record from any location.");
          throw new NumNoRecordAvailableException("Cannot retrieve NUM record from any location.");
        } else {
          log.error("NUM Populator error: {}, {}", response.getError().getCode(), response.getError().getDescription());
          throw new NumPopulatorErrorException(response.getError().getDescription());
        }
      }
    }
    return numRecord;
  }

  /**
   * Populator status codes tell us how to retry the queries while the populator works in the background to get the necessary data
   *
   * @param timeoutMillis the timeout
   * @param context       the NumAPIContextBase object.
   * @param response      the response from the populator
   * @param handler       the handler for asynchronous responses
   * @return null or a valid NUM record
   * @throws NumNoRecordAvailableException            on error
   * @throws NumInvalidPopulatorResponseCodeException on error
   * @throws NumInvalidDNSQueryException              on error
   * @throws RrSetIncompleteException                 on error
   * @throws RrSetHeaderFormatException               on error
   * @throws RrSetNoHeadersException                  on error
   */
  private String handlePopulatorStatusCodes(int timeoutMillis, NumAPIContextBase context, PopulatorResponse response, final NumAPICallbacks handler)
      throws NumNoRecordAvailableException, NumInvalidPopulatorResponseCodeException, NumInvalidDNSQueryException, RrSetHeaderFormatException, RrSetIncompleteException,
      RrSetNoHeadersException {
    log.info("handlePopulatorStatusCodes()");
    String numRecord = null;
    switch (response.getStatus().getCode()) {
      case 1:
        log.info("Populator Status code: 1");
        // Enter the populated zone retry loop
        try {
          //
          // In a change to the specification, we're going to retry the POPULATOR rather than the
          // POPULATED
          // zone because that will be the first to respond when a scraper completes.
          //
          context.setLocation(POPULATOR);
          int i = 0;
          while (i < PopulatorRetryConfig.RETRY_DELAYS.length) {
            log.info("Sleeping for {} seconds.", PopulatorRetryConfig.RETRY_DELAYS[i]);
            TimeUnit.MILLISECONDS.sleep(PopulatorRetryConfig.RETRY_DELAYS[i]);
            log.info("Retrying...");
            numRecord = getNumRecord(timeoutMillis, context);
            if (numRecord != null && !numRecord.contains("@status") && !numRecord.contains("@error")) {
              try {
                final String interpretNumRecord = interpretNumRecord(numRecord, context, timeoutMillis);
                handler.setResult(interpretNumRecord);
              } catch (final Exception e) {
                // Log the error but continue anyway
                log.error("Error in response from the populator.", e);
              }
            }
            i++;
          }
          if (numRecord != null && (numRecord.contains("@status") || !numRecord.contains("@error"))) {
            log.error("Cannot retrieve NUM record from any location.");
            throw new NumNoRecordAvailableException("Cannot retrieve NUM record from any location.");
          }
          return numRecord;
        } catch (InterruptedException e) {
          break;
        }
      case 2:
        log.info("Populator Status code: 2");
        // The record is available at the authoritative server
        context.setLocation(INDEPENDENT);
        numRecord = getNumRecord(timeoutMillis, context);
        if (numRecord == null) {
          log.error("Cannot retrieve NUM record from any location.");
          throw new NumNoRecordAvailableException("Cannot retrieve NUM record from any location.");
        }
        break;
      case 3:
        log.info("Populator Status code: 3");
        // The record exists in the hosted zone.
        context.setLocation(HOSTED);
        numRecord = getNumRecord(timeoutMillis, context);
        if (numRecord == null) {
          log.error("Cannot retrieve NUM record from any location.");
          throw new NumNoRecordAvailableException("Cannot retrieve NUM record from any location.");
        }
        break;
      case PopulatorResponse.VALID_TXT_RECORD_CODE:
        numRecord = response.getNumRecord();
        break;
      default:
        context.setLocation(null);
        log.error("Invalid response code from DNS populator service: {}", response.getStatus().getCode());
        throw new NumInvalidPopulatorResponseCodeException("Invalid response code from DNS populator service: " + response.getStatus().getCode());
    }
    return numRecord;
  }

  /**
   * Interpret the supplied NUM record from DNS.
   *
   * @param moduleNumber The module number
   * @param context      The NumAPIContext
   * @param numRecord    The NUM record from DNS
   * @return The JSON String result of the fully expanded NUM record.
   * @throws NumBadRecordException on error
   * @throws NumLookupRedirect     on error
   */
  private String getInterpretedNumRecordAsJson(final int moduleNumber, final NumAPIContext context, final String numRecord, final int timeoutMillis)
      throws NumBadRecordException, NumLookupRedirect {
    log.info("getInterpretedNumRecordAsJson({}, {})", moduleNumber, numRecord);
    final StringBuilder numRecordBuffer = new StringBuilder();

    final UserVariable[] ruv = context.getRequiredUserVariables();

    if (ruv != null) {
      for (UserVariable v : ruv) {
        numRecordBuffer.append(v.getKey());
        numRecordBuffer.append("=");
        numRecordBuffer.append(v.getValue());
        numRecordBuffer.append(";");
      }
    }
    if (moduleNumber > 0) {
      numRecordBuffer.append("*load=\"");
      numRecordBuffer.append(modulesLocation);
      numRecordBuffer.append(moduleNumber);
      numRecordBuffer.append("/rcf.txt!\";");
    }

    // Append the numRecord with legacy DNS escape sequences replaced.
    numRecordBuffer.append(legacyEscapeReplacer.apply(numRecord));

    log.info("Interpret NUM record: {}", numRecordBuffer.toString());
    return modlServices.interpretNumRecord(numRecordBuffer.toString(), timeoutMillis);
  }

  /**
   * Convert a NUM record String to an interpreted JSON String. Handle any redirect instructions in the interpreted MODL record
   *
   * @param numRecord the uninterpreted NUM record.
   * @param context   the NumAPIContext
   * @return the interpreted NUM record as a JSON string.
   * @throws NumLookupRedirect     on error
   * @throws NumBadRecordException on error
   */
  private String interpretNumRecord(final String numRecord, final NumAPIContextBase context, final int timeoutMillis) throws NumLookupRedirect, NumBadRecordException {
    log.info("interpretNumRecord({}, context)", numRecord);
    String json = null;
    if (numRecord != null && numRecord.trim().length() > 0) {
      // Build a MODL object using the required user variables, the RCF, and the NUM record from
      // DNS.
      json = getInterpretedNumRecordAsJson(context.getModuleDNSQueries().getModuleId(), context, numRecord, timeoutMillis);
    }
    return json;
  }

  /**
   * Get a NUM record for the given query string. Try multi-part queries if necessary.
   *
   * @param timeoutMillis The timeout
   * @param context       The context obtained from the NumAPI.begin() method
   * @return The raw NUM record from DNS.
   * @throws NumInvalidDNSQueryException   on error
   * @throws NumNoRecordAvailableException if a CNAME or SPF record is received instead of a TXT record
   * @throws RrSetIncompleteException      on error
   * @throws RrSetHeaderFormatException    on error
   * @throws RrSetNoHeadersException       on error
   */
  private String getNumRecord(int timeoutMillis, final NumAPIContextBase context)
      throws NumInvalidDNSQueryException, NumNoRecordAvailableException, RrSetHeaderFormatException, RrSetIncompleteException, RrSetNoHeadersException {
    final String recordLocation = context.getRecordLocation();
    if (recordLocation == null) {
      return null;
    }
    log.info("getNumRecord({}, context, {})", timeoutMillis, recordLocation);
    DNSServices.GetRecordResponse recordFromDns;
    recordFromDns = dnsServices.getRecordFromDnsNoCache(recordLocation, timeoutMillis);
    if (recordFromDns == null || recordFromDns.getRecords().length == 0) {
      return null;
    }
    context.setDnsSecSigned(recordFromDns.isSigned());
    return dnsServices.rebuildTXTRecordContent(recordFromDns.getRecords());
  }

  /**
   * Get a NUM record for the given query string. Try multi-part queries if necessary. Don't cache the responses
   *
   * @param timeoutMillis  The timeout
   * @param recordLocation The DNS query String.
   * @return The raw NUM record from DNS.
   * @throws NumInvalidDNSQueryException   on error
   * @throws NumNoRecordAvailableException if a CNAME or SPF record is received instead of a TXT record
   * @throws RrSetIncompleteException      on error
   * @throws RrSetHeaderFormatException    on error
   * @throws RrSetNoHeadersException       on error
   */
  private String getNumRecordNoCache(int timeoutMillis, String recordLocation, final NumAPIContextBase context)
      throws NumInvalidDNSQueryException, NumNoRecordAvailableException, RrSetHeaderFormatException, RrSetIncompleteException, RrSetNoHeadersException {
    log.info("getNumRecordNoCache({}, context, {})", timeoutMillis, recordLocation);
    DNSServices.GetRecordResponse recordFromDns;
    recordFromDns = dnsServices.getRecordFromDnsNoCache(recordLocation, timeoutMillis);
    if (recordFromDns == null || recordFromDns.getRecords().length == 0) {
      return null;
    }
    context.setDnsSecSigned(recordFromDns.isSigned());
    return dnsServices.rebuildTXTRecordContent(recordFromDns.getRecords());
  }

  /**
   * Stop any outstanding DNS queries still in the Executor.
   */
  @Override
  public void shutdown() {
    log.info("shutdown()");
    try {
      executor.shutdown();
      executor.awaitTermination(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error("Shutdown interrupted: ", e);
    } finally {
      if (!executor.isTerminated()) {
        log.info("Failed to shutdown after 1 second, so forcing shutdown.");
        executor.shutdownNow();
      }
    }
    log.info("Shutdown complete.");
  }

  public void setModulesLocation(final String modulesLocation) {
    this.modulesLocation = modulesLocation;
  }

}

