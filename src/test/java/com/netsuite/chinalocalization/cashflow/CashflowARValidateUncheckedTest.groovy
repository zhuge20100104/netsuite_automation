package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.NCurrentRecord
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.page.CreditMemoPage
import com.netsuite.chinalocalization.page.CustomerDepositPage
import com.netsuite.chinalocalization.page.CustomerRefundPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After
import com.netsuite.testautomation.junit.runners.SuiteSetup
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("jingzhou.wang@oracle.com&lisha.hao@oracle.com")
class CashflowARValidateUncheckedTest extends CashflowBaseTest {

    @Inject
    protected CreditMemoPage creditMemoPage
    @Inject
    protected CustomerDepositPage customerDepositPage
    @Inject
    protected CustomerRefundPage customerRefundPage
    @Inject
    protected NRecord record
    @Inject
    protected NCurrentRecord currentRecord

    def internalId = ""
    def tranType = ""
    def jsonFile = "cashflow/case_1_52.json"

    def getDefaultRole() {
        return getAdministrator()
    }

    @Before
    void setUp() {
        super.setUp()
        disableCFSMandatory("China BU")
    }

    @After
    void tearDown() {
        if (context.isPopUpPresent()) {
            context.closePopUp()
        }
        if (internalId != "") {
            deleteTransaction(tranType, internalId,)
        }
        super.tearDown()
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.1.2.1
     * @desc When CFS mandatory check box unchecked, CFS empty will not be validated when saving transaction
     */
    @Category([OWAndSI.class, P1.class])
    @Test
    void case_1_52_1_2_1() {
        tranType = "creditmemo"
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.1.2.1")

        // Fill the fields in create Credit Memo page
        creditMemoPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels)
        context.clickSaveByAPI()

        internalId = context.getRecordIdbyAPI()
        // The journal can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.2.2.1
     * @desc
     * 1. Create a Customer Deposit with item CFS is blank
     *          2. Save
     *          3. Can be save because CFS mandatory validation is OFF
     */
    @Category([OWAndSI.class, P2.class])
    @Test
    void case_1_52_2_2_1() {
        tranType = "customerdeposit"

        def caseObj = getCaseObj(jsonFile, "testcase_1.52.2.2.1")
        // Fill the fields in create Customer Deposit page
        customerDepositPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels)
        context.clickSaveByAPI()
        context.clickSaveByAPI()

        //internalId = context.getParameterValueForFromQueryString("id")
        internalId = context.getRecordIdbyAPI()
        println "internalId : " + internalId
        // The Customer Deposit can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.3.2.1
     * @desc
     * 1. Create a Customer Refund with item CFS is blank
     *          2. Save
     *          3. Can be save because CFS mandatory validation is OFF
     */
    @Category([OWAndSI.class, P3.class])
    @Test
    void case_1_52_3_2_1() {
        tranType = "customerrefund"
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.3.2.1")
        //switchToRole(getAdministrator())
        // Create a customer deposit
        customerDepositPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels[0])
        context.clickSaveByAPI()
        context.clickSaveByAPI()

        // Fill the fields in create Customer Refund page
        customerRefundPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels[1])
        context.clickSaveByAPI()
        internalId = context.getRecordIdbyAPI()
        assertTranIdNotNull(internalId)
    }

    /**
     * @CaseID Cashflow 1.52.4.4
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' is blank.
     *          1. New an Invoice
     *          2. Save
     *          3. Error message will popup
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    public void case_1_52_4_4() {
        // Create Invoice.
        setTranCurrentRecord("test case 1.52.4.1", "cashflow//case_1_50_4_data.json", CURL.INVOICE_CURL)
        internalId = context.getParameterValueForFromQueryString("id")
        tranType = "invoice"
        // Invoice can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @CaseID Cashflow 1.52.5.4
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' is blank.
     *          1. New a Cash Sale
     *          2. Save
     *          3. Cash Sale can be saved successfully
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    public void case_1_52_5_4() {
        // Create Cash Sale.
        setTranCurrentRecord("test case 1.52.5.1", "cashflow//case_1_50_4_data.json", CURL.CASH_SALE_CURL)
        internalId = context.getRecordIdbyAPI()
        tranType = "cashsale"
        // Cash Sale can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @CaseID Cashflow 1.52.6.3
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' is blank.
     *          1. New a Cash Refund
     *          2. Save
     *          3. Cash Refund can be saved successfully
     */
    @Test
    @Category([OWAndSI.class, P2.class])
    public void case_1_52_6_3() {
        // Create Cash Refund.
        setTranCurrentRecord("test case 1.52.6.1", "cashflow//case_1_50_4_data.json", CURL.CASH_REFUND_CURL)
        internalId = context.getRecordIdbyAPI()
        tranType = "cashrefund"
        // Cash Refund can be saved successfully.
        assertTranIdNotNull(internalId)
    }
}