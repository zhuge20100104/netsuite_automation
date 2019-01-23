package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("yang.g.liu@oracle.com")
class CashflowJournalValidateUncheckedTest extends CashflowBaseTest {

    @Inject
    protected JournalEntryPage journalEntryPage
    @Inject
    protected SubsidiaryPage subsidiaryPage

    def response = ""
    def responseObj = {}

    def getDefaultRole() {
        return getAdministrator()
    }

    @Before
    void setUp(){
        super.setUp()
        enableCFSMandatory("CFS Validation BU")
    }

    @After
    void tearDown() {
        if (response != "") {
//            def response = "[{\"internalid\":"+response.internalid.toInteger()+",\"trantype\":\"journalentry\"}]"
//            deleteTransaction(response)

            context.deleteTransaction("journalentry",response.internalid)
        }
        super.tearDown()
    }

    /**
     * @CaseID Cashflow 1.50.1.2.1
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Not validate the CFS field for blank when un-bank account is blank
     *          1. New a journal
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void case_1_50_1_2_1() {
        def caseId = "test case 1.50_Journal_2"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        switchToRole(getAccountant())
        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // The journal can be saved successfully.
        assertTranIdNotNull(response.internalid)
    }

    /**
     * @CaseID Cashflow 1.50.1.2.2
     * @author yang.g.liu@oracle.com
     * Description: The subsidiary check box checked
     *      Not validate the CFS field for blank when un-bank account is blank
     *          1. New a journal
     *          2. Save
     *          3. The journal can be saved successfully.
     */
    @Test
    @Category([OWAndSI.class, P2.class])
    void case_1_50_1_2_2() {
        def caseId = "test case 1.50_Journal_3"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        switchToRole(getAccountant())
        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        // The journal can be saved successfully.
        assertTranIdNotNull(response.internalid)
    }
}