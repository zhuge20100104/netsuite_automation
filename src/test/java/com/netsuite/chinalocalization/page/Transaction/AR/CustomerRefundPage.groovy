package com.netsuite.chinalocalization.page
import com.netsuite.chinalocalization.common.CONSTANTS.CURL

class CustomerRefundPage extends TransactionBasePage {

    private static String TITLE="Customer Refund"
    private static String URL=CURL.CUSTOMER_REFUND_CURL

    public CustomerRefundPage(){
        super(TITLE,URL);
    }

    public createCustomerRefund(dataObj){
        createTransaction(dataObj)
    }

    //Customer refund page need wait for apply control in the bottom, so override navigateToURL()
    def navigateToURL() {
        context.navigateTo(URL)
    }

}
