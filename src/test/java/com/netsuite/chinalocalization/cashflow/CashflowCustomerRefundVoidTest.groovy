package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.AccountPreferencePage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.testautomation.aut.pageobjects.NetSuiteDataTable
import com.netsuite.testautomation.driver.LocatorType
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import groovy.json.JsonSlurper
import net.qaautomation.common.HumanReadableDuration
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("yuanfang.chi@oracle.com & kevin.k.chen@oracle.com")
class CashflowCustomerRefundVoidTest extends CashflowBaseTest {

    @Inject
    protected JournalEntryPage journalEntryPage;
    @Inject
    protected AccountPreferencePage accountPreferencePage

    def response = "";


    @After
    public void "Tear Down"() {
        if (response)
            deleteTransaction(response);
    }

    @SuiteSetup
    void setUpTestSuite() {
        super.setUpTestSuite()
        if (!nconfig.isReversalVoiding()) {
            switchToRole(getAdministrator())
            accountPreferencePage.navigateToURL()
            accountPreferencePage.enableReversalVoiding()
        }
    }

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @desc 1. Test void customer refund with foreign currency;
     *        2. Check CashflowItem Column is hidden in Void Journal
     * @Author yuanfang.chi@oracle.com
     * @CaseID 1.40.1.1 & 1.42.3
     * */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_40_1_1() {
        def caseId = "test case 1.40.1.1";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_customerrefund_data.json", caseId);
        def dataObj = testDataObj[caseId].data;
        def uiObj = context.getUIDataObj(testDataObj, 'CFS1.40.1.1_3');

        response = createTransaction(caseId, dataObj);
        context.navigateTo(CURL.HOME_CURL);

        JsonSlurper jsonSlurper = new JsonSlurper();
        def responseObj = jsonSlurper.parseText(response);
        def refundId = responseObj[1].internalid

        //navigateto refund page and click void
        context.navigateToAndView(CURL.CUSTOMER_REFUND_CURL, refundId);
        context.clickVoidByAPI()

        def journal = journalEntryPage.createVoidJournalEntry(uiObj);

        NetSuiteDataTable lineTable = journalEntryPage.getLineDataTable(context);
        assertFalse("CashflowItem column should be hidden", lineTable.doesColumnExist(CASHFLOWITEMCOLUMN));

        //check collect cfs
        context.waitForConditionsUptoAMaxOf(new HashMap(), HumanReadableDuration.parse("60 sec"))
        checkCollectedCFS(responseObj[0].trantype as String, responseObj[0].internalid as long, testDataObj[caseId].expect_1);
        checkCollectedCFS(responseObj[1].trantype as String, responseObj[1].internalid as long, testDataObj[caseId].expect_2);
        checkCollectedCFS(journal.trantype, journal.internalid as long, testDataObj[caseId].expect_3);
    }

    /**
     * @Author kevin.k.chen@oracle.com
     * @CaseID 1.40.1.2
     * @Desc create a customer deposit and customer refund which apply the deposit. Void customer refund and check cfs collection.
     *
     * */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_40_1_2() {
        //This case need check customer return,need change to admin and run
        def caseId = "test case 1.40.1.2";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_customerrefund_data.json", caseId);
        def dataObj = testDataObj[caseId].data;

        response = createTransaction(caseId, dataObj);
        context.navigateTo("https://system.na3.netsuite.com/app/center/card.nl?sc=-29&whence=");
        JsonSlurper jsonSlurper = new JsonSlurper();
        def responseObj = jsonSlurper.parseText(response);

        def uiVoidJournalObj = context.getUIDataObj(testDataObj, 'Void Customer Refund');
        def expectedObj = testDataObj[caseId].expect;

        def refundId = responseObj[1].internalid

        //navigateto refund page and click void
        context.navigateTo("https://system.na3.netsuite.com/app/accounting/transactions/custrfnd.nl?id=" + refundId + "&whence=");
        context.clickVoidByAPI()
        context.setFieldWithValue("trandate", uiVoidJournalObj.main.trandate);
        context.clickSaveByAPI()
        def journalId = context.getRecordIdbyAPI()
        println "Internal Id:" + journalId
        //check collect cfs
        Thread.sleep(20000);
        checkCollectedCFS("journal", journalId as long, expectedObj);
    }

}
