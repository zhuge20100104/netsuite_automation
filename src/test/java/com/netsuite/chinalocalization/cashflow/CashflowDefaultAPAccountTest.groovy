package com.netsuite.chinalocalization.cashflow

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.cashflow.CONSTANTS.CashFlowEnum
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P0
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

import static com.netsuite.common.SI.*

@TestOwner("yuanfang.chi@oracle.com")
public class CashflowDefaultAPAccountTest extends CashflowBaseTest {
    String TRANID

    def getDefaultRole() {
        return getAdministrator()
    }

    private void verifyCFSDefaultInheritedFromParent(String fileName, NetSuiteAppCN appCN, String language) {
        String caseData = new File('data//cashflow//' + fileName).getText('UTF-8')
        def jsonSlurper = new JsonSlurper();
        def caseDataObj = jsonSlurper.parseText(caseData);
        if (caseDataObj.data.item != null) {
            if (caseDataObj.data.item.size() == caseDataObj.expected.item.size()) {
                this.verifyItemLineDefaultInheritedFromParent(appCN, language, caseDataObj.expected.item);
            } else {
                assertFalse("Expect result in test Data is Incomplete", true);
            }
        }
        if (caseDataObj.data.expense != null) {
            if (caseDataObj.data.expense.size() == caseDataObj.expected.expense.size()) {
                this.verifyExpenseLineDefaultInheritedFromParent(appCN, language, caseDataObj.expected.expense);
            } else {
                assertFalse("Expect result in test Data is Incomplete", true);
            }
        }
    }

    private void verifyItemLineDefaultInheritedFromParent(NetSuiteAppCN appCN, String language,
                                                          def itemLineExpectCFSList) {
        if (context.doesFormSubTabExist(ITEMTAB)) {
            context.clickFormSubTab(ITEMTAB);
        }
        EditMachineCN sublist = context.withinEditMachine("item");
        if (sublist.lineItemCount != itemLineExpectCFSList.size()) {
            assertFalse("Expect result in test Data is Incomplete or Transaction is not correct", true);
        }
        for (int i = 0; i < itemLineExpectCFSList.size(); i++) {
            def expectCFSData = itemLineExpectCFSList.get(i);

            sublist.setCurrentLine(i + 1);
            this.checkCFS(language, '', expectCFSData.cfsnew, sublist.getFieldText("custcol_cseg_cn_cfi"))
        }
    }

    private void verifyExpenseLineDefaultInheritedFromParent(NetSuiteAppCN appCN, String language,
                                                             def expenseLineExpectCFSList) {
        if (context.doesFormSubTabExist(EXPENSETAB)) {
            context.clickFormSubTab(EXPENSETAB);
        }
        EditMachineCN sublist = context.withinEditMachine("expense");
        if (sublist.lineItemCount != expenseLineExpectCFSList.size()) {
            assertFalse("Expect result in test Data is Incomplete or Transaction is not correct", true);
        }
        for (int i = 0; i < expenseLineExpectCFSList.size(); i++) {
            def expectCFSData = expenseLineExpectCFSList.get(i);

            sublist.setCurrentLine(i + 1);
            this.checkCFS(language, '', expectCFSData.cfsnew, sublist.getFieldText("custcol_cseg_cn_cfi"))
        }
    }

    @Override
    protected void verifyCFSDefault(String fileName, NetSuiteAppCN appCN, String language) {
        String caseData = new File('data//cashflow//' + fileName).getText('UTF-8')
        def jsonSlurper = new JsonSlurper();
        def caseDataObj = jsonSlurper.parseText(caseData);
        if (caseDataObj.data.item != null) {
            if (caseDataObj.data.item.size() == caseDataObj.expected.item.size()) {
                this.verifyItemLineDefault(appCN, language, caseDataObj.data.item, caseDataObj.expected.item);
            } else {
                assertFalse("Expect result in test Data is Incomplete", true);
            }
        }

        if (caseDataObj.data.expense != null) {
            if (caseDataObj.data.expense.size() == caseDataObj.expected.expense.size()) {
                this.verifyExpenseLineDefault(appCN, language, caseDataObj.data.expense, caseDataObj.expected.expense);
            } else {
                assertFalse("Expect result in test Data is Incomplete", true);
            }
        }
    }

    private void verifyExpenseLineDefault(NetSuiteAppCN appCN, String language,
                                          def expenseLineDataList, def expenseLineExpectCFSList) {
        if (context.doesFormSubTabExist(EXPENSETAB)) {
            context.clickFormSubTab(EXPENSETAB);
        }
        EditMachineCN sublist = context.withinEditMachine("expense");
        for (int i = 0; i < expenseLineDataList.size(); i++) {
            def expenseLineData = expenseLineDataList.get(i);
            def expectCFSData = expenseLineExpectCFSList.get(i);

            if (expenseLineData.lineid.toInteger() != 1) {
                sublist.setCurrentLine(expenseLineData.lineid.toInteger());
            }
            if (expenseLineData.categoryold != null) {
                sublist.setFieldWithText("category", expenseLineData.categoryold);
            } else if (expenseLineData.accountold != null) {
                sublist.setFieldWithText("account", expenseLineData.accountold);
            }

            if (expenseLineData.currency != null) {
                sublist.setFieldWithText("currency", expenseLineData.currency);
            }
            if (expenseLineData.amount != null) {
                sublist.setFieldWithValue("amount", expenseLineData.amount);
            }
            this.checkCFS(language, '', expectCFSData.cfsold, sublist.getFieldText("custcol_cseg_cn_cfi"))

            //change item to itemnew if not null
            if (expenseLineData.categorynew != null) {
                sublist.setCurrentLine(expenseLineData.lineid.toInteger());
                sublist.setFieldWithText("category", expenseLineData.categorynew);
            } else if (expenseLineData.accountnew != null) {
                sublist.setCurrentLine(expenseLineData.lineid.toInteger());
                sublist.setFieldWithText("account", expenseLineData.accountnew);
            }

            //Change CFS item if not null
            if (expenseLineData.cfsnew != null) {
                String label = "";
                if (!expenseLineData.cfsnew.isEmpty()) {
                    if (language.equals("en_US")) {
                        label = expenseLineData.cfsnew;
                    } else {
                        label = CashFlowEnum.getCnLabel(expenseLineData.cfsnew)
                    }
                }
                sublist.setCurrentLine(expenseLineData.lineid.toInteger());
                sublist.setFieldWithText("custcol_cseg_cn_cfi", label);
            }

            //Verify CFS after item changed
            this.checkCFS(language, '', expectCFSData.cfsnew, sublist.getFieldText("custcol_cseg_cn_cfi"))

            appCN.clickButton(ADD);
            Thread.sleep(2000)
        }
    }
    /**
     *  Purchase Order CFS Default Test
     */
    //@Test
    @Category([OW.class, P0.class])
    public void case_1_39_1_1() {
        verifyTransactionCFSDefault(CURL.PURCHASE_ORDER_CRUL, "vendor", "CN Automation Vendor", "case_1_39_1_1_data.json");
    }
    /**
     *  Vendor Bill and Credit CFS Default Test
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_39_2_1() {
        context.navigateTo(CURL.VENDOR_BILL_CURL);
        // Set text won't work if the dropdown list values prefix customer internalid
        String vendorId = Utility.getEntityId(context, "vendor", "CN Automation Vendor");
        aut.setFieldWithValue("entity", vendorId);
//        aut.waitForPageToLoad();
        verifyCFSDefault("case_1_39_2_1_data.json", context, LANGUAGE);
        context.clickButton(SAVE);
        TRANID = context.getRecordIdbyAPI()
        context.clickButton(CREDIT);
        Thread.sleep(2000)
        verifyCFSDefaultInheritedFromParent("case_1_39_2_1_data.json", context, LANGUAGE);
        context.clickButton(CANCEL)
        // TODO, should delete the vendor bill
    }
    /**
     *  Vendor Credit CFS Default Test
     */
    //@Test
    @Category([OW.class, P1.class])
    public void case_1_39_3_1() {
        verifyTransactionCFSDefault(CURL.VENDOR_CREDIT_CURL, "vendor", "CN Automation Vendor", "case_1_39_3_1_data.json");
    }
    /**
     *  Vendor Return Authorization CFS Default Test
     */
    //@Test
    @Category([OW.class, P1.class])
    public void case_1_39_4_1() {
        verifyTransactionCFSDefault(CURL.VENDOR_RETURN_AUTHORISATION_CURL, "vendor", "CN Automation Vendor", "case_1_39_4_1_data.json");
    }
    /**
     *  Expense Report CFS Defult Test
     */
    //@Test
    @Category([OW.class, P1.class])
    public void case_1_39_5_1() {
        verifyTransactionCFSDefault(CURL.EXPENSE_REPORT_CURL, "employee", "auto1.01", "case_1_39_5_1_data.json");
    }

    @After
    public void tearDown() {
        if (TRANID != null) {
            def response = "[{\"internalid\":" + TRANID + ",\"trantype\":\"vendorbill\"}]"
            deleteTransaction(response);
            TRANID = null
        }
        super.tearDown()
    }
}