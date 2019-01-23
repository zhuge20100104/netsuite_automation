package com.netsuite.chinalocalization.voucher

import com.google.inject.Inject
import com.netsuite.chinalocalization.page.Setup.GeneralPreferencePage
import com.netsuite.chinalocalization.page.voucher.VoucherPrintSetupPage
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Test

class VoucherPrintMaxDropDownSizeTest extends VoucherPrintBaseTest {
	@Inject
	GeneralPreferencePage preferencePage;
	@Inject
	VoucherPrintSetupPage setupPage;

	int maxDropDownSize;
	def lineDataObj;

	/**
	 * Only Administrator can do Voucher Print Setup.
	 */
	def getDefaultRole() {
		return getAdministrator()
	}

	@After
	public void afterTest() {
		// Delete the newly created records
		if (lineDataObj)
			deleteVoucherSetupRecord(lineDataObj);
		// Revert preference
		preferencePage.navigateToURL();
		preferencePage.setMaxDropDownSize(maxDropDownSize, true);
	}

	/**
	 * @Author kim.shi@oracle.com
	 * @CaseID Voucher 1.3.41
	 * @desc When MAXDROPDOWNSIZE is set to a small value, the dropdown component type will change and Voucher Setup UI will throw exception when trying to save.
	 */
	@Test
	public void case_1_3_41() {
		// Update preference setting
		preferencePage.navigateToURL();
		maxDropDownSize = Integer.parseInt(preferencePage.getMaxDropDownSize());
		preferencePage.setMaxDropDownSize(5, true);

		// Navigate to Setup UI and do the test
		setupPage.navigateToPage();
		setupPage.changeSubsidiary("Parent Company : China BU")
		setupPage.context.webDriver.waitForPageToLoad();

		int lineCountBeforeAdd = setupPage.getLineCount();
		def lineData = getLineData();
		lineDataObj = new JsonSlurper().parseText(lineData);
		setupPage.addLine(lineDataObj);
		setupPage.clickSave();

		aut.waitForPageToLoad();

		def lineCountAfterAdd = setupPage.getLineCount();
		assertAreEqual("Line should be added without error", lineCountBeforeAdd, lineCountAfterAdd - 1);
	}

	def getLineData() {
		if (isLanguageEnglish(targetLanguage()))
			return "{\"type\":\"Creator\",\"tranType\":\"Bill\",\"user\":\"auto1.01\",\"startDate\":\"3/26/2018\",\"endDate\":\"\"}";
		else
			return "{\"type\":\"记账人\",\"tranType\":\"账单\",\"user\":\"auto1.01\",\"startDate\":\"3/26/2018\",\"endDate\":\"\"}";
	}

	/**
	 * Build up script to delete the Voucher Setup data with given information.
	 * @param lineDataObj
	 */
	def deleteVoucherSetupRecord(def lineDataObj) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("var lineCount = nlapiGetLineItemCount('recmachcustrecord_subsidiary_line');")
				.append("for(var index=1; index<=lineCount; index++){")
				.append("	var lineType = nlapiGetLineItemText('recmachcustrecord_subsidiary_line','custrecord_type', index);")
				.append("	var lineTranType = nlapiGetLineItemText('recmachcustrecord_subsidiary_line','custrecord_transaction_type', index);")
				.append("	var lineUser = nlapiGetLineItemText('recmachcustrecord_subsidiary_line','custrecord_user', index);")
				.append("	var lineStartDate = nlapiGetLineItemValue('recmachcustrecord_subsidiary_line','custrecord_start_date', index);")
				.append("	var lineEndDate = nlapiGetLineItemValue('recmachcustrecord_subsidiary_line','custrecord_end_date', index);")
				.append("	if(lineType != '" + lineDataObj.type + "') continue;")
				.append("	if(lineTranType != '" + lineDataObj.tranType + "') continue;")
				.append("	if(lineUser != '" + lineDataObj.user + " ') continue;")
				.append("	if(lineStartDate != '" + lineDataObj.startDate + "') continue;")
				.append("	if(lineEndDate != '" + lineDataObj.endDate + "') continue;")
		// Match found and need to be deleted
				.append("	var internalid = nlapiGetLineItemValue('recmachcustrecord_subsidiary_line','custrecord_id_hidden', index);")
				.append("	try {")
				.append("		nlapiDeleteRecord('customrecord_cn_voucher_manage_data',internalid);")
				.append("	}catch(e){nlapiLogExecution('DEBUG','Deletion Error',e)}")
				.append("}");
		context.executeScript(sBuilder.toString());
	}

}
