package com.netsuite.chinalocalization.vat

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.chinalocalization.page.Setup.UserPreferencePage
import com.netsuite.testautomation.html.parsers.TableParser
import com.netsuite.testautomation.junit.runners.SuiteSetup
import org.junit.After
import org.junit.Before

/**
 * @desc Base test suite implementation for VAT
 * @author Jianwei Liu
 */
class VATAppTestSuite extends BaseAppTestSuite {

    @Inject
    VATLocators locators

    @Inject
    EnableFeaturesPage enableFeaPage
    @Inject
    UserPreferencePage userPrePage

    def static editData
    def static testData
    def dirtyData
    def cleanAll  // clean Flag true will delete transacton false just consolidated
    def headerlabels
    def itemlabels
    def editPath= [
            "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\vat_edit_zh_CN.json",
            "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\vat_edit_en_US.json"
    ]

    @SuiteSetup
    void setUpTestSuite() {
        super.setUpTestSuite()

        testData = data
        if(!testData){
            throw "call supper setup error could not get testdata"
        }
        if (isTargetLanguageChinese()) {
            if (doesFileExist(editPath.zhCN)) {
                editData = context.asJSON(path: editPath.zhCN.replace("\\",SEP))
            }
        } else {
            if (doesFileExist(editPath.enUS)) {
                editData = context.asJSON(path: editPath.get("enUS").replace("\\",SEP))
            }
        }
        def result = context.getPreference("DATEFORMAT")
        if (!(result =="M/D/YYYY")) {
            userPrePage.navigateToURL()
            userPrePage.setDateFormat("M/D/YYYY",true)
        }

    }
    def getEditData(){
        editData
    }
    def getTestData(){
        testData
    }
    def methodMissing(String name, args) {
        // Intercept method that starts with find.
        def field,funName
        if (name.startsWith("setUser")) {
            field = name[7..-1]
            def result = context.getPreference(field)
            if (!result) throw new MissingMethodException(name, this.class, args)
            if (!(result ==~ /${args[0]}/)) {
                userPrePage.navigateToURL()
                context.setFieldWithValue(field, args[0] )
                userPrePage.clickSave()
            }
        }else{
                throw new MissingMethodException(name, this.class, args)
        }
    }


    @Before
    void setUp() {
        super.setUp()
        cleanAll =true
        headerlabels = editData.tableHeader
        itemlabels = editData.itemHeader
        //if (!testData){
            def path = pathToTestDataFiles()
            if (path) {
                if (isTargetLanguageChinese()) {
                    if (doesFileExist(path.zhCN)) {
                        testData = context.asJSON(path: path.zhCN.replace("\\",SEP))
                        editData = context.asJSON(path: editPath.zhCN)
                    }
                } else {
                    if (doesFileExist(path.enUS)) {
                        testData = context.asJSON(path: path.enUS.replace("\\",SEP))
                        editData = context.asJSON(path: editPath.get("enUS"))
                    }
                }

                headerlabels = editData.tableHeader
                itemlabels = editData.itemHeader
            }
        //}




    }
    def switchToHome(){
        if((context.webDriver!=null) &&(context.webDriver.getPageUrl() != null)) {
            context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
            def  currentHandle = context.webDriver.getWindowHandle()
            context.webDriver.closeWindow(currentHandle)
            context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
        }
    }

    @After
    void tearDown() {
        println("tearDown: cleaning up dirty data...")
        switchToHome()
        if (!dirtyData) {
            return
        }
        //switchToRole(administrator)

        dirtyData.reverse(true)
        dirtyData.each { dirtyRecord ->
            def vatRecordType = "customrecord_cn_vat_invoices"
            if ("internalId" in dirtyRecord) dirtyRecord.internalid = dirtyRecord.internalId
            def columns = [
                    record.helper.column("internalid").create(),
                    record.helper.column("parent").create()
            ]

            def filters = [
                    record.helper.filter("custrecord_cn_invoice_type_fk_tran").isnot(null),
                    record.helper.filter("custrecord_cn_invoice_type_fk_tran").is(dirtyRecord.internalid)
            ]
            def vatRecords = record.searchRecord("${vatRecordType}", filters, columns)
            if (!vatRecords) {
                filters = [
                        record.helper.filter("custrecord_cn_vat_invoice_code").isnot(null),
                        record.helper.filter("custrecord_cn_vat_invoice_code").is(dirtyRecord.internalid)
                ]
                vatRecords = record.searchRecord("${vatRecordType}", filters, columns)
            }

            vatRecords.each { vatRecord ->
                deleteItem(vatRecord.internalid)
                //delete children customrecord_cn_vat_invoices
                record.deleteRecord("${vatRecordType}", vatRecord.internalid)
                if(!isSiblingExisted(vatRecord.parent)){
                    //delete parent customrecord_cn_vat_invoices
                    deleteItem(vatRecord.parent)
                    record.deleteRecord("${vatRecordType}", vatRecord.parent)
                }
            }
        }
        //delete transactions
        if (cleanAll) {
            dirtyData.each { dirtyRecord ->
                record.deleteRecord(dirtyRecord.trantype, dirtyRecord.internalid)
            }
        }
        //switchToRole(accountant)
    }

    def isSiblingExisted(parentId) {
        //def ITEMS_RECORD = 'customrecord_cn_vat_edit_item';

        def columns = [
                record.helper.column("internalid").create(),
                record.helper.column("parent").create()
        ]

        def filters = [
                record.helper.filter("parent").is(parentId)
        ]
        def vatRecords = record.searchRecord("customrecord_cn_vat_invoices", filters, columns)
        if (vatRecords.iterator().size() == 0) {
            return false
        } else {
            return true
        }
    }


    def deleteItem(parentId) {
        def editItemType = "customrecord_cn_vat_edit_item"
        def columns = [
                record.helper.column("internalid").create(),
                record.helper.column("custrecord_cn_vat_parent").create()
        ]

        def filters = [
                record.helper.filter("custrecord_cn_vat_parent").is(parentId)
        ]
        def vatRecords = record.searchRecord("${editItemType}", filters, columns)
        vatRecords.each { vatRecord -> record.deleteRecord("${editItemType}", vatRecord.internalid) }
    }

    def pathToTestDataFiles() {}



    def navigateToPortalPage() {
        context.navigateTo(resolveSuiteletURL("customscript_sl_cn_vat", "customdeploy_sl_cn_vat"))
    }

    def clickRefresh() {
        asClick(locators.refresh)

    }

    def clickExport() {
        asClick(locators.export)
    }

    def clickImport() {
        asClick(locators.importButton)
    }

    def clickImportSubmit() {
        asClick(locators.importSubmit)
    }

    def clickReturn() {
        asClick(locators.returnButton)
    }

    def clickEdit() {
        asClick(locators.edit)
        waitForPageToLoad()
    }
    /**
     *
     * @return count of refresh results
     */
    def getResultCount() {
        def headTables = asElements("//table[@class='vatheadertable']")
        return headTables == null ? 0 : headTables.size()
    }

    /**
     * if expected internal id exists in result tables
     *
     * @param expectedId
     * @return
     */
    def doesTransactionExistInResults(String internalId) {
        def resultData = getAllResults()
        return resultData.any { line -> internalId.equals(String.valueOf(line.internalid)) }
    }


    def getTableData(String internalId){
        def resultData = getAllResults()
        return resultData.find { line -> internalId.equals(String.valueOf(line.internalid)) }
    }

    /**
     * parse and get all result table
     *
     * @return List < Map < String colname , String value > >
     */
    def getAllResults() {
        def result = [],tmp
        TableParser table = new TableParser(context.webDriver)
        def tableLocator = "//*[@id='vat_report']/table[_index]"

        def tableStructure = [
                header: [
                        docno: [row: "0", col: "1"],
                        tranType :[row: "0", col: "0"],

                ],
                body  : [
                        internalid: [row: "0", col: "0"],
                        message: [row: "dataTable.size() - 1", col: "1"]
                ]

        ]
        for (int i = 0; i < getResultCount(); i++) {
            result[i] = [:]
            def headerTable = table.parseTable(tableLocator.replace("_index", String.valueOf(i * 2 + 1)), null, "//tbody//tr")
            def dataTable = table.parseTable(tableLocator.replace("_index", String.valueOf(i * 2 + 2)), null, "//tbody//tr")
            tableStructure.header.each { k, v ->
                tmp = headerTable.get(Eval.me(v.row)).get(v.col).split(":")
                result[i][k] =tmp.size()>1 ? tmp[1].trim() : ""
            }

            tableStructure.body.each { k, v ->
                result[i][k] = dataTable.get(Eval.me('dataTable',dataTable,v.row)).get(v.col)
            }
        }

        return result
    }

    def setMaxChinaVATInvoiceAmount(params) {
        if (context.isOneWorld()) {
            def filter = [record.helper.filter('namenohierarchy').contains(params.name)]
            def column = [record.helper.column('internalid').create()]
            def results = record.searchRecord('subsidiary', filter, column)
            def internalId = results[0].internalid // subsidiary name should be identified
            return record.updateRecord('subsidiary', internalId, ['custrecord_cn_vat_max_invoice_amount': params.amount])
        } else {
            // it seems nlapiLoadConfiguration not supported in client script
            /*def script = """
                var company = nlapiLoadConfiguration('companyinformation');
                company.setFieldValue('custrecord_cn_vat_max_invoice_amount', ${params.amount});
                nlapiSubmitConfiguration(company);
            """
            return context.executeScript(script)
            */
            context.navigateToCompanyInformationPage()
            def value = params.amount ? String.valueOf(params.amount) : ""
            context.setFieldWithValue("custrecord_cn_vat_max_invoice_amount", value)
            asClick("//*[@id=\"submitter\"]")
        }
    }
}
