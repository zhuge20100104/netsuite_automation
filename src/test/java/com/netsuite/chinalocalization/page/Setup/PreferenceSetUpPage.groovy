package com.netsuite.chinalocalization.page.Setup

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN

class PreferenceSetUpPage extends PageBaseAdapterCN {

//    private static String URL = "/app/site/hosting/scriptlet.nl"

    private static final String XPATH_CANCEL = "//input[@id='custpage_cancel_button']"
    private static final String XPATH_SEC_CANCEL = "//input[@id='secondarycustpage_cancel_button']"
    private static final String XPATH_SAVE = "//input[@id='submitter']"
    private static final String XPATH_SEC_SAVE = "//input[@id='secondarysubmitter']"
//    private static final String XPATH_PAGE_TITLE = "//input[@class='uir-page-title-firstline']"

    def clickSave(){
        asElement(XPATH_SAVE).click()
    }

    def clickSecSave(){
        asElement(XPATH_SEC_SAVE).click()
    }

    def clickCancel(){
        asElement(XPATH_CANCEL).click()
    }

    def clickSecCancel(){
        asElement(XPATH_SEC_CANCEL).click()
    }

//    def navigateToUrl(){
//        context.navigateTo(URL)
//    }

    def getCashFlowStatement(){
        context.getFieldValue("custrecord_cn_pref_cfs")
    }

    def setCashFlowStatement(String flag, boolean saveRecord = false){
        if (flag != getCashFlowStatement()){
            context.setFieldWithValue("custrecord_cn_pref_cfs", flag)
            if (saveRecord) {
                clickSave()
            }
        }
    }

    def getVatIntegration(){
        context.getFieldValue("custrecord_cn_pref_vat")
    }

    def SetVatIntegration(String flag, boolean saveRecord = false){
        if (flag != getVatIntegration()){
            context.setFieldWithValue("custrecord_cn_pref_vat", flag)
            if (saveRecord) {
                clickSecSave()
            }
        }
    }


}
