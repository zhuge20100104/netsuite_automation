package com.netsuite.chinalocalization.vat

import com.netsuite.common.OW

import com.netsuite.common.P2
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@TestOwner ("christina.chen@oracle.com")
/**
 * @desc VAT Items Excluded Include Test
 * @author Christina Chen
 */
class VATItemExcludeIncludeTest extends VATEditPageTestSuite {

    String dataFilePath = "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\"
    String fileTemplate
    def vatInfo = [
            "code": "code1",
            "number": "number1",
            "status": "2"
    ]
    String fileName
    def caseData
    def caseFilter
    def expResult
    @Rule
    public  TestName name = new TestName()
    def pathToTestDataFiles() {
        def partFilePath = dataFilePath + this.getClass().getSimpleName()

        return [
                "zhCN": partFilePath + "_zh_CN.json",
                "enUS": partFilePath + "_en_US.json"
        ]

    }

    @Before
    void setUp() {
        super.setUp()
        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        cleanAll = false
        if (caseData.containsKey("expectedResult")){expResult = caseData.expectedResult}

    }
    @After
    void tearDown(){
        if (fileName) new File(fileName).delete()
        super.tearDown()
    }

    def setQuery(options=[:]) {
        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(editData.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(editData.filter.invoicetype_s)
        if (caseFilter.datefrom) context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
        if (caseFilter.dateto) context.setFieldWithValue("custpage_dateto", caseFilter.dateto)

        context.setFieldWithValue("custpage_documentnumberfrom", caseFilter.tranid)
        context.setFieldWithValue("custpage_documentnumberto", caseFilter.tranid)

        clickRefresh()
        waitForPageToLoad()
    }
/**
     * @descItems Excluded from Normal Item Line
     * @case  14.2.1.1   Items Excluded from Normal Item Line
     * @case  14.2.1.2   check export button enabled
     */
    @Category([P2.class,OW.class])
    @Test
    void test_case_14_2_1() {
        navigateToPortalPage()
        waitForPageToLoad()
        setQuery()
        waitForElement(locators.edit)
        assertAreEqual("Refresh button enabled",  asAttributeValue(locators.refresh, "disabled"), null)
        assertAreEqual("Import button enabled",  asAttributeValue(locators.importButton, "disabled"),null)
        previewData = updatePreResults(caseData.previewData)
        assertAreEqual("No item show up",previewData[0].lineQuantity ,0)

    }

    /**
     * @descItems Excluded from Normal Item Line
     * @case 14.2.1.1  Items Included as Normal Item Line
     * @case 14.2.1.2  Check Exported file
     * @case 11.4.1.1
     * Considering Split Case
     Import VAT Invoice Code when VAT Invoice Status is Null
     All transaction type
    */
    @Category([P2.class,OW.class])
    @Test
    void test_case_14_2_2() {
        navigateToPortalPage()
        waitForPageToLoad()
        setQuery()
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        dirtyData = previewData =updatePreResults(caseData.previewData)

        assertAreEqual("",previewData[0].lineQuantity ,6)
        assertAreEqual("",previewData[0].vatlineQuantity ,12)
        verifyVatStatus(previewData[0].internalId,"")
        clickExport()
        waitForPageToLoad()
        Thread.sleep(10 * 1000)
        setQuery()
        verifyVatStatus(previewData[0].internalId,"1")
        Thread.sleep(5 * 1000)
        assertFalse("assert import internalid ${previewData[0].internalId} not in search results", context.isTextVisible(previewData[0].docNum))
        runImportSteps("import1",previewData[0].internalId)
    }

    void runImportSteps(String step,internalId) {
        clickImport()
        waitForPageToLoad()
        Thread.sleep(5* 1000)
        fileTemplate = testData["file template"]
        String fileContent = fileTemplate.replace("{internalid}", internalId).replace("{vatcode}", vatInfo.code).replace("{vatnumber}", vatInfo.number)
        fileName = "${dataFilePath}${step}.txt".replace("\\",SEP)

        new File(fileName).withWriter("gbk") { file -> file << fileContent }
        HtmlElementHandle fileInputElement = this.asElement(locators.importFileBrowser)
        WebDriver webDriver = context.getContext().getWebDriver()
        webDriver.setInputField(fileInputElement, fileName)
        Thread.sleep(5 * 1000)
        clickImportSubmit()
        waitForElement(locators.importAlert)
        verifyVatStatus(internalId,"2")
        clickReturn()
    }
    def verifyVatStatus(internalId, status) {


        def STATUS = ["1": "exported", "2": "completed"]

        def filters = [
                //record.helper.filter("custrecord_cn_vat_invoice_code").is("code1"),
                record.helper.filter("custrecord_cn_invoice_type_fk_tran").is(internalId)]
        def columns = [ record.helper.column("custrecord_cn_vat_status").create()]
        def vatRecords = record.searchRecord("customrecord_cn_vat_invoices", filters, columns)

        if (status == "") {
            assertAreEqual(" assert vat status is empty", vatRecords.size(), 0)
        } else {
            assertAreNotEqual( " assert vat status is not empty", vatRecords.size(), 0)
            assertAreEqual( " assert vat status is " + STATUS[status], vatRecords[0]["custrecord_cn_vat_status"], status)
        }

    }

}