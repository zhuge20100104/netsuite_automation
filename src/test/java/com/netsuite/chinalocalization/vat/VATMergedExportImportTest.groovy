package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.junit.TestOwner
import org.junit.experimental.categories.Category
import org.junit.After
import org.junit.Test

import java.nio.file.Files
import java.util.concurrent.TimeUnit

@TestOwner("christina.chen@oracle.com")
class VATMergedExportImportTest extends VATEditPageTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATMergedExportImportTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATMergedExportImportTest_zh_CN.json"
        ]
    }

    private  def caseData
    private  def caseFilter
    private  def expResult
    def mergeId,firstId
    def createdFiles = []
    def init(){
        caseData = testData
        caseFilter = testData.filter
        cleanAll = false
        //switchToRole(administrator)
        dirtyData = record.createRecord(caseData.data)
        //switchToRole(accountant)
        navigateToPortalPage()
        waitForPageToLoad()
    }
    def setQuery(options=[:]) {


        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(editData.filter.subsidiary)
        }
        if (options.invoiceType) {
            asDropdownList(locator: locators.invoiceType).selectItem(options.invoiceType)
        }

        if (options.customer){
            asMultiSelectField("custpage_customer").selectItems( *options.customer)
        }
        if (options.transactionType){
            asMultiSelectField("custpage_transactiontype").setValues(*options.transactionType)
        }
        if (options.isSaleslist in [ "yes", "no"] ) {
            context.setFieldWithValue(
                    "custpage_saleslist", editData.saleslist.get(options.isSaleslist))
        }
        //editData.tranType.CustInvc, editData.tranType.CashSale
        if (caseFilter.datefrom )context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
        if (caseFilter.dateto)context.setFieldWithValue("custpage_dateto", caseFilter.dateto)
        if (!caseFilter.to_tranid) { caseFilter.to_tranid = caseFilter.tranid }
        if (caseFilter.tranid) context.setFieldWithValue("custpage_documentnumberfrom", caseFilter.tranid)
        if (caseFilter.to_tranid) context.setFieldWithValue("custpage_documentnumberto", caseFilter.to_tranid)


    }
    void runImportSteps(String step) {
        clickImport()
        waitForPageToLoad()
        Thread.sleep(5* 1000)
        uploadImportFile(step)
        Thread.sleep(5 * 1000)
        clickImportSubmit()
        waitForElement(locators.importAlert)
        verifyVatStatus(step)
        clickReturn()
    }
    def uploadImportFile(String importStep ) {
        String dataFilePath = "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\"
        String fileTemplate = testData["file template"]
        def vatInfo = testData[importStep]["expected"]
        String fileContent = fileTemplate.replace("{internalid}", mergeId).replace("{vatcode}", vatInfo.code).replace("{vatnumber}", vatInfo.number)
        String fileName = "${dataFilePath}${importStep}.txt".replace("\\",SEP)
        new File(fileName).withWriter("gbk") { file -> file << fileContent }
        createdFiles.add(fileName)
        HtmlElementHandle fileInputElement = this.asElement(locators.importFileBrowser)
        WebDriver webDriver = context.getContext().getWebDriver()
        webDriver.setInputField(fileInputElement, fileName)
    }

    private def verifyVatStatus(String importStep) {
        def caseExpected = testData[importStep]["expected"]
        String status = caseExpected.status
        def STATUS = ["1": "exported", "2": "completed"]

        def filters = [
                record.helper.filter("custrecord_cn_vat_invoice_code").is(caseExpected.code),
                record.helper.filter("custrecord_cn_invoice_type_fk_tran").is(firstId)]
        def columns = [ record.helper.column("custrecord_cn_vat_status").create()]
        def vatRecords = record.searchRecord("customrecord_cn_vat_invoices", filters, columns)

        if (caseExpected.status == "") {
            assertAreEqual(importStep + " assert vat status is empty", vatRecords.size(), 0)
        } else {
            assertAreNotEqual(importStep + " assert vat status is not empty", vatRecords.size(), 0)
            assertAreEqual(importStep + " assert vat status is " + STATUS[status], vatRecords[0]["custrecord_cn_vat_status"], status)
        }

    }
    private void test_case_no_salelist(){
        init()
        /*
        caseData = testData
        caseFilter = testData.filter
        navigateToPortalPage()
        waitForPageToLoad()
        */
        setQuery()
        clickRefresh()
        waitForElement(locators.edit)
        Thread.sleep(5 * 1000)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)

        firstId = headerTextInColumnOfRow("internalId",1)
        int i =1
        while (i <= getHeaderCount()) {
            asClickTransCheckbox(i)
            i++
        }
        checkGroupSameItem()
        clickMerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        mergeId = headerTextInColumnOfRow("internalId",1)
        assertTrue("Merged internalId ${mergeId} start with CON",mergeId.startsWith("CON"))
        clickSave()
        println("finish merge")
        waitForElement(locators.export)
        Thread.sleep(5 * 1000)
        assertTrue("assert merged internalid ${mergeId} in preview Page", context.isTextVisible(mergeId))
        clickExport()
        waitForPageToLoad()
        Thread.sleep(10 * 1000)
        setQuery()
        clickRefresh()
        Thread.sleep(5 * 1000)
        assertFalse("assert merged internalid ${mergeId} not in search results", context.isTextVisible(mergeId))
        runImportSteps("import1")
        setQuery()
        clickRefresh()
        Thread.sleep(5 * 1000)
        assertFalse("assert merged internalid ${mergeId} not in search results", context.isTextVisible(mergeId))
        /*runImportSteps("import2")
        setQuery()
        clickRefresh()
        Thread.sleep(5 * 1000)
        assertFalse("assert merged internalid ${mergeId} not in search results", context.isTextVisible(mergeId))*/
    }

    /**
     * @desc If the selected transactions have total amount exceed max amount.
     *   Check error message
     * @case 19.2.2.1 single merge
     * [invoice]: [IN10850]
     item1, 1
     item1, 1
     item2, 1
     * @author Christina Chen
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_4_3() {
         test_case_no_salelist()
    }
    @After
    void tearDown() {
        super.tearDown()
        createdFiles.each { filePath -> new File(filePath).delete() }

    }

}
