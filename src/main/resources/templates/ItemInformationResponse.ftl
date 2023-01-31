<#import "lib.ftl" as lib>
18<#t>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=itemInformationResponse.transactionDate/>
