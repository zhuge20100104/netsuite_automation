package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("yanyan.yu@oracle.com")
class VATTransactionNoVatTest extends VATAppTestSuite {

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATTransactionNoVatTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATTransactionNoVatTest_en_US.json"
        ]
    }

    /**
     * @desc Common Credit Memo applying with invoices which have no VAT info.
     * Verify invoice selected does not have VAT Information or has remarks that are in incorrect format.
     * @case 6.4.3.5
     * @author Yanyan Yu
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_6_4_3_5() {
        def caseData = testData["test_case_6_4_3_5"]

        //switchToRole(administrator)
        def records = dirtyData = record.createRecord(caseData.data)
        //switchToRole(accountant)

        navigateToPortalPage()

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(caseData.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(caseData.filter.invoicetype)
        context.setFieldWithValue("custpage_documentnumberfrom", records[1].tranid)
        context.setFieldWithValue("custpage_documentnumberto", records[1].tranid)

        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.export)
        Thread.sleep(10 * 1000)

        assertTrue("Applied invoice has no VAT", context.isTextVisible(testData["test_case_6_4_3_5"].expectedResults.missing_vat))
    }

    /**
     * @desc Common Cash Refund create from cash sale which has no VAT info.
     * Verify invoice selected does not have VAT Information or has remarks that are in incorrect format.
     * @case 6.4.4.3
     * @author Yanyan Yu
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_6_4_4_3() {
        def caseData = testData["test_case_6_4_4_3"]

        //switchToRole(administrator)
        def records = dirtyData = record.createRecord(caseData.data)
        //switchToRole(accountant)

        navigateToPortalPage()

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(caseData.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(caseData.filter.invoicetype)
        context.setFieldWithValue("custpage_documentnumberfrom", records[1].tranid)
        context.setFieldWithValue("custpage_documentnumberto", records[1].tranid)

        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.export)
        Thread.sleep(10 * 1000)

        assertTrue("Cash Sale has no VAT", context.isTextVisible(testData["test_case_6_4_4_3"].expectedResults.missing_vat))
    }
}
