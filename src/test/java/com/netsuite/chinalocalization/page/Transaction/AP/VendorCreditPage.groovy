package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL

class VendorCreditPage extends TransactionBasePage {

    public static String TITLE="Vendor Credit";
    public static String URL=CURL.VENDOR_CREDIT_CURL

    public VendorCreditPage(){
        super(TITLE,URL);
    }

    public fillVendorCredit(){
        createDefaultTransaction()
    }
}
