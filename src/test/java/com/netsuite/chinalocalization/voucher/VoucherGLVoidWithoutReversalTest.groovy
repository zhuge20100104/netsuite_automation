package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.cashflow.CashflowBaseTest
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.NSError

import com.google.inject.Inject
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.InvoicePage
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.AccountPreferencePage
import com.netsuite.chinalocalization.page.VendorBillPage
import com.netsuite.chinalocalization.page.voucher.GLnumberingPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.openqa.selenium.Alert

@TestOwner("yang.g.liu@oracle.com & lisha.hao@oracle.com")
class VoucherGLVoidWithoutReversalTest extends VoucherPrintBaseTest {
    @Inject
    InvoicePage invoicePage
    @Inject
    VendorBillPage billPage
    @Inject
    GLnumberingPage gLnumberingPage
    @Inject
    JournalEntryPage journalEntryPage
    @Inject
    AccountPreferencePage accountPreferencePage
    @Inject
    CashflowBaseTest cashflowBaseTest

    def jsonFile = "voucher//case_6_1.json"
    String tranType
    String internalId
    def dirtyData = []

    JsonSlurper slurper = new JsonSlurper()

    //Something is wrong if change reversalVoiding in SuiteSetup, so move it to case setup
    @Before
    void setUp() {
        super.setUp()
        if (nconfig.isReversalVoiding()) {
            accountPreferencePage.navigateToURL()
            accountPreferencePage.disbleReversalVoiding()
        }
    }

    @After
    void tearDown() {
        if (dirtyData) {
            dirtyData.each {
                String type = it.trantype
                String id = it.internalid
                context.deleteTransaction(type, id)
            }
        }
        super.tearDown()
    }

    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @CaseID Cashflow 6.1.1 GL # continuity
     * @author yang.g.liu@oracle.com
     * Description:
     *      Void without reversal journal
     *          0.Pre setup(6. GL # continuity all case need follow the setup ):
     *            a.Subsidiary:China GL BU :  GL Impact Locking:checked
     *            b.Accounting Preferences->Void Transactions Using Reversing Journals :unchecked
     *            c.GL#  rule:GL_2018_Q1
     *            d.CN GL Automation Customer
     *            e.CN GL Automation Vendor
     *            f.Do not delete the transaction data
     *          1. Create a Bill, trandate: 01/01/2018.
     *          2. Viod Bill,trandate: 01/01/2018.
     *          3.Run GL# :GL_2018_Q1
     *          4.Navigate to Voucher printing page,  search
     *          Date From:01/01/2018  To:01/01/2018,Click 'Refresh' button
     *
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void case_6_1_1() {
        // Create Bill
        def caseObj = getCaseObj(jsonFile, "test case 6.1.1")
        def response = createTransaction(jsonFile, "test case 6.1.1")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        tranType = responseObj[0].trantype
        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Void the Bill
        billPage.navigateToEditURL(internalId)
        context.clickVoidByAPI()
        sleep(8000)
        def alterHandler = new AlertHandler()
        Alert alert = alterHandler.waitForAlertToBePresent(context.webDriver, 10)
        if (alert != null) {
            alert.accept()
        }

        //Run GL#:GL_2018_Q1
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Navigate to Voucher printing page,Bill should be displayed in voucher and all its lines are zero.
        voucherPrintPage.navigateToURL()
        String prefix = "GL2018Q1"
        String GLNumber = composeGLNumber(prefix, 6, glNoBase + 1)
        voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
        voucherPrintPage.clickRefreshBtn()
        sleep(5000)

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        if (isReportShow()) {
            actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))
            verifyReportHeader(expects[0], actualHeaders[0], GLNumber)
            verifyReportContent(expects[0])
        }
    }

    /**
     * @CaseID Cashflow 6.1.2 GL # continuity
     * @author yang.g.liu@oracle.com
     * Description:
     *      Void without reversal journal
     *      1. Create a Invoice, trandate: 01/02/2018.
     *      2. Viod Invoice,trandate: 01/02/2018.
     *      3.Run GL# :GL_2018_Q1
     *      4.Navigate to Voucher printing page,  search Date From:01/02/2018 To:01/02/2018,Click 'Refresh' button
     *
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void case_6_1_2() {
        // Create Invoice
        def caseObj = getCaseObj(jsonFile, "test case 6.1.2")
        def response = createTransaction(jsonFile, "test case 6.1.2")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        tranType = responseObj[0].trantype

        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Void the Invoice
        invoicePage.navigateToEditURL(internalId)
        context.clickVoidByAPI()
        sleep(8000)
        def alterHandler = new AlertHandler()
        Alert alert = alterHandler.waitForAlertToBePresent(context.webDriver, 10)
        if (alert != null) {
            alert.accept()
        }

        //Run GL#:GL_2018_Q1
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Navigate to Voucher printing page,Invoice should be displayed in voucher and all its lines are zero.
        voucherPrintPage.navigateToURL()
        String prefix = "GL2018Q1"
        String GLNumber = composeGLNumber(prefix, 6, glNoBase + 1)
        voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
        voucherPrintPage.clickRefreshBtn()
        sleep(5000)

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        if(isReportShow()) {

            actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))
            verifyReportHeader(expects[0], actualHeaders[0], GLNumber)
            verifyReportContent(expects[0])
        }
    }

    /**
     * @CaseID Cashflow 6.1.3 GL # continuity
     * @author yang.g.liu@oracle.com
     * Description:
     *      Void without reversal journal
     *      1. Create a Bill, trandate: 01/03/2018.
     *      2.Run GL# :GL_2018_Q1
     *      3. Viod Bill,trandate: 01/03/2018.
     *      4.Run GL# :GL_2018_Q1
     *      5.Navigate to Voucher printing page, search Date From:01/03/2018 To:01/03/2018,Click 'Refresh' button
     *
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void case_6_1_3() {
        // Create Bill
        def caseObj = getCaseObj(jsonFile, "test case 6.1.3")
        def response = createTransaction(jsonFile, "test case 6.1.3")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        tranType = responseObj[0].trantype
        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Run GL#:GL_2018_Q1
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Void the Bill
        billPage.navigateToEditURL(internalId)
        context.clickVoidByAPI()
        sleep(8000)
        def alterHandler = new AlertHandler()
        Alert alert = alterHandler.waitForAlertToBePresent(context.webDriver, 10)
        if (alert != null) {
            alert.accept()
        }

        //Run GL#:GL_2018_Q1
        int glNoBase1 = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        expects.add(caseObj.expect.voucher2)
        expects.add(caseObj.expect.voucher3)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 3; i++) {
            glNoBase++
            String prefix = "GL2018Q1"
            String GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()
            sleep(5000)

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
     * @CaseID Cashflow 6.1.4 GL # continuity
     * @author yang.g.liu@oracle.com
     * Description:
     *      Void without reversal journal
     *          1. Create a Invoice, trandate: 01/04/2018.
     *          2.Run GL# :GL_2018_Q1
     *          3. Viod Invoice,trandate: 01/04/2018.
     *          4.Run GL# :GL_2018_Q1
     *          5.Navigate to Voucher printing page, search Date From:01/04/2018  To:01/04/2018,Click 'Refresh' button
     *
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void case_6_1_4() {
        // Create Invoice
        def caseObj = getCaseObj(jsonFile, "test case 6.1.4")
        def response = createTransaction(jsonFile, "test case 6.1.4")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        tranType = responseObj[0].trantype

        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Run GL#:GL_2018_Q1
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Void the Invoice
        invoicePage.navigateToEditURL(internalId)
        context.clickVoidByAPI()
        sleep(8000)
        def alterHandler = new AlertHandler()
        Alert alert = alterHandler.waitForAlertToBePresent(context.webDriver, 10)
        if (alert != null) {
            alert.accept()
        }

        //Run GL#:GL_2018_Q1
        int glNoBase1 = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        expects.add(caseObj.expect.voucher2)
        expects.add(caseObj.expect.voucher3)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 3; i++) {
            glNoBase++
            String prefix = "GL2018Q1"
            String GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()
            sleep(5000)
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
     * @CaseID Cashflow 6.1.5 GL # continuity
     * @author yang.g.liu@oracle.com
     * Description:
     *      Void without reversal journal
     *      1. Create a Bill, trandate: 01/05/2018.
     *      2.Run GL# :GL_2018_Q1
     *      3. Edit Bill:Amount: 0,trandate: 01/05/2018.
     *      4.Run GL# :GL_2018_Q1
     *      5.Navigate to Voucher printing page, search Date From:01/05/2018 To:01/05/2018,Click 'Refresh' button
     *
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void case_6_1_5() {
        // Create Bill
        def caseObj = getCaseObj(jsonFile, "test case 6.1.5")
        def response = createTransaction(jsonFile, "test case 6.1.5")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        tranType = responseObj[0].trantype
        responseObj.each {
            dirtyData.add(["trantype": it.trantype, "internalid": it.internalid])
        }

        //Run GL#:GL_2018_Q1
        int glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Void the Bill
        billPage.navigateToEditURL(internalId)
        def bill = editBillAmountToZero(context, 1, "amount", "0")
        dirtyData.add(bill)

        //Run GL#:GL_2018_Q1
        int glNoBase1 = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expects = []
        def actualHeaders = []
        expects.add(caseObj.expect.voucher1)
        expects.add(caseObj.expect.voucher2)
        expects.add(caseObj.expect.voucher3)

        voucherPrintPage.navigateToURL()
        for (int i = 0; i < 3; i++) {
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
     * @CaseID 6.1.6 GL # continuity
     * @author lisha.hao@oracle.com
     * @desc   6.1.6_InvoiceEdit
     *       1.Create a Invoice, trandate: 01/06/2018.
     *       2.Run GL# :GL_2018_Q1
     *       3.Edit Invoice: Amount:0,trandate: 01/06/2018.
     *       4.Run GL# :GL_2018_Q1
     *       5.Navigate to Voucher printing page, search Date From:01/06/2018 To:01/06/2018, click 'Refresh' button.
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void "case_6_1_6"() {
        //Create an invoice
        tranType = "invoice"
        switchToRole(administrator)
        def caseObj = getCaseObj(jsonFile, "test case 6.1.6")
        def response = createTransaction(jsonFile, "test case 6.1.6")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        tranType = responseObj[0].trantype
        dirtyData.add(["trantype": tranType, "internalid": internalId])

        //Run GL#:GL_2018_Q1
        def glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Edit invoice
        invoicePage.navigateToEditURL(internalId)

        def invoice = editInvoiceZero(context, 1, "quantity", "0")
        dirtyData.add(invoice)

        //Run GL#:GL_2018_Q1
        def glNoBase2 = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Verify
        def expectHeaders = []
        expectHeaders.add(caseObj.expect.voucher1)
        expectHeaders.add(caseObj.expect.voucher2)
        expectHeaders.add(caseObj.expect.voucher3)
        verifyGLNO(caseObj, glNoBase, 3, expectHeaders)
    }

    /**
     * @CaseID 6.1.7 GL # continuity
     * @author lisha.hao@oracle.com
     * @desc 6.1.7_BillDelete
     *       1.Create a Bill, trandate: 01/07/2018.
     *       2.Run GL# :GL_2018_Q1
     *       3.Delete Bill,trandate: 01/07/2018.
     *       4.Run GL# :GL_2018_Q1
     *       5.Navigate to Voucher printing page, search Date From:01/07/2018 To:01/07/2018, click 'Refresh' button
     */
    @Test
    @Category([OWAndSI.class, P2.class])
    void "case_6_1_7"() {
        //Create a bill
        tranType = "bill"
        switchToRole(administrator)
        def caseObj = getCaseObj(jsonFile, "test case 6.1.7")
        def response = createTransaction(jsonFile, "test case 6.1.7")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        tranType = responseObj[0].trantype
        dirtyData.add(["trantype": tranType, "internalid": internalId])

        //Run GL#:GL_2018_Q1
        def glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Delete bill
        context.deleteTransaction(tranType, internalId)

        //Run GL#:GL_2018_Q1
        def glNoBase2 = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")
        //Verify
        def expectHeaders = []
        expectHeaders.add(caseObj.expect.voucher1)
        expectHeaders.add(caseObj.expect.voucher2)
        verifyGLNO(caseObj, glNoBase, 2, expectHeaders)
    }

    /**
     * @CaseID 6.1.8 GL # continuity
     * @author lisha.hao@oracle.com
     * @desc 6.1.8_InvoiceDelete
     *       1.Create a Invoice, trandate: 01/08/2018.
     *       2.Run GL# :GL_2018_Q1
     *       3.Delete Invoice,trandate: 01/08/2018.
     *       4.Run GL# :GL_2018_Q1
     *       5.Navigate to Voucher printing page, search Date From:01/08/2018 To:01/08/2018, click 'Refresh' button
     */
    @Test
    @Category([OWAndSI.class, P2.class])
    void case_6_1_8() {
        //Create an invoice
        tranType = "invoice"
        switchToRole(administrator)
        def caseObj = getCaseObj(jsonFile, "test case 6.1.8")
        def response = createTransaction(jsonFile, "test case 6.1.8")
        def responseObj = slurper.parseText(response)
        internalId = responseObj[0].internalid
        tranType = responseObj[0].trantype
        dirtyData.add(["trantype": tranType, "internalid": internalId])

        //Run GL#:GL_2018_Q1
        def glNoBase = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")

        //Delete invoice
        context.deleteTransaction(tranType, internalId)

        //Run GL#:GL_2018_Q1
        def glNoBase2 = gLnumberingPage.runGLNumber(getLabel("Quarter"), "Q1 2018  (Standard Fiscal Calendar)")
        //Verify
        def expectHeaders = []
        expectHeaders.add(caseObj.expect.voucher1)
        expectHeaders.add(caseObj.expect.voucher2)
        verifyGLNO(caseObj, glNoBase, 2, expectHeaders)
    }
}