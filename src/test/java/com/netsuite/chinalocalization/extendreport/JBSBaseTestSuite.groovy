package com.netsuite.chinalocalization.extendreport

import com.google.inject.Inject
import com.netsuite.base.JBaseAppTestSuite
import com.netsuite.base.page.jsondriver.JPageBase
import com.netsuite.chinalocalization.page.Setup.UserPreferencePage
import com.netsuite.testautomation.junit.runners.SuiteSetup

class JBSBaseTestSuite extends JBaseAppTestSuite{
    def testData

    @Inject
    JPageBase mPage
    @Inject
    UserPreferencePage userPrePage


    @Override
    def getTestDataPrefix() {
        return "src/test/java/com/netsuite/chinalocalization/extendreport/data/"
    }

    void initTest(String caseName) {
        testData = cData.getJson(caseName)
        this.setExtendPage(mPage)
    }

    @SuiteSetup
    void setUpTestSuite(){
        super.setUpTestSuite()
        def result = context.getPreference("DATEFORMAT")
        if (!(result =="M/D/YYYY")) {
            userPrePage.navigateToURL()
            userPrePage.setDateFormat("M/D/YYYY",true)
        }
    }

}