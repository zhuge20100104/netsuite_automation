package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.base.excel.ParseExcel
import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.common.P2
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Created by stepzhou on 5/8/2018.
 * @desc Used to verify subsidiaries settings.
 */
@TestOwner("stephen.zhou@oracle.com")
class CashflowReportTableTest extends CashflowBaseTest {
    @Inject
    CashFlowStatementPage cashFlowStatementPage

    /**
     * @case 1.5.2.2.1
     * @author Stephen
     * @desc verify the exported excel table.
     *
     * */
    @Category([P2.class, SI.class])
    @Test
    void case_1_5_2_2_1() {
        cashFlowStatementPage.navigateToURL()
        cashFlowStatementPage.setPeriodFrom("Jun 2016")
        cashFlowStatementPage.setPeriodTo("Jun 2016")
        cashFlowStatementPage.setUnit("Unit")
        cashFlowStatementPage.clickRefresh()
        cashFlowStatementPage.exportExcel()

        //Since we still can't verify the layout. I just verify if the key elements of the exported excel table.
        ParseExcel excelParser = new ParseExcel()
        assertTrue("Table name exists.", excelParser.getLinesWithKey("Cash Flow Statement").size() > 0)
        assertTrue("Period From and Period To exists.", excelParser.getLinesWithKey("Jun 2016 - Jun 2016").size() > 0)
        assertTrue("Currency unit exists.", excelParser.getLinesWithKey("GBP/Unit").size() > 0)
    }

    /**
     * @case 1.5.2.2.2
     * @author Stephen
     * @desc Verify exported PDF table
     *
     * */
    @Category([P2.class, SI.class])
    //@Test
    @Deprecated
    //We Run SI on English, so this case is deprecated
    void case_1_5_2_2_2() {
        cashFlowStatementPage.navigateToURL()
        cashFlowStatementPage.setPeriodFrom("Jun 2016")
        cashFlowStatementPage.setPeriodTo("Jun 2016")
        cashFlowStatementPage.setUnit("元")
        cashFlowStatementPage.clickRefresh()
        cashFlowStatementPage.exportPDF()

        //Since we still can't verify the layout. I just verify if the key elements of the exported PDF table.
        ParsePDF pdfParser = new ParsePDF()
        assertTrue("Including the specified Unit", pdfParser.getLineListWithKey("GBP/Unit") > 0)
        assertTrue("Including the specified Number", pdfParser.getLineListWithKey("-410,000.00").size() > 0)
    }

    @Override
    def getDefaultRole() {
        getAdministrator()
    }

    @Override
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\roles_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\roles_en_US.json"
        ]
    }

}