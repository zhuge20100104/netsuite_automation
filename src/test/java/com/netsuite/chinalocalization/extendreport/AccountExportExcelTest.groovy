package com.netsuite.chinalocalization.extendreport

import com.netsuite.common.OWAndSI
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import com.netsuite.chinalocalization.lib.HTMLTOExcel

import java.text.DecimalFormat

@TestOwner("molly.feng@oracle.com")
/**
 * @modified Molly change period from/to to date from/to 2018/7/31
 */
class AccountExportExcelTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountExportExcelTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountExportExcelTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    private  def caseData
    private  def caseFilter
    private  def expResult
    def initData(){
        println "Start: ${name.getMethodName()}"
        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    /**
     * @desc Export Excel button is disabled when no data to Export
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * ------------Test Points------------
     * no data to Export
     *
     * @case 7.1.1
     * @author Molly Feng
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void test_case_7_1_1() {
        setShowAccountNum(true)
        setNormalUserPreference(testData.defaultUserPreference)
        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        // check Export Excel is disabled
        CheckExportButtonStatus(expResult)
        assertAreEqual("Check no data in sublist", 0, currentRecord.getLineCount("custpage_atbl_report_sublist"))

    }

    /**
     * @desc Export Excel TestCases
     *
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Number Format: XXX,XXX.XX
     * ------------Test Points------------
     * has data to Export
     * Currency: CNY
     * Number format:XXX,XXX.XX
     * @case 7.2.1 Export Excel button is visible when there is data in sublist
     * @case 7.2.2 Check Report header in exported excel - Currency:CNY
     * @case 7.2.3 Check Report data in exported excel - Currency:CNY
     * @author Molly Feng
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void test_case_7_2_1() {
        setShowAccountNum(true)
        setNormalUserPreference(testData.defaultUserPreference)
        def siCompanyName = aut.getContext().getProperty("testautomation.nsapp.default.account")
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
        
        Thread.sleep(5 * 1000)
        clickExportExcel()

        // check Export Excel is disabled
        CheckExportButtonStatus(expResult)
        // check has data in sublist
        assertAreEqual("Check has data in sublist", expResult.data.size(), currentRecord.getLineCount("custpage_atbl_report_sublist"))

        // check downloaded Excle file
        checkExportedExcelFile(expResult, siCompanyName)

    }
    /**
     * @desc Export Excel TestCases :Check Report header and data in exported excel - Currency:USD;Number format
     *
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Number Format: XXX XXX.XX
     * ------------Test Points------------
     * has data to Export
     * Currency: USD
     * Number format:XXX XXX.XX
     * @case 7.2.4 Check Report header in exported excel - Currency:USD
     * @case 7.2.5 Check Report data in exported excel - Currency:USD;Number format
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P1.class])
    void test_case_7_2_4() {
        initData()
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }
        setShowAccountNum(true)
        // it switchto account in the method
        setSubsidiaryFiscalCalendar(caseData.filter.subsidiary,caseData.calenderToTest)
        setNormalUserPreference(caseData.defaultUserPreference)

        navigateToPortalPage()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()

        // if exist, delete the old file
        def excelName= "data\\downloads\\${expResult.filename}".replace("\\",File.separator)
        def tmpName = "data\\downloads\\tempExcel.xls".replace("\\",File.separator)
        if (new File(excelName).exists()) new File(excelName).delete()
        if (new File(tmpName).exists()) new File(excelName).delete()

        Thread.sleep(5 * 1000)
        clickExportExcel()

        // check has data in sublist
        assertAreEqual("Check has data in sublist", expResult.data.size(), currentRecord.getLineCount("custpage_atbl_report_sublist"))

        // check downloaded Excle file
        checkExportedExcelFile(expResult, "")

    }
    /**
     * @desc Export Excel TestCases
     * 7.2.6 Check Report data in exported excel - all Account
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Number Format: XXX,XXX.XX
     * ------------Test Points------------
     * @case 7.2.6 Check Report data in exported excel - all Account
     * @author Molly Feng
     */
    @Test
    @Category([OW.class, P3.class])
    void test_case_7_2_6() {
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }
        setShowAccountNum(true)
        setNormalUserPreference(testData.defaultUserPreference)

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

        Thread.sleep(5 * 1000)
        clickExportExcel()

        // check downloaded Excle file
        checkExportedExcelFile(expResult, "")

    }
/**
 * @desc Export Excel TestCases
 * 7.2.7 Check Report data in exported excel - Special character in account name
 * ONLY SHOW LAST SUBACCOUNT: true
 * USE ACCOUNT NUMBERS: true
 * Number Format: XXX,XXX.XX
 * ------------Test Points------------
 * @case 7.2.7 Check Report data in exported excel - Special character in account name
 * @author Molly Feng
 */
    @Test
    @Category([OW.class, P3.class])
    void test_case_7_2_7() {
        if (!context.isOneWorld()) {
            // only run this case in OW
            return
        }
        setShowAccountNum(true)
        setNormalUserPreference(testData.defaultUserPreference)

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

        Thread.sleep(5 * 1000)
        clickExportExcel()

        // check downloaded Excle file
        checkExportedExcelFile(expResult, "")

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

    def CheckExportButtonStatus(expResult) {

        def buttons = expResult.buttons
        for (btn in buttons) {

            // check button displayed
            if (btn.displayed) {
                // check button label
                assertTrue("Check button label", context.doesButtonExist(btn.text) )
            } else {
                // check button label
                assertFalse("Check button label", context.doesButtonExist(btn.text) )
            }
            // check button disabled status
            if (btn.disabled) {
                assertTrue("Check button disabled status: true", context.isFieldDisabled(btn.fieldid) )
            } else {
                assertFalse("Check button disabled status: false", context.isFieldDisabled(btn.fieldid) )
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
        //context.webDriver.reloadBrowser()
//        Alert alert = alertHandler
//                .waitForAlertToBePresent(context.webDriver)
//        if(alert!=null) {
//            alert.accept()
//        }
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
