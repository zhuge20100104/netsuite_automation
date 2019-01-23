package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.BankDepositPage
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("Jingzhou.wang@oracle.com")
class CashflowMoreFilterPDFTest extends CashflowBaseTest {

    @Inject
    protected CashFlowStatementPage cashFlowReportPage
    @Inject
    protected EnableFeaturesPage enableFeaPage
    @Inject
    protected BankDepositPage depositPage

    def jsonFile = "cashflow/case_1_48.json"
    def static response
    def static beforeData
    boolean bankDeposit = false

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    @Before
    void setUp() {
        super.setUp()
        bankDeposit = false
        enableFeaPage.enableAllCustomFilters()
    }

    @After
    void tearDown() {
        println "Test case tear down"
        println "Deleting: " + response
        if (response) {
            if (bankDeposit) {
                println "bank deposit delete"
                deleteTransaction(response)
            } else {
                println "Normal transaction delete"
                context.deleteTransaction(response)
            }
        }
        super.tearDown()
    }

    @Test
    @Category([OW.class, P1.class])
    void case_1_48_pdf() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.2.1")
        beforeData = getTableDataBeforeCreateTransaction("testcase_1.48.2.1")
        response = createTransaction("data_1", getDataObj(jsonFile, "data_1"))
        context.navigateTo(CURL.HOME_CURL)

        //wait 10s for CFS collect
        Thread.sleep(10000)

        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()
        cashFlowReportPage.exportPDF()

        ParsePDF pdfParser = new ParsePDF()
        def expect = getExpect(caseObj.expect)
        def actual = getReportData_PDF(expect.keySet(), 3, pdfParser)
        assertTable_PDF(expect, actual, beforeData)
    }

    //Get expect item:value
    def getExpect(def expectObj) {
        def ret = [:]
        expectObj.each {
            // def label = getLabel(it.item)
            def label = it.item
            ret[label] = it.value
        }
        return ret
    }

    void assertTable_PDF(def expect, def actual, def beforeData) {
        expect.keySet().each {
            String a = actual.get(it)
            String b = beforeData.get(it)
            String e = expect.get(it)
            int aa = Double.parseDouble(trimComma(a))
            int bb = Double.parseDouble(trimComma(b))
            int ee = Double.parseDouble(trimComma(e))
            try {
                checkAreEqual(it.toString() + "'s value Should be equal", aa - bb, ee)
            } catch (Exception ex) {
                ex.printStackTrace()
            }
        }
    }

    def getTableDataBeforeCreateTransaction(String caseId) {
        def caseObj = getCaseObj(jsonFile, caseId)
        def expect = getExpect(caseObj.expect)
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()
        return cashFlowReportPage.getReportData(expect.keySet(), 3)
    }

    //  2,100.32-> 2100.32
    // -2,100.20-> 2100.20
    String trimComma(String before) {
        String after = before
        if (before.contains(",")) {
            after = before.replace(",", "")
        }
        return after
    }

    def getReportData_PDF(def cfsItems, int colNum, ParsePDF pdfParser) {
        Map ret = [:]
        cfsItems.each {
            def value = pdfParser.getValueFromLine(it, colNum)
            ret[it] = value
        }
        return ret
    }

}
