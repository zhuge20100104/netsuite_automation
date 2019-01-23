package com.netsuite.chinalocalization.voucher

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

class VoucherJournalUITest extends VoucherPrintBaseTest{
    @Inject
    JournalEntryPage journalEntryPage
    def response = "";

    @After
    void tearDown() {
        if (response) {
            deleteTransaction(response)
        }
        super.tearDown()
    }

    /**
     * @author lisha.hao
     * @lastUpdateBy
     * @caseID 1.1.1.1
     * @desc Journal edit page
     *       1. Go to Financial > Other > Make Journal Enties
     *       2. "Print Voucher" button is invisible
     */
    @Category([P1.class, OW.class])
    @Test
    void case_1_1_1_1(){
        context.navigateTo(CURL.JOURNAL_ENTRY_CURL)
        assertFalse("'Print Voucher' should not exist.", netSuiteAppCN.isTextVisible("Print Voucher"))
    }

    /**
     * @author lisha.hao
     * @lastUpdateBy
     * @caseID 1.1.1.2
     * @desc Journal view page
     *       1. Create and Save a journal
     *       2. Subsidiary selected "Parent Company"
     *       3. "Print Voucher" button is available
     */
    @Category([P1.class, OW.class])
    @Test
    void case_1_1_1_2(){
        def caseId = "case_1_1_1_2";
        def dataString = context.getFileContent(caseId, "voucher//case_1_1_1_2_data.json")
        def caseObj = new JsonSlurper().parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // 'Financial->Other->Make Journal Entries', create journal.
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)
        assertFalse("'Print Voucher' should be enabled.",netSuiteAppCN.isButtonDisabled("Print Voucher"))
    }
}
