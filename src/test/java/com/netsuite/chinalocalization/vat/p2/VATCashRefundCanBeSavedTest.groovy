package com.netsuite.chinalocalization.vat.p2

import com.netsuite.chinalocalization.vat.TranType
import com.netsuite.chinalocalization.vat.VATAppP2TestSuite
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("fredriczhu.zhu@oracle.com")
class VATCashRefundCanBeSavedTest extends VATAppP2TestSuite {



    /**
     * @desc [VAT Invoice Type] = Special - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.8.3.1.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_1_1() {

        println "hehe====="

        println "test_4_8_3_1_1"

        navigateToRefundCashSalesPage()


        Thread.sleep(6 * 1000)

        String commonCompany1 = testData.labels.fieldLabel.commonCompany1
        selectCompanyItem(commonCompany1)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)

        switchToGoodsTab()

        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)

        clickAddItemBtnInGoods()

        switchToVATTab()

        String vatSpecialInvoice = testData.labels.fieldLabel.vatSpecialInvoice
        selectInvoiceTypeItem(vatSpecialInvoice)

        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()
        dirtyData = getRecordJson(internalId,TranType.CASH_REFUND)
    }


    /**
     * @desc [VAT Invoice Type] = Common - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.8.3.1.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_1_2() {

        navigateToRefundCashSalesPage()

        String commonCompany1 = testData.labels.fieldLabel.commonCompany1
        selectCompanyItem(commonCompany1)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)
        switchToGoodsTab()

        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)
        clickAddItemBtnInGoods()
        switchToVATTab()

        String vatCommonInvoice = testData.labels.fieldLabel.vatCommonInvoice
        selectInvoiceTypeItem(vatCommonInvoice)

        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()
        dirtyData = getRecordJson(internalId,TranType.CASH_REFUND)
    }


    /**
     * @desc [VAT Invoice Type] = Null - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.8.3.1.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_1_3() {

        navigateToRefundCashSalesPage()

        String commonCompany1 = testData.labels.fieldLabel.commonCompany1
        selectCompanyItem(commonCompany1)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)

        switchToGoodsTab()

        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)

        clickAddItemBtnInGoods()
        switchToVATTab()

        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()
        dirtyData = getRecordJson(internalId,TranType.CASH_REFUND)
    }


    /**
     * @desc [VAT Invoice Type] = Common - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.8.3.2.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_2_2() {

        navigateToRefundCashSalesPage()

        String smallCompanyC = testData.labels.fieldLabel.smallCompanyC
        selectCompanyItem(smallCompanyC)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)
        switchToGoodsTab()

        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)
        clickAddItemBtnInGoods()

        switchToVATTab()

        String vatCommonInvoice = testData.labels.fieldLabel.vatCommonInvoice
        selectInvoiceTypeItem(vatCommonInvoice)

        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()

        dirtyData = getRecordJson(internalId,TranType.CASH_REFUND)
    }



    /**
     * @desc [VAT Invoice Type] = Null - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.8.3.2.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_2_3() {

        navigateToRefundCashSalesPage()

        String smallCompanyC = testData.labels.fieldLabel.smallCompanyC
        selectCompanyItem(smallCompanyC)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)

        switchToGoodsTab()
        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)

        clickAddItemBtnInGoods()

        switchToVATTab()

        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()

        dirtyData = getRecordJson(internalId,TranType.CASH_REFUND)
    }


    /**
     * @desc [VAT Invoice Type] = Special - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.8.3.3.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_3_1() {

        navigateToRefundCashSalesPage()

        String unCategoryCompany = testData.labels.fieldLabel.unCategoryCompany
        selectCompanyItem(unCategoryCompany)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)
        switchToGoodsTab()

        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)

        clickAddItemBtnInGoods()

        switchToVATTab()

        String vatSpecialInvoice = testData.labels.fieldLabel.vatSpecialInvoice
        selectInvoiceTypeItem(vatSpecialInvoice)

        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()

        dirtyData = getRecordJson(internalId,TranType.CASH_REFUND)
    }



    /**
     * @desc [VAT Invoice Type] = Common - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.8.3.3.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_3_2() {

        navigateToRefundCashSalesPage()

        String unCategoryCompany = testData.labels.fieldLabel.unCategoryCompany
        selectCompanyItem(unCategoryCompany)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)

        switchToGoodsTab()

        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)

        clickAddItemBtnInGoods()

        switchToVATTab()

        String vatCommonInvoice = testData.labels.fieldLabel.vatCommonInvoice
        selectInvoiceTypeItem(vatCommonInvoice)

        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()

        dirtyData = getRecordJson(internalId,TranType.CASH_REFUND)
    }


    /**
     * @desc [VAT Invoice Type] = Null - Cash Refund
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.8.3.3.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_3_3_3() {

        navigateToRefundCashSalesPage()

        String unCategoryCompany = testData.labels.fieldLabel.unCategoryCompany
        selectCompanyItem(unCategoryCompany)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)

        switchToGoodsTab()
        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)

        clickAddItemBtnInGoods()

        switchToVATTab()
        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()

        dirtyData = getRecordJson(internalId,TranType.CASH_REFUND)
    }




}
