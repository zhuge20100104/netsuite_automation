package com.netsuite.base.page.steps.expect.handles

import com.netsuite.base.lib.NetSuiteAppBase
import org.junit.Assert

class MenuExpectHandler {

    static void handleMenusExp(NetSuiteAppBase context, String mainKey, def mainData) {
        mainData.each{
            menuStr ->
                Assert.assertTrue("Menu: ["+ menuStr +"] does not exist", context.doesActionsMenuExist(menuStr))
        }
    }
}
