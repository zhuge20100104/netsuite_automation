package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.BankDepositPage
import com.netsuite.chinalocalization.page.CustomerDepositPage
import com.netsuite.common.OW
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("mia.wang@oracle.com")
class CashflowCustomerDepositTest extends CashflowBaseTest {
    @Inject
    protected BankDepositPage depositPage;
    @Inject
    CustomerDepositPage customerDepositPage;
    private static String LANGUAGE
    def response = ""
    def payment1 = {}
    def payment2 = {}

    private String createTransaction(String scriptId, String deployId, String fileName, String caseId) {
        String url = context.resolveURL(scriptId, deployId)
        Assert.assertFalse(url.equals("You have provided an invalid script id or internal id: customscript_sl_cn_ui_create_trans"))
        response = context.createTransaction(caseId, fileName, url)
        return response
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID Cashflow 1.32.2.2 Customer deposit apply multi Invoice
     */
    @Test
    @Category([OW, P3])
    void case_1_32_2_2() {
        //Set customer CN Automation Customer
        def caseString = context.getFileContent("test case 1.32.2.2", "cashflow//cn_cashflow_custdep_data.json")
        def jsonslurper = new JsonSlurper()
        def caseObj = jsonslurper.parseText(caseString)

        //Create customer payment 1
        try {
            response = createTransaction("customscript_sl_cn_ui_create_trans", "customdeploy_sl_cn_ui_create_trans", "cashflow//cn_cashflow_custdep_data.json", "test case 1.32.2.2")
            context.log.info(response)
            println response

            //Create customer payment1
            def uidata = context.getUIDataObj(caseObj, "1.32.2.2_5")
            System.out.println(uidata)
            payment1 = acceptCustomerPayment(caseObj, uidata)

            //Create customer payment 2
            uidata = context.getUIDataObj(caseObj, "1.32.2.2_6")
            payment2 = acceptCustomerPayment(caseObj, uidata)

            //Get expect result object
            def expectPayment1 = context.getExpectedObj(caseObj, "1.32.2.2_5")
            System.out.println(expectPayment1)
            def expectPayment2 = context.getExpectedObj(caseObj, "1.32.2.2_6")
            def expectCustDep = context.getExpectedObj(caseObj, "1.32.2.2_3")

            //Check customer payment 1 CFS detail
            checkCollectedCFS("customerpayment", payment1.recordName.toInteger(), expectPayment1[expectPayment1.keySet()[0]])
            //Check customer payment 2 CFS detail
            checkCollectedCFS("customerpayment", payment2.recordName.toInteger(), expectPayment2[expectPayment2.keySet()[0]])
            //Check customer deposit CFS detail
            def tranidDeposit = getInternalIdForRecordFromRefid(caseObj, "1.32.2.2_3", response).toInteger()
            checkCollectedCFS("customerdeposit", tranidDeposit, expectCustDep[expectCustDep.keySet()[0]])
        } catch (Exception ex) {
            log.error(ex.message)
            context.getExceptionFactory().createNSAppException("Case 1.32.2.2 failed due to", ex.message, true)
        } finally {
            deleteTransaction(response)
        }
    }


    /**
     * @Author mia.wang@oracle.com
     * @CaseID Cashflow 1.32.2.3 Customer deposit apply multinvoices
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_32_2_3() {
        //Set customer CN Automation Customer
        def caseString = context.getFileContent("test case 1.32.2.3", "cashflow//cn_cashflow_custdep_data.json")
        def jsonslurper = new JsonSlurper()
        def caseObj = jsonslurper.parseText(caseString)

        //Create customer payment 1
        try {
            //Create payment 1  id: 1.32.2.3_5
            response = createTransaction("customscript_sl_cn_ui_create_trans", "customdeploy_sl_cn_ui_create_trans", "cashflow//cn_cashflow_custdep_data.json", "test case 1.32.2.3")
            context.log.info(response)
            println response

            //Create customer payment1
            def uidata = context.getUIDataObj(caseObj, "1.32.2.3_5")
            payment1 = acceptCustomerPayment(caseObj, uidata)

            //Create customer payment 2
            uidata = context.getUIDataObj(caseObj, "1.32.2.3_6")
            payment2 = acceptCustomerPayment(caseObj, uidata)

            //Get expect result object
            def expectPayment1 = context.getExpectedObj(caseObj, "1.32.2.3_5")
            def expectPayment2 = context.getExpectedObj(caseObj, "1.32.2.3_6")
            def expectCustDep = context.getExpectedObj(caseObj, "1.32.2.3_3")

            //Check customer payment 1 CFS detail
            checkCollectedCFS("customerpayment", payment1.recordName.toInteger(), expectPayment1[expectPayment1.keySet()[0]])
            //Check customer payment 2 CFS detail
            checkCollectedCFS("customerpayment", payment2.recordName.toInteger(), expectPayment2[expectPayment2.keySet()[0]])
            //Check customer deposit CFS detail
            def tranidDeposit = getInternalIdForRecordFromRefid(caseObj, "1.32.2.3_3", response).toInteger()
            checkCollectedCFS("customerdeposit", tranidDeposit, expectCustDep[expectCustDep.keySet()[0]])
        } catch (Exception ex) {
            log.error(ex.message)
            context.getExceptionFactory().createNSAppException("Case 1.32.2.3 failed due to", ex.message, true)
        } finally {
            deleteTransaction(response)
        }
    }

    /**
     * @CaseID 1.23.2.2
     * @author lisha.hao@oracle.com
     * @description 1.Create Sales Order_1
     *               2.Create Customer Deposit _1
     *               3.Create Bank Deposit_1
     *               4.Wait a few seconds to check the Cash flow statement records are collected in the list of record type 'China Cash Flow Reconciliation'
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_23_2_2() {
        def caseId = "test case 1.23.2.2";
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_custdep_data.json", caseId);
        def dataObj = testDataObj[caseId].data;
        def dataObj2 = testDataObj[caseId].data2[0];
        def uiObj = context.getUIDataObj(testDataObj, '1.23.2.2_3');

        response = createTransaction(caseId, dataObj);
        def responseObj = new JsonSlurper().parseText(response);
        def salesOrderId = responseObj[0].internalid;
        context.navigateTo(CURL.HOME_CURL)
        switchToRole(administrator)
        salesOrderPage.navigateTo(salesOrderId)
        salesOrderPage.clickApprove()
        salesOrderPage.clickCreateDeposit()

        def paymentId = createCustomerDeposit(dataObj2)
        def depositId = depositPage.createDeposit(context, uiObj, paymentId);

        responseObj += [internalid: depositId, trantype: "deposit"]
        response = JsonOutput.toJson(responseObj)
        Thread.sleep(10000);
        String searchResult = executeSavedSearch(depositId as Long);
        def resultObj = new JsonSlurper().parseText(searchResult);

        Assert.assertEquals("Number of lines collected is not correct", 1, resultObj.size);
        Assert.assertEquals("Payment Transaction internalid is not correct", paymentId as Long, resultObj[0].pymtId as Long);
        deleteTransaction(response)
    }

    /**
     * @author lisha.hao
     * @param dataObj
     * @return paymentId
     * @description this function is to create a Customer Deposit via UI
     */
    def long createCustomerDeposit(def dataObj) {
        customerDepositPage.setDate(dataObj.main.trandate)
        customerDepositPage.setExchangeRate(dataObj.main.exchangerate)
        customerDepositPage.setPaymentAmount(dataObj.main.payment)
        customerDepositPage.setCFSItem(dataObj.main.custbody_cseg_cn_cfi)
        context.waitForPageToLoad()
        customerDepositPage.clickSave()
        def internalId = context.getParameterValueForFromQueryString("id");
        return Long.parseLong(internalId);
    }
}
