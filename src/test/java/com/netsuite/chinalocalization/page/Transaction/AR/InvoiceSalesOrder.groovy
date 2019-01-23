package com.netsuite.chinalocalization.page.Transaction.AR

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator

/**
 * @Description Page object for Sales Order page: 'Transactions->Sales->Invoice Sales Order'.
 * @Author lisha.hao@oracle.com
 */
class InvoiceSalesOrder extends PageBaseAdapterCN{
    private static String TITLE = "Invoice Sales Order"
    private static String URL = CURL.INVOICE_SALES_ORDERS_CURL
    // Go to 'Invoice Sales Orders' page

    def navigateTo() {
        context.navigateTo(URL.toString())
        waitForPageToLoad()
    }

    // 'Invoice Sales Order' page
    def selectOrder(String s) {
        context.withinHtmlElementIdentifiedBy(Locator.id("orderautoenter")).sendKeys(s)
        context.withinHtmlElementIdentifiedBy(Locator.id("secondarysubmitter")).click()
        waitForPageToLoad()
    }
}
