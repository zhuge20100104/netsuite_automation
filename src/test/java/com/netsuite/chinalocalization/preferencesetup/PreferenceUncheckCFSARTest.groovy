package com.netsuite.chinalocalization.preferencesetup

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

class PreferenceUncheckCFSARTest extends PreferenceSetupBaseTest{
    @Inject
    protected NRecord record
    @Rule
    public TestName name = new TestName()
    private def static caseData
    def responseObj = {}
    def internalId=""
    def tranType=""

    def pathToTestDataFiles(){
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "zhCN":"${dataFilesPath}preferencesetup\\data\\PreferenceUncheckCFSARTest_zh_CN.json",
                "enUS":"${dataFilesPath}preferencesetup\\data\\PreferenceUncheckCFSARTest_en_US.json"
        ]
    }

    @SuiteSetup
    void setUpTestSuite(){
        super.setUpTestSuite()
        navigateToPreferenceSetUp()
        setCFS(false)
        preSetupPage.clickSave()
    }

    @SuiteTeardown
    void tearDownTestSuite() {
        navigateToPreferenceSetUp()
        setCFS(true)
        preSetupPage.clickSave()
        super.tearDownTestSuite()
    }

    @After
    void tearDown() {
        if (internalId != "") {
            //context.deleteTransaction(tranType,internalId)
            def id = Integer.parseInt(internalId)
            record.deleteRecord(tranType, id)
        }
    }

    def initData(){
        caseData = testData.get(name.getMethodName())
    }

    /**
     *@desc preference uncheck CFS,save a CashSale test
     *@case 3.2.1
     *@author lisha.hao
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_3_2_1(){
        initData()
        // Create Cash Sale
        setTranCurrentRecord(caseData,  CURL.CASH_SALE_CURL)

        internalId = context.getParameterValueForFromQueryString("id")
        tranType = "cashsale"
        Assert.assertNotNull("The transaction is not saved successfully.", internalId)
    }

    /**
     *@desc preference uncheck CFS,save a Invoice test
     *@case 3.2.2
     *@author lisha.hao
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_3_2_2() {
        initData()
        // Create Invoice.
        setTranCurrentRecord(caseData, CURL.INVOICE_CURL)
        internalId = context.getParameterValueForFromQueryString("id")
        tranType = "invoice"
        // Invoice can be saved successfully.
        Assert.assertNotNull("The transaction is not saved successfully.", internalId)
    }
}
