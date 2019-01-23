package com.netsuite.chinalocalization.balancesheet

import groovy.transform.Immutable

@Immutable
class BalanceSheetLocators {
    String subsidiaryField = "//*[@id=\"custpage_subsidiary_fs\"]"
    String asofField = "//*[@id=\"custpage_asof_fs\"]"
    String unitField = "//*[@id=\"custpage_unit_fs\"]"
    //String unitFieldIndex = "//*[@id=\"indx_custpage_unit3\"]"
    String unitFieldIndex = "//*[contains(@id, 'indx_custpage_unit')]"
    String alertMessage = "//span[@class='uir-message-text']"
    String okButtonInAlertMessage = "//button[@value='true']"
    String tableXPath = "//table[@id='blsheet_data']"
    String headerItemsIteratorXPath = "//tbody//tr//th"
    String rowsIteratorXPath = "//tbody//tr"
    String currencyLable = "//*[@id=\"blsheet_header\"]/tbody/tr[3]/td[3]"
    String excelButton = "//input[@id='custpage_export_excel']"
    String pdfButton = "//input[@id='custpage_export_pdf']"
    String clickRefresh = "//input[@id='custpage_refresh']"
    String commonPageSubsidiaryField = "//*[@id=\"crit_4_fs\"]"
    String commonPageUnitField = "//*[@id=\"crit_3_to_fs\"]"
    String commonPageRefreshButton = "//input[@id='refresh']"
    String commonPageTable = "//table[@id='rptdataarea']"
    String commonPageTableRows = "//tbody//tr"
    String systemNumberFormat = "//*[@id=\"indx_NUMBERFORMAT12\"]"
    String systemNegativeNumberFormat = "//*[@id=\"indx_NEGATIVE_NUMBER_FORMAT13\"]"
}
