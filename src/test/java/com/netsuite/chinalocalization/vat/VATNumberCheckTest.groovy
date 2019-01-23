package com.netsuite.chinalocalization.vat

import com.google.inject.Inject
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.common.OW
import com.netsuite.common.P3
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.openqa.selenium.Alert
import org.junit.experimental.categories.Category

class VATNumberCheckTest extends VATEditPageTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATNumberCheck_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATNumberCheck_en_US.json"
        ]
    }
    @Inject
    AlertHandler alertHandler
    @Rule
    public  TestName name = new TestName()
    def caseData
    def initData(){
        caseData=testData.get(name.getMethodName())
    }

    /**
     * @desc Number Check - Negative number - CN
     * create a new subsidiary with negative vat max number
     * @case 12.1.3.2
     * @author zhen tang
     */
    @Test
    @Category([OW.class, P3.class])
    void test_case_12_1_3_2(){
        initData()
        //switchToRole(administrator)
        context.navigateToSubsidiaryNewPage()
        context.setFieldWithValue("custrecord_cn_vat_max_invoice_amount","-1234")
        Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver, 10)
        assertAreEqual("check alert message",caseData.data.message,alert.getText())

    }
}
