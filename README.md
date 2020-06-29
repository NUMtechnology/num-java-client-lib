# NUM Java Client Library
A Java client for the NUM protocol - making NUM protocol adoption in Java a cinch.

See the full [Specification](https://www.num.uk/specification) at [num.uk](num.uk) and the [Explainer](https://www.num.uk/explainer) for more information.

The NUM protocol supports a range of [modules](https://www.num.uk/modules) that add rich functionality in an extensible way.

## The NUM URI Scheme

The NUM protocol uses the familiar URL format for its URIs and allows [modules](https://www.num.uk/modules) to interpret data in a variety of ways.

The data stored in a NUM Record is converted to JSON String format that can be parsed into JSON objects and Java
objects for straightforward incorporation into Java programs. Here are some example NUM URIs with module `1` - the Contacts module. The default module is `0` (zero) if no module is specified, which has no module schema.
- `num://numexample.com:1`
- `num://jo.smith@numexample.com:1`
- `num://jo.smith@numexample.com:1/work`
- `num://jo.smith@numexample.com:1/personal`
- `num://jo.smith@numexample.com:1/hobbies`
- `num://numexample.com:1/support`
- `num://numexample.com:1/support/website`
- `num://numexample.com:1/support/delivery`
- `num://numexample.com:1/enquiries`
- `num://numexample.com:1/sales`
- `num://numexample.com:1`

As you can see from the examples above, data can be associated with domains and email addresses, and can be organised hierarchically if desired. In future, the protocol will support more than just domains and email addresses.

Additional modules can be referenced in the same way as `ports` in other URIs:
- `num://numexample.com:2` for the `WhoHas` module.
- `num://numexample.com:3` for the `Images` module.
- `num://numexample.com:4` for the `Custodians` module.
- `num://numexample.com:5` for the `Payments` module.
- `num://numexample.com:6` for the `Regulatory` module.
- `num://numexample.com:7` for the `Public Key` module.
- `num://numexample.com:8` for the `Intellectual Property` module.
- `num://numexample.com:9` for the `Terms` module.
- `num://numexample.com:10` for the `Bugs` module.
- `num://numexample.com:nn` for your own module?
 
## Adding Support for the NUM Protocol
The NUM protocol library is available in the [Maven Repository](https://mvnrepository.com/artifact/uk.num/num-java-client-lib).
### Building with Gradle
```groovy
compile 'uk.num:num-java-client-lib:1.0.0'
```
### Building with Maven
```xml
<!-- https://mvnrepository.com/artifact/uk.num/num-java-client-lib -->
<dependency>
    <groupId>uk.num</groupId>
    <artifactId>num-java-client-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```
## Java Class Bindings for NUM Modules
Class libraries for the NUM modules will be added in future. The code examples below show what will be possible when these bindings become available. In the meantime the generic JSON API of your favourite JSON parser library will have to be used.
## Example API Usage
### An example without setting NUM protocol parameters
If NUM protocol parameters are not needed then the programming interface is very simple.
```java
// The required query parameters are defined by the module, in this case module 1 - Contacts

    final URL url = NumProtocolSupport.toUrl("num://numexample.com:1/?C=gb&L=en");

// Use Apache Commons IO to load the JSON from DNS

        final String json = IOUtils.toString(url, StandardCharsets.UTF_8);

// Optionally parse the JSON to the required Module class binding

    ContactsModule contactsModule = objectMapper.readValue(json, ContactsModule.class);

// Access the module data using its Java class binding

    List<Organisation> orgs = contactsModule.getOrganisations();
...
```
### An example with setting NUM protocol parameters
Setting NUM protocol parameters is only slightly more verbose.
```java
// The required query parameters are defined by the module, in this case module 1 - Contacts

    final URL url = NumProtocolSupport.toUrl("num://numexample.com:1/?C=gb&L=en");

// Set a flag to trigger the zone populator in case there is no record available.

    final NUMURLConnection connection = new NUMURLConnection(url);

    connection.setRequestProperty(NUMURLConnection.USE_POPULATOR, "true");

// Use Apache Commons IO to load the JSON from DNS

    final String json = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

// Optionally parse the JSON to the required Module class binding

    ContactsModule contactsModule = objectMapper.readValue(json, ContactsModule.class);

// Access the module data using its Java class binding

    List<Organisation> orgs = contactsModule.getOrganisations();
...
```
### Protocol Parameters
The code example above shows the use of NUM connection properties; the following properties are supported:

- `NUMURLConnection.USE_POPULATOR` defaults to `"false"`. When set to `"true"` if no NUM record is found then the NUM Server will attempt to scrape information to satisfy the query.
- `NUMURLConnection.HIDE_PARAMS` defaults to `"true"` and automatically prefixes query parameters with an underscore so that they do not appear in the JSON output. When set to `"false"` the parameters are left untouched, each parameter can be hidden manually by prepending with an underscore. 

### Record Location and DNSSEC Signature Check

When using the `NUMURLConnection` object there are two additional results available after the NUM record is retrieved:

```java
        final boolean signed = connection.isDnsSecSigned();
        final NumAPICallbacks.Location location = connection.getLocation(); // INDEPENDENT or HOSTED

``` 
## Logging

The NUM library uses slf4j over Logback for logging, and requires a `logback.xml` file on the classpath.
