package com.netsuite.test

import com.google.inject.Inject
import com.netsuite.base.JBaseAppTestSuite
import com.netsuite.base.page.actions.global.GlobalValueCache
import com.netsuite.test.jpage.JVATPage
import org.junit.After

class JVATBaseTestSuite extends JBaseAppTestSuite{
    def testData

    @Inject
    JVATPage mPage

    @Override
    def getDefaultRole() {
        return loginUtil.getAdministrator()
    }

    @Override
    def getTestDataPrefix() {
        return "src/test/java/com/netsuite/test/data/"
    }

    protected void initTest(String caseName) {
        testData = cData.getJson(caseName)
        this.setExtendPage(mPage)
    }



    @After
    void tearDown() {
        println "Cleanup all dirty records data in child"

        def subsidiaryId = GlobalValueCache.getValue("subsidiaryId")
        def dirtyData = GlobalValueCache.getValue("dirtyData")
        if(subsidiaryId!=null && dirtyData!=null) {
            def internalIds = []
            dirtyData.each {
                internalIds.add(it.internalid)
            }

            println "internalids to delete ---> " + internalIds
            mPage.navigateToClearDataPage(urlUtil,internalIds,subsidiaryId)
        }
    }

}
