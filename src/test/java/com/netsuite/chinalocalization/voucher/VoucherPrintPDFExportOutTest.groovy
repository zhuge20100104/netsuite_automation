package com.netsuite.chinalocalization.voucher

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2

import com.netsuite.testautomation.junit.TestOwner
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Description:
 * Verify Voucher print PDF Export Out
 * This is a P2 case.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 *
 * @author qunxing.liu
 * @version 2018/4/17
 * @since 2018/4/17
 */
@TestOwner("qunxing.liu@oracle.com")
class VoucherPrintPDFExportOutTest extends VoucherP2BaseTest {

    VoucherPrintPage voucherPrintPage

    @Category([P1.class, OW.class])
    @Test
    void case_1_14_2_1() {

        def testData = cData.data("test_1_14_2_1")

        def filter = testData.labels()
        def expectedInfo = testData.expect()

        //Navigate to Voucher Print Page and filter record
        navigateToVoucherPrintPage()
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
        voucherPrintPage.setParameters(filter, null)
        voucherPrintPage.clickRefreshBtn()

        //check transaction data is right.

        try {
            Map tableContent = voucherPrintPage.getVoucherReportMain(0)
            String expectedTranDate = expectedInfo.trandate
            checkAreEqual("Please check TranDate", expectedTranDate, tableContent.date)

            voucherPrintPage.clickPrintBtn()
            Thread.sleep(5000) //wait download finish

            ParsePDF pdfParser = new ParsePDF()
            //Verify the downloaded file
            String downloadedFile = pdfParser.getDownloadedFile()
            assertTrue("Download file name is correct", downloadedFile.contains(expectedInfo.fileName))


            int recordSize = 0
            List<List<String>> rawList = pdfParser.getRawContents()
            for (List<String> lines : rawList) {
                for (String row : lines) {
                    if (row.contains(expectedTranDate)) {
                        recordSize = recordSize + 1
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace()
        }

    }


}

