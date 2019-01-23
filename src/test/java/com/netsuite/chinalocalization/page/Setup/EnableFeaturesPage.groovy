package com.netsuite.chinalocalization.page

import com.google.inject.Inject
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.aut.pageobjects.SingleCheckbox
import com.netsuite.testautomation.html.HtmlElementHandle
import org.apache.tools.ant.taskdefs.Javadoc

class EnableFeaturesPage extends PageBaseAdapterCN {

    @Inject
    ElementHandler elementHandler

    private static String TITLE = "Enable Features"
    private static String URL = "/app/setup/features.nl?whence="

    EnableFeaturesPage() {
    }

    def navigateToURL() {
        context.navigateTo(URL)
        elementHandler.waitForElementToBePresent(context.webDriver,XPATH_SAVE)
    }
    //Tab name
    private static final String TAB_NAME_SUITECLOUD = "SuiteCloud"
    //TermsWindowTitle
    private static  final String TERMS_WINDOW_ADVANCEDPRINTING_EN= "NetSuiteSuiteCloud Terms Of Service"
    private static  final String TERMS_WINDOW_ADVANCEDPRINTING_CN= "NetSuiteSuiteCloud 服务条款"
    //Field ID
    private static final String FIELD_ID_DEPARTMENTS = "departments"
    private static final String FIELD_ID_LOCATIONS = "locations"
    private static final String FIELD_ID_CLASSES = "classes"
    private static final String FIELD_ID_ADVANCEDPRINTING = "advancedprinting"
    //Xpath
    private static final String XPATH_SAVE = "//input[@id='submitter']"
    public static final String XPATH_SUITE_CLOUD = "//*[@id='customtxt']"
    def methodMissing(String name, args) {
        // Intercept method that starts with find.
        def field
        if (name.startsWith("get")) {
            field = name[3..-1]
            if(!field) throw new MissingMethodException(name, this.class, args)
            // Add new method to class with metaClass.
            def result
            this.metaClass."$name" = { -> result = context.getFieldValue(field)}
            result
        }else if (name.startsWith("set")) {
            field = name[3..-1]
            if(!field) throw new MissingMethodException(name, this.class, args)
            this.metaClass."$name" = {-> context.setFieldWithValue(field, args )}

        } else {

            throw new MissingMethodException(name, this.class, args)
        }
    }
    def enableAllCustomFilters(){
        if(context.isOneWorld() && !context.isAllClassificationEnabled()) {
            navigateToURL()
            //Comment this function due to some issues
            //enableAdvancedPrinting()
            enableClasses()
            enableLocations()
            enableDepartments()
            clickSave()
        }
    }
    def disableAllCustomFilters(){
        navigateToURL()
        disbleClasses()
        disbleLocations()
        disbleDepartments()
        clickSave()
    }

    def enableDepartments(){
        context.checkNlsinglecheckbox(FIELD_ID_DEPARTMENTS)
    }

    def disbleDepartments(){
        context.unCheckNlsinglecheckbox(FIELD_ID_DEPARTMENTS)
    }

    def enableLocations(){
        context.checkNlsinglecheckbox(FIELD_ID_LOCATIONS)
    }

    def disbleLocations(){
        context.unCheckNlsinglecheckbox(FIELD_ID_LOCATIONS)
    }

    def enableClasses(){
        context.checkNlsinglecheckbox(FIELD_ID_CLASSES)
    }

    def disbleClasses(){
        context.unCheckNlsinglecheckbox(FIELD_ID_CLASSES)
    }
    def enableAdvancedPrinting(){
        //need to set preference to english before using
        context.clickFormTab(TAB_NAME_SUITECLOUD)
        context.checkNlsinglecheckbox(FIELD_ID_ADVANCEDPRINTING)
        acceptTermsOfService()
    }
    def acceptTermsOfService() {
        String buttonValue = "I Agree"
        context.webDriver.getConfirmationMessage();
        if(context.getUserLanguage().equals("en_US")){
            context.clickButtonInPopupAndSwitchToWindow("I Agree", TERMS_WINDOW_ADVANCEDPRINTING_EN, "Enable Features")
        }else{
            context.clickButtonInPopupAndSwitchToWindow("我同意", TERMS_WINDOW_ADVANCEDPRINTING_CN, "启用功能")
        }

    }


    def disableAdvancedPrinting(){
        context.clickFormTab(TAB_NAME_SUITECLOUD)
        context.unCheckNlsinglecheckbox(FIELD_ID_ADVANCEDPRINTING)
    }

    def clickSave(){
        asElement(XPATH_SAVE).click()
    }

}
