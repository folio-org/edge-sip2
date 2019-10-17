# edge-sip2

Copyright (C) 2019 The Open Library Foundation

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
$ java -jar edge-sip2-fat.jar -conf '{"port":1234,"okapiUrl":"https://folio-snapshot-okapi.aws.indexdata.com","tenant":"diku"}'
```
On Windows the `edge-sip2-fat.jar` should be launched with the JSON configuration in double quotes and the inner double quotes should be escaped, for example:
```
$ java -jar edge-sip2-fat.jar -conf "{\"port\":1234,\"okapiUrl\":\"https://folio-snapshot-okapi.aws.indexdata.com\",\"tenant\":\"diku"\}"
``` 

|Config option|Type|Description|
|-------------|----|-----------|
|`port`|int|The port the module will use to bind, typically 1024 < port < 65,535.|
|`okapiUrl`|string|The URL of the Okapi server used by FOLIO.|
|`tenant`|string|The FOLIO assigned tenant ID. Multi-tenant support TBD.|
|`fieldDelimiter`|string|The character that the self service kiosk will use when encoding SIP messages. Defaults to "\|".|
|`errorDetectionEnabled`|boolean|Indicates whether or not the self service kiosk will be using SIP error detection in messages sent to and from this module. Defaults to "false".|
|`charset`|string|The character set SIP messages must be encoded with when sent and received by the self service kiosk. The charset must be defined as a "Canonical Name for java.nio API". See: [Supported Encodings](https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html). Default is "IBM850".|
|`messageDelimiter`|string|The character sequence that indicates the end of a single SIP message. This is available in case the self check kiosk is not compliant with the SIP specification. The default is "\\r"|
|`netServerOptions`|JSON object|Configuration options for the server. These are Vertx options and are numerous. See: [NetServerOptions](https://vertx.io/docs/apidocs/io/vertx/core/net/NetServerOptions.html).|

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
$ java -jar edge-sip2-fat.jar -conf '{"port":1234,"okapiUrl":"https://folio-snapshot-okapi.aws.indexdata.com","tenant":"diku","netServerOptions":{"ssl":true,"pemKeyCertOptions":{"certPaths":["cert.crt"],"keyPaths":["cert.key"]}}}'
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

## Additional information

[SIP2 Specification](http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf)

### Issue tracker

See project [SIP2](https://issues.folio.org/browse/SIP2)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).

### Other documentation

Other [modules](https://dev.folio.org/source-code/#server-side) are described,
with further FOLIO Developer documentation at [dev.folio.org](https://dev.folio.org/)
