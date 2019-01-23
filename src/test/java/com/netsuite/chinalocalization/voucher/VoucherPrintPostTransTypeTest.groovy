package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.P2
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/*
@Author lifang.tang@oracle.com
* @Created 2018-April-11
* @desc:Test cases 1.13.2 All Posting Transaction Type
*/
@TestOwner("lifang.tang@oracle.com")
class VoucherPrintPostTransTypeTest extends VoucherP2BaseTest{
    VoucherPrintPage voucherPrintPage

    def getPayrollItem(data){
        def itemName = data[0].main.name
        def searchFilters = [record.helper.filter("name").is(itemName)]
        def columns = [record.helper.column("internalid").create()]
        def searchResult = record.searchRecord("payrollitem", searchFilters, columns)
        def payRollItemID
        if(searchResult){
            payRollItemID = searchResult[0].internalid
        }else{
            def payRollItem = record.createRecord(data)
            dirtyData = payRollItem
            payRollItemID = payRollItem.internalid
        }
        return payRollItemID
    }

    def getCurrentLoginId(){
        def currentLoginId = context.executeScript("var userId = nlapiGetContext().getUser(); return userId;")
        return currentLoginId
    }

    def createPaycheckJournal(data){
        def payrollItemId = getPayrollItem(data.payrollItem)
        data.paycheckJournal[0].main.employee = getCurrentLoginId()
        data.paycheckJournal[0].earning[0].payrollitem = payrollItemId
        def paycheckJournal = record.createRecord(data.paycheckJournal)
        if(dirtyData){
            dirtyData.add(paycheckJournal)
        }else{
            dirtyData = paycheckJournal
        }
    }

    def refreshReport(String subsidiary,String periodFrom,String periodTo,String startDate,String endDate) {
        if(subsidiary){
            printPage.selectSubsidiaryItem(subsidiary)
        }
        printPage.selectPeriodFromItem(periodFrom)
        printPage.selectPeriodToItem(periodTo)
        if(startDate != null && endDate != null) {
            printPage.enterValueInTransDateFrom(startDate)
            printPage.enterValueInTransDateTo(endDate)
        }
        printPage.clickRefreshBtn()
    }

    def verifyVoucherTableContent(Map expected, Map actual){

        checkAreEqual("Please check accountAndDescription", expected.accountAndDescription,actual.accountAndDescription)
        checkAreEqual("Please check transactionCurrency", expected.transactionCurrency,actual.transactionCurrency)
        checkAreEqual("Please check rate", expected.rate,actual.rate)
        checkAreEqual("Please check transactionCurrencyDebit", expected.transactionCurrencyDebit,actual.transactionCurrencyDebit)
        checkAreEqual("Please check transactionCurrencyCredit", expected.transactionCurrencyCredit,actual.transactionCurrencyCredit)
        checkAreEqual("Please check functionalCurrencyDebit", expected.functionalCurrencyDebit,actual.functionalCurrencyDebit)
        checkAreEqual("Please check functionalCurrencyCredit", expected.functionalCurrencyCredit,actual.functionalCurrencyCredit)
    }
    /*
    @desc All posting transaction types: paycheck
    @case 1.13.2.1
     */
    @Category([P2.class, SI.class])
    @Test
    void case_1_13_2_1(){
        assertTrue("Paycheck Journal Feature is enabled",printPage.context.isFeatureEnabled("PAYCHECKJOURNAL"))

        def testData = cData.data("voucher_post_trans_type_paycheck")
        def expectedHeader = testData.expect().expectedHeader
        def expectedRow1 = testData.expect().expectedRow[0]
        def expectedRow2 = testData.expect().expectedRow[1]

        createPaycheckJournal(testData.record())

        navigateToVoucherPrintPage()
        String subsidiaryValue
        if(context.isOneWorld()){
            subsidiaryValue = testData.labels().subsidiary
        }else{
            subsidiaryValue = null
        }
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        refreshReport(subsidiaryValue,periodFrom,periodTo,startDate,endDate)

        Map tableContent = voucherPrintPage.getVoucherReportMain(0)

        if(context.isOneWorld()){
            checkAreEqual("Please check Subsidiary", expectedHeader.subsidiary,tableContent.subsidiary)
        }
        checkAreEqual("Please check Transaction Type", expectedHeader.tranType,tableContent.tranType)
        checkAreEqual("Please check TranDate", expectedHeader.trandate,tableContent.date)
        checkAreEqual("Please check postingPeriod", expectedHeader.postingPeriod,tableContent.postingPeriod)
        checkAreEqual("Please check postingPeriod", expectedHeader.currency,tableContent.currency)

        int rowIndex = 1
        String[] expectedCols = ["accountAndDescription","transactionCurrency","rate","transactionCurrencyDebit","transactionCurrencyCredit","functionalCurrencyDebit","functionalCurrencyCredit"]
        Map colsWithValue =  voucherPrintPage.getVoucherReportTableRowCells(rowIndex,expectedCols)
        verifyVoucherTableContent(expectedRow1,colsWithValue)

        int rowIndex2 = 2
        Map colsWithValue2 =  voucherPrintPage.getVoucherReportTableRowCells(rowIndex2,expectedCols)
        println(colsWithValue2.accountAndDescription)
        verifyVoucherTableContent(expectedRow2,colsWithValue2)
    }
}
