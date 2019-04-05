<#import "lib.ftl" as lib>
10<#t>
<#-- patron status: 14-char, fixed-length required field -->
<@lib.patronStatus value=patronInformationResponse.patronStatus/>
<#-- language: 3-char, fixed-length required field -->
<@lib.language value=patronInformationResponse.language/>
<#--
    transaction date: 18-char, fixed-length required field: YYYYMMDDZZZZHHMMSS
-->
<@lib.transactionDate value=patronInformationResponse.transactionDate/>
<#-- hold items count: 4-char, fixed-length required field -->
<@lib.holdItemsCount value=patronInformationResponse.holdItemsCount/>
<#-- overdue items count: 4-char, fixed-length required field -->
<@lib.overdueItemsCount value=patronInformationResponse.overdueItemsCount/>
<#-- charged items count: 4-char, fixed-length required field -->
<@lib.chargedItemsCount value=patronInformationResponse.chargedItemsCount/>
<#-- fine items count: 4-char, fixed-length required field -->
<@lib.fineItemsCount value=patronInformationResponse.fineItemsCount/>
<#-- recall items count: 4-char, fixed-length required field -->
<@lib.recallItemsCount value=patronInformationResponse.recallItemsCount/>
<#-- unavailable items count: 4-char, fixed-length required field -->
<@lib.unavailableHoldsCount
    value=patronInformationResponse.unavailableHoldsCount/>
<#-- institution id: variable-length required field -->
<@lib.institutionId value=patronInformationResponse.institutionId/>
<#-- patron identifier: variable-length required field -->
<@lib.patronIdentifier value=patronInformationResponse.patronIdentifier/>
<#-- personal name: variable-length required field -->
<@lib.personalName value=patronInformationResponse.personalName/>
<#-- hold items limit: 4-char, fixed-length optional field -->
<@lib.holdItemsLimit value=patronInformationResponse.holdItemsLimit!""/>
<#-- overdue items limit: 4-char, fixed-length optional field -->
<@lib.overdueItemsLimit value=patronInformationResponse.overdueItemsLimit!""/>
<#-- charged items limit: 4-char, fixed-length optional field -->
<@lib.chargedItemsLimit value=patronInformationResponse.chargedItemsLimit!""/>
<#-- valid patron: 1-char, optional field -->
<@lib.validPatron value=patronInformationResponse.validPatron!""/>
<#-- valid patron password: 1-char, optional field -->
<@lib.validPatronPassword
    value=patronInformationResponse.validPatronPassword!""/>
<#-- currency type: 3-char, fixed-length optional field -->
<@lib.currencyType value=patronInformationResponse.currencyType!""/>
<#-- fee amount: variable-length optional field -->
<@lib.feeAmount value=patronInformationResponse.feeAmount!""/>
<#-- fee limit: variable-length optional field -->
<@lib.feeLimit value=patronInformationResponse.feeLimit!""/>
<#-- hold items: variable-length optional field (per hold item) -->
<@lib.holdItems value=patronInformationResponse.holdItems!""/>
<#-- overdue items: variable-length optional field (per overdue item) -->
<@lib.overdueItems value=patronInformationResponse.overdueItems!""/>
<#-- charged items: variable-length optional field (per charged item) -->
<@lib.chargedItems value=patronInformationResponse.chargedItems!""/>
<#-- fine items: variable-length optional field (per fine item) -->
<@lib.fineItems value=patronInformationResponse.fineItems!""/>
<#-- recall items: variable-length optional field (per recall item) -->
<@lib.recallItems value=patronInformationResponse.recallItems!""/>
<#--
    unavailable hold items: variable-length optional field (per unavailable
        hold item)
-->
<@lib.unavailableHoldItems
    value=patronInformationResponse.unavailableHoldItems!""/>
<#-- home address: variable-length optional field -->
<@lib.homeAddress value=patronInformationResponse.homeAddress!""/>
<#-- e-mail address: variable-length optional field -->
<@lib.emailAddress value=patronInformationResponse.emailAddress!""/>
<#-- home phone number message: variable-length optional field -->
<@lib.homePhoneNumber value=patronInformationResponse.homePhoneNumber!""/>
<#-- screen message: variable-length optional field -->
<@lib.screenMessage value=patronInformationResponse.screenMessage!""/>
<#-- screen message: variable-length optional field -->
<@lib.printLine value=patronInformationResponse.printLine!""/>
