package com.netsuite.chinalocalization.page.voucher

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.testautomation.html.parsers.TableParser

class VoucherPrintP2Page extends VoucherBasePage{

    static final String SUBSIDIARY_SELECT= "//*[@id=\"subsidiary_fs\"]"
    static final String  PERIOD_FROM_SELECT = "//*[@id=\"periodfrom_fs\"]"
    static final String PERIOD_TO_SELECT = "//*[@id=\"periodto_fs\"]"
    static final String TRANDATE_FROM_TEXT = "//input[@id='trandatefrom']"
    static final String TRANDATE_TO_TEXT = "//input[@id='trandateto']"
    static final String REFRESH_BTN =  "//input[@id='refresh']"
    static final String CURRENT_PAGE_SELECT = "//*[@id=\"selectpage_fs\"]"
    static final String ITEM_TABLE = "//*[@id=\"div__body\"]//table//table[5]"
    static final String ITEM_TABLE_REGEX = "//*[@id=\"div__body\"]//table//table[%d]"
    static final String TABLE_ROW_ITERATOR = "//tbody//tr"
    static final String TRANS_PER_PAGE_SELECT =  "//*[@id=\"transperpage_fs\"]"
    static final String DATE_FROM_SELECT = "//*[@id='trandatefrom_fs']/input"
    static final String DATE_T0_SELECT = "//*[@id='trandateto_fs']/input"
    static final String PRINT_PDF_BTN = "//*[@id='printAsPDF']"
    static final String VOUCHER_NUMBER_TYPE_SELECT = "//*[@id='glnumberoper_fs']"
    static final String VOUCHER_NUMBER_TEXT = "//*[@id='glnumberval']"


    //region common methods for pages
    def selectSubsidiaryItem(String subsidiaryItem) {
        selectDropdownItem(SUBSIDIARY_SELECT,subsidiaryItem)
    }

    def selectPeriodFromItem(String periodFromItem) {
        selectDropdownItem(PERIOD_FROM_SELECT,periodFromItem)
    }

    def selectPeriodToItem(String periodToItem) {
        selectDropdownItem(PERIOD_TO_SELECT,periodToItem)
    }

    def selectCurrentPageItem(String currentPageItem) {
        selectDropdownItem(CURRENT_PAGE_SELECT,currentPageItem)
        waitForPageToLoad()
    }

    def selectTransPerPageItem(String transPerPageItem) {
        setFieldValue(TRANS_PER_PAGE_SELECT,transPerPageItem)
        waitForPageToLoad()
    }

    def selectDateFrom(String datefrom){
        asClick(DATE_FROM_SELECT)
        setFieldValue(DATE_FROM_SELECT,datefrom)
        waitForPageToLoad()
    }

    def selectDateTo(String dateto){
        asClick(DATE_T0_SELECT)
        setFieldValue(DATE_T0_SELECT, dateto)
        waitForPageToLoad()
    }

    def selectVoucherNoTypeItem(String typeItem) {
        selectDropdownItem(VOUCHER_NUMBER_TYPE_SELECT,typeItem)
    }

    def getCurrentPageItem() {
        return getDropdownItem(CURRENT_PAGE_SELECT)
    }

    def getCurrentPageItems() {
        return getDropdownItems(CURRENT_PAGE_SELECT)
    }

    def getPerPageCountItem() {
        return getDropdownItem(TRANS_PER_PAGE_SELECT)
    }

    def getPerPageCountItems() {
        return getDropdownItems(TRANS_PER_PAGE_SELECT)
    }



    def enterValueInTransDateFrom(String transDateFrom) {
        setFieldValue(TRANDATE_FROM_TEXT,transDateFrom)
    }

    def enterValueInTransDateTo(String transDateTo) {
        setFieldValue(TRANDATE_TO_TEXT,transDateTo)
    }

    def enterValueInVoucherNo(String voucherNoValue) {
        setFieldValue(VOUCHER_NUMBER_TEXT,voucherNoValue)
    }

    def clickRefreshBtn() {
        asClick(REFRESH_BTN)
        waitForPageToLoad()
    }

    def clickPrintPDF() {
        asClick(PRINT_PDF_BTN)
        //wait for PDF to be generate
        Thread.sleep(10*1000)
    }
    //endregion



    //region  composed methods for pages to implement business
    def refreshReport(String subsidiary,String periodFrom,String periodTo,String startDate,String endDate) {
        selectSubsidiaryItem(subsidiary)
        selectPeriodFromItem(periodFrom)
        selectPeriodToItem(periodTo)

        if(startDate != null && endDate != null) {
            enterValueInTransDateFrom(startDate)
            enterValueInTransDateTo(endDate)
        }

        clickRefreshBtn()
    }


    def refreshReportForVoucherNo(String subsidiary,String periodFrom,String periodTo,String numTypeItem,String numValue,String startDate,String endDate) {
        selectSubsidiaryItem(subsidiary)
        selectPeriodFromItem(periodFrom)
        selectPeriodToItem(periodTo)

        if(startDate != null && endDate != null) {
            enterValueInTransDateFrom(startDate)
            enterValueInTransDateTo(endDate)
        }

        selectVoucherNoTypeItem(numTypeItem)
        enterValueInVoucherNo(numValue)
        clickRefreshBtn()
    }



    def refreshReportInPage(String subsidiary,String periodFrom,String periodTo,String startDate,String endDate,String currentPageItem) {
        selectSubsidiaryItem(subsidiary)
        selectPeriodFromItem(periodFrom)
        selectPeriodToItem(periodTo)

        if(startDate != null && endDate != null) {
            enterValueInTransDateFrom(startDate)
            enterValueInTransDateTo(endDate)
        }

        clickRefreshBtn()
        selectCurrentPageItem(currentPageItem)
    }



    def switchAndGetDropdownItemsInPage(String subsidiary,String periodFrom,String periodTo,String startDate,String endDate,String transPerPage) {
        selectSubsidiaryItem(subsidiary)
        selectPeriodFromItem(periodFrom)
        selectPeriodToItem(periodTo)

        if(startDate != null && endDate != null) {
            enterValueInTransDateFrom(startDate)
            enterValueInTransDateTo(endDate)
        }

        clickRefreshBtn()
        selectTransPerPageItem(transPerPage)


        def currentPageItem = getCurrentPageItem()
        def currentPageItems = getCurrentPageItems()
        def perPageItem = getPerPageCountItem()
        def perPageItems = getPerPageCountItems()

        return ["currentPageItem":currentPageItem,
                "currentPageItems":currentPageItems,
                "perPageItem":perPageItem,
                "perPageItems":perPageItems]
    }

    def switchAndGetDropdownItemsInPage(String subsidiary,String periodFrom,String periodTo,String startDate,String endDate,String transPerPage, String datefrom, String dateto) {
        selectSubsidiaryItem(subsidiary)
        selectPeriodFromItem(periodFrom)
        selectPeriodToItem(periodTo)
        selectDateFrom(datefrom)
        selectDateTo(dateto)

        if(startDate != null && endDate != null) {
            enterValueInTransDateFrom(startDate)
            enterValueInTransDateTo(endDate)
        }

        clickRefreshBtn()


        def currentPageItem = getCurrentPageItem()
        def currentPageItems = getCurrentPageItems()
        def perPageItem = getPerPageCountItem()
        def perPageItems = getPerPageCountItems()

        return ["currentPageItem":currentPageItem,
                "currentPageItems":currentPageItems,
                "perPageItem":perPageItem,
                "perPageItems":perPageItems]
    }

    def parseTableData() {
        def table = new TableParser(getWebDriver())
        List<HashMap<String, String>> transTableData = table.parseTable(ITEM_TABLE,null,TABLE_ROW_ITERATOR)
        return  transTableData
    }


    def parseTableData(int index) {
        def table = new TableParser(getWebDriver())
        String tableXPath= String.format(ITEM_TABLE_REGEX,index)
        List<HashMap<String, String>> transTableData = table.parseTable(tableXPath,null,TABLE_ROW_ITERATOR)
        return  transTableData
    }
    //endregion

}
