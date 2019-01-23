package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.testautomation.driver.BrowserActions
import com.netsuite.testautomation.html.Locator
import com.netsuite.chinalocalization.lib.NetSuiteAppCN;

class StatementChargePage extends TransactionBasePage {
    private static String TITLE="Statement Charge"
    private static String URL=CURL.CUSTOMER_CHARGE_CURL

    private static final String ACTION = ".//*[@class='pgm_action_menu']";
    private static final String DELETE_BTN = ".//a[contains(@href,'delete')]";

    def void deleteStatementCharge(NetSuiteAppCN aut, long internalid) {
        aut.navigateTo(CURL.CUSTOMER_CHARGE_CURL + "?id=" + internalid + "&e=T&whence=");
        aut.waitForPageToLoad();
        BrowserActions actions = aut.webDriver.getBrowserActionsInstance();
        actions.moveToElement(aut.webDriver.getHtmlElementByLocator(Locator.xpath(ACTION))).click().build().perform();
        Thread.sleep(3000);
        actions.moveToElement(aut.webDriver.getHtmlElementByLocator(Locator.xpath(DELETE_BTN))).click().build().perform();
        Thread.sleep(3000);
        aut.webDriver.acceptUpcomingConfirmationDialog();
        String alert = aut.webDriver.getAlertMessage();
        println "alert: " + alert;
    }


    public StatementChargePage(){
        super(TITLE,URL);
    }

    public createStatementCharge(dataObj){
        createTransaction(dataObj)
    }

}
