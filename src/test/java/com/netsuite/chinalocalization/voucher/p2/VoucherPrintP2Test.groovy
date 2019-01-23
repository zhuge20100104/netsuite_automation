package com.netsuite.chinalocalization.voucher.p2

import com.netsuite.chinalocalization.voucher.VoucherP2BaseTest
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category


@TestOwner("fredriczhu.zhu@oracle.com")
class VoucherPrintP2Test extends VoucherP2BaseTest {

    /**
     * @desc Transaction type: Customer Deposit
     * 1. Create a transaction type of Others
     * Refer Sheet:Test Data(Customer Deposit)
     * Date: Current Date
     * 2. Go to Financial > Other > 中国凭证打印报表
     * Search condition:
     * Subsidiary: China BU
     * Period From: Jul 2017
     * Period To: Jul 2017
     * Date from: 7/8/2017
     * Date To: 7/8/2017
     * 3. Click "Refresh"
     * @case 1.11.1.4.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void case_1_11_1_4_1() {

        def testData = cData.data("test_1_11_1_4_1")

        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)

        def tableData = printPage.parseTableData()

        def expectTableData = testData.expect()
        verifyReportData(expectTableData,tableData)
    }




    /**
     * @desc Not display_Search result is empty
     *1.Click "中国凭证打印报表 " Menu
     * 2.Subsidary: China BU
     * 3.Period From: Jan 2016
     * 4.Period To: Jan 2016
     * 5.Date From: 01/06/2016
     * 6.Date To: 01/06/2016
     * 7.Other fields are default values
     * 8.Click 'Refresh'
     * @case 1.12.1.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_12_1_2() {
        def testData = cData.data("test_1_12_1_2")
        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)
        String noResultMsg = testData.expect().noDataMsg
        assertTextVisible("No results message not shown!!",noResultMsg)
    }



    /**
     * @desc Deposit  cashsale
     * Account:1090 Undeposited Funds
     * 1.Create deposit data:Financial>Banking>Make Deposits
     * Refer to the data:Test Data(Transaction 9 types)
     * 2.Go to Financial > Other > 中国凭证打印报表
     * 3.Click "中国凭证打印报表 " Menu
     * 4.Subsidiary :China BU
     * 5.Period From: Jul 2017
     * 6.Period To: Jul 2017
     * 7.Date From :7/2/2017
     * 8.Date To :7/2/2017
     * 9.Other fields are default values
     * 10.Click 'Refresh'
     * @case 1.13.1.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_13_1_2() {
        def testData = cData.data("test_1_13_1_2")

        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)

        def tableData = printPage.parseTableData()
        def expectTableData = testData.expect()
        verifyReportData(expectTableData,tableData)
    }




    /**
     * @desc Customer Invoice invoice
     * 1.Create invoice data:Billing > Sales > Create Invoices
     * Refer to the data:Test Data(Transaction 9 types)
     * 2.Go to Financial > Other > 中国凭证打印报表
     * 3.Click "中国凭证打印报表 " Menu
     * 4.Subsidiary :China BU
     * 5.Period From: Jul 2017
     * 6.Period To: Jul 2017
     * 7.Date From :7/3/2017
     * 8.Date To :7/3/2017
     * 9.Other fields are default values
     * 10.Click 'Refresh'
     * @case 1.13.1.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_13_1_3() {
        def testData = cData.data("test_1_13_1_3")

        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)

        def tableData = printPage.parseTableData()
        def expectTableData = testData.expect()
        verifyReportData(expectTableData,tableData)
    }




    /**
     * @desc   Transaction type: Others (InvAdjst using Administrator Role)   inventory adjustment
     * 1. Create a transaction type of Others
     * Refer Sheet:Test Data(Inventory Adj)
     * 2. Go to Financial > Other > 中国凭证打印报表
     * Search condition:
     * Subsidiary: China BU
     * Period From: Jul 2017
     * Period To: Jul 2017
     * Date from: 7/10/2017
     * Date To: 7/10/2017
     * 3. Click "Refresh"
     * @case 1.11.1.1.4
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_11_1_1_4() {
        def testData = cData.data("test_1_11_1_1_4")

        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)

        def tableData = printPage.parseTableData()
        def expectTableData = testData.expect()
        verifyReportData(expectTableData,tableData)
    }







    /**
     * @desc   Transaction type: Others (InvAdjst using Administrator Role)   inventory adjustment Update
     * 1. Update Inventory Adjustment
     * (created in step 1.11.1.4)
     * Date: 4/25/2017
     * 2. Go to Financial > Other > 中国凭证打印报表
     * Search condition:
     * Subsidiary: China BU
     * Period From: Apr 2017
     * Period To: Apr 2017
     * Date from: 4/25/2017
     * Date To: 4/25/2017
     * 3. Click "Refresh"
     * @case 1.11.1.2.2
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_11_1_2_2() {
        def testData = cData.data("test_1_11_1_2_2")

        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)

        def data = testData.data()
        record.updateRecord(data.tranType,dirtyData.internalid,data.updateData)

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)

        def tableData = printPage.parseTableData()
        def expectTableData = testData.expect()
        verifyReportData(expectTableData,tableData)
    }





    /**
     * @desc   Not display_Change Subsidary
     * 2.Click "中国凭证打印报表 " Menu
     * 3.Subsidary: China BU
     * 4.Period From: Jul 2016
     * 5.Period To: Jul 2016
     * 6.Other fields are default values
     * 7.Click 'Refresh'
     * 8.Change Subsidary: Parent Company
     * @case 1.12.1.3
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_12_1_3(){
        def testData = cData.data("test_1_12_1_3")

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)

        def expectMsg = testData.expect().noDataDisplayMsg
        assertTextVisible("No data display label not display!!" , expectMsg)
    }






    /**
     * @desc  Reset Select Page = 3/5
     * 1.Import Journal_for_import_HTML pagination.csv file(100 journals,  lines = 10,   Period = Jul 2016)
     * 2.Click "中国凭证打印报表 " Menu
     * 3.Subsidary: China BU
     * 4.Period From : Jul 2016
     * Period To: Jul 2016
     * 5.Date From: 7/1/2016
     * Date To: 7/3/2016
     * 6.Other fields are default values
     * 7.Click 'Refresh'
     * 8.Reset Select Page = 3/5
     * @case 1.12.1.6
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_12_1_6() {
        def testData = cData.data("test_1_12_1_6")

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        String currentPage = testData.labels().currentPage

        printPage.refreshReportInPage(subsidiary,periodFrom,periodTo,startDate,endDate,currentPage)

        verifyMultiplyTableData(testData)

    }




    /**
     * @desc  Reset transactions per page = 50
     * 1.Import Journal_for_import_HTML pagination.csv file(100 journals,  lines = 10,   Period = Jul 2016)
     * 2.Click "中国凭证打印报表 " Menu
     * 3.Subsidary: China BU
     * 4.Period From: Jul 2016
     * 5.Period To: Jul 2016
     * 6.Other fields are default values
     * 7.Click 'Refresh'
     * 8.Reset transactions per page = 50
     * @case 1.12.1.8
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_12_1_8() {
        def testData = cData.data("test_1_12_1_8")

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        String currentPage = testData.labels().currentPage
        String transPerPage = testData.labels().transPerPage


        def  currentPageItems = testData.expect().currentPageItems
        def  perPageItems = testData.expect().perPageItems

        def retItems = printPage.switchAndGetDropdownItemsInPage(subsidiary,periodFrom,periodTo,startDate,endDate,transPerPage)

        assertAreEqual("Current page item not correct!",currentPage,retItems.currentPageItem)
        assertAreEqual("Per page count not correct!",transPerPage,retItems.perPageItem)


        assertAreEqual("Current page items not correct!", currentPageItems,retItems.currentPageItems)
        assertAreEqual("Per page items not correct!",perPageItems,retItems.perPageItems)

    }

    /**
     * @desc  Reset transactions per page = 50
     * 1.Import Journal_for_import_HTML pagination.csv file(100 journals,  lines = 10,   Period = Jul 2016)
     * 2.Click "中国凭证打印报表 " Menu
     * 3.Subsidary: China BU
     * 4.Period From: Jul 2016
     * 5.Period To: Jul 2016
     * 6.Other fields are default values
     * 7.Add search criteria(40 journals):
     * Date From: 07/01/2016
     * Date To: 07/01/2016
     * 8.Click 'Refresh'
     * @case 1.12.1.9
     * @author chenguang.wang@oracle.com
     */
    @Category([P2.class, OW.class])
    @Test
    void test_1_12_1_9() {
        def testData = cData.data("test_1_12_1_9")

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        String currentPage = testData.labels().currentPage
        String transPerPage = testData.labels().transPerPage
        String datefrom = testData.labels().datefrom
        String dateto = testData.labels().dateto


        def  currentPageItems = testData.expect().currentPageItems
        def  perPageItems = testData.expect().perPageItems

        def retItems = printPage.switchAndGetDropdownItemsInPage(subsidiary,periodFrom,periodTo,startDate,endDate,transPerPage,datefrom,dateto)

        assertAreEqual("Current page item not correct!",currentPage,retItems.currentPageItem)
        assertAreEqual("Per page count not correct!",transPerPage,retItems.perPageItem)


        assertAreEqual("Current page items not correct!", currentPageItems,retItems.currentPageItems)
        assertAreEqual("Per page items not correct!",perPageItems,retItems.perPageItems)

    }


}
