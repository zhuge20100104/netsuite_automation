package com.netsuite.chinalocalization.extendreport

import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P0
import com.netsuite.testautomation.junit.runners.SuiteSetup
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName


class CashBankToGLTest extends ExtendReportAppTestSuite {

    private static final String XPATH_RETURN_TO_TRANSACTION = "//*[@class='page-title-menu noprint']"

    private static final String FIRST_DOC_NUM = "//*[@id=\"custpage_report_sublistrow1\"]/td[5]/a"
    private static final String SEC_DOC_NUM = "//*[@id=\"custpage_report_sublistrow2\"]/td[5]/a"
//    private static final String DUPLICATE_DOC_NUM = "//*[@id=\"custpage_report_sublistrow46\"]/td[5]/a"

    private static final String XPATH_ACCOUNT_NAME = "//*[@id=\"row0\"]/td[1]/a"
    private static final String XPATH_SUBSIDIARY = "//*[@id=\"row0\"]/td[7]"


    @Rule
    public TestName name = new TestName()
    def caseData
    def currentHandle

    def prepareSuitData(){

        setShowAccountNum(true)
        //setUseLastSubAccount(true)
        userPrePage.navigateToURL()
        if (context.getPreference("ONLYSHOWLASTSUBACCT")!="T") {
            userPrePage.setOnlyShowLastSubAcct(statusString)
        }
        userPrePage.setDateFormat("M/D/YYYY", true)
    }

    def pathToTestDataFiles(){
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "enUS":"${dataFilesPath}extendreport\\data\\CashBankToGLTest_en_US.json",
                "zhCN":"${dataFilesPath}extendreport\\data\\CashBankToGLTest_zh_CN.json"
        ]
    }

    def initData(){
        caseData = testData.get(name.getMethodName())
    }

        /**
     *@desc drill down from cash bank to GL impact, again from GL impact to transaction page
     *@case Linkage to GL impact page -- has doc number
     * 1. Checked Use Account Number
     * 2. Checked ONLY SHOW LAST SUBACCOUNT
     * 3. Role:China Administrator
     * 4. Date Format: DD/MM/YYYY
     * @author xiaojuan.song
     */
    @Test
    @Category([P0.class, OWAndSI.class])
    void test_case_9_7(){
        initData()
        def expSearch = caseData.searchPara
        def expTransaction = caseData.transactionPara
        navigateToCashAndBankJournalPage()
        setSearchParams(expSearch)
        clickRefresh()
        waitForPageToLoad()
        if (asText(FIRST_DOC_NUM) == expTransaction.docnum){
            asElement(FIRST_DOC_NUM).click()
        }
        else {
            asElement(SEC_DOC_NUM).click()
        }
        currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(expSearch.title)

        if(context.isOneWorld()){
            assertTrue("check subsidairy", asText(XPATH_SUBSIDIARY).contains(expSearch.subsidiary))
        }
        assertTrue("check account", asText(XPATH_ACCOUNT_NAME).contains(expSearch.accountfrom[0..3]))
        assertAreEqual("check returnto", expTransaction.returnto, asText(XPATH_RETURN_TO_TRANSACTION))

        asElement(XPATH_RETURN_TO_TRANSACTION).click()
        //currentHandle = context.webDriver.getWindowHandle()
        //context.webDriver.closeWindow(currentHandle)
        //context.switchToWindow(expTransaction.title)
    }


    /**
     *@desc drill down from cash bank to GL impact, again from GL impact to transaction page
     *@case Linkage to GL impact page -- no doc number
     * 1. Checked Use Account Number
     * 2. Checked ONLY SHOW LAST SUBACCOUNT
     * 3. Role:China Administrator
     * 4. Date Format: DD/MM/YYYY
     * @author xiaojuan.song
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_9_8(){
        initData()
        def expSearch = caseData.searchPara
        def expTransaction = caseData.transactionPara
        navigateToCashAndBankJournalPage()
        setSearchParams(expSearch)
        clickRefresh()
        waitForPageToLoad()
        if (asText(FIRST_DOC_NUM) == expTransaction.docnum){
            asElement(FIRST_DOC_NUM).click()
        }
        else {
            asElement(SEC_DOC_NUM).click()
        }
        currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(expSearch.title)

        if(context.isOneWorld()){
            assertTrue("check subsidairy", asText(XPATH_SUBSIDIARY).contains(expSearch.subsidiary))
        }
        assertTrue("check account", asText(XPATH_ACCOUNT_NAME).contains(expSearch.accountfrom[0..3]))
        assertAreEqual("check returnto", expTransaction.returnto, asText(XPATH_RETURN_TO_TRANSACTION))

        asElement(XPATH_RETURN_TO_TRANSACTION).click()
        //currentHandle = context.webDriver.getWindowHandle()
        //context.webDriver.closeWindow(currentHandle)
        //context.switchToWindow(expTransaction.title)
    }

}
