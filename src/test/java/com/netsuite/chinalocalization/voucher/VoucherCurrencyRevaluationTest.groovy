package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.page.VendorBillPage
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Description:
 * Verify Voucher print table for Currency Revaluation Transaction
 * This is a P2 case.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 *
 */
@TestOwner("cuiping.peng@oracle.com")
class VoucherCurrencyRevaluationTest extends VoucherP2BaseTest {

    VoucherPrintPage voucherPrintPage
    def testData

    @Category([P2.class, OW.class])
    @Test
    void case_1_13_1_6() {
        // Prepare a bill
        VendorBillPage bill = new VendorBillPage(["context": context, "currentRecord": currentRecord])
        context.navigateTo(bill.URL)
        testData = cData.data("test_1_13_1_6")
        def mainHeader = testData.data().bill.main
        def expenseLine = testData.data().bill.expense
        bill.setHeaderInfo(mainHeader)
        bill.setSubListInfo("expense", expenseLine)
        bill.clickSaveButton()

        // Store the created Purchase Order into to be deleted in teardown
        def id = getInternalIDFromURL()
        dirtyData = [
                [
                        "trantype"  : "vendorbill",
                        "internalid": "${id}"
                ]
        ]

        // Currency Revaluation against Subsidiary "China BU" and Period "Apr 2017"
        context.navigateTo("/app/accounting/transactions/fxrevalsetup.nl?whence=")
        context.withinDropdownlist("subsidiary").selectItem(testData.data().currencyRevaluation.subsidiary)
        context.waitForPageToLoad()
        context.setFieldWithText("period", testData.data().currencyRevaluation.period)
        context.withinHtmlElementIdentifiedBy(Locator.xpath(".//*[@id='submitter']")).click()
        Thread.sleep(5 * 1000)

        navigateToVoucherPrintPage()
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        def filter = testData.labels()
        def expectedHeader = testData.expect().expectedHeader
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
        voucherPrintPage.setParameters(filter, null)
        voucherPrintPage.clickRefreshBtn()

        Map tableContent = voucherPrintPage.getVoucherReportMain(0)
        checkAreEqual("Please check Subsidiary", expectedHeader.subsidiary, tableContent.subsidiary)
        checkAreEqual("Please check Transaction Type", expectedHeader.tranType, tableContent.tranType)
        checkAreEqual("Please check TranDate", expectedHeader.trandate, tableContent.date)
        checkAreEqual("Please check postingPeriod", expectedHeader.postingPeriod, tableContent.postingPeriod)
        checkAreEqual("Please check postingPeriod", expectedHeader.currency, tableContent.currency)
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

    ArrayList<String> getInternalIDArrayFromList(String columnValue, String dateColumnName, String internalIdColumnName) {
        ArrayList<String> result = new ArrayList<String>();
        int dateColumnIndex = findLineItemColumnIndex(".//tr[contains(@class,'uir-list-headerrow')]/td", dateColumnName)
        int internalIdColumnIndex = findLineItemColumnIndex(".//tr[contains(@class,'uir-list-headerrow')]/td", internalIdColumnName)

        String xPath = String.format(".//td[%s][contains(text(),'%s')]/parent::tr/td[%s]", dateColumnIndex, columnValue, internalIdColumnIndex)
        List<HtmlElementHandle> allCells = context.webDriver.getHtmlElementsByLocator(Locator.xpath(xPath))

        for (HtmlElementHandle cell : allCells) {
            String journalId = cell.getAsText().trim()
            result.add(journalId)
        }
        return result
    }

    /**
     * Get the column number the colName
     *
     * @param locatorHeaderCols , header column locator
     * @param colName , expected column name
     * @return
     */
    int findLineItemColumnIndex(String locatorHeaderCols, String colName) {
        String expected = colName.trim()
        List<HtmlElementHandle> allColumns = context.webDriver.getHtmlElementsByLocator(Locator.xpath(locatorHeaderCols))
        int index = 1
        for (HtmlElementHandle col : allColumns) {
            String colNameText = col.getAttributeValue("data-label").trim()
            if (expected.equalsIgnoreCase(colNameText)) {
                break
            }
            index = index + 1
        }
        return index
    }

    def getCurrencyRevalutionEditPageUrl(String internalId) {
        String scriptStr = "return (nlapiResolveURL('RECORD', 'fxreval', " + internalId + ", 'EDIT'));"
        String curRevEditPageUrl = context.executeScript(scriptStr)
        return curRevEditPageUrl

    }

    @After
    void tearDown() {
        // Login Netsuite using role 'Admin'
        //cleanTestData()
        super.tearDown()

    }

    void cleanTestData() {

        //Clean Currency Revaluation Records
        context.navigateTo("https://system.na3.netsuite.com/app/accounting/transactions/transactionlist.nl?whence=&Transaction_TYPE=FxReval")
        def dateColumnName = testData.data().currencyRevaluationList.dateColumnName
        def internalIdColumnName = testData.data().currencyRevaluationList.internalIdColumnName
        ArrayList<String> curRevInternalIds = getInternalIDArrayFromList("4/30/2017", dateColumnName, internalIdColumnName)
        for (String curRevInternalId : curRevInternalIds) {
            def pageUrl = getCurrencyRevalutionEditPageUrl(curRevInternalId)
            context.navigateTo(pageUrl)
            // Click "Action" button
            context.withinHtmlElementIdentifiedBy(Locator.xpath(".//*[@id='spn_ACTIONMENU_d1']//a")).click()
            // Click "Delete" button
            context.webDriver.acceptUpcomingConfirmationDialog()
            context.withinHtmlElementIdentifiedBy(Locator.xpath(".//*[@id='nl1']/a")).click()
            Thread.sleep(2 * 1000)
        }

        // Clean other data via Internal IDs
        super.cleanUpDirtyData()
    }
}
