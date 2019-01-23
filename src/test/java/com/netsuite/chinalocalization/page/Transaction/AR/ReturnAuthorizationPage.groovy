package com.netsuite.chinalocalization.page
import com.netsuite.chinalocalization.common.CONSTANTS.CURL

class ReturnAuthorizationPage extends TransactionBasePage {

    private static String TITLE="Return Authorization"
    private static String URL=CURL.RETURN_AUTHORISATION_CURL

    String ITEMTAB = "Items"

    public ReturnAuthorizationPage(){
        super(TITLE,URL);
    }

    public createReturnAuthorization(dataObj){
        createTransaction(dataObj)
    }
}
