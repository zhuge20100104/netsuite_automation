package com.netsuite.chinalocalization.cashflow

import com.netsuite.common.NSError

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.cashflow.CONSTANTS.CashFlowEnum
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("yuanfang.chi@oracle.com")
public class CashflowDefaultItemTest extends CashflowBaseTest {


    String INVOICEID;
    /**
     *  Sales Order CFS Default Test
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_36_1_1() {
        verifyTransactionCFSDefault(CURL.SALES_ORDER_CURL, "customer", "CN Automation Customer", "case_1_36_data.json");
    }
    /**
     *  Sales Order CFS Default Test
     */
    @Test
    @Category([OW.class, P2.class])
    public void case_1_36_1_2() {
        verifyTransactionCFSDefault(CURL.SALES_ORDER_CURL, "customer", "CN Automation Customer", "case_1_36_1_2_data.json");
    }
    /**
     *  Invoice CFS Default Test
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_36_2_1() {
        verifyTransactionCFSDefault(CURL.INVOICE_CURL, "customer", "CN Automation Customer", "case_1_36_data.json");
    }
    /**
     *  Invoice CFS Default Test
     */
    @Test
    @Category([OW.class, P2.class])
    public void case_1_36_2_2() {
        verifyTransactionCFSDefault(CURL.INVOICE_CURL, "customer", "CN Automation Customer", "case_1_36_2_2_data.json");
    }
    /**
     *  Credit from Invoice CFS Default Test
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_36_3_1() {
        aut.navigateTo("/app/accounting/transactions/custinvc.nl?whence=");
        String customerId = Utility.getEntityId(context, "customer", "CN Automation Customer");
        context.setFieldWithValue("entity", customerId);
        Thread.sleep(2000)
        context.setFieldWithText("location", "CN_BJ");
        Thread.sleep(2000)
        verifyCFSDefault("case_1_36_data.json", context, LANGUAGE);

        context.clickButton(SAVE);
        Thread.sleep(2000)
        INVOICEID = context.getParameterValueForFromQueryString('id');

        context.clickButton(CREDIT);
        Thread.sleep(2000)

        EditMachineCN itemSublist = context.withinEditMachine("item");
        itemSublist.setCurrentLine(1);
        String colCFS = itemSublist.getFieldText("custcol_cseg_cn_cfi");
        if (LANGUAGE.equals("zh_CN")) {
            assertAreEqual("Check CFS is defaulted correctly", colCFS, CashFlowEnum.OTHER_CASH_PAYMENTS_RELATED_TO_OPERATING_ACTIVITIES.cnLabel);
        } else {
            assertAreEqual("Check CFS is defaulted correctly", colCFS, CashFlowEnum.OTHER_CASH_PAYMENTS_RELATED_TO_OPERATING_ACTIVITIES.enLabel);
        }

        context.clickButton(CANCEL);
    }
    /**
     *  Credit Memo CFS Default Test
     */
    @Test
    @Category([OW.class, P2.class])
    public void case_1_36_3_2() {
        verifyTransactionCFSDefault(CURL.CREDIT_MEMO_CURL, "customer", "CN Automation Customer", "case_1_36_data.json");
    }
    /**
     *  Return Authorisation CFS Default Test
     */
    @Test
    @Category([OW.class, P2.class])
    public void case_1_36_3_3() {
        //This case need check Return Authorisation,need change to admin and run
        switchToRole(getAdministrator())
        verifyTransactionCFSDefault(CURL.RETURN_AUTHORISATION_CURL, "customer", "CN Automation Customer", "case_1_36_data.json");
    }
    /**
     *  Credit Memo CFS Default Test
     */
    @Test
    @Category([OW.class, P3.class])
    public void Case_1_36_3_4() {
        verifyTransactionCFSDefault(CURL.CREDIT_MEMO_CURL, "customer", "CN Automation Customer", "case_1_36_3_4_data.json");
    }
    /**
     *  Return Authorisation CFS Default Test
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_36_3_5() {
        //This case need check Return Authorisation,need change to admin and run
        switchToRole(getAdministrator())
        verifyTransactionCFSDefault(CURL.RETURN_AUTHORISATION_CURL, "customer", "CN Automation Customer", "case_1_36_3_5_data.json");
    }
    /**
     *  Cash Sales CFS Default Test
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_36_4_1() {
        verifyTransactionCFSDefault(CURL.CASH_SALE_CURL, "customer", "CN Automation Customer", "case_1_36_data.json");
    }
    /**
     *  Cash Sale CFS Default Test
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_36_4_2() {
        verifyTransactionCFSDefault(CURL.CASH_SALE_CURL, "customer", "CN Automation Customer", "case_1_36_4_2_data.json");
    }
    /**
     *  Cash Refund CFS Default Test
     */
    @Test
    @Category([OW.class, P2.class])
    public void case_1_36_5_1() {
        verifyTransactionCFSDefault(CURL.CASH_REFUND_CURL, "customer", "CN Automation Customer", "case_1_36_data.json");
    }
    /**
     *  Cash Refund CFS Default Test
     */
    @Test
    @Category([OW.class, P3.class])
    public void case_1_36_5_2() {
        verifyTransactionCFSDefault(CURL.CASH_REFUND_CURL, "customer", "CN Automation Customer", "case_1_36_5_2_data.json");
    }

    @After
    public void tearDown() {
        if (INVOICEID) {
            def response = "[{\"internalid\":" + INVOICEID + ",\"trantype\":\"invoice\"}]"
            deleteTransaction(response);
            INVOICEID = null;
        }
        super.tearDown()
    }
}
