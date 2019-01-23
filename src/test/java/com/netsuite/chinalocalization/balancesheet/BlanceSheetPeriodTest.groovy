package com.netsuite.chinalocalization.balancesheet

import com.netsuite.base.excel.ExcelUtil
import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.chinalocalization.page.Report.ReportPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject
import java.text.SimpleDateFormat

@TestOwner ("hui.hu.wang@oracle.com")
class BlanceSheetPeriodTest extends BalanceSheetAppTestSuite{

    @Inject
    private BalanceSheetPage blsPage
    @Inject
    private SubsidiaryPage subPage
    @Inject
    private ReportPage repPage

    @com.google.inject.Inject
    ExcelUtil excelUtil


    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @desc Show error message when running custom report with non-existing period
     * @Author hui.hu.wang@oracle.com
     * @CaseID 8.5.1.1 running custom report with non-existing period
     */
    @Test
    @Category([OW.class, P2.class])
    void case_8_5_1_1(){
        if(context.isOneWorld()){
            subPage.navigateToPage(data.test_case_8_5_1_1.subsidiary.name, true)
            def fCalendar = subPage.getFiscalCalendarTex()
            subPage.setFiscalCalendarTex(data.test_case_8_5_1_1.subsidiary.fiscalcalendar)
            subPage.clickSave()

             //switchToRole(accountant)
            blsPage.navigateToURL()
            blsPage.setSearchParameter(data.test_case_8_5_1_1.filter)
            blsPage.clickRefresh()
            waitForPageToLoad()
            checkTrue("check message when report with non-existing period",
                    context.isTextVisible(data.test_case_8_5_1_1.nonperiodmessage))
            okButtonInAlertMessage().click()


            //set back the fiscal calendar value
            //switchToRole(administrator)
            subPage.navigateToPage(data.test_case_8_5_1_1.subsidiary.name, true)
            subPage.setFiscalCalendarTex(fCalendar)
            subPage.clickSave()
        }
    }

    /**
     * @desc First Load without period in multi-calendar
     * @Author hui.hu.wang@oracle.com
     * @CaseID 10.1.1 first load without period in multi-calendar
     */
    @Test
    @Category([OW.class, P2.class])
    void case_10_1_1(){
        if (context.isOneWorld()){
            subPage.navigateToPage(data.test_case_10_1_1.subsidiary.name, true)
            def fCalendar = subPage.getFiscalCalendarTex()
            subPage.setFiscalCalendarTex(data.test_case_10_1_1.subsidiary.fiscalcalendar)
            subPage.clickSave()

             //switchToRole(accountant)
            blsPage.navigateToURL()
            checkAreEqual("Check the AsOf value is empty", context.getFieldText(BalanceSheetPage.FIELD_ID_ASOF), "")

            //set back the fiscal calendar value
            //switchToRole(administrator)
            subPage.navigateToPage(data.test_case_10_1_1.subsidiary.name, true)
            subPage.setFiscalCalendarTex(fCalendar)
            subPage.clickSave()
        }
    }

    /**
     * @desc First Load with current month period opened
     * @Author kun.y.yang@oracle.com
     * @CaseID 10.1.3 First Load with current month period opened
     */
    @Test
    @Category([OW.class, P2.class])
    void case_10_1_3() {
        if(context.isOneWorld()) {
            subPage.navigateToPage(data.test_case_10_1_3.subsidiary.name, true)
            subPage.setFiscalCalendarTex(data.test_case_10_1_3.subsidiary.fiscalcalendar)
            subPage.clickSave()

             //switchToRole(accountant)
            blsPage.navigateToURL()
            Date now = new Date()
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
            String currentDate = dateFormat.format(now)
            checkAreEqual("Check the AsOf value is empty", trimText(context.getFieldText(BalanceSheetPage.FIELD_ID_ASOF)), currentDate)
        }
    }
    /**
     *  Report of last month of fiscal year in Oracle Fiscal Calendar
     */
    @Test
    @Category([OW.class, P3.class])
    void case_10_2_5(){
        if(context.isOneWorld()){
            subPage.navigateToPage(data.test_case_10_2_5.subsidiary.name, true)
            def fCalendar = subPage.getFiscalCalendarTex()
            subPage.setFiscalCalendarTex(data.test_case_10_2_5.subsidiary.fiscalcalendar)
            subPage.clickSave()

            //get line values from saved report
             //switchToRole(accountant)
            changeLanguageToEnglish()
            def savedReportData = repPage.getReportData(data.test_case_10_2_5.savedReport)
            def blsReportData = blsPage.getBalanceSheetData(data.test_case_10_2_5.blsReport)
            checkReportDataEqual(blsReportData, savedReportData)

            //set back the fiscal calendar value
            //switchToRole(administrator)
            subPage.navigateToPage(data.test_case_10_2_5.subsidiary.name, true)
            subPage.setFiscalCalendarTex(fCalendar)
            subPage.clickSave()
        }
    }
    /**
     * 10.2.7 Report of first month of fiscal year in Oracle Fiscal Calendar
     */
    @Test
    @Category([OW.class, P2.class])
    void case_10_2_7(){
        if(context.isOneWorld()){
            subPage.navigateToPage(data.test_case_10_2_7.subsidiary.name, true)
            def fCalendar = subPage.getFiscalCalendarTex()
            subPage.setFiscalCalendarTex(data.test_case_10_2_7.subsidiary.fiscalcalendar)
            subPage.clickSave()

            //get line values from saved report
             //switchToRole(accountant)
            changeLanguageToEnglish()
            def savedReportData = repPage.getReportData(data.test_case_10_2_7.savedReport)
            def blsReportData = blsPage.getBalanceSheetData(data.test_case_10_2_7.blsReport)
            checkReportDataEqual(blsReportData, savedReportData)

            //set back the fiscal calendar value
            /*
            **delete for test timed out after 900000 milliseconds
            //switchToRole(administrator)
            subPage.navigateToPage(data.test_case_10_2_7.subsidiary.name, true)
            subPage.setFiscalCalendarTex(fCalendar)
            subPage.clickSave()*/
        }
    }
    /**
     * 10.2.8 Report of fiscal year in Oracle Fiscal Calendar
     */
    @Test
    @Category([OW.class, P3.class])
    void case_10_2_8(){
        if(context.isOneWorld()){
            subPage.navigateToPage(data.test_case_10_2_8.subsidiary.name, true)
            def fCalendar = subPage.getFiscalCalendarTex()
            subPage.setFiscalCalendarTex(data.test_case_10_2_8.subsidiary.fiscalcalendar)
            subPage.clickSave()

            //get line values from saved report
             //switchToRole(accountant)
            changeLanguageToEnglish()
            def savedReportData = repPage.getReportData(data.test_case_10_2_8.savedReport)
            def blsReportData = blsPage.getBalanceSheetData(data.test_case_10_2_8.blsReport)
            checkReportDataEqual(blsReportData, savedReportData)

            //set back the fiscal calendar value
            //switchToRole(administrator)
            subPage.navigateToPage(data.test_case_10_2_8.subsidiary.name, true)
            subPage.setFiscalCalendarTex(fCalendar)
            subPage.clickSave()
        }
    }
    /**
     *  Check Excel Report Layout when fixed rows are not defined properly in the custom report
     */
    @Test
    @Category([OW.class, P2.class])
    void case_4_3_1_1_1(){
        if(!context.isOneWorld()){
            return
        }
         //switchToRole(accountant)
        navigateToPortalPage()
        //set up filter
        context.setFieldWithValue("custpage_reportname",data.test_case_4_3_1_1_1.filter.reportName);
        subsidiaryField().selectItem(data.test_case_4_3_1_1_1.filter.subsidiary)
        asofField().selectItem(data.test_case_4_3_1_1_1.filter.asof)
        unitField().selectItem(data.test_case_4_3_1_1_1.filter.unit)
        clickRefresh()
        waitForPageToLoad()

        //click the ok button in Error dialog
        okButtonInAlertMessage().click()
        waitForPageToLoad()

        // if exist, delete the old file
        def excelName= "data\\downloads\\${data.test_case_4_3_1_1_1.expected.filename}".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()

        //click Expert Excel button
        waitForElement(blsPage.XPATH_BTN_EXPORT_EXCEL)
        blsPage.exportExcel()

        // check downloaded Excle file
        checkExportedExcelFile(data.test_case_4_3_1_1_1.expected)

    }
    /**
     *  Check Exported Excel file when Unit = Ten Thousand
     */
    @Test
    @Category([OW.class, P2.class])
    void case_6_3_1_2_1(){

        if(!context.isOneWorld()){
            return
        }
         //switchToRole(accountant)
        navigateToPortalPage()
        //set up filter
        context.setFieldWithValue("custpage_reportname",data.test_case_6_3_1_2_1.filter.reportName);
        subsidiaryField().selectItem(data.test_case_6_3_1_2_1.filter.subsidiary)
        asofField().selectItem(data.test_case_6_3_1_2_1.filter.asof)
        unitField().selectItem(data.test_case_6_3_1_2_1.filter.unit)
        clickRefresh()
        waitForPageToLoad()

        // if exist, delete the old file
        def excelName= "data\\downloads\\${data.test_case_6_3_1_2_1.expected.filename}".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()

        //click Expert Excel button
        waitForElement(blsPage.XPATH_BTN_EXPORT_EXCEL)
        blsPage.exportExcel()

        // check downloaded Excle file
        checkExportedExcelFile(data.test_case_6_3_1_2_1.expected)
    }

    private void checkExportedExcelFile(expResult){
        // check downloaded file existed
        def downloadsPath = "data\\downloads\\".replace("\\",File.separator)
        File excelFile = new File(downloadsPath + expResult.filename)
        assertTrue("Check Excel downloaded",excelFile.exists())

        // check Excel report header and contents
        def tmpExcelFileName = downloadsPath + "tempBLExcel.xls"
        HTMLTOExcel.toExcel(excelFile,tmpExcelFileName);
        List<List<String>> excelContents = excelUtil.getExcelContents(tmpExcelFileName,0)

        checkExcelReportHeader(expResult.reportheader, excelContents)
        checkExcelTableHeader(expResult.tableheader, excelContents, expResult.tablestartrow)

        if (expResult.tabledata){
            checkExcelReportTable(expResult.tabledata, excelContents, expResult.tablestartrow)
        } else { // get the report data from page
            //check table data
            List<List<String>> pageData = getTableValue()
            checkExcelReportTable(pageData, excelContents, expResult.tablestartrow)
        }

        // delete the downloaded file and converted Excel file
        excelFile.delete()
        File tmpExcelFile = new File(tmpExcelFileName)
        tmpExcelFile.delete()
    }

    private void checkExcelReportHeader(header, excelContents) {
        header.each { key, value ->
            checkAreEqual("check report header: ${key}", excelContents.get(value['row']).get(value['col']), value['text'])
        }
    }

    private void checkExcelTableHeader(tableheader, excelContents, startrow){
        checkAreEqual("Check the report table header column number",excelContents.get(startrow).size(), tableheader.size())
        tableheader.eachWithIndex { thdata, col ->
            checkAreEqual("Check the report table header column " + col +" value", excelContents.get(startrow).get(col),thdata)
        }
    }

    private void checkExcelReportTable(tabledata, excelContents, startrow){
        checkAreEqual("Check the report table row number",excelContents.size()-startrow-1, tabledata.size())

        tabledata.eachWithIndex{rowdata, index ->
            checkAreEqual("Check the report table column number", excelContents.get(startrow+1+index).size(),rowdata.size())
            rowdata.eachWithIndex{cell, col ->
                def excelCellValue = excelContents.get(startrow+1+index).get(col).replaceAll('^(\\u00A0|\\s| )*', '')//delete the space at the begin
                checkAreEqual("Check the report table cell value", excelCellValue,cell)
            }
        }
    }

    private def getTableValue(){
        def XPATH_TR = ".//table[@id='blsheet_data']/tbody/tr"
        def pageData = []
        int rowCount = context.webDriver.getHtmlElementsByLocator(Locator.xpath(XPATH_TR)).size()
        for (int i = 2; i<= rowCount; i++){
            def rowData = []
            context.webDriver.getHtmlElementsByLocator(Locator.xpath(XPATH_TR+"[" + i+ "]/td")).each {tdElem ->
                rowData.add(tdElem.getAsText())
            }
            pageData.add(rowData)
        }
        return pageData
    }

    private void checkReportDataEqual(blsReportData, savedReportData){
        def TOTAL_ACCOUNT_LABEL_MAP = ["Total current assets": "流动资产合计",
                                       "Total non-current assets": "非流动资产合计",
                                       "Total assets": "资产总计",
                                       "Total current liabilities": "流动负债合计",
                                       "Total non-current liabilities": "非流动负债合计",
                                       "Total liabilities": "负债合计",
                                       "Total owner's equity": "所有者权益（或股东权益）合计",
                                       "Total liabilities and owner's equity": "负债和所有者权益（或股东权益）总计"]

        def BALANCE_COLUMN_LABEL_MAP = ["期末余额":"Closing Balance" , "期初余额":"Opening Balance"]

        savedReportData.each{k, v ->
            def lineValue = [:]
            v.each{column, value ->
                BALANCE_COLUMN_LABEL_MAP.each {cn, en ->
                    if(column.startsWith(cn)){
                        lineValue[en] = value
                    }
                }
            }
            v.clear()
            v.putAll(lineValue)
        }

        blsReportData.each{k, v ->
            if(TOTAL_ACCOUNT_LABEL_MAP.containsKey(k)){
                checkAreEqual("Total account value is equal; total account:"+ k, v, savedReportData[TOTAL_ACCOUNT_LABEL_MAP[k]])
            } else{
                checkAreEqual("Account value is equal; account:"+ k, v, savedReportData[k])
            }
        }
    }



    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BalanceSheetPeriodTest_zh_CN.json".replace("\\",SEP),
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BlanaceSheetPeriodTest_en_US.json".replace("\\",SEP)
        ]
    }



}
