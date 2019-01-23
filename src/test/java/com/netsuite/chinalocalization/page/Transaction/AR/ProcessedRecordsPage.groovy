package com.netsuite.chinalocalization.page.Transaction.AR

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator

/**
 * @Description Page object for Processed Records page: on 'Process Status' page, click link 'Complete'.
 * @Author lisha.hao@oracle.com
 */
class ProcessedRecordsPage extends PageBaseAdapterCN{
    private static String TITLE = "Processed Records"
    private static StringBuffer URL;
    // XPATH
    protected static final String XPATH_RESULT = "/html/body/div[1]/div[2]/form[2]/div[3]/table/tbody/tr[2]/td[5]"
    protected static final String XPATH_ERRORMSG = "/html/body/div[1]/div[2]/form[2]/div[3]/table/tbody/tr[2]/td[7]"

    def navigateTo(String submitId) {
        URL = new StringBuffer();
        URL.append(CURL.PROCESSEDRECORDS).append("?submissionid=").append(submitId).append("&type=BILLSALESORDERS")
        context.navigateTo(URL.toString())
    }

    // 'Result'
    def getTextRstonProcessRecords() {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_RESULT)).getAsText()
    }
    // 'Error Message'
    def getTextErrorMsgonProcessRecords() {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_ERRORMSG)).getAsText()
    }
}
