package com.netsuite.base.page.steps.checkers

import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.Locator

class ClassNameChecker implements IChecker {
    @Override
    void doChecker(CheckerData data) {
        WebDriver driver = data.context.webDriver
        String className = data.key


        boolean isChecker =  driver.isElementPresent(Locator.className(className))
        if(isChecker) {
            data.isChecked = true
            data.selType = SelType.CLASS
        }
    }
}
