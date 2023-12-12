<#import "lib.ftl" as lib>
10<#t>
<#-- ok: 1-char, fixed-length required field: 0 or 1 -->
<@lib.ok value=checkinResponse.ok/>
<#-- resensitize: 1-char, fixed-length required field: Y or N -->
<@lib.resensitize value=checkinResponse.resensitize/>
<#-- magnetic media: 1-char, fixed-length required field: Y or N or U -->
<@lib.magneticMedia value=checkinResponse.magneticMedia!""/>
<#-- alert: 1-char, fixed-length required field: Y or N -->
<@lib.alert value=checkinResponse.alert/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=checkinResponse.transactionDate/>
<#-- institution id: variable-length required field -->
<@lib.institutionId value=checkinResponse.institutionId/>
<#-- item identifier: variable-length required field -->
<@lib.itemIdentifier value=checkinResponse.itemIdentifier/>
<#-- permanent location: variable-length required field -->
<@lib.permanentLocation value=checkinResponse.permanentLocation/>
<#-- title identifier: variable-length optional field -->
<@lib.titleIdentifier value=checkinResponse.titleIdentifier!"" required=false/>
<#-- sort bin: variable-length optional field -->
<@lib.sortBin value=checkinResponse.sortBin!""/>
<#--
    patron identifier: variable-length optional field
    ID of the patron who had the item checked out
-->
<@lib.patronIdentifier
    value=checkinResponse.patronIdentifier!""
    required=false/>
<#-- media type: 3-char, fixed-length optional field -->
<@lib.mediaType value=checkinResponse.mediaType!""/>
<#-- item properties: variable-length optional field -->
<@lib.itemProperties value=checkinResponse.itemProperties!""/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=checkinResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=checkinResponse.printLine!""/>
<#-- call number: variable-length optional field (extension) -->
<@lib.callNumber value=checkinResponse.callNumber!""/>
<#-- alert type: fixed-length optional field (extension) -->
<@lib.alertType value=checkinResponse.alertType!""/>
<#-- pickup service point: variable-length optional field (extension) -->
<@lib.pickupServicePoint value=checkinResponse.pickupServicePoint!""/>
