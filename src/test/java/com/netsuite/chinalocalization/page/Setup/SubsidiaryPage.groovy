package com.netsuite.chinalocalization.page.Setup

import com.google.inject.Inject
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.chinalocalization.lib.NRecord

/**
 * @Description Page object for Subsidiary page: 'Setup->Company->Subsidiaries->Subsidiary Page'.
 * @Author lisha.hao@oracle.com
 */
class SubsidiaryPage extends PageBaseAdapterCN{
    @Inject
    protected ElementHandler elementHandler

    private static String TITLE = "Subsidiary"
    private static String URLPREFIX = "/app/common/otherlists/subsidiarytype.nl?id="
    private static String URLPOSTFIX = "&e=T"
    private String subsidiaryId
    private StringBuffer url

    final String SUBSIDIRAY_RECORD_ID = "subsidiary"
    final String FISCALCALENDAR_ID = "fiscalcalendar"

    @Inject
    NRecord nRecord

    // Field ID
    // CHINA CASH FLOW ITEM MANDATORY
    private static final String FIELD_ID_CFSMANDATORY = "custrecord_cn_cfi_mandatory"

    // Xpath
    private static final String XPATH_SAVE ="//input[@id='btn_multibutton_submitter']"
    private static final String XPATH_CANCEL ="//input[@id='_cancel']"

    // China Max VAT Invoice Amount field ID
    String CHINA_MAX_VAT_INVOICE_AMOUNT_FIELD = "custrecord_cn_vat_max_invoice_amount"

    SubsidiaryPage() {
    }

    def navigateToSubsidiaryEditPage(String subsidiaryName) {
        if(!"".equals(subsidiaryName) && subsidiaryName.equals("Parent Company")){
            subsidiaryId = nRecord.getInternalIdByText("subsidiary",subsidiaryName, null)
        } else {
            subsidiaryId = Utility.getSubsidiaryId(context,subsidiaryName,null)
        }
        url = new StringBuffer()
        url.append(URLPREFIX).append(subsidiaryId).append(URLPOSTFIX)
        context.navigateToNoWait(url.toString())
        elementHandler.waitForElementToBePresent(context.webDriver,XPATH_SAVE)
    }

    def navigateToPage(name, isEdit){
        String id=getIdByName(name)
        String redirectScript = "return nlapiResolveURL('RECORD','"+SUBSIDIRAY_RECORD_ID+"',"+id+","+(isEdit?"true":"false")+");"
        String url=context.executeScript(redirectScript)
        context.navigateTo(url)
        context.waitForPageToLoad()
    }

    def getIdByName(name){
        def script = "var x = nlapiSearchRecord('"+SUBSIDIRAY_RECORD_ID+"', null, new nlobjSearchFilter('namenohierarchy', null, 'is', '"+name+"'));" +
                "return((x != null) ? x[0].getId() : null);"
        def id = context.executeScript(script)
        return id
    }

    def getFiscalCalendarTex(){
        return context.getFieldText(FISCALCALENDAR_ID)
    }

    def setFiscalCalendarTex(calendar){
        context.setFieldWithText(FISCALCALENDAR_ID, calendar)
    }

    def isCheckedCFSMandatory() {
        def script = "return nlapiGetFieldValue('"+FIELD_ID_CFSMANDATORY+"')"
        String check=context.executeScript(script)
        if(check=="T"){
            return true
        }else if(check=="F"){
            return false
        }
    }

    def enableCFSMandatory(){
        def script = "return nlapiSetFieldValue('"+FIELD_ID_CFSMANDATORY+"','T')"
        context.executeScript(script)
    }

    def disbleCFSMandatory(){
        def script = "return nlapiSetFieldValue('"+FIELD_ID_CFSMANDATORY+"','F')"
        context.executeScript(script)
    }

    def clickSave(){
        context.clickSaveByAPI()
    }

    def clickCancel() {
        asElement(XPATH_CANCEL).click()
    }

    def setMaxVATInvAmount(amount) {
        context.setFieldWithText(CHINA_MAX_VAT_INVOICE_AMOUNT_FIELD, amount)
    }
    def getMaxVATInvAmount() {
        context.getFieldText(CHINA_MAX_VAT_INVOICE_AMOUNT_FIELD)
    }
}
