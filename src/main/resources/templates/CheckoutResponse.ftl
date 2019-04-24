<#import "lib.ftl" as lib>
12<#t>
<#-- ok: 1-char, fixed-length required field: 0 or 1 -->
<@lib.ok value=checkoutResponse.ok/>
<#-- renewal ok: 1-char, fixed-length required field: Y or N -->
<@lib.renewalOk value=checkoutResponse.renewalOk/>
<#-- magnetic media: 1-char, fixed-length required field: Y or N or U -->
<@lib.magneticMedia value=checkoutResponse.magneticMedia!""/>
<#-- desensitize: 1-char, fixed-length required field: Y or N or U -->
<@lib.desensitize value=checkoutResponse.desensitize!""/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=checkoutResponse.transactionDate/>
<#-- institution id: variable-length required field -->
<@lib.institutionId value=checkoutResponse.institutionId/>
<#-- patron identifier: variable-length required field -->
<@lib.patronIdentifier value=checkoutResponse.patronIdentifier/>
<#-- item identifier: variable-length required field -->
<@lib.itemIdentifier value=checkoutResponse.itemIdentifier/>
<#-- title identifier: variable-length required field -->
<@lib.titleIdentifier value=checkoutResponse.titleIdentifier/>
<#-- due date: variable-length required field -->
<@lib.dueDate value=checkoutResponse.dueDate!""/>
<#--
    fee type: 2-char, fixed-length optional field (01 thru 99)
    The type of fee associated with checking out this item
-->
<@lib.feeType value=checkoutResponse.feeType!""/>
<#-- security inhibit: 1-char, fixed-length optional field: Y or N -->
<@lib.securityInhibit value=checkoutResponse.securityInhibit!""/>
<#-- currency type: 3-char, fixed-length optional field -->
<@lib.currencyType value=checkoutResponse.currencyType!""/>
<#--
    fee amount: variable-length optional field
    The amount of the fee associated with checking out this item
-->
<@lib.feeAmount value=checkoutResponse.feeAmount!""/>
<#-- media type: 3-char, fixed-length optional field -->
<@lib.mediaType value=checkoutResponse.mediaType!""/>
<#-- item properties: variable-length optional field -->
<@lib.itemProperties value=checkoutResponse.itemProperties!""/>
<#--
    transaction id: variable-length optional field
    May be assigned by the ACS when checking out the item involves a fee
-->
<@lib.transactionId value=checkoutResponse.transactionId!""/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=checkoutResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=checkoutResponse.printLine!""/>
