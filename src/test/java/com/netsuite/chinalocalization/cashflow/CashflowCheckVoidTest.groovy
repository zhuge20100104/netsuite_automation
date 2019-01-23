package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.AccountPreferencePage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.testautomation.driver.LocatorType
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("kevin.k.chen@oracle.com & yuanfang.chi@oracle.com")
class CashflowCheckVoidTest extends CashflowBaseTest {

    @Inject
    protected JournalEntryPage journalEntryPage;
    @Inject
    protected AccountPreferencePage accountPreferencePage

    def response;

    @Before
    void setUp() {
        super.setUp()
        accountPreferencePage.navigateToURL()
        accountPreferencePage.enableReversalVoiding()
    }

    @After
    void tearDown() {
        context.deleteTransaction(response)
        super.tearDown()
    }

    /**
     * @Author kevin.k.chen@oracle.com
     * @CaseID 1.43.1.1
     * @Desc create a check and void this check. check cfs collection
     * */
    @Test
    @Category([OW.class, P1.class])
    void case_1_43_1_1() {
        def caseId = "test case 1.43.1.1";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_checkvoid_data.json", caseId);
        def dataObj = testDataObj[caseId].data;

        response = createTransaction(caseId, dataObj);
        context.navigateTo(CURL.HOME_CURL);
        JsonSlurper jsonSlurper = new JsonSlurper();
        def responseObj = jsonSlurper.parseText(response);
        def checkId = responseObj[0].internalid
        def uiVoidJournalObj = context.getUIDataObj(testDataObj, 'Edit void check');

        def expectedObj = testDataObj[caseId].expect;
        def expectedObj2 = testDataObj[caseId].expect_2;

        //Wait for cfs collection
        Thread.sleep(20000);
        checkCollectedCFS("check", checkId as long, expectedObj);

        //click void
        context.navigateToAndView(CURL.CHECK_CURL, checkId);
        context.clickVoidByAPI()
        def journal = journalEntryPage.createVoidJournalEntry(uiVoidJournalObj);
        def voidInternalId = journal.internalid
        checkTrue("Journal voided , id is :", voidInternalId != "")

        //Wait for cfs collection
        Thread.sleep(20000);
        checkCollectedCFS("journal", voidInternalId as long, expectedObj2)
    }

    /**
     * @Author yuanfang.chi@oracle.com
     * @CaseID 1.43.1.2
     * @Desc create a check with foreign currency and void this check. check cfs collection
     * */
    @Test
    @Category([OW.class, P1.class])
    void case_1_43_1_2() {
        def caseId = "test case 1.43.1.2";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_checkvoid_data.json", caseId);
        def dataObj = testDataObj[caseId].data;

        response = createTransaction(caseId, dataObj);
        context.navigateTo(CURL.HOME_CURL);
        JsonSlurper jsonSlurper = new JsonSlurper();
        def responseObj = jsonSlurper.parseText(response);
        def checkId = responseObj[0].internalid
        def uiVoidJournalObj = context.getUIDataObj(testDataObj, '1.43.1.2_2');

        def expectedObj = testDataObj[caseId].expect;
        def expectedObj2 = testDataObj[caseId].expect_2;

        //Wait for cfs collection
        Thread.sleep(20000);
        checkCollectedCFS("check", checkId as long, expectedObj);

        //click void
        context.navigateToAndView(CURL.CHECK_CURL, checkId);
        context.clickVoidByAPI()
        def journal = journalEntryPage.createVoidJournalEntry(uiVoidJournalObj);

        //check journal cfs
        //Wait for cfs collection
        Thread.sleep(20000);
        checkCollectedCFS("journal", journal.internalid as long, expectedObj2)
    }
}
