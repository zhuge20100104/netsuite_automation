package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import org.openqa.selenium.Keys

class BankDepositPage {
	private static final String XPATH_ACCOUNT_DROPDOWN = "//span[@id='account_fs']";
	private static final String XPATH_TRANDATE = ".//*[@id='trandate']";
	private static final String XPATH_EXCHANGERATE = "//span[@id='exchangerate_fs']/input";
	private static final String XPATH_MEMO = ".//*[@id='memo']";
	private static final String XPATH_PAYMENT_TAB = ".//*[@id='paymenttxt']";
	private static final String XPATH_FILTER_TRANDATEFROM = ".//*[@id='Transaction_TRANDATEfrom']";
	private static final String XPATH_FILTER_TRANDATETO = ".//*[@id='Transaction_TRANDATEto']";
	private static final String XPATH_SUBMIT_BTN = ".//*[@id='btn_multibutton_submitter']";

	public long createDeposit(def autCN, def uiObj, def paymentId) {
		autCN.navigateTo(CURL.BANK_DEPOSIT_CURL);
		Thread.sleep(2000)

		new DropdownList(autCN, autCN.webDriver, Locator.xpath(XPATH_ACCOUNT_DROPDOWN)).selectItem(uiObj.main.account);
		autCN.setFieldIdentifiedByWithValue(Locator.xpath(XPATH_TRANDATE), uiObj.main.trandate + Keys.TAB);
		Thread.sleep(2000);

		// Set exchange rate is it is not disabled
		HtmlElementHandle exchangeRateFld = autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_EXCHANGERATE));
		String disabledAttr = exchangeRateFld.getAttributeValue("disabled");
		if (!"true".equals(disabledAttr))
			autCN.setFieldIdentifiedByWithValue(Locator.xpath(XPATH_EXCHANGERATE), uiObj.main.exchangerate + Keys.ENTER);
		autCN.setFieldIdentifiedByWithValue(Locator.xpath(XPATH_MEMO), uiObj.main.memo + Keys.ENTER);
		autCN.waitForPageToLoad();

		// Show Payments
		autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_PAYMENT_TAB)).click();
		autCN.waitForPageToLoad();

		// Filter transaction by Transaction Date To
		autCN.executeScript("document.documentElement.scrollTop=500");
		autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_FILTER_TRANDATEFROM)).clear();
		autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_FILTER_TRANDATEFROM)).sendKeys(uiObj.main.transaction_trandate + Keys.ENTER);
		autCN.waitForPageToLoad();
		autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_FILTER_TRANDATETO)).clear();
		autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_FILTER_TRANDATETO)).sendKeys(uiObj.main.transaction_trandate + Keys.ENTER);
		autCN.waitForPageToLoad();

		List<HtmlElementHandle> allPayments = autCN.webDriver.getHtmlElementsByLocator(Locator.xpath(".//*[@id='payment_displayval']"));
		int i = 1;
		for (HtmlElementHandle payment : allPayments) {
			def paymentHref = payment.getAttributeValue("href");
			println paymentHref;
			paymentId.each { internalid ->
				if (paymentHref.endsWith("id=" + internalid)) {
					autCN.webDriver.executeScript("document.getElementById('deposit" + i + "_fs').click()");
					Thread.sleep(2000);
				}
			}
			i++;
		}
		autCN.executeScript("document.documentElement.scrollTop=0");
		autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_SUBMIT_BTN)).click();

		Thread.sleep(10000);
		def internalId = autCN.getParameterValueForFromQueryString("id");
		return Long.parseLong(internalId);
	}
}
