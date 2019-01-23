package com.netsuite.chinalocalization.vat

import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.vat.components.XDropdownList
import com.netsuite.testautomation.driver.LocatorType
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser
import net.qaautomation.exceptions.SystemException
import org.junit.After
import org.junit.Before

class TranType{
    static final String CASH_REFUND = "cashrefund"
    static final String CASH_SALE = "cashsale"
    static final String CREDIT_MEMO = "creditmemo"
}

class XTableParser extends TableParser{

    XTableParser(WebDriver webDriver) {
        super(webDriver)
    }


    HtmlElementHandle getHeaderElement(String tableLocator, int col) {
        HtmlElementHandle tableHandle = webDriver.getHtmlElementByLocator(Locator.xpath(tableLocator))
        String headerColumnLocator = String.format(".//tr[%d]/th[%d]",1,col)
        HtmlElementHandle headerColumnHandle = tableHandle.getElementByLocator(Locator.xpath(headerColumnLocator))
        return headerColumnHandle
    }

    String getHeaderElementXML(String tableLocator,int col) {
        HtmlElementHandle headerColumnHandle = getHeaderElement(tableLocator,col)
        return headerColumnHandle.getAttributeValue("innerHTML")
    }

    String getHeaderElementText(String tableLocator,int col) {
        HtmlElementHandle headerColumnHandle = getHeaderElement(tableLocator,col)
        return headerColumnHandle.getAsText()
    }


    HtmlElementHandle getBodyElement(String tableLocator, int row, int col) {
        HtmlElementHandle tableHandle = webDriver.getHtmlElementByLocator(Locator.xpath(tableLocator))
        String columnLocator = String.format(".//tr[%d]/td[%d]",row,col)
        println columnLocator
        HtmlElementHandle columnHandle = tableHandle.getElementByLocator(Locator.xpath(columnLocator))
        return columnHandle
    }

    String getBodyElementXML(String tableLocator,int row,int col) {
        HtmlElementHandle columnHandle = getBodyElement(tableLocator,row,col)
        return  columnHandle.getAttributeValue("innerHTML")
    }

    String getBodyElementText(String tableLocator,int row,int col) {
        HtmlElementHandle columnHandle = getBodyElement(tableLocator,row,col)
        return  columnHandle.getAsText()
    }

}

class VATAppP2TestSuite extends VATAppTestSuite {


    //region path to test data files
    def SEP = File.separator
    def TEST_DATA_PREFIX = "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\".replace("\\",SEP)


    def pathToTestDataFiles() {
        def zhCNFileName = TEST_DATA_PREFIX + this.getClass().getSimpleName()+"_zh_CN.json"
        def enUSFileName = TEST_DATA_PREFIX + this.getClass().getSimpleName()+"_en_US.json"
        return [
                "zhCN": zhCNFileName,
                "enUS": enUSFileName
        ]
    }
    //endregion

    //region role related methods
    def getDefaultRole() {
        return getAdministrator()
    }
    //endregion


    //region WebDriver related methods
    def getWebDriver() {
        return context.webDriver
    }
    //endregion

    //region page navigation part
    def navigateToIssueCreditMemosPage() {
        context.navigateToIssueCreditMemosPage()
    }

    def navigateToRefundCashSalesPage() {
        context.navigateToRefundCashSalesPage()
    }

    def navigateToCreateInvoicePage(){
        context.navigateToCreateInvoicePage()
    }


    def navigateToEnterCashSalesPage() {
        context.navigateToEnterCashSalesPage()
    }


    def navigateToGenerateChinaVATPage() {
//        context.navigateTo("/app/site/hosting/scriptlet.nl?script=317&deploy=1&compid=4803899&whence=")
        context.navigateTo(resolveSuiteletURL("customscript_sl_cn_vat", "customdeploy_sl_cn_vat"))
    }

    //endregion page navigation part


    //region click element part
    def clickFormTab(String tabName) {
        context.clickFormTab(tabName)
    }

    def setFieldValue(LocatorType type, String fieldName, String value) {
        def locator;
        switch (type) {
            case LocatorType.XPATH:
                locator = Locator.xpath(fieldName)
                break
            case LocatorType.CLASS:
                locator = Locator.className(fieldName)
                break
            case LocatorType.ID:
                locator = Locator.id(fieldName)
                break
            case LocatorType.CSS:
                locator = Locator.css(fieldName)
                break
            default:
                throw new SystemException("Locator type not implemented!")
                break
        }
        context.setFieldIdentifiedByWithValue(locator,value)
    }

    def setFieldValue(String fieldName,String value) {
        ElementHandler elementHandler = new ElementHandler()
        elementHandler.waitForElementToBePresent(context.webDriver,fieldName,10)

        asElement(fieldName).clear()
        setFieldValue(LocatorType.XPATH,fieldName,value)
    }


    def isTextVisible(String text) {
        return context.isTextVisible(text)
    }


    def asDropdownList(options) {
        if (options.fieldId) {
            return context.withinDropdownlist(options.fieldId)
        } else if (options.locator) {
            ElementHandler elementHandler = new ElementHandler()
            elementHandler.waitForElementToBePresent(context.webDriver,options.locator,20)
            return new XDropdownList(context, context.webDriver, Locator.xpath(options.locator))
        }
    }


    def selectDropdownItem(locator,item) {
        asDropdownList(locator: locator).selectItem(item)
    }

    def executeScript(String jsScript) {
        context.webDriver.executeScript(jsScript)
    }

    def getJsonFromString(String jsonStr) {
        return context.asJSON(text : jsonStr)
    }

    //endregion



    //region common click part
    def switchToVATTab() {
        def VATTAB = testData.labels.fieldLabel.tabLabel
        clickFormTab(VATTAB)
    }

    def switchToGoodsTab() {
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        clickFormTab(GOODSTAB)
    }

    def enterValueInCreditNumber(String value) {
        setFieldValue(locators.infoSheetNumberInput,value)
    }

    def clickSaveBtnInVAT() {
        asClick(locators.saveCreditBtn)
    }


    def clickOkBtn() {
        asClick(locators.okButton)
    }



    def clickAddItemBtnInGoods() {
        asClick(locators.addItemBtn)
    }

    def assertTextVisible(String assertMsg,String errorMsg) {
        assertTrue(assertMsg,isTextVisible(errorMsg))
    }

    def assertTextContains(String assertMsg,String srcText,String containsText) {
        assertTrue(assertMsg,srcText.contains(containsText))
    }

    def selectCompanyItem(String companyName) {
        selectDropdownItem(locators.customerDropdown,companyName)
    }

    def selectInvoiceTypeItem(String invoiceItem) {
        selectDropdownItem(locators.vatInvoiceType,invoiceItem)
    }


    def selectLocationItem(String locationItem) {
        selectDropdownItem(locators.locationDropdown,locationItem)
    }

    def selectGoodsItem(String goodsItem) {
        selectDropdownItem(locators.itemsDropdown,goodsItem)
    }

    def selectSubsidiaryItem(String subsidiaryItem) {
        selectDropdownItem(locators.subsidiary,subsidiaryItem)
    }

    def enterValueInCreatedFrom(String createdFrom) {
        setFieldValue(locators.vatCreatedFrom,createdFrom)
    }


    def getCurrentRecInternalId() {
        return executeScript("return document.getElementById('id').value;")
    }

    def getRecordJson(String internalId, String tranType) {
        def recordStr = String.format('[{"internalid":%s,"trantype":"%s"}]',internalId,tranType)
        println(recordStr)
        return getJsonFromString(recordStr)
    }

    def getANewRecord(String internalId,String tranType) {
        def ANewRecordStr = String.format('{"internalid":%s,"trantype":"%s"}',internalId,tranType)
        println(ANewRecordStr)
        return getJsonFromString(ANewRecordStr)
    }


    def enterValueInDateFromInGenerateVAT(String fromDate) {
        setFieldValue(locators.custPageDateFromInput,fromDate)
    }


    def enterValueInDateToInGenerateVAT(String toDate) {
        setFieldValue(locators.custPageDateToInput,toDate)
    }

    def clickRefreshButton() {
        asClick(locators.refresh)
        this.waitForPageToLoad()
    }

    def getFirstInternalIdXML() {
        def table = new XTableParser(getWebDriver())
        def internalIdXML = table.getBodyElementXML(locators.firstTransTable,2,1)
        return  internalIdXML
    }

    def getFirstInternalIdElement() {
        def table = new XTableParser(getWebDriver())
        def internalIdEleTd = table.getBodyElement(locators.firstTransTable,2,1)
        def internalIdEle = internalIdEleTd.getElementByLocator(Locator.xpath("./a"))
        return internalIdEle
    }

    def getPageUrl() {
        return getWebDriver().getPageUrl()
    }

    //endregion



    //region   setup teardown related part


    def testData

    @Before
    void setUp() {
        dirtyData = null
        super.setUp()
        this.prepareData()
        testData = data
    }



    @After
    void tearDown() {
        println("tearDown: cleaning up dirty data...")
        if (!dirtyData) {
            return
        }

/*        if(!getDefaultRole().equals(getAdministrator())) {
            switchToRole(administrator)
        }*/

        dirtyData.reverse(true)
        dirtyData.each { dirtyRecord ->
            def vatRecordType = "customrecord_cn_vat_invoices"
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


/*        if(!getDefaultRole().equals(getAdministrator())) {
            switchToRole(accountant)
        }*/
    }
    //endregion

}
