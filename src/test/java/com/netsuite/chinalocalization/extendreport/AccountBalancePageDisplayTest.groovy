package com.netsuite.chinalocalization.extendreport

import com.google.inject.Inject
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
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
 * @modified by Molly.feng 2018/7/31 change period from/to to date from/to
 */
class AccountBalancePageDisplayTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountBalancePageDisplay_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountBalancePageDisplay_en_US.json"
        ]
    }
    @Inject
    AlertHandler alertHandler

    @Rule
    public  TestName name = new TestName()
    def caseData
    def prepareSuitData(){
        enableAllClassificationFeatures()
        setAdvancedPrint(true)
        setShowAccountNum(true)
        setUseLastSubAccount(true)
    }
    def initData(){
        caseData = testData.get(name.getMethodName())
    }


    def monthList = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"] as String[]
    def checkLabels(){
        def expLabels = testData.labels
        if (!context.isOneWorld()){
            expLabels.remove("subsidiary")
            expLabels.remove("location")
            expLabels.remove("department")
            expLabels.remove("class")
        }
        for(exp in expLabels){
            assertAreEqual("check labels display",exp.value,asLabel("custpage_$exp.key"))
        }
    }


    def getCurValue(){
        Calendar cale = Calendar.getInstance()
        def year = cale.get(Calendar.YEAR)
        def month = cale.get(Calendar.MONTH)
    return (monthList[month]+" "+Integer.toString(year))
}
    def checkSubsidiaryOptions(expdata){
        def options = extendReportPage.getSubsidiaryOptions()
        for(expOption in expdata){
            assertTrue("assert subsidiary option",options.contains(expOption))
        }
    }

/**
 *@desc Check the labels
 *@case 2.1.2.1   Run this case in both ow and si
 * 	 Test :
 * 	 Role:China Accountant and target language
 * @case 8.1.1 add more parameters: location, department, class
 *             check the labels
 * @author Zhen Tang
 * @Regression:  page init
 */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_2_1_2_1(){
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        checkLabels()
        //clickRefresh()
    }
/**
 *@desc Check default values
 *@case 2.1.2.2  Run this case in both ow and si
 * 	 Test :check the default value of the parameters before user make any change
 * 	 Role:China Accountant and target language
 * @case 8.1.1 add more parameters: location, department, class
 *             check the default value of classification parameters
 * @author Zhen Tang
 * @Non-regression: duplicate case
 */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_2_1_2_2(){
        initData()
        def expdata = caseData.fieldValues
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        //checkParamsSetting(expdata, testData.defaultUserPreference.dateformat)
        // MonthUtil only support double M double D
        checkParamsSetting(expdata, "MM/DD/YYYY")
        //clickRefresh()
    }
/**
 *@desc Check Subsidiary options
 *@case 2.1.2.3  Run this case in ow
 * 	 Test :Check Subsidiary options before user make any change
 * 	 Role:China Accountant and target language
 * 	 check the subsidiaries in dropdown list
 *   Subsidiaries in dropdown list are the same with subsidiaries in Setup > Company > Classifications > Subsidiaries
 * @author Zhen Tang
 */
    @Category([P1.class,OW.class])
    @Test
    void test_case_2_1_2_3(){
        initData()
        def expdata = caseData.expectOptions
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        checkOptionsValue(expdata,"subsidiary")
        //clickRefresh()
    }
/**
 *@desc Check account options
 *@case 2.1.2.5  Run this case in both ow and si
 * 	 Test :Check account options of the parameters before user make any change
 * 	 Role:China Accountant and target language
 * 	 check the accounts in dropdown list
 * @author Zhen Tang
 */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_2_1_2_5(){
        initData()
        def expdata = caseData.expectedResult.account
        setDateFormat(testData.defaultUserPreference.dateformat, true)
        navigateToPortalPage()
        def filter = caseData.filter
        setSearchParams(filter)
        checkOptionsValue(expdata,"accountfrom")
        checkOptionsValue(expdata,"accountto")
        checkOptionsValue(testData.accountLevels,"accountlevel")
        //clickRefresh()
    }
    def checkParamsSetting(filter, format){
        def curMonth = monthUtil.getCurrentMonth()
        def curYear = monthUtil.getCurrentYear()
        if(filter.datefrom=='cur') filter.datefrom = monthUtil.getYearMonthDayStr(curYear, curMonth, 1, format)
        if(filter.dateto=='cur') filter.dateto = monthUtil.getYearMonthDayStr(curYear, curMonth, monthUtil.getLastDayOfAMonth(curYear,curMonth),format)

        super.checkParamsSetting(filter)
    }
}
