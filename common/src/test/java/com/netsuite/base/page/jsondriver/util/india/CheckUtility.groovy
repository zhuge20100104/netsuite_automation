package com.netsuite.base.page.jsondriver.util.india

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.testautomation.html.HtmlElementHandle
import org.junit.Assert

class CheckUtility {



    private static String msgDialog_XPATH = ".//div[@class='uir-message-popup']"

    static void checkTrue(String message,boolean condition) {
        Assert.assertTrue(message,condition)
    }

    static void checkAreEqual(String message,String value1,String value2) {
        Assert.assertEquals(message,value1,value2)
    }



    static void validatePage(NetSuiteAppBase autBase,expectedData){
        if(expectedData["main"]){
            checkMainField(autBase,expectedData["main"])
        }
        expectedData.each { k, v ->
            if (k != "main" && k != "actions" && k!= "message") {
                checkSublist(autBase,k, v)
            }
        }
        if(expectedData["actions"]){
            checkButtons(autBase,expectedData["actions"])
        }
        if(expectedData["message"]){
            checkMsg(autBase,expectedData["message"])
        }
    }



    static void checkMainField(NetSuiteAppBase autBase,mainData) {

        mainData.each { k, v ->
            if(v["subtabName"]){
                autBase.clickFormTab(v["subtabName"])
            }
            if(v["hidden"] != null && v["hidden"] == true){
                String displayType = autBase.executeScript("return document.getElementById('"+k+"').type;")
                checkTrue("Check Field Hidden, FieldId: " + k, displayType != null && displayType == "hidden")
            }else{
                if (v["label"]!=null) {
                    checkTrue("Check Field Label, FieldId: " + k, autBase.doesLabelExist(v["label"]))
                }
                if (v["headerlabel"]!=null) {
                    checkTrue("Check header Exist, header lable: " + k, autBase.isTextVisible(v["headerlabel"]))
                }
                if (v["value"]!=null) {
                    if (autBase.getHeadFieldType(k) == "select") {
                        checkAreEqual("Check Field Value, FieldId: " + k, autBase.getFieldText(k), v["value"])
                    } else {
                        String test = autBase.getFieldValue(k)
                        checkAreEqual("Check Field Value, FieldId: " + k, autBase.getFieldValue(k), v["value"])
                    }
                }
                if (v["disable"]!=null) {
                    checkAreEqual("Check Field Disable, FieldId: " + k, autBase.isFieldDisabled(k), v["disable"])
                }
                if (v["display"]!=null) {
                    checkAreEqual("Check Field Display, FieldId: " + k, autBase.isFieldDisplayed(k), v["display"])
                }
                if (v["type"]!=null) {
                    checkAreEqual("Check Field Type, FieldId: " + k, autBase.getHeadFieldType(k), v["type"])
                }
                if (v["required"]!=null) {
                    checkAreEqual("Check Field Required, FieldId: " + k, autBase.isFieldMandatory(k), v["required"])
                }
                if (v["readOnly"]!=null) {
                    checkAreEqual("Check Field ReadOnly, FieldId: " + k, autBase.isFieldReadOnly(k), v["readOnly"])
                }
                if (v["exist"]!=null) {
                    HtmlElementHandle elmt = autBase.getElementByID("${k}_fs_lbl")
                    if(elmt){
                        String style = elmt.getAttributeValue("style")
                        boolean result = true
                        if (style != null && style.indexOf("display: none") != -1){
                            result = false
                        }else{
                            result = true
                        }
                        checkAreEqual("Check Field exist, FieldId: " + k, result, v["exist"])
                    }else{
                        checkAreEqual("Check Field exist, FieldId: " + k, false, v["exist"])
                    }

                }
                if (v["listOptions"]!=null) {
                    def OptionalList = autBase.getSublistDropdownDisplayedOptions(k)
                    checkAreEqual("Check Field Optional List Size, FieldId: " + k,OptionalList.size() , v["listOptions"].size())
                    checkAreEqual("Check Field Optional List, FieldId: " + k,OptionalList.sort() , v["listOptions"].sort())
                }
                if (v["maxlength"]!=null) {
                    HtmlElementHandle field = autBase.getElementByID(k)
                    int actual = field.getAttributeValue("maxlength").toInteger()
                    checkAreEqual("Check Field Max Length, FieldID: " + k,v["maxlength"].toInteger(),actual)
                }
            }

        }
    }





    /**
     * spport check sublist for edit mode
     * @paramter sublistId
     * sublistData: like
     {
     "subtabName": "Accounts",
     "columnsHeader": [
     "Nexus",
     "Tax Asset Account",
     "Tax Liabilty Account"
     ],
     "values": [
     {
     "nexus": "India For GST 01",
     "payablesaccount":"Acc. Dep-Building",
     "receivablesaccount":"Accounts Payable"
     }
     ]
     }
     * */
    public static void checkSublist(NetSuiteAppBase autBase,sublistId, sublistData) {
        //check sublist exist
        if(sublistData["subtabName"]){
            autBase.clickFormTab(sublistData["subtabName"]);
            checkTrue("Check SubTab Exist, SublistId: " + sublistId, autBase.doesFormTabExist(sublistData["subtabName"]))
        }
        if(sublistData["formSubTabName"]){
            autBase.clickFormSubTab(sublistData["formSubTabName"]);
            checkTrue("Check SubTab Exist, SublistId: " + sublistId, autBase.doesFormSubTabExist(sublistData["formSubTabName"]))
        }


        //check column exist
        if (sublistData["columns"]) {
            List<String> sublistValues = (List<String>) sublistData["columns"];
            for (int i = 1; i <=sublistValues.size(); i++) {
                autBase.withinEditMachine(sublistId).setCurrentLine(i);
                sublistValues.get(i-1).each { k, v ->
                    if (v["value"] != null){
                        if (autBase.getItemFieldType(sublistId,k) == "select") {
                            checkAreEqual("check sublist values,sublistId：" + sublistId + " fieldId: " + k
                                    , autBase.withinEditMachine(sublistId).getFieldText(k), v["value"]);
                        }else{
                            checkAreEqual("check sublist values,sublistId：" + sublistId + " fieldId: " + k
                                    , autBase.withinEditMachine(sublistId).getFieldValue(k), v["value"]);
                        }
                    }

                    if(v["header"]!=null){
                        checkTrue("check column exist,sublistId：" + sublistId + " Column: " + v["header"]
                                , autBase.withinEditMachine(sublistId).doesColumnExist(v["header"]))
                    }

                    if (v["disable"]!=null) {
                        checkAreEqual("Check Field Disable, FieldId: " + k, autBase.withinEditMachine(sublistId).isFieldDisabled(k), v["disable"]);
                    }

                    if (v["type"]!=null) {
                        checkAreEqual("Check Field Type, FieldId: " + k, autBase.getItemFieldType(sublistId,k), v["type"]);
                    }
                    if (v["required"]!=null) {
                        checkAreEqual("Check Field Required, FieldId: " + k,autBase.isSublistFieldMandory(sublistId, v["header"]), v["required"]);
                    }

                    if (v["listOptions"]!=null) {
                        def OptionalList = autBase.getSublistDropdownDisplayedOptions(sublistId,k)
                        checkAreEqual("Check Field Optional List Size, FieldId: " + k,OptionalList.size() , v["listOptions"].size())
                        checkAreEqual("Check Field Optional List, FieldId: " + k,OptionalList.sort() , v["listOptions"].sort())
                    }
                }

            }
        }
    }





    /**
     * support to check the button exist and disable
     @paramter
      [
      {
      "name": "Save",
      "display":false,
      "disable": false
      }
      ]
      * */
    public static void checkButtons(NetSuiteAppBase autBase,actionData) {
        List<String> actions = (List<String>) actionData;
        for (int i = 0; i < actions.size(); i++) {
            def action = actions.get(i)
            if(action["display"]!=null){
                if (action["name"] == "Action") {
                    checkTrue("check Action Exist: " + action["name"], autBase.doesActionsExist()==action["display"]);
                    if (action["actionlist"]!=null){
                        action["actionlist"].each { actionButton ->
                            checkTrue("check Action Exist: Action >" + actionButton["name"], autBase.doesActionsMenuExist(actionButton["name"])==actionButton["display"]);
                        }
                    }
                } else {
                    checkTrue("check Action Exist: " + action["name"], autBase.doesButtonExist(action["name"])==action["display"]);
                }
            }

            if (action["disable"]!=null) {
                checkAreEqual("check Action Disable: " + action["name"], autBase.isButtonDisabled(action["name"]), action["disable"]);
            }

        }

    }




    /**
     * wait for message dialog to present for default 4*0.5 second
     * or to wait specified tryCount*0.5 second
     * @param tryCount int
     */
    static void waitForMessageDialog(NetSuiteAppBase autBase, int tryCount=4){//if not specify tryCount, use 4
        ElementHandler elementHandler = new ElementHandler()
        elementHandler.waitForElementToBePresent(autBase.webDriver, msgDialog_XPATH, tryCount)
    }


    static void waitForAlert(NetSuiteAppBase autBase,int tryCount=4){//if not specify tryCount, use 4
        AlertHandler alertHandler = new AlertHandler()
        alertHandler.waitForAlertToBePresent(autBase.webDriver, tryCount)
    }



    /**
     * Verify if the alert message is as expected.
     *
     * @param expectedMessage
     * @return
     */
    static boolean doesAlertMessageExist(NetSuiteAppBase autBase ,String expectedMessage){
        String actualMessage = autBase.webDriver.getAlertMessage()
        if ((actualMessage == expectedMessage)  || autBase.isTextVisible(expectedMessage)){
            return true
        }
        return false
    }

    /**
     * support to verify message
     @paramter
      "message": {
      "type": "reload/alert/dialog",
      "content": "Vendor successfully Saved"}
      }
      * */
    static void checkMsg(NetSuiteAppBase autBase,msgData){

        if(msgData["content"] != null){
            if("dialog" == msgData["type"]){
                waitForMessageDialog()
                checkTrue("check mssage exists: " + msgData["content"], doesAlertMessageExist(autBase,msgData["content"]))
            }else if("alert" == msgData["type"]){
                waitForAlert()
                checkTrue("check mssage exists: " + msgData["content"], doesAlertMessageExist(autBase,msgData["content"]))
            }else{
                autBase.waitForPageToLoad()
                checkTrue("check mssage exists: " + msgData["content"], doesAlertMessageExist(autBase,msgData["content"]))
            }
        }

    }

}
