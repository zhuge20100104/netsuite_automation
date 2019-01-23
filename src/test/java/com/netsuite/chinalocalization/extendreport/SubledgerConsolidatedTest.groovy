package com.netsuite.chinalocalization.extendreport

import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("christina.chen@oracle.com")
/**
 * This test suite is for testing more parameters of classifications.
 */
class SubledgerConsolidatedTest extends ExtendReportAppTestSuite{

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubledgerConsolidatedTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubledgerConsolidatedTest_en_US.json"
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
     * @author Christina chen
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_1() {

        navigateToSubLedgerPage()
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
     * @author Christina chen
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_2() {

        navigateToSubLedgerPage()
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
     * @author Christina chen
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_3() {

        navigateToSubLedgerPage()
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
     * @author Christina chen
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_4() {

        navigateToSubLedgerPage()
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
     * @author Christina chen
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_9_1_5() {

        navigateToSubLedgerPage()
        initData()
        setSearchParams(caseFilter)
        waitForPageToLoad()
        // check options of subsidiary for supporting consolidated report
        checkConsolidateOpt(expResult, "location")
        checkConsolidateOpt(expResult, "department")
        checkConsolidateOpt(expResult, "class")
    }


    def getSICompanyName() {
        companyInfoPage.navigateToCompanyInfoPage()
        return companyInfoPage.getCompanyName()
    }

    /**
     *  Set default user preference according json data
     * @param defaultUserPreference
     * @return
     */
    def setNormalUserPreference(defaultUserPreference){

        userPrePage.navigateToURL()

        defaultUserPreference.each { key, value ->
            if (key == "ONLYSHOWLASTSUBACCT") {
                // set this with value
                context.setFieldWithValue(key, value)
            } else {
                // set  format with text
                context.setFieldWithText(key, value)
            }
        }
        userPrePage.clickSave()
    }
}
