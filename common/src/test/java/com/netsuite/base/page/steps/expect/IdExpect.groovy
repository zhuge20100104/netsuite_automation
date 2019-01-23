package com.netsuite.base.page.steps.expect

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.expect.handles.JElementExpectHandler
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator

class IdExpect implements IExpect{
    @Override
    void doExpect(CheckerData checkerData) {
        SelType selType = checkerData.selType
        NetSuiteAppBase context = checkerData.context

        if(selType.equals(SelType.ID)) {
            String key = checkerData.key
            WebDriver driver = context.webDriver

            HtmlElementHandle handle = driver.getHtmlElementByLocator(Locator.id(key))

            def value = checkerData.value
            //handle this element
            JElementExpectHandler.handleSeleniumElementExp(context,handle,key,value)
            checkerData.isExpected = true
        }
    }
}
