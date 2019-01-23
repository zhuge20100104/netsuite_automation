package com.netsuite.chinalocalization.voucher.p2

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.voucher.VoucherP2BaseTest
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P0
import org.junit.Test
import org.junit.experimental.categories.Category

class VoucherPDFTest extends VoucherP2BaseTest{

    /**
     * @desc check Button available
     * Click "Print Voucher" button
     * 1.The voucher should be printed in PDF format, and the data should be correct.
     * 2.Verify Subsidiary shows "Parent Company" in PDF page.
     * @author FredricZhu Zhu
     */
    @Category([P0.class, OW.class])
    @Test
    void test_1_1_1_3() {
        def testData = cData.data("test_1_1_1_3")

        def recordData = testData.record()
        def labels = testData.labels()

        dirtyData = record.createRecord(recordData)
        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)
        printPage.clickPrintPDF()
        def  pdfParser = new ParsePDF()
        assertTrue("Subsidiary is not China BU!", pdfParser.getValueFromKey("子公司").equalsIgnoreCase(labels.shortSubsidiary))
        assertTrue("Date is not Jul 2017", pdfParser.getValueFromKey("过账期间").equalsIgnoreCase(periodFrom))
    }
}
