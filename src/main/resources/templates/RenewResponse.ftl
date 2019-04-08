<#import "lib.ftl" as lib>
30<#t>
<#-- ok: 1-char, fixed-length required field: 0 or 1 -->
<@lib.ok value=renewResponse.ok/>
<#-- renewal ok: 1-char, fixed-length required field: Y or N -->
<@lib.renewalOk value=renewResponse.renewalOk/>
<#-- magnetic media: 1-char, fixed-length required field: Y or N or U -->
<@lib.magneticMedia value=renewResponse.magneticMedia!""/>
<#-- desensitize: 1-char, fixed-length required field: Y or N or U -->
<@lib.desensitize value=renewResponse.desensitize!""/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=renewResponse.transactionDate/>
<#-- institution id: variable-length required field -->
<@lib.institutionId value=renewResponse.institutionId/>
<#-- patron identifier: variable-length required field -->
<@lib.patronIdentifier value=renewResponse.patronIdentifier/>
<#-- item identifier: variable-length required field -->
<@lib.itemIdentifier value=renewResponse.itemIdentifier/>
<#-- title identifier: variable-length required field -->
<@lib.titleIdentifier value=renewResponse.titleIdentifier/>
<#-- due date: variable-length required field -->
<@lib.dueDate value=renewResponse.dueDate/>
<#--
    fee type: 2-char, fixed-length optional field (01 thru 99)
    The type of fee associated with renewing this item
-->
<@lib.feeType value=renewResponse.feeType!""/>
<#-- security inhibit: 1-char, fixed-length optional field: Y or N -->
<@lib.securityInhibit value=renewResponse.securityInhibit!""/>
<#-- currency type: 3-char, fixed-length optional field -->
<@lib.currencyType value=renewResponse.currencyType!""/>
<#--
    fee amount: variable-length optional field
    The amount of the fee associated with renewing this item
-->
<@lib.feeAmount value=renewResponse.feeAmount!""/>
<#-- media type: 3-char, fixed-length optional field -->
<@lib.mediaType value=renewResponse.mediaType!""/>
<#-- item properties: variable-length optional field -->
<@lib.itemProperties value=renewResponse.itemProperties!""/>
<#--
    transaction id: variable-length optional field
    May be assigned by the ACS when renewing the item involves a fee
-->
<@lib.transactionId value=renewResponse.transactionId!""/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=renewResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=renewResponse.printLine!""/>
