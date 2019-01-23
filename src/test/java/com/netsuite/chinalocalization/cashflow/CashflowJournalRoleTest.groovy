package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.*
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("jingzhou.wang@oracle.com")
class CashflowJournalRoleTest extends CashflowBaseTest {

    @Inject
    protected JournalEntryPage journalEntryPage
    @Inject
    SubsidiaryPage subsidiaryPage
    @Inject
    protected NRecord record

    @Before
    void setUp() {
        super.setUp()
        disableCFSMandatory("China Bu")
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


    /**
     * @author Jingzhou.wang@oracle.com
     * Description: Test journal could be saved by Bookkeeper role
     *      Not validate the CFS field when bank account is blank
     *          1. New a journal
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    //@Test
    @Category([OWAndSI.class, P1.class])
    void case_role_test() {
        switchToRole(getBookkeeper())

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
}