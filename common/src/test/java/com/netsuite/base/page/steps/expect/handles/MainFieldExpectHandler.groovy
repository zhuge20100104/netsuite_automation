package com.netsuite.base.page.steps.expect.handles

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.testautomation.html.HtmlElementHandle
import org.junit.Assert

class MainFieldExpectHandler {

    static boolean checkOptionsContains(NetSuiteAppBase context,String mainKey,def checkValue) {
        List<String> actualList = context.getSublistDropdownDisplayedOptions(mainKey)
        boolean isContains = true
        for(String singleCV : checkValue) {
            if(!actualList.contains(singleCV)) {
                isContains = false
                break
            }
        }
        return isContains
    }

    static boolean checkOptionsNotContains(NetSuiteAppBase context,String mainKey,def checkValue) {
        List<String> actualList = context.getSublistDropdownDisplayedOptions(mainKey)
        boolean isNotContains = true
        for(String singleCV : checkValue) {
            if(actualList.contains(singleCV)) {
                isNotContains = false
                break
            }
        }
        return isNotContains
    }


    static void handleMainFieldExp(NetSuiteAppBase context,String mainKey,def mainData) {
        mainData.each {
            checkKey,checkValue ->

                if(checkKey.equals("options")) {

                    List<String> actualList = context.getSublistDropdownDisplayedOptions(mainKey)
                    Assert.assertEquals("Check Field Optional List Size, FieldId: " + mainKey,actualList.size(),checkValue.size())
                    Assert.assertEquals("Check Field Optional List, FieldId: " + mainKey,actualList.sort() , checkValue.sort())

                }else if(checkKey.equals("optioncontains")) {
                    boolean isContains = checkOptionsContains(context,mainKey,checkValue)
                    Assert.assertTrue("Check options contains failed,FieldId: " + mainKey,isContains)
                }else if(checkKey.equals("optionnotcontains")) {
                    boolean  isNotContains = checkOptionsNotContains(context,mainKey,checkValue)
                    Assert.assertTrue("Check options not contains failed,FieldId: " + mainKey,isNotContains)
                }else if(checkKey.equals("disabled")) {
                    Assert.assertEquals("Check Field Disable, FieldId: " + mainKey, context.isFieldDisabled(mainKey), checkValue)
                }else if(checkKey.equals("displayed")) {
                    Assert.assertEquals("Check Field Display, FieldId: " + mainKey, context.isFieldDisplayed(mainKey), checkValue)
                }else if(checkKey.equals("value")) {
                    if (context.getHeadFieldType(mainKey) == "select") {
                        Assert.assertEquals("Check Field Value, FieldId: " + mainKey, context.getFieldText(mainKey),checkValue)
                    } else {
                        Assert.assertEquals("Check Field Value, FieldId: " + mainKey, context.getFieldValue(mainKey),checkValue)
                    }
                }else if(checkKey.equals("type")){
                    Assert.assertEquals("Check Field Type, FieldId: " + mainKey, context.getHeadFieldType(mainKey), checkValue)
                }else if(checkKey.equals("readonly")) {
                    Assert.assertEquals("Check Field ReadOnly, FieldId: " + mainKey, context.isFieldReadOnly(mainKey), checkValue)
                }else if(checkKey.equals("checked")) {
                    Assert.assertEquals("Check Field checked, FieldId: " + mainKey, context.isFieldChecked(mainKey), checkValue)
                }else if(checkKey.equals("exist")) {
                    HtmlElementHandle elmt = context.getElementByID("${mainKey}_fs_lbl")
                    if(elmt){
                        String style = elmt.getAttributeValue("style")
                        boolean result = true
                        if (style != null && style.indexOf("display: none") != -1){
                            result = false
                        }else{
                            result = true
                        }
                        Assert.assertEquals("Check Field exist, FieldId: " + mainKey, result, checkValue)
                    }else{
                        Assert.assertEquals("Check Field exist, FieldId: " + mainKey, false, checkValue)
                    }
                }
        }
    }

}
