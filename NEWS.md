## 3.1.2 2024-04-22
* [SIP2-169](https://issues.folio.org/browse/SIP2-169) Correct issue with incorrect values for items in transit from checkin response (backport)

## 3.1.1 2023-11-06
* [SIP2-178](https://issues.folio.org/browse/SIP2-178) Vert.x 4.4.6 fixing Netty HTTP/2 DoS (CVE-2023-44487)

## 3.1.0 2023-10-12 
* [SIP2-121](https://issues.folio.org/browse/SIP2-121): SIP2: Renew response
* [SIP2-94](https://issues.folio.org/browse/SIP2-94): Non SIP2 protocol error occurs when faced with exceptional situations
* [SIP2-143](https://issues.folio.org/browse/SIP2-143): Update feesfines interface version to 18.0
* [SIP2-27](https://issues.folio.org/browse/SIP2-27): SIP2: Renew All response
* [SIP2-134](https://issues.folio.org/browse/SIP2-134): Add /admin/health to edge-sip2
* [SIP2-144](https://issues.folio.org/browse/SIP2-144): SIP2: Fee Paid
* [SIP2-147](https://issues.folio.org/browse/SIP2-147): SIP2: Renew response missing title identifier.
* [SIP2-15](https://issues.folio.org/browse/SIP2-15): SIP2: Patron Status Response
* [SIP2-148](https://issues.folio.org/browse/SIP2-148): Update circulation interface to 14.0
* [SIP2-145](https://issues.folio.org/browse/SIP2-145): SIP2: extend the Checkin command response
* [SIP2-146](https://issues.folio.org/browse/SIP2-146): Fee Paid response has invalid checksum
* [SIP2-149](https://issues.folio.org/browse/SIP2-149): SIP2: extend the Patron Information Response
* [SIP2-153](https://issues.folio.org/browse/SIP2-153): Update to Java 17 edge-sip2
* [SIP2-154](https://issues.folio.org/browse/SIP2-154): patronPasswordVerificationRequired is always TRUE
* [SIP2-156](https://issues.folio.org/browse/SIP2-156): Item Information Response - incorrect BT-element appears in fixed-length element
* [SIP2-132](https://issues.folio.org/browse/SIP2-132): Implement refresh token rotation
* [SIP2-152](https://issues.folio.org/browse/SIP2-152): SIP2: Extend Patron Information summary field on fee items elements
* [SIP2-155](https://issues.folio.org/browse/SIP2-155): SIP2: extend the Patron Information Response on fee fields
* [SIP2-161](https://issues.folio.org/browse/SIP2-161): Item Information Response - invalid
* [SIP2-151](https://issues.folio.org/browse/SIP2-151): SIP2: summary fields for Patron Information

## 3.0.0 2023-02-23
* [SIP2-123](https://issues.folio.org/browse/SIP2-123): SIP2: Item Information Response
* [SIP2-125](https://issues.folio.org/browse/SIP2-125): Logging improvement
* [SIP2-128](https://issues.folio.org/browse/SIP2-128): Logging improvement - Configuration
* [SIP2-136](https://issues.folio.org/browse/SIP2-136): "finding and loading template" exception in transactionDate lib.ftl on missing "99 SC Status"
* [SIP2-138](https://issues.folio.org/browse/SIP2-138): Align the module with API breaking change

## 2.4.0 2022-11-02
* [SIP2-116](https://issues.folio.org/browse/SIP2-116): Upgrade Users interface to 16.0
* [SIP2-130](https://issues.folio.org/browse/SIP2-130): Update dependencies for Nolana (MODINNREACH-322)

## 2.3.0 2022-09-07
* [SIP2-115](https://issues.folio.org/browse/SIP2-115): Always populate the "title identifier" field for checked out response

## 2.2.0 2022-03-09
* [SIP2-106](https://issues.folio.org/browse/SIP2-106): Adapt module to title level requests (TLR)

## 2.1.3 2022-03-09
* [SIP2-110](https://issues.folio.org/browse/SIP2-110): Update dependencies (CVE-2020-25649)
* [SIP2-110](https://issues.folio.org/browse/SIP2-110): Update Vert.x from 3.9.2 to 4.2.5
* [SIP2-107](https://issues.folio.org/browse/SIP2-107): Support circulation interface v13

## 2.1.2 2021-12-17
* [SIP2-102](https://issues.folio.org/browse/SIP2-102): Kiwi R3 2021 - Log4j edge- modules 2.17.0 upgrade

## 2.1.1 2021-12-17
* [SIP2-100](https://issues.folio.org/browse/SIP2-100): Update log4j for log4shell vulnerability

## 2.1.0 2021-10-11
 * [SIP2-96](https://issues.folio.org/browse/SIP2-96): Update feesfines interface version to 17.0 

## 2.0.1 2021-06-11
 * [SIP2-95](https://issues.folio.org/browse/SIP2-95): add support for empty no block due date 
 * [SIP2-92](https://issues.folio.org/browse/SIP2-92): Update circulation interface dependency 
 * [SIP2-91](https://issues.folio.org/browse/SIP2-91): Update circulation interface dependency 
 
## 2.0.0 2021-03-15
 * [SIP2-78](https://issues.folio.org/browse/SIP2-78): Add multi-tenant support 
 
## 1.4.1 2021-03-02
 * [SIP2-90](https://issues.folio.org/browse/SIP2-90): ACS command causes 502 Bad Gateway errors in logs
 
## 1.4.0 2020-10-20
 * [SIP2-86](https://issues.folio.org/browse/SIP2-86): Upgrade edge-sip2 to use Java 11 
 * [FOLIO-2706](https://issues.folio.org/browse/FOLIO-2706): Update dev documentation for FQDNs of reference environments

## 1.3.1 2020-07-13
 * [SIP2-83](https://issues.folio.org/browse/SIP2-83): Allow checkout when externalUserId is provided and password is not required
 * [SIP2-80](https://issues.folio.org/browse/SIP2-80): Fix security vulnerability reported in checkstyle < 8.29

## 1.3.0 2020-06-12
 * [SIP2-84](https://issues.folio.org/browse/SIP2-84): Add 7.0 as acceptable login interface version
 * [SIP2-82](https://issues.folio.org/browse/SIP2-82): Upgrade RMB to v30.0.2 and release it.

## 1.2.0 2020-03-18
 * [SIP2-43](https://issues.folio.org/browse/SIP2-43): Document SIP2 implementation
 * [SIP2-79](https://issues.folio.org/browse/SIP2-79): Migrate to new major version of item-storage, inventory, circulation

## 1.1.0 2019-12-09
 * [SIP2-77](https://issues.folio.org/browse/SIP2-77): Update the `circulation` interface
   dependency with `8.0`
 * [SIP2-76](https://issues.folio.org/browse/SIP2-76): Fix failing unit test

## 1.0.1 2019-10-22
 * Allow patrons to authenticate using FOLIO userName (SIP2-73) 

## 1.0.0 2019-07-24
 * Initial release contains support for the following SIP commands:
    * Checkout
    * Checkin
    * SC Status
    * Request ACS Resend
    * Login
    * Patron Information
    * End Patron Session
