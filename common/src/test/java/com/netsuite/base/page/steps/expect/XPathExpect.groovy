package com.netsuite.base.page.steps.expect

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.expect.handles.JElementExpectHandler
import com.netsuite.base.page.steps.expect.handles.JXpathTableExpectHandler
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import org.apache.log4j.Logger

class XPathExpect implements IExpect {
    private static final Logger log = Logger.getLogger(XPathExpect.class)

    @Override
    void doExpect(CheckerData checkerData) {

        SelType selType = checkerData.selType
        NetSuiteAppBase context = checkerData.context

        if(selType.equals(SelType.XPATH_ROW)) {
            String key = checkerData.key
            def value = checkerData.value

            log.info("Start to execute: //_row : params: ["+value+"]")

            JXpathTableExpectHandler.handleTableExp(context,key,value)
            checkerData.isExpected = true
            return
        }



        if(selType.equals(SelType.XPATH_ROWS)) {
            String key = checkerData.key
            def value = checkerData.value

            log.info("Start to execute: //_rows : params: ["+value+"]")

            JXpathTableExpectHandler.handleTableRowsExp(context,key,value)
            checkerData.isExpected = true
            return
        }

        if(selType.equals(SelType.XPATH)) {
            String key = checkerData.key
            WebDriver driver = context.webDriver

            HtmlElementHandle handle = driver.getHtmlElementByLocator(Locator.xpath(key))

            def value = checkerData.value

            log.info("Start to execute: xpathExpect : params: ["+key+"----->"+value+"]")

            //handle this element
            JElementExpectHandler.handleSeleniumElementExp(context,handle,key,value)
            checkerData.isExpected = true
        }
    }
}
