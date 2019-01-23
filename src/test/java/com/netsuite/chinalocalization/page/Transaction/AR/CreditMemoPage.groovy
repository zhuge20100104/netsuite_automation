package com.netsuite.chinalocalization.page
import com.netsuite.chinalocalization.common.CONSTANTS.CURL

class CreditMemoPage extends TransactionBasePage {

    private static String TITLE="Credit Memo"
    private static String URL=CURL.CREDIT_MEMO_CURL

    public CreditMemoPage(){
        super(TITLE,URL);
    }

    public createCreditMemo(dataObj){
        createTransaction(dataObj)
    }
}
