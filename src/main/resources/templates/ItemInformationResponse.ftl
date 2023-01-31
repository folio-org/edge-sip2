<#import "lib.ftl" as lib>
18<#t>
<#-- circulation Status: 2-char, fixed-length required field: 00 thru 99 -->
<@lib.circulationStatus value=itemInformationResponse.circulationStatus/>
