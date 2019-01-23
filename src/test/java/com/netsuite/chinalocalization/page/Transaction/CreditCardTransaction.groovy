package com.netsuite.chinalocalization.page.Transaction

import com.netsuite.chinalocalization.page.TransactionBasePage
import com.netsuite.testautomation.html.Locator

/**
 * Description:
 * Page Object for Credit Card Transaction Page.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 *
 * @author qunxing.liu
 * @version 2018/4/17
 * @since 2018/4/17
 */
class CreditCardTransaction extends TransactionBasePage{

    String addBtn = ".//*[@id='expense_addedit']"
    String saveBtn = ".//*[@id='btn_multibutton_submitter']"
    def cont
    def cRecord

    CreditCardTransaction(String title, String url, Map attrs = null) {
        super(title, url)
        // ["context":context,"currentRecord":currentRecord]
        this.cont = attrs.context
        this.cRecord = attrs.currentRecord
    }

    def setHeaderInfo(Map info){
        info.each { k, v ->
            if (getHeadFieldType(k) == "select"){
                cRecord.setText([(k): v])
                //cont.setFieldWithText(k,v)

            } else {
                cRecord.setValue([(k): v])
                //cont.setFieldWithValue(k,v)
            }
            Thread.sleep(5000)
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
        asClick(addBtn)
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
}

