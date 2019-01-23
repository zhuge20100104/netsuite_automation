package com.netsuite.chinalocalization.cashflow

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.common.OW
import com.netsuite.common.P3
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("jingzhou.wang@oracle.com")
class CashflowVerifyInvoiceTest extends CashflowBaseTest {
    def response = ""

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID Cashflow 1.33.2.2
     * @desc Create transaction with CFS in header and promotion in item
     */
    @Category([OW, P3])
    @Test
    void case_1_33_2_2() {

        // Prepare test data
        def caseId = "test case 1.33.2.2"
        def testDataObj = loadCFSTestData("cashflow//case_1_33_data.json", caseId)
        def dataObj = testDataObj[caseId].data

        response = createTransaction(caseId, dataObj)
        def responseObj = new JsonSlurper().parseText(response)

        Thread.sleep(20000)
        context.navigateTo(CURL.HOME_CURL)

        //context.navigateTo(CURL.HOME_CURL)
        checkCollectedCFS(responseObj[0].trantype as String, responseObj[0].internalid as long, testDataObj[caseId].expect);
        checkCollectedCFS(responseObj[1].trantype as String, responseObj[1].internalid as long, testDataObj[caseId].expect);
    }

    /**
     * @Author jingzhou.wang@oracle.com
     * @CaseID Cashflow 1.33.2.4
     * @desc Create transaction with CFS in header and discount in item
     */
    @Category([OW, P3])
    @Test
    void case_1_33_2_4() {

        // Prepare test data
        def caseId = "test case 1.33.2.4"
        def testDataObj = loadCFSTestData("cashflow//case_1_33_data.json", caseId)
        def dataObj = testDataObj[caseId].data

        response = createTransaction(caseId, dataObj)
        def responseObj = new JsonSlurper().parseText(response)

        Thread.sleep(20000)
        context.navigateTo(CURL.HOME_CURL)

        //context.navigateTo(CURL.HOME_CURL)
        checkCollectedCFS(responseObj[0].trantype as String, responseObj[0].internalid as long, testDataObj[caseId].expect);
        checkCollectedCFS(responseObj[1].trantype as String, responseObj[1].internalid as long, testDataObj[caseId].expect);
    }


    @Override
    def getDefaultRole() {
        getAdministrator()
    }

    @After
    void tearDown() {
        if (response) {
            deleteTransaction(response)
        }
        super.tearDown()
    }
}