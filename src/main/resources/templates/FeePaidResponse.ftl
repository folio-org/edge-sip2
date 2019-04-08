<#import "lib.ftl" as lib>
38<#t>
<#-- payment accepted: 1-char, fixed-length required field: Y or N -->
<@lib.paymentAccepted value=feePaidResponse.paymentAccepted/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=feePaidResponse.transactionDate/>
<#-- institution id: variable-length required field -->
<@lib.institutionId value=feePaidResponse.institutionId/>
<#-- patron identifier: variable-length required field -->
<@lib.patronIdentifier value=feePaidResponse.patronIdentifier/>
<#--
    transaction id: variable-length optional field
    May be assigned by the ACS to acknowledge that the payment was received
-->
<@lib.transactionId value=feePaidResponse.transactionId!""/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=feePaidResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=feePaidResponse.printLine!""/>
