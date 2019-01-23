package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
@TestOwner ("wan.feng@oracle.com")
/**
 * @desc VAT workflow cases suite
 * @author Feng Wan
 */
class VATExportImportFlowTest extends VATAppTestSuite {

    private String dataFilePath = "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\"
    private List<Path> createdFiles = []
    def caseData
    private String expectedInternalId

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
        caseData = testData["data"]
        //switchToRole(administrator)
        dirtyData = record.createRecord(caseData)
        //switchToRole(accountant)
        expectedInternalId = dirtyData[0].internalid
    }
/**
     * @desc Test the who flow of VAT
     *       create invoice -- refresh -- export -- import -- refresh -- import another file with different vat info
     * @case 11.2.1
     * @case 11.3.1.1
     * @case 11.3.2.1
     * @case 11.4.2.1
     * @case 11.3.3.1
     * @case 11.4.3.1
     * @author Feng Wan
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_11_3_1() {

        test_case_11_2_1("11.2.1")

        navigateToPortalPage()

        test_case_11_3_1_1("11.3.1.1")

        test_case_11_3_2_1("11.3.2.1")

        test_case_11_4_2_1("11.4.2.1")

        test_case_11_3_3_1("11.3.3.1")

        test_case_11_4_3_1("11.4.3.1")


    }

    // 11.2.1 assert new invoice VAT status is empty
    private void test_case_11_2_1(String caseNo) {
        verifyVatStatus(caseNo)
    }

    //11.3.1.1 refresh, export,  assert status is exported
    private void test_case_11_3_1_1(String caseNo) {

        runRefreshSteps(caseNo)
        TimeUnit.SECONDS.sleep(10)
        clickExport()
        TimeUnit.SECONDS.sleep(10)
        verifyVatStatus(caseNo)
    }

    //11.3.2.1 refresh again, the invoice is not listed in result
    private void test_case_11_3_2_1(String caseNo) {
        runRefreshSteps(caseNo)
    }

    //11.4.2.1 import file, check status is completed
    private void test_case_11_4_2_1(String caseNo) {
        runImportSteps(caseNo)
    }

    //11.3.3.1 refresh again, the invoice is not listed in result
    private void test_case_11_3_3_1(String caseNo) {
        runRefreshSteps(caseNo)
    }

    //11.4.3.1 import another file with same internal id and different code&number, check status is completed
    private void test_case_11_4_3_1(String caseNo) {
        runImportSteps(caseNo)
    }

    private void runRefreshSteps(String caseNo) {
        waitForPageToLoad()
        setQueryParams()
        clickRefresh()
        TimeUnit.SECONDS.sleep(10)
        def expectedVisible = testData[caseNo]["expected"]["visible"]
        assertAreEqual(caseNo + " assert internal id in search results: " + expectedVisible, doesTransactionExistInResults(expectedInternalId).toString(), expectedVisible)
    }

    private void runImportSteps(String caseNo) {
        clickImport()
        waitForPageToLoad()
  //      assertTrue("Vat import page title visible ", context.isTextVisible(caseData.expectedResults.importtitle))
        TimeUnit.SECONDS.sleep(10)
        setUploadPath( createTempImportFile(caseNo))
        TimeUnit.SECONDS.sleep(5)
        clickImportSubmit()
        waitForElement(locators.importAlert)
        verifyVatStatus(caseNo)
        clickReturn()
    }

    void setQueryParams() {
        if(getContext().isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(testData.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(testData.filter.invoicetype)
        context.setFieldWithValue("custpage_datefrom", format.formatDate(testData.filter.datefrom))
        context.setFieldWithValue("custpage_dateto", format.formatDate(testData.filter.datefrom))
        def tranid = dirtyData[0].tranid
        if (tranid != null) {
            context.setFieldWithValue("custpage_documentnumberfrom", tranid)
            context.setFieldWithValue("custpage_documentnumberto", tranid)
        }

    }

    private def verifyVatStatus(String caseNo) {
        def caseExpected = testData[caseNo]["expected"]
        String status = caseExpected.status
        def STATUS = ["1": "exported", "2": "completed"]

        def filters = [
                //record.helper.filter("custrecord_cn_vat_invoice_code").is(caseExpected.code),
                record.helper.filter("custrecord_cn_invoice_type_fk_tran").is(expectedInternalId)]
        def columns = [ record.helper.column("custrecord_cn_vat_status").create(),
                        record.helper.column("custrecord_cn_vat_invoice_code").create()
        ]
        ArrayList vatRecords = record.searchRecord("customrecord_cn_vat_invoices", filters, columns)

        if (caseExpected.status == "") {
            checkAreEqual(caseNo + " assert vat status is empty", vatRecords.size(), 0)
        } else {
            checkAreNotEqual(caseNo + " assert vat status is not empty", vatRecords.size(), 0)
            checkAreEqual(caseNo + " assert vat status is " + STATUS[status], vatRecords[0]["custrecord_cn_vat_status"], status)
        }

    }


    private createTempImportFile(String caseNo) {
        String fileTemplate = testData["file template"]
        def vatInfo = testData[caseNo]["expected"]

        String fileContent = fileTemplate.replace("{internalid}", expectedInternalId).replace("{vatcode}", vatInfo.code).replace("{vatnumber}", vatInfo.number)
        String fileName = "${dataFilePath}${caseNo}.txt"
        if(SEP == "/") fileName = fileName.replace("\\", SEP)
        def impfile =  new File(fileName)
        impfile.withWriter("gbk"){ file -> file << fileContent }
        //new File(fileName).withWriter("gbk"){ file -> file << fileContent }
        createdFiles.add(impfile)
        fileName

    }


    private setUploadPath(path) {

        HtmlElementHandle fileInputElement = this.asElement(locators.importFileBrowser)
        WebDriver webDriver = context.getContext().getWebDriver()
        webDriver.setInputField(fileInputElement, path.toString())

    }




    @After
    void tearDown() {

        createdFiles.each { filePath ->
            if(filePath.exists()) filePath.delete()  }
           // Files.deleteIfExists(filePath) }
        super.tearDown()

    }

}