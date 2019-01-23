package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.eclipse.jetty.util.ajax.JSON
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner ("zhen.t.tang@oracle.com")
class VATMergeRedLetterTest extends VATEditPageTestSuite {
    @Rule
    public  TestName name = new TestName()
    def caseData
    def caseFilter

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATMergeRedLetter_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATMergeRedLetter_en_US.json"
        ]
    }
    def caseDataInit(String caseNum){
        caseData = testData[caseNum]
        caseFilter = caseData.filter
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
        // switch to admin
        //switchToRole(administrator)
        dirtyData = record.createRecord(caseData.data)
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

    def doMergeTest(boolean isSalesList){
        dirtyData = previewData = updatePreResults(caseData.previewData)
        clickEdit()
        Thread.sleep(5 * 1000)
        def mergedLineNum = prepareMerge([])
        clickMerge()
        Thread.sleep(5 * 1000)
        checkMergedResult(mergedLineNum,isSalesList)
        clickSave()
        waitForElement(locators.export)
        checkMergedPreview( isSalesList)
        clickEdit()
        waitForElement(editLocators.unmergeButton)
        Thread.sleep(2 * 1000)
        prepareUnMerge(mergedLineNum, isSalesList)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        clickSave()
    }

    /**
     * @desc merge two negative invoices
     * 1.special invoices could only be merged when have same information sheet number
     * @case 19.2.1.6
     * @author zhen tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_2_1_6(){
        caseDataInit("test_case_19_2_1_6")
        doMergeTest(false)
    }
    /**
     * @desc merge two negative invoices
     * common invoices could only be merged when have same vat code & number
     * @case 19.2.1.7
     * @author zhen tang
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_2_1_7(){
        caseData = testData["test_case_19_2_1_7"]
        caseFilter = testData["test_case_19_2_1_7"].filter
        cleanAll = false
//        // switch to admin
//        switchToRole(administrator)
//        def invoices = record.createRecord(caseData.data)
//        String json_def_1 = '''{
//                                        "data":[
//                                         {"main" : {
//                                        "trantype":"customrecord_cn_vat_invoices",
//                                        "custrecord_cn_invoice_type_fk_tran": "internalID",
//                                        "custrecord_cn_vat_invoice_code":"1000000001",
//                                        "custrecord_cn_vat_invoice_number":"100001",
//                                        "custrecord_cn_vat_status":"2",
//                                        "custrecord_cn_vat_invoice_date":"20180201"
//                                        }
//                                        }
//                                        ]
//                                        }'''
//        json_def_1 = json_def_1.replace("internalID", invoices[0].internalid)
//        def data1 = JSON.parse(json_def_1).data
//        def records2= record.createRecord(data1)
//        String json_def_2 = '''{
//                                        "data":[
//                                         {"main" : {
//                                        "trantype":"customrecord_cn_vat_invoices",
//                                        "custrecord_cn_invoice_type_fk_tran": "internalID",
//                                        "custrecord_cn_vat_invoice_code":"1000000001",
//                                        "custrecord_cn_vat_invoice_number":"100001",
//                                        "custrecord_cn_vat_status":"2",
//                                        "custrecord_cn_vat_invoice_date":"20180201"
//                                        }
//                                        }
//                                        ]
//                                        }'''
//        json_def_2 = json_def_2.replace("internalID", invoices[2].internalid)
//        def data2 = JSON.parse(json_def_2).data
//        def records3 = record.createRecord(data2)
//        // switch back to accountant
//        switchToRole(accountant)
        navigateToPortalPage()
        waitForPageToLoad()

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(caseFilter.subsidiary)
        }

        asDropdownList(locator: locators.invoiceType).selectItem(caseFilter.invoicetype)
        context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
        context.setFieldWithValue("custpage_dateto", caseFilter.dateto)
        context.setFieldWithValue("custpage_documentnumberfrom_formattedValue", caseFilter.docNumFrom)
        context.setFieldWithValue("custpage_documentnumberto_formattedValue", caseFilter.docNumTo)


        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        Thread.sleep(3 * 1000)
        doMergeTest(false)
    }

}
