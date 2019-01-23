package com.netsuite.chinalocalization.extendreport

import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1


import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("xiaojuan.song@oracle.com")
class CashBankExportExcelTest extends ExtendReportAppTestSuite {

    @Rule
    public TestName name = new TestName()
    private def caseData

    @SuiteSetup
    void setUpTestSuite(){

        super.setUpTestSuite()

        userPrePage.navigateToURL()
        userPrePage.setNumberFormat("1,000,000.00", true)
        userPrePage.setDateFormat("M/D/YYYY", true)

        setShowAccountNum(true)
        setUseLastSubAccount(true)
        setAdvancedPrint(true)
    }

    def pathToTestDataFiles() {
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "zhCN": "${dataFilesPath}extendreport\\data\\CashBankExportExcelTest_zh_CN.json",
                "enUS": "${dataFilesPath}extendreport\\data\\CashBankExportExcelTest_en_US.json"
        ]
    }

    def initData(){
        caseData = testData.get(name.getMethodName())
    }

    def checkExportButtonStatus(expResult) {

        if (expResult.displayed) {
            assertTrue("Check button ${expResult.text} display", context.doesButtonExist(expResult.text) )
        } else {
            assertFalse("Check button ${expResult.text} not display", context.doesButtonExist(expResult.text) )
        }
        if (expResult.disabled) {
            assertTrue("Check button ${expResult.fieldid} disabled status: true", context.isFieldDisabled(expResult.fieldid) )
        } else {
            assertFalse("Check button ${expResult.fieldid} disabled status: false", context.isFieldDisabled(expResult.fieldid) )
        }
    }

    def checkExportedExcelFile(expResult, siCompanyName) {
        // check downloaded file existed
        def downloadsPath = "data\\downloads\\".replace("\\",File.separator)
        File excelFile = new File(downloadsPath + expResult.filename)
        assertTrue("Excel downloaded",excelFile.exists())

        // check Excel report header and contents
        def tmpExcelFileName = downloadsPath + "tempExcel.xls"
        HTMLTOExcel.toExcel(excelFile,tmpExcelFileName)
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
                    header.subsidiary.text = "Company:" + siCompanyName
                } else {
                    header.subsidiary.text = "公司：" + siCompanyName
                }
            }
        }

        header.each { key, value ->
            assertAreEqual("check report header: ${key}", value['text'], excelContents.get(value['row']).get(value['col']))
        }
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

    def getReportData(patternStr) {
        def resultData = [],item
        for(int i =1 ; i <=cbjLedgerHelper("getLineCount"); i++){
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

    /**
     *@desc CashBank Export Excel Test
     *@case 6.1.1  Export Excel Button Visible - Disabled
     1. Number Format: XXX,XXX.XX
     2. Negative Format:-100
     3. Checked Use Account Number
     4. Checked ONLY SHOW LAST SUBACCOUNT
     5. Role:China Accountant
     * @author xiaojuan.song
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_6_1_1(){
        def expSearchParm

        navigateToCashAndBankJournalPage()
        initData()
        if (context.isOneWorld()) {
            expSearchParm = caseData.SearchParm_OW
        }
        else {
            expSearchParm = caseData.SearchParm_SI
        }
        def expButtStat = caseData.ExcelButton
        setSearchParams(expSearchParm)

        clickRefresh()
        waitForPageToLoad()
        // check button status
        checkExportButtonStatus(expButtStat)
        // check data in sublist : no data
        assertAreEqual("Check no data in sublist", 0, currentRecord.getLineCount("custpage_report_sublist"))
    }

    /**
     *@desc CashBank Export Excel Test
     *@case 6.1.2  Export Excel Button Visible - Enabled
     1. Number Format: XXX,XXX.XX
     2. Negative Format:-100
     3. Checked Use Account Number
     4. Checked ONLY SHOW LAST SUBACCOUNT
     5. Role:China Accountant
     * @author xiaojuan.song
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_6_1_2(){
        navigateToCashAndBankJournalPage()
        initData()
        def expSearchParm = caseData.SearchParm
        def expButtStat = caseData.ExcelButton
        setSearchParams(expSearchParm)

        clickRefresh()
        waitForPageToLoad()
        // check button status
        checkExportButtonStatus(expButtStat)
        // check data in sublist : has data

    }


    /**
     *@desc CashBank Export Excel Test
     *@case 6.2.1  Export Excel Button Visible - Check Report Header and Report Data - CNY
     1. Number Format: XXX,XXX.XX
     2. Negative Format:-100
     3. Checked Use Account Number
     4. Checked ONLY SHOW LAST SUBACCOUNT
     5. Role:China Accountant
     * @author xiaojuan.song
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_6_2_1(){
        def companyName = aut.getContext().getProperty("testautomation.nsapp.default.account")

        navigateToCashAndBankJournalPage()

        initData()
        def expSearchParm = caseData.SearchParm
        def expExcelData = caseData.ExcelData

        setSearchParams(expSearchParm)

        clickRefresh()
        waitForPageToLoad()

        // export file
        clickExportExcel()
        // check the downloaded file
        checkExportedExcelFile(expExcelData, companyName)
    }
}
