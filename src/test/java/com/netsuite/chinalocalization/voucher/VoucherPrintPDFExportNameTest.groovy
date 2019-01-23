package com.netsuite.chinalocalization.voucher

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2

import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("cuiping.peng@oracle.com")
class VoucherPrintPDFExportNameTest extends VoucherP2BaseTest{

    VoucherPrintPage voucherPrintPage
    String downloadedFile = ""
    ParsePDF pdfParser

    @Category([P1.class, OW.class])
    @Test
    void case_1_5_1_2() {
        if (!context.isOneWorld()) {
            return
        }

        def testData = cData.data("test_1_5_1_2")

        def filter = testData.labels()
        def expectedInfo = testData.expect()

        //Navigate to Voucher Print Page and filter record
        navigateToVoucherPrintPage()
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
        voucherPrintPage.setParameters(filter,null)
        voucherPrintPage.clickPrintBtn()
        Thread.sleep(5000) //wait download finish

        //Verify the downloaded file name
        pdfParser = new ParsePDF()
        downloadedFile = pdfParser.getDownloadedFile()
        assertTrue("Download file name is correct", downloadedFile.contains(expectedInfo.fileName))
    }

    @After
    public void tearDown() {
        pdfParser.cleanDownloadFolder()
        super.tearDown()
    }

}

