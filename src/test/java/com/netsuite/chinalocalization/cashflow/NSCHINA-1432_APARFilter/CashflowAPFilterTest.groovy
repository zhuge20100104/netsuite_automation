package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.page.CheckPage
import com.netsuite.chinalocalization.page.ExpenseReportpage
import com.netsuite.chinalocalization.page.TransactionBasePage
import com.netsuite.chinalocalization.page.VendorCreditPage
import com.netsuite.chinalocalization.page.VendorReturnAuthorizationPage
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.common.SI
import com.netsuite.common.P1
import com.netsuite.testautomation.enums.Page
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import com.netsuite.chinalocalization.page.VendorBillPage
import com.netsuite.chinalocalization.page.PurchaseOrderPage
import org.junit.experimental.categories.Category

@TestOwner("Jingzhou.wang@oracle.com")
class CashflowAPFilterTest extends CashflowBaseTest {

    @Inject
    protected PurchaseOrderPage purchaseOrderPage
    @Inject
    protected VendorBillPage vendorBillPage
    @Inject
    protected VendorCreditPage vendorCreditPage
    @Inject
    protected VendorReturnAuthorizationPage vendorReturnAuthorizationPage
    @Inject
    protected ExpenseReportpage expenseReportpage
    @Inject
    protected CheckPage checkPage


    static HashSet expectOutFlowItems
    static HashSet actualHeaderCFSItems
    static HashSet actualItemLineCFSItems
    static HashSet actualExpenseLineCFSItems

    def response = ""
    def responseObj = {}
    boolean delete = false

    @SuiteSetup
    void setUpTestSuite() {
        super.setUpTestSuite()
        expectOutFlowItems = getCFSItemFromJson("OUT")
    }

    @After
    void tearDown() {
        if (delete) {
            context.deleteTransaction(response)
        }
        delete = false
        super.tearDown()
    }

    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @CaseID Cashflow 1.45.1.1
     * @author jingzhou.wang@oracle.com
     * Description: Check CFS filter in Header and Item line of Purchase Order transaction
     */
    @Test
    @Category([OW.class, P0.class])
    void case_1_45_1_1() {
        purchaseOrderPage.navigateToURL()

        //Assert Header CFS item quantity and each cfs option
        verifyHeaderCFS(purchaseOrderPage)
    }

    /**
     * @CaseID Cashflow 1.45.1.2
     * @author jingzhou.wang@oracle.com
     * Description: Check CFS filter in Header and Item line of Vendor Bill transaction
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_45_1_2() {
        vendorBillPage.navigateToURL()

        //Assert CFS item quantity and each item in Header line
        verifyHeaderCFS(vendorBillPage)

        //Assert CFS item quantity and each item in Item line
        //Expense
        clickExpense()
        verifyExpenseCFS(vendorBillPage)

        //Item
        clickItem()
        //verifyItemCFS2(vendorBillPage)
    }

    /**
     * @CaseID Cashflow 1.45.1.3
     * @author jingzhou.wang@oracle.com
     * Description: Check CFS filter in Header and Item line of Vendor Credit transaction
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_45_1_3() {
        vendorCreditPage.navigateToURL()

        //Assert CFS item quantity and each item in Header line
        verifyHeaderCFS(vendorCreditPage)

        //Assert CFS item quantity and each item
        //Expense Tab
        vendorCreditPage.clickExpense()
        verifyExpenseCFS(vendorCreditPage)

        //Item Tab
        vendorCreditPage.clickItem()
        //verifyItemCFS2(vendorCreditPage)
    }

    /**
     * @CaseID Cashflow 1.45.1.4
     * @author jingzhou.wang@oracle.com
     * Description: Check CFS filter in Header and Item line of Vendor Return Authorization transaction
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_45_1_4() {
        vendorReturnAuthorizationPage.navigateToURL()

        //Assert CFS item quantity and each item
        verifyHeaderCFS(vendorReturnAuthorizationPage)

        //Assert CFS item quantity and each item in Expense line
        vendorReturnAuthorizationPage.clickExpense()
        verifyExpenseCFS(vendorReturnAuthorizationPage)

        //Item
        vendorReturnAuthorizationPage.clickItem()
        //verifyItemCFS2(vendorReturnAuthorizationPage)
    }

    /**
     * @CaseID Cashflow 1.45.1.5
     * @author jingzhou.wang@oracle.com
     * Description: Check CFS filter in Header and Item line of Expense Report transaction
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_45_1_5() {
        expenseReportpage.navigateToURL()

        //Assert CFS item quantity and each item
        verifyHeaderCFS(expenseReportpage)

        //Assert CFS item quantity and each item in Item line
        //Expense
        verifyExpenseCFS(expenseReportpage)

    }

    /**
     * @CaseID Cashflow 1.45.1.6
     * @author jingzhou.wang@oracle.com
     * Description: Check CFS filter in Header and Item line of Check transaction
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_45_1_6() {
        checkPage.navigateToURL()

        //Assert CFS item quantity and each item in Header line
        verifyHeaderCFS(checkPage)

        //Assert CFS item quantity and each item in Item line
        //Expense Tab
        checkPage.clickExpense()
        verifyExpenseCFS(checkPage)

        //Item Tab
        checkPage.clickItem()
        //verifyItemCFS2(checkPage)
    }

    /**
     * @CaseID Cashflow 1.45.1.7
     * @author jingzhou.wang@oracle.com
     * Description: Edit a transaction and check CFS filter
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_45_1_7() {
        //Create a transaction
        def caseId = "test case 1.45.1.7"
        def dataObj = getDataObj("cashflow//case_1_45_1_data.json", caseId)


        response = createTransaction(caseId, dataObj)
        responseObj = new JsonSlurper().parseText(response)
        String tranId = responseObj[0].internalid
        delete = true

        //Go to edit page
        vendorCreditPage.navigateToEditURL(tranId)

        verifyHeaderCFS(vendorCreditPage)

        //Assert CFS item quantity and each item
        vendorCreditPage.clickExpense()
        verifyExpenseCFS(vendorCreditPage)

        //Item Tab
        vendorCreditPage.clickItem()
        //verifyItemCFS2(vendorCreditPage)
    }

    /**
     * @CaseID Cashflow 1.45.1.8
     * @author jingzhou.wang@oracle.com
     * Description: Copy a transaction and check CFS filter
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_45_1_8() {
        //Create a transaction
        def caseId = "test case 1.45.1.8"
        def dataObj = getDataObj("cashflow//case_1_45_1_data.json", caseId)

        response = createTransaction(caseId, dataObj)
        responseObj = new JsonSlurper().parseText(response)
        String tranId = responseObj[0].internalid
        delete = true

        //Go to Copy page
        vendorCreditPage.navigateToCopyURL(tranId)
        verifyHeaderCFS(vendorCreditPage)

        //Assert CFS item quantity and each item
        //Expense Tab
        vendorCreditPage.clickExpense()
        verifyExpenseCFS(vendorCreditPage)

        //Item Tab
        vendorCreditPage.clickItem()
        //verifyItemCFS2(vendorCreditPage)
    }


    void assertCFS(HashSet expect, HashSet actual) {
        if (expect && actual) {
            def size_act = actual.size()
            def size_exp = expect.size()
            checkAreEqual("CFS item quantity is not match expected", size_act, size_exp)
            checkAreEqual("CFS items are not match expected", actual, expect)
        }
    }

    void verifyHeaderCFS(TransactionBasePage page) {
        actualHeaderCFSItems = page.getCNDropDownListOptions(page.FIELD_ID_HEADER_CFS)
        trimCFSOptions(actualHeaderCFSItems)
        assertCFS(expectOutFlowItems, actualHeaderCFSItems)
    }

    void verifyExpenseCFS(TransactionBasePage page) {
        actualExpenseLineCFSItems = page.getExpenseLineCFSOptions(page.FIELD_ID_EXPENSE_CFS)
        trimCFSOptions(actualExpenseLineCFSItems)
        assertCFS(expectOutFlowItems, actualExpenseLineCFSItems)
    }

    void verifyItemCFS(TransactionBasePage page) {
        actualItemLineCFSItems = page.getItemLineCFSOptions(page.FIELD_ID_ITEM_CFS)
        trimCFSOptions(actualItemLineCFSItems)
        assertCFS(expectOutFlowItems, actualItemLineCFSItems)
    }

    void verifyItemCFS2(TransactionBasePage page) {
        actualItemLineCFSItems = page.getItemLineCFSOptions2(page.FIELD_ID_ITEM_CFS)
        trimCFSOptions(actualItemLineCFSItems)
        assertCFS(expectOutFlowItems, actualItemLineCFSItems)
    }

}
