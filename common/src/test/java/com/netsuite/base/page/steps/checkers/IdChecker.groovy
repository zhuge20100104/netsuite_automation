package com.netsuite.base.page.steps.checkers

import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.Locator

class IdChecker implements IChecker {
    @Override
    void doChecker(CheckerData data) {
        WebDriver driver = data.context.webDriver
        String id = data.key

        boolean isChecker = driver.isElementPresent(Locator.id(id))

        if(isChecker) {
            data.isChecked = true
            data.selType = SelType.ID
        }
    }
}
