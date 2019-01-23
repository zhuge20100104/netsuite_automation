package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.page.BalanceSheetPage
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
class BalanceSheetEmptyLineTest extends BalanceSheetAppTestSuite{

    @Inject
    private ReportPage savedReportPage
    @Inject
    private BalanceSheetPage blsPage


    @Override
    def getDefaultRole() {
        return getAdministrator()
    }
    /**
     * @desc Test no empty line display on both sides of customized report
     * @Author kun.y.yang@oracle.com
     * @CaseID 3.1.2.1.1verify no empty line display
     * @modify Molly feng, no need to check report data,
     *                      change report filter to case22
     */
    @Category([P3.class, OWAndSI.class, CN.class])
    @Test
    void case_3_1_2_1_1() {

        //Get actualData from balance sheet report
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_3_1_2_1_1.report)
        //validate no empty line on both side
        def reportContent = reportContent()
        def isEmptyLineExist = false
        for(int i = 0; i < reportContent.size(); i++) {
            def row = reportContent.get(i)
            if (row.get("" +Index.ASSETS.value).isEmpty()|| row.get("" + Index.LIABILITIES.value).isEmpty()) {
                isEmptyLineExist = true
                return
            }
        }
        assertFalse("There is no empty line", isEmptyLineExist)

    }


    /**
     * @desc Test empty lines are filled on top of [Total liabilities and owner's equity]
     * @Author kun.y.yang@oracle.com
     * @CaseID 3.1.3.1.1verify empty line filled on the right side
     * @upate Molly Feng, we use the ReportCase2 in this case instead of case3
     */
    @Category([P2.class, OWAndSI.class, CN.class])
    @Test
    void case_3_1_3_1_1() {
        //Get actualData from balance sheet report
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_3_1_3_1_1.report)
        //verify empty lines are filled on top of [Total liabilities and owner's equity].
        def firstEmptyLine = false
        def secondEmptyLine = false
        def thirdEmptyLine = false
        def reportContent = reportContent()

        // actually, only need to check there is one or more empty line on the right
        def secondRow = reportContent.get(reportContent.size() - 2)
        if(secondRow.get("" + 4).isEmpty() && secondRow.get("" + 6).isEmpty() && secondRow.get("" + 7).isEmpty()) {
            secondEmptyLine = true
        }

        assertTrue("There aren't empty lines on the right side", secondEmptyLine)
    }


    /**
     * @desc Test when all fixed rows and columns in custom report are disordered, custom report display correctly.
     * @Author kun.y.yang@oracle.com
     * @CaseID 3.2.2.2.1.1verify all rows and columns in custom report display correctly
     */
    @Category([P3.class, OWAndSI.class, CN.class])
    @Test
    void case_3_2_2_2_1_1() {

        //Get reportData from saved report
        savedReportPage.navigateToSavedReportPage(data.test_case_3_2_2_2_1_1.report.reportName)
        savedReportPage.selectSubsidiary(data.test_case_3_2_2_2_1_1.report.subsidiary)
        savedReportPage.selectASOF(data.test_case_3_2_2_2_1_1.report.asof)
        savedReportPage.clickRefresh()
        Map<String,List<String>> balanceMap = savedReportPage.getCommonReportdata()

        //Get actualData from balance sheet report
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_3_2_2_2_1_1.report)

        //validate report header
        verifyReportHeader(data.test_case_3_2_2_2_1_1)
        //verify report content is correct
        def reportContent = reportContent()
        verifyReportContent(reportContent, balanceMap, data.test_case_3_2_2_2_1_1)
    }

    /**
     * @desc Test add rows to saved report.
     * @Author kun.y.yang@oracle.com
     * @CaseID 3.2.2.4.1.1add rows to saved report
     */
    @Category([P3.class, OWAndSI.class, CN.class])
    @Test
    void case_3_2_2_4_1_1() {
        //Get actualData from balance sheet report
        blsPage.navigateToBalanceSheetPageAndSetFilters(data.test_case_3_2_2_4_1_1.report)

        //validate report header
        verifyReportHeader(data.test_case_3_2_2_4_1_1)
       //verify new added line exist
        def reportContent = reportContent()
        // line under assets should not display
        def newHeaderExist = false
        def newLineExist = false
        // new added line under current assets should be displayed
        def newLineInCurAsset = false
        for(int i = 0; i < reportContent.size(); i++) {
            def row = reportContent.get(i)
            for(int j = 0; j < row.size(); j++) {
                if(trimText(row.get("" + j)).equals(data.test_case_3_2_2_4_1_1.expected.newHeader)) {
                    newHeaderExist = true
                } else if(trimText(row.get("" + j)).equals(data.test_case_3_2_2_4_1_1.expected.newLine)) {
                    newLineExist = true
                } else if (trimText(row.get("" + j)).equals(data.test_case_3_2_2_4_1_1.expected.newLineDisplay)) {
                    newLineInCurAsset = true
                }
            }
        }

        assertFalse("New added line is not found", newLineExist)

        assertFalse("New added header doesn't exist", newHeaderExist)
        assertTrue("New added line is found", newLineInCurAsset)
    }

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BalanceSheetEmptyLineTest_data_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BalanceSheetEmptyLineTest_data_en_US.json"
        ]
    }

}
