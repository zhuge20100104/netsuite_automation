package com.netsuite.chinalocalization.voucher

import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.common.NSError

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.CheckPage
import com.netsuite.chinalocalization.page.CustomerRefundPage
import com.netsuite.chinalocalization.page.Setup.AccountPreferencePage
import com.netsuite.chinalocalization.page.voucher.GLnumberingPage
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.common.P2
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonSlurper
import com.netsuite.common.OWAndSI
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P1
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import com.netsuite.testautomation.junit.runners.SuiteSetup

@TestOwner("Jingzhou.wang@oracle.com")
class VoucherGLVoidWithReversalTest extends VoucherPrintBaseTest {
    @Inject
    CheckPage checkPage
    @Inject
    CustomerRefundPage customerRefundPage
    @Inject
    GLnumberingPage gLnumberingPage
    @Inject
    JournalEntryPage journalEntryPage
    @Inject
    AccountPreferencePage accountPreferencePage
    @Inject
    ElementHandler elementHandler

    def jsonFile = "voucher//case_6_2.json"
    String tranType
    String internalId
    def dirtyData = []
    JsonSlurper slurper = new JsonSlurper()

    @Before
    void setUp() {
        super.setUp()
        accountPreferencePage.navigateToURL()
        accountPreferencePage.enableReversalVoiding()
    }

    @After
    void tearDown() {
        if (dirtyData) {
            for (int i = dirtyData.size() - 1; i >= 0; i--) {
                String type = dirtyData[i].trantype
                String id = dirtyData[i].internalid
                context.deleteTransaction(type, id)
            }
        }
        super.tearDown()
    }


    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 6.2.1
     * @desc 1. Create a check
     *           2. Void the check
     *           3. Run GL#
     *           4. Check Voucher Print report, the GL# should be continue
     */
    @Category([OWAndSI.class, P0.class])
    @Test
    void case_6_2_1() {
        //Create a check
        tranType = "check"
        def caseObj = getCaseObj(jsonFile, "test case 6.2.1")
        def response = createTransaction(jsonFile, "test case 6.2.1")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Void the check
        checkPage.navigateToEditURL(internalId)
        context.clickVoidByAPI()
        def journal = journalEntryPage.createVoidJournalEntry(caseObj.ui)
        dirtyData.add(journal)

        //Run GL#
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        expects.add(caseObj.expect.voucher2)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 2; i++) {
            glNoBase++
            String prefix = "GL2018Q1"
            String GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()
            if(isReportShow()) {
                actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))
                //Verify Header
                verifyReportHeader(expects[i], actualHeaders[i], GLNumber)
                //Verify Body
                verifyReportContent(expects[i])
            }
        }
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 6.2.2
     * @desc 1. Create a customer deposit and customer refund
     *           2. Void the customer refund
     *           3. Run GL#
     *           4. Check Voucher Print report, the GL# should be continue
     */
    @Category([OWAndSI.class, P1.class])
    @Test
    void case_6_2_2() {
        //Create a customer deposit and refund
        tranType = "customerdeposit"

        def caseObj = getCaseObj(jsonFile, "test case 6.2.2")
        def response = createTransaction(jsonFile, "test case 6.2.2")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[1].internalid
        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Void the check
        customerRefundPage.navigateToEditURL(internalId)
        context.clickVoidByAPI()
        context.clickSaveByAPI()

        //Run GL#
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        expects.add(caseObj.expect.voucher2)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 2; i++) {
            glNoBase++
            String prefix = "GL2018Q1"
            String GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()

            if(isReportShow()) {
                actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))

                //Verify Header
                verifyReportHeader(expects[i], actualHeaders[i], GLNumber)

                //Verify Body
                verifyReportContent(expects[i])
            }
        }
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 6.2.3
     * @desc 1. Create a check
     *           2. Run GL#
     *           3. Void the check
     *           4. Check Voucher Print report, the GL# should be continue
     */
    @Category([OWAndSI.class, P2.class])
    @Test
    void case_6_2_3() {
        //Create a check
        tranType = "check"

        def caseObj = getCaseObj(jsonFile, "test case 6.2.3")
        def response = createTransaction(jsonFile, "test case 6.2.3")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Void the check
        checkPage.navigateToEditURL(internalId)
        context.clickVoidByAPI()
        def journal = journalEntryPage.createVoidJournalEntry(caseObj.ui)
        dirtyData.add(journal)

        //Run GL#
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        expects.add(caseObj.expect.voucher2)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 2; i++) {
            glNoBase++
            String prefix = "GL2018Q1"
            String GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()
            if(isReportShow()) {
                actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))

                //Verify Header
                verifyReportHeader(expects[i], actualHeaders[i], GLNumber)

                //Verify Body
                verifyReportContent(expects[i])
            }
        }
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 6.2.4
     * @desc 1. Create a customer deposit and customer refund
     *           2. Void the customer refund
     *           3. Run GL#
     *           4. Check Voucher Print report, the GL# should be continue
     */
    @Category([OWAndSI.class, P2.class])
    @Test
    void case_6_2_4() {
        //Create a customer deposit and refund
        tranType = "customerdeposit"

        def caseObj = getCaseObj(jsonFile, "test case 6.2.4")
        def response = createTransaction(jsonFile, "test case 6.2.4")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[1].internalid
        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Void the check
        customerRefundPage.navigateToEditURL(internalId)
        context.clickVoidByAPI()
        context.clickSaveByAPI()

        //Run GL#
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        expects.add(caseObj.expect.voucher2)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 1; i++) {
            glNoBase++
            String prefix = "GL2018Q1"
            String GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()
            try {
                actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))
                if (isReportShow()) {
                    //Verify Header
                    verifyReportHeader(expects[i], actualHeaders[i], GLNumber)

                    //Verify Body
                    verifyReportContent(expects[i])
                }
            }
            catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 6.3.1
     * @desc Cost Re-evaluation
     */
    @Category([OWAndSI.class, P1.class])
    @Test
    void case_6_3_1() {
        tranType = "InvReval"
        def caseObj = getCaseObj(jsonFile, "test case 6.3.1")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 1; i++) {
            int glNoBase = 232
            String prefix = "GL2018Q1"
            String GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()
            Thread.sleep(5000)
            try {
                actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))

            }
            catch (Exception e) {
                e.printStackTrace()
            }

            if (isReportShow()) {
                //Verify Header
                verifyReportHeader(expects[i], actualHeaders[i], GLNumber)

                //Verify Body
                verifyReportContent(expects[i])
            }
        }
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 6.4.1
     * @desc 1. Create a journal
     *           2. Create a journal
     *           3. Run GL#
     *           4. Check Voucher Print report, the GL# should be continue
     */
    @Category([OW.class, P1.class])
    @Test
    void case_6_4_1() {
        //Create 2 journals
        def caseObj = getCaseObj(jsonFile, "test case 6.4.1")
        changeLanguageToEnglish()
        def response = createTransaction(jsonFile, "test case 6.4.1")
        changeLanguageToChinese()
        def responseObj = slurper.parseText(response)
        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Run GL#
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        expects.add(caseObj.expect.voucher2)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 2; i++) {
            glNoBase++
            String prefix = "GL2018Q1"
            String GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()

            if(isReportShow()) {
                actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))

                //Verify Header
                verifyReportHeader(expects[i], actualHeaders[i], GLNumber)

                //Verify Body
                verifyReportContent(expects[i])
            }
        }
    }


    void verifyReportHeader(def expect, def actualHeader, String baseglNumber) {
        def expectedHeader = expect.expectedHeader
        def siCompanyName = context.getContext().getProperty("testautomation.nsapp.default.account")
        if (context.isOneWorld()) {
            checkAreEqual("Please check Subsidiary", actualHeader.subsidiary, expectedHeader.subsidiary)
        } else {
            checkAreEqual("Please check Subsidiary", actualHeader.subsidiary, siCompanyName)
        }
        checkAreEqual("Please check glNumber", actualHeader.voucherNo, baseglNumber)
    }

//    //Check after click refresh button , if report show
//    boolean isReportShow() {
//        HtmlElementHandle report_header = elementHandler.waitForElementToBePresent(context.webDriver, "//table[@class='header']")
//        return (report_header != null)
//    }

    void verifyReportContent(def expectedDataObj) {
        if (expectedDataObj.expectedBody) {
            for (def expectedLineObj : expectedDataObj.expectedBody) {
                def reportLineObj = voucherPrintPage.getReportLineOnBody(0, expectedLineObj)
                if (expectedLineObj.linedesp) {
                    checkAreEqual("Line should show correctly", reportLineObj.acctDesp, expectedLineObj.acctDesp)
                }
                if (expectedLineObj.acctdesp) {
                    checkAreEqual("Account should show correctly", reportLineObj.acctDesp, expectedLineObj.acctDesp)
                }
                if (expectedLineObj.funcCcycredit) {
                    checkAreEqual("Credit value should show correctly", reportLineObj.funcCcyCredit, expectedLineObj.funcCcyCredit)
                }
                if (expectedLineObj.funcCcydebit) {
                    checkAreEqual("Debit value should show correctly", reportLineObj.funcCcyDebit, expectedLineObj.funcCcyDebit)
                }
            }
        }
    }
}