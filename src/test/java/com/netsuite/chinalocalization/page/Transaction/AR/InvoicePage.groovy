package com.netsuite.chinalocalization.page
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.testautomation.html.Locator
import org.openqa.selenium.JavascriptExecutor

class InvoicePage extends TransactionBasePage {
    private static String TITLE="Invoice Order"
    private static String URL=CURL.INVOICE_CURL

    private static final String XPATH_QUANTITY = "//*[@id=\"quantity_formattedValue\"]"
    private static final String XPATH_SAVE = "//*[@id=\"btn_secondarymultibutton_submitter\"]"

    String ITEMTAB = "Items"
    def ADD

    public InvoicePage(){
        super(TITLE,URL);
    }

    public createInvoice(dataObj){
        createTransaction(dataObj)
    }

    def clickSave() {
        asElement(XPATH_SAVE).click()
        context.waitForPageToLoad();
    }



}
