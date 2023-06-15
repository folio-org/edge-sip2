<#import "lib.ftl" as lib>
24<#t>
<#-- patron status: 14-char, fixed-length required field -->
<@lib.patronStatus value=patronStatusResponse.patronStatus/>
<#-- language: 3-char, fixed-length required field -->
<@lib.language value=patronStatusResponse.language/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=patronStatusResponse.transactionDate/>
<#-- institution id: variable-length required field -->
<@lib.institutionId value=patronStatusResponse.institutionId/>
<#-- patron identifier: variable-length required field -->
<@lib.patronIdentifier value=patronStatusResponse.patronIdentifier/>
<#-- personal name: variable-length required field -->
<@lib.personalName value=patronStatusResponse.personalName/>
<#-- valid patron: 1-char, optional field -->
<@lib.validPatron value=patronStatusResponse.validPatron!""/>
<#-- valid patron password: 1-char, optional field -->
<@lib.validPatronPassword
    value=patronStatusResponse.validPatronPassword!""/>
<#-- currency type: 3-char, fixed-length optional field -->
<@lib.currencyType value=patronStatusResponse.currencyType!""/>
<#-- fee amount: variable-length optional field -->
<@lib.feeAmount value=patronStatusResponse.feeAmount!""/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=patronStatusResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=patronStatusResponse.printLine!""/>

