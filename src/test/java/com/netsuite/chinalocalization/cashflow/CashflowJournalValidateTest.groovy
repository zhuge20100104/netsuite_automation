package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.common.CFSMandatory
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("yang.g.liu@oracle.com")
class CashflowJournalValidateTest extends CashflowBaseTest {

    @Inject
    protected JournalEntryPage journalEntryPage
    @Inject
    SubsidiaryPage subsidiaryPage
    @Inject
    protected NRecord record

    @Before
    void setUp() {
        super.setUp()
        enableCFSMandatory("CFS Validation BU")
    }

    def response = ""
    def responseObj = {}


    @After
    void tearDown() {
        if (response != "") {
            context.deleteTransaction("journalentry", response.internalid)
        }
        super.tearDown()
    }

    @SuiteTeardown
    void tearDownTestSuite() {
        switchToRole(getAdministrator())
        // The subsidiary check box unchecked
        disableCFSMandatory("CFS Validation BU")
        super.tearDownTestSuite()
    }

    /**
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Not validate the CFS field when bank account is blank
     *          1. New a journal
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P1.class, CFSMandatory.class])
    void case_1_50_1_1_1() {
        def caseId = "test case 1.50_Journal_1"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // The journal can be saved successfully.
        assertTranIdNotNull(response.internalid)
    }

    /**
     * @CaseID Cashflow 1.50.1.1.2
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Validate the CFS field for blank when un-bank account is blank
     *          1. New a journal
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    void case_1_50_1_1_2() {
        def caseId = "test case 1.50_Journal_2"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.1.1.3
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Validate the CFS field for blank when all accounts are blank
     *          1. New a journal
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    void case_1_50_1_1_3() {
        def caseId = "test case 1.50_Journal_3"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.1.1.4
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Validate the CFS field for blank when AR account is blank
     *          1. New a journal
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    void case_1_50_1_1_4() {
        def caseId = "test case 1.50_Journal_4"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.1.1.5
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Validate the CFS field for blank when accounts are blank in multi debits and multi credits journal
     *          1. New a journal
     *          2. Save
     *          3. Check error message will popup.
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    void case_1_50_1_1_5() {
        def caseId = "test case 1.50_Journal_5"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // Check error message will popup.
        assertErrorMsg()
    }

    /**
     * @CaseID Cashflow 1.50.1.1.6
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Not validate the CFS field when both debit and credit are bank accounts
     *          1. New a journal
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P2.class, CFSMandatory.class])
    void case_1_50_1_1_6() {
        def caseId = "test case 1.50_Journal_6"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // The journal can be saved successfully.
        assertTranIdNotNull(response.internalid)
    }

    /**
     * @CaseID Cashflow 1.50.1.1.7
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Not validate the CFS field when both debit and credit are un-bank accounts
     *          1. New a journal
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    void case_1_50_1_1_7() {
        def caseId = "test case 1.50_Journal_7"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // The journal can be saved successfully.
        assertTranIdNotNull(response.internalid)
    }

    /**
     * @CaseID Cashflow 1.50.1.1.8
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      The journal can be saved when CFS values in all lines are not blank
     *          1. New a journal
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P3.class, CFSMandatory.class])
    void case_1_50_1_1_8() {
        def caseId = "test case 1.50_Journal_8"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // The journal can be saved successfully.
        assertTranIdNotNull(response.internalid)
    }
}