package com.netsuite.chinalocalization.vat

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.SI
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("jianwei.liu@oracle.com")
class VATMaxInvoiceAmountTest extends VATAppTestSuite {

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATMaxInvoiceAmountTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATMaxInvoiceAmountTest_en_US.json"
        ]
    }

    def login() {
        try {
            loginAsRole(roles.zhCN.Administrator)
        } catch (e) {
            loginAsRole(roles.enUS.Administrator)
        }
    }

    /**
     * @desc Test max invoice amount in OW subsidiary page.
     * @case 12.1.1.1
     * @author Jianwei Liu
     */
    @Category([P0.class,OW.class])
    @Test
    void test_case_12_1_1_1() {
        if (!context.isOneWorld()) {
            return
        }

        context.navigateToSubsidiaryNewPage()
        waitForPageToLoad()

        assertTrue("Label not exist", context.doesLabelExist(testData.test_case_12_1_1_1.data.MaxInvoiceAmount))
        assertAreEqual("Label not expected", asLabel("custrecord_cn_vat_max_invoice_amount"), testData.test_case_12_1_1_1.data.MaxInvoiceAmount)
        assertAreEqual("Field type not expected", asAttributeValue(locators.maxInvoiceAmountField, "type"), "text")
        assertNull("Field value not expected", asFieldText("custrecord_cn_vat_max_invoice_amount"))
    }

    /**
     * @desc Test max invoice amount in SI company page.
     * @case 12.1.2.1
     * @author Jianwei Liu
     */
    @Category([P0.class,SI.class])
    @Test
    void test_case_12_1_2_1() {
        if (!context.isSingleInstance()) {
            return
        }

        context.navigateToCompanyInformationPage()
        waitForPageToLoad()

        assertTrue("Label not exist", context.doesLabelExist(testData.test_case_12_1_1_1.data.MaxInvoiceAmount))
        assertAreEqual("Label not expected", asLabel("custrecord_cn_vat_max_invoice_amount"), testData.test_case_12_1_1_1.data.MaxInvoiceAmount)
        assertAreEqual("Field type not expected", asAttributeValue(locators.maxInvoiceAmountField, "type"), "text")
        assertNull("Field value not expected", asFieldText("custrecord_cn_vat_max_invoice_amount"))
    }
}
