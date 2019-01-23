package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.common.CFSMandatory
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("yang.g.liu@oracle.com")
class CashflowAPValidateCheckedTest extends CashflowBaseTest {

    @Inject
    protected SubsidiaryPage subsidiaryPage

    def internlId = ""
    def trantype = ""

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
        if (internlId) {
            deleteTransaction(trantype, internlId)
        }
        super.tearDown()
    }

    @SuiteTeardown
    void tearDownTestSuite() {
        // The subsidiary check box unchecked
        try {
            disableCFSMandatory("China BU")
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        super.tearDownTestSuite()
    }

    /**
     * @CaseID Cashflow 1.51.1.1.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in Expenses tab
     *          1. New a Bill
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P0.class, CFSMandatory.class])
    void case_1_51_1_1_1() {
        setTranCurrentRecord("test case 1.51.1.1.1", "cashflow//case_1_51_data.json", CURL.VENDOR_BILL_CURL)
        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.51.1.1.2
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in Item tab
     *          1. New a Bill
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    void case_1_51_1_1_2() {
        setTranCurrentRecord("test case 1.51.1.1.2", "cashflow//case_1_51_data.json", CURL.VENDOR_BILL_CURL)
        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.51.1.1.3
     * @author yang.g.liu@oracle.com
     * Description:
     *      If CFS entered in the header,not validate the CFS field for blank in lines
     *          1. New a Bill
     *          2. Save
     *          3. The bill can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    void case_1_51_1_1_3() {
        setTranCurrentRecord("test case 1.51.1.1.3", "cashflow//case_1_51_data.json", CURL.VENDOR_BILL_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "vendorbill"
        // The bill can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.51.1.1.4
     * @author yang.g.liu@oracle.com
     * Description:
     *      The bill can be saved when CFS values in all lines are not blank
     *          1. New a Bill
     *          2. Save
     *          3. The bill can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    void case_1_51_1_1_4() {
        setTranCurrentRecord("test case 1.51.1.1.4", "cashflow//case_1_51_data.json", CURL.VENDOR_BILL_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "vendorbill"
        // The bill can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.51.2.1.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in Expenses tab
     *          1. New a Expense Report
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P1.class, CFSMandatory.class])
    void case_1_51_2_1_1() {
        setTranCurrentRecord("test case 1.51.2.1.1", "cashflow//case_1_51_data.json", CURL.EXPENSE_REPORT_CURL)
        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.51.2.1.2
     * @author yang.g.liu@oracle.com
     * Description:
     *      The Expense Report can be saved when CFS values in all lines are not blank
     *          1. New a Expense Report
     *          2. Save
     *          3. The expense report can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    void case_1_51_2_1_2() {
        setTranCurrentRecord("test case 1.51.2.1.2", "cashflow//case_1_51_data.json", CURL.EXPENSE_REPORT_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "expensereport"

        // The expense report can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.51.3.1.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in Item tab
     *          1. New a Credit
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P1.class, CFSMandatory.class])
    void case_1_51_3_1_1() {
        setTranCurrentRecord("test case 1.51.3.1.1", "cashflow//case_1_51_data.json", CURL.VENDOR_CREDIT_CURL)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.51.3.1.2
     * @author yang.g.liu@oracle.com
     * Description:
     *      The credit can be saved when CFS values are not blank in exspanse line and not validate description Item in Item Tab
     *          1. New a Credit
     *          2. Save
     *          3. The Credit can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    void case_1_51_3_1_2() {
        setTranCurrentRecord("test case 1.51.3.1.2", "cashflow//case_1_51_data.json", CURL.VENDOR_CREDIT_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "vendorcredit"

        // The credit can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.51.4.1.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in Expenses tab
     *          1. New a Check
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    void case_1_51_4_1_1() {
        setTranCurrentRecord("test case 1.51.4.1.1", "cashflow//case_1_51_data.json", CURL.CHECK_CURL)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.51.4.1.2
     * @author yang.g.liu@oracle.com
     * Description:
     *      The check can be saved when CFS values are not blank in exspanse line and not validate description Item in Item Tab
     *          1. New a Check
     *          2. Save
     *          3. The Check can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    void case_1_51_4_1_2() {
        setTranCurrentRecord("test case 1.51.4.1.2", "cashflow//case_1_51_data.json", CURL.CHECK_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "check"

        // The check can be saved successfully.
        assertTranIdNotNull(internlId)
    }
}