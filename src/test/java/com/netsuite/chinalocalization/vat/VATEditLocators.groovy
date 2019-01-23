package com.netsuite.chinalocalization.vat

import groovy.transform.Immutable

@Immutable
class VATEditLocators {
	String editPageTitle = ".//*[@id='main_form']/table/tbody/tr/td/div[1]/div[2]/h1"

	String cancelButton = "//input[@id='custpage_cancel']"
	String saveButton = "//input[@id='custpage_save']"
	String primaryInforHeader = ".//*[@id='custpage_transactions_headertxt']"
	String MergeButton = "//input[@id='merge']"
	String unmergeButton = "//input[@id='unmerge']"

	String tablePrimaryInfoXPath = "//table[@id='custpage_header_sublist_splits']"
	String transRowsIteratorXPath = "//tbody//tr"
	String itemsHeader = ".//*[@id='custpage_item_sublisttxt']"
	String confirmOk =".//*[@class='uir-message-buttons']/button[1]"
	String confirmCancel =".//*[@class='uir-message-buttons']/button[2]"
	String itemList = "custpage_item_sublist"
	String tranList = "custpage_header_sublist"

	String groupSameItemChkbox = "//input[@id='custpage_groupsameitems']"

	// OK button in error message dialog
	String okInErrMsgBox = ".//div[contains(@class, 'uir-message-buttons')]/button"

}
