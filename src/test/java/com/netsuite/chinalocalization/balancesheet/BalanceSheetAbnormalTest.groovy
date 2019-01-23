package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.chinalocalization.page.Report.ReportPage
import com.netsuite.common.CN
import com.netsuite.common.OW
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import javax.inject.Inject
import org.junit.experimental.categories.Category
import com.netsuite.common.OWAndSI
import com.netsuite.common.P2

@TestOwner ("kun.y.yang@oracle.com")
class BalanceSheetAbnormalTest extends BalanceSheetAppTestSuite{
    @Inject
    private ReportPage savedReportPage
    @Inject
    private BalanceSheetPage blsPage
    @Inject
    private EnableFeaturesPage enableFeaPage

    //XPATH
    private static final String ERROR_TITLE = "//*[@id='ext-gen8']"
    private static final String ERROR_TEXT = "//*[@id=\"ext-gen4\"]/div/span"
    private static final String CONFIRM_BTN = "//*[@id=\"ext-gen4\"]/div/div/button"
/*    @Override
    def getDefaultRole() {
        return getAdministrator()
    }*/
    /**
     * @desc Verify when balance sheet name is empty, error message is displayed in a dialog
     * @Author kun.y.yang@oracle.com
     * @CaseID 8.2.1.1set empty balance sheet name
     */
    @Category([P2.class, OW.class, CN.class])
    @Test
    void case_8_2_1_1() {
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_8_2_1_1.report)
        assertErrorMessage(data.test_case_8_2_1_1.error)
        assertFalse("report is not loaded", blsPage.isReportAppear())
        assertFalse("Export Excel button is not appear", blsPage.isExportExcelAppear())
        assertFalse("Export PDF button is not appear", blsPage.isExportPDFAppear())
    }


    /**
     * @desc empty input after report is displayed
     * @Author kun.y.yang@oracle.com
     * @CaseID 8.2.1.2empty input after report is displayed
     */
    @Category([P3.class, OW.class,CN.class])
//    @Test
    void case_8_2_1_2() {
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_8_2_1_2.report)
        waitForPageToLoad()
        assertTrue("report is loaded", blsPage.isReportAppear())
        //empty the balance sheet name
        blsPage.emptyBalanceSheetName()
        waitForPageToLoad()
        blsPage.clickRefresh()
        //verify error message
        assertErrorMessage(data.test_case_8_2_1_2.error)
        //verify report is still exist, export excel button and export pdf button are visible
        assertTrue("report is still loaded", blsPage.isReportAppear())
        assertTrue("Export Excel button is appear", blsPage.isExportExcelAppear())
        assertTrue("Export PDF button is appear", blsPage.isExportPDFAppear())
    }

    /**
     * @desc Balance Sheet Name is not existed in the environment
     * @Author kun.y.yang@oracle.com
     * @CaseID 8.2.1.3balance sheet name is not existed in the environment
     */
    @Category([P2.class, OW.class,CN.class])
    @Test
    void case_8_2_1_3() {
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_8_2_1_3.report)
        waitForPageToLoad()
        assertErrorMessage(data.test_case_8_2_1_3.error)
        assertFalse("report is not loaded", blsPage.isReportAppear())
        assertFalse("Export Excel button is not appear", blsPage.isExportExcelAppear())
        assertFalse("Export PDF button is not appear", blsPage.isExportPDFAppear())

    }

    /**
     * @desc Not existed custom report after report is displayed
     * @Author kun.y.yang@oracle.com
     * @CaseID 8.2.1.4Change name to not existed one after report is displayed
     */
    @Category([P3.class, OW.class, CN.class])
//    @Test
    void case_8_2_1_4() {
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_8_2_1_4.normal)
        waitForPageToLoad()
        assertTrue("report is loaded", blsPage.isReportAppear())
        //empty the balance sheet name
        blsPage.emptyBalanceSheetName()
        blsPage.fillBalanceSheetName(data.test_case_8_2_1_4.error.reportName)
        waitForPageToLoad()
        blsPage.clickRefresh()
        //verify error message
        assertErrorMessage(data.test_case_8_2_1_4.error)
        //verify report is still exist, export excel button and export pdf button are visible
        assertTrue("report is still loaded", blsPage.isReportAppear())
        assertTrue("Export Excel button is appear", blsPage.isExportExcelAppear())
        assertTrue("Export PDF button is appear", blsPage.isExportPDFAppear())
    }

    /**
     * @desc custom report is not defined properly. All fixed rows are not defined properly.
     * @Author kun.y.yang@oracle.com
     * @CaseID 8.3.1.1all fixed rows are not defined properly
     */
    @Category([P2.class, OW.class, CN.class])
    @Test
    void case_8_3_1_1() {

        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_8_3_1_1.normal)
        waitForPageToLoad()
        //verify error message
        assertErrorMessage(data.test_case_8_3_1_1.error)
        //verify report is still exist, export excel button and export pdf button are visible
        assertTrue("report is still loaded", blsPage.isReportAppear())
        assertTrue("Export Excel button is appear", blsPage.isExportExcelAppear())
        assertTrue("Export PDF button is appear", blsPage.isExportPDFAppear())
    }

    /**
     * @desc custom reprot is not defined properly. All fixed columns are not defined properly
     * @Author kun.y.yang@oracle.com
     * @CaseID 8.3.1.2all fixed columns are not defined properly.
     */
    @Category([P3.class, OW.class, CN.class])
    @Test
    void case_8_3_1_2() {

        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_8_3_1_2.normal)
        waitForPageToLoad()
        //verify error message
        assertErrorMessage(data.test_case_8_3_1_2.error)
        //verify report is still exist, export excel button and export pdf button are visible
        assertTrue("report is still loaded", blsPage.isReportAppear())
        assertTrue("Export Excel button is appear", blsPage.isExportExcelAppear())
        assertTrue("Export PDF button is appear", blsPage.isExportPDFAppear())
    }


    void assertErrorMessage(def expectedData) {
        def title = asElement(ERROR_TITLE).getAsText()
        def text = asElement(ERROR_TEXT).getAsText()
        assertAreEqual("error message title display", title, expectedData.title)
        assertAreEqual("error message text is correct", text, expectedData.text)
        asElement(CONFIRM_BTN).click()
    }
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BalanceSheetAbnormalTest_data_zh_CN.json".replace("\\",SEP),
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BalanceSheetAbnormalTest_data_en_US.json".replace("\\",SEP)
        ]
    }


}
