package com.netsuite.chinalocalization.page.Transaction.AR

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator
import com.netsuite.base.lib.alert.AlertHandler
import org.openqa.selenium.Alert

/**
 * @Description Page object for Fulfill page: 'Sales Order' page, click [Approve], then click [Fulfill].
 * @Author lisha.hao@oracle.com
 */
class FulfillPage extends PageBaseAdapterCN{
    private static String TITLE = "FulFill"
    //XPATH
    protected static final String XPATH_SAVE = "//input[@id='btn_secondarymultibutton_submitter']"    // "//*[@id=\"btn_secondarymultibutton_submitter\"]"   //
    protected static final String XPATH_ORDERNUMBER = "/html/body/div[1]/div[2]/div[3]/form/table/tbody/tr[2]/td/table/tbody/tr[1]/td/table/tbody/tr[2]/td[1]/table/tbody/tr[3]/td/div/span[2]/span/a"

    // Fulfill page
    def setFulfill() {
        def orderStatus = ""
        if (context.getUserLanguage().equalsIgnoreCase("en_US")) {
            orderStatus = "Shipped"
        }else {
            orderStatus = "已付运"
        }
        asDropdownList(fieldId: "shipstatus").selectItem(orderStatus)
        // Set 'Location' in line
        asElement("//*[@id=\"inpt_location13\"]").sendKeys("CN_BJ")
    }
    // Get order No.
    def getOrderNum() {
        def beginIndex
        if (context.getUserLanguage().equalsIgnoreCase("en_US")) {
            beginIndex = 13
        }else {
            beginIndex = 6
        }
        return asElement(XPATH_ORDERNUMBER).getAsText().substring(beginIndex)
    }

     //Click [Save] button via UI is not stable.
     def clickSave() {
         asElement(XPATH_SAVE).scrollToView()
         asElement(XPATH_SAVE).click()
         waitForPageToLoad()
         // context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_SAVE)).click()
     }
    // Will popup unknow message window
    def clickSaveviaAPI() {
        context.executeScript("NLInvokeButton(getButton('submitter'));")
        sleep(1000)
        def alterHandler = new AlertHandler()
        Alert alert = alterHandler.waitForAlertToBePresent(context.webDriver, 10)
        if (alert != null) {
            alert.accept()
        }
    }
}