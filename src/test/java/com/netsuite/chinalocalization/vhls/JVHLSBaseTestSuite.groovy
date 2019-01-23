package com.netsuite.chinalocalization.vhls

import com.google.inject.Inject
import com.netsuite.base.JBaseAppTestSuite
import com.netsuite.base.page.actions.global.GlobalValueCache
import com.netsuite.base.page.jsondriver.JPageBase
import com.netsuite.chinalocalization.page.Setup.UserPreferencePage
import com.netsuite.chinalocalization.vhls.page.VHLSPage
import com.netsuite.testautomation.junit.runners.SuiteSetup
import org.junit.After

class JVHLSBaseTestSuite extends JBaseAppTestSuite{
    def testData

    @Inject
    JPageBase mPage
    @Inject
    UserPreferencePage userPrePage



    @Override
    def getTestDataPrefix() {
        return "src/test/java/com/netsuite/chinalocalization/vhls/data/"
    }

    void initTest(String caseName) {
        testData = cData.getJson(caseName)
        this.setExtendPage(mPage)
    }

    @SuiteSetup
    void setUpTestSuite(){
        setMiniRole("MiniRole_vhls")
        super.setUpTestSuite()
        def result = context.getPreference("DATEFORMAT")
        if (!(result =="M/D/YYYY")) {
            userPrePage.navigateToURL()
            userPrePage.setDateFormat("M/D/YYYY",true)
        }
    }



}
