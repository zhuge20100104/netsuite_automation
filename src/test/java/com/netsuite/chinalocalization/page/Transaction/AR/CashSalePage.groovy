package com.netsuite.chinalocalization.page
import com.netsuite.chinalocalization.common.CONSTANTS.CURL

class CashSalePage extends TransactionBasePage {

    private static String TITLE="Cash Sale"
    private static String URL=CURL.CASH_SALE_CURL

    String ITEMTAB = "Items"

    public CashSalePage(){
        super(TITLE,URL);
    }

    public createCashSale(dataObj){
        createTransaction(dataObj)
    }
}
