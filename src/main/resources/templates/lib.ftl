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

<#macro variableLengthRepeatableField id value length>
  <#list value?matches('.{1,${length}}', 's') as chunk>
    ${id}${chunk?replace(delimiter, " ")}${delimiter}<#t>
  </#list>
</#macro>

<#macro variableLengthDateField id value>
  ${id}<#t>
  <#if value?has_content>
    ${formatDateTime(value, "yyyyMMdd    HHmmss", timezone)}<#t>
  </#if>
  ${delimiter}<#t>
</#macro>

<#macro limitNumberToRange value min max length>
  <#if value gt max>
    ${max?c}<#t>
  <#elseif value lt min>
    ${min?c?left_pad(length, "0")}<#t>
  <#else>
    ${value?c?left_pad(length, "0")}<#t>
  </#if>
</#macro>

<#macro limitNumberToRangeOrSpaces value min max length>
  <#if value?has_content>
    <@limitNumberToRange value=value min=min max=max length=length/><#t>
  <#else><#rt>
    ${value?right_pad(length)}<#lt><#rt>
  </#if>
</#macro>

<#macro fixedLengthNumberToRangeField value id min max length>
  <#if value?has_content>
    ${id}<#t>
    <@limitNumberToRange value=value min=min max=max length=length/><#t>
    ${delimiter}<#t>
  </#if>
</#macro>

<#macro variableLengthListField id value>
  <#if value?has_content>
    <#list value as i>
      <@variableLengthField id=id value=i/>
    </#list>
  </#if>
</#macro>

<#macro variableLengthListRepeatableField id value length>
  <#if value?has_content>
    <#list value as i>
      <@variableLengthRepeatableField id=id value=i length=length/>
    </#list>
  </#if>
</#macro>

<#-- Command macros that are mapped directly to a field name -->

<#macro alert value>
  <@booleanToYorN value=value/><#t>
</#macro>

<#macro chargedItems value>
  <@variableLengthListField id="AU" value=value/>
</#macro>

<#macro chargedItemsCount value>
  <@limitNumberToRangeOrSpaces value=value min=0 max=9999 length=4/>
</#macro>

<#macro chargedItemsLimit value>
  <@fixedLengthNumberToRangeField value=value id="CB" min=0 max=9999 length=4/>
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

<#macro dueDate value required=true>
  <#if required || value?has_content>
    <@variableLengthDateField id="AH" value=value/>
  </#if>
</#macro>

<#macro endSession value>
  <@booleanToYorN value=value/><#t>
</#macro>

<#macro emailAddress value>
  <@variableLengthField id="BE" value=value/>
</#macro>

<#macro feeAmount value>
  <#if value?has_content>
    <@variableLengthField id="BV" value=value/>
  </#if>
</#macro>

<#macro feeLimit value>
  <#if value?has_content>
    <@variableLengthField id="CC" value=value/>
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

<#macro circulationStatus value>
  <#if value?has_content>
    <#switch value>
      <#case "OTHER">
        01<#t>
        <#break>
      <#case "ON_ORDER">
        02<#t>
        <#break>
      <#case "AVAILABLE">
        03<#t>
        <#break>
      <#case "CHARGED">
        04<#t>
        <#break>
      <#case "CHARGED_NOT_TO_BE_RECALLED_UNTIL_EARLIEST_RECALL_DATE">
        05<#t>
        <#break>
      <#case "IN_PROCESS">
        06<#t>
        <#break>
      <#case "RECALLED">
        07<#t>
        <#break>
      <#case "WAITING_ON_HOLD_SHELF">
        08<#t>
        <#break>
      <#case "WAITING_TO_BE_RESHELVED">
        09<#t>
        <#break>
      <#case "IN_TRANSIT_BETWEEN_LIBRARY_LOCATIONS">
        10<#t>
        <#break>
      <#case "CLAIMED_RETURNED">
        11<#t>
        <#break>
      <#case "LOST">
        12<#t>
        <#break>
      <#case "MISSING">
        13<#t>
        <#break>
      <#default>
        01<#t>
    </#switch>
  </#if>
</#macro>

<#macro fineItems value>
  <@variableLengthListField id="AV" value=value/>
</#macro>

<#macro fineItemsCount value>
  <@limitNumberToRangeOrSpaces value=value min=0 max=9999 length=4/>
</#macro>

<#macro holdItems value>
  <@variableLengthListField id="AS" value=value/>
</#macro>

<#macro holdItemsCount value>
  <@limitNumberToRangeOrSpaces value=value min=0 max=9999 length=4/>
</#macro>

<#macro holdItemsLimit value>
  <@fixedLengthNumberToRangeField value=value id="BZ" min=0 max=9999 length=4/>
</#macro>

<#macro homeAddress value>
  <#if value?has_content>
    <@variableLengthField id="BD" value=value/>
  </#if>
</#macro>

<#macro homePhoneNumber value>
  <#if value?has_content>
    <@variableLengthField id="BF" value=value/>
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

<#macro language value>
  <#if value?has_content>
    <#switch value>
      <#case "UNKNOWN">
        000<#t>
        <#break>
      <#case "ENGLISH">
        001<#t>
        <#break>
      <#case "FRENCH">
        002<#t>
        <#break>
      <#case "GERMAN">
        003<#t>
        <#break>
      <#case "ITALIAN">
        004<#t>
        <#break>
      <#case "DUTCH">
        005<#t>
        <#break>
      <#case "SWEDISH">
        006<#t>
        <#break>
      <#case "FINNISH">
        007<#t>
        <#break>
      <#case "SPANISH">
        008<#t>
        <#break>
      <#case "DANISH">
        009<#t>
        <#break>
      <#case "PORTUGUESE">
        010<#t>
        <#break>
      <#case "CANADIAN_FRENCH">
        011<#t>
        <#break>
      <#case "NORWEGIAN">
        012<#t>
        <#break>
      <#case "HEBREW">
        013<#t>
        <#break>
      <#case "JAPANESE">
        014<#t>
        <#break>
      <#case "RUSSIAN">
        015<#t>
        <#break>
      <#case "ARABIC">
        016<#t>
        <#break>
      <#case "POLISH">
        017<#t>
        <#break>
      <#case "GREEK">
        018<#t>
        <#break>
      <#case "CHINESE">
        019<#t>
        <#break>
      <#case "KOREAN">
        020<#t>
        <#break>
      <#case "NORTH_AMERICAN_SPANISH">
        021<#t>
        <#break>
      <#case "TAMIL">
        022<#t>
        <#break>
      <#case "MALAY">
        023<#t>
        <#break>
      <#case "UNITED_KINGDOM">
        024<#t>
        <#break>
      <#case "ICELANDIC">
        025<#t>
        <#break>
      <#case "BELGIAN">
        026<#t>
        <#break>
      <#case "TAIWANESE">
        027<#t>
        <#break>
      <#default>
        000<#t>
    </#switch>
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
        005<#t>
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

<#macro overdueItems value>
  <@variableLengthListField id="AT" value=value/>
</#macro>

<#macro overdueItemsCount value>
  <@limitNumberToRangeOrSpaces value=value min=0 max=9999 length=4/>
</#macro>

<#macro overdueItemsLimit value>
  <@fixedLengthNumberToRangeField value=value id="CA" min=0 max=9999 length=4/>
</#macro>

<#macro patronIdentifier value required=true>
  <#if required || value?has_content>
    <@variableLengthField id="AA" value=value/>
  </#if>
</#macro>

<#macro patronStatus value>
  <#assign seq = ['CHARGE_PRIVILEGES_DENIED', 'RENEWAL_PRIVILEGES_DENIED',
      'RECALL_PRIVILEGES_DENIED', 'HOLD_PRIVILEGES_DENIED',
      'CARD_REPORTED_LOST', 'TOO_MANY_ITEMS_CHARGED', 'TOO_MANY_ITEMS_OVERDUE',
      'TOO_MANY_RENEWALS', 'TOO_MANY_CLAIMS_OF_ITEMS_RETURNED',
      'TOO_MANY_ITEMS_LOST', 'EXCESSIVE_OUTSTANDING_FINES',
      'EXCESSIVE_OUTSTANDING_FEES', 'RECALL_OVERDUE', 'TOO_MANY_ITEMS_BILLED']>
  <#list seq as check>
    ${value?seq_contains(check)?string("Y", " ")}<#t>
  </#list>
</#macro>

<#macro paymentAccepted value>
  <@booleanToYorN value=value/><#t>
</#macro>

<#macro permanentLocation value>
  <@variableLengthField id="AQ" value=value/>
</#macro>

<#macro personalName value>
  <@variableLengthField id="AE" value=value/>
</#macro>

<#macro printLine value>
  <#if value?has_content>
    <@variableLengthListRepeatableField id="AG" value=value length="${maxLength!255}"/>
  </#if>
</#macro>

<#macro recallItems value>
  <@variableLengthListField id="BU" value=value/>
</#macro>

<#macro recallItemsCount value>
  <@limitNumberToRangeOrSpaces value=value min=0 max=9999 length=4/>
</#macro>

<#macro renewalOk value>
  <@booleanToYorN value=value/><#t>
</#macro>

<#macro renewedCount value>
  <@limitNumberToRange value=value min=0 max=9999 length=4/>
</#macro>

<#macro renewedItems value>
  <@variableLengthListField id="BM" value=value/>
</#macro>

<#macro resensitize value>
  <@booleanToYorN value=value/><#t>
</#macro>

<#macro screenMessage value>
  <#if value?has_content>
    <@variableLengthListRepeatableField id="AF" value=value length=255/>
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
  ${formatDateTime(value, "yyyyMMdd    HHmmss", timezone)}<#t>
</#macro>

<#macro transactionId value>
  <#if value?has_content>
    <@variableLengthField id="BK" value=value/>
  </#if>
</#macro>

<#macro unavailableHoldItems value>
  <@variableLengthListField id="CD" value=value/>
</#macro>

<#macro unavailableHoldsCount value>
  <@limitNumberToRangeOrSpaces value=value min=0 max=9999 length=4/>
</#macro>

<#macro unrenewedCount value>
  <@limitNumberToRange value=value min=0 max=9999 length=4/>
</#macro>

<#macro unrenewedItems value>
  <@variableLengthListField id="BN" value=value/>
</#macro>

<#macro validPatron value>
  <#if value?has_content>
    BL<@booleanToYorN value=value/>${delimiter}<#t>
  </#if>
</#macro>

<#macro validPatronPassword value>
  <#if value?has_content>
    CQ<@booleanToYorN value=value/>${delimiter}<#t>
  </#if>
</#macro>
