package com.netsuite.chinalocalization.voucher

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category
/**
 * Description:
 * Check footer on Single Voucher
 * This is a P1-Sanity case.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 * @CaseID: 1.6.1.3
 * @author lifang.tang@oracle.com
 * @version 2018/5/24
 * @since 2018/5/24
 */
@TestOwner ("lifang.tang@oracle.com")
class SingleCurrencyPrintTemplateTest extends VoucherP2BaseTest{
    VoucherPrintPage voucherPrintPage

    @Category([P2.class, OW.class])
    @Test
    void case_1_6_1_3(){
        def testData = cData.data("test_1_6_1_3")
        def recordData = testData.record()
        navigateToVoucherPrintPage()
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
        voucherPrintPage.setParameters(testData.labels(),"单币")
        voucherPrintPage.clickPrintBtn()
    }
}
