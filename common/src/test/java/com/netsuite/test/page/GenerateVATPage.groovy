package com.netsuite.test.page

import com.netsuite.base.page.PageBaseAdapter

class GenerateVATPage extends  PageBaseAdapter {


    private static final String  custPageDateFromInput = ".//input[@id='custpage_datefrom']"
    private static final String  custPageDateToInput = ".//input[@id='custpage_dateto']"
    private static final String refresh = "//input[@id='custpage_refresh']"

    //region common methods for pages
    def enterValueInDateFromInGenerateVAT(String fromDate) {
        setFieldValue(custPageDateFromInput,fromDate)
    }

    def enterValueInDateToInGenerateVAT(String toDate) {
        setFieldValue(custPageDateToInput,toDate)
    }


    def clickRefreshButton() {
        asClick(refresh)
        this.waitForPageToLoad()
    }
    //endregion

}
