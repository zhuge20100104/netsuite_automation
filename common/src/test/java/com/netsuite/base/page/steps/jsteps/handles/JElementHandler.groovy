package com.netsuite.base.page.steps.jsteps.handles

import com.netsuite.base.control.ext.DropdownListExt
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.testautomation.aut.pageobjects.MultiSelect
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator

class JElementHandler {

    static DropdownListExt asDropdownListExt(NetSuiteAppBase context,options) {
        if (options.fieldId) {
            return new DropdownListExt(context, context.webDriver, options.fieldId)
        } else if (options.locator) {
            return new DropdownListExt(context, context.webDriver, Locator.xpath(options.locator))
        }
    }


    static void handleElement(NetSuiteAppBase context, HtmlElementHandle handle,String key,def value) {
        //Multi-select element
        if(handle.getAttributeValue("type").equals("hidden") && handle.getAttributeValue("nlmultidropdown") != null) {
            MultiSelect select = context.withinMultiSelectField(key)
            select.setValues(value)
            //dropdown list element
        }else if(handle.getAttributeValue("class").contains("nldropdown")) {
            DropdownListExt dropdownList = null
            if(key.contains("//")) {
                dropdownList = asDropdownListExt(context,[locator: key])
            }else {
                dropdownList = asDropdownListExt(context,[fieldId: key])
            }

            dropdownList.selectItem(value)

            //textfield or textarea
        }else if(handle.getAttributeValue("class").contains("input")) {
            //Fix bug for selenium 3.0 and chrome
            handle.clear()
            handle.sendKeys(value)
            //check box field with checkbox id,need to click the img tag
        }else {
            handle.click()
        }

    }
}
