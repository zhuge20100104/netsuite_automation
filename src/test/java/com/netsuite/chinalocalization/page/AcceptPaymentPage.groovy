package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.testautomation.aut.pageobjects.NetSuiteDataTable
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import org.openqa.selenium.Keys

class AcceptPaymentPage {
    private static final String XPATH_ACCOUNT = ".//*[@id='undepfunds_fs_inp'][@class='radio'][@value='F']";
    private static final String XPATH_TRANDATE = ".//*[@id='trandate']"
    private static final String XPATH_TRANDATE_FROM = ".//*[@id='Transaction_TRANDATEfrom']";
    private static final String XPATH_TRANDATE_TO = ".//*[@id='Transaction_TRANDATEto']";
    private static final String XPATH_SUBMIT = ".//*[@id='btn_secondarymultibutton_submitter']"
    private static final String XPATH_APPLY = ".//*[@id='applytxt']";
    private static final String XPATH_CREDIT = ".//*[@id='credittxt']";
    private static final String XPATH_DEPOSIT = ".//*[@id='deposittxt']";
    private static final String XPATH_LINE_MACHINE = "//*[@id=\"apply_splits\"]"

    def acceptCustomerPayment(def autCN, def caseObj, def uidata, def response) {
        autCN.navigateTo(CURL.CUSTOMER_PAYMENT_CURL);
        autCN.waitForPageToLoad();

        String entityId = Utility.getEntityId(autCN, "customer", uidata.main.customer);
        autCN.setFieldWithValue("customer", entityId);
        autCN.waitForPageToLoad();

        autCN.setFieldWithText("aracct", uidata.main.aracct);
        autCN.waitForPageToLoad();

        //Set bank account
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_ACCOUNT)).scrollToView();
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_ACCOUNT)).click();
        autCN.setFieldWithText("account", uidata.main.account);
        autCN.waitForPageToLoad();

        //Set Transaction Date
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_TRANDATE)).clear();
        autCN.waitForPageToLoad();
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_TRANDATE)).sendKeys(uidata.main.trandate);
        autCN.setFieldWithText("postingperiod", uidata.main.postingperiod);

        autCN.waitForPageToLoad(); //posting period will be refreshed according to transdate

        //Filter transaction by Transaction Date From/To
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_TRANDATE_FROM)).clear();
        autCN.waitForPageToLoad();
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_TRANDATE_TO)).clear();
        autCN.waitForPageToLoad();
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_TRANDATE_FROM)).sendKeys(uidata.main.transaction_trandatefrom + Keys.ENTER);
        autCN.waitForPageToLoad();
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_TRANDATE_TO)).sendKeys(uidata.main.transaction_trandate + Keys.ENTER);
        autCN.waitForPageToLoad();

        //Check apply list and amount
        uidata.apply.each { transaction ->
            //transaction.refid
            def trantype = autCN.getRecordTrantype(caseObj, transaction.refid);
            def tranId = autCN.getTranidForRecordFromRefid(caseObj, transaction.refid, response);

            String checkBoxXpath = "";
            String amountXpath = "";
            if (trantype == "invoice") {
                checkBoxXpath = ".//*[@id='apply_div']/table[@id='apply_splits']/*/tr/td[text()='" + tranId + "']/../td[span[@class='checkbox_unck']]/span";
                amountXpath = ".//*[@id='apply_div']/table[@id='apply_splits']/*/tr/td[text()='" + tranId + "']/../td[span[contains(@id, 'apply_amount')]]/span/input";
                autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_APPLY)).scrollToView();
                autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_APPLY)).click();

            } else if (trantype == "creditmemo") {
                checkBoxXpath = ".//*[@id='credit_div']/table[@id='credit_splits']/*/tr/td[text()='" + tranId + "']/../td[span[@class='checkbox_unck']]/span";
                amountXpath = ".//*[@id='credit_div']/table[@id='credit_splits']/*/tr/td[text()='" + tranId + "']/../td[span[contains(@id, 'credit_amount')]]/span/input";
                autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_CREDIT)).scrollToView();
                autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_CREDIT)).click();
            } else if (trantype == "customerdeposit") {
                checkBoxXpath = ".//*[@id='deposit_div']/table[@id='deposit_splits']/*/tr/td[text()='" + tranId + "']/../td[span[@class='checkbox_unck']]/span";
                amountXpath = ".//*[@id='deposit_div']/table[@id='deposit_splits']/*/tr/td[text()='" + tranId + "']/../td[span[contains(@id, 'deposit_amount')]]/span/input";
                autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_DEPOSIT)).scrollToView();
                autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_DEPOSIT)).click();
            }

            //Check corresponding checkbox
            autCN.webDriver.getHtmlElementByLocator(Locator.xpath(checkBoxXpath)).scrollToView();
            autCN.webDriver.getHtmlElementByLocator(Locator.xpath(checkBoxXpath)).click();
            //Set payment amount
            autCN.webDriver.getHtmlElementByLocator(Locator.xpath(amountXpath)).clear();
            autCN.webDriver.getHtmlElementByLocator(Locator.xpath(amountXpath)).sendKeys(transaction.amount + Keys.ENTER);
            autCN.waitForPageToLoad();
        }

        //Submit changes
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_SUBMIT)).scrollToView();
        autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_SUBMIT)).click();
        autCN.waitForPageToLoad();

        //last step
        //Get payment id
        def internlId = autCN.getParameterValueForFromQueryString("id");
        return [internalid: internlId, trantype: "customerpayment"];
    }

    def getLineDataTable(def autCN){
        HtmlElementHandle table = autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_LINE_MACHINE));
        HtmlElementHandle headerContainer = autCN.webDriver.getHtmlElementByLocator(Locator.xpath(String.format("(%s)//td[contains(@class,'listheader')]/..", table.getXPath())));

        return new NetSuiteDataTable(autCN, autCN.webDriver, headerContainer, table, table.getLocator());
    }
}
