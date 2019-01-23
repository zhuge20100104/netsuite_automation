package com.netsuite.chinalocalization.preferencesetup

import com.google.inject.Inject
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.Setup.PreferenceSetUpPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import org.openqa.selenium.Alert


@TestOwner("xiaojuan.song@oracle.com")
class PreferenceUncheckVatTest extends PreferenceSetupBaseTest{

    @Rule
    public TestName name = new TestName()
    private def static caseData

    @Inject
    PreferenceSetUpPage preSetupPage
    @Inject
    AlertHandler alertHandler

    @SuiteSetup
    void setUpTestSuite(){
        super.setUpTestSuite()
        //switchToRole(administrator)
/*        navigateToPreferenceSetUp()
        setVAT(false)
        preSetupPage.clickSave()*/
    }

/*    @SuiteTeardown
    void tearDownTestSuite() {
        navigateToPreferenceSetUp()
        setVAT(true)
        preSetupPage.clickSave()
//        super.tearDownTestSuite()
    }*/

    def pathToTestDataFiles(){
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "zhCN":"${dataFilesPath}preferencesetup\\data\\PreferenceUncheckVatTest_zh_CN.json",
                "enUS":"${dataFilesPath}preferencesetup\\data\\PreferenceUncheckVatTest_en_US.json"
        ]
    }

    def initData(){
        caseData = testData.get(name.getMethodName())
    }

    def setTranCurrentRecord(String tranType) {
        context.navigateTo(tranType)
        context.acceptUpcomingConfirmation()
        currentRecord.setCurrentRecord(caseData)
        context.clickSaveByAPI()
    }

    def checkExisting(columnName, columnValue, expType){
        def filters = [record.helper.filter(columnName).is(columnValue)]
        def columns = [record.helper.column("internalid").create()]
        def internalId = record.searchRecord(expType, filters, columns)

        return internalId
    }

    def checkItemExisting(columnName, columnValue, expType, expResult){
        def internalId = checkExisting(columnName, columnValue, expType)
        context.navigateToItemEditPage(internalId[0]['internalid'])
        checkState(expResult)

    }

    def checkState(expResult){
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expResult))
        context.clickSaveByAPI()
        switchWindow()
    }

    /**
     *@desc vat unchecked, new records successfully
     *@case check the custom fields disappeared and new records successfully
     1. login as admin
     2. go to preference setup page
     3. uncheck vat
     4. click save
     5. check the custom fields disapeared, including four type transactions, customer, items, subsidiray(company)
     6. new records successfully, including four type transactions, customer, items, subsidiray(company)
     * @author xiaojuan.song
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_2_3_1(){
        def expTab = testData.labels.tabLabel
        def expField = testData.labels.fieldLabel

        // hidden subtab : new transactions
        context.navigateToRefundCashSalesPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToEnterCashSalesPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToCreateInvoicePage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToIssueCreditMemosPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToPrepareEstimatesPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToEnterSalesOrdersPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        // hidden fields : new subsidiary
        context.navigateToCompanyInformationPage()
        assertFalse("check no 'CHINA MAX VAT INVOICE AMOUNT' field existing", context.doesTextExist(expField))
        if (context.isOneWorld()){
            context.navigateToSubsidiaryNewPage()
            assertFalse("check no 'CHINA MAX VAT INVOICE AMOUNT' field existing", context.doesTextExist(expField))
        }

        // hidden fields : new customer
        context.navigateToCustomerNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        // hidden fields : new items
        context.navigateToDiscountItemNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToInventoryItemNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToItemGroupNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToKitPackageNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToLotNumberedNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToServiceItemNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToOtherChargeItemNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

        context.navigateToNonInventoryItemNewPage()
        assertFalse("check china vat subtab",context.doesFormSubTabExist(expTab))

    }

    /**
     *@desc vat unchecked, edit the existing records successfully - OW
     *@case Edit existing exported Transaction
     1. login as admin
     2. go to preference setup page
     3. uncheck vat
     4. click save
     5. edit the existing records, including customer, item, subsidiray(company), transactions(four type)
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_2_3_2(){
        def expTab = testData.labels.tabLabel

        // save existing records : transcations
        def internalId = checkExisting('tranid','10001', 'cashrefund')
        context.navigateToCashRefundEditPage(internalId[0]['internalid'])
        checkState(expTab)

        internalId = checkExisting('tranid','10001', 'cashsale')
        context.navigateToCashSaleEditPage(internalId[0]['internalid'])
        checkState(expTab)

        internalId = checkExisting('tranid','10001', 'creditmemo')
        context.navigateToCreditMemosEditPage(internalId[0]['internalid'])
        checkState(expTab)

        internalId = checkExisting('tranid','10001', 'invoice')
        context.navigateToInvoiceEditPage(internalId[0]['internalid'])
        checkState(expTab)

        // save existing records : customer
        internalId = checkExisting('companyname', '北京一般纳税公司01', 'customer')
        context.navigateToCustomerEditPage(internalId[0]['internalid'])
        checkState(expTab)
        if(context.isOneWorld()){
            // save existing records : items
            checkItemExisting('itemid', 'Item-Inventory', 'inventoryitem', expTab)
            checkItemExisting('itemid', 'Item-Group', 'itemgroup', expTab)
            checkItemExisting('itemid', 'Item-KitPackage', 'kititem', expTab)
//        checkItemExisting('itemid', 'LotNumberdInventory-test', 'lotnumberedinventoryitem', expTab)
            checkItemExisting('itemid', 'Item-Noninventory', 'noninventoryitem', expTab)
            checkItemExisting('itemid', 'Item-OtherCharge', 'otherchargeitem', expTab)
            checkItemExisting('itemid', 'Item-Service', 'serviceitem', expTab)
        }

        }

    /**
     *@desc preference uncheck vat,save a CashSale test
     *@case 2_3_3
     *@author xiaojuan.song
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_2_3_3(){
        initData()
        setTranCurrentRecord(caseData,  CURL.CASH_SALE_CURL)

        def internalId = context.getParameterValueForFromQueryString("id")
        def tranType = "cashsale"
        Assert.assertNotNull("The transaction is not saved successfully.", internalId)

        if (internalId != "") {
            def id = Integer.parseInt(internalId)
            record.deleteRecord(tranType, id)
        }
    }

    /**
     *@desc preference uncheck vat ,save a Invoice test
     *@case 2_3_4
     *@author xiaojuan.song
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_2_3_4() {
        initData()
        setTranCurrentRecord(caseData, CURL.INVOICE_CURL)

        def internalId = context.getParameterValueForFromQueryString("id")
        def tranType = "invoice"
        Assert.assertNotNull("The transaction is not saved successfully.", internalId)

        if (internalId != "") {
            def id = Integer.parseInt(internalId)
            record.deleteRecord(tranType, id)
        }
    }

}
