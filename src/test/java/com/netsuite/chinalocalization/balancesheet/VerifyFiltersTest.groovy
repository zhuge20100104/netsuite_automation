package com.netsuite.chinalocalization.balancesheet

import com.netsuite.base.excel.ParseExcel
import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import javax.inject.Inject
import org.junit.experimental.categories.Category

@TestOwner("stephen.zhou@oracle.com")
class VerifyFiltersTest extends BalanceSheetAppTestSuite {

    @Inject
    private BalanceSheetPage blsPage

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @desc Verify if the filters take effect in PDF output.
     * @Author stephen.zhou@oracle.com
     * @CaseID 13.1.9 , 13.1.10, 13.1.11, 13.1.12 (PDF part)_Verify_filters_in_PDF_output_files
     */
    @Category([P2.class])
    @Test
    void case_13_1_9() {
        //Check the output when there's no filters.
        blsPage.navigateToURL()
        blsPage.fillBalanceSheetName("中国资产负债表模板_bl03_Filter")
        blsPage.selectSubsidiary("China BlSheet 03")
        blsPage.clickRefresh()
        waitForPageToLoad()
        blsPage.exportPDF()
        ParsePDF pdfParser = new ParsePDF()
        assertTrue("No location settings", pdfParser.getValueFromKey("地点").equalsIgnoreCase(""))
        assertTrue("No department settings", pdfParser.getValueFromKey("部门").equalsIgnoreCase(""))
        assertTrue("No class settings", pdfParser.getValueFromKey("类别").equalsIgnoreCase(""))

        //Check the output when there are filters.
        blsPage.setClazz("Paas")
        blsPage.setDepartment("采购部门")
        blsPage.setLocation("华南地区")
        blsPage.clickRefresh()
        waitForPageToLoad()
        blsPage.exportPDF()
        pdfParser = new ParsePDF()
        assertTrue("Incorrect location settings", pdfParser.getValueFromKey("地点").equalsIgnoreCase("华南地区"))
        assertTrue("Incorrect department settings", pdfParser.getValueFromKey("部门").equalsIgnoreCase("采购部门"))
        assertTrue("Incorrect class settings", pdfParser.getValueFromKey("类别").equalsIgnoreCase("Paas"))
    }

    /**
     * @desc Verify if the filters take effect in PDF output.
     * @Author stephen.zhou@oracle.com
     * @CaseID 13.1.9 , 13.1.10, 13.1.11, 13.1.12 (Excel part)Verify filters in Excel output files
     */
    @Category([P2.class])
    @Test
    void case_13_1_11() {
        //Check the output when there's no filters.
        blsPage.navigateToURL()
        blsPage.fillBalanceSheetName("中国资产负债表模板_bl03_Filter")
        blsPage.selectSubsidiary("China BlSheet 03")
        blsPage.clickRefresh()
        waitForPageToLoad()
        blsPage.exportExcel()
        ParseExcel excelParser = new ParseExcel()
        assertFalse("No location settings", excelParser.isKeyValueCorrect("地点", "华南地区"))
        assertFalse("No department settings", excelParser.isKeyValueCorrect("部门", "采购部门"))
        assertFalse("No class settings", excelParser.isKeyValueCorrect("类别", "Paas"))

        //Check the output when there are filters.
        blsPage.setClazz("Paas")
        blsPage.setDepartment("采购部门")
        blsPage.setLocation("华南地区")
        blsPage.clickRefresh()
        waitForPageToLoad()
        blsPage.exportExcel()
        excelParser = new ParseExcel()
        assertTrue("Incorrect location settings", excelParser.isKeyValueCorrect("地点", "华南地区"))
        assertTrue("Incorrect department settings", excelParser.isKeyValueCorrect("部门", "采购部门"))
        assertTrue("Incorrect class settings", excelParser.isKeyValueCorrect("类别", "Paas"))
    }

}
