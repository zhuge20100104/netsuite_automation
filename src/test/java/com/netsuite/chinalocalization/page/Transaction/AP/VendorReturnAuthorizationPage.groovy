package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL

class VendorReturnAuthorizationPage extends TransactionBasePage {

    public static String TITLE="Vendor Return Authorization"
    public static String URL=CURL.VENDOR_RETURN_AUTHORISATION_CURL

    public VendorReturnAuthorizationPage(){
        super(TITLE,URL)
    }

    public fillVendorReturnAuthorization(){
        createDefaultTransaction()
    }
}
