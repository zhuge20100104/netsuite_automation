package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
@TestOwner ("yanyan.yu@oracle.com")
class VATInvalidCustomerTest extends VATAppTestSuite {

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATInvalidCustomerTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATInvalidCustomerTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()

    def caseData
    def caseFilter
    def expResult
    def records
    def init(){

        caseData = testData.get(name.getMethodName())
        if ("filter" in caseData) caseFilter = caseData.filter
        if ("expectedResults" in caseData) expResult = caseData.expectedResults
        //switchToRole(administrator)
        records= dirtyData = record.createRecord(caseData.data)
        //switchToRole(accountant)
    }
    def initNoCreate(){

        caseData = testData.get(name.getMethodName())
        if ("filter" in caseData) caseFilter = caseData.filter
        if ("expectedResults" in caseData) expResult = caseData.expectedResults
/*        //switchToRole(administrator)
        records= dirtyData = record.createRecord(caseData.data)
        //switchToRole(accountant)*/

    }

    /**
     * @desc Some Mandatory fields (Tax Reg. Number, Address and Phone, Bank Account) are missing from customer.
     * This case is specified for common invoice.
     * Verify the error message for Tax Reg. Number missing.
     * @case 6.4.1.1
     * @author Yanyan Yu
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_6_4_1_1() {
        init()
        navigateToPortalPage()
        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(editData.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(editData.filter.invoicetype_c)
        context.setFieldWithValue("custpage_documentnumberfrom", records[1].tranid)
        context.setFieldWithValue("custpage_documentnumberto", records[1].tranid)
        clickRefresh()
        waitForElement(locators.export)
        Thread.sleep(5 * 1000)

        assertTrue("Tax Reg. Number missing", context.isTextVisible(expResult.tax_reg_num_missing))
    }

    /**
     * @desc Some Mandatory fields (Tax Reg. Number, Address and Phone, Bank Account) are missing from customer.
     * This case is specified for special invoice.
     * Verify the error message for Tax Reg. Number, bank, address, phone missing
     * @case 6.4.1.3
     * @author Yanyan Yu
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_6_4_1_3() {
        init()
        navigateToPortalPage()

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(editData.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(editData.filter.invoicetype_s)
        context.setFieldWithValue("custpage_documentnumberfrom", records[1].tranid)
        context.setFieldWithValue("custpage_documentnumberto", records[1].tranid)

        clickRefresh()
        waitForElement(locators.export)
        Thread.sleep(10 * 1000)

        assertTrue("Tax Reg. Number,bank,phone and address missing", context.isTextVisible(expResult.customer_fields_missing))
    }
    /**
     * @desc  Warning Message when the length of the fields exceed the limit - OW CN
     Transactions: Invoice
     Normal transaction without error message
     Duplicated overlength fields exist.
     Partial field overlengthed
     * @case 2.12.1
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_2_12_1() {
        initNoCreate()
        navigateToPortalPage()


        context.with(){
            if (isOneWorld()) {
                asDropdownList(locator: locators.subsidiary).selectItem(editData.filter.subsidiary)
            }
            asDropdownList(locator: locators.invoiceType).selectItem(editData.filter.invoicetype_s)
            setFieldWithValue("custpage_datefrom", '11/30/2017')
            setFieldWithValue("custpage_dateto", '11/30/2017')
            setFieldWithValue("custpage_documentnumberfrom", '10601')
            setFieldWithValue("custpage_documentnumberto", '10601')
        }


        clickRefresh()
        waitForElement(locators.export)
        Thread.sleep(5 * 1000)
        clickExport()
        assertTrue("The value for {Customer Bank Account} has exceeded the 100 limit.", context.isTextVisible(expResult.errmsg1))
        assertTrue("The value for {Item Name} has exceeded the 92 limit.", context.isTextVisible(expResult.errmsg2))
        assertTrue("The value for {UOM} has exceeded the 22 limit.", context.isTextVisible(expResult.errmsg3))
    }
}
