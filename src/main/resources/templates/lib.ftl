<#-- General purpose operations -->
<#macro booleanToYorN value>
  <#if value == true>
    Y<#t>
  <#else>
    N<#t>
  </#if>
</#macro>

<#macro booleanToYorNorU value>
  <#if value?has_content>
    <@booleanToYorN value=value/>
  <#else>
    U<#t>
  </#if>
</#macro>

<#macro booleanTo1or0 value>
  <#if value == true>
    1<#t>
  <#else>
    0<#t>
  </#if>
</#macro>

<#macro fixedLengthField id value length>
  ${id}${value[0..*length]?replace(delimiter, " ")}${delimiter}<#t>
</#macro>

<#macro variableLengthField id value>
  ${id}${value[0..*255]?replace(delimiter, " ")}${delimiter}<#t>
</#macro>

<#macro variableLengthRepeatableField id value>
  <#list value?matches('.{1,${maxLength}}', 's') as chunk>
    ${id}${chunk?replace(delimiter, " ")}${delimiter}<#t>
  </#list>
</#macro>

<#macro variableLengthDateField id value>
  ${id}${formatDateTime(value, "yyyyMMdd    HHmmss")}${delimiter}<#t>
</#macro>

<#-- Command macros that are mapped directly to a field name -->

<#macro alert value>
  <@booleanTo1or0 value=value/><#t>
</#macro>

<#macro currencyType value>
  <#if value?has_content>
    <#--
        This should be a 1-to-1 map since the currencyType enums are named
        the same as ISO 4217, which is what SIP uses.
    -->
    BH${value}${delimiter}<#t>
  </#if>
</#macro>

<#macro desensitize value>
  <@booleanToYorNorU value=value/><#t>
</#macro>

<#macro dueDate value>
  <@variableLengthDateField id="AH" value=value/>
</#macro>

<#macro feeAmount value>
  <#if value?has_content>
    <@variableLengthField id="BV" value=value/>
  </#if>
</#macro>

<#macro feeType value>
  <#if value?has_content>
    BT<#t>
    <#switch value>
      <#case "OTHER_UNKNOWN">
        01<#t>
        <#break>
      <#case "ADMINISTRATIVE">
        02<#t>
        <#break>
      <#case "DAMAGE">
        03<#t>
        <#break>
      <#case "OVERDUE">
        04<#t>
        <#break>
      <#case "PROCESSING">
        05<#t>
        <#break>
      <#case "RENTAL">
        06<#t>
        <#break>
      <#case "REPLACEMENT">
        07<#t>
        <#break>
      <#case "COMPUTER_ACCESS_CHARGE">
        08<#t>
        <#break>
      <#case "HOLD_FEE">
        09<#t>
        <#break>
      <#default>
        01<#t>
    </#switch>
    ${delimiter}<#t>
  </#if>
</#macro>

<#macro institutionId value>
  <@variableLengthField id="AO" value=value/>
</#macro>

<#macro itemIdentifier value>
  <@variableLengthField id="AB" value=value/>
</#macro>

<#macro itemProperties value>
  <#if value?has_content>
    <@variableLengthField id="CH" value=value/>
  </#if>
</#macro>

<#macro magneticMedia value>
  <@booleanToYorNorU value=value/><#t>
</#macro>

<#macro mediaType value>
  <#if value?has_content>
    CK<#t>
    <#switch value>
      <#case "OTHER">
        000<#t>
        <#break>
      <#case "BOOK">
        001<#t>
        <#break>
      <#case "MAGAZINE">
        002<#t>
        <#break>
      <#case "BOUND_JOURNAL">
        003<#t>
        <#break>
      <#case "AUDIO_TAPE">
        004<#t>
        <#break>
      <#case "VIDEO_TAPE">
        004<#t>
        <#break>
      <#case "CD_CDROM">
        006<#t>
        <#break>
      <#case "DISKETTE">
        007<#t>
        <#break>
      <#case "BOOK_WITH_DISKETTE">
        008<#t>
        <#break>
      <#case "BOOK_WITH_CD">
        009<#t>
        <#break>
      <#case "BOOK_WITH_AUDIO_TAPE">
        010<#t>
        <#break>
      <#default>
        000<#t>
    </#switch>
    ${delimiter}<#t>
  </#if>
</#macro>

<#macro ok value>
  <@booleanTo1or0 value=value/><#t>
</#macro>

<#macro patronIdentifier value required=true>
  <#if required || value?has_content>
    <@variableLengthField id="AA" value=value/>
  </#if>
</#macro>

<#macro permanentLocation value>
  <@variableLengthField id="AQ" value=value/>
</#macro>

<#macro printLine value>
  <#if value?has_content>
    <@variableLengthRepeatableField id="AG" value=value/>
  </#if>
</#macro>

<#macro renewalOk value>
  <@booleanToYorN value=value/><#t>
</#macro>

<#macro resensitize value>
  <@booleanToYorN value=value/><#t>
</#macro>

<#macro screenMessage value>
  <#if value?has_content>
    <@variableLengthRepeatableField id="AF" value=value/>
  </#if>
</#macro>

<#macro securityInhibit value>
  <#if value?has_content>
    CI<@booleanToYorN value=value/>${delimiter}<#t>
  </#if>
</#macro>

<#macro sortBin value>
  <#if value?has_content>
    <@variableLengthField id="CL" value=value/>
  </#if>
</#macro>

<#macro titleIdentifier value required=true>
  <#if required || value?has_content>
    <@variableLengthField id="AJ" value=value/>
  </#if>
</#macro>

<#macro transactionDate value>
  ${formatDateTime(value, "yyyyMMdd    HHmmss")}<#t>
</#macro>

<#macro transactionId value>
  <#if value?has_content>
    <@variableLengthField id="BK" value=value/>
  </#if>
</#macro>
