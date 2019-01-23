package com.netsuite.chinalocalization.voucher

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.page.voucher.VoucherPrintP2Page
import com.netsuite.chinalocalization.voucher.model.p2.CaseData
import com.netsuite.chinalocalization.voucher.model.p2.TestData
import org.junit.After
import org.junit.Before


class VoucherP2BaseTest extends BaseAppTestSuite {


    @Inject
    VoucherPrintP2Page printPage

    CaseData cData

    def dirtyData

    def cleanAll

//    def responseFromSuitelet = ""
//    def responseFromUI = [:]

    def SEP = File.separator
    def TEST_DATA_PREFIX = "src\\test\\java\\com\\netsuite\\chinalocalization\\voucher\\data\\".replace("\\", SEP)


    def pathToTestDataFiles() {
        def zhCNFileName = TEST_DATA_PREFIX + this.getClass().getSimpleName() + "_zh_CN.json"
        def enUSFileName = TEST_DATA_PREFIX + this.getClass().getSimpleName() + "_en_US.json"
        return [
                "zhCN": zhCNFileName,
                "enUS": enUSFileName
        ]
    }

    //region role related methods
    def getDefaultRole() {
        return getAdministrator()
    }
    //endregion

    //region url navigation related  methods
    def navigateToVoucherPrintPage() {
        def voucherPrintPageURL = resolveSuiteletURL("customscript_sl_cn_voucher_print", "customdeploy_sl_cn_voucher_print");
        context.navigateTo(voucherPrintPageURL)
    }
    //end region


    @Before
    void setUp() {
        dirtyData = null

        super.setUp()
        def path = pathToTestDataFiles()
        if (!path) {
            return
        }

        if (isTargetLanguageChinese()) {
            if (doesFileExist(path.zhCN)) {
                cData = new CaseData(context, path.zhCN)
            }
        } else {
            if (doesFileExist(path.enUS)) {
                cData = new CaseData(context, path.enUS)
            }
        }

        cleanAll = true
    }


    def isSiblingExisted(parentId) {
        //def ITEMS_RECORD = 'customrecord_cn_vat_edit_item';

        def columns = [
                record.helper.column("internalid").create(),
                record.helper.column("parent").create()
        ]

        def filters = [
                record.helper.filter("parent").is(parentId)
        ]
        def vatRecords = record.searchRecord("customrecord_cn_vat_invoices", filters, columns)
        if (vatRecords.iterator().size() == 0) {
            return false
        } else {
            return true
        }
    }


    def deleteItem(parentId) {
        def editItemType = "customrecord_cn_vat_edit_item"
        def columns = [
                record.helper.column("internalid").create(),
                record.helper.column("custrecord_cn_vat_parent").create()
        ]

        def filters = [
                record.helper.filter("custrecord_cn_vat_parent").is(parentId)
        ]
        def vatRecords = record.searchRecord("${editItemType}", filters, columns)
        vatRecords.each { vatRecord -> record.deleteRecord("${editItemType}", vatRecord.internalid) }
    }

    def cleanUpDirtyData() {
        println("tearDown: cleaning up dirty data...")
        if (!dirtyData) {
            return
        }

        if (!getDefaultRole().equals(getAdministrator())) {
            switchToRole(administrator)
        }

        dirtyData.reverse(true)

        //delete transactions
        if (cleanAll) {
            dirtyData.each { dirtyRecord ->
                try {
                    record.deleteRecord(dirtyRecord.trantype, dirtyRecord.internalid)
                } catch (Exception ex) {
                    ex.printStackTrace()
                }
            }
        }

        if (!getDefaultRole().equals(getAdministrator())) {
            switchToRole(accountant)
        }
    }


    @After
    public void tearDown() {
        cleanUpDirtyData()
        switchWindow()
    }

    def switchWindow() {
        context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
        def currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
    }

    //region  assert related methods
    def assertTextVisible(String assertMsg, String errorMsg) {
        assertTrue(assertMsg, context.isTextVisible(errorMsg))
    }

    def assertTextContains(String assertMsg, String srcText, String containsText) {
        assertTrue(assertMsg, srcText.contains(containsText))
    }

    def assertTextEquals(String assertMsg, String srcText, String equalsText) {
        assertTrue(assertMsg, srcText.equals(equalsText))
    }
    //endregion assert related methods

    //region business related verify methods

    def verifyReportData(def expectData, List<HashMap<String, String>> tableData) {
        def listIdxs = expectData.listIndex
        def shouldAssertData = expectData.listData

        def startIt = listIdxs[0]
        def endIt = listIdxs[1]

        def actualData = tableData[startIt..endIt]

        assertAreEqual("Table data not as expected!!", actualData, shouldAssertData)

        if (expectData.summaryIndex != null) {
            def summaryIdxs = expectData.summaryIndex
            def startSummaryIt = summaryIdxs[0]
            def endSummaryIt = summaryIdxs[1]

            if (startSummaryIt == 0 && endSummaryIt == 0) {
                startSummaryIt = endSummaryIt = tableData.size() - 1
            }

            def actualSummaryData = tableData[startSummaryIt..endSummaryIt]
            def shouldAssertSummaryData = expectData.summaryData
            assertAreEqual("Summary data not as expected!!", actualSummaryData, shouldAssertSummaryData)
        }
    }


    def verifyMultiplyTableData(TestData testData) {
        def startIndex = testData.expect().tableStartIndex
        def perPageCount = testData.expect().perPageCount

        def secondTableIndex = startIndex + 6
        def lastTableIndex = startIndex + (perPageCount * 2 - 1) * 6
        def preLastTableIndex = lastTableIndex - 6

        def actFirstTableData = printPage.parseTableData(startIndex)
        def actSecondTableData = printPage.parseTableData(secondTableIndex)
        def actPreLastTableData = printPage.parseTableData(preLastTableIndex)
        def actLastTableData = printPage.parseTableData(lastTableIndex)

        def expFirstTableData = testData.expect().firstTableData
        def expSecondTableData = testData.expect().secondTableData
        def expPreLastTableData = testData.expect().preLastTableData
        def expLastTableData = testData.expect().lastTableData

        verifyReportData(expFirstTableData, actFirstTableData)
        verifyReportData(expSecondTableData, actSecondTableData)
        verifyReportData(expPreLastTableData, actPreLastTableData)
        verifyReportData(expLastTableData, actLastTableData)
    }
    //endregion

}
