package com.netsuite.base.page.steps.jsteps

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.expect.handles.JXpathTableExpectHandler
import com.netsuite.base.page.steps.jsteps.handles.JElementHandler
import com.netsuite.base.page.steps.jsteps.handles.JXpathTableHandler
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import org.apache.log4j.Logger

class XPathStep implements ISteps {

    private static final Logger log = Logger.getLogger(XPathStep.class)

    @Override
    void doStep(CheckerData checkerData) {
        SelType selType = checkerData.selType
        NetSuiteAppBase context = checkerData.context


        if(selType.equals(SelType.XPATH_ROW_EDIT)) {
            String key = checkerData.key
            def value = checkerData.value

            log.info("Start to execute: //_row_edit : params: ["+value+"]")

            JXpathTableHandler.handleRowEdit(context, key, value)
            checkerData.isStepHandled = true
            return
        }


        if(selType.equals(SelType.XPATH_COLUMN_EDIT)) {
            String key = checkerData.key
            def value = checkerData.value

            log.info("Start to execute: //_column_edit : params: ["+value+"]")

            JXpathTableHandler.handleColumnEdit(context, key, value)
            checkerData.isStepHandled = true
            return
        }


        if(selType.equals(SelType.XPATH_ROW_SELECT)) {
            String key = checkerData.key
            def value = checkerData.value

            log.info("Start to execute: //_row_select : params: ["+value+"]")

            JXpathTableHandler.handleRowSelect(context, key, value)
            checkerData.isStepHandled = true
            return
        }


        if(selType.equals(SelType.XPATH)) {
            String key = checkerData.key
            WebDriver driver = context.webDriver

            HtmlElementHandle handle = driver.getHtmlElementByLocator(Locator.xpath(key))

            if(handle!=null && handle.exists()) {
                def value = checkerData.value

                log.info("Start to execute: xpath : params: ["+key+"---->"+value+"]")

                //handle this element
                JElementHandler.handleElement(context, handle, key, value)
            }

            checkerData.isStepHandled = true

        }
    }
}
