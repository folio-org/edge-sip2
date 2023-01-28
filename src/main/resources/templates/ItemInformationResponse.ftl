<#import "lib.ftl" as lib>
18<#t>
<#-- circulationStatus: 2-char, fixed-length required field: 00 thru 99 -->
<@lib.circulationStatus value=itemInformationResponse.circulationStatus/>
<#-- item identifier: variable-length required field -->
<@lib.itemIdentifier value=itemInformationResponse.itemIdentifier/>
