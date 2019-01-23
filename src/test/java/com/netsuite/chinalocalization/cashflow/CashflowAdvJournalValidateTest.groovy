package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.NCurrentRecord
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.OW
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

@TestOwner ("yang.g.liu@oracle.com")
class CashflowAdvJournalValidateTest extends CashflowBaseTest {

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
        if (context.isPopUpPresent()) {
            context.closePopUp()
        }
        if (internlId != "") {
            record.deleteRecord("journalentry",internlId)
        }
        super.tearDown()
    }

    @SuiteTeardown
    void tearDownTestSuite() {
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU")
        disableCFSMandatory("CFS Validation BU 01")
        disableCFSMandatory("CFS Validation BU 02")
        super.tearDownTestSuite()
    }

    /**
     * @CaseID Cashflow 1.50.3.1.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Advanced Intercompany journal
     *             CFS Validation BU:checked
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:checked
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OW.class, P0.class,CFSMandatory.class])
    void case_1_50_3_1_1() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU")
        enableCFSMandatory("CFS Validation BU 01")
        enableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Advanced Intercompany Journal Enties,Create Advanced Intercompany Journal
        setTranCurrentRecord("test case 1.50_Advanced Intercompany Journal_1", "cashflow//case_1_50_data.json", CURL.ADVANCED_INTERCOMPANY_JOURNAL_ENTRY_CURL)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.3.1.2
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Advanced Intercompany journal
     *             CFS Validation BU:checked
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:checked
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OW.class, P2.class,CFSMandatory.class])
    void case_1_50_3_1_2() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU")
        enableCFSMandatory("CFS Validation BU 01")
        enableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Advanced Intercompany Journal Enties,Create Advanced Intercompany Journal
        setTranCurrentRecord("test case 1.50_Advanced Intercompany Journal_2", "cashflow//case_1_50_data.json", CURL.ADVANCED_INTERCOMPANY_JOURNAL_ENTRY_CURL)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.3.1.3
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Advanced Intercompany journal
     *             CFS Validation BU:checked
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:checked
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OW.class, P3.class,CFSMandatory.class])
    void case_1_50_3_1_3() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU")
        enableCFSMandatory("CFS Validation BU 01")
        enableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Advanced Intercompany Journal Enties,Create Advanced Intercompany Journal
        setTranCurrentRecord("test case 1.50_Advanced Intercompany Journal_3", "cashflow//case_1_50_data.json", CURL.ADVANCED_INTERCOMPANY_JOURNAL_ENTRY_CURL)
        //internlId = context.getParameterValueForFromQueryString("id")
        internlId = record.getRecordId()

        // The journal can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.50.3.2.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Advanced Intercompany journal
     *             CFS Validation BU:checked
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:unchecked
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OW.class, P2.class,CFSMandatory.class])
    void case_1_50_3_2_1() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU")
        enableCFSMandatory("CFS Validation BU 01")
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Advanced Intercompany Journal Enties,Create Advanced Intercompany Journal
        setTranCurrentRecord("test case 1.50_Advanced Intercompany Journal_4", "cashflow//case_1_50_data.json", CURL.ADVANCED_INTERCOMPANY_JOURNAL_ENTRY_CURL)
        internlId = context.getParameterValueForFromQueryString("id")

        // The journal can be saved successfully.
        assertTranIdNotNull(internlId)
    }

    /**
     * @CaseID Cashflow 1.50.3.2.2
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Advanced Intercompany journal
     *             CFS Validation BU:checked
     *             CFS Validation BU 01:checked
     *             CFS Validation BU 02:unchecked
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OW.class, P3.class,CFSMandatory.class])
    void case_1_50_3_2_2() {
        // The subsidiary check box checked
        enableCFSMandatory("CFS Validation BU")
        enableCFSMandatory("CFS Validation BU 01")
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Advanced Intercompany Journal Enties,Create Advanced Intercompany Journal
        setTranCurrentRecord("test case 1.50_Advanced Intercompany Journal_5", "cashflow//case_1_50_data.json", CURL.ADVANCED_INTERCOMPANY_JOURNAL_ENTRY_CURL)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.3.3.1
     * @author yang.g.liu@oracle.com
     * Description:
     *      Validate the CFS field for blank in OW
     *          1. New a Advanced Intercompany journal
     *             CFS Validation BU:unchecked
     *             CFS Validation BU 01:unchecked
     *             CFS Validation BU 02:unchecked
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OW.class, P1.class,CFSMandatory.class])
    void case_1_50_3_3_1() {
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU")
        disableCFSMandatory("CFS Validation BU 01")
        disableCFSMandatory("CFS Validation BU 02")

        // Financial > Other >  Make Advanced Intercompany Journal Enties,Create Advanced Intercompany Journal
        setTranCurrentRecord("test case 1.50_Advanced Intercompany Journal_6", "cashflow//case_1_50_data.json", CURL.ADVANCED_INTERCOMPANY_JOURNAL_ENTRY_CURL)
        internlId = context.getParameterValueForFromQueryString("id")

        // The journal can be saved successfully.
        assertTranIdNotNull(internlId)
    }
}