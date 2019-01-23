package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.AcceptPaymentPage
import com.netsuite.common.OW
import com.netsuite.common.P3
import com.netsuite.testautomation.aut.pageobjects.NetSuiteDataTable
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("kevin.k.chen@oracle.com")
class CashflowCustomerPaymentTest extends CashflowBaseTest {

    @Inject
    protected AcceptPaymentPage acceptPaymentPage;

    def response = "";

    @After
    public void tearDown() {
        if (response != null && !"".equals(response)) {
            deleteTransaction(response);
        }
        super.tearDown()
    }

    /**
     * @desc Create customer deposit and invoice record, accept customer payment for both.
     * @Author kevin.k.chen@oracle.com
     * @CaseID 1.32.2.1 Customer Payment for Customer Deposit and Invoice
     * */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_32_2_1() {
        def caseId = "test case 1.32.2.1";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_customerpayment_data.json", caseId);
        def dataObj = testDataObj[caseId].data;
        def uiObj = context.getUIDataObj(testDataObj, '1.32.2.1_4');
        def expectedObj = testDataObj[caseId].expect;

        response = createTransaction(caseId, dataObj);
        def payment = acceptPaymentPage.acceptCustomerPayment(context, testDataObj, uiObj, response);
        Thread.sleep(60000);
        checkCollectedCFS(payment.trantype, payment.internalid as long, expectedObj);
    }

    /**
     * @desc Open Customer Payment,"CHINA CASH FLOW ITEM" filed should be invisible
     * @Author lisha.hao@oracle.com
     * @CaseID 1.42.1 CFS invisible for Customer Payment
     * */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_42_1() {
        def caseId = "test case 1.42.1";
        context.navigateTo(CURL.CUSTOMER_PAYMENT_CURL)
        NetSuiteDataTable lineTable = acceptPaymentPage.getLineDataTable(context);
        assertFalse("CashflowItem column should be hidden", lineTable.doesColumnExist(CASHFLOWITEMCOLUMN));
        response = null
    }
}
