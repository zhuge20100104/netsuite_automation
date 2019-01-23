package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import org.junit.Test
import org.junit.experimental.categories.Category

class VoucherTranOtherTest extends VoucherP2BaseTest{
    VoucherPrintPage voucherPrintPage
    /**
     * @desc   Transaction type: Others (InvAdjst using Administrator Role)   inventory adjustment
     * 1. Create a transaction type of Others
     * Refer Sheet:Test Data(Inventory Adj)
     * 2. Go to Financial > Other > 中国凭证打印报表
     * Search condition:
     * Subsidiary: China BU
     * Period From: Jul 2017
     * Period To: Jul 2017
     * Date from: 7/10/2017
     * Date To: 7/10/2017
     * 3. Click "Refresh"
     * @case 1.11.2.1.3
     * @author lisha.hao
     */
    @Category([P1.class, OW.class])
    @Test
    void test_1_11_2_1_3() {
        def testData = cData.data("test_1_11_2_1_3")
        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)

        //refresh report
        navigateToVoucherPrintPage()
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
        voucherPrintPage.setParameters(testData.labels(),null)
        voucherPrintPage.clickRefreshBtn()

        def expect = testData.expect()
        def footer = voucherPrintPage.getVoucherReportFooter(0)
        checkAreEqual("Verify Creator", expect[0].creator, footer.creator)
        checkAreEqual("Verify Approver", expect[0].approver, footer.approver)
        checkAreEqual("Verify Poster", expect[0].poster, footer.poster)
    }
}
