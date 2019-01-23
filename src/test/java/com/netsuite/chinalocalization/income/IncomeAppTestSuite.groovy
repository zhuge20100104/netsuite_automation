package com.netsuite.chinalocalization.income

import com.google.inject.Inject
import com.netsuite.base.excel.ExcelUtil
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.chinalocalization.page.Report.IncomeStatementPage
import com.netsuite.chinalocalization.page.Report.ReportPage
import com.netsuite.chinalocalization.page.Setup.CompanyInformationPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.chinalocalization.page.Setup.UserPreferencePage
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After
import org.junit.Before

/**
 * @desc Base test suite implementation for Income Statement
 * @author Jianwei Liu
 */
class IncomeAppTestSuite extends BaseAppTestSuite {

    @Inject
    ReportPage standardReportPage
    @Inject
    IncomeStatementPage incomeStatementPage
    @Inject
    EnableFeaturesPage enableFeaPage
    @Inject
    UserPreferencePage userPrePage
    @Inject
    IncomeLocators locators
    @Inject
    SubsidiaryPage subsidiaryPage
    @Inject
    CompanyInformationPage companyInfoPage
    @Inject
    ExcelUtil excelUtil



    def static testData

    def dirtyData


    def pathToTestSuiteFile() {
    }
   @SuiteSetup
    void setUpTestSuite() {
        super.setUpTestSuite()
       testData = data
       // Enable Classification: Location, Department, Class in Suite setup
       if(context.getPreference("REPORTBYPERIOD") != "FINANCIALS" ){
           userPrePage.navigateToURL()
           context.setFieldWithValue("REPORTBYPERIOD", "FINANCIALS" )
           userPrePage.clickSave()
       }

       if (!context.isAllClassificationEnabled()) {
           //switchToRole(administrator)
           enableFeaPage.enableAllCustomFilters()
       }
       // Enable PDF/HTML Advanced Feature
       if(!context.isFeatureEnabled("ADVANCEDPRINTING")){
           //switchToRole(administrator)
           enableFeaPage.navigateToURL()
           enableFeaPage.enableAdvancedPrinting()
           enableFeaPage.clickSave()
       }

    }


    def switchWindow() {
        context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
        def  currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
    }

    @Before
    void setUp() {
        super.setUp()
        println("setUp: process init ...")
        //if (!testData){
            def path = pathToTestDataFiles()
            if (path) {
                if (isTargetLanguageChinese()) {
                    if (doesFileExist(path.zhCN)) {
                        testData = context.asJSON(path: path.zhCN.replace("\\",SEP))

                    }
                } else {
                    if (doesFileExist(path.enUS)) {
                        testData = context.asJSON(path: path.enUS.replace("\\",SEP))

                    }
                }

            }
        //}

    }
    @After
    void tearDown() {
        println("tearDown: cleaning up dirty data...")
        switchWindow()

    }

    @SuiteTeardown
    void tearDownTestSuite() {
        println("tearDown in Test Suite: cleaning up dirty data...")

        // Enable Classification:Location, Department, Class
        if (!context.isAllClassificationEnabled()) {
            //switchToRole(administrator)
            enableFeaPage.enableAllCustomFilters()
        }

    }
    def pathToTestDataFiles() {}


    def getTestData(){
        testData
    }

    def navigateToPortalPage() {
        context.navigateTo(resolveSuiteletURL("customscript_sl_cn_income", "customdeploy_sl_cn_income"))
    }

    def setSearchParams(filter) {
        if (filter.reportName){

            def names = incomeStatementPage.getNameOptions()
            if( !names.contains(filter.reportName)){
                incomeStatementPage.addName(filter.reportName)
            }
            incomeStatementPage.selectName(filter.reportName)
            //incomeStatementPage.deleteName(name)
        }
        if (context.isOneWorld()&&filter.subsidiary) incomeStatementPage.asDropdownList(locator: incomeStatementPage.getXPATH_PARAM_SUBSIDIARY()).selectItem(filter.subsidiary)
        if (filter.period) incomeStatementPage.asDropdownList(locator: incomeStatementPage.getXPATH_PARAM_PERIOD()).selectItem(filter.period)
        if (filter.unit) incomeStatementPage.asDropdownList(locator: incomeStatementPage.getXPATH_PARAM_UNIT()).selectItem(filter.unit)
        if (filter.location) incomeStatementPage.asDropdownList(locator: incomeStatementPage.getXPATH_PARAM_LOCATION()).selectItem(filter.location)
        if (filter.department) incomeStatementPage.asDropdownList(locator: incomeStatementPage.getXPATH_PARAM_DEPARTMENT()).selectItem(filter.department)
        if (filter.class) incomeStatementPage.asDropdownList(locator: incomeStatementPage.getXPATH_PARAM_CLASS()).selectItem(filter.class)
   }


    def monthList = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"] as String[]
    def getCurValue(){
        Calendar cale = Calendar.getInstance()
        def year = cale.get(Calendar.YEAR)
        def month = cale.get(Calendar.MONTH)
        return (monthList[month]+" "+Integer.toString(year))
    }
    def checkParamsSetting(filter){
        def cur = getCurValue()
        if(filter.period.equals("cur")){filter.period = cur}
        for(param in filter){
            if(context.isSingleInstance()){
                continue
            }
            assertAreEqual("${param.key} value check  ", trimText(context.getFieldText("custpage_${param.key}")),param.value)
        }
    }
    def trimText(text) {
        return text.trim().replaceAll("[\\u00A0]+", "")
    }
    def clickRefresh() {
        incomeStatementPage.clickRefresh()
    }

    def getSavedReport(report) {
        standardReportPage.getReportDataForIS(report)
    }

    def getISReport(report) {
        incomeStatementPage.getTableValue(report.rows)
    }
    def clickExportExcel() {
        incomeStatementPage.clickExpToExcel()
    }
    def clickExportPDF() {
        incomeStatementPage.clickExpToPDF()
    }
    def checkSubsidiaryFiscalCalendar(subsidiaryName){
        //switchToRole(administrator)
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName)
        def calendar = subsidiaryPage.getFiscalCalendarTex()
         //switchToRole(accountant)
        return calendar
    }
    def clickErrorDialogOk(){
        incomeStatementPage.clickErrorMessageOkButton()
    }
    def setSubsidiaryFiscalCalendar(subsidiaryName,calendarName){
        //switchToRole(administrator)
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName)
        subsidiaryPage.setFiscalCalendarTex(calendarName)
        subsidiaryPage.clickSave()
         //switchToRole(accountant)
    }










}
