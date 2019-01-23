package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.TransactionBasePage

class CheckPage extends TransactionBasePage {

    public static String TITLE="Check";
    public static String URL=CURL.CHECK_CURL

    public CheckPage(){
        super(TITLE,URL);
    }

    public fillCheck(){
        createDefaultTransaction()
    }
}
