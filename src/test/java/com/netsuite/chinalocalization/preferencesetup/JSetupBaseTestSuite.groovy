package com.netsuite.chinalocalization.preferencesetup

import com.google.inject.Inject
import com.netsuite.base.JBaseAppTestSuite
import com.netsuite.base.page.jsondriver.JPageBase
import org.junit.After

class JSetupBaseTestSuite extends JBaseAppTestSuite{
    def testData

    @Inject
    JPageBase mPage


    @Override
    def getTestDataPrefix() {
        return "src/test/java/com/netsuite/chinalocalization/preferencesetup/data/"
    }

    void initTest(String caseName) {
        testData = cData.getJson(caseName)
        this.setExtendPage(mPage)
    }



}
