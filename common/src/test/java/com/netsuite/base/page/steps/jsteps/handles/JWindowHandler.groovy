package com.netsuite.base.page.steps.jsteps.handles

import com.netsuite.base.lib.NetSuiteAppBase
import org.apache.commons.lang.StringUtils

class JWindowHandler {
    static void switchWindow(NetSuiteAppBase context ,String windowTitle=null) {
        String currentHandle = context.webDriver.getWindowHandle()
        if(!StringUtils.isEmpty(windowTitle)) {
            context.webDriver.closeWindow(currentHandle)
            context.switchToWindow(windowTitle)
            return
        }
        Set<String> windowHandles = context.webDriver.getWindowHandles()
        for(String handle: windowHandles) {
            if(currentHandle !=handle) {
                context.webDriver.closeWindow(currentHandle)
                context.switchToWindowInstance(handle)
                return
            }
        }
    }
}
