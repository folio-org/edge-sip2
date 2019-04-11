98<#rt>
${ACSStatus.onLineStatus}<#rt>
${ACSStatus.checkinOk}<#rt>
${ACSStatus.checkoutOk}<#rt>
${ACSStatus.acsRenewalPolicy}<#rt>
${ACSStatus.statusUpdateOk}<#rt>
${ACSStatus.offLineOk}<#rt>
${ACSStatus.timeoutPeriod}<#rt>
${ACSStatus.retriesAllowed}<#rt>
${formatDateTime(ACSStatus.dateTimeSync, "yyyyMMdd    HHmmss")}<#t>
${ACSStatus.protocolVersion}|<#rt>
AO${ACSStatus.institutionId}|<#rt>
AM${ACSStatus.libraryName}|<#rt>
BX<@supportedMessages />|<#rt>
AN${ACSStatus.terminalLocation}|<#rt>
AF${ACSStatus.screenMessage}|<#rt>
AG${ACSStatus.printLine}|<#rt>
<#macro supportedMessages>
 ${PackagedSupportedMessages.patronStatusRequest}<#t>
 ${PackagedSupportedMessages.checkOut}<#t>
 ${PackagedSupportedMessages.checkIn}<#t>
 ${PackagedSupportedMessages.blockPatron}<#t>
 ${PackagedSupportedMessages.scAcsStatus}<#t>
 ${PackagedSupportedMessages.requestScAcsResend}<#t>
 ${PackagedSupportedMessages.login}<#t>
 ${PackagedSupportedMessages.patronInformation}<#t>
 ${PackagedSupportedMessages.endPatronSession}<#t>
 ${PackagedSupportedMessages.feePaid}<#t>
 ${PackagedSupportedMessages.itemInformation}<#t>
 ${PackagedSupportedMessages.itemStatusUpdate}<#t>
 ${PackagedSupportedMessages.patronEnable}<#t>
 ${PackagedSupportedMessages.hold}<#t>
 ${PackagedSupportedMessages.renew}<#t>
 ${PackagedSupportedMessages.renewAll}<#t>
</#macro>
