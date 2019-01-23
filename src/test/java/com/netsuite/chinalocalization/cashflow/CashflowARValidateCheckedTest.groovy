package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.NCurrentRecord
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.page.CreditMemoPage
import com.netsuite.chinalocalization.page.CustomerDepositPage
import com.netsuite.chinalocalization.page.CustomerRefundPage
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After
import com.netsuite.common.OWAndSI
import com.netsuite.common.CFSMandatory
import com.netsuite.common.P1
import com.netsuite.common.P2
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.experimental.categories.Category

import org.junit.Test

@TestOwner("jingzhou.wang & lisha.hao")
class CashflowARValidateCheckedTest extends CashflowBaseTest {

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

    def response = ""
    def internalId = ""
    def tranType = ""
    def jsonFile = "cashflow/case_1_52.json"

    def getDefaultRole() {
        return getAdministrator()
    }

    @Before
    void setUp() {
        super.setUp()
        enableCFSMandatory("China BU")
    }

    @After
    void tearDown() {
        if (context.isPopUpPresent()) {
            context.closePopUp()
        }
        if (internalId != "") {
            context.deleteTransaction(tranType, internalId)
        }
        super.tearDown()
    }

    @SuiteTeardown
    void tearDownTestSuite() {
        try {
            disableCFSMandatory("China BU")
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        super.tearDownTestSuite()
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.1.1.1
     * @desc
     * 1. Create a Credit memo
     *          2. Save
     *          3. Cannot be save because CFS is blank
     */
    @Category([OWAndSI, P1, CFSMandatory])
    @Test
    void case_1_52_1_1_1() {
        tranType = "creditmemo"
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.1.1.1")
        // Fill the fields in create Credit Memo page
        creditMemoPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels)
        context.clickSaveByAPI()

        //Cannot save , error message show
        assertErrorMsg()
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.1.1.2
     * @desc
     * 1. Create a Credit memo which item type is discount
     *          2. Save
     *          3. Can be save because discount item CFS will not be validated
     */
    @Category([OWAndSI, P2, CFSMandatory])
    @Test
    void case_1_52_1_1_2() {
        tranType = "creditmemo"
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.1.1.2")
        // Fill the fields in create Credit Memo page
        creditMemoPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels)
        context.clickSaveByAPI()

        internalId = context.getRecordIdbyAPI()
        // The Credit memo can be saved successfully when discount item CFS is blank.
        assertTranIdNotNull(internalId)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.1.1.3
     * @desc
     * 1. Create a Credit memo which item type is markup
     *          2. Save
     *          3. Can be save because markup item CFS will not be validated
     */
    @Category([OWAndSI, P1, CFSMandatory])
    @Test
    void case_1_52_1_1_3() {
        tranType = "creditmemo"
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.1.1.3")

        // Fill the fields in create Credit Memo page
        creditMemoPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels)
        context.clickSaveByAPI()

        internalId = context.getRecordIdbyAPI()
        // The Credit memo can be saved successfully when markup item CFS is blank.
        assertTranIdNotNull(internalId)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.1.1.4
     * @desc
     * 1. Create a Credit memo with item CFS is non-blank
     *          2. Save
     *          3. Can be save
     */
    @Category([OWAndSI, P3, CFSMandatory])
    @Test
    void case_1_52_1_1_4() {
        tranType = "creditmemo"
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.1.1.4")

        // Fill the fields in create Credit Memo page
        creditMemoPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels)
        context.clickSaveByAPI()

        def internalId = context.getRecordIdbyAPI()
        // The journal can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.2.1.1
     * @desc
     * 1. Create a Customer Deposit with item CFS is non-blank
     *          2. Save
     *          3. Can be save
     */
    @Category([OWAndSI, P1, CFSMandatory])
    @Test
    void case_1_52_2_1_1() {
        tranType = "customerdeposit"
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.2.1.1")

        // Fill the fields in create Customer Deposit page
        customerDepositPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels)
        context.clickSaveByAPI()

        assertNoErrorMsg()
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.2.1.2
     * @desc
     * 1. Create a Customer Deposit with item CFS is blank
     *          2. Save
     *          3. Can not be save because CFS validation is mandatory
     */
    @Category([OWAndSI, P3, CFSMandatory])
    @Test
    void case_1_52_2_1_2() {
        tranType = "customerdeposit"
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.2.1.2")
        // Fill the fields in create Customer Refund page

        customerDepositPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels)
        context.clickSaveByAPI()

        assertErrorMsg()
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.3.1.1
     * @desc
     * 1. Create a Customer Refund with item CFS is non-blank
     *          2. Save
     *          3. Can be save
     */
    @Category([OWAndSI, P2, CFSMandatory])
    @Test
    void case_1_52_3_1_1() {
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.3.1.1")
        tranType = "customerrefund"

        // Create a customer deposit
        customerDepositPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels[0])
        context.clickSaveByAPI()

        // Fill the fields in create Customer Refund page
        customerRefundPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels[1])
        context.clickSaveByAPI()

        internalId = context.getRecordIdbyAPI()
        // The Customer Refund can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 1.52.2.1.2
     * @desc
     * 1. Create a Customer Refund with item CFS is blank
     *          2. Save
     *          3. Can not be save because CFS validation is mandatory
     */
    @Category([OWAndSI, P2, CFSMandatory])
    @Test
    void case_1_52_3_1_2() {

        tranType = "customerdepoist"   //Need to delete customer deposit
        // Fill the fields in create Customer Refund page
        def caseObj = getCaseObj(jsonFile, "testcase_1.52.3.1.2")

        // Create a customer deposit
        customerDepositPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels[0])
        context.clickSaveByAPI()
        internalId = context.getRecordIdbyAPI()

        // Fill the fields in create Customer Refund page
        customerRefundPage.navigateToURL()
        currentRecord.setCurrentRecord(caseObj.labels[1])
        context.clickSaveByAPI()

        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.52.4.1
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' is blank.
     *          1. New an Invoice
     *          2. Save
     *          3. Error message will popup
     */
    @Test
    @Category([OWAndSI.class, P1.class, CFSMandatory.class])
    public void case_1_52_4_1() {
        // Create Invoice.
        setTranCurrentRecord("test case 1.52.4.1", "cashflow//case_1_50_4_data.json", CURL.INVOICE_CURL)
        // Error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.52.4.2
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' in both 2 Line has value.
     *          1. New an Invoice
     *          2. Save
     *          3. Invoice can be saved successfully
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    public void case_1_52_4_2() {
        // Create Invoice.
        setTranCurrentRecord("test case 1.52.4.2", "cashflow//case_1_50_4_data.json", CURL.INVOICE_CURL)
        internalId = context.getRecordIdbyAPI()
        tranType = "invoice"
        // Invoice can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @CaseID Cashflow 1.52.4.3
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' in ONLY 1 line has value.
     *          1. New an Invoice
     *          2. Save
     *          3. Error message will popup
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    public void case_1_52_4_3() {
        // Create Invoice.
        setTranCurrentRecord("test case 1.52.4.3", "cashflow//case_1_50_4_data.json", CURL.INVOICE_CURL)
        // Error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.52.5.1
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' is blank.
     *          1. New a Cash Sale
     *          2. Save
     *          3. Error message will popup
     */
    @Test
    @Category([OWAndSI.class, P1.class, CFSMandatory.class])
    public void case_1_52_5_1() {
        context.navigateToNoWait(CURL.CASH_SALE_CURL)
        // Create Cash Sale.
        setTranCurrentRecord("test case 1.52.5.1", "cashflow//case_1_50_4_data.json", CURL.CASH_SALE_CURL)
        // Error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.52.5.2
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' in Line has value, but in Header is blank.
     *          1. New a Cash Sale
     *          2. Save
     *          3. Cash Sale can be saved successfully
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    public void case_1_52_5_2() {
        // Create Cash Sale.
        setTranCurrentRecord("test case 1.52.5.2", "cashflow//case_1_50_4_data.json", CURL.CASH_SALE_CURL)
        internalId = context.getRecordIdbyAPI()
        tranType = "cashsale"
        // Cash Sale can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @CaseID Cashflow 1.52.5.3
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' in Line is blank,but in Header has value.
     *          1. New a Cash Sale
     *          2. Save
     *          3. Cash Sale can be saved successfully
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    public void case_1_52_5_3() {
        // Create Cash Sale.
        setTranCurrentRecord("test case 1.52.5.3", "cashflow//case_1_50_4_data.json", CURL.CASH_SALE_CURL)
        internalId = context.getRecordIdbyAPI()
        tranType = "cashsale"
        // Cash Sale can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @CaseID Cashflow 1.52.6.1
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' is blank.
     *          1. New a Cash Refund
     *          2. Save
     *          3. Error message will popup
     */
    @Test
    @Category([OWAndSI.class, P1.class, CFSMandatory.class])
    public void case_1_52_6_1() {
        // Create Cash Refund.
        setTranCurrentRecord("test case 1.52.6.1", "cashflow//case_1_50_4_data.json", CURL.CASH_REFUND_CURL)
        // Error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.52.6.2
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' in Line has value.
     *          1. New a Cash Refund
     *          2. Save
     *          3. Cash Refund can be saved successfully
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    public void case_1_52_6_2() {
        // Create Cash Refund.
        setTranCurrentRecord("test case 1.52.6.2", "cashflow//case_1_50_4_data.json", CURL.CASH_REFUND_CURL)
        internalId = context.getRecordIdbyAPI()
        tranType = "cashrefund"
        // Cash Refund can be saved successfully.
        assertTranIdNotNull(internalId)
    }

    /**
     * @CaseID Cashflow 1.53.1.1
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' is blank.
     *          1. New a Sales Order, save
     *          2. Approve
     *          3. Fulfill
     *          4. Go to Invoice sales order page, select the invoice created above
     *          5. Result should be '1 Error'
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    public void case_1_53_1_1() {
        def caseId = "test case 1.53.1.1";
        def testDataObj = loadCFSTestData("cashflow//case_1_50_4_data.json", caseId);
        def dataObj = testDataObj[caseId].data;

        /*def salesOrderId = createInvoiceSOviaAPI(dataObj)
        println("salesOrderId:" + salesOrderId)*/

        def salesOrderId = createInvoiceSO(caseId, dataObj)
        // Assert on Process Status page
        def submitId = processStatusPage.getTextSubmitId()
        assertSubmitStatusonProcessStatus(processStatusPage.getTextSubmiStatusonProcessStatus())
        assert1ErrorMsgonProcessStatus(processStatusPage.getTextMessageonProcessStatus())
        assertPercentCompleteonProcessStatus(processStatusPage.getTextPercentComplete())
        // Assert on Process Records page
        processedRecordsPage.navigateTo(submitId)
        assertFailRstonProcessRecords(processedRecordsPage.getTextRstonProcessRecords())
        assertErrorMsgProcess(processedRecordsPage.getTextErrorMsgonProcessRecords())

        deleteInvoiceSO(salesOrderId.toString())
    }

    /**
     * @CaseID Cashflow 1.53.1.2
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' header is blank, line has value.
     *          1. New a Sales Order, save
     *          2. Approve
     *          3. Fulfill
     *          4. Go to Invoice sales order page, select the invoice created above
     *          5. Result should be '0 Errors'
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    public void case_1_53_1_2() {
        def caseId = "test case 1.53.1.2";
        def testDataObj = loadCFSTestData("cashflow//case_1_50_4_data.json", caseId);
        def dataObj = testDataObj[caseId].data;

        def salesOrderId = createInvoiceSO(caseId, dataObj)
        // Assert on Process Status page
        def submitId = processStatusPage.getTextSubmitId()
        assertSubmitStatusonProcessStatus(processStatusPage.getTextSubmiStatusonProcessStatus())
        assert0ErrorMsgonProcessStatus(processStatusPage.getTextMessageonProcessStatus())
        assertPercentCompleteonProcessStatus(processStatusPage.getTextPercentComplete())
        // Assert on Process Records page
        processedRecordsPage.navigateTo(submitId)
        assertSuccessRstonProcessRecords(processedRecordsPage.getTextRstonProcessRecords())
        assertTrue("Should not show error message.", "".equals(processedRecordsPage.getTextErrorMsgonProcessRecords()))

        deleteInvoiceSO(salesOrderId.toString())
    }

    /**
     * @CaseID Cashflow 1.53.1.3
     * @author lisha.hao@oracle.com
     * @Desc: 'China Cash Flow item' line is blank, header has value.
     *          1. New a Sales Order, save
     *          2. Approve
     *          3. Fulfill
     *          4. Go to Invoice sales order page, select the invoice created above
     *          5. Result should be '0 Errors'
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    public void case_1_53_1_3() {
        def caseId = "test case 1.53.1.3";
        def testDataObj = loadCFSTestData("cashflow//case_1_50_4_data.json", caseId);
        def dataObj = testDataObj[caseId].data;

        def salesOrderId = createInvoiceSO(caseId, dataObj)
        // Assert on Process Status page
        def submitId = processStatusPage.getTextSubmitId()
        assertSubmitStatusonProcessStatus(processStatusPage.getTextSubmiStatusonProcessStatus())
        assert0ErrorMsgonProcessStatus(processStatusPage.getTextMessageonProcessStatus())
        assertPercentCompleteonProcessStatus(processStatusPage.getTextPercentComplete())
        // Assert on Process Records page
        processedRecordsPage.navigateTo(submitId)
        assertSuccessRstonProcessRecords(processedRecordsPage.getTextRstonProcessRecords())
        assertTrue("Should not show error message.", "".equals(processedRecordsPage.getTextErrorMsgonProcessRecords()))

        deleteInvoiceSO(salesOrderId.toString())
    }
}