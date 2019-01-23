package com.netsuite.base.page.steps.jsteps

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.jsteps.handles.JElementHandler
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator

class IdStep implements ISteps {
    @Override
    void doStep(CheckerData checkerData) {

        SelType selType = checkerData.selType
        NetSuiteAppBase context = checkerData.context

        if(selType.equals(SelType.ID)) {
            String key = checkerData.key
            WebDriver driver = context.webDriver

            HtmlElementHandle handle = driver.getHtmlElementByLocator(Locator.id(key))

            def value = checkerData.value

            //handle this element
            JElementHandler.handleElement(context,handle,key,value)
            checkerData.isStepHandled = true
        }

    }
}
