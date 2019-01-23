package com.netsuite.base.page.utility

import com.netsuite.base.lib.wait.ICondition
import com.netsuite.base.lib.wait.Result
import com.netsuite.base.lib.wait.Waiter
import com.netsuite.testautomation.driver.WebDriver
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriverException

class PageLoadWait {
    static final int MAX_RETRY_COUNT = 120

    static boolean waitForPageReady(WebDriver driver) {
        return waitForPageReady(driver,MAX_RETRY_COUNT)
    }


    static boolean waitForPageReady(WebDriver driver,int tryCount) {
        Waiter waiter = new Waiter()

        Result result = waiter.waitForCondition(new ICondition<Boolean>() {
            @Override
            boolean check(Result<Boolean> r) {
                if(isPageReady(driver)) {
                    r.setCode(true)
                    return true
                }
                r.setCode(false)
                return false
            }
        },500, tryCount)

        return result.getCode()
    }


    // To see if jquery is loaded
    static Boolean jQueryLoaded(WebDriver driver) {
        Boolean loaded
        try{
            loaded = Boolean.parseBoolean(driver.executeScript("return "+ "jQuery()!=null"))
        } catch(WebDriverException e) {
            loaded = false
        }
        return loaded
    }


    static boolean  isDocumentReady(WebDriver driver) {
        return  driver.executeScript("return document.readyState;").equalsIgnoreCase("complete")
    }


    static boolean isAjaxReady(WebDriver driver) {
        try{
            String readyState =  driver.executeScript("return "+ "jQuery.active;").toString();
            return readyState.equals("0");
        } catch(WebDriverException e) {
            return false;
        }
    }

    static boolean isPageReady(WebDriver driver) {
        if(!jQueryLoaded(driver)) {
            return isDocumentReady(driver)
        }else {
            return isDocumentReady(driver) && isAjaxReady(driver)
        }
    }
}
