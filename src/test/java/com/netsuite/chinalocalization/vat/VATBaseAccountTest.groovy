package com.netsuite.chinalocalization.vat
import com.netsuite.common.NSError

import com.netsuite.base.report.rerun.op.ErrorAnnotationRemover

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
@TestOwner("christina.chen@oracle.com")
class VATBaseAccountTest extends VATEditPageTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATBaseAccountTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATBaseAccountTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()

    def caseData
    def caseFilter
    def expResult
    def init(){
        caseData = testData.get(name.getMethodName())
        if ("filter" in caseData) caseFilter = caseData.filter
        if ("expectedResult" in caseData ){expResult = caseData.expectedResult}


    }

    /**
     * @desc If no permission should not access the page.
     *   Check error message
     * @case .1 single merge
     * @Regression:  Negtive business workflow
     *
     * @author Christina Chen
     */
    @Category([P3.class,NSError.class,OW.class])
    @Test
    void test_case_1() {

        switchToRole(baseRole)
        navigateToPortalPage()
        waitForPageToLoad()
//        clickRefresh()
        assertTrue("Should show INSUFFICIENT_PERMISSION", context.isTextVisible("INSUFFICIENT_PERMISSION"))
    }
    /**
     * @desc no China subsidiary - CN
     * @case 5.1.1
     * @author Christina Chen
     * @Regression:  Negtive business workflow
     */
    @Category([P3.class,OW.class])
    @Test
    void test_case_5_1_1() {
        init()
        switchToRole(noChianRole)
        navigateToPortalPage()
        waitForPageToLoad()

        assertAreEqual("Refresh button disabled", "true", asAttributeValue(locators.refresh, "disabled"))
        assertAreEqual("Import button enabled",  asAttributeValue(locators.importButton, "disabled"),null)
        assertFalse("Export Button not present", context.doesFieldExist("custpage_export"))
        assertTrue("Warning message expected", context.isTextVisible(expResult.message))
        clickConfirmOk()
    }




}
