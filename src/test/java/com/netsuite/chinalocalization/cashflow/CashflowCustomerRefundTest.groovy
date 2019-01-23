package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.BankDepositPage
import com.netsuite.common.OW
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("kevin.k.chen@oracle.com")
class CashflowCustomerRefundTest extends CashflowBaseTest {

    @Inject
    protected BankDepositPage bankDepositPage;

    def response = "";
    def bankDepositId;


    @After
    void tearDown() {
        if (response) {
            deleteTransaction(response);
        }
        super.tearDown()
    }

    /**
     * @desc Create two Customer Deposit records, refund them using Undeposited Funds, then deposit customer refund record.
     * @Author kevin.k.chen@oracle.com
     * @CaseID CFS 1.25.1.1 Deposit Customer Refund
     * */
    @Test
    @Category([OW.class, P3.class])
    void case_1_25_1_1() {
        //This case need deposit customer refund,need change to admin and run
        switchToRole(getAdministrator())
        def caseId = "test case 1.25.1.1";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_customerrefund_data.json", caseId);

        def dataObj = testDataObj[caseId].data;
        response = createTransaction(caseId, dataObj);

        JsonSlurper jsonSlurper = new JsonSlurper();
        def responseObj = jsonSlurper.parseText(response);
        def uiBankDepositObj = context.getUIDataObj(testDataObj, '1.25.1.1_4');

        def refundId = responseObj.internalid[2];
        bankDepositId = bankDepositPage.createDeposit(context, uiBankDepositObj, [refundId]);

        Thread.sleep(30000);
        checkCollectedCFS("deposit", bankDepositId, testDataObj[caseId].expect);
    }

    /**
     * @desc Create Customer Deposit record first, then refund this deposit. After refund, deposit application record will be created automatically.
     *        Also checked that in deposit application UI, the CFS field should not shown.
     * @Author yinwan.zhao@oracle.com & kim.shi@oracle.com
     * @CaseID Cashflow 1.25.1.2 & Cashflow 1.42.2 Customer Refund customer deposit
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_25_1_2() {
        def caseId = "test case 1.25.1.2";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_customerrefund_data.json", caseId);

        def dataObj = testDataObj[caseId].data;
        response = createTransaction(caseId, dataObj);

        def responseObj = new JsonSlurper().parseText(response);
        def refundId = responseObj.internalid[1];
        def depositApplId = responseObj.internalid[2];

        context.navigateTo("https://system.na3.netsuite.com/app/center/card.nl?sc=-29&whence=");
        sleep(20000);
        checkCollectedCFS("customerrefund", refundId, testDataObj[caseId].expect);

        // Check cfs does not exists in deposit application UI
        context.navigateTo(CURL.DEPOSIT_APPLICATION_CURL + "?id=" + depositApplId);
        // context.isFieldDisplayed("custcol_cseg_cn_cfi")
        Assert.assertFalse("Cashflow field should not shown in Deposit Application", context.doesFieldExist("custcol_cseg_cn_cfi"));
    }
}
