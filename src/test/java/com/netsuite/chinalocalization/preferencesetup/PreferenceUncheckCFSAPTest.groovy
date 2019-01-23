package com.netsuite.chinalocalization.preferencesetup

import com.google.inject.Inject
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import org.openqa.selenium.Alert

class PreferenceUncheckCFSAPTest extends PreferenceSetupBaseTest {

    @Rule
    public TestName name = new TestName()
    private def static caseData

    @Inject
    AlertHandler alertHandler

    def pathToTestDataFiles() {
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "zhCN": "${dataFilesPath}preferencesetup\\data\\PreferenceUncheckCFSAPTest_zh_CN.json",
                "enUS": "${dataFilesPath}preferencesetup\\data\\PreferenceUncheckCFSAPTest_en_US.json"
        ]
    }

    def initData() {
        caseData = testData.get(name.getMethodName())
    }

    String type
    String internalId

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
            def id = Integer.parseInt(internalId)
            record.deleteRecord(type, id)
        }
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID
     * @desc  1. Disable CFS feature
     *        2. Create a new vendor bill
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_4_1_1() {
        type = "vendorbill"
        initData()
        setTranCurrentRecord(CURL.VENDOR_BILL_CURL)
        internalId = record.getRecordId()
        assertAreNotEqual("Transaction should be saved.", internalId, "")
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID
     * @desc  1. Disable CFS feature
     *        2. Create a new expense report
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_4_1_2() {
        type = "expensereport"
        initData()
        setTranCurrentRecord(CURL.EXPENSE_REPORT_CURL)
        internalId = record.getRecordId()
        assertAreNotEqual("Transaction should be saved.", internalId, "")
    }


    def setTranCurrentRecord(String tranType) {
        context.navigateTo(tranType)
        context.acceptUpcomingConfirmation()
        currentRecord.setCurrentRecord(caseData)
        context.clickSaveByAPI()
    }

}
