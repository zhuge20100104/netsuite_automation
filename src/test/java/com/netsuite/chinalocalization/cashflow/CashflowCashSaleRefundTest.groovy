package com.netsuite.chinalocalization.cashflow

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

import java.util.concurrent.TimeUnit

@TestOwner("stephen.zhou@oracle.com")
class CashflowCashSaleRefundTest extends CashflowBaseTest {
    def response = ""
    String SELECTED = "收到的税费返还"

    /**
     * @Author stephen.zhou@oracle.com
     * @CaseID Cashflow 1.19.2.3
     */
    @Test
    @Category([OW.class, P0.class])
    void case_1_19_2_3() {
        // Prepare test data
        def caseId = "test case 1.19.2.3"
        def testDataObj = loadCFSTestData("cashflow//case_1_19_2_3_data.json", caseId)
        def dataObj = testDataObj[caseId].data

        // Create a cash sale transaction
        response = createTransaction(caseId, dataObj)

        // Verify the cash flow record details
        verifyCashFlowDetails()
    }

    def verifyCashFlowDetails() {
        try {
            context.navigateTo("/app/accounting/transactions/transactionlist.nl?Transaction_TYPE=CashSale&whence=")
            waitForShort()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def refundDepositedCaseSale() {
        context.navigateTo("/app/accounting/transactions/transactionlist.nl?Transaction_TYPE=CashSale&whence=")
        getElement("//*[@id='row0']/td[1]/a[2]").click()
        getElement("//*[@id='refund']").click()

        getElement("//*[@name='inpt_custbody_cseg_cn_cfi']").click()
        getSpecifiedElement("//*[@class='dropdownNotSelected']").click()
        getElement("//*[@id='spn_multibutton_submitter']").click()
    }

    def createDepositeFromUI() {
        context.navigateTo(CURL.BANK_DEPOSIT_CURL)
        getElement("//*[@name='inpt_paymentrange']").click()
        getElement("//*[@id='nl5']").click()
        clickLastElement("//*[@onclick='NLCheckboxOnClick(this);']")
        getElement("//*[@id='spn_secondarymultibutton_submitter']").click()
    }

    def waitForShort() {
        TimeUnit.SECONDS.sleep(2)
    }

    def clickLastElement(String xPath) {
        List<HtmlElementHandle> list = context.webDriver.getHtmlElementsByLocator(Locator.xpath(xPath))
        list.get(list.size() - 1).click()
    }

    HtmlElementHandle getSpecifiedElement(String xPath) {
        List<HtmlElementHandle> dropDownList = context.webDriver.getHtmlElementsByLocator(Locator.xpath(xPath))
        for (HtmlElementHandle handler : dropDownList) {
            if (handler.getAsText().equalsIgnoreCase(SELECTED)) {
                return handler
            }
        }
        return null
    }

    HtmlElementHandle getElement(String xPath) {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(xPath))
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