package com.netsuite.chinalocalization.page.Setup

import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator

import javax.inject.Inject

class GeneralPreferencePage extends PageBaseAdapterCN {

    @Inject
    ElementHandler elementHandler

    private static String URL = "/app/setup/general.nl?";

    def navigateToURL() {
        context.navigateToNoWait(URL)
        elementHandler.waitForElementToBePresent(context.webDriver,"//*[@id=submitter]")
    }

    def getMaxDropDownSize() {
        return context.getFieldValue("MAXDROPDOWNSIZE");
    }

    def setMaxDropDownSize(int size, boolean saveRecord) {
        context.setFieldWithValue("MAXDROPDOWNSIZE", size as String);
        if (saveRecord) {
            context.clickSaveByAPI()
        }
    }

    def setShowInternalId(boolean onOff, boolean save) {
        String value = onOff ? "T" : "F"
        context.setFieldWithValue("EXPOSEIDS", value);
        if (save) {
            context.clickSaveByAPI()
        }
    }

    def clickSave() {
        context.webDriver.getHtmlElementByLocator(Locator.xpath("/*//*[@id='submitter']")).scrollToView();
        context.webDriver.click(Locator.xpath("/*//*[@id='submitter']"));
        context.webDriver.waitForPageToLoad();
    }
}
