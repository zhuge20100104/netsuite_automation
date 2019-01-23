package com.netsuite.chinalocalization.voucher

import com.google.inject.Inject
import com.netsuite.chinalocalization.page.PurchaseOrderPage
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Description:
 * Verify Voucher print table for Item Receipt Transaction
 * This is a P2 case.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 *
 */
@TestOwner("cuiping.peng@oracle.com")
class VoucherItemReceiptTest extends VoucherP2BaseTest {

    def pageName = "Purchase Order Page"
    def pageURL = "/app/accounting/transactions/purchord.nl?whence="

    PurchaseOrderPage purchaseOrder
    VoucherPrintPage voucherPrintPage

    @Category([P2.class, OW.class])
    @Test
    void case_1_13_1_5() {
        try {
            if (!context.isOneWorld()) {
                return
            }

            if (!getDefaultRole().equals(getAdministrator())) {
                switchToRole(administrator)
            }

            purchaseOrder = new PurchaseOrderPage(["context": context, "currentRecord": currentRecord])

            // Go to Purchase Order page to fill info, and then save this purchase order
            context.navigateTo(purchaseOrder.URL)
            def testData = cData.data("test_1_13_1_5")
            def mainHeader = testData.data().main
            def itemLine = testData.data().item

            def filter = testData.labels()
            def expectedHeader = testData.expect().expectedHeader
            def expectedRow1 = testData.expect().expectedRow[0]
            def expectedRow2 = testData.expect().expectedRow[1]

            purchaseOrder.setHeaderInfo(mainHeader)
            context.waitForPageToLoad()
            purchaseOrder.setSubListInfo("item", itemLine)
            def transDate = context.getFieldValue("trandate")
            purchaseOrder.clickSaveButton()
            context.waitForPageToLoad()

            // Store the created Purchase Order into to be deleted in teardown
            def id = getInternalIDFromURL()
            dirtyData = [
                    [
                            "trantype"  : "purchaseorder",
                            "internalid": "${id}"
                    ]
            ]

            // Receive this purchase order
            purchaseOrder.clickReceiveButton()

            // Fill ans save Item Receipt Page
            context.waitForPageToLoad()
            context.setFieldWithValue("trandate", transDate)
            Thread.sleep(2 * 1000)
            context.withinHtmlElementIdentifiedBy(Locator.xpath(".//*[@id='btn_multibutton_submitter']")).click()

            // Store the generated Item Receipt into to be deleted in teardown
            id = getInternalIDFromURL()
            dirtyData.add([
                    "trantype"  : "itemreceipt",
                    "internalid": "${id}"
            ])

            // Go to Vouncher Print Page to validate the data of Item Receipt
            navigateToVoucherPrintPage()
            def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
            def MULTICURRENCY = context.executeScript(script).toBoolean()
            voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
            voucherPrintPage.setParameters(filter, null)
            voucherPrintPage.clickRefreshBtn()

            Map tableContent = voucherPrintPage.getVoucherReportMain(0)
            checkAreEqual("Please check Subsidiary", expectedHeader.subsidiary, tableContent.subsidiary)
            checkAreEqual("Please check Transaction Type", expectedHeader.tranType, tableContent.tranType)
            checkAreEqual("Please check TranDate", expectedHeader.trandate, tableContent.date)
            checkAreEqual("Please check postingPeriod", expectedHeader.postingPeriod, tableContent.postingPeriod)
            checkAreEqual("Please check postingPeriod", expectedHeader.currency, tableContent.currency)

            int rowIndex = 1
            String[] expectedCols = ["accountAndDescription", "transactionCurrency", "rate", "transactionCurrencyDebit", "transactionCurrencyCredit", "functionalCurrencyDebit", "functionalCurrencyCredit"]
            Map colsWithValue = voucherPrintPage.getVoucherReportTableRowCells(rowIndex, expectedCols)
            verifyVoucherTableContent(expectedRow1, colsWithValue)

            int rowIndex2 = 2
            Map colsWithValue2 = voucherPrintPage.getVoucherReportTableRowCells(rowIndex2, expectedCols)
            println(colsWithValue2.accountAndDescription)
            verifyVoucherTableContent(expectedRow2, colsWithValue2)
        }
        catch (Exception e) {
            e.printStackTrace()
        }

    }

    def verifyVoucherTableContent(Map expected, Map actual) {

        checkAreEqual("Please check accountAndDescription", expected.accountAndDescription, actual.accountAndDescription)
        checkAreEqual("Please check transactionCurrency", expected.transactionCurrency, actual.transactionCurrency)
        checkAreEqual("Please check rate", expected.rate, actual.rate)
        checkAreEqual("Please check transactionCurrencyDebit", expected.transactionCurrencyDebit, actual.transactionCurrencyDebit)
        checkAreEqual("Please check transactionCurrencyCredit", expected.transactionCurrencyCredit, actual.transactionCurrencyCredit)
        checkAreEqual("Please check functionalCurrencyDebit", expected.functionalCurrencyDebit, actual.functionalCurrencyDebit)
        checkAreEqual("Please check functionalCurrencyCredit", expected.functionalCurrencyCredit, actual.functionalCurrencyCredit)
    }

    def navigateToVoucherPrintPage() {
        def voucherPrintPageURL = resolveSuiteletURL("customscript_sl_cn_voucher_print", "customdeploy_sl_cn_voucher_print")
        context.navigateTo(voucherPrintPageURL)
    }

    String getInternalIDFromURL() {
        String regEx = ~/id=\d+/
        String urlParts = aut.getPageUrl().find(regEx)
        String id = urlParts.substring(3)
        return id
    }

}
