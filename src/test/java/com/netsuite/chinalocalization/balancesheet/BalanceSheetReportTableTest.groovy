package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.chinalocalization.page.Report.ReportPage
import com.netsuite.common.CN
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category
import com.netsuite.common.OWAndSI
import com.netsuite.common.P2

import javax.inject.Inject
@TestOwner ("kun.y.yang@oracle.com")
class BalanceSheetReportTableTest extends BalanceSheetAppTestSuite{
    @Inject
    private ReportPage savedReportPage
    @Inject
    private BalanceSheetPage blsPage
    @Inject
    private EnableFeaturesPage enableFeaPage

    //XPATH
    def popupDialogTitle = "//*[@id=\"ext-gen3\"]"
    def popupDialogText = "//*[@id='ext-gen4']/div/span"
    def confirmBtn = "//*[@id='ext-gen4]/div/div/button"


    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @desc Verify custom report display correctly when choosing one quarter
     * @Author kun.y.yang@oracle.com
     * @CaseID 3.4.2.1.1check report table for one quarter
     */
    @Category([P2.class, OWAndSI.class, CN.class])
    @Test
    void case_3_4_2_1_1() {
        //Get reportData from saved report
        savedReportPage.navigateToSavedReportPage(data.test_case_3_4_2_1_1.report.reportName)
        if(context.isOneWorld()){
            savedReportPage.selectSubsidiary(data.test_case_3_4_2_1_1.report.subsidiary)
        }

        savedReportPage.selectASOF(data.test_case_3_4_2_1_1.report.asof)
        savedReportPage.clickRefresh()

        Map<String,List<String>> balanceMap = savedReportPage.getCommonReportdata()

        //Get actualData from balance sheet report
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_3_4_2_1_1.report)

        //validate report header
        verifyReportHeader(data.test_case_3_4_2_1_1);

        def reportContent = reportContent()

        //verify report content is correct
        verifyReportContent(reportContent, balanceMap, data.test_case_3_4_2_1_1)

    }
/**
     * @desc Verify custom report display correctly
     * @Author kun.y.yang@oracle.com
     * @CaseID 3.4.3.1.1check report table for one fiscal year
     */
    @Category([P2.class, OWAndSI.class, CN.class])
    @Test
    void case_3_4_3_1_1() {
        //Get reportData from saved report
        savedReportPage.navigateToSavedReportPage(data.test_case_3_4_3_1_1.report.reportName)
        if(context.isOneWorld()){
            savedReportPage.selectSubsidiary(data.test_case_3_4_3_1_1.report.subsidiary)
        }

        savedReportPage.selectASOF(data.test_case_3_4_3_1_1.report.asof)
        savedReportPage.clickRefresh()

        Map<String,List<String>> balanceMap = savedReportPage.getCommonReportdata()

        //Get actualData from balance sheet report
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_3_4_3_1_1.report)

        //validate report header
        verifyReportHeader(data.test_case_3_4_3_1_1);

        def reportContent = reportContent()

        //verify report content is correct
        verifyReportContent(reportContent, balanceMap, data.test_case_3_4_3_1_1)

    }

    /**
     * @desc Verify when no transaction data in specific period, the Opening Balance and Closing Balance amount are equal in this specific period.
     * @Author kun.y.yang@oracle.com
     * @CaseID 3.5.1.1check opening balance and closing balance amount are equal
     */
    @Category([P3.class, OWAndSI.class, CN.class])
    @Test
    void case_3_5_1_1() {

        //Get actualData from balance sheet report
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_3_5_1_1.report)

        //validate report header
        verifyReportHeader(data.test_case_3_5_1_1);

        def reportContent = reportContent()

        //verify closing balance and opening balance are equal.
        for (int i = 0; i < reportContent.size(); i++) {
            def row = reportContent.get(i)
            String value1 = row.get("" + 2)
            String value2 = row.get("" + 3)
            assertAreEqual("closing balance and opening balance of assets are equal", row.get("" + 2), row.get("" + 3))
            assertAreEqual("closing balance and opening balance of liabilities are equal", row.get("" + 6), row.get("" + 7))

        }
    }
    /**
     * @desc Verify error message is displayed when advanced PDF/HTML is not enabled.
     * @Author kun.y.yang@oracle.com
     * @CaseID 8.1.1.1check setup setting error
     */
    @Category([P3.class, OWAndSI.class, CN.class])
    //@Test
    void case_8_1_1_1() {
        //uncheck the advanced PDF/HTML Templates configuration
        ChangeStateOfAdvancedPdfHTML(false)
        //navigate to balance sheet report
        blsPage.navigateToURL()
        waitForPageToLoad()
        //verify the error message
        def title = asElement(popupDialogTitle).getAsText()
        def errorMessage = asElement(popupDialogText).getAsText()
        assertAreEqual("error title:", title, data.test_case_8_1_1_1.error.title)
        assertAreEqual("error message display correctly", errorMessage, data.test_case_8_1_1_1.error.text)
        asElement(confirmBtn).click()

        waitForPageToLoad()

        //verify refresh button is disable

        ChangeStateOfAdvancedPdfHTML(true)
    }
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BalanceSheetReportTableTest_data_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BalanceSheetReportTableTest_data_en_US.json"
        ]
    }

    void ChangeStateOfAdvancedPdfHTML(enable) {
        enableFeaPage.navigateToURL()
        asElement(enableFeaPage.XPATH_SUITE_CLOUD).click()
        if(enable) {
            enableFeaPage.enableAdvancedPrinting()
            context.webDriver.switchToWindowWithTitle(data.test_case_8_1_1_1.netSuite_terms_of_service.terms)
            asElement("//*[@id=\"agree\"]").click()
            context.webDriver.switchToWindowWithTitle(enableFeaPage.TITLE)
        } else {
            enableFeaPage.disableAdvancedPrinting()
        }
        enableFeaPage.clickSave()
    }

}
