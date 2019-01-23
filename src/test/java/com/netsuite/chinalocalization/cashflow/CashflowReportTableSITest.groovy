package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.base.excel.ParseExcel
import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Created by stepzhou on 5/9/2018.
 * @desc Check the exported file
 */
@TestOwner("stephen.zhou@oracle.com")
class CashflowReportTableSITest extends CashflowBaseTest {
    @Inject
    CashFlowStatementPage cashFlowStatementPage

    /**
     * @case 1.5.1.2.1
     * @author Stephen
     * @desc Check the exported PDF file in SI.
     *
     * */
    @Category([P2.class, SI.class])
    @Test
    void case_1_5_1_2_1() {
        cashFlowStatementPage.navigateToURL()

        cashFlowStatementPage.setPeriodFrom("Jan 2016")
        cashFlowStatementPage.setPeriodTo("Jun 2016")
        cashFlowStatementPage.setUnit("Thousand")

        cashFlowStatementPage.clickRefresh()
        cashFlowStatementPage.exportPDF()

        ParsePDF pdfParser = new ParsePDF()
        //Verify the downloaded file
        String downloadedFile = pdfParser.getDownloadedFile()
        assertTrue("Download file name is correct", downloadedFile.contains("Cash Flow Statement"))
    }

    /**
     * @case 1.5.1.2.2
     * @author Stephen
     * @desc Check the exported Excel file in SI.
     *
     * */
    @Category([P2.class, SI.class])
    @Test
    //We Run SI on English, so this case is deprecated
    void case_1_5_1_2_2() {
        cashFlowStatementPage.navigateToURL()

        cashFlowStatementPage.setPeriodFrom("Jan 2016")
        cashFlowStatementPage.setPeriodTo("Jun 2016")

        cashFlowStatementPage.clickRefresh()
        cashFlowStatementPage.exportExcel()

        ParseExcel excelParser = new ParseExcel()

        String downloadedFile = excelParser.getDownloadedFile()
        assertTrue("Download file name is correct", downloadedFile.contains("Cash Flow Statement"))
        assertTrue("Date periods are correct.", excelParser.getLinesWithKey("Jan 2016 - Jun 2016").size() > 0)
    }

    @Override
    def getDefaultRole() {
        getAdministrator()
    }

}