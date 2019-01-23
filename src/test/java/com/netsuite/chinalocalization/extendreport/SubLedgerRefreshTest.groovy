package com.netsuite.chinalocalization.extendreport

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P3
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("christina.chen@oracle.com")
class SubLedgerRefreshTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerRefreshTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerRefreshTest_en_US.json"
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
     * Check a parameter & buttons
     * Page Reload - parameter & buttons
     * @case 2.1.1
     * @case 2.2.1
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_2_1_1() {
        setShowAccountNum(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        userPrePage.setNumberFormat("1,000,000.00")
        userPrePage.setDateFormat("M/D/YYYY")
        userPrePage.clickSave()
        setFeatureglauditnumbering(true)
        navigateToSubLedgerPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkSLHeader()
        checkParamsSetting(caseFilter)
        checkSLContexts(expResult.data,subledger,name.getMethodName())

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     * Check account show in order, start with account number - if set
     * show account's parent  separate with :
     * @case 2.2.1
     * * @case 2.2.3
     * @author Christina Chen
     */
    @Category([P0.class,SI.class])
    @Test
    void test_case_2_2_3() {
        setShowAccountNum(true)
        //setUseLastSubAccount(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        //userPrePage.setNumberFormat("1.000.000,00")
        userPrePage.setNumberFormat("1.000.000,00")
        userPrePage.setDateFormat("YYYY/M/D")
        userPrePage.clickSave()
        setFeatureglauditnumbering(true)
        navigateToSubLedgerPage()
        initData()

        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkSLHeader()
        checkParamsSetting(caseFilter)
        checkSLContexts(expResult.data, subledger, name.getMethodName())
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Check disable gl_number
     * gl_number will be empty
     *
     * @case 3.9

     * @author Christina Chen
     */
    @Category([P3.class,SI.class])
    @Test
    void test_case_3_9() {
        setShowAccountNum(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        //userPrePage.setNumberFormat("1.000.000,00")
        userPrePage.setNumberFormat("1.000.000,00")
        userPrePage.setDateFormat("YYYY/M/D")
        userPrePage.clickSave()
        setFeatureglauditnumbering(false)
        navigateToSubLedgerPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkSLHeader()
        checkParamsSetting(caseFilter)
        def item
        for(int i =1 ; i <=subLedgerHelper("getLineCount"); i ++){
            item = subLedgerHelper("getSublistValue","custpage_gl_number", i)
            assertAreEqual("gl_number should be empty","",item)
        }
        setFeatureglauditnumbering(true)
    }
}
