package com.netsuite.chinalocalization.lib

import com.netsuite.testautomation.aut.NetSuiteApp
import com.netsuite.testautomation.aut.pageobjects.EditMachine
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.junit.reporting.ReportMe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class EditMachineCN extends EditMachine {
    private String type
    private static final Logger log = LoggerFactory.getLogger(EditMachineCN.class)

    EditMachineCN(NetSuiteApp netSuiteApp, WebDriver driver, String type) {
        super(netSuiteApp, driver, type)
        this.type = type
    }

    @ReportMe
    void setFieldWithText(String fieldId, String text) {
        Method method = null
        try {
            method = getClass().getSuperclass().getDeclaredMethod("selectLineItem")
            method.setAccessible(true)
            method.invoke(this)
        } catch (NoSuchMethodException e) {
            e.printStackTrace()
        } catch (IllegalAccessException e) {
            e.printStackTrace()
        } catch (InvocationTargetException e) {
            e.printStackTrace()
        }
        try {
            Thread.sleep(3000)
        } catch (InterruptedException e) {
            e.printStackTrace()
        }

//        this.webDriver.waitForPageToLoad()
        this.webDriver.executeScript("nlapiSetCurrentLineItemText('" + this.type + "', '" + fieldId + "', '" + text + "', true, true)")

        log.info("Set field '" + fieldId + "' with text '" + text + "'")
    }

    @ReportMe
    void setFieldWithValue(String fieldId, String text) {
        Method method = null
        try {
            method = getClass().getSuperclass().getDeclaredMethod("selectLineItem")
            method.setAccessible(true)
            method.invoke(this)
        } catch (NoSuchMethodException e) {
            e.printStackTrace()
        } catch (IllegalAccessException e) {
            e.printStackTrace()
        } catch (InvocationTargetException e) {
            e.printStackTrace()
        }
        try {
            Thread.sleep(3000)
        } catch (InterruptedException e) {
            e.printStackTrace()
        }

//        this.webDriver.waitForPageToLoad()
        this.webDriver.executeScript("nlapiSetCurrentLineItemValue('" + this.type + "', '" + fieldId + "', '" + text + "', true, true)")

        log.info("Set field '" + fieldId + "' with text '" + text + "'")
    }

}
