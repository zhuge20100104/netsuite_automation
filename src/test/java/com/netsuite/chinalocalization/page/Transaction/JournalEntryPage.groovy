package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.testautomation.aut.pageobjects.NetSuiteDataTable
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator

class JournalEntryPage extends com.netsuite.chinalocalization.page.TransactionBasePage {
    private static final String XPATH_SUBMIT = "//*[@id=\"submitter\"]"
    private static final String XPATH_LINE_MACHINE = "//*[@id=\"line_splits\"]"
    def final XPATH_SUBSIDIARY = "//*[contains(@id,'inpt_subsidiary')]/div[2]/div/div/button"
    private static final String XPATH_ERR = ".//*[@id='ext-gen5']/div/span";

    private static String TITLE="Journal Entry"
    private static String URL=CURL.JOURNAL_ENTRY_CURL

    def final Line = "//*[contains(@id,'line_row_')]"

    private static final String ACCOUNT = "//*[@id=\"line_account_fs\"]" //inpt_account6
    private static final String DEBIT = "//*[@id=\"line_debit_fs\"]/input"
    private static final String CREDIT = "//*[@id=\"line_credit_fs\"]/input"
    private static final String CN_CASHFLOW_ITEM = "//*[@id=\"line_custcol_cseg_cn_cfi_fs\"]"

    def final ADD_BTN = "//*[@id=\"line_addedit\"]"
    def final SAVE_BTN = "//*[@id=\"spn_secondarymultibutton_submitter\"]"
    private static final String XPATH_OK_BTN = "//button[@value='true']";

    public fillJournal(){
        context.setFieldWithText("subsidiary", "Parent Company : China BU");
    }

    def createVoidJournalEntry(def uiObj) {
        uiObj.main.each {
            if(it.key == "trandate"){
                context.setFieldWithValue(it.key, it.value)
            }

            if(it.key == "postingperiod"){
                context.setFieldWithText(it.key, it.value)
            }
        }
        context.clickSaveByAPI()
        //last step
        //Get journal id
        String internlId = context.getRecordIdbyAPI()
        return [internalid: internlId, trantype: "journalentry"];
    }

    def getLineDataTable(def autCN){
        HtmlElementHandle table = autCN.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_LINE_MACHINE));
        HtmlElementHandle headerContainer = autCN.webDriver.getHtmlElementByLocator(Locator.xpath(String.format("(%s)//td[contains(@class,'listheader')]/..", table.getXPath())));

        return new NetSuiteDataTable(autCN, autCN.webDriver, headerContainer, table, table.getLocator());
    }

    // added by base transaction
    public JournalEntryPage(){
        super(TITLE,URL);
    }

    public createJournalEntry(dataObj){
        createTransaction(dataObj)
    }

    def addLine(data){
        EditMachineCN sublist = context.withinEditMachine("line")

        data.line.each{eachline->
            def linenum = checkLineNum()
            sublist.setCurrentLine(linenum+1)

            sublist.setFieldWithText("account", eachline.account)
            if (eachline.debit != null) {
                sublist.setFieldWithValue("debit", eachline.debit)
            }
            if (eachline.credit != null) {
                sublist.setFieldWithValue("credit", eachline.credit)
            }
            if (eachline.custcol_cseg_cn_cfi != null) {
                sublist.setFieldWithText("custcol_cseg_cn_cfi", eachline.custcol_cseg_cn_cfi)
            }
            // todo what is the credit and cn..items' internal_id
            sublist.add()
        }
        asClick(SAVE_BTN)
    }

    def editLine(int lineNum, String fieldText) {
        EditMachineCN sublist = context.withinEditMachine("line")
        sublist.setCurrentLine(lineNum)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", fieldText)
    }

    def addJournal(data){
        //add main
        def mainObj = data.main
        def subsidiary = mainObj.subsidiary
        def date = mainObj.trandate
        def approvalStatus = mainObj.approvalstatus
        println("approvalStatus:" + approvalStatus)

        String subId
        if(context.isOneWorld()) {
            subId = Utility.getSubsidiaryId(context, subsidiary, null) //todo change subsidiary
        }
        Thread.sleep(2000)
        setSubsidiary(subId)
        Thread.sleep(2000)

        if (date != null && date != "") {
            context.setFieldWithValue(FIELD_ID_DATE, date)
            context.waitForPageToLoad()
            Thread.sleep(2000)
        }

        if (approvalStatus != null && approvalStatus != "") {
            context.setFieldWithValue(FIELD_ID_APPROVALSTATUS, approvalStatus)
            context.waitForPageToLoad()
            Thread.sleep(2000)
        }

        addLine(data)
        Thread.sleep(2000)

        context.clickSaveByAPI()
        //Get journal id
        try{
            def internlId = context.getRecordIdbyAPI()
            return [internalid: internlId, trantype: "journalentry"]

        } catch (Exception e) {
            return ""
        }
    }

    def addSIJournal(data){
        //add main
        def mainObj = data.main
        waitForPageToLoad()
        addLine(data)
        waitForPageToLoad()

        //Get journal id
        try{
            def internlId = context.getParameterValueForFromQueryString("id")
            return [internalid: internlId, trantype: "journalentry"]
        } catch (Exception e) {
            return ""
        }
    }

    def checkLineNum(){
        if (!context.isElementVisible(Locator.xpath(Line))){
            return 0
        } else {
            return context.webDriver.getHtmlElementsByLocator(Locator.xpath(Line)).size()
        }
    }

    String getErrorMessage() {
        return (context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_ERR))).getAsText();
    }

    def clickOKButton() {
        context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_OK_BTN)).click();
    }

    def clickSaveButton() {
        asClick(SAVE_BTN)
    }

    def getJournalId() {
        //Get journal id
        try{
            //def internlId = context.getParameterValueForFromQueryString("id")
            def internlId = context.getRecordIdbyAPI()
            return [internalid: internlId, trantype: "journalentry"]

        } catch (Exception e) {
            return ""
        }
    }
}