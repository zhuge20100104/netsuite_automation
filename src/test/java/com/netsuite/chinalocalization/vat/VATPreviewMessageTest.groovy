package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import java.util.concurrent.TimeUnit
@TestOwner ("wan.feng@oracle.com")
/**
 * @desc VAT workflow cases suite
 * @author Feng Wan
 */
class VATPreviewMessageTest extends VATAppTestSuite {

    private String dataFilePath = "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\"
    def expectedInternalId
    def caseNo
    def caseData

    def pathToTestDataFiles() {
        def partFilePath = dataFilePath + this.getClass().getSimpleName()

        return [
                "zhCN": partFilePath + "_zh_CN.json",
                "enUS": partFilePath + "_en_US.json"
        ]

    }

    def createCaseData() {
        //switchToRole(administrator)
        dirtyData = record.createRecord(caseData.data)
        //switchToRole(accountant)
        expectedInternalId = dirtyData[0].internalid
    }

    /**
     * @desc Without Information Sheet Number
     * Transaction Type: Credit Memo
     * VAT Type: Special
     * @case 6.4.7.1
     * @author Feng Wan
     * @Non-regression: duplicate case
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_6_4_7_1() {

        caseNo = "test case 6.4.7.1"
        caseData = testData[caseNo]
        createCaseData()
        runCase()
    }

    /**
     * @desc Without Information Sheet Number
     * Transaction Type: Cash Refund
     * VAT Type: Special
     * @case 6.4.7.2
     * @author Feng Wan
     * @Non-regression: duplicate case
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_6_4_7_2() {
        caseNo = "test case 6.4.7.2"
        caseData = testData[caseNo]
        createCaseData()
        runCase()
    }

    private void runCase() {
        navigateToPortalPage()
        runRefreshSteps()
        verifyExistsInResult()
        verifyMessage()
    }

    private void runRefreshSteps() {
        waitForPageToLoad()
        setQueryParams()
        clickRefresh()
        TimeUnit.SECONDS.sleep(10)
    }

    private void verifyExistsInResult() {
        assertAreEqual(caseNo + " assert internal id in search results: true", doesTransactionExistInResults(expectedInternalId).toString(), "true")
    }

    private void verifyMessage() {
        def tableData = getTableData(expectedInternalId)
        def expectedMessage = caseData.expected.message
        assertAreEqual(caseNo + " assert message is " + expectedMessage, expectedMessage, tableData.message)
    }

    private void setQueryParams() {
        if (getContext().isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(caseData.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(caseData.filter.invoicetype)
        def tranid = dirtyData[0].tranid
        if (tranid != null) {
            context.setFieldWithValue("custpage_documentnumberfrom", tranid)
            context.setFieldWithValue("custpage_documentnumberto", tranid)
        }

    }

}