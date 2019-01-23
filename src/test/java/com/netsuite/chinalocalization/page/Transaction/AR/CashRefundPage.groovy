package com.netsuite.chinalocalization.page
import com.netsuite.chinalocalization.common.CONSTANTS.CURL

class CashRefundPage extends TransactionBasePage {

    private static String TITLE="Cash Refund"
    private static String URL=CURL.CASH_REFUND_CURL

    String ITEMTAB = "Items"

    public CashRefundPage(){
        super(TITLE,URL);
    }

    public createCashRefund(dataObj){
        createTransaction(dataObj)
    }
}
