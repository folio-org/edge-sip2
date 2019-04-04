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

<#macro mapMediaType value>
  CK<#t>
  <#switch value>
    <#case "OTHER">
      001<#t>
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
      001<#t>
  </#switch>
  ${delimiter}<#t>
</#macro>