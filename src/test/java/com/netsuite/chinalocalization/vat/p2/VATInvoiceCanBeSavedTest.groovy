package com.netsuite.chinalocalization.vat.p2

import com.netsuite.chinalocalization.vat.VATAppP2TestSuite
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("fredriczhu.zhu@oracle.com")
class VATInvoiceCanBeSavedTest  extends VATAppP2TestSuite {



    /**
     * @desc [VAT Invoice Type] = Special - Invoice
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.9.1.1.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_1_1() {


        String VATTAB = testData.labels.fieldLabel.tabLabel
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        //switchToRole(administrator)
        context.navigateToCreateInvoicePage()

        String commonCompany1 = testData.labels.fieldLabel.commonCompany1
        asDropdownList(locator: locators.customerDropdown).selectItem(commonCompany1)

        String location = testData.labels.fieldLabel.location
        asDropdownList(locator: locators.locationDropdown).selectItem(location)

        context.clickFormTab(GOODSTAB)
        String itemLabel = testData.labels.fieldLabel.itemLabel
        asDropdownList(locator: locators.itemsDropdown).selectItem(itemLabel)

        asClick(locators.addItemBtn)

        context.clickFormTab(VATTAB)

        String vatSpecialInvoice = testData.labels.fieldLabel.vatSpecialInvoice
        asDropdownList(locator: locators.vatInvoiceType).selectItem(vatSpecialInvoice)

        asClick(locators.saveCreditBtn)

        Thread.sleep(14 * 1000)

        String internalId = context.webDriver.executeScript("return document.getElementById('id').value;")
        println("Current internalId:"+internalId)

        def dirtyDataStr = String.format('[{"internalid":%s,"trantype":"invoice"}]',internalId)

        println(dirtyDataStr)
        dirtyData = context.asJSON(text : dirtyDataStr)
    }


    /**
     * @desc [VAT Invoice Type] = Common - Invoice
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.9.1.1.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_1_2() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        //switchToRole(administrator)
        context.navigateToCreateInvoicePage()

        String commonCompany1 = testData.labels.fieldLabel.commonCompany1
        asDropdownList(locator: locators.customerDropdown).selectItem(commonCompany1)

        String location = testData.labels.fieldLabel.location
        asDropdownList(locator: locators.locationDropdown).selectItem(location)

        context.clickFormTab(GOODSTAB)
        String itemLabel = testData.labels.fieldLabel.itemLabel
        asDropdownList(locator: locators.itemsDropdown).selectItem(itemLabel)

        asClick(locators.addItemBtn)

        context.clickFormTab(VATTAB)

        String vatCommonInvoice = testData.labels.fieldLabel.vatCommonInvoice
        asDropdownList(locator: locators.vatInvoiceType).selectItem(vatCommonInvoice)

        asClick(locators.saveCreditBtn)

        Thread.sleep(14 * 1000)

        String internalId = context.webDriver.executeScript("return document.getElementById('id').value;")
        println("Current internalId:"+internalId)

        def dirtyDataStr = String.format('[{"internalid":%s,"trantype":"invoice"}]',internalId)

        println(dirtyDataStr)
        dirtyData = context.asJSON(text : dirtyDataStr)
    }



    /**
     * @desc [VAT Invoice Type] = Null - Invoice
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.9.1.1.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_1_3() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        //switchToRole(administrator)
        context.navigateToCreateInvoicePage()

        String commonCompany1 = testData.labels.fieldLabel.commonCompany1
        asDropdownList(locator: locators.customerDropdown).selectItem(commonCompany1)

        String location = testData.labels.fieldLabel.location
        asDropdownList(locator: locators.locationDropdown).selectItem(location)

        context.clickFormTab(GOODSTAB)
        String itemLabel = testData.labels.fieldLabel.itemLabel
        asDropdownList(locator: locators.itemsDropdown).selectItem(itemLabel)

        asClick(locators.addItemBtn)

        context.clickFormTab(VATTAB)
        asClick(locators.saveCreditBtn)

        Thread.sleep(14 * 1000)

        String internalId = context.webDriver.executeScript("return document.getElementById('id').value;")
        println("Current internalId:"+internalId)

        def dirtyDataStr = String.format('[{"internalid":%s,"trantype":"invoice"}]',internalId)

        println(dirtyDataStr)
        dirtyData = context.asJSON(text : dirtyDataStr)
    }


    /**
     * @desc [VAT Invoice Type] = Common - Invoice
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.9.1.2.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_2_2() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        //switchToRole(administrator)
        context.navigateToCreateInvoicePage()

        String smallCompanyC = testData.labels.fieldLabel.smallCompanyC
        asDropdownList(locator: locators.customerDropdown).selectItem(smallCompanyC)

        String location = testData.labels.fieldLabel.location
        asDropdownList(locator: locators.locationDropdown).selectItem(location)

        context.clickFormTab(GOODSTAB)
        String itemLabel = testData.labels.fieldLabel.itemLabel
        asDropdownList(locator: locators.itemsDropdown).selectItem(itemLabel)

        asClick(locators.addItemBtn)

        context.clickFormTab(VATTAB)
        String vatCommonInvoice = testData.labels.fieldLabel.vatCommonInvoice
        asDropdownList(locator: locators.vatInvoiceType).selectItem(vatCommonInvoice)
        asClick(locators.saveCreditBtn)

        Thread.sleep(14 * 1000)

        String internalId = context.webDriver.executeScript("return document.getElementById('id').value;")
        println("Current internalId:"+internalId)

        def dirtyDataStr = String.format('[{"internalid":%s,"trantype":"invoice"}]',internalId)

        println(dirtyDataStr)
        dirtyData = context.asJSON(text : dirtyDataStr)
    }







    /**
     * @desc [VAT Invoice Type] = Null - Invoice
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京小规模纳税公司C
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.9.1.2.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_2_3() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        //switchToRole(administrator)
        context.navigateToCreateInvoicePage()

        String smallCompanyC = testData.labels.fieldLabel.smallCompanyC
        asDropdownList(locator: locators.customerDropdown).selectItem(smallCompanyC)

        String location = testData.labels.fieldLabel.location
        asDropdownList(locator: locators.locationDropdown).selectItem(location)

        context.clickFormTab(GOODSTAB)
        String itemLabel = testData.labels.fieldLabel.itemLabel
        asDropdownList(locator: locators.itemsDropdown).selectItem(itemLabel)

        asClick(locators.addItemBtn)

        context.clickFormTab(VATTAB)
        asClick(locators.saveCreditBtn)

        Thread.sleep(14 * 1000)

        String internalId = context.webDriver.executeScript("return document.getElementById('id').value;")
        println("Current internalId:"+internalId)

        def dirtyDataStr = String.format('[{"internalid":%s,"trantype":"invoice"}]',internalId)

        println(dirtyDataStr)
        dirtyData = context.asJSON(text : dirtyDataStr)
    }



    /**
     * @desc [VAT Invoice Type] = Special - Invoice
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Special
     * 5) Click save
     * @case 4.9.1.3.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_3_1() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        //switchToRole(administrator)
        context.navigateToCreateInvoicePage()

        String unCategoryCompany = testData.labels.fieldLabel.unCategoryCompany
        asDropdownList(locator: locators.customerDropdown).selectItem(unCategoryCompany)

        String location = testData.labels.fieldLabel.location
        asDropdownList(locator: locators.locationDropdown).selectItem(location)

        context.clickFormTab(GOODSTAB)
        String itemLabel = testData.labels.fieldLabel.itemLabel
        asDropdownList(locator: locators.itemsDropdown).selectItem(itemLabel)

        asClick(locators.addItemBtn)

        context.clickFormTab(VATTAB)

        String vatSpecialInvoice = testData.labels.fieldLabel.vatSpecialInvoice
        asDropdownList(locator: locators.vatInvoiceType).selectItem(vatSpecialInvoice)

        asClick(locators.saveCreditBtn)

        Thread.sleep(14 * 1000)

        String internalId = context.webDriver.executeScript("return document.getElementById('id').value;")
        println("Current internalId:"+internalId)

        def dirtyDataStr = String.format('[{"internalid":%s,"trantype":"invoice"}]',internalId)

        println(dirtyDataStr)
        dirtyData = context.asJSON(text : dirtyDataStr)
    }



    /**
     * @desc [VAT Invoice Type] = Common - Invoice
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Common
     * 5) Click save
     * @case 4.9.1.3.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_3_2(){
        String VATTAB = testData.labels.fieldLabel.tabLabel
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        //switchToRole(administrator)
        context.navigateToCreateInvoicePage()

        String unCategoryCompany = testData.labels.fieldLabel.unCategoryCompany
        asDropdownList(locator: locators.customerDropdown).selectItem(unCategoryCompany)

        String location = testData.labels.fieldLabel.location
        asDropdownList(locator: locators.locationDropdown).selectItem(location)

        context.clickFormTab(GOODSTAB)
        String itemLabel = testData.labels.fieldLabel.itemLabel
        asDropdownList(locator: locators.itemsDropdown).selectItem(itemLabel)

        asClick(locators.addItemBtn)

        context.clickFormTab(VATTAB)

        String vatCommonInvoice = testData.labels.fieldLabel.vatCommonInvoice
        asDropdownList(locator: locators.vatInvoiceType).selectItem(vatCommonInvoice)

        asClick(locators.saveCreditBtn)

        Thread.sleep(14 * 1000)

        String internalId = context.webDriver.executeScript("return document.getElementById('id').value;")
        println("Current internalId:"+internalId)

        def dirtyDataStr = String.format('[{"internalid":%s,"trantype":"invoice"}]',internalId)

        println(dirtyDataStr)
        dirtyData = context.asJSON(text : dirtyDataStr)
    }



    /**
     * @desc [VAT Invoice Type] = Null - Invoice
     * 1) Go to Transactions > Sales > Create Invoices
     * 2) Create a new Invoice
     * 3) Select Customer: 北京未分类公司
     * 4) Select [VAT Invoice type] = Null
     * 5) Click save
     * @case 4.9.1.3.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_9_1_3_3() {
        String VATTAB = testData.labels.fieldLabel.tabLabel
        String GOODSTAB = testData.labels.fieldLabel.tabGoods
        //switchToRole(administrator)
        context.navigateToCreateInvoicePage()

        String unCategoryCompany = testData.labels.fieldLabel.unCategoryCompany
        asDropdownList(locator: locators.customerDropdown).selectItem(unCategoryCompany)

        String location = testData.labels.fieldLabel.location
        asDropdownList(locator: locators.locationDropdown).selectItem(location)

        context.clickFormTab(GOODSTAB)
        String itemLabel = testData.labels.fieldLabel.itemLabel
        asDropdownList(locator: locators.itemsDropdown).selectItem(itemLabel)

        asClick(locators.addItemBtn)

        context.clickFormTab(VATTAB)
        asClick(locators.saveCreditBtn)

        Thread.sleep(14 * 1000)

        String internalId = context.webDriver.executeScript("return document.getElementById('id').value;")
        println("Current internalId:"+internalId)

        def dirtyDataStr = String.format('[{"internalid":%s,"trantype":"invoice"}]',internalId)

        println(dirtyDataStr)
        dirtyData = context.asJSON(text : dirtyDataStr)
    }










}
