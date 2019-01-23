package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.BankDepositPage
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.chinalocalization.page.Report.GeneralLedgerReportPage
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("Jingzhou.wang@oracle.com")
class CashflowMoreFilterTest extends CashflowBaseTest {

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

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.1.2
     * @desc Verify filter drop down list will change according to subsidiary's change
     */
    @Test
    @Category([OW.class, P0.class])
    void case_1_48_1_2() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.1.2")
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param_1)
        assertTrue("北京地区_CFS should in location options", cashFlowReportPage.getLocationOptions().contains("北京地区_CFS"))
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.1.3
     * @desc
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_48_1_3() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.1.3")
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()
        assertTrue("Report Table should show.", asElement(cashFlowReportPage.XPATH_HEADER).exists())
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.2.1 All customer filter enable, make customer payment and check department
     * @desc
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_48_2_1() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.2.1")
        beforeData = getTableDataBeforeCreateTransaction("testcase_1.48.2.1")
        response = createTransaction("data_1", getDataObj(jsonFile, "data_1"))
        context.navigateTo(CURL.HOME_CURL)

        //wait 10s for CFS collect
        Thread.sleep(10000)
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()
        try {
            assertHeaderDepartment(caseObj.param)
            //assertTable(caseObj.expect, beforeData)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.2.2 All customer filter enable, make undeposited invoice payment and then bank deposite
     * @desc
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_48_2_2() {
        bankDeposit = true
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.2.2")
        beforeData = getTableDataBeforeCreateTransaction("testcase_1.48.2.2")
        response = createDeposit("data_2", jsonFile)
        context.navigateTo(CURL.HOME_CURL)

        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()

        assertHeaderLocation(caseObj.param)
        assertTable(caseObj.expect, beforeData)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.2.3 All customer filter enable
     * @desc
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_48_2_3() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.2.3")
        beforeData = getTableDataBeforeCreateTransaction("testcase_1.48.2.3")
        response = createTransaction("data_3", getDataObj(jsonFile, "data_3"))
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()

        assertHeaderLocation(caseObj.param)
        assertTable(caseObj.expect, beforeData)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.2.4 All customer filter enable-> Set all 3, cash sale
     * @desc
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_48_2_4() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.2.4")
        beforeData = getTableDataBeforeCreateTransaction("testcase_1.48.2.4")
        response = createTransaction("data_4", getDataObj(jsonFile, "data_4"))
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()

        assertHeaderDepartment(caseObj.param)
        assertHeaderLocation(caseObj.param)
        assertHeaderClass(caseObj.param)

        assertTable(caseObj.expect, beforeData)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.2.5 All customer filter enable-> Set all 3, journal
     * @desc
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_48_2_5() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.2.5")
        beforeData = getTableDataBeforeCreateTransaction("testcase_1.48.2.5")
        context.navigateTo(CURL.HOME_CURL)
        changeLanguageToEnglish()
        response = createTransaction("data_5", getDataObj(jsonFile, "data_5"))
        context.navigateTo(CURL.HOME_CURL)
        if (isTargetLanguageChinese()) {
            changeLanguageToChinese()
        }
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()

        assertHeaderDepartment(caseObj.param)
        assertHeaderLocation(caseObj.param)
        assertHeaderClass(caseObj.param)

        assertTable(caseObj.expect, beforeData)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.2.4 All customer filter enable-> Set all 3, journal
     * @desc
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_48_2_6() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.2.6")
        beforeData = getTableDataBeforeCreateTransaction("testcase_1.48.2.6")
        response = createTransaction("data_6", getDataObj(jsonFile, "data_6"))
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()

        assertHeaderDepartment(caseObj.param)
        assertHeaderLocation(caseObj.param)
        assertTable(caseObj.expect, beforeData)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.2.4 All customer filter enable-> Set all 3, journal
     * @desc
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_48_2_7() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.48.2.7")
        beforeData = getTableDataBeforeCreateTransaction("testcase_1.48.2.7")
        response = createTransaction("data_7", getDataObj(jsonFile, "data_7"))
        cashFlowReportPage.navigateToURL()
        cashFlowReportPage.setSearchParameter(caseObj.param)
        cashFlowReportPage.clickRefresh()

        assertHeaderClass(caseObj.param)
        assertTable(caseObj.expect, beforeData)
    }

    //Disable Class
    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.4.1 Disable Class
     * @desc
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_48_4_1() {
        enableFeaPage.navigateToURL()
        enableFeaPage.disbleClasses()
        enableFeaPage.clickSave()
        cashFlowReportPage.navigateToURL()

        assertTrue("Class drop down list should not show", asElement(cashFlowReportPage.XPATH_CLASS_DROPDOWN) == null)
    }

    //Disable Department
    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.48.5.1 Disable Department
     * @desc
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_48_5_1() {
        enableFeaPage.navigateToURL()
        enableFeaPage.disbleDepartments()
        enableFeaPage.clickSave()
        cashFlowReportPage.navigateToURL()

        assertTrue("Department drop down list should not show", asElement(cashFlowReportPage.XPATH_DEPARTMENT_DROPDOWN) == null)
    }

    def assertHeaderClass(def param) {
        def expect = cashFlowReportPage.SearchParameter(param, "Class")
        def actual = cashFlowReportPage.getHeaderData(4, getLabel("Class"))
        assertAreEqual("Proper Class should show in header.", actual.trim(), expect)
    }

    def assertHeaderDepartment(def param) {
        def expect = cashFlowReportPage.SearchParameter(param, "Department")
        def actual = cashFlowReportPage.getHeaderData(4, getLabel("Department"))
        assertAreEqual("Proper Department should show in header.", actual.trim(), expect)
    }

    def assertHeaderLocation(def param) {
        def expect = cashFlowReportPage.SearchParameter(param, "Location")
        def actual = cashFlowReportPage.getHeaderData(4, getLabel("Location"))
        assertAreEqual("Proper Location should show in header.", actual.trim(), expect)
    }

    def getExpectValue(def expectObj, def target) {
        for (int i = 0; i < expectObj.size(); i++) {
            if (expectObj[i].item == target) {
                return expectObj[i].value
            }
        }
    }

    //Get expect item:value
    def getExpect(def expectObj) {
        def ret = [:]
        expectObj.each {
            def label = getLabel(it.item)
            ret[label] = it.value
        }
        return ret
    }

    def getExpectItems(def expectObj) {
        def items = []
        expectObj.each {
            //Change to Chinese if necessary
            items.add(getLabel(it.item))
        }
    }

    def createDeposit(def caseId, def jsonFile) {
        def caseObj = loadCFSTestData(jsonFile, caseId)
        def dataObj = caseObj[caseId].data
        def uiObj = context.getUIDataObj(caseObj, '1.48_bank_deposit')

        def response = createTransaction(caseId, dataObj)

        def responseObj = new JsonSlurper().parseText(response)
        def paymentId = responseObj[1].internalid
        def invoiceId = responseObj[0].internalid
        def depositId = depositPage.createDeposit(context, uiObj, paymentId)

        responseObj += [internalid: depositId, trantype: "deposit"]
        response = JsonOutput.toJson(responseObj)
        return response
    }

    void assertTable(def expectObj, def beforeData) {
        def expect = getExpect(expectObj)
        def actual = cashFlowReportPage.getReportData(expect.keySet(), 3)
        expect.keySet().each {
            String a = actual.get(it)
            String b = beforeData.get(it)
            String e = expect.get(it)

            int aa = Double.parseDouble(trimComma(a))
            int bb = Double.parseDouble(trimComma(b))
            int ee = Double.parseDouble(trimComma(e))

            try {
                checkAreEqual(it.toString() + "'s value Should be equal", aa - bb, ee)
            }
            catch (Exception ex) {
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

    def getTableDataBeforeCreateTransaction_quick(String caseId) {
        def caseObj = getCaseObj(jsonFile, caseId)
        def expect = getExpect(caseObj.expect)
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
}
