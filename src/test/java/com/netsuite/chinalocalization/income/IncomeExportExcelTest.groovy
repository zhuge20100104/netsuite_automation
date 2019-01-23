package com.netsuite.chinalocalization.income

import com.netsuite.chinalocalization.extendreport.ExtendReportAppTestSuite
import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

import java.text.DecimalFormat

@TestOwner ("zhen.t.tang@oracle.com")
class IncomeExportExcelTest extends IncomeAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeExportExcelTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeExportExcelTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    private  def caseData
    private  def caseFilter
    private  def expResult
    def initData(){

        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }



    def checkExportedExcelFile(expResult, siCompanyName) {
        // check downloaded file existed
        def downloadsPath = "data\\downloads\\".replace("\\",File.separator)
        File excelFile = new File(downloadsPath + expResult.filename)
        assertTrue("Excel downloaded",excelFile.exists())

        // check Excel report header and contents
        def tmpExcelFileName = downloadsPath + "tempExcel.xls"
        HTMLTOExcel.toExcel(excelFile,tmpExcelFileName);
        List<List<String>> excelContents = excelUtil.getExcelContents(tmpExcelFileName,0)

        if (expResult.reportheader) {
            checkExcelReportHeader(expResult.reportheader, excelContents, siCompanyName)
        }
        if (expResult.reporttableheader) {
            checkExcelReportHeader(expResult.reporttableheader, excelContents, siCompanyName)
        }
        // get the report data from page
        //def pageData = getReportData("#,##0.00")
        def pageData = getISReport(caseData.report)
        if (expResult.data) {
            checkExcelReportData(expResult, excelContents)
        } else {
            expResult.put('data', pageData)
            checkExcelReportData(expResult, excelContents)
        }

        // delete the downloaded file and converted Excel file
        excelFile.delete()
        File tmpExcelFile = new File(tmpExcelFileName)
        tmpExcelFile.delete()
    }
    def checkExcelReportHeader(header, excelContents, siCompanyName) {
        if (header.subsidiary) {
            if (!context.isOneWorld()) {
                // reset expected company information in SI
                if (context.getUserLanguage() == "en_US") {
                    header.subsidiary.text = "Prepared by: " + siCompanyName
                } else {
                    header.subsidiary.text = "编制单位: " + siCompanyName
                }
            }
        }

        header.each { key, value ->
            assertAreEqual("check report header: ${key}", value['text'], excelContents.get(value['row']).get(value['col']))
        }
//        for (index in header) {
//            assertAreEqual("check report header: " + index.value.text, index.value.text, excelContents.get(index.value.row).get(index.value.col))
//        }
    }

    def checkExcelReportData(expResult, dataContents) {
        def startrow = expResult.datastartrow
        def startcol = expResult.datastartcol
        List<List<String>> excelContents = dataContents
        assertAreEqual("check data line count", excelContents.size()-startrow, expResult.data.size())

        for (int i = 0; i < expResult.data.size(); i++) {
            def line = expResult.data.get(i)
            line.eachWithIndex { val, idx ->
                idx = (idx == 0) ? idx:idx + 4
                //if(idx==1) idx+=4
                def tmp = val.value
                def tmp2 = excelContents.get(startrow + i).get(startcol + idx)
                assertAreEqual("check data line : [ ${startrow + i} , ${startcol + idx} ]", val.value, excelContents.get(startrow + i).get(startcol + idx))
            }
        }
    }
    /**
     * @desc Check Downloaded Excel Report
     * @case 5.1
     * @author Zhen Tang
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_5_1(){
        initData()
        navigateToPortalPage()
        setSearchParams(caseData.filter)
        clickRefresh()
        waitForPageToLoad()
        def siCompanyName = aut.getContext().getProperty("testautomation.nsapp.default.account")
        def excelName= "data\\downloads\\${caseData.expResult.filename}".replace("\\",File.separator)
        def tmpName = "data\\downloads\\tempExcel.xls".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()
        if (new File(tmpName).exists()) new File(excelName).delete()
        clickExportExcel()
        checkExportedExcelFile(caseData.expResult,siCompanyName)


    }

    /**
     * @desc Check Downloaded Excel Report
     * @case 8.2.2.1
     * @author Zhen Tang
     */
    @Category([P1.class,OW.class])
    @Test
    void test_case_8_2_2_1(){
        if(context.isSingleInstance()){
            return
        }
        initData()
        navigateToPortalPage()
        setSearchParams(caseData.filter)
        clickRefresh()
        waitForPageToLoad()
        def siCompanyName = aut.getContext().getProperty("testautomation.nsapp.default.account")
        def excelName= "data\\downloads\\${caseData.expResult.filename}".replace("\\",File.separator)
        def tmpName = "data\\downloads\\tempExcel.xls".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()
        if (new File(tmpName).exists()) new File(excelName).delete()
        clickExportExcel()
        checkExportedExcelFile(caseData.expResult,siCompanyName)


    }
}
