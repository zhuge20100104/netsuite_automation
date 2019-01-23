package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.testautomation.html.parsers.TableParser
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("zhen.t.tang@oracle.com")
class VATLinkTest extends VATEditPageTestSuite{
    def caseData
    def caseFilter
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATLink_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATLink_en_US.json"
        ]
    }
    def caseDataInit(String caseNum){
        caseData = testData[caseNum]
        caseFilter = testData[caseNum].filter
        cleanAll = false
        //insertData(caseData)
        navigateToPortalPage()
        waitForPageToLoad()
        setFilter(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        Thread.sleep(3 * 1000)
    }
    def insertData(caseData){

        def invoices = dirtyData = record.createRecord(caseData.data)
        // switch back to accountant
        //switchToRole(accountant)

    }
    def setFilter(caseFilter){
        if (caseFilter.subsidiary) {
            if (context.isOneWorld()) {
                asDropdownList(locator: locators.subsidiary).selectItem(caseFilter.subsidiary)
            }
        }
        if (caseFilter.invoicetype) {
            asDropdownList(locator: locators.invoiceType).selectItem(caseFilter.invoicetype)
        }
        if (caseFilter.transType) {
            // not consider multi transaction type setting yet
            asMultiSelectField("custpage_transactiontype").setValues(caseFilter.transType)
        }
        if (caseFilter.datefrom) context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
        if (caseFilter.dateto) context.setFieldWithValue("custpage_dateto", caseFilter.dateto)
        if (caseFilter.docNumFrom) context.setFieldWithValue("custpage_documentnumberfrom_formattedValue", caseFilter.docNumFrom)
        if (caseFilter.docNumTo) context.setFieldWithValue("custpage_documentnumberto_formattedValue", caseFilter.docNumTo)
        if (caseFilter.enableSalesList) asDropdownList(locator: locators.enableSalesList).selectItem(caseFilter.enableSalesList)
    }
    def doMergeTest(boolean isSalesListBefore,isSalesListAfter){
        //previewData = getAllResults()
        int i=30
        while (!context.doesFieldExist("merge") && i>0){
            Thread.sleep(2 * 1000)
            clickEdit()

            waitForPageToLoad()
            i--
        }
        Thread.sleep(3 * 1000)
        for(int index =1; index <= previewData.size(); index ++){
            asClickTransCheckbox(index)
        }
        checkGroupSameItem()
        clickMerge()
        Thread.sleep(3 * 1000)

        clickSave()
        waitForElement(locators.export)
        //------------------------------------------
        Thread.sleep(5000)
        clickHyperlink(0)
        checkConsolidatedTransactionsInPopUPWindow()

    }
//click the link for Nth invoice(n>=0)
    def clickHyperlink(int num){
        def iter = num*2+2
        def tableLocator = "//div[@id='vat_report']/table[${iter}]/tbody/tr[2]/td[1]/a"
        asClick(tableLocator)
    }



    def checkConsolidatedTransactionsInPopUPWindow(){
        def company = aut.getContext().getProperty("testautomation.nsapp.default.account")
        //def pagetitle = context.getPageTitle().split('-')[1..-1].join("")
        def pagetitle = context.getPageTitle()
        //def pagetocheck="NetSuite国际版 (PSG_QA - China_Localization_Feature Testing_SI auto_T2)"

        pagetitle = pagetitle[pagetitle.indexOf('-')+1..-1].trim()
        def  currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindow(pagetitle)
        checkPopUpTransactionsTableData()
        asClick("//*[@id='div__bodytab']/tbody/tr[2]/td[1]/a")
        waitForPageToLoad()
        checkTransactionPageInfo(0)
    }
    def checkTransactionPageInfo(int num){
        def type = asText("//*[@class='uir-record-type']")
        def docNum = asText("//*[@class='uir-record-id']")
        assertAreEqual("check transaction type", type, previewData[num]."tranType")
        assertAreEqual("check transaction type", docNum, previewData[num]."docno")
    }
    def checkPopUpTransactionsTableData(){

        TableParser table = new TableParser(context.webDriver)
        def tablelocator = "//*[@id='div__bodytab']"

        def results = table.parseTable(tablelocator,null, "//tbody//tr")[1..-2]
        def i = 0
        for(transaction in previewData){
            assertAreEqual("check internalId",transaction."internalid",results[i]."0")
            assertAreEqual("check TranType",transaction."tranType",results[i]."1")
            assertAreEqual("check docNum",transaction."docno",results[i]."2")
            i++;

        }

        //add check logic
    }
    /**
     * @desc Hyperlink for Internal ID ---consolidated invoice
     * click the hyperlink on ID field
     * check the children transactions show and link
     * @case 19.6.1.1
     * @author zhen tang
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void test_case_19_6_1_1(){
        caseDataInit("test_case_19_6_1_1")
        dirtyData = previewData = getAllResults()
        assertAreEqual("should find 4 record",caseData.data.size(), previewData.size())
        doMergeTest(false,false)

    }
    /**
     * @desc Hyperlink for Internal ID - Splited VAT Invoice
     * click the hyperlink on ID field
     * the split transactions should link to the same transaction
     * @case 6.3.8
     * @author zhen tang
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void test_case_6_3_8(){
        caseDataInit("test_case_6_3_8")
        previewData = getAllResults()
        clickHyperlink(1)
        checkTransactionPageInfo(1)

    }
}
