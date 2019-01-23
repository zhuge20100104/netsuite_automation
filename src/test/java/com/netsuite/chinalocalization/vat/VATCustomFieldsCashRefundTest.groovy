package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P2

import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("johnny.niu@oracle.com")
class VATCustomFieldsCashRefundTest extends VATAppTestSuite{

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATCustomFieldsCashRefundTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATCustomFieldsCashRefundTest_en_US.json"
        ]
    }
    /**
     * @desc Test VAT Invoice Type in Refund Cash Sales page.
     * @case 4.6.1.1
     * @author Johnny Niu
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_4_6_1_1() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        context.navigateToRefundCashSalesPage()
        aut.clickFormTab(VATTAB)
        assertTrue("Label exist", context.doesLabelExist(testData.labels.fieldLabel.vatinvoicetype))
        assertAreEqual("Label expected", asLabel("custbody_cn_vat_invoice_type"), testData.labels.fieldLabel.vatinvoicetype)
        assertAreEqual("Field type expected", asAttributeValue(locators.vatInvoiceType, "data-fieldtype"), "select")
        assertAreEqual("Illegal Options", asDropdownList(locator: locators.vatInvoiceType).getOptions(), testData.test_case_4_6_1_5.expected.VATInvoiceType)
    }
    /**
     * @desc Test VAT Create From in Refund Cash Sales page.
     * @case 4.6.1.2 and 4.6.1.3
     * @author Johnny Niu
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_4_6_1_2() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        context.navigateToRefundCashSalesPage()
        aut.clickFormTab(VATTAB)
        assertTrue("Label exist", context.doesLabelExist(testData.labels.fieldLabel.vatcreatedfrom))
        assertAreEqual("Label expected", asLabel("custbody_cn_vat_createdfrom"), testData.labels.fieldLabel.vatcreatedfrom)
        assertAreEqual("Field type expected", asAttributeValue(locators.vatCreatedFrom, "type"), "text")

    }
    /**
     * @desc Test VAT Information Sheet Number in Refund Cash Sales page.
     * @case 4.6.1.4
     * @author Johnny Niu
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_4_6_1_4() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        context.navigateToRefundCashSalesPage()
        aut.clickFormTab(VATTAB)
        assertTrue("Label exist", context.doesLabelExist(testData.labels.fieldLabel.vatinfosheetnumber))
        assertAreEqual("Label expected", asLabel("custbody_cn_info_sheet_number"), testData.labels.fieldLabel.vatinfosheetnumber)
        assertAreEqual("Field type expected", asAttributeValue(locators.vatInfoSheetNumber, "type"), "text")
        //assertNull("Default Value should be null",asAttributeValue(locators.vatInvoiceTypeInputRefund, "value"))
        //assertAreEqual("Illegal Options", asDropdownList(locator: locators.vatInvoiceType).getOptions(), testData.test_case_12_4_1.data.VATInvoiceType)
    }
    /**
     * @desc Test VAT Invoice Status in Refund Cash Sales page.
     * Commentted due to Gray "Import" button on VAT trans page. Need to add invoice verification step in future.
     * @case 4.6.1.5
     * @author Johnny Niu
     */
//    @Test
//    void test_case_4_6_1_5() {
//// switch to admin
//        switchToRole(administrator)
//        def invoices = dirtyData = record.createRecord(testData.test_case_4_6_1_5.data)
//        // switch back to accountant
//        switchToRole(accountant)
//
//        navigateToPortalPage()
//
//        if (context.isOneWorld()) {
//            asDropdownList(locator: locators.subsidiary).selectItem(testData.test_case_4_6_1_5.filter.subsidiary)
//        }
//        asDropdownList(locator: locators.invoiceType).selectItem(testData.test_case_4_6_1_5.filter.invoicetype)
//        context.setFieldWithValue("custpage_documentnumberfrom", invoices[0].tranid)
//        context.setFieldWithValue("custpage_documentnumberto", invoices[0].tranid)
//
//        clickRefresh()
//        waitForPageToLoad()
//
//        waitForElement(locators.export)
//        Thread.sleep(10 * 1000)
//        clickExport()
//        Thread.sleep(5 * 1000)
//
//        context.navigateToCashRefundEditPage(invoices[0].internalid)
//        waitForPageToLoad()
//        context.clickButton(testData.test_case_4_6_1_5.expected.warningDialog.ok)
//
//        String VATTAB = testData.labels.fieldLabel.tabLabel
//        //context.navigateToRefundCashSalesPage()
//        aut.clickFormTab(VATTAB)
//        //assertTrue("Label not exist", context.doesLabelExist(testData.labels.fieldLabel.vatStatus))
//        //assertAreEqual("Label not expected", asLabel("custbody_cn_info_sheet_number"), testData.labels.fieldLabel.vatinfosheetnumber)
//        assertAreEqual("Table Label not match", asAttributeValue(locators.vatInvoiceStatusLabel, "data-label"), testData.labels.fieldLabel.vatStatus)
//        //assertAreEqual("Illegal Options", asDropdownList(locator: locators.vatInvoiceStatus).getOptions(), testData.test_case_4_6_1_5.expected.VATInvoiceStatus)
//        assertAreEqual("Illegal Options", asAttributeValue(locators.vatInvoiceStatus, "data-options"), testData.test_case_4_6_1_5.expected.VATInvoiceStatusString)
//        assertNull("Invoice Code Field can input", asAttributeValue(locators.vatInvoiceCode, "type"))
//        assertNull("Invoice Number Field can input", asAttributeValue(locators.vatInvoiceNumber, "type"))
//        assertNull("Invoice Date Field can input", asAttributeValue(locators.vatInvoiceDate, "type"))
//        assertNull("Invoice Tax Amount Field can input", asAttributeValue(locators.vatInvoiceTaxAmount, "type"))
//        assertNull("Invoice Tax Exclusive Amount Field can input", asAttributeValue(locators.vatInvoiceTaxExclusiveAmount, "type"))
//    }
    /**
     * @desc Test VAT Split Rules in Refund Cash Sales page.
     * @case 12.4.1.4
     * @author Johnny Niu
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_12_4_1_4() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        context.navigateToRefundCashSalesPage()
        aut.clickFormTab(VATTAB)
        assertTrue("Label exist", context.doesLabelExist(testData.test_case_12_4_1.expected.VATSplitRule))
        assertAreEqual("Label expected", asLabel("custbody_cn_vat_split_rule"), testData.test_case_12_4_1.expected.VATSplitRule)
        assertAreEqual("Field type expected", asAttributeValue(locators.splitRule, "data-fieldtype"), "select")
        assertNull("Default Value should be null",asAttributeValue(locators.splitRuleInputRefund, "value"))
        assertAreEqual("Illegal Options", asDropdownList(locator: locators.splitRule).getOptions(), testData.test_case_12_4_1.expected.RuleDetails)

    }
}