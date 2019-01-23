package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.cashflow.CONSTANTS.VoucherEnum
import com.netsuite.testautomation.aut.PageMenu
import groovy.json.JsonSlurper
import net.qaautomation.common.HumanReadableDuration
import org.junit.After
import org.junit.Assert
import org.junit.Test

class VoucherPrintReportTest extends VoucherPrintBaseTest {

    def responseFromSuitelet = ""
    def responseFromUI = [:]

    @After
    void tearDown() {
        if (!responseFromSuitelet.isEmpty() || !responseFromUI.isEmpty())
            switchToRole(getAdministrator())
        if (!responseFromSuitelet.isEmpty()) {
            deleteTransactionBySuitelet()
            responseFromSuitelet = ""
        }
        if (!responseFromUI.isEmpty()) {
            deleteTransactionFromUI()
            responseFromUI = [:]
        }
        super.tearDown()
    }

    def createTransactionBySuitelet(String caseId, String fileName) {
        String url = context.resolveURL("customscript_sl_cn_ui_create_trans", "customdeploy_sl_cn_ui_create_trans")
        Assert.assertFalse(url.equals("You have provided an invalid script id or internal id: customscript_sl_cn_ui_create_trans"))
        responseFromSuitelet = context.createTransaction(caseId, fileName, url)
        context.navigateTo(CURL.HOME_CURL)
        return responseFromSuitelet
    }

    void deleteTransactionBySuitelet() {
        Assert.assertTrue(context.deleteTransaction(responseFromSuitelet))
        Assert.assertTrue(context.postDeleteCheck(responseFromSuitelet))
    }

    void createTransactionFromUI(String transactionUrl, def caseDataObj) {
        context.navigateTo(transactionUrl)
        context.waitForConditionsUptoAMaxOf(new HashMap(), HumanReadableDuration.parse("3 secs"))

        if (caseDataObj != null) {
            caseDataObj.each {
                if (it.key.contains("account") || it.key.contains("subsidiary")) {
                    context.setFieldWithText(it.key, it.value)
                } else {
                    context.setFieldWithValue(it.key, it.value)
                }
            }
            context.waitForPageToLoad()
            context.clickButton(SAVE)
            context.waitForPageToLoad()
            responseFromUI[transactionUrl] = context.getParameterValueForFromQueryString("id")
        }
    }

    void deleteTransactionFromUI() {
        responseFromUI.each {
            context.navigateTo(it.key + "?e=T&id=" + it.value)
            context.acceptUpcomingConfirmation()
            context.selectFromMenu(DELETE, PageMenu.ACTIONS)
        }
    }

    void verifyReportContent(def expectedDataObj) {
        if (expectedDataObj.voucherReportMain) {
            def actualReportMain = voucherPrintPage.getVoucherReportMain(0)
            if (expectedDataObj.voucherReportMain.subsidiary) {
                assertAreEqual("Subsidiary should be correct", actualReportMain.subsidiary, expectedDataObj.voucherReportMain.subsidiary)
            }
            assertAreEqual("Transaction Type should be correct", actualReportMain.tranType, parseTestData(expectedDataObj.voucherReportMain.tranType))
            assertAreEqual("Date should be correct", actualReportMain.date, expectedDataObj.voucherReportMain.date)
            assertAreEqual("Posting Period should be correct", actualReportMain.postingPeriod, expectedDataObj.voucherReportMain.postingPeriod)
            assertAreEqual("Currency should be correct", actualReportMain.currency, expectedDataObj.voucherReportMain.currency)
        }

        if (expectedDataObj.voucherReportFooter) {
            def actualReportFooter = voucherPrintPage.getVoucherReportFooter(0)
            assertAreEqual("Creator should be correct", actualReportFooter.creator, expectedDataObj.voucherReportFooter.creator)
            assertAreEqual("Approver should be correct", actualReportFooter.approver, expectedDataObj.voucherReportFooter.approver)
            assertAreEqual("Poster should be correct", actualReportFooter.poster, expectedDataObj.voucherReportFooter.poster)
        }

        // Add for Printing template
        if (expectedDataObj.voucherReportBody) {
            for (def expectedLineObj : expectedDataObj.voucherReportBody) {
                def reportLineObj = voucherPrintPage.getReportLineOnBody(0, expectedLineObj)
                if (expectedLineObj.lineDesp) {
                    assertAreEqual("Line Description should be correct", reportLineObj.lineDesp, expectedLineObj.lineDesp)
                }
                if (expectedLineObj.acctDesp) {
                    assertAreEqual("Account should be correct", reportLineObj.acctDesp, expectedLineObj.acctDesp)
                }
                if (expectedLineObj.tranCcy) {
                    assertAreEqual("Currency should be correct", reportLineObj.tranCcy, expectedLineObj.tranCcy)
                }
                if (expectedLineObj.rate) {
                    assertAreEqual("Rate should be correct", reportLineObj.rate, expectedLineObj.rate)
                }
                if (expectedLineObj.tranCcyDebit) {
                    assertAreEqual("Transaction_Debit should be correct", reportLineObj.tranCcyDebit, expectedLineObj.tranCcyDebit)
                }
                if (expectedLineObj.tranCcyCredit) {
                    assertAreEqual("Transaction_Credit should be correct", reportLineObj.tranCcyCredit, expectedLineObj.tranCcyCredit)
                }
                if (expectedLineObj.funcCcyDebit) {
                    assertAreEqual("Functional_Debit should be correct", reportLineObj.funcCcyDebit, expectedLineObj.funcCcyDebit)
                }
                if (expectedLineObj.funcCcyCredit) {
                    assertAreEqual("Credit should be correct", reportLineObj.funcCcyCredit, expectedLineObj.funcCcyCredit)
                }
            }
        }
    }

    void verifyTemplateLOV() {
        def defValue = voucherPrintPage.getValueByFieldId("template")
        if (isTargetLanguageEnglish()) {
            assertAreEqual("Print template LOV should be correct as in en_US", defValue, VoucherEnum.VOUCHER_MULT_CURRENCIES_TEMPLATE.getEnLabel())
        } else if (isTargetLanguageChinese()) {
            assertAreEqual("Print template LOV should be correct as in zh_CN", defValue, VoucherEnum.VOUCHER_MULT_CURRENCIES_TEMPLATE.getCnLabel())
        }

        def expectCN = []
        expectCN.add(VoucherEnum.VOUCHER_SINGLE_CURRENCY_TEMPLATE.getCnLabel())
        expectCN.add(VoucherEnum.VOUCHER_MULT_CURRENCIES_TEMPLATE.getCnLabel())
        def expectEN = []
        expectEN.add(VoucherEnum.VOUCHER_SINGLE_CURRENCY_TEMPLATE.getEnLabel())
        expectEN.add(VoucherEnum.VOUCHER_MULT_CURRENCIES_TEMPLATE.getEnLabel())
        def actual = voucherPrintPage.getListOfValueByFieldId("template")

        if (isTargetLanguageEnglish()) {
            assertAreEqual("Print template LOV should be correct as in en_US", actual, expectEN)
        } else if (isTargetLanguageChinese()) {
            assertAreEqual("Print template LOV should be correct as in zh_CN", actual, expectCN)
        }
    }

    void verifyVoucherPrintReportData(def caseDataObj) {
        voucherPrintPage.setParameters(caseDataObj.ui.voucherPrintParams, parseTestData(caseDataObj.ui.voucherPrintParams.template))
        voucherPrintPage.clickRefreshBtn()
        verifyReportContent(caseDataObj.expected)
    }

    /**
     * @Author yuanfang.chi@oracle.com
     * @CaseID Voucher 1.13.2.2
     * @Author jing.han@oracle.com
     * @CaseID Voucher 1.18.1.1
     * @CaseID Voucher 1.18.1.2
     * @CaseID Voucher 1.3.1.1.7
     */
    @Test
    void case_1_13_2_2_1_18() {
        createTransactionBySuitelet("test case 1.13.2.2", "voucher//cn_voucher_report_data.json")

        String caseData = new File('data//voucher//' + "cn_voucher_report_data.json").getText('UTF-8')
        def testDataObj = new JsonSlurper().parseText(caseData)

        super.voucherPrintPage.navigateToPage(super.URL)

        //Verify template LOV - case 1.3.1.1.7
        verifyTemplateLOV()

        def caseDataObj
        caseDataObj = testDataObj["test case 1.13.2.2"]
        verifyVoucherPrintReportData(caseDataObj)

        caseDataObj = testDataObj["test case 1.18.1.1"]
        verifyVoucherPrintReportData(caseDataObj)

        caseDataObj = testDataObj["test case 1.18.1.2"]
        verifyVoucherPrintReportData(caseDataObj)
    }

    /**
     * @Author yuanfang.chi@oracle.com
     * @CaseID Voucher 1.13.1.1
     */
    @Test
    void case_1_13_1_1() {
        String caseData = new File('data//voucher//' + "cn_voucher_report_data.json").getText('UTF-8')
        def testDataObj = new JsonSlurper().parseText(caseData)
        def caseDataObj = testDataObj["test case 1.13.1.1"]
        createTransactionFromUI("/app/accounting/transactions/transfer.nl", caseDataObj.data)
        super.voucherPrintPage.navigateToPage(super.URL)

        verifyVoucherPrintReportData(caseDataObj)
    }

}