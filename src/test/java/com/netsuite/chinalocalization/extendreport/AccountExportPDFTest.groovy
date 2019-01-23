package com.netsuite.chinalocalization.extendreport

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.CN
import com.netsuite.common.EN
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@TestOwner("christina.chen@oracle.com")
/**
 * @modified Molly change period from/to to date from/to 2018/7/31
 */
class AccountExportPDFTest extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountExportPDFTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\AccountExportPDFTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    def pdfName
    def caseData
    def caseFilter
    def expResult
    def initData(){
        pdfName= "data\\downloads\\${testData.title}.pdf".replace("\\",File.separator)
        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Check  no data won't show export button
     * won't show account's parent
     * @case 6.1.1
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_6_1_1() {

        //add set default date format: M/D/YYYY
        userPrePage.navigateToURL()
        userPrePage.setDateFormat("M/D/YYYY")
        userPrePage.clickSave()

        navigateToPortalPage()
        waitForPageToLoad()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        //waitForElement(extendReportPage.XPATH_BTN_REFRESH)
        clickRefresh()
        waitForPageToLoad()
        assertAreEqual("Export pdf button disabled","true", asAttributeValue(extendReportPage.XPATH_BTN_EXP_PDF, "disabled") )
        assertAreEqual("Export excel button disabled", "true",asAttributeValue(extendReportPage.XPATH_BTN_EXP_EXCEL, "disabled") )

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: Tre
     * USE ACCOUNT NUMBERS: true
     *Test will generate a pdf file
     * @case 6.1.2
     * @author Christina Chen
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_6_1_2() {
        setShowAccountNum(true)
        setUseLastSubAccount(true)
        //add set default date format: M/D/YYYY
        userPrePage.navigateToURL()
        userPrePage.setDateFormat("M/D/YYYY")
        userPrePage.clickSave()

        navigateToPortalPage()
        waitForPageToLoad()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()

        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()
        assertFileExist("Shoud generate a new pdf file",pdfName)

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: Tre
     * USE ACCOUNT NUMBERS: true
     *Test special character will generate a pdf file
     * @case 6.1.3
     * @author Christina Chen
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_6_1_3() {
        setShowAccountNum(true)
        setUseLastSubAccount(true)
        //add set default date format: M/D/YYYY
        userPrePage.navigateToURL()
        userPrePage.setDateFormat("M/D/YYYY")
        userPrePage.clickSave()

        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()

        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()
        assertFileExist("Shoud generate a new pdf file",pdfName)

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * check one page pdf export correct
     * @case 6.2.1
     * @author Christina Chen
     */
    @Category([P0.class,OW.class,CN.class])
    @Test
    void test_case_6_2_1() {
        if (targetLanguage() != 'zh_CN') {
            // only run this case in CN
            println "Please run this case in CN."
            return
        }
        if (!context.isOneWorld()) {
            // only run this case in OW
            println "Please run this case in OW."
            return
        }
        setShowAccountNum(true)
        //setUseLastSubAccount(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        //userPrePage.setNumberFormat("1.000.000,00")
        userPrePage.setNumberFormat("1.000.000,00")
        userPrePage.setDateFormat("YYYY/M/D")
        userPrePage.clickSave()
        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkWrappedPDF(testData, caseFilter, expResult, "yyyy/M/d")
        //checkPDF(testData,labelData, caseFilter, expResult)

    }
    /* @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Check account wrapped an amount more than 10000000.00 can display correct
     * won't show account's parent
     * @case 6.2.2
     * @author Christina Chen
     */
    @Category([P3.class,OW.class,CN.class])
//    @Test
    void test_case_6_2_2() {
        if (targetLanguage() != 'zh_CN') {
            // only run this case in CN
            println "Please run this case in CN."
            return
        }
        if (!context.isOneWorld()) {
            // only run this case in OW
            println "Please run this case in OW."
            return
        }
        setShowAccountNum(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        //userPrePage.setNumberFormat("1,000,000.00")
        userPrePage.setNumberFormat("1,000,000.00")
        userPrePage.setTIMEZONE( "Europe/London")
        userPrePage.setDateFormat("YYYY M D")
        userPrePage.clickSave()
        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkWrappedPDF(testData, caseFilter, expResult, "yyyy M d")
        //checkPDF(testData,labelData, caseFilter, expResult)

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Check multi page every page should has header and footer
     * @case 6.3.1
     * @author Christina Chen
     */

    @Category([P1.class,OWAndSI.class, CN.class])
    @Test
    void test_case_6_3_1() {

        if (targetLanguage() != 'zh_CN') {
            // only run this case in CN
            println "Please run this case in CN."
            return
        }
        setShowAccountNum(true)
        //setUseLastSubAccount(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        //userPrePage.setNumberFormat("1,000,000.00")
        userPrePage.setNumberFormat("1,000,000.00")
        //"Asia/Hong_Kong"
        userPrePage.setTIMEZONE( "Europe/London")
        userPrePage.setDateFormat("M/D/YYYY")
        userPrePage.clickSave()
        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkWrappedPDF(testData, caseFilter, expResult, "M/d/yyyy")
        //checkPDF(testData,labelData, caseFilter, expResult)

    }
    /**
     * @desc
     * Multi currency check
     * @case 6.4.1
     * @author Christina Chen
     */
    @Category([P0.class,OW.class,EN.class])
    @Test
    void test_case_6_4_1() {
        if (context.getContext().getProperty("testautomation.nsapp.language") == "zh_CN" ){
            return
        }
        if (!context.isOneWorld()) {
            // only run this case in OW
            println "Please run this case in OW."
            return
        }
        setShowAccountNum(true)
        userPrePage.navigateToURL()
        userPrePage.setOnlyShowLastSubAcct("T")
        /* NUMBERFORMAT  1,000,000.00 ==> VALUE =0
                         1.000.000,00 ==> VALUE =1
        * */
        userPrePage.setNumberFormat("1,000,000.00")
        userPrePage.setTIMEZONE( "Europe/London")
        userPrePage.setDateFormat("M/D/YYYY")
        userPrePage.clickSave()
        navigateToPortalPage()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        checkWrappedPDF(testData, caseFilter, expResult, "M/d/yyyy")
        //checkPDF(testData,labelData, caseFilter, expResult)

    }


    @After
    void tearDown() {
        //if (new File(pdfName).exists()) new File(pdfName).delete()
    }
    def checkWrappedPDF(testData, caseFilter, expResult, dateFormat){

        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()
        assertFileExist("Shoud generate a new pdf file",pdfName)
        ParsePDF pdfPaser = new ParsePDF(pdfName)
        //check title
        //check subsidiary
        def page1 = pdfPaser.pdfFileContents[0]
        def footer = pdfPaser.getLineContents(page1[-1])
        if (context.isOneWorld()) {

            assertTrue("Subsidiary is $testData.subsidiary${caseFilter.subsidiary} correct", page1[1].startsWith("${testData.subsidiary}${caseFilter.subsidiary}"))
        }else{
            def company = context.getContext().getProperty("testautomation.nsapp.default.account")
            //println(page1[1])
            //println("${testData.company}${company}")
            //assertTrue("Company is correct",  page1[1].startsWith("${testData.company}${company}" ))
        }
        def printBy = footer[0].replace(testData.printby,"")
        def printTime = footer[1].replace(testData.printTime,"").trim()

        assertTrue("Title is correct", page1[0] ==~ /\s*${testData.title}$/)

        assertTrue("Period:${caseFilter.datefrom} - ${caseFilter.dateto}: is correct", page1[1]==~/.*   ${caseFilter.datefrom} - ${caseFilter.dateto}    .*/)
        assertTrue("Currency: is correct", page1[1] ==~/.*${expResult.currency}/)
        int j =1
        def item, checkLine
        def table = pdfPaser.getDataFromTable(labelData.header.openBalance,testData.page)
        def page_size = table.size()

        table.eachWithIndex{tb, index ->
            assertTrue("Header line1 correct",tb[0] ==~/\s+${labelData.header.openBalance}\s+${labelData.header.currentPeriod}\s+${labelData.header.closeBalance}/)
            assertTrue("account is correct",tb[1] ==~/\s+${labelData.header.account}/)
            assertTrue("Header line2 correct",tb[2] ==~/\s+${labelData.header.debitCredit}\s+${labelData.header.amount}\s+${labelData.header.debit}\s+${labelData.header.credit}\s+${labelData.header.debitCredit}\s+${labelData.header.amount}/)
            if ("data" in expResult){
                tb[4..(tb.size() -2)].each{ line->
                    if (line.size()== 60)
                        assertTrue("Account start with ${line}",expResult.data[j-1].accountName.startsWith(line.trim()))
                    if (line.size()>67){
                        //println line
                        cust.each { key, value ->
                            item = expResult.data[j-1].get(key)
                            if (key == "accountName") {
                                checkLine =" ${item}"
                                if (item.size() >60)  checkLine =""
                            } else {
                                checkLine +="\\s+${item}"
                            }
                        }
                        assertTrue("line ${j}:${line}: :${checkLine}:", line ==~/${checkLine}/)
                        j++
                    }
                }
            }
            else
               tb[4..(tb.size() -2)].each{ line->

                    if (line.size()== 60){
                        item = asText(".//*[@id='custpage_atbl_report_sublistrow${j-1}']/td[1]")
                        assertTrue("Account start with ${line}", item.startsWith(line))
                    }
                    if (line.size()>67){
                        cust.each { key, value ->
                            item = sublistHelper("getSublistValue", key, j)
                            if (key == "accountName") {
                                //checkLine =" ${item}"
                                item = asText(".//*[@id='custpage_atbl_report_sublistrow${j-1}']/td[1]")
                                if (item.size() > 60) item = ""
                            }
                            if (key== "openBalance_amount") {
                                item = asText(".//*[@id='custpage_atbl_report_sublistrow${j-1}']/td[3]")
                            }
                            if (key== "debitAmount") {
                                item = asText(".//*[@id='custpage_atbl_report_sublistrow${j-1}']/td[4]")
                            }
                            if (key== "creditAmount") {
                                item = asText(".//*[@id='custpage_atbl_report_sublistrow${j-1}']/td[5]")
                            }
                            if (key== "closeBalanceAmount") {
                                item = asText(".//*[@id='custpage_atbl_report_sublistrow${j-1}']/td[7]")
                            }
                            assertTrue("line ${j}:${line}: :${item}:", line.contains(item))
                        }
                        j++
                    }
                }
            //println(tb[-1])
            //println("${testData.printby} ${printBy}\\s+${testData.printTime}: ${printTime}\\s+${testData.page}: ${index + 1}\\/${page_size}/")
           // assertTrue("Page:${index + 1}/${page_size}", tb[-1].contains("   ${testData.page}${index + 1}/${page_size}"))
            assertTrue("Footer printBy match:", tb[-1] ==~/.*${printBy}.*/)
            assertTrue("Footer printTime match:", tb[-1] ==~/.*${printTime}.*/)
            assertTrue("Footer Page match:", tb[-1] ==~/.*${index + 1} \/ ${page_size}.*/)
        }
        //println("Print by:${printBy} : ${printTime}")
        //def dateFormat = context.getPreference("DATEFORMAT").replaceAll(/Y/,'y').replace(/DD/, "d").replace(/MM/, "M")
        //def timeFormat = context.getPreference("TIMEFORMAT")
        if (printTime){
            DateTimeFormatter format = DateTimeFormatter.ofPattern(dateFormat)
            def timeZone = context.getPreference("TIMEZONE")
            def now =LocalDate.now(ZoneId.of(timeZone))
            def dateStr = now.format(format)
            assertTrue("Print time:${printTime} :start with ${dateStr}", printTime.startsWith(dateStr))
        }
    }


}
