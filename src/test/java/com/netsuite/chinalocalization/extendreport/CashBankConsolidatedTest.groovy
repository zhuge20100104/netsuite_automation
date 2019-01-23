package com.netsuite.chinalocalization.extendreport

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


@TestOwner("ying.hu.huang@oracle.com")
/**
 * This test suite is for testing more parameters of classifications.
 */
class CashBankConsolidatedTest extends ExtendReportAppTestSuite{

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\CashBankConsolidatedTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\CashBankConsolidatedTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    def caseData
    def caseFilter
    def expResult
    def prepareSuitData(){
        enableAllClassificationFeatures()
        setShowAccountNum(true)
		setNormalUserPreference(testData.defaultUserPreference)
    }
    def initData(casename){

		caseData = testData.get(name.getMethodName())
		if (casename) {
			caseData = testData.get(casename)
		}
		caseFilter = caseData.filter
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
     * @author Ying Huang
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_1() {

        initData()
        waitForPageToLoad()
        // check options of subsidiary for supporting consolidated report
        navigateToCashAndBankJournalPage()
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
     * @author Ying Huang
     */
    @Test
    @Category([P0.class, OW.class])
    void test_case_9_1_2() {

        navigateToCashAndBankJournalPage()
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
     * @author Ying Huang
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_3() {

        navigateToCashAndBankJournalPage()
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
     * @author Ying Huang
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_4() {

        navigateToCashAndBankJournalPage()
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
     * @author Ying Huang
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_5() {

        navigateToCashAndBankJournalPage()
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
     *  - Location:中国 Income 01华北区; Department:中国 Income 01生产部; Class:中国 Income 01智能手机
     * @case 9.2.2
     * @author Ying Huang
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_2_2() {

        initData()
        navigateToCashAndBankJournalPage()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkParamsSetting(caseFilter)
        checkRefreshReportData(expResult)
    }

    def checkRefreshReportData(expResult) {
        List<List<String>> refreshContents = getReportData()

        for (int i = 0; i < expResult.size(); i++) {
            def line = expResult.get(i)
            line.eachWithIndex { val, idx ->
                assertAreEqual("check data line", val, refreshContents.get(i).get(idx))
            }
        }
    }

    def getReportData() {
        def resultData = []
        for(int i =1 ; i <=cbjLedgerHelper("getLineCount"); i++){
            def curLine = []
            def reportString
            subledger.eachWithIndex{it,index ->
                reportString = asText(".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[${i+2}]/td[${index + 1 }]")
                curLine.add(reportString.toString().trim())
            }
            resultData.add(curLine)
        }
        return resultData
    }

}
