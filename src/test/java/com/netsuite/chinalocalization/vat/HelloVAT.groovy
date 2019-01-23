package com.netsuite.chinalocalization.vat

import org.junit.Test

class HelloVAT extends VATAppTestSuite {

    /**
     * @desc Demo purpose
     * @case NA
     * @author Jianwei Liu
     */
    @Test
    void test_case_hello() {

        navigateToPortalPage()

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem("北京VAT")
        }
        asMultiSelectField("custpage_transactiontype").setValues("Invoice", "Cash Sale")
        asMultiSelectField("custpage_customer").selectItems("1 CN Automation Customer", "3 Cash FLow BU Customer")
        context.setFieldWithValue("custpage_datefrom", format.formatDate("2017/11/29"))
        context.setFieldWithValue("custpage_dateto", format.formatDate("2017/11/29"))

        clickRefresh()
        waitForPageToLoad()
        Thread.sleep(10 * 1000)

        asClick("//*[@id=\"custpage_edit\"]")
        waitForPageToLoad()
        Thread.sleep(10 * 1000)

        def headerSublist = asSublist("custpage_header_sublist")
        assertTrue("Column doesn't exist", headerSublist.doesColumnExist("Transaction Type"))
        def tranType = headerSublist.getTextInColumnOfRow("Transaction Type", 0)
        assertAreEqual("Transaction Type not expected", tranType, "Cash Sale")
    }
}