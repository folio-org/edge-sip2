<#import "lib.ftl" as lib>
36<#t>
<#-- end session: 1-char, fixed-length required field: Y or N -->
<@lib.endSession value=endSessionResponse.endSession/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=endSessionResponse.transactionDate tz=timezone/>
<#-- institution id: variable-length required field -->
<@lib.institutionId value=endSessionResponse.institutionId/>
<#-- patron identifier: variable-length required field -->
<@lib.patronIdentifier value=endSessionResponse.patronIdentifier/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=endSessionResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=endSessionResponse.printLine!""/>
