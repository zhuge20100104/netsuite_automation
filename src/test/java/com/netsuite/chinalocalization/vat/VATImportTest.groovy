package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

import java.nio.file.Path

@TestOwner ("christina.chen@oracle.com")

class VATImportTest extends VATAppTestSuite {
    String dataFilePath = "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\"
    @Rule
    public  TestName name = new TestName()

    def caseData
    def caseFilter
    def expResult
    def records
    String fileName
    String fileContent
    def init(){

        caseData = testData.get(name.getMethodName())
        fileName = "${dataFilePath}${name.getMethodName()}.txt"
        if ("filter" in caseData) caseFilter = caseData.filter
        if ("fileTemplate" in caseData) fileContent = caseData.fileTemplate
        if ("expectedResults" in caseData) expResult = caseData.expectedResults
/*        //switchToRole(administrator)
        records= dirtyData = record.createRecord(caseData.data)
        //switchToRole(accountant)*/
    }
    def execute(){
        init()
        navigateToPortalPage()
        clickImport()
        waitForPageToLoad()
        verifyImport()
    }
    def pathToTestDataFiles() {
        def partFilePath = dataFilePath + this.getClass().getSimpleName()

        return [
                "zhCN": partFilePath + "_zh_CN.json",
                "enUS": partFilePath + "_en_US.json"
        ]

    }

    /**
     * @desc Items display.
     * Page Name
     * Button Name
     * File Upload
     * @case 7.2.1.1 en label *
     * @case 7.2.1.2 cn lable *
     * @case 7.2.2.1 enable button
     * @author Christion Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_7_2_1() {
        init()
        navigateToPortalPage()
        clickImport()
        waitForPageToLoad()
        checkTrue("Check the title for import page", context.isTextVisible(expResult.importtitle))
        checkTrue("Check the fileupload import page", context.isTextVisible(expResult.fileupload))
        checkAreEqual("Import button name",  asAttributeValue(locators.importSubmit, "value"),expResult.importtxt)
        checkAreEqual("Return button name",  asAttributeValue(locators.returnButton, "value"),expResult.back)
        checkAreEqual("Import button enabled",  asAttributeValue(locators.importSubmit, "disabled"),null)
        checkAreEqual("Return button enabled",  asAttributeValue(locators.returnButton, "disabled"),null)
        checkAreEqual("ImportFileBrowser button enabled",  asAttributeValue(locators.importFileBrowser, "disabled"),null)
   }
    /**
     * @desc error message
     * @case 7.2.3.2	Import without file uploaded (EN)	"Error message alerts:
     No file selected. Choose a file to upload."
     * @case 7.2.3.3	Import without file uploaded (CN)	"Error message alerts:
     请提供用于上传的有效文件。"
     * @author Christion Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void  test_case_7_2_3_2(){
        init()
        navigateToPortalPage()
        clickImport()
        waitForPageToLoad()
        clickImportSubmit()
        checkTrue("Check error message ${caseData.errmsg} should show", context.isTextVisible(caseData.errmsg))
    }
    /**
     * @desc error message
     * @case 7.2.3.4	Import file with not txt extension (EN)	"Error message shown:
     Invalid file format. Select one fomatted as TXT file."
     * @case 7.2.3.5	Import file with not txt extension (CN)	"Error message shown:
     无效文件格式，请选择要上传的 TXT文件。"
     * @author Christion Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void  test_case_7_2_3_4(){
        init()
        navigateToPortalPage()
        clickImport()
        waitForPageToLoad()
        fileName = "${dataFilePath}test_case_7_2_3_4"
        if(SEP == "/") fileName = fileName.replace("\\", SEP)
        def impFile =  new File(fileName)
        impFile.withWriter("gbk"){ file -> file << "Test " }
        setUploadPath(fileName)
        clickImportSubmit()
        checkTrue("Check error message ${caseData.errmsg} should show", context.isTextVisible(caseData.errmsg))
        impFile.deleteOnExit()
    }
    /**
     * @desc error message
     * @case 7.2.3.6	"Import file with incorrect format (EN)
     No header line:
     SJJK0201~~已开发票传出
     1~~20150201~~20150228
     //发票1"	"Error message shown:
     Incorrect File Format. Select a VAT Invoice file with the correct format."
     * @case 7.2.3.7	"Import file with incorrect format (CN)
     No header line:
     SJJK0201~~已开发票传出
     1~~20150201~~20150228
     //发票1"	"Error message shown:
     文件格式错误。请选择正确的增值税发票文件格式。"
     * @author Christion Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void  test_case_7_2_3_6(){
        execute()
    }

    /**
     * @desc error message
     * @case 7.2.3.8	"Import file with incorrect format (CN)
     Header line incorrect (with spaces between items):
     SJJK0201 ~~ 已开发票传出
     1~~20150201~~20150228
     //发票1"	"Error message shown:
     文件格式错误。请选择正确的增值税发票文件格式。"
     * @author Christion Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void  test_case_7_2_3_8(){
        execute()

    }

    /**
     * @desc error message
     * @case 7.2.3.9	"Import file with incorrect format (CN)
     No third line info (comment line //发票):
     SJJK0201 ~~ 已开发票传出
     1~~20150201~~20150228
     //发票1
     "	"Error message shown:
     文件格式错误。请选择正确的增值税发票文件格式。"
     * @author Christion Chen
     * @Regression: Vat Feature
     */

    @Category([P1.class,OWAndSI.class])
    @Test
    void  test_case_7_2_3_9(){
        execute()
    }


    /** @desc error message
     @case 7.2.3.10	"Import file with incorrect format (CN)
     Third line incorrect (comment line // 发票):
     SJJK0201 ~~ 已开发票传出
     1~~20150201~~20150228
     // 发票1"	"Error message shown:
     文件格式错误。请选择正确的增值税发票文件格式。"
     * @author Christion Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void  test_case_7_2_3_10(){
        execute()
    }
    /** @desc error message
     @case 7.2.3.11	"Import file with incorrect data (EN)
     Internal ID incorrect"	"Error message shown:
     There are no transactions found for the VAT Invoices {Internal ID}."
     @case7.2.3.12	"Import file with incorrect data (CN)
     Internal ID incorrect"	"Error message shown:
     无增值税发票 {Internal ID} 对应的事务处理。"
     * @author Christion Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void  test_case_7_2_3_11(){
        execute()

    }

    /**
     * @desc Then Verify China vat page name <pageName>
     * @case 7.2.3.19	Then Open China Vat Import Page
     Then Back China Vat Page
     Then Verify China vat page name <pageName>
     * @author Christion Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void  test_case_7_2_3_19(){
        init()
        navigateToPortalPage()
        clickImport()
        waitForPageToLoad()
        clickReturn()
        waitForPageToLoad()
        checkTrue("Check error message ${caseData.title} should show", context.isTextVisible(caseData.title))
    }

    def verifyImport() {
        if(SEP == "/") fileName = fileName.replace("\\", SEP)
        def impFile =  new File(fileName)
        impFile.withWriter("gbk"){ file -> file << fileContent }
        setUploadPath(fileName)
        clickImportSubmit()
        checkTrue("Check error message ${caseData.errmsg} should show", context.isTextVisible(caseData.errmsg))
        impFile.deleteOnExit()
    }


    def setUploadPath(String path) {

        HtmlElementHandle fileInputElement = this.asElement(locators.importFileBrowser)
        WebDriver webDriver = context.getContext().getWebDriver()
        webDriver.setInputField(fileInputElement, path)

    }



}