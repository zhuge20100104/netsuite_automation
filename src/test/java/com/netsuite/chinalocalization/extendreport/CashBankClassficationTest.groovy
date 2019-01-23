package com.netsuite.chinalocalization.extendreport

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P0
import com.netsuite.testautomation.junit.runners.SuiteSetup
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName


class CashBankClassficationTest extends  ExtendReportAppTestSuite {

    @Rule
    public TestName name = new TestName()
    private def static caseData

    @SuiteSetup
    void setUpTestSuite() {
        super.setUpTestSuite()
        enableAllClassificationFeatures()

        setShowAccountNum(true)
        setUseLastSubAccount(true)
        userPrePage.navigateToURL()
        userPrePage.setDateFormat("M/D/YYYY", true)
    }

    def pathToTestDataFiles() {
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "enUS": "${dataFilesPath}extendreport\\data\\CashBankClassficationTest_en_US.json",
                "zhCN": "${dataFilesPath}extendreport\\data\\CashBankClassficationTest_zh_CN.json"
        ]
    }

    def initData() {
        caseData = testData.get(name.getMethodName())
    }

    def checkExportedExcelFile(expExcel) {
        def downloadsPath = "data\\downloads\\".replace("\\",File.separator)
        File excelFile = new File(downloadsPath + expExcel.filename)
        excelFile.deleteOnExit()
        clickExportExcel()
        assertTrue("Excel downloaded",excelFile.exists())

        def tmpExcelFileName = downloadsPath + "tempExcel.xls"
        HTMLTOExcel.toExcel(excelFile,tmpExcelFileName)
        List<List<String>> excelContents = excelUtil.getExcelContents(tmpExcelFileName,0)

        if (expExcel.reportheader) {
            checkExcelReportHeader(expExcel.reportheader, excelContents)
        }

        excelFile.delete()
        File tmpExcelFile = new File(tmpExcelFileName)
        tmpExcelFile.delete()
    }

    def checkExcelReportHeader(header, excelContents) {
        header.each { key, value ->
            assertAreEqual("check excel header: ${key}", value['text'], excelContents.get(value['row']).get(value['col']))
        }
    }

    def checkExportedPdfFile(expPdf){
        def pdfName= "data\\downloads\\${expPdf.filename}".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()
        ParsePDF pdfPaser = new ParsePDF(pdfName)
        def rawContents = pdfPaser.getRawContents()

        checkPdfReportHeader(expPdf, rawContents)

        File tmpPdfFile = new File(pdfName)
        tmpPdfFile.delete()
    }

    def checkPdfReportHeader(expPdf, pdfContents){
        for (int i=0; i<pdfContents.size(); i++){
            def table = pdfContents[i]
            table.eachWithIndex { line, index ->
                switch (index) {
                    case 0:
                        assertTrue("check title", line.contains(expPdf.title))
                        break
                    case 2:
                        assertTrue("check subsidiary", line.contains(expPdf.subsidiary))
                        assertTrue("check date", line.contains(expPdf.period))
                        assertTrue("check currency", line.contains(expPdf.currency))
                        break
                    case 3:
                        // check pdf account
                        if (i%2 == 1){
                            assertTrue("check account", line.contains(expPdf.account2))
                        }
                        else {
                            assertTrue("check account", line.contains(expPdf.account))
                        }

                        // check pdf classification
                        def count = 0
                        expPdf.classification.each { key, value ->
                            if (count < 2){
                                assertTrue("check classification", line.contains(value))
                                count ++
                            }
                            else {
                                count = 10
                            }
                        }
                        break
                    case 4:
                        if (expPdf.classification.size() == 3){
                            assertTrue("check classification", line.contains(expPdf.classification.class))
                        }
                        break
                    default:
                        break
                }
            }
        }
    }

    def checkRefreshReportData(expResult) {
        List<List<String>> refreshContents = getReportData()

        for (int i = 0; i < expResult.size(); i++) {
            def line = expResult.get(i)
            line.eachWithIndex { val, idx ->
                assertAreEqual("check data line", val, refreshContents.get(i).get(idx))
            }
        }
    }

    def getReportData() {
        def resultData = []
        for(int i =1 ; i <=cbjLedgerHelper("getLineCount"); i++){
            def curLine = []
            def reportString
            subledger.eachWithIndex{it,index ->
                reportString = asText(".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[${i+2}]/td[${index + 1 }]")
                curLine.add(reportString.toString().trim())
            }
            resultData.add(curLine)
        }
        return resultData
    }

    /**
     * @desc classification parameters -- refresh -- three parameters
     * @case 8.2.1
     *      1.Checked Use Account Number
     *      2.Checked ONLY SHOW LAST SUBACCOUNT
     *      3. Date Format: M/D/YYYY
     *      4. Login as China Accountant
     *      5. Standard Fiscal Calender
     *      6. Enable Classfication Feature
     * @author xiaojuan.song
     */
    @Category([P0.class, OW.class])
    @Test
    void test_case_8_2_1() {
        initData()
        def expSearch = caseData.searchparm
        def expResult = caseData.ReportData

        navigateToCashAndBankJournalPage()
        setSearchParams(expSearch)

        clickRefresh()
        checkRefreshReportData(expResult)
    }

    /**
     * @desc classification parameters -- export excel -- three parameters
     * @case 8.2.2
     *      1.Checked Use Account Number
     *      2.Checked ONLY SHOW LAST SUBACCOUNT
     *      3. Date Format: M/D/YYYY
     *      4. Login as China Accountant
     *      5. Standard Fiscal Calender
     *      6. Enable Classfication Feature
     * @author xiaojuan.song
     */
    @Category([P0.class, OW.class])
    @Test
    void test_case_8_2_2() {
        initData()
        def expSearch = caseData.searchparm
        def expExcel = caseData.ExcelData

        navigateToCashAndBankJournalPage()
        setSearchParams(expSearch)

        clickRefresh()
        waitForPageToLoad()

        //clickExportExcel()
        checkExportedExcelFile(expExcel)
    }


    /**
     * @desc classification parameters -- export pdf-- three parameters
     * @case 8.2.3
     *      1.Checked Use Account Number
     *      2.Checked ONLY SHOW LAST SUBACCOUNT
     *      3. Date Format: M/D/YYYY
     *      4. Login as China Accountant
     *      5. Standard Fiscal Calender
     *      6. Enable Classfication Feature
     * @author xiaojuan.song
     */
    @Category([P0.class, OW.class])
    @Test
    void test_case_8_2_3() {
        if (targetLanguage() != 'zh_CN') {
            println "Please run this case in CN."
            return
        }

        initData()
        def expSearch = caseData.searchparm
        def expPdf = caseData.PdfData

        navigateToCashAndBankJournalPage()
        setSearchParams(expSearch)

        clickRefresh()
        waitForPageToLoad()

        //clickExportPDF()
        checkExportedPdfFile(expPdf)
    }

    /**
     * @desc classification parameters -- export pdf&excel -- two parameters
     * @case 8.2.4
     *      1.Checked Use Account Number
     *      2.Checked ONLY SHOW LAST SUBACCOUNT
     *      3. Date Format: M/D/YYYY
     *      4. Login as China Accountant
     *      5. Standard Fiscal Calender
     *      6. Enable Classfication Feature
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_8_2_4() {
        initData()
        def expSearch = caseData.searchparm
        def expExcel = caseData.ExcelData
        def expPdf = caseData.PdfData

        navigateToCashAndBankJournalPage()
        setSearchParams(expSearch)

        clickRefresh()
        waitForPageToLoad()

        //clickExportExcel()
        checkExportedExcelFile(expExcel)

        if (targetLanguage() == 'zh_CN') {
            println "Please run this step in CN."
            //clickExportPDF()
            checkExportedPdfFile(expPdf)
        }
    }

    /**
     * @desc classification parameters -- export pdf&excel -- one parameter
     * @case 8.2.5
     *      1.Checked Use Account Number
     *      2.Checked ONLY SHOW LAST SUBACCOUNT
     *      3. Date Format: M/D/YYYY
     *      4. Login as China Accountant
     *      5. Standard Fiscal Calender
     *      6. Enable Classfication Feature
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_8_2_5() {
        initData()
        def expSearch = caseData.searchparm
        def expExcel = caseData.ExcelData
        def expPdf = caseData.PdfData

        navigateToCashAndBankJournalPage()
        setSearchParams(expSearch)

        clickRefresh()
        waitForPageToLoad()

        //clickExportExcel()
        checkExportedExcelFile(expExcel)

        if (targetLanguage() == 'zh_CN') {
            println "Please run this step in CN."
            //clickExportPDF()
            checkExportedPdfFile(expPdf)
        }
    }

}