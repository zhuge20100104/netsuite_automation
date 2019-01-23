package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.base.excel.ParseExcel
import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.common.OW
import com.netsuite.common.P0
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
class CashflowCheckReportTableTest extends CashflowBaseTest {
    @Inject
    CashFlowStatementPage cashFlowStatementPage

    /**
     * @case 1.5.1.1.1
     * @author Stephen
     * @desc check the exported PDF file.
     *
     * */
    @Category([P0.class, OW.class])
    @Test
    void case_1_5_1_1_1() {
        cashFlowStatementPage.navigateToURL()

        String subId = Utility.getSubsidiaryId(context, "Cash Flow BU", null)
        cashFlowStatementPage.setParameters(subId, "Q1 2016", "Q3 2016")
        cashFlowStatementPage.setUnit("元")

        cashFlowStatementPage.clickRefresh()
        cashFlowStatementPage.exportPDF()

        ParsePDF pdfParser = new ParsePDF()
        //Verify the downloaded file
        String downloadedFile = pdfParser.getDownloadedFile()
        assertTrue("Download file name is correct", downloadedFile.contains("现金流量表"))

        //Verify the table information
        assertTrue("Subsidiary is correct.", pdfParser.getLineListWithKey("编制单位").get(0).contains("编制单位: Cash Flow BU"))
        assertTrue("Date periods are correct.", pdfParser.getLineListWithKey("编制单位").get(1).contains("Q1 2016 - Q3 2016"))
        assertTrue("Currency unit is correct.", pdfParser.getLineListWithKey("编制单位").get(2).contains("单位: CNY/元"))
    }

    /**
     * @case 1.5.1.1.2
     * @author Stephen
     * @desc check the exported PDF file.
     *
     * */
    @Category([P0.class, OW.class])
    @Test
    void case_1_5_1_1_2() {
        cashFlowStatementPage.navigateToURL()

        String subId = Utility.getSubsidiaryId(context, "Cash Flow BU", null)
        cashFlowStatementPage.setParameters(subId, "Q1 2016", "Q3 2016")
        cashFlowStatementPage.setUnit("元")

        cashFlowStatementPage.clickRefresh()
        cashFlowStatementPage.exportExcel()

        ParseExcel excelParser = new ParseExcel()

        String downloadedFile = excelParser.getDownloadedFile()
        assertTrue("Download file name is correct", downloadedFile.contains("现金流量表"))

        assertTrue("Subsidiary is correct.", excelParser.getLinesWithKey("Cash Flow BU").size() > 0)
        assertTrue("Date periods are correct.", excelParser.getLinesWithKey("Q1 2016 - Q3 2016").size() > 0)
        assertTrue("Currency unit is correct.", excelParser.getLinesWithKey("CNY/元").size() > 0)
    }

    @Override
    def getDefaultRole() {
        getAdministrator()
    }

}