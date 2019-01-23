package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("jianwei.liu@oracle.com")
class VATEditInvoicesAfterExportTest extends VATAppTestSuite {

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\\\data\\VATEditInvoicesAfterExportTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATEditInvoicesAfterExportTest_en_US.json"
        ]
    }

    /**
     * @desc Test case that edit invoices after export, and a waring message alerts.
     * @case 2.11.1
     * @author Jianwei Liu
     * @Regression: Vat Feature
     */
    @Category([P0.class,OWAndSI.class])
    @Test
    void test_case_2_11_1() {
        // switch to admin
        //switchToRole(administrator)
        def invoices = dirtyData = record.createRecord(testData.test_case_2_11_1.data)
        // switch back to accountant
        //switchToRole(accountant)

        navigateToPortalPage()

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(testData.test_case_2_11_1.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(testData.test_case_2_11_1.filter.invoicetype)
        context.setFieldWithValue("custpage_documentnumberfrom", invoices[0].tranid)
        context.setFieldWithValue("custpage_documentnumberto", invoices[0].tranid)

        clickRefresh()
        waitForPageToLoad()

        waitForElement(locators.export)
        Thread.sleep(10 * 1000)
        clickExport()
        Thread.sleep(5 * 1000)

        context.navigateToInvoiceEditPage(invoices[0].internalid)
        waitForPageToLoad()

        // check present
        assertTrue("Edit Warning dialog present", context.isPopUpPresent())
        // check title
        assertTrue("Title not expected", context.isTextVisible(testData.test_case_2_11_1.expectedResults.warningDialog.title))
        // check warning message
        assertTrue("Warning message expected", context.isTextVisible(testData.test_case_2_11_1.expectedResults.warningDialog.message))
        // accept warning
        context.clickButton(testData.test_case_2_11_1.expectedResults.warningDialog.ok)
        // stay on invoice edit page
        assertTrue("Current page not expected", context.isInvoiceEditPage(invoices[0].internalid))
    }
}