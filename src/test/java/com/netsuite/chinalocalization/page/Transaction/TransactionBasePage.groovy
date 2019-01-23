package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.base.lib.element.ElementHandler

import javax.inject.Inject

class TransactionBasePage extends PageBaseAdapterCN {

    @Inject
    ElementHandler elementHandler

    protected String url
    protected String title

    //Default value for a new transaction
    String dft_vendor = "CN Automation Vendor"
    String dft_location = "CN_BJ"
    String dft_memo = "default memo"

    //Field ID
    public static String FIELD_ID_HEADER_CFS = "custbody_cseg_cn_cfi"
    public static String FIELD_ID_ITEM_CFS = "custcol_cseg_cn_cfi"
    public static String FIELD_ID_EXPENSE_CFS = "custcol_cseg_cn_cfi"
    private static final String subsidiaryFieldId = "subsidiary"
    public static String FIELD_ID_DATE = "trandate"
    public static String FIELD_ID_APPROVALSTATUS = "approvalstatus"

    //Other Locator
    Locator BTN_SAVE = Locator.xpath("//input[@id='submitter']")
    Locator BTN_EXPENSE = Locator.xpath("//a[@id='expensetxt']")
    Locator BTN_ITEM = Locator.xpath("//a[@id='itemtxt']")

    //XPATH
    protected static final String XPATH_BTN_EXPENSE = "//a[@id='expensetxt']"
    protected static final String XPATH_BTN_ITEM = "//a[@id='itemtxt']"
    protected static final String XPATH_BTN_CANCEL = "//*[@id='_cancel']"
    protected static final String XPATH_TR_EXPENSE_HEADER = "//tr[@id='expense_headerrow']"
    protected static final String XPATH_TR_ITEM_HEADER = "//tr[@id='item_headerrow']"
    protected static final String XPATH_TR_JOURNAL_HEADER = "//tr[@id='line_headerrow']"
    protected static final String XPATH_TR_ACTIVE_LINE = "//tr[contains(@class,'uir-machine-row-focused')]"
    protected static final String LABEL_CFS_CN = "中国现金流量表项"
    protected static final String LABEL_CFS_EN = "China Cash Flow Item"

    TransactionBasePage(String title, String url) {
        this.title = title
        this.url = url
    }

    //New a transaction but not save
    def createTransaction(String url, String entityId, String location, String memo) {
        context.navigateTo(url)
        fillHeader()
        clickSaveButton()
    }

    def createDefaultTransaction() {
        context.navigateTo(url)
        fillHeader(dft_vendor, dft_location, dft_memo)
    }

    // Common function
    def createTransaction(caseDataObj) {
        context.navigateTo(url)
        fillData(caseDataObj)
    }

    def fillData(def caseDataObj) {
        context.waitForPageToLoad()
        caseDataObj.each {
            //Set subsidiary
            if (it.key == "subsidiary") {
                context.setFieldWithText(it.key, it.value)
            }

            //Set customer
            if (it.key == "customer" || it.key == "entity") {
                String customerId = Utility.getEntityId(context, "customer", it.value)
                context.setFieldWithValue(it.key, customerId)
            }

            //Set location
            if (it.key == "location") {
                context.setFieldWithText(it.key, it.value)
            }

            //Set vendor
            if (it.key == "vendor") {
                context.setFieldWithText(it.key, it.value)
            }

        }
    }

    def fillHeader(String vendor, String location, String memo) {

        //Set vendor
        String entityId = Utility.getEntityId(context, "vendor", vendor)
        context.setFieldWithValue("entity", entityId)

        //Set location
        context.waitForPageToLoad()
        context.setFieldWithText("location", location)
    }

    def fillItem(String item, String quantity, String taxCode, String rate, String amount, String cfsItem) {
    }

    def navigateToURL() {
//        context.navigateTo(url)
        context.navigateToNoWait(url)
        elementHandler.waitForElementToBePresent(context.webDriver,XPATH_BTN_CANCEL)

    }

    def navigateToEditURL(String tranId) {
        context.navigateTo(getTrasactionEditUrl(tranId))
    }

    def navigateToCopyURL(String tranId) {
        context.navigateTo(getTrasactionCopyUrl(tranId))
    }

    def clickSaveButton() {
        context.webDriver.getHtmlElementByLocator(BTN_SAVE)
        context.waitForPageToLoad()
    }

    def clickItem() {
        HtmlElementHandle item = asElement(XPATH_BTN_ITEM)
        item.click()
    }

    def clickExpense() {
        HtmlElementHandle expense = asElement(XPATH_BTN_EXPENSE)
        expense.click()
    }

    HashSet getCNDropDownListOptions(String field_id) {
        HashSet options = new ArrayList<String>()
        for (String item in context.getCNdropdownOptions(field_id))
            if (item != "") {
                options.add(item)
            }
        return options
    }

    HashSet getDropDownListOptions(String field_id) {
        return getCNDropDownListOptions(field_id)
    }

    HashSet getItemLineCFSOptions(String fieldId) {
        return getItemLineDDLOptions(XPATH_TR_ITEM_HEADER, LABEL_CFS_CN, LABEL_CFS_EN, fieldId)
    }

    HashSet getItemLineCFSOptions2(String fieldId) {
        return getItemLineDDLOptions2(XPATH_TR_ITEM_HEADER, LABEL_CFS_CN, LABEL_CFS_EN, fieldId)
    }

    HashSet getExpenseLineCFSOptions(String fieldId) {
        return getItemLineDDLOptions(XPATH_TR_EXPENSE_HEADER, LABEL_CFS_CN, LABEL_CFS_EN, fieldId)
    }

    HashSet getJournalLineCFSOptions(String fieldId, EditMachineCN sublist) {
        return getJournalLineDDLOptions(XPATH_TR_JOURNAL_HEADER, LABEL_CFS_CN, LABEL_CFS_EN, fieldId, sublist)
    }

    private HashSet getJournalLineDDLOptions(String HeaderXpath, String cnLable, String enLable, String fieldId, EditMachineCN sublist) {

        int cfsCol = getCFSColumn(HeaderXpath)
        cfsCol++
        sublist.clickElementIdentifiedBy(Locator.xpath(XPATH_TR_ACTIVE_LINE + "/td[" + cfsCol + "]"))
        HashSet options = getCNDropDownListOptions(fieldId)
        return options
    }

    private HashSet getItemLineDDLOptions(String HeaderXpath, String cnLable, String enLable, String fieldId) {

        HashSet options = new HashSet()

        //Search Item , get the match index of cnLable or enLable
        int index = -1
        List headers = context.webDriver.getHtmlElementsByLocator(Locator.xpath(HeaderXpath + "/td"))
        for (int i = 0; i < headers.size(); i++) {
            String header_value = headers[i].getAttributeValue("data-label")
            if (header_value == cnLable || header_value == enLable) {
                index = i
                break
            }
        }
        Thread.sleep(3000)

        //td index is based on 1
        index++

        //DropDownList options only show in HTML source after clicking the tr
        String xpath = HeaderXpath + "/following-sibling::tr[1]/td[" + index + "]"
        HtmlElementHandle td = elementHandler.waitForElementToBePresent(context.webDriver, xpath)

        td.click()
        Thread.sleep(2000)
        td.click()
        Thread.sleep(3000)

        options = getCNDropDownListOptions(fieldId)
        return options
    }

    //If two tab in Item line(Item/Expense), if click to the second tab, CFS ddl cannot get
    //Update getItemLineDDLOptions to getItemLineDDLOptions2 for fix this
    private HashSet getItemLineDDLOptions2(String HeaderXpath, String cnLable, String enLable, String fieldId) {

        HashSet options = new HashSet()

        //Search Item , get the match index of cnLable or enLable
        int index = -1
        List headers = context.webDriver.getHtmlElementsByLocator(Locator.xpath(HeaderXpath + "/td"))
        for (int i = 0; i < headers.size(); i++) {
            String header_value = headers[i].getAttributeValue("data-label")
            if (header_value == cnLable || header_value == enLable) {
                index = i
                break
            }
        }
        Thread.sleep(3000)

        //td index is based on 1
        index++

        //DropDownList options only show in HTML source after clicking the tr
        String xpath = HeaderXpath + "/following-sibling::tr[1]/td[" + index + "]"
        HtmlElementHandle td = elementHandler.waitForElementToBePresent(context.webDriver, xpath)

        td.click()
        Thread.sleep(2000)

        context.webDriver.click(Locator.xpath('//*[@id="item_custcol_cseg_cn_cfi_fs"]/div[1]/span'))
        def el = context.webDriver.getHtmlElementsByLocator(Locator.xpath(".//div[@class='dropdownDiv']/div[@style='display: block;']"))

        for (int i = 0; i < el.size(); i++) {
            String str = el.get(i).getAsText()
            if (str != "") {
                options.add(str)
            }
        }
        return options
    }


    int getCFSColumn(String XHeader) {
        int index = -1
        List headers = context.webDriver.getHtmlElementsByLocator(Locator.xpath(XHeader + "/td"))
        for (int i = 0; i < headers.size(); i++) {
            String header_value = headers[i].getAttributeValue("data-label")
            if (header_value == LABEL_CFS_CN || header_value == LABEL_CFS_EN) {
                index = i
                break
            }
        }
        return index
    }

    String getTrasactionEditUrl(String tranId) {
        return url + "?id=" + tranId + "&e=T"
    }

    String getTrasactionCopyUrl(String tranId) {
        return url + "?id=" + tranId + "&e=T&memdoc=0"
    }

    // Set Subsidiary, period
    void setSubsidiary(String subsidiary) {
        if (subsidiary != null && subsidiary != "") {
            context.setFieldWithValue(subsidiaryFieldId, subsidiary)
            context.waitForPageToLoad()
        }
    }

}
