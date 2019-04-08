<#import "lib.ftl" as lib>
66<#t>
<#-- ok: 1-char, fixed-length required field: 0 or 1 -->
<@lib.ok value=renewAllResponse.ok/>
<#-- renewed count: 4-char, fixed-length required field -->
<@lib.renewedCount value=renewAllResponse.renewedCount/>
<#-- unrenewed count: 4-char, fixed-length required field -->
<@lib.unrenewedCount value=renewAllResponse.unrenewedCount/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=renewAllResponse.transactionDate/>
<#-- institution id: variable-length required field -->
<@lib.institutionId value=renewAllResponse.institutionId/>
<#-- renewed items: variable-length optional field (per renewed item) -->
<@lib.renewedItems value=renewAllResponse.renewedItems!""/>
<#-- unrenewed items: variable-length optional field (per unrenewed item) -->
<@lib.unrenewedItems value=renewAllResponse.unrenewedItems!""/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=renewAllResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=renewAllResponse.printLine!""/>
