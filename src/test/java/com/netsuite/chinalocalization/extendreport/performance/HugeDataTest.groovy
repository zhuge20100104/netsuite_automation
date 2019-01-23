package com.netsuite.chinalocalization.extendreport.performance

import com.netsuite.chinalocalization.extendreport.ExtendReportAppTestSuite
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

import javax.validation.constraints.AssertTrue
@TestOwner ("zhen.t.tang@oracle.com")
class HugeDataTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\HugeData_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\HugeData_en_US.json"
        ]
    }
    @Before
    void setUp(){
        super.setUp()
        setAdvancedPrint(true)
        setShowAccountNum(true)
        setUseLastSubAccount(true)
    }
    @Rule
    public  TestName name = new TestName()
    def caseData
    def companyName
    def expResult
    def featureName
    def initData(){
        caseData = testData.get(name.getMethodName())
        companyName = aut.getContext().getProperty("testautomation.nsapp.default.account")
        featureName = caseData.featureName
        if ("expectedResult" in caseData) expResult= caseData.expectedResult

    }
    def checkValidationMsg(filter){
        invokeMethod("navigateTo${featureName}Page",null)
        setSearchParams(filter)
        clickRefresh()
        context.waitForElementIdentifiedByToDisappear(Locator.xpath("//*[@id='div__alert']/div/div[2]/div[1]"))
        assertAreEqual("check ${filter?.keySet()} validation messages ",caseData.errMsg,getErrMsg())
        clickErrDialogOK()
    }
    def refreshAndCheckReportDisplay(filter){
        setSearchParams(filter)
        clickRefresh()
        waitForPageToLoad()
        checkParamsSetting(filter)
    }
    def getCurrentMonth(){
        def monthList = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"] as String[]
        Calendar cale = Calendar.getInstance()
        return (monthList[cale.get(Calendar.MONTH)]+" "+Integer.toString(cale.get(Calendar.YEAR)))
    }
    @Override
    def checkParamsSetting(filter){
        if(filter.periodfrom=='cur') filter.periodfrom = getCurrentMonth()
        if(filter.periodto=='cur') filter.periodto = getCurrentMonth()
        super.checkParamsSetting(filter)
    }

    def doTest(){
        initData()
        if(!companyName.contains("Performance")||context.isSingleInstance()) {
            return
        }
        checkValidationMsg(caseData.filter)
        checkParamsSetting(caseData.defaultParam)
        checkButtonsDisplayed(true)
        def excelName= "data\\downloads\\${expResult.excelfilename}".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()
        clickExportExcel()
        waitForPageToLoad()
        assertTrue("Excel downloaded",new File(excelName).exists())
        def pdfName= "data\\downloads\\${expResult.pdffilename}".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()
        waitForPageToLoad()
        assertTrue("Excel downloaded",new File(pdfName).exists())
    }

    /**
     *@desc Mass Data Handling test(for feature subsidiary ledger and cash&bank journal ledger)
     *@case 7.1.1 Check notice message dialog when over 5000 lines in report
     *       7.2.1 Check report display with mass data
     *       7.3.1 Check report header and structure ---Excel
     *       7.3.2 Check report header and structure ---PDF
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   Subsidiary: China Accountant
     *   Special case, has to run under perf environment
     *
     * @author Zhen Tang
     */
    @Category([P2.class,OW.class])
    @Test
    void test_case_sblg(){
        doTest()
    }

    @Category([P2.class,OW.class])
    @Test
    void test_case_cbjl(){
        doTest()
    }


}
