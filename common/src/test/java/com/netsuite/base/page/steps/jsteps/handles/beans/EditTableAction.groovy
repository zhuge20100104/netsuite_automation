package com.netsuite.base.page.steps.jsteps.handles.beans

import com.netsuite.testautomation.driver.SeleniumHtmlElementHandle
import com.netsuite.testautomation.html.Locator

class EditTableAction {
    SeleniumHtmlElementHandle editEle
    SeleniumHtmlElementHandle endEditEle

    EditTableAction(SeleniumHtmlElementHandle editEle,SeleniumHtmlElementHandle endEditEle) {
        this.editEle = editEle
        this.endEditEle = endEditEle
    }


    def startEdit() {
        editEle.click()
    }


    def edit(String key) {
        if(key.contains("||")) {
            key = key.replace("||","``")
        }

        String [] keys = key.split("\\|")

        keys[0] = keys[0].replace("``","|")

        //Common text element, perform send text action
        if(keys.length == 1) {
            editEle = editEle.getElementByLocator(Locator.xpath(".//input"))
            editEle.sendKeys(keys[0])
            return
        }
    }


    def endEdit() {
        endEditEle.click()
        endEditEle.click()
    }

}
