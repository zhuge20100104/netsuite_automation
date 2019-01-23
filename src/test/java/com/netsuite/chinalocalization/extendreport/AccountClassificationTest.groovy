package com.netsuite.chinalocalization.extendreport

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.common.*
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import org.w3c.dom.html.HTMLElement

@TestOwner("molly.feng@oracle.com")
/**
 * This test suite is for testing more parameters of classifications.
 */
class AccountClassificationTest extends ExtendReportAppTestSuite{

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountClassificationTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountClassificationTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    private  def caseData
    private  def caseFilter
    private  def expResult
    def prepareSuitData(){
        enableAllClassificationFeatures()
        //setAdvancedPrint(true) this has been down in ExtendReportAppTestSuite.setUpTestSuite
        setShowAccountNum(true)
		setNormalUserPreference(testData.defaultUserPreference)
    }
    def initData(casename){
        println "Start: ${name.getMethodName()}"
		caseData = testData.get(name.getMethodName())
		if (casename) {
			caseData = testData.get(casename)
		}
		caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    /**
     * @desc Classification Prameters Sync - Option list(all features enabled)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *
     * @case 8.1.2
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_8_1_2() {

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        // check options of parameters: location, department, and class
        checkClassificationOpt(expResult)
    }
    /**
     * @desc Classification Prameters - Report Data (Refresh)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Refresh)
     *  - Location:中国 Income华北区; Department:Income生产部; Class:智能手机
     * @case 8.2.1
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_8_2_1() {

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)
        // drilldown to Subledger
        drillDownToSLAndCB(caseData.accountToSL, caseData.sltitle)
        checkParamsSetting(caseFilter)
		checkParamsSetting(['accountfrom':caseData.accountToSL, 'accountto':caseData.accountToSL])
        assertTrue("Check Page title: ${caseData.sltitle}", context.doesTextExist(caseData.sltitle))

    }
	@Test
	@Category([OW.class, P0.class])
	void test_case_8_2_1_2() {

		navigateToPortalPage()
		initData("test_case_8_2_1")
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()

		// drilldown to CashBank
		drillDownToSLAndCB(caseData.accountToCB, caseData.cbtitle)
		checkParamsSetting(caseFilter)
		checkParamsSetting(['accountfrom':caseData.accountToCB, 'accountto':caseData.accountToCB])
		assertTrue("Check Page title: ${caseData.cbtitle}", context.doesTextExist(caseData.cbtitle))

	}
    def drillDownToSLAndCB(accountname,title) {
        def currentHandle
        int clickindex =getIndexOfAccount(accountname)

        def xpathAccount = "//*[@id=\"custpage_atbl_report_sublistrow${clickindex}\"]/td[1]/a"
        asClick(xpathAccount)
        waitForPageToLoad()
        currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(title)
		waitForPageToLoad()

    }
    def getIndexOfAccount(accountname) {

        def lineAccountName
		def lineCount = currentRecord.getLineCount("custpage_atbl_report_sublist")
        for(int i = 0 ; i < lineCount; i ++){
            lineAccountName = asText(".//*[@id='custpage_atbl_report_sublistrow${i}']/td[1]")
            if (lineAccountName == accountname) {
                return i
            }
        }
    }
    /**
     * @desc Classification Prameters - Report Data (Export Excel)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Refresh)
     *  - Location:中国 Income华北区; Department:Income生产部; Class:智能手机
     * @case 8.2.2
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_8_2_2() {
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()

        // if exist, delete the old file
        def excelName= "data\\downloads\\${expResult.filename}".replace("\\",File.separator)
        def tmpName = "data\\downloads\\tempExcel.xls".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()
        if (new File(tmpName).exists()) new File(excelName).delete()

        clickExportExcel()
        checkExportedExcelFile(expResult, "")
    }

    /**
     * @desc Classification Prameters - Report Data (Export PDF)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Export PDF)
     *  - Location:中国 Income华北区; Department:Income生产部; Class:智能手机
     * @case 8.2.3
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P0.class])
    void test_case_8_2_3() {
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()

        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()

        clickExportPDF()
        // check PDF report header for classifications
        checkSimpleABPDF(testData, expResult)
    }

    /**
     * @desc Classification Prameters - Report Data (Export Excel)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Refresh)
     *  - Location:all; Department:Income生产部; Class:智能手机
     * @case 8.2.4- Export Excel
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P1.class])
    void test_case_8_2_4_Excel() {
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()

        // if exist, delete the old file
        def excelName= "data\\downloads\\${expResult.filename}".replace("\\",File.separator)
        def tmpName = "data\\downloads\\tempExcel.xls".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()
        if (new File(tmpName).exists()) new File(excelName).delete()

        clickExportExcel()
        checkExportedExcelFile(expResult, "")
    }

    /**
     * @desc Classification Prameters - Report Data (Export PDF)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Export PDF)
     *  - Location:all; Department:Income生产部; Class:智能手机
     * @case 8.2.4 -Export PDF
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P1.class])
    void test_case_8_2_4_PDF() {
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()

        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()

        clickExportPDF()
        // check PDF report header for classifications
        checkSimpleABPDF(testData, expResult)
    }
    /**
     * @desc Classification Prameters - Report Data (Export Excel)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Refresh)
     *  - Location:all; Department:all; Class:智能手机
     * @case 8.2.5- Export Excel
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P1.class])
    void test_case_8_2_5_Excel() {
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()

        // if exist, delete the old file
        def excelName= "data\\downloads\\${expResult.filename}".replace("\\",File.separator)
        def tmpName = "data\\downloads\\tempExcel.xls".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()
        if (new File(tmpName).exists()) new File(excelName).delete()

        clickExportExcel()
        checkExportedExcelFile(expResult, "")
    }

    /**
     * @desc Classification Prameters - Report Data (Export PDF)
     * '------Before Test---------------------
     *      Number Format: XXX,XXX.XX
     *      Date Format: M/D/YYYY
     *      Checked Use Account Number
     *      Checked ONLY SHOW LAST SUBACCOUNT
     *      Role:China Accountant
     *      Features Location, Departments, Classes : all enabled
     * -----------Test Point-------------------
     *  Classification Prameters - Report Data (Export PDF)
     *  - Location:all; Department:all; Class:智能手机
     * @case 8.2.5 -Export PDF
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P1.class])
    void test_case_8_2_5_PDF() {
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)

        clickRefresh()
        waitForPageToLoad()

        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()

        clickExportPDF()
        // check PDF report header for classifications
        checkSimpleABPDF(testData, expResult)
    }

    def checkSimpleABPDF(testData, expResult){

        // only check PDF report header for classifications
        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)

        assertFileExist("PDF file has been downloaded.", pdfName)
        ParsePDF pdfPaser = new ParsePDF(pdfName)

        def rawContents = pdfPaser.getRawContents()
       //ssertAreEqual("Check page numbers in PDF file.", expResult.data.size(), rawContents.size())
        for (int i = 0; i < rawContents.size(); i++) {
            //println "Current Page: ${i} /${ rawContents.size()}"
            def footerLine = ""
            def classificationLine = ""
            def table = rawContents[i]
            def tableWithoutEmptyLine = []
            table.eachWithIndex { line, index ->
                //println "Line:${index} - ${line}"
                switch (index) {
                    case 0 :
                        // check title
                        assertTrue("Check Title is correct.", line.contains(testData.title))
                        break
                    case 1 :
                        // check subsidiary, period, and currency
                        if (context.isOneWorld()) {
                            assertTrue("Check Subsidiary is correct.", line.contains(expResult.subsidiary))
                        } else {
                            // Check Subsidiary Lebel in SI env
                            assertTrue("Check Company is correct.", line.contains(testData.company))
                            // In feature si environment, company name will be different according test environment.

                        }
                        assertTrue("Check Period is correct.", line.contains(expResult.period))
                        assertTrue("Check Currency is correct.", line.contains(expResult.currency))
                        break
                    case 2:
                        classificationLine = line
                        // check classification line
                        def splitStrArray = line.toString().trim().split(" {3,10}")
                        def resultStrList = []
                        splitStrArray.each { it->
                            if (it != "") resultStrList.add(it)
                        }

                        expResult.classification.eachWithIndex {value, indexC ->
                            assertTrue("Check ${resultStrList} Classification has ${value} .", resultStrList[indexC].contains(value))
                        }
                        break
                    case table.size()-1:
                        footerLine = line
                        break
                    default:
                        break
                }
            }
            // check Page: current page / total page
            def expPage = "${testData.page}${i+1} / ${rawContents.size()}"
            assertTrue("Check Page", footerLine.contains(expPage))
        }
    }

    def checkClassificationOpt(expResult) {
        if (expResult.locations) {
            def locOpts = asDropdownList(["fieldId": "custpage_location"]).getOptions()
            expResult.location.each {
                assertTrue("Check values of option: Location", locOpts.contains(it))
            }
        }
        if (expResult.departments) {
            def depOpts = asDropdownList(["fieldId": "custpage_department"]).getOptions()
            expResult.department.each {
                assertTrue("Check values of option: Department", depOpts.contains(it))
            }
        }
        if (expResult.classes) {
            def classOpts = asDropdownList(["fieldId": "custpage_class"]).getOptions()
            expResult.class.each {
                assertTrue("Check values of option: Class", classOpts.contains(it))
            }
        }
    }

    def getReportData(labelList=cust) {
        def resultData = []
        def count = currentRecord.getLineCount("custpage_atbl_report_sublist")
        def value
        for(int i = 0 ; i < count; i ++) {

            def curLine = []
            labelList.eachWithIndex { item, index ->
                value = asText(".//*[@id='custpage_atbl_report_sublistrow${i}']/td[${index + 1}]")
                //println "Line[${i}]: Column[${item.value}] - ${value}"
                curLine.push(value)
            }
            resultData.push(curLine)
        }

        return resultData
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
        def pageData = getReportData(cust)
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
                    header.subsidiary.text = "Company: " + siCompanyName
                } else {
                    header.subsidiary.text = "公司：" + siCompanyName
                }
            }
        }

        header.each { key, value ->
            checkAreEqual("check report header: ${key}", value['text'], excelContents.get(value['row']).get(value['col']))
        }

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
    /**
     *  Set default user preference according json data
     * @param defaultUserPreference
     * @return
     */
    def setNormalUserPreference(defaultUserPreference){

        userPrePage.navigateToURL()

        defaultUserPreference.each { key, value ->
            if (key == "ONLYSHOWLASTSUBACCT") {
                // set this with value
                context.setFieldWithValue(key, value)
            } else {
                // set  format with text
                context.setFieldWithText(key, value)
            }
        }
        userPrePage.clickSave()
    }
}
