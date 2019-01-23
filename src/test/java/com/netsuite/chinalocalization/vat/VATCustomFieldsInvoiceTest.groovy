package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P2

import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("johnny.niu@oracle.com")
class VATCustomFieldsInvoiceTest extends VATAppTestSuite {

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATCustomFieldsInvoiceTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATCustomFieldsInvoiceTest_en_US.json"
        ]
    }

    /**
     * @desc Test VAT Invoice Type in Create Invoice page.
     * @case 4.2.1.1
     * @author Johnny Niu
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_4_2_1_1() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        context.navigateToCreateInvoicePage()
        aut.clickFormTab(VATTAB)
        assertTrue("Label exist", context.doesLabelExist(testData.labels.fieldLabel.vatinvoicetype))
        assertAreEqual("Label expected", asLabel("custbody_cn_vat_invoice_type"), testData.labels.fieldLabel.vatinvoicetype)
        assertAreEqual("Field type expected", asAttributeValue(locators.vatInvoiceType, "data-fieldtype"), "select")
        assertAreEqual("Illegal Options", asDropdownList(locator: locators.vatInvoiceType).getOptions(), testData.test_case_4_2_1_2.expected.VATInvoiceType)
    }
    /**
     * @desc Test VAT Invoice record columns in Create Invoice page.
     * @case 4.6.1.2 - 4.6.1.7
     * @author Johnny Niu
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_4_2_1_2() {

        // switch to admin
        //switchToRole(administrator)
        def invoices = dirtyData = record.createRecord(testData.test_case_4_2_1_2.data)
        // switch back to accountant
        //switchToRole(accountant)

        navigateToPortalPage()

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(testData.test_case_4_2_1_2.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(testData.test_case_4_2_1_2.filter.invoicetype)
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
        context.clickButton(testData.test_case_4_2_1_2.expected.warningDialog.ok)
        String VATTAB = testData.labels.fieldLabel.tabLabel

        aut.clickFormTab(VATTAB)
        //assertTrue("Label not exist", context.doesLabelExist(testData.labels.fieldLabel.vatStatus))
        //assertAreEqual("Label not expected", asLabel("custbody_cn_info_sheet_number"), testData.labels.fieldLabel.vatinfosheetnumber)
        assertAreEqual("Table Label match", asAttributeValue(locators.vatInvoiceStatusLabel, "data-label"), testData.labels.fieldLabel.vatStatus)
        //assertAreEqual("Illegal Options", asDropdownList(locator: locators.vatInvoiceStatus).getOptions(), testData.test_case_4_2_1_2.expected.VATInvoiceStatus)
        assertAreEqual("Illegal Options", asAttributeValue(locators.vatInvoiceStatus, "data-options"), testData.test_case_4_2_1_2.expected.VATInvoiceStatusString)

        assertNull("Invoice Code Field can input", asAttributeValue(locators.vatInvoiceCode, "type"))
        assertNull("Invoice Number Field can input", asAttributeValue(locators.vatInvoiceNumber, "type"))
        assertNull("Invoice Date Field can input", asAttributeValue(locators.vatInvoiceDate, "type"))
        assertNull("Invoice Tax Amount Field can input", asAttributeValue(locators.vatInvoiceTaxAmount, "type"))
        assertNull("Invoice Tax Exclusive Amount Field can input", asAttributeValue(locators.vatInvoiceTaxExclusiveAmount, "type"))

    }

    /**
     * @desc Test VAT Split Rules in Create Invoices page.
     * @case 12.4.1.1
     * @author Johnny Niu
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_12_4_1_1() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        context.navigateToCreateInvoicePage()
        aut.clickFormTab(VATTAB)
        assertTrue("Label exist", context.doesLabelExist(testData.test_case_12_4_1.expected.VATSplitRule))
        assertAreEqual("Label expected", asLabel("custbody_cn_vat_split_rule"), testData.test_case_12_4_1.expected.VATSplitRule)
        assertAreEqual("Field type expected", asAttributeValue(locators.splitRule, "data-fieldtype"), "select")
        assertNull("Default Value should be null",asAttributeValue(locators.splitRuleInputInvoice, "value"))
        assertAreEqual("Illegal Options", asDropdownList(locator: locators.splitRule).getOptions(), testData.test_case_12_4_1.expected.RuleDetails)

    }
}
