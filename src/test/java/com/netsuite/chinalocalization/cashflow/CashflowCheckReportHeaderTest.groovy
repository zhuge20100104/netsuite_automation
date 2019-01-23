package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.base.excel.ParseExcel
import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.chinalocalization.page.common.UnrealizedExchangePage
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P2
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Created by stepzhou on 5/8/2018.
 * @desc Check the exported PDF/Excel files header.
 */
@TestOwner("stephen.zhou@oracle.com")
class CashflowCheckReportHeaderTest extends CashflowBaseTest {
    @Inject
    CashFlowStatementPage cashFlowStatementPage

    /**
     * @case 1.4.1.1.1 , 1.4.1.1.2
     * @author Stephen
     * @desc check the exported PDF file header.
     *
     * */
    @Category([P0.class, OW.class])
    @Test
    void case_1_4_1_1_1() {
        cashFlowStatementPage.navigateToURL()

        cashFlowStatementPage.setSubsidiaryByName("China BU")
        cashFlowStatementPage.setPeriodFrom("May 2018")
        cashFlowStatementPage.setPeriodTo("May 2018")
        cashFlowStatementPage.setUnit("元")

        cashFlowStatementPage.clickRefresh()
        cashFlowStatementPage.exportPDF()

        ParsePDF pdfParser = new ParsePDF()
        assertTrue("Subsidiary is correct.", pdfParser.getLineListWithKey("编制单位").get(0).contains("编制单位: China BU"))
        assertTrue("Date periods are correct.", pdfParser.getLineListWithKey("编制单位").get(1).contains("May 2018 - May 2018"))
        assertTrue("Currency unit is correct.", pdfParser.getLineListWithKey("编制单位").get(2).contains("单位: CNY/元"))
    }

    /**
     * @case 1.4.1.1.1 , 1.4.1.1.2
     * @author Stephen
     * @desc check the exported Excel file header.
     *
     * */
    @Category([P2.class, OW.class])
    @Test
    void case_1_4_1_1_2() {
        cashFlowStatementPage.navigateToURL()

        cashFlowStatementPage.setSubsidiaryByName("China BU")
        cashFlowStatementPage.setPeriodFrom("May 2018")
        cashFlowStatementPage.setPeriodTo("May 2018")
        cashFlowStatementPage.setUnit("元")

        cashFlowStatementPage.clickRefresh()
        cashFlowStatementPage.exportExcel()

        ParseExcel excelParser = new ParseExcel()
        assertTrue("Subsidiary is correct.", excelParser.isKeyValueCorrect("编制单位", "China BU"))
        assertTrue("Date periods are correct.", excelParser.isKeyValueCorrect("May 2018", "May 2018"))
        assertTrue("Currency unit is correct.", excelParser.isKeyValueCorrect("单位", "CNY/元"))
    }

    @Override
    def getDefaultRole() {
        getAdministrator()
    }

}