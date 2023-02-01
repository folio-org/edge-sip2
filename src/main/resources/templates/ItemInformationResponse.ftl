<#import "lib.ftl" as lib>
18<#t>
<#-- circulation Status: 2-char, fixed-length required field: 00 thru 99 -->
<@lib.circulationStatus value=itemInformationResponse.circulationStatus/>
<#-- hold queue length: optional -->
<@lib.holdQueueLength value=itemInformationResponse.holdQueueLength!"" required=false/>
<#-- security Marker: 2-char, fixed-length required field: 00 thru 99-->
<@lib.securityMarker value=itemInformationResponse.securityMarker/>
<#--
    fee type: 2-char, fixed-length optional field (01 thru 99)
    The type of fee associated with checking out this item
-->
<@lib.feeType value=itemInformationResponse.feeType!""/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=itemInformationResponse.transactionDate/>
<#-- due date: 18-char, fixed-length field: YYYYMMDDZZZZHHMMS -->
<@lib.dueDate value=itemInformationResponse.dueDate!"" required=false/>
<#-- recall date: 18-char, fixed-lengt  field: YYYYMMDDZZZZHHMMSS -->
<@lib.recallDate value=itemInformationResponse.recallDate!"" required=false/>
<#-- hold pickup date: 18-char, fixed-length field: YYYYMMDDZZZZHHMMSS -->
<@lib.holdPickupDate value=itemInformationResponse.holdPickupDate!"" required=false/>
<#-- item identifier: variable-length required field -->
<@lib.itemIdentifier value=itemInformationResponse.itemIdentifier/>
<#-- title identifier: variable-length optional field -->
<@lib.titleIdentifier value=itemInformationResponse.titleIdentifier!""/>
<#-- owner: variable-length field -->
<@lib.owner value=itemInformationResponse.owner!"" required=false/>
<#-- currency type: variable-length required field -->
<@lib.currencyType value=itemInformationResponse.currencyType!"USD"/>
<#-- fee amount: variable-length required field -->
<@lib.feeAmount value=itemInformationResponse.feeAmount!""/>
<#-- media type: 3-char, fixed-length optional field -->
<@lib.mediaType value=itemInformationResponse.mediaType!""/>
<#-- permanent location: variable-length required field -->
<@lib.permanentLocation value=itemInformationResponse.permanentLocation!""/>
<#-- current location: variable-length required field -->
<@lib.currentLocation value=itemInformationResponse.currentLocation!""/>
<#-- item properties: variable-length optional field -->
<@lib.itemProperties value=itemInformationResponse.itemProperties!""/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage Message value=itemInformationResponse.screenMessage!""/>
<#-- print Line: variable-length optional field -->
<@lib.printLine value=itemInformationResponse.printLine!""/>
