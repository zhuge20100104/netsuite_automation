package com.netsuite.base.page

import com.netsuite.base.control.ext.DropdownListExt
import com.netsuite.testautomation.driver.LocatorType
import com.netsuite.testautomation.html.Locator
import net.qaautomation.exceptions.SystemException

class PageBaseAdapter extends PageBase{


    //region WebDriver related methods
    def getWebDriver() {
        return context.webDriver
    }
    //endregion

    //region click element part
    def asDropdownList(options) {
        if (options.fieldId) {
            return getContext().withinDropdownlist(options.fieldId)
        } else if (options.locator) {
            return new DropdownListExt(getContext(), getContext().webDriver, Locator.xpath(options.locator))
        }
    }




    def clickFormTab(String tabName) {
        context.clickFormTab(tabName)
    }

    def setFieldValue(LocatorType type, String fieldName, String value) {
        def locator;
        switch (type) {
            case LocatorType.XPATH:
                locator = Locator.xpath(fieldName)
                break
            case LocatorType.CLASS:
                locator = Locator.className(fieldName)
                break
            case LocatorType.ID:
                locator = Locator.id(fieldName)
                break
            case LocatorType.CSS:
                locator = Locator.css(fieldName)
                break
            default:
                throw new SystemException("Locator type not implemented!")
                break
        }
        context.setFieldIdentifiedByWithValue(locator,value)
    }

    def setFieldValue(String fieldName,String value) {
        asElement(fieldName).clear()
        setFieldValue(LocatorType.XPATH,fieldName,value)
    }


    def getFieldTextByFieldId(String fieldId) {
        context.getFieldText(fieldId)
    }


    def getFieldText(String xpath) {
        asElement(xpath).getAttributeValue("value")
    }


    def isElementVisible(locator) {

    }


    def isTextVisible(String text) {
        return context.isTextVisible(text)
    }

    def selectDropdownItem(locator,item) {
        asDropdownList(locator: locator).selectItem(item)
    }


    def selectDropdownItemByFieldId(locator,item) {
        asDropdownList(fieldId: locator).selectItem(item)
    }

    def getDropdownItem(locator) {
        asDropdownList(locator: locator).getValue()
    }


    def getDropdownItems(locator) {
        asDropdownList(locator: locator).getOptions()
    }

    def executeScript(String jsScript) {
        context.webDriver.executeScript(jsScript)
    }

    def getJsonFromString(String jsonStr) {
        return context.asJSON(text : jsonStr)
    }


    def getPageUrl() {
        return getWebDriver().getPageUrl()
    }

    //endregion


}
