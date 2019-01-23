package com.netsuite.chinalocalization.page.Report

import com.netsuite.chinalocalization.common.control.CNDropdownList
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.chinalocalization.lib.NFormat
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.aut.pageobjects.Report
import com.netsuite.testautomation.aut.pageobjects.reporting.FilterOperator
import com.netsuite.testautomation.aut.pageobjects.reporting.ReportCustomFilter
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser
import org.junit.Assert

import javax.inject.Inject

class ReportPage extends PageBaseAdapterCN{
    @Inject NFormat format

    String CURRENT_PERIOD_CN = "本期金额"
    String CURRENT_PERIOD_EN = "CURRENT PERIOD"
    String PRIOR_PERIOD_CN = "上期金额"
    String PRIOR_PERIOD_EN = "PRIOR PERIOD"
    String IS_REPORT_RESULT_KEP_ITEM = "Items"
    String IS_REPORT_RESULT_KEP_CURRENT_PERIOD = "Current Period"
    String IS_REPORT_RESULT_KEP_PRIOR_PERIOD = "Prior Period"
    String commonPageSubsidiaryField = "//*[@id='crit_4_fs']"
    String commonPageAsOfField = "//*[@id='crit_3_to_fs']"
    String commonPageRefreshButton = "//input[@id='refresh']"
    String commonPageTable = "//table[@id='rptdataarea']"
    String commonPageTableRows = "//tbody//tr"


    /**
     * This page supports English or Chinese reports, but only works when system language is set to English.
     * Please change the language preference beforehand.
     */
    def getReportData(def reportObj){
        String reportName = reportObj.name;
        String id = context.getSavedReportId(reportName);
        Assert.assertNotNull(reportName + " does not exist", id);
        context.navigateTo("/app/reporting/reportrunner.nl?cr=" + id + "&reload=T&whence=");

        Report report = new Report(context, context.webDriver)
        report.footer.resetFilters()

        reportObj.standardFilters.each{ standardFilter ->
            if(report.footer.getFilterValue(standardFilter.label)){
                if(standardFilter.label == "As of"){
                    def dropdownList = new DropdownList(getContext(), getContext().webDriver, Locator.xpath("//*[@id=\"crit_3_to_fs\"]"))
                    dropdownList.selectItem(0)
                    dropdownList.selectItem((String) standardFilter.text)
                }
                else{
                    report.footer.setFilterValue(standardFilter.label, standardFilter.text)
                }
            }
        }

        reportObj.customFilters.each{ customFilter ->
            def filterOperator = FilterOperator.valueOf(customFilter.filterOperator)
            if(FilterOperator.IN.equals(filterOperator)){
                def multiSelectCustomFilter = report.footer.getCustomFilter(customFilter.label, filterOperator)
                this.setCustomMultiSelectFilterWithValue(multiSelectCustomFilter, customFilter.text)
            }
        }
        report.refresh()

        def reportData = [:]
        reportObj.rows.each{ row ->
            def reportRowData = [:]
            def rowIndex = report.getRowIndexWithTextInColumn(row.text, row.columnHeader);
            rowIndex=rowIndex-3
            reportObj.columns.each{ column ->
                HtmlElementHandle cell = report.getElementInColumnOfRow(column.columnHeader, rowIndex)
                String summaryValue = cell.getAttributeValue("summary_value")
                if(!summaryValue){
                    reportRowData[column.columnHeader] = cell.getAsText()
                }else{
                    reportRowData[column.columnHeader] = summaryValue
                }
            }
            reportData[row.text] = reportRowData
        }
        return reportData;
    }

    void setCustomMultiSelectFilterWithValue(ReportCustomFilter filter, List<String> values){
        if(!filter)
            return

        HtmlElementHandle popupIcon = context.webDriver.getHtmlElementByLocator(Locator.id(filter.id + "_popupIcon"))
        popupIcon.click(false)

        context.withinMultiSelectField(filter.id).setValues(values)
        popupIcon.click(false)
    }
    /**
     * This page supports English or Chinese reports, but only works when system language is set to English.
     * Please change the language preference beforehand.
     * update the result
     * @Modified By Molly
     */
    def getReportDataForIS(def reportObj){
        String reportName = reportObj.name;
        String id = context.getSavedReportId(reportName);
        Assert.assertNotNull(reportName + " does not exist", id);
        context.navigateTo("/app/reporting/reportrunner.nl?cr=" + id + "&reload=T&whence=");

        Report report = new Report(context, context.webDriver)
        report.footer.resetFilters()

        reportObj.standardFilters.each{ standardFilter ->
            if(report.footer.getFilterValue(standardFilter.label)){
                if(standardFilter.label == "As of"){
                    def dropdownList = new DropdownList(getContext(), getContext().webDriver, Locator.xpath("//*[@id=\"crit_3_to_fs\"]"))
                    dropdownList.selectItem(0)
                    dropdownList.selectItem((String) standardFilter.text)
                }
                else{
                    report.footer.setFilterValue(standardFilter.label, standardFilter.text)
                }
            }
        }

        reportObj.customFilters.each{ customFilter ->
            def filterOperator = FilterOperator.valueOf(customFilter.filterOperator)
            if(FilterOperator.IN.equals(filterOperator)){
                def multiSelectCustomFilter = report.footer.getCustomFilter(customFilter.label, filterOperator)
                this.setCustomMultiSelectFilterWithValue(multiSelectCustomFilter, customFilter.text)
            }
        }
        report.refresh()

        def reportData = []
        reportObj.rows.each{ row ->
            def reportRowData = [:]
            def rowIndex = report.getRowIndexWithTextInColumn(row.text, row.columnHeader);
            rowIndex=rowIndex-3
            reportObj.columns.each{ column ->
                HtmlElementHandle cell = report.getElementInColumnOfRow(column.columnHeader, rowIndex)
                String summaryValue = cell.getAttributeValue("summary_value")
                if(!summaryValue){
                    if (column.columnHeader.contains(getCURRENT_PERIOD_CN()) ||column.columnHeader.contains(getCURRENT_PERIOD_EN())) {
                        reportRowData[getIS_REPORT_RESULT_KEP_CURRENT_PERIOD()] = cell.getAsText()
                    } else if (column.columnHeader.contains(getPRIOR_PERIOD_CN()) || column.columnHeader.contains(getPRIOR_PERIOD_EN())) {
                        reportRowData[getIS_REPORT_RESULT_KEP_PRIOR_PERIOD()] = cell.getAsText()
                    }

                }else{
                    if (column.columnHeader.contains(getCURRENT_PERIOD_CN()) ||column.columnHeader.contains(getCURRENT_PERIOD_EN())) {
                        reportRowData[getIS_REPORT_RESULT_KEP_CURRENT_PERIOD()] = summaryValue
                    } else if (column.columnHeader.contains(getPRIOR_PERIOD_CN()) ||column.columnHeader.contains(getPRIOR_PERIOD_EN())) {
                        reportRowData[getIS_REPORT_RESULT_KEP_PRIOR_PERIOD()] = summaryValue
                    }
                }
            }
            reportRowData[getIS_REPORT_RESULT_KEP_ITEM()] = row.text
            reportData.add(reportRowData)
        }
        return reportData;
    }


    /**
     * @desc navigateToSavedReportPage
     * @Author kun.y.yang@oracle.com
     * @Date 2018/3/20
     */
    def navigateToSavedReportPage(String reportName) {
        String id = context.getSavedReportId(reportName)
        context.navigateTo("/app/reporting/reportrunner.nl?cr=" + id + "&reload=T&whence=");

    }
    /**
     * @desc selectASOF
     * @Author kun.y.yang@oracle.com
     * @Date 2018/3/20
     */
    def selectASOF(String asof) {
      // asDropdownList(locator: commonPageAsOfField).selectItem(asof)
        context.withinDropdownlist()
        def dropdownList = new DropdownList(getContext(), getContext().webDriver, Locator.xpath(commonPageAsOfField))
        dropdownList.selectItem(asof)
    }
    /**
     * @desc selectSubsidiary
     * @Author kun.y.yang@oracle.com
     * @Date 2018/3/20
     */
    def selectSubsidiary(String subsidiary) {
        context.withinDropdownlist()
        def dropdownList = new DropdownList(getContext(), getContext().webDriver, Locator.xpath(commonPageSubsidiaryField))
        dropdownList.selectItem(subsidiary)
     // asDropdownList(locator: commonPageSubsidiaryField).selectItem(subsidiary)
    }
    /**
     * @desc clickRefresh
     * @Author kun.y.yang@oracle.com
     * @Date 2018/3/20
     */
    def clickRefresh() {
        asElement(commonPageRefreshButton).click()
    }

    /**
     * @desc getCommonReportdata
     * @Author kun.y.yang@oracle.com
     * @Date 2018/3/20
     */
    def getCommonReportdata() {
        TableParser table = new TableParser(context.webDriver);
        List<HashMap<String, String>> commonBalanceSheetTable = table.parseTable(commonPageTable,null,commonPageTableRows)
        Map<String,List<String>> balanceMap = new HashMap<String,List<String>>()
        for( int i = 0 ; i < commonBalanceSheetTable.size() ; i++ ) {
            List<String> balance = new ArrayList<String>()
            HashMap<String, String> row = commonBalanceSheetTable.get(i)
            String label = row.get("0")
            label = format.formatCharacter(label)
            String closingBalance = row.get("1")
            String openingBalance = row.get("2")
            balance.add(closingBalance)
            balance.add(openingBalance)
            //toUpperCase
            balanceMap.put(label.toUpperCase(),balance)
        }
        return balanceMap
    }

}
