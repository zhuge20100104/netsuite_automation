package com.netsuite.chinalocalization.page.voucher

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.testautomation.html.Locator

class VoucherPrintSetupPage extends PageBaseAdapterCN {
	//FieldId
	private static final String FID_SUBSIDIARY = "subsidiary";

	VoucherPrintSetupPage navigateToPage() {
		def url = context.resolveURL("customscript_sl_cn_voucher_manage", "customdeploy_sl_cn_voucher_manage");
		context.navigateTo(url as String);
		return this;
	}

	void changeSubsidiary(def subsidiary) {
		context.setFieldWithText(FID_SUBSIDIARY, subsidiary);
	}


	void clickSave() {
		context.webDriver.getHtmlElementByLocator(Locator.xpath("/*//*[@id='submitter']")).scrollToView();
		context.webDriver.click(Locator.xpath("/*//*[@id='submitter']"));
		context.webDriver.waitForPageToLoad();
	}

	void addLine(def lineData) {
		EditMachineCN lineSublist = context.withinEditMachine("recmachcustrecord_subsidiary_line");
		lineSublist.setFieldWithText("custrecord_type", lineData.type);
		lineSublist.setFieldWithText("custrecord_transaction_type", lineData.tranType);
		// For employee dropdown, the text might ends with space
		if ((lineData.user as String).endsWith(" "))
			lineSublist.setFieldWithText("custrecord_user", lineData.user);
		else
			lineSublist.setFieldWithText("custrecord_user", lineData.user + " ");
		lineSublist.setFieldWithValue("custrecord_start_date", lineData.startDate);
		if (lineData.endDate)
			lineSublist.setFieldWithValue("custrecord_end_date", lineData.endDate);
		lineSublist.add();
	}

	def getLineCount() {
		return context.withinEditMachine("recmachcustrecord_subsidiary_line").getRowCount();
	}
}
