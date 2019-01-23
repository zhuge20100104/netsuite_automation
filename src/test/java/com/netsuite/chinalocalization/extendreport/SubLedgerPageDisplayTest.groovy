package com.netsuite.chinalocalization.extendreport

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import org.openqa.selenium.Alert

import javax.validation.constraints.AssertTrue

@TestOwner ("zhen.t.tang@oracle.com")
class SubLedgerPageDisplayTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerPageDisplay_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerPageDisplay_en_US.json"
        ]
    }


    @Rule
    public  TestName name = new TestName()
    def caseData
    def prepareSuitData(){
        enableAllClassificationFeatures()

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
    def checkLabels(){
        def expLabels = SBlabel.paramLable
        if (!context.isOneWorld()){
            expLabels.remove("subsidiary")
        }
        for(exp in expLabels){
            assertAreEqual("check labels display",exp.value,asLabel("custpage_$exp.key"))
        }
    }

    def checkOptions(expOptions, fieldId){
        def actualOptions = asDropdownList(["fieldId": fieldId]).getOptions()

        for (option in expOptions){
            assertTrue("check options", option in actualOptions)
        }
    }

    @Override
    def checkParamsSetting(filter){
        def curMonth = monthUtil.getCurrentMonth()
        def curYear = monthUtil.getCurrentYear()
        if(filter.datefrom=='cur') filter.datefrom = monthUtil.getYearMonthDayStr(curYear, curMonth, 1, "MM/DD/YYYY")
        if(filter.dateto=='cur') filter.dateto = monthUtil.getYearMonthDayStr(curYear, curMonth, monthUtil.getLastDayOfAMonth(curYear,curMonth),"MM/DD/YYYY")

        super.checkParamsSetting(filter)
    }

    def checkAccountContains(tarAccount,boolean contains = true){
        def actualAccountFromOptions = asDropdownList(["fieldId": "custpage_accountfrom"]).getOptions()
        def actualAccountToOptions = asDropdownList(["fieldId": "custpage_accountto"]).getOptions()
        assertTrue("account from options check",contains == tarAccount in actualAccountFromOptions)
        assertTrue("account to options check",contains == tarAccount in actualAccountToOptions)
    }


    /**
     *@desc Check the labels
     *@case 1.2.1   Run this case in both ow and si
     * 	 Test :
     * 	 Role:China Accountant and target language
     * @author Zhen Tang
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_1_2_1(){
        //navigateToPortalPage("subLedger")
        navigateToSubLedgerPage()
        checkLabels()
    }
    /**
     *@desc Check default values
     *@case 1.2.2  Run this case in both ow and si
     * 	 Test :check the default value of the parameters before user make any change
     * 	 Role:China Accountant and target language
     * @author Zhen Tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_1_2_2(){
        initData()
        def expdata = caseData.fieldValues
        navigateToSubLedgerPage()
        checkParamsSetting(expdata)

    }
    /**
     *@desc Check page name and button
     *@case 1.2.3  Run this case in both ow and si
     * 	 Test :check the page name and button displayed before user make any change
     * 	 Role:China Accountant and target language
     * @author Zhen Tang
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_1_2_3(){
        initData()
        //def expdata = caseData.fieldValues
        navigateToSubLedgerPage()
        checkPageName()
        assertTrue("refresh button should be Displayed",extendReportPage.isExist("custpage_refresh"))
    }
    /**
     *@desc Check page header load before refresh
     *@case 1.3.1  Run this case in both ow and si
     * 	 Test :check the report header displayed before user make any change
     * 	 Role:China Accountant and target language
     * @author Zhen Tang
     */
/*    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_1_3_1() {
        initData()
        //def expdata = caseData.fieldValues
        navigateToSubLedgerPage()
        checkSLHeader()
    }*/
    /**
     *@desc Check Subsidiary options
     *@case 1.4.1  Run this case in ow
     * 	 Test :Check Subsidiary options before user make any change
     * 	 Role:China Accountant and target language
     * 	 check the subsidiaries in dropdown list
     *   Subsidiaries in dropdown list are the same with subsidiaries in Setup > Company > Classifications > Subsidiaries
     * @author Zhen Tang
     */
    @Category([P1.class,OW.class])
    @Test
    void test_case_1_4_1(){
        initData()
        def expdata = caseData.expectOptions
        navigateToSubLedgerPage()
        //checkSubsidiaryOptions(expdata)
        checkOptionsValue(expdata,"subsidiary")

    }
    /**
     *@desc Check Period options
     *@case 1.4.2  Run this case in both ow and si
     * 	 Test :Check Period options before user make any change
     * 	 Role:China Accountant and target language
     * 	 check the periods in dropdown list
     *   Periods in dropdown list are the
     *   same with periods in Setup > Accounting > Manage G/L > Manage Accounting Periods (expect of Adjustment period)
     * @author Zhen Tang
     */
//    @Category([P1.class,OWAndSI.class])
//    @Test
//    void test_case_1_4_2(){
//        initData()
//        def expdata = caseData.expectOptions
//        navigateToSubLedgerPage()
//        checkOptionsValue(expdata,"periodfrom")
//        checkOptionsValue(expdata,"periodto")
//
//    }
    /**
     *@desc Check account options
     *@case 1.4.3  Run this case in both ow and si
     * 	 Test :Check account options of the parameters before user make any change
     * 	 Role:China Accountant and target language
     * 	 check the accounts in dropdown list
     * @author Zhen Tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_1_4_3(){
        initData()
        def expdata = caseData.expectedResult.account
        navigateToSubLedgerPage()
        def filter = caseData.filter
        setSearchParams(filter)
        checkOptionsValue(expdata,"accountfrom")
        checkOptionsValue(expdata,"accountto")
        clickRefresh()
    }
    /**
     *@desc Check account options
     *@case 1.4.4  Run this case in both ow and si
     * 	 Test :Check account options of the parameters before user make any change
     * 	 Role:China Accountant and target language
     * 	 check the accounts in dropdown list
     * @author Zhen Tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_1_4_4(){
        initData()
        setUseLastSubAccount(false)
        def expdata = caseData.expectedResult.account
        navigateToSubLedgerPage()
        def filter = caseData.filter
        setSearchParams(filter)
        checkOptionsValue(expdata,"accountfrom")
        checkOptionsValue(expdata,"accountto")
        clickRefresh()
    }
    /**
     *@desc Check Period sync
     *@case 1.5.1  Run this case in both ow and si
     * 	 Test :Check Period options sync with subsidiary before user make any change
     * 	 Role:China Accountant and target language
     * 	 check the periods in dropdown list
     * 	 calendar : cal without current period
     * @author Zhen Tang
     */
    /*@Category([P1.class,OW.class])
    @Test
    void test_case_1_5_1(){
        initData()
        def expdata = caseData.expectedResult.period
        def filter = caseData.filter
        if(!context.isOneWorld()){
            return
        }
        setSubsidiaryFiscalCalendar(caseData.filter.subsidiary,caseData.calenderToTest)
        navigateToSubLedgerPage()
        setSearchParams(filter)
        checkOptionsValue(expdata,"periodfrom")
        checkOptionsValue(expdata,"periodto")
        context.webDriver.reloadBrowser()
        Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver, 10)
        if(alert!=null) {
            alert.accept()
        }
        setSubsidiaryFiscalCalendar(caseData.filter.subsidiary,caseData.defaultCalendar)

    }*/
    /**
     *@desc Check Period sync
     *@case 1.5.2  Run this case in both ow and si
     * 	 Test :Check Account options sync with subsidiary before user make any change
     * 	 Role:China Accountant and target language
     * 	 check the account in dropdown list
     * 	 calendar : cal without current period
     * @author Zhen Tang
     */
    /*@Category([P1.class,OW.class])
    @Test
    void test_case_1_5_2(){
        initData()
        def expdata = caseData.accountToCheck
        def filterWithTarget = caseData.filterWithTarget
        def filterWithoutTarget = caseData.filterWithoutTarget
        if(!context.isOneWorld()){
            return
        }
        navigateToSubLedgerPage()
        setSearchParams(filterWithTarget)
        checkAccountContains(expdata)
        setSearchParams(filterWithoutTarget)
        checkAccountContains(expdata,false)

    }*/

    /**
     *@desc classification parameter test
     *@case 8.1.2 Classification Options Check
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. Standard Fiscal Calender
     6. Enable Classfication Feature
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_8_1_2(){
        initData()
        def expSearch = caseData.searchparm
        def expLoc = caseData.LocOptions
        def expDep = caseData.DepOptions
        def expClass = caseData.ClassOptions

        navigateToSubLedgerPage()
        setSearchParams(expSearch)

        checkOptions(expLoc, "inpt_custpage_location")
        checkOptions(expDep, "inpt_custpage_department")
        checkOptions(expClass, "inpt_custpage_class")
    }


}