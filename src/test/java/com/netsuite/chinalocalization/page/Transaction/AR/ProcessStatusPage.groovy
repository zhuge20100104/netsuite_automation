package com.netsuite.chinalocalization.page.Transaction.AR

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.chinalocalization.page.TransactionBasePage
import com.netsuite.testautomation.html.Locator

/**
 * @Description Page object for Process Status page: on 'Invoice Sales Order' page, click [Submit].
 * @Author lisha.hao@oracle.com
 */
class ProcessStatusPage extends PageBaseAdapterCN{
    private static String TITLE = "Process Status"

    //XPATH
    // 'Process Status' page
    protected static final String XPATH_SUBMITID = "/html/body/div[1]/div[2]/form[2]/div[3]/table/tbody/tr[2]/td[1]"
    protected static final String XPATH_SUBMITSTATUS = "/html/body/div[1]/div[2]/form[2]/div[3]/table/tbody/tr[2]/td[3]/a"
    protected static final String XPATH_PERCENTCOMPLETE = "/html/body/div[1]/div[2]/form[2]/div[3]/table/tbody/tr[2]/td[4]"
    protected static final String XPATH_MESSAGE = "/html/body/div[1]/div[2]/form[2]/div[3]/table/tbody/tr[2]/td[5]/a"

    def getTextSubmitId() {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_SUBMITID)).getAsText()
    }

    // 'Submit status'
    def getSubmiStatusonProcessStatus() {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_SUBMITSTATUS))
    }
    // 'Message': 0 Errors, 1 Error
    def getMessageonProcessStatus() {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_MESSAGE))
    }

    def getTextPercentComplete() {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_PERCENTCOMPLETE)).getAsText()
    }

    def getTextSubmiStatusonProcessStatus() {
        return getSubmiStatusonProcessStatus().getAsText()
    }
    def getTextMessageonProcessStatus() {
        return getMessageonProcessStatus().getAsText()
    }

    def clickMessageonProcessStatus() {
        return getMessageonProcessStatus().click()
    }
    def clickRefesh() {
        context.withinHtmlElementIdentifiedBy(Locator.id("refresh")).click()
    }
}