package com.netsuite.chinalocalization.page.Setup

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator

class AccountPreferencePage extends PageBaseAdapterCN {
	private static String URL = "/app/setup/acctsetup.nl?";
	private static final String FIELD_ID_REVERSALVOIDING = "REVERSALVOIDING"

	def navigateToURL() {
		context.navigateTo(URL)
	}

	def getAccountNumbers() {
		return context.getFieldValue("ACCOUNTNUMBERS");
	}

	def getReversalVoiding() {
		return context.getFieldValue(FIELD_ID_REVERSALVOIDING);
	}

	def setAccountNumbers(String flag) {
		if(!flag.equals(getAccountNumbers())){
		   context.setFieldWithValue("ACCOUNTNUMBERS", flag);
		   clickSave()
		}
	}

	def clickSave() {
		context.webDriver.getHtmlElementByLocator(Locator.xpath("/*//*[@id='submitter']")).scrollToView();
		context.webDriver.click(Locator.xpath("/*//*[@id='submitter']"));
		context.webDriver.waitForPageToLoad();
	}

	def enableReversalVoiding(){
		if(getReversalVoiding()=="F") {
			context.checkNlsinglecheckbox(FIELD_ID_REVERSALVOIDING)
			context.clickSaveByAPI()
		}
	}

	def disbleReversalVoiding(){
		if(getReversalVoiding()=="T") {
			context.unCheckNlsinglecheckbox(FIELD_ID_REVERSALVOIDING)
			context.clickSaveByAPI()
		}
	}
}
