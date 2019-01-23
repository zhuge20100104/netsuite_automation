package com.netsuite.chinalocalization.extendreport

import com.google.inject.Inject
import com.netsuite.base.lib.month.MonthUtil
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P3
import com.netsuite.common.disableAPDF
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import org.openqa.selenium.Alert

@TestOwner ("zhen.t.tang@oracle.com")
class SubLedgerValidationTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerValidation_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerValidation_en_US.json"
        ]
    }
    @Inject
    AlertHandler alertHandler

    /*@SuiteTeardown
    void tearDownTestSuite(){
        //deal with system alert message
        *//*context.webDriver.reloadBrowser()
        Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver, 10)
        if(alert!=null) {
            alert.accept()
        }*//*
        super.tearDownTestSuite()
    }*/
    @Rule
    public  TestName name = new TestName()
    def caseData
    def prepareSuitData(){
        //setAdvancedPrint(true)
        setShowAccountNum(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        userPrePage.setDateFormat("M/D/YYYY")
        userPrePage.clickSave()
    }

    def initData(){
        caseData = testData.get(name.getMethodName())
    }

    def checkValidationMsg(filter=null,refreshFlag=false, datenull=false){
        navigateToSubLedgerPage()
        if(filter){
            def curMonth = monthUtil.getCurrentMonth()
            def curYear = monthUtil.getCurrentYear()
            if(filter.datefrom=='cur') filter.datefrom = monthUtil.getYearMonthDayStr(curYear, curMonth, 1)
            if(filter.dateto=='cur') filter.dateto = monthUtil.getYearMonthDayStr(curYear, curMonth, monthUtil.getLastDayOfAMonth(curYear, curMonth))
        }
        setSearchParams(filter)
        if(datenull){
            if (filter.datefrom=="") context.setFieldWithValue(extendReportPage.FIELD_ID_DATEFROM, "")
            if (filter.dateto=="") context.setFieldWithValue(extendReportPage.FIELD_ID_DATETO, "")
        }

        if (refreshFlag )clickRefresh()
        assertAreEqual("check ${filter?.keySet()} validation messages ",caseData.errMsg,getErrMsg())
        clickErrDialogOK()

    }

    def refreshAndCheckReportDisplay(filter){
        setSearchParams(filter)
        clickRefresh()
        waitForPageToLoad()
        checkParamsSetting(filter)
    }

    @Override
    def checkParamsSetting(filter){
        def curMonth = monthUtil.getCurrentMonth()
        def curYear = monthUtil.getCurrentYear()
        if(filter.datefrom=='cur') filter.datefrom = monthUtil.getYearMonthDayStr(curYear, curMonth, 1)
        if(filter.dateto=='cur') filter.dateto = monthUtil.getYearMonthDayStr(curYear, curMonth, monthUtil.getLastDayOfAMonth(curYear, curMonth))
        super.checkParamsSetting(filter)
    }
    /**
     *@desc validation test
     *@case 4.1.1 Check No Accounting Period
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   Subsidiary: 中国 SubLedger 02
     * @author Zhen Tang
     */
/*    @Category([P1.class,OW.class])
    @Test
    void test_case_4_1_1(){
        if(context.isSingleInstance()){
            return
        }
        initData()
        checkValidationMsg(caseData.filter)
        checkParamsSetting(caseData.defaultParam)
        refreshAndCheckReportDisplay(caseData.filterAfter)


    }*/
    /**
     *@desc validation test
     *@case 4.2.1 Feature validation: Advanced PDF/HTML
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     * @author Zhen Tang
     */
    @Category([P3.class,OWAndSI.class, disableAPDF.class])
    @Test
    void test_case_4_2_1(){
        initData()
        setAdvancedPrint(false)
        checkValidationMsg()
        checkParamsSetting(caseData.defaultParam)
        setAdvancedPrint(true)
        navigateToSubLedgerPage()
        refreshAndCheckReportDisplay(caseData.filterAfter)
    }

    /**
     *@desc validation test
     *@case 4.3.1 Period Validation --- From Month - To Month
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 SubLedger 03 (OW only )
     *   PERIOD FROM（期间自）: Apr 2018
     *   TO（至）: Dec 2017
     * @author Zhen Tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_3_1(){
        initData()
        checkValidationMsg(caseData.filter, true)
        checkParamsSetting(caseData.defaultParam)
    }
    /**
     *@desc validation test
     *@case 4.3.2 Period Validation ---From FY - To Month
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 SubLedger 03 (OW only )
     *   PERIOD FROM（期间自）: FY 2018
     *   TO（至）: Feb 2017
     * @author Zhen Tang
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_4_3_2(){
        initData()
        checkValidationMsg(caseData.filter, true, true)
        checkParamsSetting(caseData.defaultParam)
    }
    /**
     *@desc validation test
     *@case 4.3.3 Period Validation ---From Month - To Quarter
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 SubLedger 03 (OW only )
     *   PERIOD FROM（期间自）: Feb 2018
     *   TO（至）: Q4 2017
     * @author Zhen Tang
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_4_3_3(){
        initData()
        checkValidationMsg(caseData.filter, true, true)
        checkParamsSetting(caseData.defaultParam)

    }
    /**
     *@desc validation test
     *@case 4.4.1 Account Validation -------With number
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 SubLedger 03 (OW only )
     *   ACCOUNT FROM（科目自）: 608402
     *   TO（至）: 6081
     * @author Zhen Tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_4_1(){
        initData()
        checkValidationMsg(caseData.filter)
        checkParamsSetting(caseData.defaultParam)

        /*context.webDriver.reloadBrowser()
        Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver, 10)
        if(alert!=null) {
            alert.accept()
        }*/
    }
    /**
     *@desc validation test
     *@case 4.4.2 Account Validation -------"account from" has no number
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 SubLedger 03 (OW only )
     *   ACCOUNT FROM（科目自）: Estimates
     *   TO（至）: 608102
     * @author Zhen Tang
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_4_4_2(){
        initData()
        checkValidationMsg(caseData.filter)
        checkParamsSetting(caseData.defaultParam)
    }
}
