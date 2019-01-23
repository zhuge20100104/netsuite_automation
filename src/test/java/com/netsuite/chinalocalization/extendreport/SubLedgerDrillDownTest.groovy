package com.netsuite.chinalocalization.extendreport

import com.google.inject.Inject

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0

import com.netsuite.common.P3
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName


@TestOwner("christina.chen@oracle.com")
class SubLedgerDrillDownTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerDrillDownTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerDrillDownTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    def caseData
    def caseFilter
    def expResult
    def abbody = "//table[@id='custpage_atbl_report_sublist_splits']/tbody/"
    def sblgbody = ".//table[@id='custpage_report_sublist_splits']/tbody/"


    def prepareSuitData(){
        setShowAccountNum(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        userPrePage.setDateFormat("YYYY/M/D")
        userPrePage.clickSave()
    }
    def preCase(){
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        userPrePage.setDateFormat("YYYY/M/D")
        userPrePage.clickSave()

        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
        navigateToPortalPage()
        //initData()
        setSearchParams(caseFilter)
    }
    def checkDrillDown(){
        int clickindex =0
        def accountname,currentHandle
        expResult.account.eachWithIndex {item, index ->
            accountname = asText("${abbody}tr[${3+index}]/td[1]")
            checkAreEqual("Account is correct:",accountname, expResult.account[index])
            if (accountname == expResult.filter.accountfrom ) clickindex =3+index
        }

        asClick("${abbody}tr[${clickindex}]/td[1]/a")
        waitForPageToLoad()
        currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)


        context.switchToWindow(expResult.subtitle)

        checkParamsSetting(expResult.filter)
        checkTrue("Title expected", context.isTextVisible(expResult.subtitle))
        def docNum=null,docDate=null,item
        int i =1
        for( ; i <=subLedgerHelper("getLineCount"); i ++){
            item = subLedgerHelper("getSublistValue","type", i)
            if(item == expResult.journaltype){
                docDate = subLedgerHelper("getSublistValue","date", i)
                docNum = asText("${sblgbody}tr[${2+i}]/td[3]")
                break
            }
        }
        if(docNum) {
            asClick("${sblgbody}tr[${2+i}]/td[3]/a")
            waitForPageToLoad()
            currentHandle = context.webDriver.getWindowHandle()
            context.webDriver.closeWindow(currentHandle)
            context.switchToWindow(expResult.gltitle)

            if (docNum == testData.docNum){
                checkTrue("Return to :${expResult.glreturn + expResult.journaltype}", context.isTextVisible("${expResult.glreturn}${expResult.journaltype}"))
            }
            else{
                checkTrue("Return to :${expResult.glreturn + expResult.journaltype} #${docNum}", context.isTextVisible("${expResult.glreturn}${expResult.journaltype} #${docNum}"))
            }


            expResult.glaccount.each{
                checkTrue("Account ${it} is expected", context.isTextVisible(it))
            }

            asClick("//li[@id=\"NS_MENU_ID0-item0\"]/a")
            checkTrue("Transaction type is correct", context.isTextVisible(expResult.journaltype))
            if (docNum != testData.docNum){ checkTrue("DocNum is correct", context.isTextVisible("${docNum}"))}
            checkTrue("DocDate is correct", context.isTextVisible("${docDate}"))
        }

    }

    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * subsidiary: 中国 SubLedger 03
     Date From/To: 5/1/2018 - 5/31/2018
     ACCOUNT FROM/TO:
     6003 - 6003
     ACCOUNT LEVEL:"All Accounts and Subaccounts"
     * click the account 6003-02 in result list can open a subledger reprt tab page for this account with same Date Date.
     * @case 8.1.1
     * @case 8.2.1
     * @author Christina Chen
     */

    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_8_1_1(){
        navigateToPortalPage()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkDrillDown()

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     *      Date From/To: 5/1/2018 - 5/31/2018
     ACCOUNT FROM/TO:
     6003 - 6003
     ACCOUNT LEVEL:"All Accounts and Subaccounts"
     * click the account 6003 in result list can open a subledger reprt tab page for this account with same Date Date.
     * @case 8.1.2
     * @case 8.2.1
     * @author Christina Chen
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_8_1_2(){
        navigateToPortalPage()
        //initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkDrillDown()
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     *"subsidiary": "中国 SubLedger 03",
     "datefrom": "2018/7/1",
     "dateto": "2018/7/31",
     "accountfrom": "6003-02 - 服务类收入&(Special)",
     "accountto":"6003-02 - 服务类收入&(Special)",
     "accountlevel": "Only Last Subaccounts"
     * click on a transaction no document number  can got to Related  GL impact page
     * click the account 6003-02  in result list can open a subledger report tab page for this account with same Date Date.
     * click the transaction  can open a new GLimpact tab page
     * click the return to link can go to the transaction page
     * @case 8.2.2
     * @author Christina Chen
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_8_2_2(){

        clickRefresh()
        waitForPageToLoad()
        checkDrillDown()
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     "subsidiary": "中国 SubLedger 03",
     "datefrom": "2018/7/1" - "dateto": "2018/7/31",
     "accountfrom": "6003 - 企业营业收入&(Special)",
     "accountto":"6003 - 企业营业收入&(Special)",
     "accountlevel": "All Accounts and Subaccounts"
     check has duplicated document number in same account  can got to Related  GL impact page
     * click the account 6003.01  in result list can open a subledger report tab page for this account with same Date Date.
     * click the transaction (duplicated document number) can open a new GLimpact tab page
     * click the return to link can go to the transaction page
     * @case 8.2.3
     * @author Christina Chen
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_8_2_3(){

        clickRefresh()
        waitForPageToLoad()
        checkDrillDown()
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     "subsidiary": "China SubLedger 01",
     "datefrom": "2016/7/1",
     "dateto": "2016/7/31",
     "accountfrom": "1100 - Accounts Receivable",
     "accountto":"1100 - Accounts Receivable",
     "accountlevel": "Only Last Subaccounts"
     * click the account 1100 in result list can open a subledger report tab page for this account with same Date Date.
     * click the transaction payment can open a new GLimpact tab page
     * click the return to link can go to the transaction page
     * @case 8.2.4
     * @author Christina Chen
     */
    @Category([P0.class,SI.class])
    @Test
    void test_case_8_2_4(){

        clickRefresh()
        waitForPageToLoad()
        checkDrillDown()
    }
}
