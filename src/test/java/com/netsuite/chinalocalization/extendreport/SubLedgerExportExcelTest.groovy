package com.netsuite.chinalocalization.extendreport

import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

import java.text.DecimalFormat
@TestOwner ("zhen.t.tang@oracle.com")
class SubLedgerExportExcelTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerExportExcelTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\SubLedgerExportExcelTest_en_US.json"
        ]
    }

    @Rule
    public  TestName name = new TestName()
    def companyName
    def caseData
    def caseFilter
    def expResult
    def preCase(){
        setAdvancedPrint(true)
        setShowAccountNum(true)
        setUseLastSubAccount(true)
        companyName = context.getContext().getProperty("testautomation.nsapp.default.account")
        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    def getReportData(patternStr) {
        def resultData = [],item
        for(int i =1 ; i <=subLedgerHelper("getLineCount"); i ++){
            def curLine = []
            def reportString
            subledger.eachWithIndex{it,index ->
                //item = subLedgerHelper("getSublistValue",key, i)
                reportString = asText(".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[${i+2}]/td[${index + 1 }]")
//                if (it.value.contains("amount")) {
//                    DecimalFormat df = new DecimalFormat(patternStr);
//                    item = df.format(Double.parseDouble(item))
//                }
                curLine.add(reportString.toString().trim())
            }
            resultData.add(curLine)
        }
        return resultData
    }
    def checkExportedExcelFile(expResult, siCompanyName) {
        // check downloaded file existed
        def downloadsPath = "data\\downloads\\${expResult.filename}".replace("\\",File.separator)
        println("in checkExportedExcelFile：${downloadsPath}")

        File excelFile = new File(downloadsPath )
        assertTrue("Excel downloaded: ${downloadsPath}",excelFile.exists())

        // check Excel report header and contents
        def tmpExcelFileName = downloadsPath + "tempExcel.xls"
        HTMLTOExcel.toExcel(excelFile,tmpExcelFileName);
        List<List<String>> excelContents = excelUtil.getExcelContents(tmpExcelFileName,0)

        if (expResult.reportheader) {
            checkExcelReportHeader(expResult.reportheader, excelContents, siCompanyName)
        }

        def pageData = getReportData("#,##0.00")
        expResult.put('data', pageData)
        checkExcelReportData(expResult, excelContents)

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
                    header.subsidiary.text = "Company: " + siCompanyName
                } else {
                    header.subsidiary.text = "公司：" + siCompanyName
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
    def getSICompanyName() {
        companyInfoPage.navigateToCompanyInfoPage()
        return companyInfoPage.getCompanyName()
    }
    def checkExcelReportData(expResult, dataContents) {
        def startrow = expResult.datastartrow
        def startcol = expResult.datastartcol
        List<List<String>> excelContents = dataContents
        assertAreEqual("check data line count", excelContents.size()-startrow, expResult.data.size())

        for (int i = 0; i < expResult.data.size(); i++) {
            def line = expResult.data.get(i)
            line.eachWithIndex { val, idx ->
                assertAreEqual("check data line : [ ${startrow + i} , ${startcol + idx} ]", val, excelContents.get(startrow + i).get(startcol + idx))
            }
        }
    }

    def CheckExportButtonStatus(buttons) {


        for (btn in buttons) {

            // check button displayed
            if(btn.containsKey("displayed")){
                assertAreEqual("Check button ${btn.text} label display :${btn.displayed}", context.doesButtonExist(btn.text),btn.displayed )
            }
            if(btn.containsKey("disabled")){
                assertAreEqual("Check button ${btn.fieldid} disabled status: true", context.isFieldDisabled(btn.fieldid),btn.disabled )
            }

//            if (btn.displayed) {
//                // check button label
//                assertTrue("Check button label", context.doesButtonExist(btn.text) )
//            } else {
//                // check button label
//                assertFalse("Check button label", context.doesButtonExist(btn.text) )
//            }
//            // check button disabled status
//            if (btn.disabled) {
//                assertTrue("Check button disabled status: true", context.isFieldDisabled(btn.fieldid) )
//            } else {
//                assertFalse("Check button disabled status: false", context.isFieldDisabled(btn.fieldid) )
//            }
        }
    }
    def cleanFilePath(){
        // if exist, delete the old file
        def excelName= "data\\downloads\\${expResult.filename}".replace("\\",File.separator)
        def tmpName = "data\\downloads\\tempExcel.xls".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()
        if (new File(tmpName).exists()) new File(excelName).delete()
    }

    /**
     * @desc Export Excel TestCases :Check Report header and data in exported excel
     *
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Number Format: XXX,XXX.XX
     * ------------Test Points------------
     * has data to Export
     * Currency: CNY
     * @case 6.2.1 Check Report header and data display in exported excel - Currency:CNY
     * @author Zhen Tang
     */

    @Test
    @Category([OWAndSI.class, P0.class])
    void test_case_6_2_1() {
        //def companyName = aut.getContext().getProperty("testautomation.nsapp.default.account")
        navigateToSubLedgerPage()
        //initData()
        CheckExportButtonStatus(expResult.buttons)
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        cleanFilePath()
        clickExportExcel()

        // check Export Excel is disabled
        CheckExportButtonStatus(expResult.buttonsAfterRefresh)

        // check downloaded Excle file
        checkExportedExcelFile(expResult, companyName)

    }
    /**
     * @desc Export Excel TestCases :Check Report header and data in exported excel--Special character
     *
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Number Format: XXX,XXX.XX
     * ------------Test Points------------
     * has data to Export
     * Currency: CNY
     * @case 6.2.2 Check Export Excel Contents:Special character in Account Number
     * @author Zhen Tang
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void test_case_6_2_2() {

        navigateToSubLedgerPage()
        //initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        cleanFilePath()
        clickExportExcel()
        sleep(3* 1000)
        // check downloaded Excle file
        checkExportedExcelFile(expResult, companyName)

    }
}
