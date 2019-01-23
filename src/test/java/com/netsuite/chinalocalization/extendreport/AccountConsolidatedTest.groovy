package com.netsuite.chinalocalization.extendreport

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.extendreport.ExtendReportAppTestSuite
import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("molly.feng@oracle.com")
/**
 * This test suite is for testing more parameters of classifications.
 */
class AccountConsolidatedTest extends ExtendReportAppTestSuite{

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountConsolidatedTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountConsolidatedTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    private  def caseData
    private  def caseFilter
    private  def expResult
    def prepareSuitData(){
        enableAllClassificationFeatures()
        setShowAccountNum(true)
		setNormalUserPreference(testData.defaultUserPreference)
    }
    def initData(casename){
        println "Start: ${name.getMethodName()}"
		caseData = testData.get(name.getMethodName())
		if (casename) {
			caseData = testData.get(casename)
		}
		if (caseData.filter) {
            caseFilter = caseData.filter
        }
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    /**
     * @desc Parameter Options - Subsidiary Option
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *
     * @case 9.1.1
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_1() {

        navigateToPortalPage()
        initData()
        waitForPageToLoad()
        // check options of subsidiary for supporting consolidated report
        checkConsolidateOpt(expResult, "subsidiary")
    }
    /**
     * @desc Parameter Options - Account From/To Option
     * Parameter Options - Location,Department, Class Option
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     * 3. Set parameter:
     *  Subsidiary: 中国合并   (Consolidated)
     * @case 9.1.2
     * @case 9.1.4
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_2() {

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        waitForPageToLoad()
        // check options of subsidiary for supporting consolidated report
        checkConsolidateOpt(expResult, "accountfrom")
        checkConsolidateOpt(expResult, "accountto")
    }
    /**
     * @desc Parameter Options - Account From/To Option
     * Parameter Options - Location,Department, Class Option
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     * 3. Set parameter:
     *  Subsidiary: 中国合并
     * @case 9.1.3
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_3() {

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        waitForPageToLoad()
        // check options of subsidiary for supporting consolidated report
        checkConsolidateOpt(expResult, "accountfrom")
        checkConsolidateOpt(expResult, "accountto")
    }
    /**
     * @desc Parameter Options - Location,Department, Class Option
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     * 3. Set parameter:
     *  Subsidiary: 中国 Income (Consolidated)
     * @case 9.1.4
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_4() {

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        waitForPageToLoad()
        // check options of subsidiary for supporting consolidated report
        checkConsolidateOpt(expResult, "location")
        checkConsolidateOpt(expResult, "department")
        checkConsolidateOpt(expResult, "class")
    }
    /**
     * @desc Parameter Options - Location,Department, Class Option
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     * 3. Set parameter:
     *  Subsidiary: 中国 Income
     * @case 9.1.5
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_5() {

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        waitForPageToLoad()
        // check options of subsidiary for supporting consolidated report
        checkConsolidateOpt(expResult, "location")
        checkConsolidateOpt(expResult, "department")
        checkConsolidateOpt(expResult, "class")
    }
    /**
     * @desc Consolidated Report - Report Data (Refresh)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Refresh)
     *  - Location:all; Department:Income生产部; Class:智能手机
     *  Add DrillDown to SL
     * @case 9.2.1
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_2_1() {

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)
        // drilldown to Subledger
        drillDownToSLAndCB(caseData.accountToSL, caseData.sltitle)
        checkParamsSetting(caseFilter)
        checkParamsSetting(['accountfrom':caseData.accountToSL, 'accountto':caseData.accountToSL])
        assertTrue("Check Page title: ${caseData.sltitle}", context.doesTextExist(caseData.sltitle))

    }

    /**
     * @desc Consolidated Report - Report Data (Refresh)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Refresh)
     *  - Location:all; Department:Income生产部; Class:智能手机
     *  Add DrillDown to CB
     * @case 9.2.3
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_2_3() {

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)
        // drilldown to CashBank
        drillDownToSLAndCB(caseData.accountToCB, caseData.cbtitle)
        checkParamsSetting(caseFilter)
        checkParamsSetting(['accountfrom':caseData.accountToCB, 'accountto':caseData.accountToCB])
        assertTrue("Check Page title: ${caseData.cbtitle}", context.doesTextExist(caseData.cbtitle))

    }
    def drillDownToSLAndCB(accountname,title) {
        def currentHandle
        def clickindex =getIndexOfAccount(accountname)

        def xpathAccount = "//*[@id=\"custpage_atbl_report_sublistrow${clickindex}\"]/td[1]/a"
        asClick(xpathAccount)
        waitForPageToLoad()
        currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(title)
		waitForPageToLoad()

    }
    def getIndexOfAccount(accountname) {

        def lineAccountName
		def lineCount = currentRecord.getLineCount("custpage_atbl_report_sublist")
        for(int i = 0 ; i < lineCount; i ++){
            lineAccountName = asText(".//*[@id='custpage_atbl_report_sublistrow${i}']/td[1]")
            if (lineAccountName == accountname) {
                return i
            }
        }
    }

}
