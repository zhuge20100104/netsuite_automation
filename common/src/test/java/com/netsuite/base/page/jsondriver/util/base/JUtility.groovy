package com.netsuite.base.page.jsondriver.util.base

import com.netsuite.base.control.ext.DropdownListExt
import com.netsuite.base.lib.NRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.testautomation.aut.NetSuiteApp
import com.netsuite.testautomation.aut.pageobjects.EditMachine
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import net.qaautomation.exceptions.SystemException

import java.text.ParseException
import java.text.SimpleDateFormat

class JUtility {

    static String getEnvValue(NetSuiteAppBase aut, String key) {
        return System.getenv(key) == null ? aut.getContext().getProperty(key) : System.getenv(key)
    }

    public static String getSuiteletURL(NetSuiteAppBase aut, String scriptId, String deploymentId) {
        return aut.executeScript("return (nlapiResolveURL('SUITELET', '" + scriptId + "', '" + deploymentId + "'));");
    }


    public static String getRestletURL(NetSuiteAppBase aut,scriptId, deploymentId) {
        return aut.executeScript("return nlapiResolveURL('RESTLET', '" + scriptId + "', '" + deploymentId + "');")
    }

    static boolean isOneWorld(NetSuiteAppBase aut) {
        return Boolean.parseBoolean(aut.executeScript("return nlapiGetContext().getFeature('SUBSIDIARIES');"))
    }

    public static boolean isCloseBrowserOnTeardown(NetSuiteApp aut) {
        return Boolean.parseBoolean(aut.context.getProperty("testautomation.selenium.close_browser_on_teardown"));
    }

    // customer/vendor, customerName
    public static String getEntityId(NetSuiteAppBase aut, String type, String name) {
        String script = "var x = nlapiSearchRecord('" + type + "', null, new nlobjSearchFilter('entityId', null, 'contains', '" + name + "'));" +
                "return((x != null) ? x[0].getId() : null);";
        return (aut.executeScript(script));
    }

    public static void maxSizeWindow(NetSuiteApp aut){
        String width = aut.executeScript("return screen.width;")
        String height = aut.executeScript("return screen.height; ")
        Map<String, String> dimensions = new HashMap<String,String>()
        dimensions.put("width",width)
        dimensions.put("height",height)
        aut.changeWindowSizeTo(dimensions)
        //aut.executeScript("window.moveTo(0,0); ") //Not work there!!
    }


    public static String getTaxCodeIdByName(NetSuiteAppBase aut,String recordType, String name){
        String script = "var x = nlapiSearchRecord('"+recordType+"', null, new nlobjSearchFilter('name', null, 'is', '"+name+"'));" +
                "return((x != null) ? x[0].getId() : null);";
        String id = aut.executeScript(script);
        return id;
    }
    static String getEntityIdByName(NetSuiteAppBase aut,String recordType, String name){
        String script = "var x = nlapiSearchRecord('"+recordType+"', null, new nlobjSearchFilter('name', null, 'is', '"+name+"'));" +
                "return((x != null) ? x[0].getId() : null);"
        String id = aut.executeScript(script)
        return id
    }

    public static String getEntityIdByFilter(NetSuiteAppBase aut,String recordType, String filterName,String value){
        String operator="is";
        String script
        if(value.contains("%")){
            operator="startswith";
            value=value.split("%")[0];
        }
        script = "var x = nlapiSearchRecord('"+recordType+"', null, new nlobjSearchFilter('"+filterName+"', null, '"+operator+"', '"+value+"'));" +
                "return((x != null) ? x[0].getId() : null);";
        if(isValidDate(value)){
            script = "var x = nlapiSearchRecord('"+recordType+"', null, new nlobjSearchFilter('"+filterName+"', null, 'on', nlapiStringToDate('"+value+"')));" +
                    "return((x != null) ? x[0].getId() : null);";
        }
        String id = aut.executeScript(script);
        return id;
    }

    public static void navigateToNewPage(NetSuiteAppBase aut, String recordType){
        String redirectScript = "return (nlapiResolveURL('RECORD', '" + recordType + "'));";
        aut.webDriver.acceptUpcomingConfirmationDialog();
        aut.navigateTo(aut.executeScript(redirectScript));
        aut.waitForPageToLoad();
    }
    public static void navigateToEditPageById(NetSuiteAppBase aut,String recordType, String id){
        String redirectScript = "return nlapiResolveURL('RECORD','"+recordType+"',"+id+",'EDIT');";
        System.out.println("View test id: ." + redirectScript);
        aut.navigateTo(aut.executeScript(redirectScript));
        aut.waitForPageToLoad();
    }
    static String navigateToEditPageByName(NetSuiteAppBase aut,String recordType, String name){
        String id=getEntityIdByName( aut, recordType,  name)
        String redirectScript = "return nlapiResolveURL('RECORD','"+recordType+"',"+id+",'EDIT');"
        System.out.println("View Script: ." + redirectScript)
        aut.navigateTo(aut.executeScript(redirectScript))
        aut.waitForPageToLoad()
        return id
    }

    public static void navigateToEditPageByFilter(NetSuiteAppBase aut,String recordType,String filterName, String name){
        String id=getEntityIdByFilter( aut, recordType,filterName,  name);
        String redirectScript = "return nlapiResolveURL('RECORD','"+recordType+"',"+id+",true);";
        String url=aut.executeScript(redirectScript);
        if(url.contains("nexuses")){
            String newUrl=url.replace("nexuses","nexus");
            aut.navigateTo(newUrl);
        }else{
            aut.navigateTo(url);
        }
        aut.waitForPageToLoad();
    }
    public static String navigateToPageByFilter(NetSuiteAppBase aut,String recordType,String filterName, String name,boolean isEdit){
        String id=getEntityIdByFilter( aut, recordType,filterName,  name);
        String redirectScript = "return nlapiResolveURL('RECORD','"+recordType+"',"+id+","+(isEdit?"true":"false")+");";
        String url=aut.executeScript(redirectScript);
        if(url.contains("nexuses")){
            String newUrl=url.replace("nexuses","nexus");
            aut.navigateTo(newUrl);
        }else{
            aut.navigateTo(url);
        }
        aut.waitForPageToLoad();
        return id;
    }


    public static void navigateToViewPageById(NetSuiteAppBase aut,String recordType, String id){
        String redirectScript = "return nlapiResolveURL('RECORD','"+recordType+"',"+id+");";
        aut.navigateTo(aut.executeScript(redirectScript));
        aut.waitForPageToLoad();
    }

    static String navigateToViewPageByName(NetSuiteAppBase aut,String recordType, String name){
        String id=getEntityIdByName( aut, recordType,  name)
        String redirectScript = "return nlapiResolveURL('RECORD','"+recordType+"',"+id+");"
        System.out.println("View Script: ." + redirectScript)
        aut.navigateTo(aut.executeScript(redirectScript))
        aut.waitForPageToLoad()
        return id
    }

    public static boolean isRecordExist(NetSuiteAppBase aut,String recordType, String name){
        String id=getEntityIdByName( aut, recordType,  name);
        if (id == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isRecordExist(NetSuiteAppBase aut,String recordType, String filterName,String value){
        String id=getEntityIdByFilter( aut, recordType, filterName,value );
        System.out.println("View current EntityID ." + id)
        if (id == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {

            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess=false;
        }
        return convertSuccess;
    }


    public static def asDropdownList(NetSuiteAppBase autBase,options) {
        if (options.fieldId) {
            return autBase.withinDropdownlist(options.fieldId)
        } else if (options.locator) {
            return new DropdownListExt(autBase, autBase.webDriver, Locator.xpath(options.locator))
        }
    }


    static HtmlElementHandle asElement(NetSuiteAppBase autBase, String expression) {
        return autBase.withinHtmlElementIdentifiedBy(Locator.xpath(expression))
    }

    public static String getText(NetSuiteAppBase autBase,options) {
        if (options.fieldId) {
            return autBase.getFieldText(options.fieldId)
        } else if (options.locator) {
            return asElement(autBase,options.locator).getAttributeValue("value")
        }
    }

    static def click(NetSuiteAppBase autBase,String type,String locatorValue) {
        println "In JPageBase,setFieldWithText-->"

        def locator
        switch (type) {
            case "xpath":
                locator = Locator.xpath(locatorValue)
                break
            case "class":
                locator = Locator.className(locatorValue)
                break
            case "id":
                locator = Locator.id(locatorValue)
                break
            case "css":
                locator = Locator.css(locatorValue)
                break
            default:
                throw new SystemException("Locator type not implemented!")
                break
        }

        def ele = autBase.webDriver.getHtmlElementByLocator(locator)
        ele.click()
    }



    static def setFieldText(NetSuiteAppBase autBase, String type,String fieldName, String newValue) {
        println "In JPageBase,setFieldWithText-->"

        def locator
        switch (type) {
            case "xpath":
                locator = Locator.xpath(fieldName)
                break
            case "class":
                locator = Locator.className(fieldName)
                break
            case "id":
                locator = Locator.id(fieldName)
                break
            case "css":
                locator = Locator.css(fieldName)
                break
            default:
                throw new SystemException("Locator type not implemented!")
                break
        }

        autBase.setFieldIdentifiedByWithValue(locator,newValue)
    }


    static def findByInternalId(NRecord record,String internalId, String RecordType) {
        def columns = [
                record.helper.column("tranid").create()
        ]

        def filters = [
                record.helper.filter("internalid").is(internalId)
                //record.helper.filter("custrecord_cn_invoice_type_fk_tran").is(dirtyRecord)
        ]
        def retRecords = record.searchRecord("${RecordType}", filters, columns)
        return retRecords.tranid.size() > 0 ? retRecords.tranid[0] : null
    }


    static def click(NetSuiteAppBase autBase,def clickOption) {
        if(clickOption.id) {
            autBase.webDriver.click(Locator.id(clickOption.id))
        }else if(clickOption.xpath) {
            autBase.webDriver.click(Locator.xpath(clickOption.xpath))
        }else if(clickOption.css) {
            autBase.webDriver.click(Locator.css(clickOption.css))
        }
    }



    static int findSublistLineNumberWithValue(NetSuiteAppBase autBase,sublistId, fieldId, value){
        int countNum
        EditMachine editMachine = autBase.withinEditMachine(sublistId)
        int rowNum =  Integer.parseInt(autBase.webDriver.executeScript("return nlapiGetLineItemCount('${sublistId}')"));
        for(int i = 0; i < rowNum; i++){
            editMachine.setCurrentLine(i)
            if(autBase.getItemFieldType(sublistId, fieldId) == "select"){
                if(editMachine.getFieldText(fieldId).equals(value)){
                    countNum = i
                    break
                }
            }else{
                if(editMachine.getFieldValue(fieldId).equals(value)){
                    countNum = i
                    break
                }
            }
        }
        return countNum
    }



    static void deleteLine(NetSuiteAppBase autBase,sublistId, lineNum){
        EditMachine editMachine = autBase.withinEditMachine(sublistId)
        editMachine.setCurrentLine(lineNum)
        editMachine.remove()
    }


    static def findField(NRecord record, String recordType,String inField,String inValue,String outField) {
        def columns = [
                record.helper.column(outField).create()
        ]

        def filters = [
                record.helper.filter(inField).is(inValue)
        ]
        def retRecords = record.searchRecord("${recordType}", filters, columns)
        return retRecords."$outField".size() > 0 ? retRecords."$outField"[0] : null
    }


    static def getText(NetSuiteAppBase autBase,String type, String value) {
        switch (type) {
            case "fieldid":
                return  getText(autBase,[fieldId:value])
                break
            case "xpath":
                return  getText(autBase,[locator:value])
                break
            default:
                throw new SystemException("Not supported text locator, please check again!!!")
        }
    }



    static def selectItem(NetSuiteAppBase autBase,String type,String value,String item) {
        switch (type) {
            case "fieldid":
                asDropdownList(autBase,[fieldId:value]).selectItem(item)
                break
            case "xpath":
                asDropdownList(autBase,[locator:value]).selectItem(item)
                break
        }
    }
}
