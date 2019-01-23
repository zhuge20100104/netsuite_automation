package com.netsuite.chinalocalization.page
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.common.control.CNDropdownList

/**
 * @author lisha.hao
 */
class CustomerDepositPage extends TransactionBasePage {

    private static String TITLE="Customer Deposit"
    private static String URL=CURL.CUSTOMER_DEPOSIT_CURL

    //FieldID
    private static final String ExchangeRateFieldID = "exchangerate"
    private static final String DateFieldID = "trandate"
    private static final String PaymentAmountID = "payment"
    private static final String MemoFieldID = "memo"
    private static final String CFSItemID = "custbody_cseg_cn_cfi"

    public CustomerDepositPage(){
        super(TITLE,URL);
    }

    public createCustomerDeposit(dataObj){
        createTransaction(dataObj)
    }

    def setPaymentAmount(String amount) {
        context.setFieldWithValue(PaymentAmountID, amount)
    }

    def setExchangeRate(String rate) {
        context.setFieldWithValue(ExchangeRateFieldID, rate)
    }

    def setDate(String date) {
        context.setFieldWithValue(DateFieldID, date)
    }

    def setMemo(String memo) {
        context.setFieldWithValue(MemoFieldID, memo)
    }

    def setCFSItem(String item) {
        context.withinCNDropdownlist(CFSItemID).selectItem(item)
    }

    def clickSave() {
        asElement("//input[@id='btn_multibutton_submitter']").click()
    }
/*    context.withinHtmlElementIdentifiedBy(Locator.id("orderautoenter")).sendKeys(s)
    context.withinHtmlElementIdentifiedBy(Locator.id("secondarysubmitter")).click()*/

}
