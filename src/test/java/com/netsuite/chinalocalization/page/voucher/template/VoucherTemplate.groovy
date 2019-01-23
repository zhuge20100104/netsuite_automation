package com.netsuite.chinalocalization.page.voucher.template

abstract class VoucherTemplate {

    /**
     *
     * @param reportIndex: starts from 2
     * @return
     */
    String getReportTitleXpath(int reportIndex) {
        String XPATH_RPT_TITLE = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='header']" + reportIndex +"/tbody/tr/td"; //OK for both template
        return XPATH_RPT_TITLE;
    };

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    String getReportSubsidiaryXpath(int reportIndex) {
        String XPATH_RPT_SUBSIDIARY = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='body'][" + reportIndex + "]/tbody/tr[1]/td[1]";
        return XPATH_RPT_SUBSIDIARY;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    String getReportDateXpath(int reportIndex) {
        String XPATH_RPT_DATE = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='body'][" + reportIndex + "]/tbody/tr[1]/td[2]";
        return XPATH_RPT_DATE;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    String getReportDocNumberXpath(int reportIndex) {
        String XPATH_RPT_DOC_NO = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='body'][" + reportIndex + "]/tbody/tr[1]/td[3]";
        return XPATH_RPT_DOC_NO;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    String getReportTransactionTypeXpath(int reportIndex) {
        String XPATH_RPT_TRAN_TYPE = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='body'][" + reportIndex + "]/tbody/tr[2]/td[1]";
        return XPATH_RPT_TRAN_TYPE;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    String getReportPeriodXpath(int reportIndex) {
        String XPATH_RPT_PERIOD = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='body'][" + reportIndex + "]/tbody/tr[2]/td[2]";
        return XPATH_RPT_PERIOD;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    String getReportVoucherNumberXpath(int reportIndex) {
        String XPATH_RPT_VOUCHER_NO = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='body'][" + reportIndex + "]/tbody/tr[2]/td[3]";
        return XPATH_RPT_VOUCHER_NO;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    String getReportCurrencyXpath(int reportIndex) {
        String XPATH_RPT_CURRENCY = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='body'][" + reportIndex + "]/tbody/tr[3]/td[1]";
        return XPATH_RPT_CURRENCY;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    String getReportPageXpath(int reportIndex) {
        String XPATH_RPT_PAGE = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='body'][" + reportIndex + "]/tbody/tr[3]/td[3]";
        return XPATH_RPT_PAGE;
    }


    abstract String getReportCreatorXpath(int reportIndex) ;

    abstract String getReportApproverXpath(int reportIndex) ;

    abstract String getReportPosterXpath(int reportIndex) ;

    abstract String getReportAttachmentXpath(int reportIndex) ;

    /**
     *
     * @param headerTitle
     * @param headerSubTitle Debit/Credit, could be null
     * @param reportIndex : starts from 1
     * @param lineIndex: starts from 1
     * @return
     */
    abstract String getReportItemsOnLineXpath(String headerTitle, String headerSubTitle, int reportIndex, int lineIndex) ;


    /*------------define column index, Please do not delete these properties, these are used in getItemTableRowCells() -------------------------*/
    def lineNo = 1
    def lineDescription = 2
    def accountAndDescription = 3
    def transactionCurrency = 4
    def rate = 5
    def transactionCurrencyDebit = 6
    def transactionCurrencyCredit = 7
    def functionalCurrencyDebit = 8
    def functionalCurrencyCredit= 9

    def itemTableLineCell = ".//table[@class='itemtable']/tbody/tr[%s]/td[%t]"
    def getItemTableRowCells(rowIndex,String[] cols){
        def colsWithValue = [:]
        cols.each{ col ->
            def colIndex = getProperty(col)
            def xpath = itemTableLineCell.replace("%s",String.valueOf(rowIndex)).replace("%t",String.valueOf(colIndex))
            //colsWithValue << ["${col}":"${xpath}"]
            colsWithValue[col] = xpath
        }

        return colsWithValue
    }
}
