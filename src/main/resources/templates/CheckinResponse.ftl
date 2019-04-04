<#import "lib.ftl" as lib>
10<#t>
<#-- ok: 1-char, fixed-length required field: 0 or 1 -->
<@lib.booleanTo1or0 value=checkinResponse.ok/>
<#-- resensitize: 1-char, fixed-length required field: Y or N -->
<@lib.booleanToYorN value=checkinResponse.resensitize/>
<#-- magnetic media: 1-char, fixed-length required field: Y or N or U -->
<@lib.booleanToYorNorU value=checkinResponse.magneticMedia!""/>
<#-- alert: 1-char, fixed-length required field: Y or N -->
<@lib.booleanToYorN value=checkinResponse.alert/>
<#--
  transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
${formatDateTime(checkinResponse.transactionDate, "yyyyMMdd    HHmmss")}<#t>
<#-- institution id: variable-length required field -->
<@lib.variableLengthField id="AO" value=checkinResponse.institutionId/>
<#-- item identifier: variable-length required field -->
<@lib.variableLengthField id="AB" value=checkinResponse.itemIdentifier/>
<#-- permanent location: variable-length required field -->
<@lib.variableLengthField id="AQ" value=checkinResponse.permanentLocation/>
<#-- title identifier: variable-length optional field -->
<#if checkinResponse.titleIdentifier??>
  <@lib.variableLengthField id="AJ" value=checkinResponse.titleIdentifier/>
</#if>
<#-- sort bin: variable-length optional field -->
<#if checkinResponse.sortBin??>
  <@variableLengthField id="CL" value=checkinResponse.sortBin/>
</#if>
<#-- patron identifier: variable-length optional field -->
<#if checkinResponse.patronIdentifier??>
  <@lib.variableLengthField id="AA" value=checkinResponse.patronIdentifier/>
</#if>
<#-- media type: 3-char, fixed-length optional field -->
<#if checkinResponse.mediaType??>
  <@lib.mapMediaType value=checkinResponse.mediaType/>
</#if>
<#-- item properties: variable-length optional field -->
<#if checkinResponse.itemProperties??>
  <@lib.variableLengthField id="CH" value=checkinResponse.itemProperties/>
</#if>
<#-- screen message: variable-length optional field -->
<#if checkinResponse.screenMessage??>
  <@lib.variableLengthRepeatableField
      id="AF"
      value=checkinResponse.screenMessage/>
</#if>
<#-- screen message: variable-length optional field -->
<#if checkinResponse.printLine??>
  <@lib.variableLengthRepeatableField id="AG" value=checkinResponse.printLine/>
</#if>
