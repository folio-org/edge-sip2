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

## Additional information

[SIP2 Specification](http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf)

### Issue tracker

See project [SIP2](https://issues.folio.org/browse/SIP2)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).

### Other documentation

Other [modules](https://dev.folio.org/source-code/#server-side) are described,
with further FOLIO Developer documentation at [dev.folio.org](https://dev.folio.org/)