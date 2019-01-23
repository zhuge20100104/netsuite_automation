package com.netsuite.chinalocalization.vat.p2

import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.chinalocalization.vat.VATAppP2TestSuite
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category
import org.openqa.selenium.Alert

@TestOwner("fredriczhu.zhu@oracle.com")
class VATIssueCreditMemoTest extends VATAppP2TestSuite {

    def handleErrorAndAlert() {
        clickOkBtn()


        context.webDriver.reloadBrowser()
        Thread.sleep(4*1000)

        AlertHandler alertHandler = new AlertHandler()
        Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver,10)
        if(alert!=null) {
            alert.accept()
        }


        Thread.sleep(3*1000)
    }

    //region business part
    def wrongCreditMemoTest(String creditNumber) {
        navigateToIssueCreditMemosPage()
        switchToVATTab()

        enterValueInCreditNumber(creditNumber)
        clickSaveBtnInVAT()

        String errorCreditMsg = testData.data.wrongCreditMsg
        Thread.sleep(3*1000);
        assertTextVisible("Error message not shown",errorCreditMsg)

        handleErrorAndAlert()

    }

    def wrongCashRefundTest(String informationSheetNumber) {
        navigateToRefundCashSalesPage()
        switchToVATTab()

        enterValueInCreditNumber(informationSheetNumber)
        clickSaveBtnInVAT()


        String wrongInformationSheetMsg = testData.data.wrongInformationSheetMsg
        Thread.sleep(3*1000);
        assertTextVisible("Error message not shown", wrongInformationSheetMsg)
        handleErrorAndAlert()
    }
    //endregion


    /**
     * @desc Information Sheet Number > 16 digits - Credit Memo
     * Information Sheet Number Error - CN
     * 1) Go to Transactions > Customers > Issue Credit Memos
     * 2) Create a Credit Memo with null VAT Invoice Type
     * 3) Enter Information sheet number (>16 digits)
     * @case 4.7.1.1.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_7_1_1_1() {
        String longCreditNumber = testData.data.wrongCreditNumber
        wrongCreditMemoTest(longCreditNumber)
    }



    /**
     * @desc Information Sheet Number < 16 digits - Credit Memo
     * Information Sheet Number Error - CN
     * 1) Go to Transactions > Customers > Issue Credit Memos
     * 2) Create a Credit Memo with null VAT Invoice Type
     * 3) Enter Information sheet number (<16 digits)
     * @case 4.7.1.2.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_7_1_2_1() {
        String shortCreditNumber = testData.data.shortWrongCreditNumber
        wrongCreditMemoTest(shortCreditNumber)
    }





    /**
     * @desc [VAT Invoice Type] = Special - Credit Memo
     * VAT Invoice Type Error - CN
     * 1) Go to Transactions > Customers > Issue Credit Memos
     * 2) Create a new Credit Memo
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.7.3.2.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_7_3_2_1() {

        navigateToIssueCreditMemosPage()

        String smallCompanyC = testData.labels.fieldLabel.smallCompanyC
        selectCompanyItem(smallCompanyC)

        switchToVATTab()
        String specialInvoiceType = testData.labels.fieldLabel.vatSpecialInvoice
        selectInvoiceTypeItem(specialInvoiceType)

        clickSaveBtnInVAT()
        String errorCreditMsg = testData.data.wrongCreditMsgSmallCompany
        Thread.sleep(3*1000);
        assertTextVisible("Error message not shown",errorCreditMsg)
        handleErrorAndAlert()
    }


    /**
     * @desc Information Sheet Number > 16 digits - Cash Refund
     * Information Sheet Number Error - CN
     * 1) Go to Transactions > Customers > Refund Cash Sales >List
     * 2) Create a Cash Refund with null VAT Invoice Type
     * 3) Enter Information sheet number (>16 digits)
     * 4) Click Save button
     * @case 4.8.1.1.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_1_1_1() {
        String wrongInformationSheet = testData.data.wrongInformationSheet
        wrongCashRefundTest(wrongInformationSheet)
    }



    /**
     * @desc Information Sheet Number < 16 digits - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales >List
     * 2) Create a Cash Refund with null VAT Invoice Type
     * 3) Enter Information sheet number (<16 digits)
     * 4) Click Save button
     * @case 4.8.1.2.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_1_2_1() {
        String wrongInformationSheet = testData.data.shortWrongInformationSheet
        wrongCashRefundTest(wrongInformationSheet)
    }


    /**
     * @desc [VAT Invoice Type] = Special - Cash Refund VAT Invoice Type Error - CN
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.8.3.2.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_2_1() {

        navigateToRefundCashSalesPage()

        String smallCompanyC = testData.labels.fieldLabel.smallCompanyC
        selectCompanyItem(smallCompanyC)

        switchToVATTab()

        String specialInvoiceType = testData.labels.fieldLabel.vatSpecialInvoice
        selectInvoiceTypeItem(specialInvoiceType)

        clickSaveBtnInVAT()

        String errorCreditMsg = testData.data.wrongCreditMsgSmallCompany
        Thread.sleep(3*1000);
        assertTextVisible("Error message not shown",errorCreditMsg)
        handleErrorAndAlert()
    }


    /**
     * @desc Invalide Cash Sales Doc Number - Cash Refund CN
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Enter a invalid Cash Sales Document Number.
     *（existing Cash Sales but not for this customer)
     * 5) Click save
     * @case 4.8.4.1.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_4_1_1() {

        navigateToRefundCashSalesPage()

        String commonCompany2 = testData.labels.fieldLabel.commonCompany2
        selectCompanyItem(commonCompany2)
        switchToVATTab()

        String invalidCashRefundNum = testData.data.invalidCashRefundNum
        enterValueInCreatedFrom(invalidCashRefundNum)

        clickSaveBtnInVAT()

        String cashRefundNotExistMsg = testData.data.cashRefundNotExistMsg
        Thread.sleep(3*1000);
        assertTextVisible("Error message not shown", cashRefundNotExistMsg)
        handleErrorAndAlert()
    }



    /**
     * @desc Has been refund - Cash Refund CN
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Enter an invalid Cash Sales Document Number that has been refund before.
     * 5) Click save
     * @case 4.8.4.2.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_4_2_1() {
        navigateToRefundCashSalesPage()

        String commonCompany2 = testData.labels.fieldLabel.commonCompany2
        selectCompanyItem(commonCompany2)
        switchToVATTab()

        String hasBeenRefundNum = testData.data.hasBeenRefundNum
        enterValueInCreatedFrom(hasBeenRefundNum)

        clickSaveBtnInVAT()

        String hasBeenRefundMsg = testData.data.hasBeenRefundMsg
        Thread.sleep(3*1000);
        assertTextVisible("Error message not shown", hasBeenRefundMsg)
        handleErrorAndAlert()
    }


    /**
     * @desc [VAT Invoice Type] = Special - Invoice VAT Invoice Type Error - CN
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.9.1.2.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_2_1() {

        navigateToCreateInvoicePage()
        String smallCompanyC = testData.labels.fieldLabel.smallCompanyC
        selectCompanyItem(smallCompanyC)
        switchToVATTab()

        String specialInvoiceType = testData.labels.fieldLabel.vatSpecialInvoice
        selectInvoiceTypeItem(specialInvoiceType)
        clickSaveBtnInVAT()

        String wrongInvoiceMsgSmallCompany = testData.data.wrongInvoiceMsgSmallCompany
        Thread.sleep(3*1000);
        assertTextVisible("Error message not shown",wrongInvoiceMsgSmallCompany)
        handleErrorAndAlert()
    }



    /**
     * @desc [VAT Invoice Type] = Special - Cash Sales
     * VAT Invoice Type Error - CN
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.9.2.2.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_2_1() {

        navigateToEnterCashSalesPage()
        String smallCompanyC = testData.labels.fieldLabel.smallCompanyC
        selectCompanyItem(smallCompanyC)
        switchToVATTab()

        String specialInvoiceType = testData.labels.fieldLabel.vatSpecialInvoice
        selectInvoiceTypeItem(specialInvoiceType)
        clickSaveBtnInVAT()

        String wrongCashSalesMsgSmallCompany = testData.data.wrongCashSalesMsgSmallCompany
        Thread.sleep(3*1000);
        assertTextVisible("Error message not shown", wrongCashSalesMsgSmallCompany)

        handleErrorAndAlert()
    }

}
