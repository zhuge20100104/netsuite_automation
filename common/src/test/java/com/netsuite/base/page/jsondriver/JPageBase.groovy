package com.netsuite.base.page.jsondriver

import com.google.inject.Inject
import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.base.lib.login.LoginUtil
import com.netsuite.base.lib.property.PropertyUtil
import com.netsuite.base.page.jsondriver.util.base.JExpectUtility
import com.netsuite.base.page.jsondriver.util.base.JStepUtility
import com.netsuite.base.page.jsondriver.util.base.JUtility
import com.netsuite.base.page.jsondriver.util.base.JNavigationUtility
import com.netsuite.base.page.jsondriver.util.india.CheckUtility
import com.netsuite.base.page.jsondriver.util.india.PrepareDataUtility
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.checkers.ClassNameChecker
import com.netsuite.base.page.steps.checkers.IChecker
import com.netsuite.base.page.steps.checkers.IdChecker
import com.netsuite.base.page.steps.checkers.NameChecker
import com.netsuite.base.page.steps.checkers.XPathChecker
import com.netsuite.base.page.steps.checkers.fieldids.MainFieldChecker
import com.netsuite.base.page.steps.checkers.fieldids.SubFieldChecker
import com.netsuite.base.page.steps.expect.ClassNameExpect
import com.netsuite.base.page.steps.expect.IExpect
import com.netsuite.base.page.steps.expect.IdExpect
import com.netsuite.base.page.steps.expect.NameExpect
import com.netsuite.base.page.steps.expect.XPathExpect
import com.netsuite.base.page.steps.expect.fieldids.MainFieldExpect
import com.netsuite.base.page.steps.expect.fieldids.SubFieldExpect
import com.netsuite.base.page.steps.jsteps.ClassNameStep
import com.netsuite.base.page.steps.jsteps.ISteps
import com.netsuite.base.page.steps.jsteps.IdStep
import com.netsuite.base.page.steps.jsteps.NameStep
import com.netsuite.base.page.steps.jsteps.XPathStep
import com.netsuite.base.page.steps.jsteps.fieldids.MainFieldStep
import com.netsuite.base.page.steps.jsteps.fieldids.SubFieldStep
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.aut.pageobjects.EditMachine
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.runners.GuiceRunner
import net.qaautomation.exceptions.SystemException
import org.apache.commons.lang3.StringUtils
import org.junit.Assert
import org.junit.runner.RunWith
import org.openqa.selenium.Alert

@RunWith(GuiceRunner.class)
class JPageBase extends GroovyObjectSupport{


    String title
    String url
    String recordType
    String internalId

    private String msgDialog_XPATH = ".//div[@class='uir-message-popup']"

    def xpathRegEx = ~/^(\.\/\/|\/\/)/
    def idRegEx = ~/^(\w)+\u0024/
    //def cssRegEx = ~/\/^[#a-z,A-Z]|[.a-z,A-Z]|[\*a-z,A-Z]/

    String commonDropDownOptions= ".//div[contains(@class,'dropdownDiv')]/div[@style='display: block;']"
    String xpathLocator_DropDownOption = ".//div[@class='dropdownDiv']/div[contains(text(),'%s')]"
    String lineItemFirstCol= ".//tr[td[contains(text(),'%s')]]/td[1]"
    String msgSaveSuccess = "div.session_confirmation_alert"

    private static String OK_BUTTON_NAME = "OK"


    @Inject
    NetSuiteAppBase autBase

    @Inject
    NCurrentRecord currentRecord

    @Inject
    NRecord record

    @Inject
    LoginUtil loginUtil



    /**
     * Reset DropDown Options locator
     * @param xpathLocator
     */
    void setCommonDropDownOptionsValue(String xpathLocator){
        commonDropDownOptions = xpathLocator
    }

    /**
     * Get DropDown Element by ID
     * @param id - required
     * @return
     */
    DropdownList getDropDownListByID(String id){
        return new DropdownList(autBase,autBase.webDriver,id)
    }

    def click(def clickOption) {
        JUtility.click(autBase,clickOption)
    }


    def findByInternalId(String internalId,String RecordType) {
        return JUtility.findByInternalId(record, internalId, RecordType)
    }


    /**
     * Open SubTab by click SubTab Name
     * @param subTabName - required
     */
     void openFormSubTab(String subTabName){
         autBase.clickFormSubTab(subTabName)
    }

    /**
     * Get DropDown Option by it contained value
     * @param value - required
     * @return
     */
     HtmlElementHandle getDropDownOptionByValue(String value){
        String xpathLocator = xpathLocator_DropDownOption.replace("%s",value)
        return autBase.getElementByXPath(xpathLocator)
    }


    void clickOKButton() {
        autBase.clickButton(OK_BUTTON_NAME)
    }

    /***
     *the current page title, which no company information
     */
    String getPageTitle(){
        return autBase.getPageTitle()
    }

    /**
     * call object getAsText() method.
     * @param obj
     * @return
     */
    def getAsText(obj){
        return obj.getAsText()
    }

    /**
     * call object getValue() method.
     * @param obj
     * @return
     */
    def getElementValue(obj){
        return obj.getValue()
    }

    /**
     * call object exists() method.
     * Not support Dropdown element now ,because it not have exists() method.
     * @param obj
     * @return
     */
    boolean isElementExist(obj){
        return obj.exists()
    }

    /**
     * if element have disabled attribute and its value equals true,
     * then return true.
     *
     * @param obj
     * @return
     */
    boolean isElementDisabled(obj){
        return obj.getAttributeValue("disabled").contains("true")
    }

    /**
     * if element is visible in UI, than return true.
     * @param locator  could be ID, xpath or css locator
     * @return
     */
    boolean isElementVisible(String locator){
        if(locator =~ idRegEx){
            return autBase.isElementVisible(Locator.id(locator))
        }else if(locator =~ xpathRegEx){
            return autBase.isElementVisible(Locator.xpath(locator))
        }else{
            return autBase.isElementVisible(Locator.css(locator))
        }
    }

    /**
     * if element have dropdownInput css attribute,
     * then return true.
     *
     * @param obj
     * @return
     */
    boolean isDropdownElement(obj){
        return obj.getAttributeValue("class").contains("dropdownInput")

    }

    /**
     * Click Single Check Box
     *
     * @param obj, the checkbox element
     * @return
     */
    def clickSingleCheckbox(obj){
        autBase.checkNlsinglecheckbox(obj)
    }

    def getDropDownOptionElements(){
        return autBase.getElementsByXPath(commonDropDownOptions)
    }

    /**
     * Select dropdown option as expected.
     * By this method, will auto click the dropdown element which locate by
     * the first parameter @dropdownSite, and then the dropdown options list will open,
     * than click the expected option by the value of @targetValue to select it.
     *
     * @param dropdownSite, the locator for the dropdown element
     * @param targetValue,  the value of expected dropdown options
     */
    def enhanceDropDownSelect(String dropdownSite,String targetValue){
        HtmlElementHandle dropdown =  enhanceGet(dropdownSite)
        dropdown.click()
        for(HtmlElementHandle option:getDropDownOptionElements()){
            if(targetValue == option.getAsText().trim()){
                option.click()
                break
            }
        }
    }

    /**
     * Get DropDown Options Values
     * will filter hidden and empty option out
     *
     * @param dropdownSite
     * @param dropDownOptionsLocator - optional
     * @return   option's value list
     */
    def enhanceGetDropDownOptionsValue(String dropdownSite,String dropDownOptionsLocator = null){
        dropDownOptionsLocator ? commonDropDownOptions = dropDownOptionsLocator : ""
        HtmlElementHandle dropdown =  enhanceGet(dropdownSite)
        dropdown.click()  //click to open
        ArrayList<String> options = new ArrayList<String>()
        for(HtmlElementHandle option:getDropDownOptionElements()){
            if(option.isVisible()&&option.getAsText().trim().length()!=0){
                //filter hidden and empty option out
                options.add(option.getAsText().trim())
            }

        }
        dropdown.click()  //click to close
        return options
    }

    /**
     *  switch from other windows to current window
     * */
    void switchToPage(){
        autBase.switchToWindow(title)
    }

    void activeLineItemByValue(String fieldValue){
        String rowCell = lineItemFirstCol.replace("%s",fieldValue)
        HtmlElementHandle theFirstCell = enhanceGet(rowCell)
        theFirstCell.click()
    }

    /**
     * the Record ID from the URL, filter by regEx: ~/id=\d+/
     * @return
     */
    String getInternalIDFromURL(){
        String regEx = ~/id=\d+/
        String urlParts = autBase.getPageUrl().find(regEx)
        String id = urlParts.substring(3)
        return id
    }


    def getRolePathPrefix() {
        PropertyUtil propertyUtil = new PropertyUtil()
        return propertyUtil.getRolePathPrefix()
    }

    /**
     * Delete an existing record.
     * @param {string} [required] type - The record internal ID name.
     * @param {int} [required] id - The internalId for the record.
     * @return void
     */
    def apiDeleteRecord(type, id) {
        autBase.acceptUpcomingConfirmation()
        loginUtil.setRolePathPrefix(getRolePathPrefix())
        loginUtil.loginAsRole(loginUtil.getAdministrator())
        autBase.executeScript("nlapiDeleteRecord('${type}', ${id})")
    }

    /**
     * update an existing record
     * @param type
     * @param id
     * @param fieldId
     * @param fieldValue
     * @return void
     */
    def apiUpdateRecord(type, id, fieldId, fieldValue) {

        loginUtil.setRolePathPrefix(getRolePathPrefix())
        loginUtil.loginAsRole(loginUtil.getAdministrator())

        String script = """
             var rec = nlapiLoadRecord('${type}', ${id});
             rec.setFieldValue('${fieldId}', '${fieldValue}');
             nlapiSubmitRecord(rec, true);
            """
        autBase.executeScript(script)
    }



    /**
     * Verify if save success,
     * return true if the message div shown.
     * return false if the message div not shown.
     * @return
     */
    boolean isSaveSuccess(){
        return isElementVisible(msgSaveSuccess)
    }

    void setURLValue(String url){
        this.url = url
    }


    void setTitleValue(String title){
        this.title = title
    }


    void setRecordTypeValue(String recordType) {
        this.recordType = recordType
    }

    void setInternalIDValue(String internalID){
        this.internalId = internalID
    }

    /*---------------------------------------Navigate To Methods Below:-----------------------------------------------*/
    /**
     * navigate to new page
     *
     */

    //region navigation part start
    void navigate() {
        this.navigateToPage()
    }
     void navigateToPage() {

         println "In navigate to Page"
         if (this.url == null) {
             String scriptStr = "return (nlapiResolveURL('RECORD', '" + this.recordType + "'));"
             setURLValue(autBase.executeScript(scriptStr))

         }

         autBase.navigateTo(this.url)
         autBase.waitForPageToLoad()
         if (!this.title) {
             setTitleValue(getPageTitle())
         }

     }


    void navigate(String url) {
        this.navigateToPage(url)
    }
    void navigateToPage(String url) {
        JNavigationUtility.navigateToPage(autBase,url)
    }

    void navigate(String type,String scriptId,String deployId) {
        this.navigateToPage(type,scriptId,deployId)
    }
    void navigateToPage(String type,String scriptId,String deployId) {
        JNavigationUtility.navigateToPage(autBase,type,scriptId,deployId)
    }


    void navigate(int internalId,boolean isEdit) {
        this.navigateToPage(internalId,isEdit)
    }

    /**
     * navigate to page by internal id and mode
     * eg: navigateToPage(14639,true)
     */
     void navigateToPage(int internalId,boolean isEdit) {
        String scriptStr = "return (nlapiResolveURL('RECORD', '" + this.recordType + "', " + internalId + ", '"+(isEdit?"EDIT":"VIEW")+"'));"
        String transactionPageUrl = autBase.executeScript(scriptStr)
         autBase.navigateTo(transactionPageUrl)
         autBase.waitForPageToLoad()
        if( !this.title){
            setTitleValue(getPageTitle())
        }

         setInternalIDValue(String.valueOf(internalId))
    }


    void navigate(String name,boolean isEdit) {
        this.navigateToPage(name,isEdit)
    }

    /**
     * navigate to page by name and mode
     * eg: navigateToPage("GST Nonrecovery",true)
     */
     void navigateToPage(String name,boolean isEdit) {
        if(isEdit){
            setInternalIDValue(JUtility.navigateToEditPageByName(autBase,this.recordType,name))
        }else{
            setInternalIDValue(JUtility.navigateToViewPageByName(autBase,this.recordType,name))
        }
        if( !this.title){
            setTitleValue(getPageTitle())
         }
    }


    void navigate(String filterName,String name,boolean isEdit) {
        this.navigateToPage(filterName,name,isEdit)
    }

    /**
     * navigate to page by filtername and name and mode
     * eg: for nexus navigateToPage("discription","Nexus1",true)
     */
    void navigateToPage(String filterName,String name,boolean isEdit) {
        JUtility.navigateToPageByFilter(autBase,this.recordType,filterName,name,isEdit)
        if( !this.title){
            setTitleValue(getPageTitle())
        }
    }
    //endregion navigation part end


    /*---------------------------------------Enhance Getter and Setter Methods Below:---------------------------------*/

    /**
     * Auto set the value to the UI Element
     *
     * propertyName is the attribute in Page Object,
     * the newValue is the value fill in UI.
     *
     * For Checkbox element, use boolean:true to select it. and use boolean:false to un-select it.
     * For other type of element, this method will auto get the element by the locator which relative by the propertyName
     * in the Page Object. And set value on it.
     *
     * @param propertyName
     * @param newValue
     */
    @Override
    void setProperty(String propertyName, Object newValue) {
        //super.setProperty(propertyName, newValue)
        if(newValue instanceof  Boolean){
            String checkbox =  super.getProperty(propertyName)

            if(newValue){
                autBase.checkNlsinglecheckbox(checkbox)
            }else{
                autBase.unCheckNlsinglecheckbox(checkbox)
            }
        }else{
            enhanceSet(propertyName,newValue)
        }
    }



    //region actions and expect region

    def actions(def data) {
        JStepUtility.actions(record,currentRecord,autBase,data)
    }

    def expects(def data) {
        JExpectUtility.expects(record,currentRecord,autBase,data)
    }

    def prepareData(def prepareData) {
        PrepareDataUtility.prepareData(currentRecord,autBase,prepareData)
    }

    /**
     * Old india version for prepare data actions
     * @param data
     * @return
     */
    def inactions(def data) {
        prepareData(data)
    }

    /**
     * Old india version for check data actions
     * @param data
     * @return
     */
    void validatePage(expectedData){
        CheckUtility.validatePage(autBase,expectedData)
    }

    def inexpects(def data) {
        validatePage(data)
    }
    //endregion actions and expect region



    //region old ui related regions, will be removed in new versions
    def setFieldText(String type,String fieldName, String newValue) {
        JUtility.setFieldText(autBase,type,fieldName,newValue)
    }

    def click(String type,String locatorValue) {
        JUtility.click(autBase,type,locatorValue)
    }

    def selectItem(String type,String value,String item) {
        JUtility.selectItem(autBase,type,value,item)
    }

    def getText(String type, String value) {
        return JUtility.getText(autBase,type,value)
    }

    def waitForPageToLoad() {
        autBase.waitForPageToLoad()
    }


    def createRecord(def data) {
        return record.createRecord(data)
    }


    def setCurrentRecord(def recordData) {
        currentRecord.setCurrentRecord(recordData)
    }



    def findField(String recordType,String inField,String inValue,String outField) {
        return JUtility.findField(record,recordType,inField,inValue,outField)
    }

    def handleAlert() {
        autBase.handleAlert()
    }


    def sleep(int sec) {
        Thread.sleep(sec * 1000)
    }

    /**
     * @desc This function for those cannnot find the right line number
     * (if you use findSublistLineWithValue find the wrong number)
     * Note: for example, for company "tax registration" sublist
     * @return the line number of the value
     */
    int findSublistLineNumberWithValue(sublistId, fieldId, value){
        return JUtility.findSublistLineNumberWithValue(autBase,sublistId,fieldId,value)
    }

    /**
     * @desc remove the exact number of the line
     */
    void deleteLine(sublistId, lineNum){
        JUtility.deleteLine(autBase,sublistId,lineNum)
    }
    //endregion old ui related regions, will be removed in new versions



    //region enhance get and enhance set methods from india side
    /**
     * this method will auto get the element by the locator which relative by the propertyName
     * in the Page Object. And set value on it.
     *
     * @param propertyName
     * @param newValue
     * @return
     */
    def enhanceSet(String propertyName, Object newValue){
        def locator  = super.getProperty(propertyName)
        def targetElement =  enhanceGet(locator)

        if (targetElement instanceof NetSuiteAppBase){
            return super.setProperty(propertyName, newValue)
        }else if(targetElement instanceof  Boolean){
            return super.setProperty(propertyName, newValue)
        }else{
            String className =  targetElement.getClass().getName()
            if (targetElement instanceof HtmlElementHandle){
                if(isElementVisible(locator)){
                    targetElement.click()
                    targetElement.clear()
                    targetElement.invokeMethod("sendKeys",newValue)
                }else{
                    autBase.setFieldWithValue(locator,newValue)
                }

            }else if(targetElement instanceof DropdownList){
                targetElement.invokeMethod("setValue",newValue)
            }else {
                println("Cannot parse element class:$className")
            }
        }


    }

    /**
     * this method will auto get the element by the locator which relative by the propertyName
     * in the Page Object.
     *
     * @param proValue
     * @return the relative UI Element
     */
    def enhanceGet(String proValue){
        def element
        if(proValue =~ idRegEx){
            if(autBase.getHeadFieldType(proValue) == "select" ){
                element = new DropdownList(autBase,autBase.webDriver, proValue)
            }else{
                element =  autBase.getElementByID(proValue)
            }
           // element =  autBase.getElementByID(proValue)
           // element != null ? element : (element = new DropdownList(autBase,autBase.webDriver, proValue))
        }else if(proValue =~ xpathRegEx){
            element =  autBase.getElementByXPath(proValue)
            element != null ? element : (element = new DropdownList(autBase,autBase.webDriver, Locator.xpath(proValue)))
        }else{
            element =  autBase.getElementByCSS(proValue)
            element != null ? element : (element = new DropdownList(autBase,autBase.webDriver, Locator.css(proValue)))
        }
        return element

    }
    //endregion enhance get and enhance set methods from india side
}

