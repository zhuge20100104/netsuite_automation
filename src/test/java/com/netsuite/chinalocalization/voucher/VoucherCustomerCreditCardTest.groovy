package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.page.Transaction.CreditCardTransaction
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import java.util.concurrent.ExecutionException

/**
 * Description:
 * Verify Voucher print table for CreditCard Transaction
 * This is a P2 case.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 *
 * @author qunxing.liu
 * @version 2018/4/17
 * @since 2018/4/17
 */
@TestOwner("qunxing.liu@oracle.com")
class VoucherCustomerCreditCardTest extends VoucherP2BaseTest {

    def pageName = "Credit Card Transaction"
    def pageURL = "/app/accounting/transactions/cardchrg.nl?whence="

    CreditCardTransaction ccTransc
    VoucherPrintPage voucherPrintPage

    @Category([P2.class, OW.class])
    @Test
    void case_1_13_1_4() {
        try {
            ccTransc = new CreditCardTransaction(pageName, pageURL, ["context": context, "currentRecord": currentRecord])

            context.navigateTo(pageURL)

            def testData = cData.data("test_1_13_1_4")
            def transactionHeader = testData.data().main
            def expenseLine = testData.data().expense
            def filter = testData.labels()
            def expectedHeader = testData.expect().expectedHeader
            def expectedRow1 = testData.expect().expectedRow[0]
            def expectedRow2 = testData.expect().expectedRow[1]

            //Create a credit card record
            ccTransc.setHeaderInfo(transactionHeader)
            ccTransc.setSubListInfo("expense", expenseLine)
            //       currentRecord.setCurrentRecord(testData.data())
            ccTransc.clickSaveButton()

            def id = getInternalIDFromURL()
            dirtyData = [
                    [
                            "trantype"  : "creditcardcharge",
                            "internalid": "${id}"
                    ]
            ]

            //Navigate to Voucher Print Page and filter record
            navigateToVoucherPrintPage()
            def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
            def MULTICURRENCY = context.executeScript(script).toBoolean()
            voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
            voucherPrintPage.setParameters(filter, null)
            voucherPrintPage.clickRefreshBtn()

            //check the table header properties
            Map tableContent = voucherPrintPage.getVoucherReportMain(0)
            checkAreEqual("Please check Subsidiary", expectedHeader.subsidiary, tableContent.subsidiary)
            checkAreEqual("Please check Transaction Type", expectedHeader.tranType, tableContent.tranType)
            checkAreEqual("Please check TranDate", expectedHeader.trandate, tableContent.date)
            checkAreEqual("Please check postingPeriod", expectedHeader.postingPeriod, tableContent.postingPeriod)
            checkAreEqual("Please check postingPeriod", expectedHeader.currency, tableContent.currency)

            //check the table content row values
            int rowIndex = 1
            String[] expectedCols = ["accountAndDescription", "transactionCurrency", "rate", "transactionCurrencyDebit", "transactionCurrencyCredit", "functionalCurrencyDebit", "functionalCurrencyCredit"]
            Map colsWithValue = voucherPrintPage.getVoucherReportTableRowCells(rowIndex, expectedCols)
            verifyVoucherTableContent(expectedRow1, colsWithValue)

            int rowIndex2 = 2
            Map colsWithValue2 = voucherPrintPage.getVoucherReportTableRowCells(rowIndex2, expectedCols)
            println(colsWithValue2.accountAndDescription)
            verifyVoucherTableContent(expectedRow2, colsWithValue2)
        } catch (Exception e) {
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

