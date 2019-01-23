package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.BankDepositPage
import com.netsuite.chinalocalization.page.StatementChargePage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("mia.wang@oracle.com")
class CashflowStatementChargeTest extends CashflowBaseTest {

    @Inject
    protected BankDepositPage depositPage;
    @Inject
    protected StatementChargePage statementChargePage;

    def response = "";
    def responseObj = {};
    def depositId = null;
    def statementChargeId1 = "";
    def statementChargeId2 = "";

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * Payment for Statement Charge(Undeposit->Deposit)
     * @Author mia.wang@oracle.com
     * @CaseID Cashflow 1.41.1.1 Statement Charge Test
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_41_1_1() {
        def caseId = "test case 1.41.1.1";
        def caseString = context.getFileContent("test case 1.41.1.1", "cashflow//cn_cashflow_statementcharge_data.json");
        def jsonslurper = new JsonSlurper();
        def caseObj = jsonslurper.parseText(caseString);
        def dataObj = caseObj[caseId].data;
        try {
            response = createTransaction(caseId, dataObj);
            context.log.info(response);
            println response;
            responseObj = jsonslurper.parseText(response);
            def paymentId = responseObj[responseObj.size - 1].internalid;

            //Create customer payment
            def uidata = context.getUIDataObj(caseObj, "CFS1.41.1.1_3");
            //The last element in response is customer payment
            depositId = depositPage.createDeposit(context, uidata, responseObj[responseObj.size - 1].internalid);

            //Check customer payment CFS detail
            //Get expect result object
            def expectPayment = context.getExpectedObj(caseObj, "CFS1.41.1.1_2");
            checkCollectedCFS("customerpayment", paymentId, expectPayment[expectPayment.keySet()[0]]);
        } catch (ex) {
            context.log.error(ex.message);
        }
    }

    /**
     * @desc Payment two Statement Charges and two Credit Memos together.
     *        One statement charge will be fully applied by credit memo, so the collected result may vary.
     * @Author kim.shi@oracle.com
     * @CaseID Cashflow 1.41.1.1 Statement Charge Test
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_41_2_1() {
        def caseId = "test case 1.41.2.1";
        def caseString = context.getFileContent(caseId, "cashflow//cn_cashflow_statementcharge_data.json");
        def caseObj = new JsonSlurper().parseText(caseString);

        def dataObj = caseObj[caseId].data;
        response = createTransaction(caseId, dataObj);
        responseObj = new JsonSlurper().parseText(response);

        def paymentId = responseObj.internalid[4];
        statementChargeId1 = responseObj.internalid[0];
        statementChargeId2 = responseObj.internalid[1];

        context.navigateTo(CURL.HOME_CURL);
        Thread.sleep(10000);

        def actualAppliedLine = getActualAppliedTo('customerpayment', paymentId as String);
        if (actualAppliedLine[0].internalid == statementChargeId1) {
            actualAppliedLine[0].item = dataObj[0].main.custbody_cseg_cn_cfi;
        } else {
            actualAppliedLine[0].item = dataObj[1].main.custbody_cseg_cn_cfi;
        }
        actualAppliedLine[0].trandate = dataObj[4].main.trandate;
        actualAppliedLine[0].postingperiod = 'FY 2018 : Q1 2018 : Jan 2018';

        // Need to wait for scheduled task to be completed
        Thread.sleep(30000);
        def actualCollectedCFSObj = new JsonSlurper().parseText(getCollectedCFS('customerpayment', paymentId as Long)).result;

        context.navigateTo(CURL.HOME_CURL);
        def matchFound = false;
        for (int i = 0; i < actualCollectedCFSObj.size(); i++) {
            print(actualCollectedCFSObj[i]);
            if (isMatched(actualAppliedLine[0], actualCollectedCFSObj[i])) {
                matchFound = true;
                break;
            }
        }
        Assert.assertTrue("Expected Line should be 100", matchFound);
    }

    /**
     * Get applied information for customer payment.
     */
    private def getActualAppliedTo(String tranType, String paymentId) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("var paymentRec=nlapiLoadRecord('" + tranType + "','" + paymentId + "');")
                .append("var applyLineCount=paymentRec.getLineItemCount('apply');")
                .append("var rtnResults = [];")
                .append("for (var i = 0; i < applyLineCount; i++) {")
                .append("  var apply=paymentRec.getLineItemValue('apply','apply',i);")
                .append("  if(apply==='T'){")
                .append("    rtnResults.push({")
                .append("      'internalid': paymentRec.getLineItemValue('apply','internalid',i),")
                .append("      'amount': paymentRec.getLineItemValue('apply','amount',i),")
                .append("      'item': '',")
                .append("      'trandate': '',")
                .append("      'postingperiod': ''")
                .append("    });")
                .append("  }")
                .append("}")
                .append("return JSON.stringify(rtnResults);");
        String result = context.executeScript(sBuilder.toString());

        return new JsonSlurper().parseText(result);
    }
}
