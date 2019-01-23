package com.netsuite.chinalocalization.page.Setup

import com.google.inject.Inject
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator
import org.apache.log4j.Logger

/**
 * @Description Page object for Company Information page: 'Setup->Company->Company Information'.
 * @Author lisha.hao@oracle.com
 */
class CompanyInformationPage extends PageBaseAdapterCN{
    private static final Logger log = Logger.getLogger(CompanyInformationPage.class);
    @Inject
    BaseAppTestSuite baseAppTestSuite;
    @Inject
    protected ElementHandler elementHandler
    private static String TITLE = "Company Information"
    private static String URL= "/app/common/otherlists/company.nl?whence="

    private static final String FIELD_ID_CFSMANDATORY = "custrecord_cn_cfi_mandatory"
    // Xpath
    // CHINA CASH FLOW ITEM MANDATORY
    private static final String XPATH_SAVE ="//input[@id='submitter']"
    private static final String XPATH_CANCEL ="//input[@id='_cancel']"
    private static final String XPATH_CFSMANDATORY = "//*[@id='custrecord_cn_cfi_mandatory_fs_lbl']/a"
    private static final String XPATH_CHECKBOX_CFSMANDATORY = "//input[@id='custrecord_cn_cfi_mandatory_fs_inp']"

    // China Max VAT Invoice Amount field ID
    String CHINA_MAX_VAT_INVOICE_AMOUNT_FIELD = "custrecord_cn_vat_max_invoice_amount"

    CompanyInformationPage() {
    }

    def navigateToCompanyInfoPage() {
        context.navigateToNoWait(URL.toString())
        elementHandler.waitForElementToBePresent(context.webDriver,XPATH_CFSMANDATORY)
    }

    def enableSICFSMandatory_ori() {
        try{
            context.checkNlsinglecheckbox(Locator.xpath(XPATH_CHECKBOX_CFSMANDATORY))
        }catch(Exception e){
            log.error("There is no CFS Mandatory Checkbox!")
        }
    }

    def disbleSICFSMandatory_ori(){
        try{
            context.unCheckNlsinglecheckbox(Locator.xpath(XPATH_CHECKBOX_CFSMANDATORY))
        }catch(Exception e) {
            log.error("There is no CFS Mandatory Checkbox!")
        }
    }

    def isCheckedSICFSMandatory_ori() {
        try{
            return context.isFieldChecked(Locator.xpath(XPATH_CHECKBOX_CFSMANDATORY))
        }catch(Exception e){
            log.error("There is no CFS Mandatory Checkbox!")
        }
    }

    boolean isCheckedSICFSMandatory() {
        def script = "return nlapiGetFieldValue('"+FIELD_ID_CFSMANDATORY+"')"
        String check=context.executeScript(script)
        if(check=="T"){
            return true
        }else if(check=="F"){
            return false
        }
    }

    def enableSICFSMandatory() {
        def script = "return nlapiSetFieldValue('"+FIELD_ID_CFSMANDATORY+"','T')"
        context.executeScript(script)
    }

    def disbleSICFSMandatory(){
        def script = "return nlapiSetFieldValue('"+FIELD_ID_CFSMANDATORY+"','F')"
        context.executeScript(script)
    }


    boolean isExistCFSMandatory() {
        def bool = true;
        def s = "";
        try{
            s = context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_CFSMANDATORY)).getAsText()
            if(s.equalsIgnoreCase("CHINA CASH FLOW ITEM MANDATORY") || s.equalsIgnoreCase("中国现金流量表项必填")) {
                bool = true;
            }
        }catch(Exception e){
            bool =  false;
        }
        return bool;
    }

    def clickSISave(){
        asElement(XPATH_SAVE).click()
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