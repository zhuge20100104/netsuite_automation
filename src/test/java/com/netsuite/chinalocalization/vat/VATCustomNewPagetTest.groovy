package com.netsuite.chinalocalization.vat

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
@TestOwner("christina.chen@oracle.com")
class VATCustomNewPagetTest extends VATEditPageTestSuite{

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATCustomNewPageTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATCustomNewPageTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()

    private  def caseData
    private  def caseFilter
    private  def expResult
    def init(){
        //switchToRole(administrator)
        caseData = testData.get(name.getMethodName())
        if ("filter" in caseData) caseFilter = caseData.filter
        if ("expectedResult" in caseData ){expResult = caseData.expectedResult}
        /*cleanAll = false
        if (caseData.containsKey("expectedResult")){expResult = caseData.expectedResult}
        /*
        def records = dirtyData = record.createRecord(caseData.data)
        */
        //switchToRole(accountant)

    }

    /**
     * @desc check custom field for  Customer New Page
     * @case 4.1.2.1 check banck name
     * @case 4.1.2.2 check vat taxpayer types
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_4_1_2() {
        init()
        context.navigateToCustomerNewPage()
        waitForPageToLoad()
        checkAreEqual("Bank Name should show up", context.getFieldLabel("custentity_bank_account_name"),expResult.bankAccount)
        checkAreEqual("vat taxpayer types should show up", context.getFieldLabel("custentity_cn_vat_taxpayer_types"),expResult.taxpayer)
        context.clickLink(expResult.subTab)
        sleep(3 * 1000)
        //def options =  asDropdownList(fieldId: "custentity_cn_vat_taxpayer_types").getOptions()
        def options = context.getCNdropdownOptions("custentity_cn_vat_taxpayer_types")
        options.eachWithIndex { String entry, int i ->
            assertAreEqual("taxpayer type option ${i} ${options[i]} Should ", options[i], expResult.taxpayerOptions[i])
        }
    }

    /**
     * @desc Check initial VAT Invoice Status : Blank - Credit Memo
     * @case 11.2.3 check vat taxpayer types
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_11_2_3() {
        init()
        context.navigateToIssueCreditMemosPage()
        waitForPageToLoad()
        context.clickLink(expResult.subTab)
        sleep(3 * 1000)
        def datas = asText("//table[@id=\"custpage_cn_vat_invoices_splits\"]/tbody/tr[2]/td[1]")
       
        assertAreEqual("no data fond", datas, expResult.message)

        //def options =  asDropdownList(fieldId: "custentity_cn_vat_taxpayer_types").getOptions()

    }

    /**
     * @desc Check initial VAT Invoice Status : Blank - Cash Refund
     * @case 11.2.4 check vat taxpayer types
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_11_2_4() {
        init()
        context.navigateToRefundCashSalesPage()
        waitForPageToLoad()
        context.clickLink(expResult.subTab)
        sleep(3 * 1000)
        def datas = asText("//table[@id=\"custpage_cn_vat_invoices_splits\"]/tbody/tr[2]/td[1]")

        assertAreEqual("no data fond", datas, expResult.message)

        //def options =  asDropdownList(fieldId: "custentity_cn_vat_taxpayer_types").getOptions()

    }





}
