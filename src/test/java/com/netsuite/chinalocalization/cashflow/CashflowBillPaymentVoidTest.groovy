package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.Setup.AccountPreferencePage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.testautomation.html.Locator
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import groovy.json.JsonSlurper
import org.junit.Test
import org.junit.After
import org.junit.experimental.categories.Category
import org.openqa.selenium.Keys
import com.netsuite.testautomation.aut.pageobjects.NetSuiteDataTable

@TestOwner("Jingzhou.wang@oracle.com & mia.wang@oracle.com")
class CashflowBillPaymentVoidTest extends CashflowBaseTest {

    @Inject
    protected JournalEntryPage journalEntryPage
    @Inject
    protected AccountPreferencePage accountPreferencePage

    @After
    void tearDown() {
        //deleteTransaction(response)
        context.deleteTransaction(response)
        super.tearDown()
    }

    @SuiteSetup
    void setUpTestSuite() {
        super.setUpTestSuite()
        switchToRole(getAdministrator())
        if (!nconfig.isReversalVoiding()) {
            accountPreferencePage.navigateToURL()
            accountPreferencePage.enableReversalVoiding()
        }
    }


    def response = "";
    def responseObj = {};
    /**
     * @CaseID Cashflow 1.44.1.1
     * @author jingzhou.wang@oracle.com
     * Description:
     *      Void a full paied Bill Payment:
     *          1. Create a bill
     *          2. Full pay the bill
     *          3. Void the payment
     *          4. Check CFS
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_44_1_1() {
        def dataString = context.getFileContent("test case 1.44.1.1", "cashflow//cn_cashflow_billpaymentvoid_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def caseId = "test case 1.44.1.1"
        def dataObj = caseObj[caseId].data
        def uiObj = context.getUIDataObj(caseObj, "CFS1.44.1.1_3")

        response = createTransaction(caseId, dataObj)
        responseObj = jsonSlurper.parseText(response)

        //Open bill payment and  Void the payment
        def paymentId = responseObj[1].internalid
        def vendorPaymentUrl = CURL.VENDOR_PAYMENT_CURL + "?id=" + paymentId + "&e=T&whence="
        context.navigateTo(vendorPaymentUrl)
        context.clickVoidByAPI()

        def journal = journalEntryPage.createVoidJournalEntry(uiObj)

        NetSuiteDataTable lineTable = journalEntryPage.getLineDataTable(context);
        assertFalse("CashflowItem column should be hidden", lineTable.doesColumnExist(CASHFLOWITEMCOLUMN));

        //Wait 10 seconds for CFS collector
        Thread.sleep(10000)

        checkCollectedCFS("customerpayment", paymentId, caseObj[caseId].expected_payment)
        checkCollectedCFS("journalentry", Long.parseLong(journal.internalid), caseObj[caseId].expected_journal)
    }

    /**
     * Void Billpayment
     * One payment => Multi Bills and Credit Bill
     * One bill full pay & One bill partial pay
     * @Author mia.wang@oracle.com
     * @CaseID Cashflow 1.44.1.2
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_44_1_2() {
        def caseId = "test case 1.44.1.2";
        def caseString = context.getFileContent(caseId, "cashflow//cn_cashflow_billpaymentvoid_data.json");
        def jsonslurper = new JsonSlurper();
        def caseObj = jsonslurper.parseText(caseString);
        def dataObj = caseObj[caseId].data;
        response = createTransaction(caseId, dataObj);
        context.log.info(response);
        responseObj = jsonslurper.parseText(response);

        //Open bill payment and click Void
        def paymentId = responseObj[responseObj.size - 1].internalid;
        def vendorPaymentUrl = CURL.VENDOR_PAYMENT_CURL + "?id=" + paymentId + "&e=T&whence=";
        context.navigateTo(vendorPaymentUrl);
        context.clickVoidByAPI()

        //Get void journal transdate/posting period
        //Set void journal transdata/posting period from UI
        def uidata = context.getUIDataObj(caseObj, "CFS1.44.1.2_5");
        context.webDriver.getHtmlElementByLocator(Locator.id("trandate")).clear();
        context.webDriver.getHtmlElementByLocator(Locator.id("trandate")).sendKeys(uidata.main.trandate + Keys.TAB);
        context.setFieldWithText("postingperiod", uidata.main.postingperiod);
        context.clickSaveByAPI()
        //def journalId = Long.parseLong(context.getParameterValueForFromQueryString("id"));
        def journalId = Long.parseLong(context.getRecordIdbyAPI());
        //Wait 20 seconds for CFS collector
        Thread.sleep(20000)

        checkCollectedCFS("customerpayment", paymentId, caseObj[caseId].expected_payment);
        checkCollectedCFS("journalentry", journalId, caseObj[caseId].expected_journal);
    }
}
