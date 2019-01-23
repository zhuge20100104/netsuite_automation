package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.CheckPage
import com.netsuite.chinalocalization.page.ExpenseReportpage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.chinalocalization.page.VendorBillPage
import com.netsuite.chinalocalization.page.VendorCreditPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("yang.g.liu@oracle.com")
class CashflowAPValidateUncheckedTest extends CashflowBaseTest {

    @Inject
    protected SubsidiaryPage subsidiaryPage

    def internlId = ""
    def trantype = ""

    def getDefaultRole() {
        return getAdministrator()
    }

    @Before
    void setUp(){
        super.setUp()
        disableCFSMandatory("China BU")
    }

    @After
    void tearDown() {
        if (internlId) {
            deleteTransaction(trantype, internlId)
        }
        super.tearDown()
    }


    /**
     * @CaseID Cashflow 1.51.1.2.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Not validate the CFS field for blank in Expenses tab and Item Tab
     *          1. New a Bill
     *          2. Save
     *          3. The Bill can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void case_1_51_1_2_1() {
        setTranCurrentRecord("test case 1.51.1.2.1", "cashflow//case_1_51_data.json", CURL.VENDOR_BILL_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "vendorbill"

        // The bill can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.51.2.2.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Not validate the CFS field for blank in Expenses tab
     *          1. New a Expense Report
     *          2. Save
     *          3. The Expense Report can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void case_1_51_2_2_1() {
        setTranCurrentRecord("test case 1.51.2.2.1", "cashflow//case_1_51_data.json", CURL.EXPENSE_REPORT_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "expensereport"

        // The expense report can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.51.3.2.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Not validate the CFS field for blank in Expenses tab and Item Tab
     *          1. New a Credit
     *          2. Save
     *          3. The Credit can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P2.class])
    void case_1_51_3_2_1() {
        setTranCurrentRecord("test case 1.51.3.2.1", "cashflow//case_1_51_data.json", CURL.VENDOR_CREDIT_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "vendorcredit"

        // The credit can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.51.4.2.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Not validate the CFS field for blank in Expenses tab and Item Tab
     *          1. New a Check
     *          2. Save
     *          3. The Check can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P3.class])
    void case_1_51_4_2_1() {
        setTranCurrentRecord("test case 1.51.4.2.1", "cashflow//case_1_51_data.json", CURL.CHECK_CURL)
        internlId = context.getRecordIdbyAPI()
        trantype = "check"

        // The check can be saved successfully.
        assertTranIdNotNull(internlId)
    }
}