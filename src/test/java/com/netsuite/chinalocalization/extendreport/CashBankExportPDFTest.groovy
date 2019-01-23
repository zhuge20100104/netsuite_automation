package com.netsuite.chinalocalization.extendreport

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.common.*
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("molly.feng@oracle.com")
class CashBankExportPDFTest extends ExtendReportAppTestSuite{

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\CashBankExportPDFTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\CashBankExportPDFTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    private  def caseData
    private  def caseFilter
    private  def expResult
    def initData(){
        println "Start Test Case ${name.getMethodName()}"
        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    /**
     * @desc Check  button display when no report data
     *        Check  button display when Initial page load
     * ----Before test ------
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Date Format: M/D/YYYY
     * Number Format: XXX,XXX.XX
     * Standard Fiscial Calendar
     * ------------------------------
     * Export PDF button is invisible when initial page load
     * Export PDF button is visible but disabled when initial page load
     * @case 5.1.1
     * @author Molly Feng
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_5_1_1() {
        initData()
        navigateToCashAndBankJournalPage()
        waitForPageToLoad()

        // check button invisible on initial page load
        checkFalse("Check button label", context.doesButtonExist(expResult.buttons[0].text) )
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        // check Export Excel is disabled
        CheckExportButtonStatus(expResult)
        checkAreEqual("Check no data in sublist", 0, currentRecord.getLineCount("custpage_report_sublist"))

    }
    /**
     * @desc  Export PDF Action - Check  button display when report data returned
     *                            Check pdf can be exported normally
     *
     * ----Before test ------
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Date Format: M/D/YYYY
     * Number Format: XXX,XXX.XX
     * Standard Fiscial Calendar
     * ------------------------------
     * Export PDF button is visible when report data returned.
     * Check pdf can be exported normally.
     * Check PDF file name
     *
     * @case 5.2.1
     * @author Molly Feng
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_5_2_1() {
        setShowAccountNum(true)
        setNormalUserPreference(testData.defaultUserPreference)
        navigateToCashAndBankJournalPage()
        waitForPageToLoad()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()

//        Thread.sleep(3 * 1000)
        // check Export Excel is displayed
        CheckExportButtonStatus(expResult)

        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()
        assertFileExist("Shoud generate a new pdf file",pdfName)

    }
    /**
     * @desc  Check Export PDF Contents - Report header & table header & report contents & report footer
     *
     * ----Before test ------
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Date Format: M/D/YYYY
     * Number Format: XXX,XXX.XX
     * Standard Fiscial Calendar
     * ------------------------------
     *
     *
     * @case 5.2.2 Check Export PDF Contents - Report Header - multiple accounts(multiple pages)
     * @case 5.2.3 Check Export PDF Contents:Table Header&Tabe Contents - multiple accounts(multiple pages)
     * @case 5.2.4 Check Export PDF Contents: Report Footer -  multiple accounts(multiple pages)
     * @author Molly Feng
     */
    @Category([P0.class,OWAndSI.class, CN.class])
    @Test
    void test_case_5_2_2() {
        if (targetLanguage() != 'zh_CN') {
            // only run this case in CN
            println "Please run this case in CN."
            return
        }
        setShowAccountNum(true)
        setNormalUserPreference(testData.defaultUserPreference)
        navigateToCashAndBankJournalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
//        Thread.sleep(10 * 1000)

        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()

        // check report data
        checkCBJLPDF(testData,expResult)

    }
    /**
     * @desc Check Export PDF Contents: English PDF in SI env
     *
     * ----Before test ------
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Date Format: M/D/YYYY
     * Number Format: XXX,XXX.XX
     * Standard Fiscial Calendar
     * ------------------------------
     *
     * @case 5.3.1 Check Export PDF Contents: English PDF
     * Note: Table Header is different in English from in Chinese.
     * @author Molly Feng
     */
    @Category([P1.class,SI.class,EN.class])
    @Test
    void test_case_5_3_1() {
        if (targetLanguage() != 'en_US') {
            // only run this case in EN
            println "Please run this case in EN."
            return
        }
        if (context.isOneWorld()) {
            // only run this case in SI
            println "Please run this case in SI."
            return
        }
        setShowAccountNum(true)
        setNormalUserPreference(testData.defaultUserPreference)
        navigateToCashAndBankJournalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
//        Thread.sleep(10 * 1000)
        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()

        // check report data
        checkCBJLPDF(testData,expResult)

    }
    /**
     * @desc Check Export PDF Contents: pagination for one account
     * ----Before test ------
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     * Date Format: M/D/YYYY
     * Number Format: XXX,XXX.XX
     * Standard Fiscial Calendar
     * ------------------------------
     *  multiple pages for one account
     *
     *
     * @case 5.4.1 Check Export PDF Contents: pagination for one account
     * Note: Line number is different in English from in Chinese.  16 lines in EnglishPDF. 15 lines in Chinese PDF.
     * @author Molly Feng
     */
    @Category([P0.class,OWAndSI.class,CN.class])
    @Test
    void test_case_5_4_1() {
        if (targetLanguage() != 'zh_CN') {
            // only run this case in CN
            println "Please run this case in CN."
            return
        }
        initData()
        setShowAccountNum(true)
        // set user preference by case definition
        setNormalUserPreference(caseData.defaultUserPreference)
        navigateToCashAndBankJournalPage()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
//        Thread.sleep(10 * 1000)
        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()

        // check report data
        checkCBJLPDF(testData,expResult)

    }

    def CheckExportButtonStatus(expResult) {

        def buttons = expResult.buttons
        for (btn in buttons) {

            // check button displayed
            if (btn.displayed) {
                // check button label
                checkTrue("Check button label", context.doesButtonExist(btn.text) )
            } else {
                // check button label
                checkFalse("Check button label", context.doesButtonExist(btn.text) )
            }
            // check button disabled status
            if (btn.disabled) {
                checkTrue("Check button disabled status: true", context.isFieldDisabled(btn.fieldid) )
            } else {
                checkFalse("Check button disabled status: false", context.isFieldDisabled(btn.fieldid) )
            }
        }
    }


    def checkCBJLPDF(testData, expResult){
        // get employee name
        def expPrintby = testData.employeeName.find {it.key == context.getContext().getProperty("testautomation.nsapp.default.user")}

        if (expPrintby) {
            //expPrintby = testData.printby + expPrintby.value
            // Print By: cannot be parsed out
            expPrintby =  expPrintby.value
        } else {
            println "Login user is not listed in the expected employeeName Map."
            expPrintby = ""
        }


        def pdfName= "data\\downloads\\${testData.filename}.pdf".replace("\\",File.separator)

        assertFileExist("PDF file has been downloaded.", pdfName)
        ParsePDF pdfPaser = new ParsePDF(pdfName)

        def rawContents = pdfPaser.getRawContents()
       checkAreEqual("Check page numbers in PDF file.", expResult.data.size(), rawContents.size())
        for (int i = 0; i < rawContents.size(); i++) {
            println "Current Page: ${i} /${ rawContents.size()}"
            def expAccount = ""
            def currentAccountLine = ""
            def footerLine = ""
            def headerMap = {
            }
            // need to adjust the corresponding header lines in different PDF file.
            if (context.getUserLanguage() == "zh_CN") {
                headerMap = [
                        5: expResult.tableHeader[0],
                        6: expResult.tableHeader[1],
                        7: expResult.tableHeader[2]
                ]
            } else {
                headerMap = [
                        5: expResult.tableHeader[0],
                        6: expResult.tableHeader[1],
                        7: expResult.tableHeader[2],
                        8: expResult.tableHeader[3],
                        9: expResult.tableHeader[4],
                        10: expResult.tableHeader[5]
                ]
            }

            def table = rawContents[i]
            def tableWithoutEmptyLine = []
            table.eachWithIndex { line, index ->
                println "Line:${index} - ${line}"
                switch (index) {
                    case 0 :
                        // check title
                        checkTrue("Check Title is correct.", line.contains(testData.title))
                        break
                    case 2 :
                        // check subsidiary, period, and currency
                        if (context.isOneWorld()) {
                            checkTrue("Check Subsidiary is correct.", line.contains(expResult.subsidiary))
                        } else {
                            // Check Subsidiary Lebel in SI env
                            checkTrue("Check Company is correct.", line.contains(testData.company))
                            // In feature si environment, company name will be different according test environment.

                        }
                        checkTrue("Check Period is correct.", line.contains(expResult.period))
                        checkTrue("Check Currency is correct.", line.contains(expResult.currency))
                        break
                    case 3:
                        currentAccountLine = line
                        break

                    case 5..getEndIndexOfHeader(context.getUserLanguage()):
                        checkTableHeader(line, headerMap.get(index))
                        break

                    case getEndIndexOfHeader(context.getUserLanguage()) + 1 ..table.size()-2:
                        // check data line
                        def splitStrArray = line.toString().trim().split(" {3,10}")
                        def resultStrList = new ArrayList<String>(splitStrArray.length);
                        Collections.addAll(resultStrList, splitStrArray)

                        // if the length of resultStrList > 3 then this line is data line.
                        if ((line.toString().trim() != "") &&  (resultStrList.size() > 3)){
                            tableWithoutEmptyLine.add(line)
                        } else {
                            // do nothing
                            println "It is empty line."
                        }
                        break
                    case table.size()-1:
                        footerLine = line
                        break
                    default:
                        break
                }
            }

            // check report data
            checkAreEqual("check line number in the current page.",
                    expResult.data[i].lines.size(), tableWithoutEmptyLine.size())
            tableWithoutEmptyLine.eachWithIndex {String line, j ->
                expResult.data[i].lines[j].each { col ->
                    // check each columns in expected result
                    checkTrue("Check if expected data value [${col}] existed in the line : ${j} in page ${i+1}.", line.contains(col))
                }
                expAccount = testData.account  + expResult.data[i].account
            }

            // check account in the current page
            checkTrue("Check account:${expAccount} in the report header", currentAccountLine.contains(expAccount))

            // check footer in the current page
            // Check Print By: user name
            checkTrue("Print by is correct ${expPrintby}",   footerLine.contains(expPrintby))

            // check Print Time label
            checkTrue("Print by is correct ${testData.printTime}",   footerLine.contains(testData.printTime))
            // check Print Time date
            def finder = (footerLine =~ /([1-9]|1[012])[\/.]([1-9]|[12][0-9]|3[01])[\/.](20)\d\d ([01]?[0-9]):([0-5][0-9]):([0-5][0-9])/)
            checkTrue("match the date time.", finder.size()>0)
            def matcher =  (footerLine =~ /([1-9]|1[012])[\/.]([1-9]|[12][0-9]|3[01])[\/.](20)\d\d/)
            if (matcher.size() == 1) {
                def actDate = new Date().parse('MM/dd/yyyy', matcher[0][0])
                def previousDay = new Date() - 2
                checkTrue("Check print time is within 2 days ago.", actDate.after(previousDay))
            }

            // check Page: current page / total page
            def expPage = "${testData.page}${i+1} / ${rawContents.size()}"
            checkTrue("Check Page", footerLine.contains(expPage))
        }
    }
    def getEndIndexOfHeader(language) {
        if (language == "zh_CN") {
            return 7
        } else {
            return 9
        }
    }
    /**
     *  Check table header
     * @param line rawLineContent in pdf file
     * @param expResult
     */
    def checkTableHeader(String line,  expResult) {

        expResult.each{ String col ->
            checkTrue("Check Table Header in the report.", line.contains(col.trim()))
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
