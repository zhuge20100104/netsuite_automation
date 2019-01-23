package com.netsuite.chinalocalization.extendreport

import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import org.junit.experimental.categories.Category
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.openqa.selenium.Alert

@TestOwner("christina.chen@oracle.com")
/**
 * @modified Molly change period from/to to date from/to 2018/7/31
 */
class AccountSortTest  extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountSortTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountSortTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    private  def caseData
    private  def caseFilter
    private  def expResult
    def initData(){

        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Check account show in order, start with account number - if set
     * won't show account's parent
     * @case 4.1.1
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_1_1() {
        setShowAccountNum(true)
        setUseLastSubAccount(true)
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        clickRefresh()
        //Alert alert = AlertHandler.waitForAlertToBePresent(com.netsuite.testautomation.driver.SeleniumDrive)
        //alert.accept()

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     * Check account show in order, start with account number - if set
     * show account's parent  separate with :
     * @case 4.1.2
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_1_2() {
        setShowAccountNum(true)
        setUseLastSubAccount(false)
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        clickRefresh()
        //Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver)
        //alert.accept()
                //switchTo().alert().accept()

    }
    /* @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: false
     * Check account show in order, start without account number
     * won't show account's parent
     * @case 4.1.3
     * @author Christina Chen
     */
    @Category([P3.class,OW.class])
    //@Test
    void test_case_4_1_3() {
        setShowAccountNum(false)
        setUseLastSubAccount(true)
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        //context.webDriver.acceptUpcomingConfirmationDialog()
        //Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver)
        //alert.accept()
        clickRefresh()
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: false
     * Check account show in order, start without account number
     * show account's parent separated with :
     * @case 4.1.4
     * @author Christina Chen
     */
    @Category([P3.class,OWAndSI.class])
    //@Test
    void test_case_4_1_4() {

        setShowAccountNum(false)
        setUseLastSubAccount(false)
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        //Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver)
        //alert.accept()
        clickRefresh()
    }
    /**
     * @desc Check header and contexts after press refresh button
     * @case 2.2.2*
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_2_2_2() {
        setShowAccountNum(true)
        setUseLastSubAccount(true)
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        waitForPageToLoad()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        //println("test1 ${asAttributeValue(extendReportPage.XPATH_BTN_REFRESH, "disabled")}")
        //Thread.sleep(2 * 1000)
        //assertTrue("Show loading message", context.isTextVisible(labelData.expectedResults.loadingMsg))
        //assertAreEqual("Refresh button disabled", "true", asAttributeValue(extendReportPage.XPATH_BTN_REFRESH, "disabled"))

        waitForPageToLoad()
        checkHeader()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * check after press the button refresh  the context,header and  parameter changes
     * @case 4.1.5
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_1_5() {
        setShowAccountNum(true)
        setUseLastSubAccount(true)
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        clickRefresh()
        waitForPageToLoad()
        checkHeader()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     * Check after press the button refresh  the context,header and  parameter changes
     * @case 4.1.6
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_1_6() {
        setShowAccountNum(true)
        setUseLastSubAccount(false)
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        clickRefresh()
        waitForPageToLoad()
        checkHeader()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)
    }
}
