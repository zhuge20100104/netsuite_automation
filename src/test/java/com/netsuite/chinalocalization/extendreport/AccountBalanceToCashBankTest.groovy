package com.netsuite.chinalocalization.extendreport

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.CN
import com.netsuite.testautomation.junit.runners.SuiteSetup

import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

import java.text.SimpleDateFormat
import java.text.DateFormat
import java.text.ParseException


class AccountBalanceToCashBankTest extends ExtendReportAppTestSuite {

    String XPATH_TOP_ACCOUNT_NAME = "//*[@id=\"custpage_atbl_report_sublistrow0\"]/td[1]/a"

    String XPATH_SUBACCOUNT_NAME = "//*[@id=\"custpage_atbl_report_sublistrow3\"]/td[1]/a"

    @Rule
    public TestName name = new TestName()
    def caseData


    def prepareSuitData(){
        setShowAccountNum(true)
        userPrePage.navigateToURL()
        userPrePage.setDateFormat("M/D/YYYY" )
        userPrePage.setOnlyShowLastSubAcct("T")
        userPrePage.clickSave()
    }

    def pathToTestDataFiles(){
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "enUS":"${dataFilesPath}extendreport\\data\\AccountBalanceToCashBankTest_en_US.json",
                "zhCN":"${dataFilesPath}extendreport\\data\\AccountBalanceToCashBankTest_zh_CN.json"
        ]
    }

    def initData(){
        caseData = testData.get(name.getMethodName())
    }

     boolean compareDate(testDate, expResult){
        boolean flag = true
        DateFormat dateFormat=new SimpleDateFormat("MM/dd/yyyy")
        try {
            Date sDate = dateFormat.parse(expResult.datefrom)
            Date eDate = dateFormat.parse(expResult.dateto)
            Date tDate = dateFormat.parse(testDate)

            if(  (tDate.before(eDate)||tDate.equals(eDate))  && (tDate.after(sDate)||tDate.equals(sDate)) ){
                flag = true
            }else{
                flag = false
            }
        } catch (ParseException e) {
            e.printStackTrace()
        }
        return flag
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

    def checkPartHeader(expResult, excelContents){
        def siCompanyName = aut.getContext().getProperty("testautomation.nsapp.default.account")

        if (context.isOneWorld()){
            assertAreEqual("check subsidiary", expResult.subsidiary, excelContents[1][0][4..18])
        }
        else {
            assertTrue("check subsidiary", excelContents[1][0].contains(siCompanyName))
        }

        assertAreEqual("check datefrom", expResult.datefrom, excelContents[1][2][0..9])
        assertAreEqual("check dateto", expResult.dateto, excelContents[1][2][13..22])
    }

    def checkPartReportData(expResult){
        def reportData = getReportData()

        for (int i=0; i<reportData.size(); i++){
            if (reportData[i][0]){
               assertTrue("check account", reportData[i][0].contains('6002'))
            }
            else {
                assertTrue("check date", compareDate(reportData[i][1] , expResult))
            }
        }
    }

    def getRefreshReportData() {
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

    def ClickAccountByName(expAccountName){
        for (int i=0; i<getRefreshReportData().size(); i++){
            def accountName = asText("//*[@id=\"custpage_atbl_report_sublistrow${i}\"]/td[1]/a")
            if (accountName.equals(expAccountName)){
                asElement("//*[@id=\"custpage_atbl_report_sublistrow${i}\"]/td[1]/a").click()
            }
        }

    }

    /**
     *@desc drill down from account balance to cash bank
     *@case Linkage to CashBank page -- Checked ONLY SHOW LAST SUBACCOUNT -- Top Account
     * 1. Checked Use Account Number
     * 2. Checked ONLY SHOW LAST SUBACCOUNT
     * 3. Role:China Accountant
     * 4. Date Format: DD/MM/YYYY
     * @author xiaojuan.song
     */
    @Test
    @Category([P0.class, OWAndSI.class])
    void test_case_9_1(){
        initData()
        def expSearch = caseData.searchPara
        def expResult = caseData.expPara

        navigateToPortalPage()
        setSearchParams(expSearch)
        clickRefresh()
        waitForPageToLoad()
        //asClick(XPATH_TOP_ACCOUNT_NAME)
        int clickindex =getIndexOfAccount(caseData.account)
        def xpathAccount = "//*[@id=\"custpage_atbl_report_sublistrow${clickindex}\"]/td[1]/a"
        asClick(xpathAccount)
        def currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(expSearch.page_title)
        checkParamsSetting(expResult)

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
    /**
     *@desc drill down from account balance to cash bank
     *@case Linkage to  CashBank page -- Checked ONLY SHOW LAST SUBACCOUNT -- Subaccount
     * 1. Checked Use Account Number
     * 2. Checked ONLY SHOW LAST SUBACCOUNT
     * 3. Role:China Accountant
     * 4. Date Format: DD/MM/YYYY
     * @author xiaojuan.song
     */
    @Test
    @Category([P0.class, OWAndSI.class])
    void test_case_9_2(){
        initData()
        def expSearch = caseData.searchPara
        def expSearch_sec = caseData.searchPara_sec
        def expResult = caseData.expPara

        navigateToPortalPage()
        setSearchParams(expSearch)
        clickRefresh()
        setSearchParams(expSearch_sec)

        //asClick(XPATH_SUBACCOUNT_NAME)
        int clickindex =getIndexOfAccount(caseData.account)
        def xpathAccount = "//*[@id=\"custpage_atbl_report_sublistrow${clickindex}\"]/td[1]/a"
        asClick(xpathAccount)
        def currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(expSearch.page_title)
        checkParamsSetting(expResult)
    }


    /**
     *@desc drill down from account balance to cash bank
     *@case Linkage to  CashBank page -- Uncheck ONLY SHOW LAST SUBACCOUNT -- Top account
     * 1. Checked Use Account Number
     * 2. Uncheck ONLY SHOW LAST SUBACCOUNT
     * 3. Role:China Accountant
     * 4. Date Format: DD/MM/YYYY
     * @author xiaojuan.song
     */
    @Test
    @Category([P1.class, OWAndSI.class, CN.class])
    void test_case_9_3(){
        if (targetLanguage() != 'zh_CN') {
            println "Please run this case in CN."
            return
        }

        setUseLastSubAccount(false)

        initData()
        def expSearch = caseData.searchPara
        def expResult = caseData.expPara

        navigateToPortalPage()
        setSearchParams(expSearch)
        clickRefresh()

        asClick(XPATH_TOP_ACCOUNT_NAME)
        def currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(expSearch.page_title)
        checkParamsSetting(expResult)

    }

    /**
     *@desc drill down from account balance to cash bank
     *@case Linkage to CashBank page -- Uncheck ONLY SHOW LAST SUBACCOUNT -- Subaccount
     * 1. Checked Use Account Number
     * 2. Uncheck ONLY SHOW LAST SUBACCOUNT
     * 3. Role:China Accountant
     * 4. Date Format: DD/MM/YYYY
     * @author xiaojuan.song
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_9_4(){
        setUseLastSubAccount(false)

        initData()
        def expSearch = caseData.searchPara
        def expResult = caseData.expPara

        navigateToPortalPage()
        setSearchParams(expSearch)
        clickRefresh()

        asClick(XPATH_SUBACCOUNT_NAME)
        def currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(expSearch.page_title)
        checkParamsSetting(expResult)
    }
}
