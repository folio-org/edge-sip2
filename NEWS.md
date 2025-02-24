## 3.3.2 2025-02-24
* [SIP2-249](https://issues.folio.org/browse/SIP2-249): Checkout Request for Item with '/' in barcode fails with cql parse error - TITLE NOT FOUND
* [SIP2-252](https://issues.folio.org/browse/SIP2-252): CQL injection, encode CQL strings, use percent encoding

## 3.3.1 2024-11-26
* [SIP2-231](https://issues.folio.org/browse/SIP2-231): AJ Field (Title) Issue in Successful Renewals
* [SIP2-232](https://issues.folio.org/browse/SIP2-232): AH Field (Due Date) Issue in Unsuccessful Renewals
* [SIP2-240](https://issues.folio.org/browse/SIP2-240): SIP2 providing values in USD rather than ZAR


## 3.3.0 2024-10-31
* [SIP2-195](https://issues.folio.org/browse/SIP2-195): FolioResourceProvider is submitting expired access tokens to FOLIO
* [SIP2-173](https://issues.folio.org/browse/SIP2-173): SIP2: extend checkin response with Patron identifier (AA)
* [SIP2-202](https://issues.folio.org/browse/SIP2-202): Vert.x 4.5.7 fixing netty form POST OOM CVE-2024-29025
* [SIP2-200](https://issues.folio.org/browse/SIP2-200): Enhance WebClient TLS Configuration for Secure Connections to OKAPI
* [SIP2-205](https://issues.folio.org/browse/SIP2-205): Issue with edge-sip2
* [SIP2-208](https://issues.folio.org/browse/SIP2-208): \r missing from error response message
* [SIP2-209](https://issues.folio.org/browse/SIP2-209): EdgeSip2IT for "mvn verify"
* [SIP2-89](https://issues.folio.org/browse/SIP2-89): Implement patron PIN field instead of patron password field
* [SIP2-212](https://issues.folio.org/browse/SIP2-212): wrong patron information response (64)
* [SIP2-214](https://issues.folio.org/browse/SIP2-214): Disable host veryfing in FIPS mode
* [SIP2-216](https://issues.folio.org/browse/SIP2-216): FeePaidHandlerTests test ends with FAILURE. Issue with locale
* [SIP2-150](https://issues.folio.org/browse/SIP2-150): Add port with IP address CIDR as option for tenant identification in configuration
* [SIP2-219](https://issues.folio.org/browse/SIP2-219): Add support for COP currency type
* [SIP2-221](https://issues.folio.org/browse/SIP2-221): edge-sip2 returns "valid patron password" field in 64 message even with password validation turned off
* [SIP2-223](https://issues.folio.org/browse/SIP2-223): SIP2: Invalid checkin response message when using reject on item status feature (SIP2-186)
* [SIP2-222](https://issues.folio.org/browse/SIP2-222): SIP2 login issue - active session with an invalid password
* [SIP2-225](https://issues.folio.org/browse/SIP2-225): SIP2: Invalid item information response (18) when item identifier is not found.
* [SIP2-224](https://issues.folio.org/browse/SIP2-224): Clean up the login with cache code
* [SIP2-226](https://issues.folio.org/browse/SIP2-226): Invalid Response for Patron Information due to holdItems array with null entry
* [SIP2-233](https://issues.folio.org/browse/SIP2-233): Release: Ramsons - edge-sip2

## 3.2.7 2024-10-01
* [SIP2-222](https://issues.folio.org/browse/SIP2-222): SIP2 login issue - active session with an invalid password

## 3.2.6 2024-09-10
* [SIP2-219](https://issues.folio.org/browse/SIP2-219): Add support for COP CurrencyType

## 3.2.5 2024-07-03
* [SIP2-214](https://issues.folio.org/browse/SIP2-214): Disable host veryfing in FIPS mode

## 3.2.4 2024-05-30
* [SIP2-208](https://issues.folio.org/browse/SIP2-208): \r missing from error response message
* [SIP2-209](https://issues.folio.org/browse/SIP2-209): EdgeSip2IT for "mvn verify"

## 3.2.3 2024-05-29
* [SIP2-205](https://issues.folio.org/browse/SIP2-205): edge-sip2 release issue, main process exit with error in description

## 3.2.2 2024-05-28
* [SIP2-200](https://issues.folio.org/browse/SIP2-200): Enhance WebClient TLS Configuration for Secure Connections to OKAPI
* [SIP2-201](https://issues.folio.org/browse/SIP2-201): Enhance SIP2 Endpoint Security with TLS and FIPS-140-2 Compliant Cryptography
* [SIP2-202](https://issues.folio.org/browse/SIP2-202): Vert.x 4.5.7 fixing netty form POST OOM CVE-2024-29025

## 3.2.1 2024-04-19
* [SIP2-195](https://issues.folio.org/browse/SIP2-195): FolioResourceProvider is submitting expired access tokens to FOLIO

## 3.2.0 2024-03-22
* [SIP2-155](https://issues.folio.org/browse/SIP2-155): SIP2: extend the Patron Information Response on fee fields
* [SIP2-177](https://issues.folio.org/browse/SIP2-177): Patron Status Command always returns all statuses as 'Y'
* [SIP2-175](https://issues.folio.org/browse/SIP2-175): SIP2: No fine items summary returned in Patron Information response (63)
* [SIP2-169](https://issues.folio.org/browse/SIP2-169): Checkin response gives wrong values for items in transit (CT &CV)
* [SIP2-174](https://issues.folio.org/browse/SIP2-174): SIP2: extend Patron information Response with valid patron password (CQ)
* [SIP2-180](https://issues.folio.org/browse/SIP2-180): SIP2: Patron Information Response respond with fractional amount in BV field
* [SIP2-160](https://issues.folio.org/browse/SIP2-160): SIP2: extend the Fee Paid to enable to pay fee items
* [SIP2-181](https://issues.folio.org/browse/SIP2-181): Patron Information Response fee item summary responds with closed fees
* [SIP2-182](https://issues.folio.org/browse/SIP2-182): Sip2 Needed User permissions not listed
* [SIP2-188](https://issues.folio.org/browse/SIP2-188): SIP2: different BV amounts in Patron Information Response (64) and  Patron Status Response (24)
* [SIP2-194](https://issues.folio.org/browse/SIP2-194): edgs-sip2 Quesnelia 2024 R1 - Vertex update
* [SIP2-192](https://issues.folio.org/browse/SIP2-192): Load configuration on login request
* [SIP2-186](https://issues.folio.org/browse/SIP2-186): SIP2: Reject checkins on dedicated items statuses


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
