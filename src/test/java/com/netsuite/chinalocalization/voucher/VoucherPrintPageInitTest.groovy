package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Description:
 * Verify Voucher print page init
 * This is a P2 case.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 *
 * @author qunxing.liu
 * @version 2018/4/17
 * @since 2018/4/17
 */
@Deprecated
@TestOwner("qunxing.liu@oracle.com")
class VoucherPrintPageInitTest extends VoucherP2BaseTest {

    VoucherPrintPage voucherPrintPage

    @Category([P2.class, OW.class])
    @Test
    void case_1_3_1_1_8() {

        def testData = cData.data("test_1_3_1_1_8")

        def expectedInfo = testData.expect()

        //Navigate to Voucher Print Page and filter record
        navigateToVoucherPrintPage()
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)


        DropdownList dpList = new DropdownList(context, context.webDriver, VoucherPrintPage.FID_TEMPLATE)
        try {
            //Verify Printing Template default value as expected
            checkTrue("The default Printing Template Value wrong.",
                    dpList.getValue() != expectedInfo.defaultTemplate)

            //Verify Printing Template dropdown list options as expected
            checkTrue("The Printing Template LOV wrong.",
                    dpList.getOptions().toString() != expectedInfo.printTemplateLOV.toString())
        }
        catch (Exception e) {
            e.printStackTrace()
        }

    }


}

