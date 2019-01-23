package com.netsuite.common.utilities

import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator

import java.util.concurrent.TimeUnit

/**
 * Created by stepzhou on 5/31/2018.
 */
class Utility {
    private static final MAX_RETRY_COUNT = 60

    static boolean isElementReady(Locator locator, WebDriver driver) {
        List<HtmlElementHandle> tarEles = driver.getHtmlElementsByLocator(locator)
        int timeCount = 0

        while (tarEles.size() == 0 || !isPageReady(driver)) {
            tarEles = driver.getHtmlElementsByLocator(locator)

            TimeUnit.SECONDS.sleep(1)
            timeCount++

            if (timeCount > MAX_RETRY_COUNT)
                throw new RuntimeException("The element is not available.")
        }

        return true
    }

    static boolean isElementReady(Locator locator, WebDriver driver, int seconds) {
        List<HtmlElementHandle> tarEles = driver.getHtmlElementsByLocator(locator)
        int timeCount = 0

        while (tarEles.size() == 0 || !isPageReady(driver)) {
            tarEles = driver.getHtmlElementsByLocator(locator)

            TimeUnit.SECONDS.sleep(1)
            timeCount++

            if (timeCount > seconds)
                throw new RuntimeException("The element is not available.")
        }

        return true
    }

    static boolean isPageReady(WebDriver driver) {
        return driver.executeScript("return document.readyState").equalsIgnoreCase("complete")
    }
}
