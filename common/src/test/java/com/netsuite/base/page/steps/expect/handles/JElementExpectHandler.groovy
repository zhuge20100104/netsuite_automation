package com.netsuite.base.page.steps.expect.handles

import com.netsuite.base.control.ext.DropdownListExt
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.testautomation.aut.pageobjects.MultiSelect
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import net.qaautomation.exceptions.SystemException
import org.junit.Assert

class JElementExpectHandler {


    static DropdownListExt asDropdownListExt(NetSuiteAppBase context,options) {
        if (options.fieldId) {
            return new DropdownListExt(context, context.webDriver, options.fieldId)
        } else if (options.locator) {
            return new DropdownListExt(context, context.webDriver, Locator.xpath(options.locator))
        }
    }


    static void handleValueExp(NetSuiteAppBase context, HtmlElementHandle handle,  String key,def subListValue) {
        //Multi-select field control
        if(handle.getAttributeValue("type").equals("hidden") && handle.getAttributeValue("nlmultidropdown") != null) {
            MultiSelect select = context.withinMultiSelectField(key)
            List<String> actualSelectValues = select.getValues()
            boolean isContain = true

            for(String value0 in subListValue) {
                if(!actualSelectValues.contains(value0)){
                    isContain = false
                }
            }

            Assert.assertTrue("Multi select :["+ key + "] with value : ["+ subListValue +"] not corrected!!!", isContain)

            //dropdown list element
        }else if(handle.getAttributeValue("class").contains("nldropdown")) {
            DropdownListExt dropdownList = null
            if(key.contains("//")) {
                dropdownList = asDropdownListExt(context,[locator: key])
            }else {
                dropdownList = asDropdownListExt(context,[fieldId: key])
            }

            String value = dropdownList.getValue()
            Assert.assertTrue("Drop downlist  :["+ key + "] with value : ["+ subListValue +"] not corrected!!!", subListValue.contains(value))

            //textfield or textarea
        }else if(handle.getAttributeValue("class").contains("input")) {
            //Fix bug for selenium 3.0 and chrome

            String value = handle.getAsText()
            Assert.assertTrue("text field  :["+ key + "] actValue ${value} expValue : ["+ subListValue +"] not corrected!!!", subListValue.contains(value))

        }else if(handle.getAttributeValue("class").equals("labelSpanEdit smallgraytextnolink")){
            String value = handle.getAsText()
            Assert.assertTrue("Label :["+ key + "] actValue [${value}] expValue : ["+ subListValue +"] not corrected!!!", subListValue.contains(value))
        }else if(handle.getAttributeValue("class").contains("smallgraytextnolink")) {
            //for common label assert
            String value = handle.getAttributeValue("innerHTML")
            Assert.assertTrue("Label :["+ key + "] actValue ${value} expValue : ["+ subListValue +"] not corrected!!!", subListValue.contains(value))
        }else if(handle.getAttributeValue("class").contains("rndbuttoninpt")) {
            //For common button  value assert
            String value = handle.getAttributeValue("value")
            Assert.assertTrue("Button :["+ key + "] actValue ${value} expValue : ["+ subListValue +"] not corrected!!!", subListValue.contains(value))
        }else {
            //for common control assert
            String value = handle.getAttributeValue("innerHTML")
            Assert.assertTrue("Control :["+ key + "] actValue ${value} expValue : ["+ subListValue +"] not corrected!!!", subListValue.contains(value))
        }
    }


    static void handleCheckboxExp(NetSuiteAppBase context, HtmlElementHandle handle,  String key,def subListValue) {
        if(handle.getAttributeValue("class").contains("checkbox")) {
            //For checkbox check handle
            boolean  isChecked = context.isFieldChecked(handle.getLocator())
            Assert.assertEquals("Checkbox :["+ key +" ] is checked value: ["+subListValue+"] not corrected!!",subListValue,isChecked)
        }else {
            throw new SystemException("Not a checkbox, please check your key ["+ key + "] and try again!!!")
        }
    }



    static void handleOptionsExp(NetSuiteAppBase context, HtmlElementHandle handle,  String key,List<String> subListValue) {
        //Multi-select field control
        if(handle.getAttributeValue("type").equals("hidden") && handle.getAttributeValue("nlmultidropdown") != null) {
            MultiSelect select = context.withinMultiSelectField(key)
            List<String> actualAllValues = select.getAllValues()
            boolean isContain = true

            for(String value0 in subListValue) {
                if(!actualAllValues.contains(value0)){
                    isContain = false
                }
            }

            Assert.assertTrue("Multi select :["+ key + "] with value : ["+ subListValue +"] not corrected!!!", isContain)

            //dropdown list element
        }else if(handle.getAttributeValue("class").contains("nldropdown")) {
            DropdownListExt dropdownList = null
            if(key.contains("//")) {
                dropdownList = asDropdownListExt(context,[locator: key])
            }else {
                dropdownList = asDropdownListExt(context,[fieldId: key])
            }

            List<String> allDropdownValues = dropdownList.getOptions()
            boolean isContain = true
            for(String value0 in subListValue) {
                if(!allDropdownValues.contains(value0)){
                    isContain = false
                }
            }

            Assert.assertTrue("Drop downlist  :["+ key + "] with value : ["+ subListValue +"] not corrected!!!", isContain)
        }else {
            throw new SystemException("Not supported control type exception!!!")
        }
    }




    static void handleOptionsNotContains(NetSuiteAppBase context, HtmlElementHandle handle,  String key,List<String> subListValue) {
        //Multi-select field control
        if(handle.getAttributeValue("type").equals("hidden") && handle.getAttributeValue("nlmultidropdown") != null) {
            MultiSelect select = context.withinMultiSelectField(key)
            List<String> actualAllValues = select.getAllValues()
            boolean isNotContain = true

            for(String value0 in subListValue) {
                if(actualAllValues.contains(value0)){
                    isNotContain = false
                }
            }

            Assert.assertTrue("Multi select :["+ key + "] with value : ["+ subListValue +"] not contains check failed!!!", isNotContain)

            //dropdown list element
        }else if(handle.getAttributeValue("class").contains("nldropdown")) {
            DropdownListExt dropdownList = null
            if(key.contains("//")) {
                dropdownList = asDropdownListExt(context,[locator: key])
            }else {
                dropdownList = asDropdownListExt(context,[fieldId: key])
            }

            List<String> allDropdownValues = dropdownList.getOptions()
            boolean isNotContain = true
            for(String value0 in subListValue) {
                if(allDropdownValues.contains(value0)){
                    isNotContain = false
                }
            }

            Assert.assertTrue("Drop downlist  :["+ key + "] with value : ["+ subListValue +"] not contains check failed!!!", isNotContain)
        }else {
            throw new SystemException("Not supported control type exception!!!")
        }
    }




    static void handleSeleniumElementExp(NetSuiteAppBase context, HtmlElementHandle handle, String key, Map<String,Object> value) {

        value.each {
            subListKey,subListValue ->
                if(subListKey.equals("values")) {
                    handleValueExp(context,handle,key,subListValue)
                }else if(subListKey.equals("options")) {
                    handleOptionsExp(context,handle,key,subListValue)
                }else if(subListKey.equals("optioncontains")){
                    handleOptionsExp(context,handle,key,subListValue)
                }else if(subListKey.equals("optionnotcontains")){
                    handleOptionsNotContains(context,handle,key,subListValue)
                }else if(subListKey.equals("checked")) {
                    handleCheckboxExp(context,handle,key,subListValue)
                }else {
                    throw new SystemException("Not supported expect: ["+ subListKey +"] for element: ["+ key +"]")
                }
        }
    }
}
