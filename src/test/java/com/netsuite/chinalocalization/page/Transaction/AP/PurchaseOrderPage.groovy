package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.testautomation.html.Locator


class PurchaseOrderPage extends TransactionBasePage {

    public static String TITLE="Purchase Order";
    public static String URL=CURL.PURCHASE_ORDER_CRUL

    String addItemBtn = ".//*[@id='item_addedit']"
    String saveBtn = ".//*[@id='btn_multibutton_submitter']"
    String receiveBtn = ".//*[@id='receive']"
    def cont
    def cRecord

    String ITEMTAB = "Items"

    public PurchaseOrderPage(){
        super(TITLE,URL);
    }

    PurchaseOrderPage(Map attrs) {
        super(TITLE,URL)
        this.cont = attrs.context
        this.cRecord = attrs.currentRecord
    }

    public fillPurchaseOrder(){
        createDefaultTransaction()
    }

    def setHeaderInfo(Map info){
        info.each { k, v ->
            if (getHeadFieldType(k) == "select"){
                cRecord.setText([(k): v])
            } else {
                cRecord.setValue([(k): v])
            }
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
        }
        asClick(addItemBtn)
    }

    String getHeadFieldType(String fieldId){
        return cont.executeScript("return (nlapiGetField('" + fieldId + "').getType());")
    }

    /***
     * get Item Field Type By subsidary/field Id
     * @param subsidaryId
     * @param fieldId
     * @return Item Field Type
     */
    String getItemFieldType(String subsidaryId , String fieldId){
        return cont.executeScript("return (nlapiGetLineItemField('" + subsidaryId +"','" +fieldId+"',0).getType());")
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

    def clickReceiveButton(){
        cont.waitForElementIdentifiedBy(Locator.xpath(receiveBtn))
        asClick(receiveBtn)
    }
}
