package com.netsuite.chinalocalization.balancesheet

import com.netsuite.base.lib.os.OSUtil
import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.chinalocalization.page.Report.ReportPage
import com.netsuite.common.P2
import com.netsuite.common.P1
import com.netsuite.common.P0
import com.netsuite.common.P3
import com.netsuite.common.OW
import com.netsuite.common.disableClassifications
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject
@TestOwner("jingzhou.wang@oracle.com")
class BalanceSheetSavedReportTest extends BalanceSheetAppTestSuite {

    @Inject
    private ReportPage savedReportPage
    @Inject
    private BalanceSheetPage blsPage
    @Inject
    private EnableFeaturesPage enableFeaPage
    @Inject
    private OSUtil osUtil

    def jsonFile = "data/balancesheet/case_13.json"
    private static final int LINE_INDEX = 4

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    //Enable all custom filters before each test run
    @Before
    void setUp() {
        super.setUp()
        enableFeaPage.enableAllCustomFilters()
    }

    def verifyHeaderFilters(def caseObj){

        caseObj.report.customFilters.each { filters ->
            if(filters.label == "Location"){
                def expectedLocation = filters.text[0]
                def selectedFilters = trimText(blsPage.getLocation())
                def reportHeader = trimText(blsPage.getHeaderData(LINE_INDEX,expectedLocation))
                checkAreEqual("Filters in Header should remain", selectedFilters, expectedLocation)
                checkAreEqual("Filters not correct in reporter", selectedFilters, reportHeader)
            }
            if(filters.label == "Department"){
                def expectedDepartment = filters.text[0]
                def selectedFilters = trimText(blsPage.getDepartment())
                def reportHeader = trimText(blsPage.getHeaderData(LINE_INDEX,expectedDepartment))
                checkAreEqual("Filters in Header should remain", selectedFilters, expectedDepartment)
                checkAreEqual("Filters not correct in reporter", selectedFilters, reportHeader)
            }
            if(filters.label == "Class"){
                def expectedClass = filters.text[0]
                def selectedFilters = trimText(blsPage.getClazz())
                def reportHeader = trimText(blsPage.getHeaderData(LINE_INDEX,expectedClass))
                checkAreEqual("Filters in Header should remain", selectedFilters, expectedClass)
                checkAreEqual("Filters not correct in reporter", selectedFilters, reportHeader)
            }
        }

    }

    //Enable all custom filters after test suite run
    @SuiteTeardown
    void tearDownTestSuite() {
        enableFeaPage.enableAllCustomFilters()
        super.tearDownTestSuite()
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.0.1 All customer filter enabled
     *         Verify_change_subsidary_will_cause_custom_field_change
     */
    @Test
    @Category([OW,P0])
    void case_13_0_1() {

        def caseObj = getCaseObj(jsonFile, "testcase_13.0.1")

        blsPage.navigateToURL()
        blsPage.setSearchParameter(caseObj.param_1)
        Thread.sleep(3000)
        assertTrue("'财务部门' should in Department options", blsPage.getDepartmentOptions().contains("财务部门"))

        blsPage.setSearchParameter(caseObj.param_2)
        Thread.sleep(3000)
        assertTrue("'Department1_Japan BU' should in Department options", blsPage.getDepartmentOptions().contains("Department1_Japan BU"))
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.1.1 All customer filter enabled
     * @Modified jing.han@oracle.com
     */
    @Test
    @Category([OW,P2])
    void case_13_1_1() {
        def caseObj = getCaseObj(jsonFile, "testcase_13.1.1")

        //Get reportData from standard report
        changeLanguageToEnglish()
        def reportData = savedReportPage.getReportData(caseObj.report)
        changeToTargetLanguage()
        def actualData = blsPage.getSingleColBalanceSheetData(caseObj.report)

        //Compare expect and actual
        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)

        //NSCHINA-2143  Verify filters in header
        verifyHeaderFilters(caseObj)
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.1.2 All customer filter enabled
     */
    @Category([OW,P1])
    @Test
    void case_13_1_2() {
        def caseObj = getCaseObj(jsonFile, "testcase_13.1.2")

        //Get reportData from standard report
        changeLanguageToEnglish()
        def reportData = savedReportPage.getReportData(caseObj.report)
        changeToTargetLanguage()
        def actualData = blsPage.getSingleColBalanceSheetData(caseObj.report)

        //Compare expect and actual
        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)

        //NSCHINA-2143  Verify filters in header
        verifyHeaderFilters(caseObj)
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.1.3 All customer filter enabled
     */
    @Category([OW,P2])
    @Test
    void case_13_1_3() {
        def caseObj = getCaseObj(jsonFile, "testcase_13.1.3")

        //Get reportData from standard report
        changeLanguageToEnglish()
        def reportData = savedReportPage.getReportData(caseObj.report)
        changeToTargetLanguage()
        def actualData = blsPage.getSingleColBalanceSheetData(caseObj.report)

        //Compare expect and actual
        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }


    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.1.8 All customer filter enabled
     */
    @Category([OW,P2])
    @Test
    void case_13_1_8_all_customer_filter_enabled() {
        def caseObj = getCaseObj(jsonFile, "testcase_13.1.8")

        //Get reportData from standard report
        changeLanguageToEnglish()
        def reportData = savedReportPage.getReportData(caseObj.report)
        changeToTargetLanguage()
        def actualData = blsPage.getSingleColBalanceSheetData(caseObj.report)

        //Compare expect and actual
        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)
    }

    //Location could not be disable once enabled , so comments relative cases and develop the solution like run in other accounts

//    /**
//     * @Author jingzhou.wang@oracle.com
//     * @CaseID 13.2.1
//     * @Description: Enabled feature: Department, Class； Disable: Location
//     */
//    @Test
//    void "13.2.1"() {
//
//        //Use the same test json with 13.1.1
//        def caseObj = getCaseObj(jsonFile, "testcase_13.1.1")
//
//        //Enable  Department,Classes
//        //Disable Locations
//        enableFeaPage.navigateToURL()
//        enableFeaPage.enableDepartments()
//        enableFeaPage.enableClasses()
//        enableFeaPage.disbleLocations()
//        enableFeaPage.clickSave()
//
//        blsPage.navigateToURL()
//        assertFalse("Location Filter should not show", blsPage.isLoacationAppear())
//
//        //Get reportData from standard report
//        def reportData = savedReportPage.getReportData(caseObj.report)
//        //Get actualData from balance sheet report
//        def actualData = blsPage.getBalanceSheetData(caseObj.report)
//
//        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)
//    }
//
//    /**
//     * @Author jingzhou.wang@oracle.com
//     * @CaseID 13.2.2
//     * @Description: Enabled feature: Department, Class； Disable: Location
//     */
//    @Test
//    void "13.2.2"() {
//
//        //Use the same test json with 13.1.1
//        def caseObj = getCaseObj(jsonFile, "testcase_13.1.1")
//
//        //Enable  Department,Classes
//        //Disable Locations
//        enableFeaPage.navigateToURL()
//        enableFeaPage.enableDepartments()
//        enableFeaPage.enableClasses()
//        enableFeaPage.disbleLocations()
//        enableFeaPage.clickSave()
//
//        blsPage.navigateToURL()
//        assertFalse("Location Filter should not show", blsPage.isLoacationAppear())
//
//        //Get reportData from standard report
//        def reportData = savedReportPage.getReportData(caseObj.report)
//        //Get actualData from balance sheet report
//        def actualData = blsPage.getBalanceSheetData(caseObj.report)
//
//        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)
//    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.3.1
     * @Description: Enabled feature: Department, Location； Disable: Class
     */
    @Category([OW,P2,disableClassifications])
    @Test
    void case_13_3_1() {

        //Use the same test json with 13.1.1
        def caseObj = getCaseObj(jsonFile, "testcase_13.1.1")

        //Enable  Department,Location
        //Disable Class
        enableFeaPage.navigateToURL()
        enableFeaPage.enableDepartments()
        enableFeaPage.enableLocations()
        enableFeaPage.disbleClasses()
        enableFeaPage.clickSave()

        blsPage.navigateToURL()
        assertFalse("Class Filter should not show", blsPage.isClassAppear())

        changeLanguageToEnglish()
        def reportData = savedReportPage.getReportData(caseObj.report)
        changeToTargetLanguage()
        def actualData = blsPage.getSingleColBalanceSheetData(caseObj.report)

        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.3.2
     * @Description: Enabled feature: Department, Location； Disable: Class
     */
    @Category([OW,P2,disableClassifications])
    @Test
    void case_13_3_2() {

        //Use the same test json with 13.1.3
        def caseObj = getCaseObj(jsonFile, "testcase_13.1.3")

        //Enable  Department,Location
        //Disable Class
        enableFeaPage.navigateToURL()
        enableFeaPage.enableDepartments()
        enableFeaPage.enableLocations()
        enableFeaPage.disbleClasses()
        enableFeaPage.clickSave()

        blsPage.navigateToURL()
        assertFalse("Class Filter should not show", blsPage.isClassAppear())

        changeLanguageToEnglish()
        def reportData = savedReportPage.getReportData(caseObj.report)
        changeToTargetLanguage()
        def actualData = blsPage.getSingleColBalanceSheetData(caseObj.report)

        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.4.1
     * @Description: Enabled feature: Class, Location； Disable: Department
     */
    @Category([OW,P1,disableClassifications])
    @Test
    void case_13_4_1() {

        //Use the same test json with 13.1.2
        def caseObj = getCaseObj(jsonFile, "testcase_13.1.2")

        //Enable  Class,Location
        //Disable Department
        enableFeaPage.navigateToURL()
        enableFeaPage.disbleDepartments()
        enableFeaPage.enableLocations()
        enableFeaPage.enableClasses()
        enableFeaPage.clickSave()

        blsPage.navigateToURL()
        assertFalse("Department Filter should not show", blsPage.isDepartmentAppear())

        changeLanguageToEnglish()
        def reportData = savedReportPage.getReportData(caseObj.report)
        changeToTargetLanguage()
        def actualData = blsPage.getSingleColBalanceSheetData(caseObj.report)

        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID 13.4.2
     * @Description: Enabled feature: Class, Location； Disable: Department
     */
    @Category([OW,P2,disableClassifications])
    @Test
    void case_13_4_2() {

        //Use the same test json with 13.1.3
        def caseObj = getCaseObj(jsonFile, "testcase_13.1.3")

        //Enable  Class,Location
        //Disable Department
        enableFeaPage.navigateToURL()
        enableFeaPage.disbleDepartments()
        enableFeaPage.enableLocations()
        enableFeaPage.enableClasses()
        enableFeaPage.clickSave()

        blsPage.navigateToURL()
        assertFalse("Department Filter should not show", blsPage.isDepartmentAppear())

        changeLanguageToEnglish()
        def reportData = savedReportPage.getReportData(caseObj.report)
        changeToTargetLanguage()
        def actualData = blsPage.getSingleColBalanceSheetData(caseObj.report)

        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }

//    /**
//     * @Author jingzhou.wang@oracle.com
//     * @CaseID 13.5.1
//     * @Description: Disable: Location, Class，Department
//     */
//    @Test
//    void "13.5.1 All customer filter disabled"() {
//
//        //Use the same json of 13.1.8
//        def caseObj = getCaseObj(jsonFile, "testcase_13.1.8")
//
//        //Disable Department,Class,Location
//        enableFeaPage.navigateToURL()
//        enableFeaPage.disbleDepartments()
//        enableFeaPage.disbleLocations()
//        enableFeaPage.disbleClasses()
//        enableFeaPage.clickSave()
//
//        //Check bls report , all customize filter should not show
//        blsPage.navigateToURL()
//        assertFalse("Department Filter should not show", blsPage.isDepartmentAppear())
//        assertFalse("Location Filter should not show", blsPage.isLoacationAppear())
//        assertFalse("Class Filter should not show", blsPage.isClassAppear())
//
//        def actualData = blsPage.getBalanceSheetData(caseObj.report)
//        def reportData = savedReportPage.getReportData(caseObj.report)
//
//        // Compare expect and actual
//        assertAreEqual("Result are Not Equal between saved report and balance sheet Report", reportData, actualData)
//    }

    /**
     * @Author jing.han@oracle.com
     * @CaseID 13.6.1 Select Location & Department & Class
     * @Description: Enabled feature: Class, Location, Department
     */
    @Category([OW,P2,disableClassifications])
    @Test
    void case_13_6_1_3() {
        def caseObj = getCaseObj(jsonFile, "testcase_13.6.1")
        blsPage.refreshBalanceSheetData(caseObj.report)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }

    /**
     * @Author jing.han@oracle.com
     * @CaseID 13.6.2 Select Location & Department
     * @Description: Enabled feature: Class, Location, Department
     */
    @Category([OW,P2,disableClassifications])
    @Test
    void case_13_6_2_2_filters() {
        def caseObj = getCaseObj(jsonFile, "testcase_13.6.2")
        blsPage.refreshBalanceSheetData(caseObj.report)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }

    /**
     * @Author jing.han@oracle.com
     * @CaseID 13.6.3 Select Location & Class
     * @Description: Enabled feature: Class, Location, Department
     */
    @Category([OW,P2])
    @Test
    void case_13_6_3_2_filters() {
        def caseObj = getCaseObj(jsonFile, "testcase_13.6.3")
        blsPage.refreshBalanceSheetData(caseObj.report)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }

    /**
     * @Author jing.han@oracle.com
     * @CaseID 13.6.4 Select Department & Class
     * @Description: Enabled feature: Class, Location, Department
     */
    @Category([OW,P2])
    @Test
    void case_13_6_4_2() {
        def caseObj = getCaseObj(jsonFile, "testcase_13.6.4")
        blsPage.refreshBalanceSheetData(caseObj.report)

        //NSCHINA-2143 Verify filters in header
        verifyHeaderFilters(caseObj)
    }
}
