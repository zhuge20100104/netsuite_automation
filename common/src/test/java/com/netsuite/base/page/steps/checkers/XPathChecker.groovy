package com.netsuite.base.page.steps.checkers

import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.Locator

class XPathChecker implements IChecker {
    @Override
    void doChecker(CheckerData data) {
        WebDriver driver = data.context.webDriver
        String xpath = data.key
        String value = data.value

        if(xpath.equals("//_row")) {
            data.isChecked = true
            data.selType = SelType.XPATH_ROW
            return
        }

        if(xpath.equals("//_row_edit")) {
            data.isChecked = true
            data.selType = SelType.XPATH_ROW_EDIT
            return
        }


        if(xpath.equals("//_column_edit")) {
            data.isChecked = true
            data.selType = SelType.XPATH_COLUMN_EDIT
            return
        }


        if(xpath.equals("//_row_select")) {
            data.isChecked = true
            data.selType = SelType.XPATH_ROW_SELECT
            return
        }


        if(xpath.equals("//_rows")) {
            data.isChecked = true
            data.selType = SelType.XPATH_ROWS
            return
        }


        boolean isChecker =  driver.isElementPresent(Locator.xpath(xpath))

        if(isChecker) {
            data.isChecked = true
            data.selType = SelType.XPATH
            return
        }else if(!isChecker && value.equals("IF")) {
            data.isChecked = true
            data.selType = SelType.XPATH
            return
        }else if(xpath.contains("subsidiary")){
            if(data.context.isSingleInstance()){
                data.isChecked = true
                data.selType = SelType.UNKNOWN
            }
        }
    }
}
