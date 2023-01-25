<#import "lib.ftl" as lib>
18<#t>
<#-- circulationStatus: 2-char, fixed-length required field: 00 thru 99 -->
<@lib.circulationStatus value=itemInformationResponse.circulationStatus/>
<#-- securityMarker: 2-char, fixed-length required field: 00 thru 99-->
<@lib.securityMarker value=itemInformationResponse.securityMarker/>
<#-- securityMarker: 2-char, fixed-length required field: 01 thru 99-->
<@lib.feeType value=itemInformationResponse.feeType!"OTHER_UNKNOWN"/>
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
<@lib.owner value=itemInformationResponse.owner!""/>
<#-- currency type: variable-length required field -->
<@lib.currencyType value=itemInformationResponse.currencyType!"USD"/>
<#-- media type: 3-char, fixed-length optional field -->
<@lib.mediaType value=itemInformationResponse.mediaType!""/>
<#-- permanent location: variable-length required field -->
<@lib.permanentLocation value=itemInformationResponse.permanentLocation!""/>
<#-- current location: variable-length required field -->
<@lib.currentLocation value=itemInformationResponse.currentLocation!""/>
<#-- item properties: variable-length optional field -->
<@lib.itemProperties value=itemInformationResponse.itemProperties!""/>
<#-- destinationInstitutionId: variable-length optional field -->
<@lib.destinationInstitutionId value=itemInformationResponse.destinationInstitutionId!""/>
<#-- hold patron id: variable-length optional field -->
<@lib.holdPatronId value=itemInformationResponse.holdPatronId!""/>
<#-- hold patron name: variable-length optional field -->
<@lib.holdPatronName value=itemInformationResponse.holdPatronName!""/>
<#-- materialType: variable-length optional field -->
<@lib.materialType value=itemInformationResponse.materialType!"" required=false/>
<#-- author: variable-length optional field -->
<@lib.author value=itemInformationResponse.author!"" required=false/>
<#-- summary: variable-length optional field -->
<@lib.summary value=itemInformationResponse.summary!"" required=false/>
<#-- isbn: variable-length optional field -->
<@lib.isbn value=itemInformationResponse.isbn!"" required=false/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=itemInformationResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=itemInformationResponse.printLine!""/>
