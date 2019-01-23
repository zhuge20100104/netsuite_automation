package com.netsuite.chinalocalization.preferencesetup

import com.google.inject.Inject

import com.netsuite.chinalocalization.page.Setup.PreferenceSetUpPage
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.testautomation.junit.runners.SuiteSetup
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before

class PreferenceSetupBaseTest extends BaseAppTestSuite {

    def testData

    @Inject
    PreferenceSetUpPage preSetupPage

/*
    @SuiteSetup
    void setUpTestSuite(){
        loginAsRole(getAdministrator())
    }
*/

    def pathToTestDataFiles(){

    }


    @Before
    void setUp() {
        def path = pathToTestDataFiles()

        if (isTargetLanguageChinese()){
            if (doesFileExist(path.zhCN)) {
                testData = context.asJSON(path: path.zhCN)
            }
        }
        if (!isTargetLanguageChinese()) {
            if (doesFileExist(path.enUS)) {
                testData = context.asJSON(path: path.enUS)
            }
        }
    }
    def switchWindow() {
        context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
        def  currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
    }

    @After
    void tearDown() {
        println("tearDown: cleaning up dirty data...")
        switchWindow()
    }
    def setCFS(boolean status, boolean saveRecord = false){
        def statusString = status ? "T" : "F"
        preSetupPage.setCashFlowStatement(statusString, saveRecord)
    }

    def setVAT(boolean status, boolean saveRecord = false){
        def statusString = status ? "T" : "F"
        preSetupPage.SetVatIntegration(statusString, saveRecord)
    }

    def navigateToPreferenceSetUp(){
        context.navigateTo(resolveSuiteletURL("customscript_sl_cn_pref", "customdeploy_sl_cn_pref"))
    }

    def setTranCurrentRecord(def caseData, String tranType) {
        context.navigateTo(tranType)
        //context.acceptUpcomingConfirmation()
        currentRecord.setCurrentRecord(caseData)
        context.clickSaveByAPI()
    }
}
