package com.netsuite.chinalocalization.vat.p2

import com.netsuite.chinalocalization.vat.TranType
import com.netsuite.chinalocalization.vat.VATAppP2TestSuite
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("fredriczhu.zhu@oracle.com")
class VATCashSalesCanBeSavedTest extends VATAppP2TestSuite {



    /**
     * @desc [VAT Invoice Type] = Special - Cash Sales
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.9.2.1.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_1_1() {

        navigateToEnterCashSalesPage()

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
        dirtyData = getRecordJson(internalId,TranType.CASH_SALE)
    }



    /**
     * @desc [VAT Invoice Type] = Common - Cash Sales
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.9.2.1.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_1_2() {

        navigateToEnterCashSalesPage()

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
        dirtyData = getRecordJson(internalId,TranType.CASH_SALE)
    }

    /**
     * @desc [VAT Invoice Type] = Null - Cash Sales
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.9.2.1.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_1_3() {

        navigateToEnterCashSalesPage()

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
        dirtyData = getRecordJson(internalId,TranType.CASH_SALE)
    }



    /**
     * @desc [VAT Invoice Type] = Common - Cash Sales
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.9.2.2.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_2_2() {

        navigateToEnterCashSalesPage()

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
        dirtyData = getRecordJson(internalId,TranType.CASH_SALE)
    }



    /**
     * @desc [VAT Invoice Type] = Null - Cash Sales
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.9.2.2.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_2_3(){

        navigateToEnterCashSalesPage()

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
        dirtyData = getRecordJson(internalId,TranType.CASH_SALE)
    }


    /**
     * @desc [VAT Invoice Type] = Special - Cash Sales
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.9.2.3.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_3_1() {
        navigateToEnterCashSalesPage()

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
        dirtyData = getRecordJson(internalId,TranType.CASH_SALE)
    }


    /**
     * @desc [VAT Invoice Type] = Common - Cash Sales
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.9.2.3.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_3_2() {
        navigateToEnterCashSalesPage()

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
        dirtyData = getRecordJson(internalId,TranType.CASH_SALE)
    }



    /**
     * @desc [VAT Invoice Type] = Null - Cash Sales
     * 1) Go to Transactions > Sales > Enter Cash Sales
     * 2) Create a new Cash Sale
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.9.2.3.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_2_3_3(){
        navigateToEnterCashSalesPage()

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
        dirtyData = getRecordJson(internalId,TranType.CASH_SALE)
    }

}
