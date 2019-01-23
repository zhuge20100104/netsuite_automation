package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.NCurrentRecord
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("yang.g.liu@oracle.com")
class CashflowInterJournalValidateTest extends CashflowBaseTest {

    @Inject
    protected JournalEntryPage journalEntryPage
    @Inject
    protected SubsidiaryPage subsidiaryPage
    @Inject
    protected NCurrentRecord currentRecord
    @Inject
    protected NRecord record

    def response = ""
    def internlId = ""

    def getDefaultRole() {
        return getAdministrator()
    }

    @After
    void tearDown() {
        if (internlId != "") {
            deleteTransaction("journalentry", internlId)
        }
        super.tearDown()
    }

    @SuiteTeardown
    void tearDownTestSuite() {
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU 01")
        disableCFSMandatory("CFS Validation BU 02")
        super.tearDownTestSuite()
    }

    /**
     * As of 2018.1, advanced intercompany journal entries replace legacy intercompany journal entries in new OneWorld accounts.
     * @CaseID Cashflow 1.50.2.1.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Intercompany journal
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:checked
     *          2. Save
     *          3. Check error message will popup.
     */
    //@Test
    @Category([OW.class, P1.class])
    void case_1_50_2_1_1() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU 01")
        enableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Intercompany Journal Enties,Create Intercompany Journal
        setTranCurrentRecord("test case 1.50_Intercompany Journal_1", "cashflow//case_1_50_data.json", CURL.INTERCOMPANY_JOURNAL_ENTRY_CURL)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.2.1.2
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Intercompany journal
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:checked
     *          2. Save
     *          3. Check error message will popup.
     */
    //@Test
    @Category([OW.class, P2.class])
    void case_1_50_2_1_2() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU 01")
        enableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Intercompany Journal Enties,Create Intercompany Journal
        setTranCurrentRecord("test case 1.50_Intercompany Journal_2", "cashflow//case_1_50_data.json", CURL.INTERCOMPANY_JOURNAL_ENTRY_CURL)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.2.1.3
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Intercompany journal
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:checked
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    //@Test
    @Category([OW.class, P2.class])
    void case_1_50_2_1_3() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU 01")
        enableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Intercompany Journal Enties,Create Intercompany Journal
        setTranCurrentRecord("test case 1.50_Intercompany Journal_3", "cashflow//case_1_50_data.json", CURL.INTERCOMPANY_JOURNAL_ENTRY_CURL)
        internlId = context.getParameterValueForFromQueryString("id")

        // The journal can be saved successfully.
        assertNoErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.2.2.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Intercompany journal
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:unchecked
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    //@Test
    @Category([OW.class, P2.class])
    void case_1_50_2_2_1() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU 01")
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Intercompany Journal Enties,Create Intercompany Journal
        setTranCurrentRecord("test case 1.50_Intercompany Journal_4", "cashflow//case_1_50_data.json", CURL.INTERCOMPANY_JOURNAL_ENTRY_CURL)
        internlId = context.getParameterValueForFromQueryString("id")

        // The journal can be saved successfully.
        assertNoErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.2.2.2
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Intercompany journal
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:unchecked
     *          2. Save
     *          3. Check error message will popup.
     */
    //@Test
    @Category([OW.class, P3.class])
    void case_1_50_2_2_2() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU 01")
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Intercompany Journal Enties,Create Intercompany Journal
        setTranCurrentRecord("test case 1.50_Intercompany Journal_5", "cashflow//case_1_50_data.json", CURL.INTERCOMPANY_JOURNAL_ENTRY_CURL)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.2.3.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Intercompany journal
     *             CFS Validation BU 01:unchecked
     *             CFS Validation BU 02:unchecked
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    //@Test
    @Category([OW.class, P1.class])
    void case_1_50_2_3_1() {
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU 01")
        disableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Intercompany Journal Enties,Create Intercompany Journal
        setTranCurrentRecord("test case 1.50_Intercompany Journal_6", "cashflow//case_1_50_data.json", CURL.INTERCOMPANY_JOURNAL_ENTRY_CURL)
        internlId = context.getParameterValueForFromQueryString("id")

        // The journal can be saved successfully.
        assertNoErrorMsg()
    }
}