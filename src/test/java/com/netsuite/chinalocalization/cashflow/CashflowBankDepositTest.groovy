package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.BankDepositPage
import com.netsuite.chinalocalization.page.InvoicePage
import com.netsuite.chinalocalization.page.SalesOrderPage
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("kim.shi@oracle.com")
class CashflowBankDepositTest extends CashflowBaseTest {

    @Inject
    protected BankDepositPage depositPage;
    @Inject
    protected InvoicePage invoicePage

    def response = "";
    def depositId;

    def getDefaultRole() {
        return getAdministrator()
    }

    @After
    public void tearDown() {
        if (depositId)
            context.deleteTransaction('deposit', depositId as String);
        if (response && !"".equals(response)) {
            context.deleteTransaction(response)
        }
        super.tearDown()
    }

    /**
     * @Author kim.shi@oracle.com
     * @CaseID Cashflow 1.23.1.2
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_23_1_2() {
        def caseId = "test case 1.23.1.2";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId);

        def dataObj = testDataObj[caseId].data;
        def uiObj = context.getUIDataObj(testDataObj, '1.23.1.2_2');
        def expectedObj = testDataObj[caseId].expect;

        response = createTransaction(caseId, dataObj);
        def responseObj = new JsonSlurper().parseText(response);
        depositId = depositPage.createDeposit(context, uiObj, responseObj.internalid[0]);

        responseObj += [internalid: depositId, trantype: "deposit"]
        response = JsonOutput.toJson(responseObj)

        Thread.sleep(30000);
        checkCollectedCFS("deposit", depositId, expectedObj);
    }

    /**
     * @Author yinwan.zhao@oracle.com
     * @CaseID Cashflow 1.28.3.1
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_28_3_1() {
        def caseId = "test case 1.28.3.1";
        def testDataObj = loadCFSTestData("cashflow//case_1_28_3_1_data.json", caseId);

        def dataObj = testDataObj[caseId].data;
        def uiObj = context.getUIDataObj(testDataObj, '1.28.3.1_3');
        def expectedObj = testDataObj[caseId].expected;

        response = createTransaction(caseId, dataObj);
        def responseObj = new JsonSlurper().parseText(response);

        def depositId = depositPage.createDeposit(context, uiObj, [responseObj.internalid[0], responseObj.internalid[1]]);

        responseObj += [internalid: depositId, trantype: "deposit"]
        response = JsonOutput.toJson(responseObj)

        Thread.sleep(20000);
        checkCollectedCFS("deposit", depositId, expectedObj);
    }

    /**
     * @Author kim.shi@oracle.com
     * @CaseID Cashflow 1.35.3
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_35_3() {
        def caseId = "test case 1.35.3";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId);

        def dataObj = testDataObj[caseId].data;
        def uiObj = context.getUIDataObj(testDataObj, '1.35.3_3');

        response = createTransaction(caseId, dataObj);

        def responseObj = new JsonSlurper().parseText(response);
        def paymentId = responseObj[1].internalid;
        def invoiceId = responseObj[0].internalid;
        depositId = depositPage.createDeposit(context, uiObj, paymentId);

        responseObj += [internalid: depositId, trantype: "deposit"]
        response = JsonOutput.toJson(responseObj)

        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long);
        def resultObj = new JsonSlurper().parseText(searchResult);

        Assert.assertEquals("Number of lines collected is not correct", 1, resultObj.size);
        Assert.assertEquals("Deposit Transaction internalid is not correct", depositId as Long, resultObj[0].depositId as Long);
        Assert.assertEquals("Payment Transaction internalid is not correct", paymentId as Long, resultObj[0].pymtId as Long);
        Assert.assertEquals("Paid Transaction internalid is not correct", invoiceId as Long, resultObj[0].paidId as Long);
    }

    /**
     * @Author lisha.hao@oracle.com
     * @CaseID Cashflow 1.35.4
     * @Description Create Invoice,then deposited Customer Payment, at last check Saved Search Result
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_35_4() {
        def caseId = "test case 1.35.4";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId);
        def dataObj = testDataObj[caseId].data;

        response = createTransaction(caseId, dataObj);
        def responseObj = new JsonSlurper().parseText(response);
        def invoiceId = responseObj[0].internalid;
        def paymentId = responseObj[1].internalid;

        response = JsonOutput.toJson(responseObj)

        Thread.sleep(10000);
        context.navigateTo(CURL.HOME_CURL);
        String searchResult = executeSavedSearchforPayment(paymentId as Long);
        def resultObj = new JsonSlurper().parseText(searchResult);

        Assert.assertEquals("Number of lines collected is not correct", 1, resultObj.size);
        Assert.assertEquals("Payment Transaction internalid is not correct", paymentId as Long, resultObj[0].pymtId as Long);
        Assert.assertEquals("Paid Transaction internalid is not correct", invoiceId as Long, resultObj[0].paidId as Long);
    }

    /**
     * @Author lisha.hao@oracle.com
     * @CaseID Cashflow 1.15.1
     * @Description Create Invoice with 'Discount Item 10', then create Customer Payment and Bank Deposit,
     * 				 to check Cash flow statement collected into record.
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_15_1() {
        def caseId = "test case 1.15.1"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        def uiObj = context.getUIDataObj(testDataObj, '1.15.1_3')
        def expectedObj = testDataObj[caseId].expected;

        response = createTransaction(caseId, dataObj);

        def responseObj = new JsonSlurper().parseText(response);
        def invoiceId = responseObj[0].internalid;
        def paymentId = responseObj[1].internalid;
        depositId = depositPage.createDeposit(context, uiObj, paymentId);
        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long);
        def resultObj = new JsonSlurper().parseText(searchResult);

        checkCollectedCFS("deposit", depositId, expectedObj);
    }

    /**
     * @Author lisha.hao@oracle.com
     * @CaseID Cashflow 1.15.2
     * @Description Create Invoice with 'Discount Item 10%', then create Customer Payment and Bank Deposit,
     * 				 to check Cash flow statement collected into record.
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_15_2() {
        def caseId = "test case 1.15.2"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        def uiObj = context.getUIDataObj(testDataObj, '1.15.2_3')
        def expectedObj = testDataObj[caseId].expected;

        response = createTransaction(caseId, dataObj);

        def responseObj = new JsonSlurper().parseText(response);
        def invoiceId = responseObj[0].internalid;
        def paymentId = responseObj[1].internalid;
        depositId = depositPage.createDeposit(context, uiObj, paymentId);
        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long);
        def resultObj = new JsonSlurper().parseText(searchResult);

        checkCollectedCFS("deposit", depositId, expectedObj);
    }

    /**
     * @Author lisha.hao@oracle.com
     * @CaseID Cashflow 1.15.3
     * @Description Create Invoice with 'Discount Item Non-Posting 10', then create Customer Payment and Bank Deposit,
     * 				 to check Cash flow statement collected into record.
     */
    @Test
    @Category([SI.class, P3.class])
    public void case_1_15_3() {
        def caseId = "test case 1.15.3"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        def uiObj = context.getUIDataObj(testDataObj, '1.15.3_3')
        def expectedObj = testDataObj[caseId].expected;

        response = createTransaction(caseId, dataObj);

        def responseObj = new JsonSlurper().parseText(response);
        def invoiceId = responseObj[0].internalid;
        def paymentId = responseObj[1].internalid;
        depositId = depositPage.createDeposit(context, uiObj, paymentId);
        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long);
        def resultObj = new JsonSlurper().parseText(searchResult);

        checkCollectedCFS("deposit", depositId, expectedObj);
    }

    /**
     * @Author lisha.hao@oracle.com
     * @CaseID Cashflow 1.15.4
     * @Description Create Invoice with 'Discount Item Non-Posting 10%', then create Customer Payment and Bank Deposit,
     * 				 to check Cash flow statement collected into record.
     */
    @Test
    @Category([SI.class, P3.class])
    public void case_1_15_4() {
        def caseId = "test case 1.15.4"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        def uiObj = context.getUIDataObj(testDataObj, '1.15.4_3')
        def expectedObj = testDataObj[caseId].expected;

        response = createTransaction(caseId, dataObj);

        def responseObj = new JsonSlurper().parseText(response);
        def invoiceId = responseObj[0].internalid;
        def paymentId = responseObj[1].internalid;
        depositId = depositPage.createDeposit(context, uiObj, paymentId);
        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long);
        def resultObj = new JsonSlurper().parseText(searchResult);

        checkCollectedCFS("deposit", depositId, expectedObj);
    }

    /**
     * @Author yang.g.liu@oracle.com
     * @CaseID Cashflow 1.15.5
     * @Description Create Invoice with 'Discount Item 10%' and 'Discount Item 10', then create Customer Payment and Bank Deposit,
     * 				 to check Cash flow statement collected into record.
     *          1.Cash flow Item in invioce header
     *          2.Discount Item 10 in line
     *          3.Discount Item 10% in line
     *          4.Different tax code between item
     *          5.functional currency
     *          6..Full payment
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_15_5() {
        def caseId = "test case 1.15.5"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        def uiObj = context.getUIDataObj(testDataObj, '1.15.5_3')
        def expectedObj = testDataObj[caseId].expected

        response = createTransaction(caseId, dataObj)

        def responseObj = new JsonSlurper().parseText(response);
        def invoiceId = responseObj[0].internalid
        def paymentId = responseObj[1].internalid
        depositId = depositPage.createDeposit(context, uiObj, paymentId)
        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long)
        def resultObj = new JsonSlurper().parseText(searchResult)

        checkCollectedCFS("deposit", depositId, expectedObj)
    }

    /**
     * @Author yang.g.liu@oracle.com
     * @CaseID Cashflow 1.15.7
     * @Description Create Invoice with 'Discount Item on-Posting 10%' and 'Discount Item Non-Posting 20%', then create Customer Payment and Bank Deposit,
     * 				 to check Cash flow statement collected into record.
     *          1.Cash flow Item in invioce header
     *          2.Discount Item Non-Posting 10% in header
     *          3.Discount Item Non-Posting 20% in the line of item1
     *          4.Discount Item Non-Posting 10% in the line of item2
     *          5.functional currency
     *          6..Full payment
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_15_7() {
        def caseId = "test case 1.15.7"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        def uiObj = context.getUIDataObj(testDataObj, '1.15.7_3')
        def expectedObj = testDataObj[caseId].expected

        response = createTransaction(caseId, dataObj)

        def responseObj = new JsonSlurper().parseText(response);
        def invoiceId = responseObj[0].internalid
        def paymentId = responseObj[1].internalid
        depositId = depositPage.createDeposit(context, uiObj, paymentId)
        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long)
        def resultObj = new JsonSlurper().parseText(searchResult)

        checkCollectedCFS("deposit", depositId, expectedObj)
    }

    /**
     * @Author yang.g.liu@oracle.com
     * @CaseID Cashflow 1.15.9
     * @Description Create Invoice with 'Discount Item 10%' and Foreign currency, then create Customer Payment and Bank Deposit,
     * 				 to check Cash flow statement collected into record.
     * 				 1.Cash flow Item in invioce header
     * 				 2.Discount Item 10%  in header
     * 				 3.Foreign currency:US Dollar
     * 				 4.Full payment
     * 				 5.tax
     * 				 6.Deposit modify Amount -10
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_15_9() {
        def caseId = "test case 1.15.9"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        def uiObj = context.getUIDataObj(testDataObj, '1.15.9_3')
        def expectedObj = testDataObj[caseId].expected

        response = createTransaction(caseId, dataObj)

        def responseObj = new JsonSlurper().parseText(response);
        def invoiceId = responseObj[0].internalid
        def paymentId = responseObj[1].internalid
        depositId = depositPage.createDeposit(context, uiObj, paymentId)
        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long)
        def resultObj = new JsonSlurper().parseText(searchResult)

        checkCollectedCFS("deposit", depositId, expectedObj)
    }

    /**
     * @Author stephen.zhou@oracle.com
     * @CaseID Cashflow 1.15.10
     * @Description Create double invoice, payment and deposit
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_15_10() {
        // First time, create invoice, customer payment and deposit
        def caseId = "test case 1.15.10"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        def uiObj = context.getUIDataObj(testDataObj, '1.15.10_3')
        def expectedObj = testDataObj[caseId].expected

        response = createTransaction(caseId, dataObj)

        def responseObj = new JsonSlurper().parseText(response)
        def invoiceId = responseObj[0].internalid
        def paymentId = responseObj[1].internalid
        depositId = depositPage.createDeposit(context, uiObj, paymentId)
        Thread.sleep(10000)
        String searchResult = executeSavedSearch(depositId as Long)
        def resultObj = new JsonSlurper().parseText(searchResult)

        //checkCollectedCFS("deposit", depositId, expectedObj)

        // Second time, create invoice, customer payment and deposit

        def caseId1 = "test case 1.15.11"
        def testDataObj1 = loadCFSTestData("cashflow//cn_cashflow_deposit_data.json", caseId1)
        def dataObj1 = testDataObj1[caseId1].data
        def uiObj1 = context.getUIDataObj(testDataObj1, '1.15.11_3')
        def expectedObj1 = testDataObj1[caseId1].expected

        response = createTransaction(caseId1, dataObj1)

        def responseObj1 = new JsonSlurper().parseText(response)
        def invoiceId1 = responseObj1[0].internalid
        def paymentId1 = responseObj1[1].internalid
        depositId = depositPage.createDeposit(context, uiObj1, paymentId1)
        Thread.sleep(10000)
        String searchResult1 = executeSavedSearch(depositId as Long)
        def resultObj1 = new JsonSlurper().parseText(searchResult1)

        //checkCollectedCFS("deposit", depositId, expectedObj1)
    }

    /**
     * 	Load saved search, add filter for Payment internalid, then execute search and return the results.
     * @param depositId - Payment internalid
     * @return
     */
    String executeSavedSearchforPayment(Long paymentId) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("var search=nlapiLoadSearch('customrecord_cn_cashflow_record_detail','customsearch_cn_cf_detail');")
                .append("var newFilter=new nlobjSearchFilter('custrecord_cfs_pymt_tranid',null,'is',").append(paymentId).append(");")
                .append("search.addFilter(newFilter);")
                .append("var results = search.runSearch().getResults(0,10) || [];")
                .append("var rtnResults = [];")
                .append("for(var i=0;i<results.length;i++) {")
                .append("  rtnResults.push({")
                .append("    'item': results[i].getText('custrecord_cfs_item'),")
                .append("    'amount': results[i].getValue('custrecord_cfs_amount'),")
                .append("    'depositId': results[i].getValue('custrecord_cfs_deposit_tranid'),")
                .append("    'pymtId': results[i].getValue('custrecord_cfs_pymt_tranid'),")
                .append("    'paidId': results[i].getValue('custrecord_cfs_paid_tranid')")
                .append("  });")
                .append("}")
                .append("return JSON.stringify(rtnResults);");
        String result = context.executeScript(sBuilder.toString());
        context.webDriver.waitForPageToLoad();
        return result;
    }
}