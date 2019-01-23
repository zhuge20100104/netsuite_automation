package com.netsuite.chinalocalization.voucher

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("jingzhou.wang@oracle.com")
class VoucherPrintCustomFieldTest extends VoucherPrintBaseTest {

    @Inject
    protected EnableFeaturesPage enableFeaPage

    def jsonFile = "/voucher/case_5_1.json"
    def caseObj
    def response

    @Before
    void setUp() {
        super.setUp();
        enableFeaPage.enableAllCustomFilters()
        //Check if all 3 feature enabled
        boolean allEnable = context.isAllClassificationEnabled()
        assertTrue("All classification should be ENABLED!", allEnable)
    }

    @After
    void tearDown() {
        if (response) {
            context.deleteTransaction(response)
        }
        super.tearDown()
    }

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 5.1.1
     * @desc Single currency template  -> Customized fields show
     *       Multi currencies template -> Customized fields disabled and unchecked
     */
    @Test
    @Category([OW.class, P0.class])
    void case_5_1_1() {
        voucherPrintPage.navigateToURL()

        //Switch to Single Currency template and check all 3 fields could be checked
        voucherPrintPage.changeTemplate(SINGLE_CURRENCY)
        voucherPrintPage.checkAllCustomField()
        checkTrue("Print Department should be checked.", voucherPrintPage.isPrintDeparmentChecked())
        checkTrue("Print Location should be checked.", voucherPrintPage.isPrintLocationChecked())
        checkTrue("Print Class should be checked.", voucherPrintPage.isPrintClassChecked())

        //Switch to Multi-Currencies template and check all 3 field is disabled and unchecked
        voucherPrintPage.changeTemplate(MULTI_CURRENCIES)
        println "checked: " + voucherPrintPage.isPrintDeparmentChecked()
        println "disabled: " + voucherPrintPage.isPrintDeparmentDisabled()
        checkTrue("Print Department should be unchecked and disabled.", !voucherPrintPage.isPrintDeparmentChecked() && voucherPrintPage.isPrintDeparmentDisabled())
        checkTrue("Print Location should be unchecked and disabled.", !voucherPrintPage.isPrintLocationChecked() && voucherPrintPage.isPrintLocationDisabled())
        checkTrue("Print Class should not be unchecked and disabled.", !voucherPrintPage.isPrintClassChecked() && voucherPrintPage.isPrintClassDisabled())
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 5.1.2
     * @desc Scenario 1:Enable Location & Class, Disable Department
     *       Scenario 2:Enable Location & Department , Disable Class
     */
    @Test
    @Category([OW.class, P1.class])
    void case_5_1_2() {
        switchToRole(getAdministrator())

        /*Scenario 1:Enable Location & Class, Disable Department*/
        enableFeaPage.enableAllCustomFilters()
        enableFeaPage.navigateToURL()
        enableFeaPage.disbleDepartments()
        enableFeaPage.clickSave()

        //In VoucherPrint page, check that Department is not show on page
        voucherPrintPage.navigateToURL()
        voucherPrintPage.changeTemplate(SINGLE_CURRENCY)
        checkTrue("Print Department should not show on page.", !voucherPrintPage.isPrintDepartmentExist())
        checkTrue("Print Location should show on page.", voucherPrintPage.isPrintLoacationExist())
        checkTrue("Print Class should show on page.", voucherPrintPage.isPrintClassExist())
        voucherPrintPage.clickRefreshBtn()

        /*Scenario 2:Enable Location & Department , Disable Class*/
        enableFeaPage.enableAllCustomFilters()
        enableFeaPage.navigateToURL()
        enableFeaPage.disbleClasses()
        enableFeaPage.clickSave()
        //In VoucherPrint page, check that Class is not show on page
        voucherPrintPage.navigateToURL()
        voucherPrintPage.changeTemplate(SINGLE_CURRENCY)
        checkTrue("Print Department should show on page.", voucherPrintPage.isPrintDepartmentExist())
        checkTrue("Print Location should show on page.", voucherPrintPage.isPrintLoacationExist())
        checkTrue("Print Class should not show on page.", !voucherPrintPage.isPrintClassExist())
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 5.1.3
     * @desc Use Single-Currency template to print Voucher, customized field should show in Account column
     */
    @Test
    @Category([OW.class, P1.class])
    void case_5_1_3() {
        caseObj = getCaseObj(jsonFile, "testcase_5.1.3")
        response = createJournalOnBackendUnderEnglish("testcase_5.1.3")

        voucherPrintPage.navigateToURL()
        voucherPrintPage.setParameters(caseObj.ui.voucherPrintParams, SINGLE_CURRENCY)
        voucherPrintPage.checkAllCustomField()
        voucherPrintPage.clickRefreshBtn()

        //Check custom field show and separate by '/'
        verifyReportContent(caseObj.expected)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 5.1.4
     * @desc Use Multi-Currencies template to print Voucher, customized field should not show
     */
    @Test
    @Category([OW.class, P2.class])
    void case_5_1_4() {
        caseObj = getCaseObj(jsonFile, "testcase_5.1.4")
        response = createJournalOnBackendUnderEnglish("testcase_5.1.4")

        voucherPrintPage.navigateToURL()
        voucherPrintPage.setParameters(caseObj.ui.voucherPrintParams, MULTI_CURRENCIES)
        voucherPrintPage.checkAllCustomField()
        voucherPrintPage.clickRefreshBtn()

        //Check custom field not show if choose multi-currencies template
        verifyReportContent(caseObj.expected)
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 5.1.5
     * @desc Check all 3 customize fields
     *          If total length of customized fields exceed 25 per line
     *             * In HTML,Check custom field will not trancated
     *             * In PDF, Check custom field will be trancated
     */
    @Test
    @Category([OW.class, P1.class])
    void case_5_1_5() {
        caseObj = getCaseObj(jsonFile, "testcase_5.1.5")
        response = createJournalOnBackendUnderEnglish("testcase_5.1.5")

        voucherPrintPage.navigateToURL()
        voucherPrintPage.setParameters(caseObj.ui.voucherPrintParams, SINGLE_CURRENCY)
        voucherPrintPage.checkAllCustomField()
        voucherPrintPage.clickRefreshBtn()

        //In HTML,Check custom field will not trancated if exceed 25 per line
        verifyReportContent(caseObj.expected)

        //Todo: In PDF,Check custom field will trancated if exceed 25 per line
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 5.1.6
     * @desc Check 2 customize field(Department and Location)
     *           If total length of customized fields exceed 25 per line
     *            * In HTML,Check custom field will not trancated
     *            * In PDF, Check custom field will be trancated
     *             Verify customized fields show in account column(2 fields)
     */
    @Test
    @Category([OW.class, P1.class])
    void case_5_1_6() {
        caseObj = getCaseObj(jsonFile, "testcase_5.1.6")
        response = createJournalOnBackendUnderEnglish("testcase_5.1.6")

        voucherPrintPage.navigateToURL()
        voucherPrintPage.setParameters(caseObj.ui.voucherPrintParams, SINGLE_CURRENCY)
        voucherPrintPage.checkPrintDepartment()
        voucherPrintPage.checkPrintLocation()
        voucherPrintPage.clickRefreshBtn()

        //In HTML,Check custom field will not trancated if exceed 25 per line
        verifyReportContent(caseObj.expected)

        //Todo: In PDF,Check custom field will not trancated if not exceed 25 per line
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 5.1.7
     * @desc Check 1 customize field(Class)
     *           If total length of customized fields exceed 25 per line
     *             * In HTML,Check custom field will not trancated
     *             * In PDF, Check custom field will be trancated
     *             Verify customized fields show in account column(1 field)
     */
    @Test
    @Category([OW.class, P2.class])
    void case_5_1_7() {
        caseObj = getCaseObj(jsonFile, "testcase_5.1.7")
        response = createJournalOnBackendUnderEnglish("testcase_5.1.7")

        voucherPrintPage.navigateToURL()
        voucherPrintPage.setParameters(caseObj.ui.voucherPrintParams, SINGLE_CURRENCY)
        voucherPrintPage.checkPrintClass()  //Only print class
        voucherPrintPage.clickRefreshBtn()

        //In HTML,Check custom field will not trancated if exceed 25 per line
        verifyReportContent(caseObj.expected)

        //Todo: In PDF, check custom field trancated if exceed 25 per line
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID 5.2.1
     * @desc Verify account name over 25 character will wrap to 2nd line
     */
    @Test
    @Category([OW.class, P2.class])
    void case_5_2_1() {
        caseObj = getCaseObj(jsonFile, "testcase_5.2.1")
        response = createJournalOnBackendUnderEnglish("testcase_5.2.1")

        voucherPrintPage.navigateToURL()
        voucherPrintPage.setParameters(caseObj.ui.voucherPrintParams, SINGLE_CURRENCY)
        voucherPrintPage.checkAllCustomField()
        voucherPrintPage.clickRefreshBtn()

        //In HTML,Check custom field will not trancated if exceed 25 per line
        verifyReportContent(caseObj.expected)

        //Todo: In PDF,check account line trancated if exceed 25 per line
    }


    def createJournalOnBackendUnderEnglish(String testcase) {
        changeLanguageToEnglish()
        def response = createTransaction(jsonFile, testcase)
        if (isTargetLanguageChinese()) {
            changeLanguageToChinese()
        }
        return response
    }

    void verifyReportContent(def expectedDataObj) {
        if (expectedDataObj.voucherReportBody) {
            for (def expectedLineObj : expectedDataObj.voucherReportBody) {
                def reportLineObj = voucherPrintPage.getReportLineOnBody(0, expectedLineObj)
                if (expectedLineObj.acctDesp) {
                    checkAreEqual("Account should show correctly", reportLineObj.acctDesp, expectedLineObj.acctDesp)
                }
            }
        }
    }
}


