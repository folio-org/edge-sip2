# edge-sip2

Copyright (C) 2019-2020 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

## Overview

The purpose of this edge API is to bridge the gap between self service
circulation and patron services stations and FOLIO by allowing these systems to
issue requests and receive responses in Standard Interchange Protocol v2 (SIP2).
These requests will be serviced by FOLIO APIs and the responses will be based on
FOLIO results for all supported (TBD) SIP2 commands.

## Configuration

The edge-sip2 module can be launched via `edge-sip2-fat.jar` as follows:

```bash
$ java -jar edge-sip2-fat.jar -conf sip2.conf
```
The -conf option can either specify the filename of the configuration or inline JSON. 
Here is a sample sip2.conf file:
```
{ 
  "port": 6443,
  "okapiUrl": "https://folio-testing-okapi.dev.folio.org",
  "tenantConfigRetrieverOptions": {
    "scanPeriod": 300000,
    "stores": [{
      "type": "file",
      "format": "json",
      "config": {
        "path": "sip2-tenants.conf"
      },
      "optional": false
    }]
  }
}
```
For inline JSON, the format is:
```
-conf '{"port":1234,"okapiUrl":"https://folio-snapshot-okapi.dev.folio.org".....}'
```
On Windows, inline JSON configuration is in double quotes and the inner double quotes should be escaped, for example:
```
-conf "{\"port\":1234,\"okapiUrl\":\"https://folio-snapshot-okapi.dev.folio.org\"....}"
``` 

One option is to mount the configuration files to the Docker container and provide
command line arguments to point it to the right path (e.g. `-conf /path/to/config`).

|Config option|Type|Description|
|-------------|----|-----------|
|`port`|int|The port the module will use to bind, typically 1024 < port < 65,535.|
|`okapiUrl`|string|The URL of the Okapi server used by FOLIO.|
|`tenantConfigRetrieverOptions`|JSON object|Location for tenant configuration.|
|`scanPeriod`|int|Frequency in msec that sip2 will check for and reload tenant configuration changes.|
|`stores`|JSON array|Defines the properties for the tenant configuration stores. Multiple sources of tenant configuration can be loaded and combined together. |
|`type`|string|The store type. Several supported types include: file, http, github, s3. See: [vertx config](https://vertx.io/docs/vertx-config/java/) |
|`format`|string|Sip2 expects configuration to be in json format.|
|`config`|string|Store type-specific properties. |
|`path`|string|Path name of the tenant configuration file for file type stores. |
|`optional`|boolean|If a failure is caught while loading the tenant configuration from an optional store, the failure is logged, but the processing does not fail. Instead, the tenant configuration will be empty.|
|`netServerOptions`|JSON object|Configuration options for the server. These are Vertx options and are numerous. See: [NetServerOptions](https://vertx.io/docs/apidocs/io/vertx/core/net/NetServerOptions.html).|

Note: edge-sip2 now requires two config files: the main bootstrap sip2.conf and tenant configuration: sip2-tenants.conf. The additional config file is required to support multi-tenants and runtime reloading of tenant configuration without restarting the edge-sip2 module.
 
Here is a sample sip2-tenants.conf file:
```
{
"scTenants": [
  {
  "scSubnet": "11.11.00.00/16",
  "tenant": "test_tenant1",
  "errorDetectionEnabled": true,
  "messageDelimiter": "\r",
  "fieldDelimiter": "\|",
  "charset": "ISO-8859-1"
  },
  {
  "scSubnet": "22.22.00.00/16",
  "tenant": "test_tenant2",
  "errorDetectionEnabled": true,
  "messageDelimiter": "\r",
  "fieldDelimiter": "\|",
  "charset": "ISO-8859-1"
  }
]
}
```

|Config option|Type|Description|
|-------------|----|-----------|
|`scTenants`|JSON array|Array of sip2 tenant configurations.|
|`scSubnet`|string|IPv4 CIDR of a tenant's self service kiosk. This is used to identify the tenant configuration for an incoming kiosk connection. |
|`tenant`|string|The FOLIO assigned tenant ID. |
|`errorDetectionEnabled`|boolean|Indicates whether or not the self service kiosk will be using SIP error detection in messages sent to and from this module. Defaults to "false".|
|`messageDelimiter`|string|The character sequence that indicates the end of a single SIP message. This is available in case the self check kiosk is not compliant with the SIP specification. The default is "\\r"|
|`fieldDelimiter`|string|The character that the self service kiosk will use when encoding SIP messages. Defaults to "\|".|
|`charset`|string|The character set SIP messages must be encoded with when sent and received by the self service kiosk. The charset must be defined as a "Canonical Name for java.nio API". See: [Supported Encodings](https://docs.oracle.com/en/java/javase/11/intl/supported-encodings.html). Default is "IBM850".|

### Tenant configuration located in AWS S3
Edge-sip2 supports [various locations](https://vertx.io/docs/vertx-config/java/#_available_configuration_stores) for sip2-tenants.conf  tenant configuration. Additionally, it supports [S3 config](https://github.com/mikelee2082/vertx-config-s3). To include vertx-config-s3 libraries when building edge-sip2, include the maven profile command:

    mvn -P vertx-config-s3

Here is a sample sip2.conf for storing tenant config in S3:

    {
    "port": 6443,
    "okapiUrl": "https://folio-testing-okapi.dev.folio.org",
    "tenantConfigRetrieverOptions": {
      "scanPeriod": 300000,
      "stores": [{
        "type": "s3",
        "format": "json",
        "config": {
          "region": "my-region",
          "bucket": "my-bucket",
          "key": "sip2/sip2-tenants.conf"
        },
        "optional": true
      }]
    }
  }


## FOLIO Configuration

Certain properties are retrieved from FOLIO configuration once a user has logged in via SIP2. There are properties at the tenant level and properties per kiosk, which is defined as a FOLIO service point. Properties are stored as JSON via the `configuration` module. Missing configuration properties will lead to edge-sip2 runtime failures. The edge-sip2 properties listed below must be manually created via the `POST` `/configurations/entries` API using a tool such as curl or postman.

### Tenant Properties

|Property|Type|Description|
|--------|----|-----------|
|`statusUpdateOk`|`boolean`|Indicates to the kiosk that the SIP service allows patron status updates from the kiosk.|
|`offlineOk`|`boolean`|Indicates to the kiosk that FOLIO supports off-line operations.|
|`supportedMessages`|`object[]`|An array objects that indicate to the kiosk which messages are supported by the edge-sip2 module.|
|`patronPasswordVerificationRequired`|`boolean`|Indicates whether or not SIP commands that supply a patron password will attempt to verify the password by attempting a FOLIO login with these supplied patron credentials. A failed patron login will fail the SIP request.|

#### `supportedMessages` object properties

|Property|Type|Description|
|--------|----|-----------|
|`messageName`|`string`|The name of the message. See: [Messages](src/main/java/org/folio/edge/sip2/domain/messages/enumerations/Messages.java)|
|`isSupported`|`string`|`Y` or `N` to indicate to the kiosk whether or not the message is supported|

#### Example `configuration` object

```javascript
{
  "module": "edge-sip2",
  "configName": "acsTenantConfig",
  "enabled": true,
  "value": "{\"supportedMessages\": [{\"messageName\": \"PATRON_STATUS_REQUEST\",\"isSupported\": \"N\"},{\"messageName\": \"CHECKOUT\",\"isSupported\": \"Y\"},{\"messageName\": \"CHECKIN\",\"isSupported\": \"Y\"},{\"messageName\": \"BLOCK_PATRON\",\"isSupported\": \"N\"},{\"messageName\": \"SC_ACS_STATUS\",\"isSupported\": \"Y\"},{\"messageName\": \"LOGIN\",\"isSupported\": \"Y\"},{\"messageName\": \"PATRON_INFORMATION\",\"isSupported\": \"Y\"},{\"messageName\": \"END_PATRON_SESSION\",\"isSupported\": \"Y\"},{\"messageName\": \"FEE_PAID\",\"isSupported\": \"N\"},{\"messageName\": \"ITEM_INFORMATION\",\"isSupported\": \"N\"},{\"messageName\": \"ITEM_STATUS_UPDATE\",\"isSupported\": \"N\"},{\"messageName\": \"PATRON_ENABLE\",\"isSupported\": \"N\"},{\"messageName\": \"HOLD\",\"isSupported\": \"N\"},{\"messageName\": \"RENEW\",\"isSupported\": \"N\"},{\"messageName\": \"RENEW_ALL\",\"isSupported\": \"N\"}, {\"messageName\": \"REQUEST_SC_ACS_RESEND\",\"isSupported\": \"Y\"}],\"statusUpdateOk\": false,\"offlineOk\": false,\"patronPasswordVerificationRequired\": true}"
}
```

### Kiosk (service point) Properties

|Property|Type|Description|
|--------|----|-----------|
|`retriesAllowed`|`number`|Indicates to the kiosk the number of retries allowed by FOLIO. This should be a number between 0 and 999, where 999 means that the retry number is unknown.|
|`timeoutPeriod`|`number`|Indicates to the kiosk the period of time before a transaction is aborted by the kiosk. The number should be between 0 and 999, where 0 means that FOLIO is not online and 999 means the time out is unknown. The number is expressed in tenths of a second.|
|`checkinOk`|`boolean`|Indicates whether or not the kiosk is allowed to check in items.|
|`acsRenewalPolicy`|`boolean`|Indicates that the kiosk is allowed by FOLIO to process patron renewal requests.|
|`checkoutOk`|`boolean`|Indicates whether or not the kiosk is allowed to check out items.|
|`libraryName`|`string`|The name of the library where the kiosk is located or whatever makes sense for the tenant.|
|`terminalLocation`|`string`|This could be the location of the kiosk within the library or the UUID of the service point.|

#### Example `configuration` object

```javascript
{
  "module": "edge-sip2",
  "configName": "selfCheckoutConfig.e0ab8c91-2a4a-433d-a3cf-1837053c89a8",
  "enabled": true,
  "value": "{\"timeoutPeriod\": 5,\"retriesAllowed\": 3,\"checkinOk\": true,\"checkoutOk\": true,\"acsRenewalPolicy\": false,\"libraryName\": \"Datalogisk Institut\",\"terminalLocation\": \"e0ab8c91-2a4a-433d-a3cf-1837053c89a8\"}"
}
```

### FOLIO Provided Properties

|Property|Type|Description|
|--------|----|-----------|
|`timezone`|`string`|The tenant's time zone as set in FOLIO.| 

#### Example `configuration` object

```javascript
{
  "module": "ORG",
  "configName": "localeSettings",
  "enabled": true,
  "value": "{\"timezone\":\"America/New_York\"}"
}
```

## Implemented Messages

Currently, edge-sip2 implements select SIP messages. Below, is the list of all implemented messages.

|SIP Request|Implemented|Notes|
|-----------|-----------|-----|
|Patron Status Request|No||
|Checkout|Yes|Response SIP fields hardcoded: "renewal ok" is set to "N", "magnetic media" is set to "U". SIP field "desensitize" is set to "Y" is the FOLIO check out succeeded and "N" when there is failure. Fee/fines related fields are not implemented. The "due date" format is the same as other SIP date/time format strings: "YYYYMMDDZZZZHHMMSS".|
|Checkin|Yes|Response SIP fields hardcoded: "alert" is set to "N", "magnetic media" is set to "U". Most optional SIP fields are not implemented. The "resensitize" field will be set to "Y" if the FOLIO check in succeeded and "N" if there was a failure.|
|Block Patron|No||
|SC Status|Yes||
|Request ACS Resend|Yes||
|Login|Yes|The request "location code" should contain the UUID of the service point for the kiosk.|
|Patron Information|Yes|Response SIP field "hold items count" currently only refers to FOLIO "Hold" requests and not "Page" requests. Only "Hold" request data is returned in the summary results as well. The "charged items count", "fine items count" and "unavailable holds count" are not supported. However, "unavailable holds count" is not implemented due to an oversight as "hold items count" includes all "Hold" requests in any "Open" state, instead of limited to "Open - Awaiting pickup". Likewise, "unavailable holds count" could contain "Hold" requests that are not "Closed" and not "Open - Awaiting pickup". This will likely need to be corrected. The response "patron status" field is partially supported via FOLIO manual blocks where a "borrowing" block will set all privilege codes to "N", a "renewals" block will set the SIP "renewal privileges defined" code to "N", and a "requests" block will set "hold privileges denied" and "recall privileges denied" to "N".|
|End Patron Session|Yes||
|Fee Paid|No||
|Item Information|No||
|Patron Enable|No||
|Hold|No||
|Renew|No||
|Renew All|No||

## Security

The SIP protocol does not consider security apart from providing the possibility of SC/ACS negotiated encryption algorithm for the password and user ID, which seems unlikely to be used. All communication over the wire, via TCP, is plain text. This may be fine in an environment that is locked down in some way. However, FOLIO, along with this module, can be hosted in the cloud and plain text communication is not acceptable.

We **_strongly_** recommend securing communication from the SC to edge-sip2. To this end, edge-sip2 can be configured to use TLS to encrypt communication. This requires the SC to communicate with TLS as well. It is our understanding that most self service kiosks do not have this ability natively and third party solutions must be employed. One such solution is `stunnel`.

A typical `stunnel` deployment will involve installing the `stunnel` service either on the SC or a machine that is locked down with the SC and provides a port that the SC will be configured to connect to for SIP communication. The SIP commands are then sent to and received from the `stunnel` port unencrypted. Communication from the `stunnel` to the TLS termination end point will be encrypted. There are several ways to terminate TLS and the advantages/disadvantages of these is out of scope for this document. Here, we will focus on enabling TLS termination via the edge-sip2 module. This is done via simple launch configuration options.

`stunnel` can be downloaded here: [https://www.stunnel.org/downloads.html](https://www.stunnel.org/downloads.html)

Example FOLIO configuration section for `stunnel.conf`:

```ini
[FOLIO]
key = stunnel.pem
cert = stunnel.pem
client = yes
accept = 127.0.0.1:5555
connect = sip2.example.com:6443
```
Example edge-sip2 configuration:

```bash
$ java -jar edge-sip2-fat.jar -conf '{"port":1234,"okapiUrl":"https://folio-snapshot-okapi.dev.folio.org","tenant":"diku","netServerOptions":{"ssl":true,"pemKeyCertOptions":{"certPaths":["cert.crt"],"keyPaths":["cert.key"]}}}'
```

|Config option|Type|Description|
|-------------|----|-----------|
|`ssl`|boolean|Indicates whether or not to enable SSL (TLS) support for the server|
|`pemKeyCertOptions`|JSON object|Used when the certificate is in PEM format|
|`pfxKeyCertOptions`|JSON object|Used when the certificate is in PFX format|
|`keyStoreOptions`|JSON object|Used when the certificate is in JKS (Java Keystore) format|

|`pemKeyCertOptions`|type|Description|
|-------------------|----|-----------|
|`certPath`|string|File system path to a PEM formatted certificate|
|`certPaths`|JSON array of strings|File system paths to PEM formatted certificates|
|`keyPath`|string|File system path to PEM formatted key|
|`keyPaths`|JSON array of strings|File system paths to PEM formatted keys|

|`pfxKeyCertOptions`|type|Description|
|-------------------|----|-----------|
|`path`|string|File system path to PFX (PKCS #12) store|
|`password`|string|The password for the PFX (PKCS #12) store|

|`keyStoreOptions`|type|Description|
|-----------------|----|-----------|
|`path`|string|File system path to JKS key store|
|`password`|string|The password for the JKS key store|

#### Security concerns for developers

For local development, there is no requirement to encrypt communications from a SIP2 client to edge-sip2. Unencrypted TCP sockets are the default when launching edge-sip2 as described in the [Configuration](#configuration) section. Encrypted communication from a SIP2 client is only required when explicitly configured via the [above options](#security) and is up to the developer to provide that secure connection for edge-sip2.

## Metrics

This module makes use of [Micrometer](https://micrometer.io) to collect SIP2, Vert.x and JVM metrics. The metrics need to be collected by a monitoring system backed. This is where Micrometer provides flexibility, by allowing the module to code to the Micrometer interface, which is vendor neutral. Once determined, the vendor specific backend binding is provided runtime and can be easily replaced.

### Enabling Vert.x metrics

By default, metrics are disabled. To enable Vert.x metrics pass the following Java argument:

```
-Dvertx.metrics.options.enabled=true
```

With metrics enabled, configuration must be supplied to the verticle. This can be done as follows:

```
 -options '{"metricsOptions":{"labels":["LOCAL","REMOTE","HTTP_PATH","HTTP_METHOD","HTTP_CODE","CLASS_NAME"],"enabled":true,"prometheusOptions":{"enabled":true,"startEmbeddedServer":true,"embeddedServerOptions":{"port":8081}}}}'
```

The `metricsOptions` here indicate that the verticle should collect metrics with the supplied list of `labels`. Some of these labels, like `REMOTE`, may lead to high cardinality metrics. The default `labels` list is "HTTP\_METHOD", "HTTP\_CODE", "POOL\_TYPE" and "EB\_SIDE".

Also specified here are `prometheusOptions`. In this case, the backend will be [Prometheus](https://prometheus.io). Prometheus "scrapes" metrics via HTTP at a specified interval. The options specified here allow Vert.x to create an HTTP server to handle metrics scraping. Prometheus setup is outside the scope of this document. Other bindings could be used, like [InfluxDB](https://www.influxdata.com/products/influxdb-overview/).

### Available metrics

For a list of Vert.x metrics (HTTP Client and Net Server are the primary sources for metrics in this module) see: [Vert.x core tools metrics](https://vertx.io/docs/vertx-micrometer-metrics/java/#_vert_x_core_tools_metrics)

The following metrics are supplied by this module:

|Metric name|Labels|Type|Description|
|-----------|------|----|-----------|
|`org_folio_edge_sip2_command_timer`|`command`|Timer|SIP2 command execution time|
|`org_folio_edge_sip2_invalidMessage_errors`|`port`|Counter|A count of invalid message errors|
|`org_folio_edge_sip2_request_errors`|`port`|Counter|A count of request errors|
|`org_folio_edge_sip2_response_errors`|`port`|Counter|A count of response errors|
|`org_folio_edge_sip2_scResend_errors`|`port`|Counter|A count of SC resend errors, which occurs when the module fails to send the SC a resend message when the prior received message was not understood|
|`org_folio_edge_sip2_socket_errors`|`port`|Counter|A count of socket errors|

JVM metrics (memory, GC, threads, etc.) are supplied as well.

### Building with metrics

The Maven pom.xml contains 2 profiles, `metrics-prometheus` and `metrics-influxdb`. Building with either or both of these profiles active will include the appropriate dependencies required to use metrics with that registry.

```
$ mvn install -P metrics-prometheus
```

### Launching with the community Docker image

If metrics need to be enabled, it is probably best to add any required runtime binding jars to the fat jar as part of a build. If this is not possible, the module can still be launched via the community Docker image. N.B., we may find that this approach cumbersome and may need to come up with an alternative approach.

Copy the appropriate runtime binding jars to a directory:

```
$ cp micrometer-registry-prometheus-1.1.5.jar simpleclient_common-0.5.0.jar simpleclient-0.5.0.jar /my/metrics/libs
```

Then run a container from the FOLIO docker hub image (either snapshot `folioci/edge-sip2` or released `folioorg/edge-sip2`):

```
$ docker run -v /my/metrics/libs:/metrics -p 6443:6443 --expose 8081 -p 8081:8081  -e JAVA_OPTIONS="-Dvertx.metrics.options.enabled=true " -e JAVA_CLASSPATH=/metrics/*:/usr/verticles/edge-sip2-fat.jar -e JAVA_MAIN_CLASS=io.vertx.core.Launcher folioci/edge-sip2 run org.folio.edge.sip2.MainVerticle -conf '{"port":6443,"okapiUrl":"https://folio-okapi.example.com","tenant":"diku","messageDelimiter":"\r","errorDetectionEnabled":true,"charset":"ISO-8859-1"}' -options '{"metricsOptions":{"labels":["LOCAL","REMOTE","HTTP_PATH","HTTP_METHOD","HTTP_CODE","CLASS_NAME"],"enabled":true,"prometheusOptions":{"enabled":true,"startEmbeddedServer":true,"embeddedServerOptions":{"port":8081}}}}'
```

This example shows how to launch with the Prometheus binding. Since Prometheus needs to scrape the metrics, we need to expose port for the HTTP server.

## Common Problems

### "Unable to find all necessary configuration(s). Found \<N\> of \<M\>"

This log message happens when one or more of the FOLIO configuration key/value maps are missing when retrieved. Ensure that each set of properties is stored in FOLIO `configuration` and that the service point UUID for the kiosk configuration matches the "location code" in the SIP "Login" message. Another problem could be that the tenant locale settings may not be saved in the database. On the initial deployment of FOLIO, as of Edelweiss, the locale settings are defaulted by the UI and not stored in the database until the "save" button is pressed. Since the UI defaults to usable settings for many, it may be misleading that these settings are present for backend modules, like edge-sip2, to consume.

## Additional information

[SIP2 Specification](http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf)

### Issue tracker

See project [SIP2](https://issues.folio.org/browse/SIP2)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).

### Other documentation

Other [modules](https://dev.folio.org/source-code/#server-side) are described,
with further FOLIO Developer documentation at [dev.folio.org](https://dev.folio.org/)
