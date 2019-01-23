package com.netsuite.chinalocalization.voucher

import com.google.inject.Inject
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import groovy.json.JsonSlurper
import org.junit.Test
import org.junit.experimental.categories.Category

class VoucherPrintCapTest extends VoucherPrintBaseTest{
    @Inject
    JournalEntryPage journalEntryPage
    def response = "";
    /**
     * @desc
     * 0.The journal of Period To:  Aug 2017, 08/06/2017(Date) is created.
     * 1.Subsidary: China BU
     * 2.Period From: Jul 2017
     * 3.Period To: Aug2017
     * 4.Date From: 08/06/2017
     * 5.Date To: 08/06/2017
     * 6.Other fields are default values
     * 7.Click 'Refresh'
     * @case 11.3.2.1.1
     * @author lisha.hao
     */
    @Category([P1.class, OW.class])
    @Test
    void test_11_3_2_1_1() {
        def caseId = "case_11_3_2_1_1";
        def dataString = context.getFileContent(caseId, "voucher//case_11_3_2_1_1_data.json")
        def caseObj = new JsonSlurper().parseText(dataString)
        def dataObj = caseObj[caseId].data[0]

        // 'Financial->Other->Make Journal Entries', create journal.
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(dataObj)

        voucherPrintPage.navigateToURL()
        voucherPrintPage.setParameters(caseObj[caseId].ui.voucherPrintParams, MULTI_CURRENCIES)
        voucherPrintPage.checkAllCustomField()
        voucherPrintPage.clickRefreshBtn()

        verifyReportContent(caseObj[caseId].expected)
        
       /* voucherPrintPage.navigateToURL()
        //refresh report
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
        voucherPrintPage.setParameters(dataObj.labels,null)
        voucherPrintPage.clickRefreshBtn()*/

        // def tableData = printPage.parseTableData()
        /*def expect = dataObj.expect
        def footer = voucherPrintPage.getVoucherReportFooter(0)
        checkAreEqual("Verify Creator", expect[0].creator, footer.creator)
        checkAreEqual("Verify Approver", expect[0].approver, footer.approver)
        checkAreEqual("Verify Poster", expect[0].poster, footer.poster)*/
    }

}
