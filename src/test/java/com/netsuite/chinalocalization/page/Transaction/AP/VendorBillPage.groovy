package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.testautomation.html.Locator

class VendorBillPage extends TransactionBasePage {

    public static String TITLE="Vendor Bill";
    public static String URL=CURL.VENDOR_BILL_CURL

    def cont
    def cRecord

    String saveBtn = ".//*[@id='btn_multibutton_submitter']"

    public VendorBillPage(){
        super(TITLE,URL);
    }

    VendorBillPage(Map attrs) {
        super(TITLE,URL)
        this.cont = attrs.context
        this.cRecord = attrs.currentRecord
    }

    public fillVendorBill(){
        createDefaultTransaction()
    }

    def asClick(expression) {
        asElement(expression).click()
    }

    def asElement(expression) {
        return cont.withinHtmlElementIdentifiedBy(Locator.xpath(expression))
    }

    def clickSaveButton(){
        asClick(saveBtn)
    }

    def setHeaderInfo(Map info){
        info.each { k, v ->
            if (getHeadFieldType(k) == "select"){
                cRecord.setText([(k): v])
            } else {
                cRecord.setValue([(k): v])
            }
            Thread.sleep(3000)
        }
    }

    def setSubListInfo(sublistId, Map info){
        int line = 0
        info.each { fieldId, value ->
            if (getItemFieldType(sublistId, fieldId) == "select") {
                cRecord.setSublistText(sublistId, [(fieldId): value], line + 1)
            } else {
                cRecord.setSublistValue(sublistId, [(fieldId): value], line + 1)
            }
            cRecord.commitLine(sublistId)
        }
    }

    String getHeadFieldType(String fieldId){
        return cont.executeScript("return (nlapiGetField('" + fieldId + "').getType());")
    }

    /***
     * get Item Field Type By sublistId/field Id
     * @param sublistId
     * @param fieldId
     * @return Item Field Type
     */
    String getItemFieldType(String sublistId , String fieldId){
        return cont.executeScript("return (nlapiGetLineItemField('" + sublistId +"','" +fieldId+"',0).getType());")
    }
}
