package com.netsuite.chinalocalization.cashflow

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.cashflow.CONSTANTS.CashFlowEnum
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.aut.PageMenu
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonSlurper
import net.qaautomation.common.HumanReadableDuration
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category
import org.openqa.selenium.Keys
import groovy.json.JsonOutput

@TestOwner("yuanfang.chi@oracle.com")
class CashflowPromotionTest extends CashflowBaseTest {

    def responseFromUI = [:]
    def transactionRefidMap = [:]

    def getDefaultRole() {
        return getAdministrator()
    }

    @After
    public void tearDown() {
        if (!responseFromUI.isEmpty()) {
            def jsonSlurper = new JsonSlurper()
            def responseObj = jsonSlurper.parseText('{ }')
            responseFromUI.each {
                responseObj += [internalid: it.value.toInteger(), trantype: it.key]
            }
            def response = JsonOutput.toJson(responseObj)
            deleteTransaction(response);
            responseFromUI = [:]
        }
        super.tearDown()
    }

    public void createTransactionFromUI(def caseDataObj) {
        if (caseDataObj.data != null) {
            caseDataObj.data.each { data ->
                def refid = ""
                def transactiontype = ""
                data.each {
                    if (it.key == "main") {
                        if (it.value.trantype == 'invoice') {
                            transactiontype = it.value.trantype
                            context.navigateTo(CURL.INVOICE_CURL)
                            if (it.value.id != null) {
                                refid = it.value.id
                            }
                        }
                        if (it.value.trantype == 'customerpayment') {
                            transactiontype = it.value.trantype
                            context.navigateTo(CURL.CUSTOMER_PAYMENT_CURL)
                            refid = "payingTran"
                        }
                        fillTransactionMain(it.value)
                    }
                    if (it.key == "item") {
                        if (context.doesFormTabExist(ITEMTAB)) {
                            context.clickFormTab(ITEMTAB)
                        }
                        EditMachineCN machine = context.withinEditMachine("item")

                        it.value.eachWithIndex { item, i ->
                            int lineNumber = i + 1
                            machine.setCurrentLine(lineNumber);
                            fillTransactionLine(machine, item)
                            context.clickButton(ADD)
                        }
                    }
                    if (it.key == "promotions") {
                        if (context.doesFormTabExist(PROMOTIONTAB)) {
                            context.clickFormTab(PROMOTIONTAB)
                        }
                        EditMachineCN machine = context.withinEditMachine("promotions")

                        it.value.eachWithIndex { promotion, i ->
                            int lineNumber = i + 1
                            machine.setCurrentLine(lineNumber)
                            fillTransactionLine(machine, promotion)
                            context.clickButton(ADD)
                        }
                    }
                    if (it.key == "apply") {
                        it.value.each { apply ->
                            fillApplySublist(apply)
                        }
                    }
                }
                context.waitForPageToLoad()
                context.clickButton(SAVE)
                context.waitForPageToLoad()
                responseFromUI[transactiontype] = context.getParameterValueForFromQueryString("id")
                if (refid.contains("payingTran")) {
                    transactionRefidMap[refid] = context.getParameterValueForFromQueryString("id")
                } else {
                    context.getTextIdentifiedBy(Locator.xpath("//*[@id=\"main_form\"]/table/tbody/tr[1]/td/div[1]/div[4]/div[1]"))
                    transactionRefidMap[refid] = context.getTextIdentifiedBy(Locator.xpath("//*[@id=\"main_form\"]/table/tbody/tr[1]/td/div[1]/div[4]/div[1]"))
                }
            }
        }
    }

    void fillTransactionMain(def transactionMain) {
        transactionMain.each {
            if (it.key.equals("trantype") || it.key.equals("id")) {
                return
            }
            if (it.key.equals("entity") || it.key.equals("customer")) {
                String customerId = Utility.getEntityId(context, "customer", it.value)
                context.setFieldWithValue(it.key, customerId)
                context.waitForConditionsUptoAMaxOf(new HashMap(), HumanReadableDuration.parse("10 sec"))
                return
            }

            if (it.key.equals("postingperiod")) {
                context.setFieldWithText(it.key, it.value)
                return
            }

            if (it.key.equals("account")) {
                context.setFieldWithText(it.key, it.value)
                context.waitForConditionsUptoAMaxOf(new HashMap(), HumanReadableDuration.parse("2 sec"))
                return
            }

            if (it.key.equals("currency")) {
                context.setFieldWithText(it.key, it.value)
                return
            }

            if (it.key.equals('location')) {
                context.setFieldWithText(it.key, it.value)
                return
            }

            if (it.key.equals("custbody_cseg_cn_cfi")) {
                if (LANGUAGE.equals("zh_CN")) {
                    context.setFieldWithText(it.key, CashFlowEnum.getCnLabel(it.value))
                } else {
                    context.setFieldWithText(it.key, it.value)
                }
                return
            }
            context.setFieldWithValue(it.key, it.value)
        }
    }

    void fillTransactionLine(EditMachineCN machine, def transactionLine) {
        transactionLine.each {
            if (it.key.equals('item')) {
                machine.setFieldWithText(it.key, it.value)
                context.waitForConditionsUptoAMaxOf(new HashMap(), HumanReadableDuration.parse("2 sec"))
                return
            }

            if (it.key.equals("custcol_cseg_cn_cfi")) {
                if (it.value == "") {
                    machine.setFieldWithValue(it.key, "")
                } else if (LANGUAGE.equals("zh_CN")) {
                    machine.setFieldWithText(it.key, CashFlowEnum.getCnLabel(it.value))
                } else {
                    machine.setFieldWithText(it.key, it.value)
                }
                return
            }

            if (it.key.equals('promocode')) {
                DropdownList promotionDropdown = new DropdownList(context, context.webDriver, Locator.xpath("//span[@id='promotions_promocode_fs']"));
                promotionDropdown.setValue((String) it.value)
                context.waitForConditionsUptoAMaxOf(new HashMap(), HumanReadableDuration.parse("2 sec"))
                return
            }
            machine.setFieldWithValue(it.key, it.value)
        }
    }

    void fillApplySublist(def apply) {
        def tranId = transactionRefidMap[apply.refid]
        apply.each {
            if (it.key.equals('refid')) {
                def checkBoxXpath = ".//*[@id='apply_div']/table[@id='apply_splits']/*/tr/td[text()='" + tranId + "']/../td[span[@class='checkbox_unck']]/span";
                context.webDriver.getHtmlElementByLocator(Locator.xpath(checkBoxXpath)).scrollToView();
                context.webDriver.getHtmlElementByLocator(Locator.xpath(checkBoxXpath)).click();
            }

            if (it.key.equals('amount')) {
                def amountXpath = ".//*[@id='apply_div']/table[@id='apply_splits']/*/tr/td[text()='" + tranId + "']/../td[span[contains(@id, 'apply_amount')]]/span/input"
                context.webDriver.getHtmlElementByLocator(Locator.xpath(amountXpath)).clear();
                context.webDriver.getHtmlElementByLocator(Locator.xpath(amountXpath)).sendKeys(it.value + Keys.ENTER);
                context.waitForPageToLoad();
            }
        }
    }

    void deleteTransactionFromUI() {
        responseFromUI.reverseEach {
            context.navigateTo(it.key + "?e=T&id=" + it.value)
            context.acceptUpcomingConfirmation();
            context.selectFromMenu(DELETE, PageMenu.ACTIONS)
        }
    }

    /**
     * @Author yuanfang.chi@oracle.com
     * @CaseID Cashflow 1.27.1.3 Standard Promotion
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_27_1_3() {
        String caseData = new File('data//cashflow//' + "case_1_27_1_3_data.json").getText('UTF-8')
        def jsonSlurper = new JsonSlurper();
        def caseDataObj = jsonSlurper.parseText(caseData);

        createTransactionFromUI(caseDataObj)

        context.waitForConditionsUptoAMaxOf(new HashMap(), HumanReadableDuration.parse("20 sec"))

        if (transactionRefidMap["payingTran"] != null) {
            Long tranid = Long.valueOf((String) transactionRefidMap["payingTran"])
            checkCollectedCFS("customerpayment", tranid, caseDataObj.expect)
        }
    }

    /**
     * @Author yuanfang.chi@oracle.com
     * @CaseID Cashflow 1.27.1.5 Order Promotion
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_27_1_5() {
        String caseData = new File('data//cashflow//' + "case_1_27_1_5_data.json").getText('UTF-8')
        def jsonSlurper = new JsonSlurper();
        def caseDataObj = jsonSlurper.parseText(caseData);

        createTransactionFromUI(caseDataObj)

        context.waitForConditionsUptoAMaxOf(new HashMap(), HumanReadableDuration.parse("20 sec"))

        if (transactionRefidMap["payingTran"] != null) {
            Long tranid = Long.valueOf((String) transactionRefidMap["payingTran"])
            checkCollectedCFS("customerpayment", tranid, caseDataObj.expect)
        }
    }
    /**
     * @CaseID Promotion 1.27.1.2
     * @author lisha.hao@oracle.com
     * @description InvoicePromotion
     *               1.Create Invoice_1
     *               2.Create credit memo_1
     *               3.Create Customer Payment:Fully pay invoice_1& credit memo_1
     *               4. Wait a few seconds to check the Cash flow statement records are collected in the list of record type 'China Cash Flow Reconciliation'
     */
    @Test
    @Category([OW.class, P3.class])
    def void case_1_27_1_2() {
        def caseId = "test case 1.27.1.2"
        def testDataObj = loadCFSTestData("cashflow//cn_cashflow_promotion_data.json", caseId)
        def dataObj = testDataObj[caseId].data
        // Create Invoice and Credit Memo
        response = createTransaction(caseId, dataObj)

        context.navigateTo(CURL.HOME_CURL)
        def caseUIString = context.getFileContent(caseId, "cashflow//cn_cashflow_promotion_data.json")
        def uiObj = new JsonSlurper().parseText(caseUIString)

        // Create customer payment
        def uidata = context.getUIDataObj(uiObj, "1.27.1.2_3")
        def payment1 = acceptCustomerPayment(uiObj, uidata)

        // Get expect result object
        def expectPayment1 = context.getExpectedObj(uiObj, "1.27.1.2_3")
        //Check customer payment CFS detail
        checkCollectedCFS("customerpayment", payment1.recordName.toInteger(), expectPayment1[expectPayment1.keySet()[0]])
    }
}