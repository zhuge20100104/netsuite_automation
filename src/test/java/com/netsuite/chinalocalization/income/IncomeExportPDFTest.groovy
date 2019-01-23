package com.netsuite.chinalocalization.income

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.extendreport.ExtendReportAppTestSuite
import com.netsuite.common.*
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("christina.chen@oracle.com")
class IncomeExportPDFTest extends IncomeAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeExportPDFTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeExportPDFTest_en_US.json"
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
    /**
     * @desc Base on :
     * SUBSIDIARY (子公司):中国 Income
     PERIOD(期间):Jun 2017
     UNIT(单位):Unit
     LOCATION(地点):中国 Income华北区
     DEPARTMENT(部门):Income生产部
     CLASS(类别):智能手机
     * @case 6.1.1
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class,CN.class])
    @Test
    void test_case_8_2_2_2() {
        if (targetLanguage() != 'zh_CN') {
            // only run this case in CN
            println "Please run this case in CN."
            return
        }
        navigateToPortalPage()
        waitForPageToLoad()
        initData()
        setSearchParams(caseFilter)
        //waitForElement(extendReportPage.XPATH_BTN_REFRESH)
        clickRefresh()
        waitForPageToLoad()
        checkPDF(caseFilter)
    }



    def checkPDF(caseFilter){
        def pdfName= "data\\downloads\\${testData.title}.pdf".replace("\\",File.separator)
        if (new File(pdfName).exists()) new File(pdfName).delete()
        clickExportPDF()
        assertFileExist("Shoud generate a new pdf file",pdfName)
        ParsePDF pdfPaser = new ParsePDF(pdfName)
        //check subsidiary

        def page1 = pdfPaser.pdfFileContents[0]


        if(context.isOneWorld()){
            def checkLine = page1[3].split(" {2,20}")
            assertTrue("location ${checkLine[0]} is correct",checkLine[0] ==~ /${testData.location}${caseFilter.location}/)
            assertTrue("department ${checkLine[1]} is correct",checkLine[1] ==~ /${testData.department}${caseFilter.department}/)
            assertTrue("class ${checkLine[2]} is correct",checkLine[2] ==~ /${testData.class}${caseFilter.class}/)
        }
        else{
            def checkLine = page1[2].split(" {3,20}")
            assertTrue("PreparedBy ${checkLine[0]} is correct",checkLine[0].replaceAll("  ","") ==~ /${testData.PreparedBy}/)
        }
        int i = 2
        def value, checkStr
        def rows = asElements(".//table[@id='incomestatement_data']/tbody/tr")

        //  .//table[@id='incomestatement_data']/tbody/tr[3]/td/tr[3]
        page1[8..-2].each{ line ->
            if (line) {
                checkStr=""
                for(int index =1 ; index <5; index++){
                    value = asText(".//table[@id='incomestatement_data']/tbody/tr[${i}]/td[${index}]")
                    //println("${line}:${value}" )
                    assertTrue("line${i} contains ${value}", line.contains(value))
                    //checkStr +="\\s*${value}"
                }
                i++
                //assertTrue("line${i++} match ${checkStr}", line ==~ /${checkStr}/)
            }

        }

    }


}
