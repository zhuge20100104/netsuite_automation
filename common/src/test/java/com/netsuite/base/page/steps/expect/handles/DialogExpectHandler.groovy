package com.netsuite.base.page.steps.expect.handles

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.base.page.utility.PageLoadWait
import net.qaautomation.exceptions.SystemException
import org.junit.Assert
import org.openqa.selenium.Alert

class DialogExpectHandler {

    static void waitForAlert(NetSuiteAppBase context,int retryCount = 8) {
        AlertHandler alertHandler = new AlertHandler()
        Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver, retryCount)
        if(alert!=null) {
            alert.accept()
        }
    }


    static void handleAlert(NetSuiteAppBase context) {
        context.webDriver.reloadBrowser()
        PageLoadWait.waitForPageReady(context.webDriver)
        AlertHandler alertHandler = new AlertHandler()
        Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver, 8)

        if(alert != null) {
            alert.accept()
        }

        PageLoadWait.waitForPageReady(context.webDriver)
    }


    static void handleDialogExp(NetSuiteAppBase context, String mainKey, def mainData) {
        mainData.each{
            dialogData ->
                if(dialogData.type.equals("alert")) {
                    waitForAlert(context)
                    Assert.assertTrue("Alert message: [" + dialogData.message + "] does not exist!!",context.doesTextExist(dialogData.message))
                }else if(dialogData.type.equals("common")) {
                    Assert.assertTrue("Common message: [" + dialogData.message + "] does not exist!!",context.isTextVisible(dialogData.message))
                }else if(dialogData.type.equals("reload")) {
                    println("Start to handle reload alert!!")
                    handleAlert(context)
                    println("End handle reload alert!!")
                }else {
                    throw new SystemException("Not supported dialog type exception, Please check your dialog type and try again!!")
                }
        }
    }
}
