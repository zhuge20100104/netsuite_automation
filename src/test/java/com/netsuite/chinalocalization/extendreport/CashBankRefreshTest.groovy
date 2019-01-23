package com.netsuite.chinalocalization.extendreport
import com.netsuite.common.NSError

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("molly.feng@oracle.com")
class CashBankRefreshTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\CashBankRefreshTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\CashBankRefreshTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    private  def caseData
    private  def caseFilter
    private  def expResult
    def initData(caseName){
        println "Start Test : ${name.getMethodName()}"
        if (caseName) {
            caseData = testData.get(caseName)
        } else {
            caseData = testData.get(name.getMethodName())
        }
        caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    /**
     * @desc Check parameter  and  button when report data  returned
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Number Format: XXX,XXX.XX
     * Data Format: M/D/YYYY
     * Enable: GL Audit Numbering
     * -----------Test Point------
     *
     * @case 2.1.1 check parameter  and  button when report data  returned
     * @case 2.2.1 Check Sublist Column header
     * @case 2.2.2 Check Sublist  Contents
     *
     *
     * @author Molly Feng
     */
    @Category([P0.class, OWAndSI.class])
    @Test
    void test_case_2_1_1() {
        setShowAccountNum(true)
        enableFeatureGLAuditNumbering(true)
        setNormalUserPreference(testData.defaultUserPreference)
        navigateToCashAndBankJournalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        // check the button display status
        CheckButtonStatus(expResult)
        // check report table header
        checkCBJHeader()
        // check parameters after refresh
        checkParamsSetting(caseFilter)
        // check report contents
        checkCBJContexts(expResult.data,cashBankJournal)

    }
/**
 * @desc check parameter  and  button  when no report data
 * ONLY SHOW LAST SUBACCOUNT: true
 * USE ACCOUNT NUMBERS: true
 * Number Format: XXX,XXX.XX
 * Data Format: M/D/YYYY
 * Enable: GL Audit Numbering
 * -----------Test Point------
 *
 * @case 2.1.2 check parameter  and  button  when no report data
 * @author Molly Feng
 */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_2_1_2() {
        setShowAccountNum(true)
        enableFeatureGLAuditNumbering(true)
        setNormalUserPreference(testData.defaultUserPreference)
        navigateToCashAndBankJournalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()

        // check Parameter after refresh
        checkParamsSetting(caseFilter)
        // no date returned in sublist
        assertAreEqual("Check no data in sublist", 0, currentRecord.getLineCount("custpage_report_sublist"))

    }
/**
 * @desc Check Context : Check GL# is Empty when disalbed GL Audit Numbering feature
 * ONLY SHOW LAST SUBACCOUNT: true
 * USE ACCOUNT NUMBERS: true
 * Number Format: XXX,XXX.XX
 * Data Format: M/D/YYYY
 * disable: GL Audit Numbering
 *  Check GL# is Empty after
 * @case 2.2.3
 * @author Molly
 */
    @Category([P1.class,SI.class])
    @Test
    void test_case_2_2_3() {
        if (context.isOneWorld()) {
            println "Please Run this case in SI."
            return
        }
        setShowAccountNum(true)
        enableFeatureGLAuditNumbering(false)
        setNormalUserPreference(testData.defaultUserPreference)
        navigateToCashAndBankJournalPage()
        initData("test_case_2_1_1")
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()

        // check GL# column is displayed in the report
        checkCBJHeader()
        checkParamsSetting(caseFilter)

        CheckGLNumDisplayDisabledFeature()
        // enable GL# Numbering feature again
        enableFeatureGLAuditNumbering(true)

    }
    def CheckGLNumDisplayDisabledFeature() {
        // check the GL# is Empty when disabled GL# Numbering feature
        for(int i =1 ; i <= cbjLedgerHelper("getLineCount"); i ++){
            def glNum = cbjLedgerHelper("getSublistValue","custpage_gl_number", i)
            assertAreEqual("gl_number should be empty","",glNum)
        }

    }
    /**
     *  Enable Feature: GL audit Numbering feature
     * @param Boolean enabled
     */
    def enableFeatureGLAuditNumbering(Boolean enabled) {

        if (context.isFeatureEnabled("glauditnumbering") != enabled) {
            //switchToRole(administrator)
            enableFeaPage.navigateToURL()
            context.setFieldWithValue("glauditnumbering", enabled ? "T" : "F")
            enableFeaPage.clickSave()
            //switchToRole(accountant)
        }

    }
    /**
     *  Set default user preference according json data
     * @param defaultUserPreference
     */
    def setNormalUserPreference(defaultUserPreference){

        userPrePage.navigateToURL()
        //context.webDriver.reloadBrowser()
//        Alert alert = alertHandler
//                .waitForAlertToBePresent(context.webDriver)
//        if(alert!=null) {
//            alert.accept()
//        }
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

    /**
     *  check Button status according expected result in json file
     * @param defaultUserPreference
     * example:
     *         "expectedResult": {
     *              "buttons":[{
     *                   "text" : "刷新",
     *                   "fieldid" : "custpage_refresh",
     *                   "displayed": true,
     *                   "disabled": false }]
     *       }
     */
    def CheckButtonStatus(expResult) {

        def buttons = expResult.buttons
        for (btn in buttons) {

            // check button displayed
            if (btn.displayed) {
                // check button label
                assertTrue("Check button label", context.doesButtonExist(btn.text) )
            } else {
                // check button label
                assertFalse("Check button label", context.doesButtonExist(btn.text) )
            }
            // check button disabled status
            if (btn.disabled) {
                assertTrue("Check button disabled status: true", context.isFieldDisabled(btn.fieldid) )
            } else {
                assertFalse("Check button disabled status: false", context.isFieldDisabled(btn.fieldid) )
            }
        }
    }

}
