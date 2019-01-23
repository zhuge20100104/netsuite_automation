package com.netsuite.chinalocalization.extendreport

import com.google.inject.Inject
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P3
import com.netsuite.common.disableAPDF
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import org.openqa.selenium.Alert

@TestOwner ("zhen.t.tang@oracle.com")
/**
 * @modified Molly change period from/to to date from/to 2018/7/31
 */
class AccountBalanceValidationTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountBalanceValidation_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountBalanceValidation_en_US.json"
        ]
    }
    @Inject
    AlertHandler alertHandler

    @Rule
    public  TestName name = new TestName()
    def caseData
    def initData(){
        //setAdvancedPrint(true)
        setShowAccountNum(true)
        setUseLastSubAccount(true)
        caseData = testData.get(name.getMethodName())
    }

    def checkParamsSetting(filter, format){

        def curMonth = monthUtil.getCurrentMonth()
        def curYear = monthUtil.getCurrentYear()
        if(filter.datefrom=='cur') filter.datefrom = monthUtil.getYearMonthDayStr(curYear, curMonth, 1, format)
        if(filter.dateto=='cur') filter.dateto = monthUtil.getYearMonthDayStr(curYear, curMonth, monthUtil.getLastDayOfAMonth(curYear,curMonth), format)

        if(!filter.datefrom) {
            assertAreEqual("Date from should equal","",trimText(context.getFieldValue("custpage_datefrom")))
        }
        if(!filter.dateto) {
            assertAreEqual("Date to should equal","",trimText(context.getFieldValue("custpage_dateto")))
        }
        super.checkParamsSetting(filter)
    }

    /**
     *@desc validation test
     *@case 5.2.1 Feature validation: Advanced PDF/HTML
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     * @author Zhen Tang
     * @modification Molly feng 2018/8/28  remove default subsidiary check. we cannot make sure which is the default subsidiary on the initial page.
     */
    @Category([P3.class,OWAndSI.class, disableAPDF.class])
    @Test
    void test_case_5_2_1(){
        initData()
        def filter = caseData.filter
        setAdvancedPrint(false)
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        // no need setting parameter
        assertAreEqual("check ${filter?.keySet()} validation messages ",caseData.errMsg,getErrMsg())
        clickErrDialogOK()
        checkParamsSetting(caseData.defaultParam, "MM/DD/YYYY")
        setAdvancedPrint(true)
        //setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        //refreshAndCheckReportDisplay(caseData.filterAfter)
        setSearchParams(caseData.filterAfter, "MM/DD/YYYY")
        clickRefresh()
        waitForPageToLoad()
        checkParamsSetting(caseData.filterAfter, "MM/DD/YYYY")
    }

    /**
     *@desc validation test
     *@case 5.3.2 DATE Validation ---From Date is empty
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 AccountBalance 03 (OW only )
     *   DATE FROM（日期自）: empty
     *   TO（至）: 2/28/2017
     * @author Zhen Tang
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_5_3_2(){
        initData()
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        //checkValidationMsg(caseData.filter)
        def filter = caseData.filter
        navigateToPortalPage()
        setSearchParams(filter, "M/D/YYYY")

        if (context.doesButtonExist("OK")) {
            clickErrDialogOK() // need this close popup msg box in docker
        }
        clickRefresh()
        assertAreEqual("check ${filter?.keySet()} validation messages ",caseData.errMsg,getErrMsg())
        clickErrDialogOK()
        checkParamsSetting(caseData.defaultParam, "MM/DD/YYYY")

        // to refresh the page to normal status
        setSearchParams(caseData.filterAfter, "MM/DD/YYYY")
        clickRefresh()

    }
    /**
     *@desc validation test
     *@case 5.3.1 DATE Validation --- From Date bigger than To Date
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 AccountBalance 03 (OW only )
     *   DATE FROM（日期自）: 4/1/2018
     *   TO（至）: 12/31/2017
     * @author Zhen Tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_5_3_1(){
        initData()

        setDateFormat(testData.defaultUserPreference.dateformat, true)
        //checkValidationMsg(caseData.filter)
        def filter = caseData.filter
        navigateToPortalPage()
        setSearchParams(filter, "MM/DD/YYYY")
        clickRefresh()
        assertAreEqual("check ${filter?.keySet()} validation messages ",caseData.errMsg,getErrMsg())
        clickErrDialogOK()
        checkParamsSetting(caseData.defaultParam, "MM/DD/YYYY")

        // to refresh the page to normal status
        /*setSearchParams(caseData.filterAfter, "M/D/YYYY")
        clickRefresh()
        */
    }
    /**
     *@desc validation test
     *@case 5.3.3 DATE Validation ---To Date is empty
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 AccountBalance 03 (OW only )
     *   DATE FROM（日期自）: 12/31/2017
     *   TO（至）: empty
     * @author Zhen Tang
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_5_3_3(){
        initData()
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        //checkValidationMsg(caseData.filter)
        def filter = caseData.filter
        navigateToPortalPage()
        setSearchParams(filter,"M/D/YYYY")
        if (context.doesButtonExist("OK")) {
            clickErrDialogOK() // need this close popup msg box in docker
        }
        clickRefresh()
        assertAreEqual("check ${filter?.keySet()} validation messages ",caseData.errMsg,getErrMsg())
        clickErrDialogOK()
        checkParamsSetting(caseData.defaultParam, "M/D/YYYY")

/*        // to refresh the page to normal status
        setSearchParams(caseData.filterAfter, "M/D/YYYY")
        clickRefresh()*/
    }
    /**
     *@desc validation test
     *@case 5.4.1 Account Validation -------With number
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 AccountBalance 03 (OW only )
     *   ACCOUNT FROM（科目自）: 608402
     *   TO（至）: 6081
     * @author Zhen Tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_5_4_1(){
        initData()
        setDateFormat(testData.defaultUserPreference.dateformat, true)

        def filter = caseData.filter
        navigateToPortalPage()
        setSearchParams(filter, "MM/DD/YYYY")
        assertAreEqual("check ${filter?.keySet()} validation messages ",caseData.errMsg,getErrMsg())
        clickErrDialogOK()
        checkParamsSetting(caseData.defaultParam, "MM/DD/YYYY")

        //clickRefresh()

    }
    /**
     *@desc validation test
     *@case 5.4.2 Account Validation -------"account from" has no number
     * 	 Role:China Accountant
     * 	 Checked Use Account Number
     *   Checked ONLY SHOW LAST SUBACCOUNT
     *   Role:China Accountant
     *   subsidiary: 中国 AccountBalance 03 (OW only )
     *   ACCOUNT FROM（科目自）: Estimates
     *   TO（至）: 608102
     * @author Zhen Tang
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_5_4_2(){
        initData()
        setDateFormat(testData.defaultUserPreference.dateformat, true)
       // checkValidationMsgNoRefresh(caseData.filter)
        def filter = caseData.filter
        navigateToPortalPage()
        setSearchParams(filter, "MM/DD/YYYY")
        assertAreEqual("check ${filter?.keySet()} validation messages ",caseData.errMsg,getErrMsg())
        clickErrDialogOK()
        checkParamsSetting(caseData.defaultParam, "MM/DD/YYYY")
        //clickRefresh()
    }

    def setSearchParams(filter, format='MM/DD/YYYY') {
        def curMonth = monthUtil.getCurrentMonth()
        def curYear = monthUtil.getCurrentYear()

        if(filter) {
            if(filter.datefrom=='cur') filter.datefrom = monthUtil.getYearMonthDayStr(curYear, curMonth, 1, format)
            if(filter.dateto=='cur') filter.dateto = monthUtil.getYearMonthDayStr(curYear, curMonth, monthUtil.getLastDayOfAMonth(curYear, curMonth), format)
            extendReportPage.setQuery(filter)
            if (!filter.datefrom) context.setFieldWithValue(extendReportPage.FIELD_ID_DATEFROM, "")
            if (!filter.dateto) context.setFieldWithValue(extendReportPage.FIELD_ID_DATETO, "")

        }
    }
}
