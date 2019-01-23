package com.netsuite.chinalocalization.page.Report

import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.common.control.CNDropdownList
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.aut.NetSuiteApp
import org.junit.Assert

import javax.inject.Inject


class CashFlowStatementPage extends PageBaseAdapterCN {

    @Inject
    ElementHandler elementHandler

    private NetSuiteApp aut

    private static String TITLE = "China Cash Flow Statement"
    private static String URL

    // Params xpath
    private static final String XPATH_REFRESH_BTN = ".//*[@id='refresh']"
    private static final String XPATH_EXPORT_EXCEL_BTN = "//*[@id='tdbody_custpage_export_excel']"
    private static final String XPATH_EXPORT_PDF_BTN = "//*[@id='tdbody_custpage_export_pdf']"
    private static final String XPATH_SUBSIDIARY_DROPDOWN = ".//*[@name='inpt_custpage_subsidiary']"
    private static final String XPATH_PERIOD_FROM_DROPDOWN = ".//*[@name='inpt_custpage_periodfrom']"
    private static final String XPATH_PERIOD_TO_DROPDOWN = ".//*[@name='inpt_custpage_periodto']"
    private static final String XPATH_DEPARTMENT_DROPDOWN = ".//*[@name='inpt_custpage_department']"
    private static final String XPATH_LOCATION_DROPDOWN = ".//*[@name='inpt_custpage_location']"
    private static final String XPATH_CLASS_DROPDOWN = ".//*[@name='inpt_custpage_class']"

    //Fieldid
    private static final String subsidiaryFieldId = "custpage_subsidiary"
    private static final String periodfromFieldId = "custpage_periodfrom"
    private static final String periodtoFieldId = "custpage_periodto"


    private static final String FID_UNIT = "unit"
    private static final String FID_LOCATION = "custpage_location"
    private static final String FID_DEPARTMENT = "custpage_department"
    private static final String FID_CLASS = "custpage_class"
    private static final String FID_SUBSIDIARY = "custpage_subsidiary"

    // Cashflow report xpath
    private static final String XPATH_RPT_LINE = ".//*[@id='cashflow_data']/tbody/tr[%s]/td[%s]"
    private static final String XPATH_RPT_HEADER = ".//*[@id='cashflow_header']/tbody/tr[%s]"
    private static final String XPATH_HEADER = ".//*[@id='cashflow_header']"

    private static final int CURRENT_PERIOD_INDEX = 3
    private static final int PRIOR_PERIOD_INDEX = 4

    CashFlowStatementPage(NetSuiteApp aut) {
        this.aut = aut
    }

    CashFlowStatementPage() {
    }

    def navigateToURL() {
        //context.navigateTo(CURL.HOME_CURL)
        context.navigateToHomePage()
        String url = context.resolveURL("customscript_sl_cn_cashflow", "customdeploy_sl_cn_cashflow")
        context.navigateTo(url)
    }

    void setParameters(String subsidiary, String periodFrom, String periodTo) {
        setSubsidiary(subsidiary)
        setPeriodFrom(periodFrom)
        setPeriodTo(periodTo)
    }

    def setSubsidiary(String subsidiary) {
        if (subsidiary != null && subsidiary != "") {
            context.setFieldWithValue(subsidiaryFieldId, subsidiary)
            context.waitForPageToLoad()
        } else {
            println "Subsidiary is Null!"
        }
    }

    def setSubsidiaryByName(String subsidiary) {
        if (subsidiary != null && subsidiary != "") {
            String subId = Utility.getNonCashflowSubsidiaryId(context, subsidiary);
            context.setFieldWithValue(subsidiaryFieldId, subId)
        } else {
            println "Subsidiary is Null!"
        }
    }


    def setPeriodFrom(String periodFrom) {
        if (periodFrom != null && periodFrom != "") {
            context.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_PERIOD_FROM_DROPDOWN)).sendKeys(periodFrom)
        } else {
            println "SetPeriodFrom is Null!"
        }
    }

    def setPeriodTo(String periodTo) {
        if (periodTo != null && periodTo != "") {
            context.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_PERIOD_TO_DROPDOWN)).sendKeys(periodTo)
        } else {
            println "SetPeriodTo is Null!"
        }
    }

    def setUnit(String unit) {
        if (unit != null && unit != "") {
            context.setFieldWithText(FID_UNIT, unit)
        } else {
            println "Unit is Null!"
        }
    }

    def setLocation(String location) {
        if (location.contains(":")) {
            location = location.split(":")[1].trim()
        }
        CNDropdownList ddl = context.withinCNDropdownlist(FID_LOCATION)
        ddl.selectItem(location)
    }

    def setDepartment(String department) {
        if (department.contains(":")) {
            department = department.split(":")[1].trim()
        }
        CNDropdownList ddl = context.withinCNDropdownlist(FID_DEPARTMENT)
        ddl.selectItem(department)
    }

    def setClass(String clazz) {
        if (clazz.contains(":")) {
            clazz = clazz.split(":")[1].trim()
        }
        CNDropdownList ddl = context.withinCNDropdownlist(FID_CLASS)
        ddl.selectItem(clazz)
    }

    def getDepartmentOptions() {
        return context.withinCNDropdownlist(FID_DEPARTMENT).getOptions()
    }

    def getClassOptions() {
        return context.withinCNDropdownlist(FID_CLASS).getOptions()
    }

    def getLocationOptions() {
        return context.withinCNDropdownlist(FID_LOCATION).getOptions()
    }

    def getSubsidiaryOptions() {
        if (context.withinCNDropdownlist(FID_SUBSIDIARY)) {
            return context.withinCNDropdownlist(FID_SUBSIDIARY).getOptions()
        }
    }

    String getPeriodFrom() {
        return context.getFieldText(periodfromFieldId)
    }

    String getPeriodTo() {
        return context.getFieldText(periodtoFieldId)
    }

    void clickRefresh() {
        context.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_REFRESH_BTN)).click()
        def rtp_header = elementHandler.waitForElementToBePresent(context.webDriver,XPATH_HEADER)
        Assert.assertNotNull(rtp_header)
    }

    void exportExcel() {
        context.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_EXPORT_EXCEL_BTN)).click()
    }

    void exportPDF() {
        context.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_EXPORT_PDF_BTN)).click()
    }

    /**
     * Get Cash Flow Statement Line Data.
     *
     * @param lineNum - The lineNum shown in 'Lines' column. Valid value should be from 1 to 38.
     * @param colNum - Items - 1, Lines - 2, Current Period - 3, Prior Period - 4
     * @return
     */
    String getReportData(int lineNum, int colNum) {
        Locator locator = Locator.xpath(String.format(XPATH_RPT_LINE, lineNum + 1, colNum))
        return context.webDriver.getHtmlElementByLocator(locator).getAsText()
    }

    /**
     * Get Cash Flow Statement Line Data.
     *
     * @param lineItem - The CFS item
     * @param colNum - Items - 1, Lines - 2, Current Period - 3, Prior Period - 4
     * @return
     */
    def getReportData(def cfsItems, int colNum) {
        Map ret = [:]
        def value
        String XPATH_CURRENT_PERIOD
        String XPATH_PRIOR_PERIOD

        while (asElement(XPATH_HEADER) == null) {
            println "Waiting for report to show."
            Thread.sleep(3000)
        }

        for (int i = 2; i < 39; i++) {
            String XPATH_ITEM = ".//*[@id='cashflow_data']/tbody/tr[" + i + "]/td[1]"
            Locator locator = Locator.xpath(XPATH_ITEM)
            String label = context.webDriver.getHtmlElementByLocator(locator).getAsText().toString()
            if (cfsItems.contains(label)) {
                XPATH_CURRENT_PERIOD = ".//*[@id='cashflow_data']/tbody/tr[" + i + "]/td[3]"
                XPATH_PRIOR_PERIOD = ".//*[@id='cashflow_data']/tbody/tr[" + i + "]/td[4]"
                if (colNum == 3) {
                    value = context.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_CURRENT_PERIOD)).getAsText()
                }
                if (colNum == 4) {
                    value = context.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_PRIOR_PERIOD)).getAsText()
                }
                ret[label] = value
            }
        }
        return ret
    }

    String getReportDataCurrentPeriod(int lineNum) {
        return getReportData(lineNum, CURRENT_PERIOD_INDEX)
    }

    String getReportDataPriorPeriod(int lineNum) {
        return getReportData(lineNum, PRIOR_PERIOD_INDEX)
    }


    String getHeaderData(int lineNum, int colNum) {
        Locator locator = Locator.xpath(String.format(XPATH_RPT_HEADER, lineNum, colNum))
        return context.webDriver.getHtmlElementByLocator(locator).getAsText()
    }

    /**
     * Get Cash Flow Statement Header Data.
     *
     * @param lineNum - The lineNum of header. Valid value should be 3 and 4.
     * @param filter - Set filter="Location" if the header line is "Location: Beijing"
     * @return Filted value ->Beijing
     */
    String getHeaderData(int lineNum, String filter) {
        String ret = ""
        boolean found = false
        for (int i = 1; i < 4; i++) {
            HtmlElementHandle td_filter = asElement((String.format(XPATH_RPT_HEADER, lineNum)) + "/td[" + i + "]")
            String filter_text = td_filter.getAsText()
            if (filter_text.contains(filter)) {
                found = true
                ret = filter_text.split(":")[1]
                break
            }
        }
        if (found == false) {
            println "Did not found filter : " + filter
        }
        return ret
    }

    def setSearchParameter(def paramsObj) {
        //Standard filter
        paramsObj.standardFilters.each { filter ->
            if (filter.label == "Subsidiary") {
                setSubsidiaryByName(filter.text)
            }
            if (filter.label == "Period From") {
                setPeriodFrom(filter.text)
            }
            if (filter.label == "Period To") {
                setPeriodTo(filter.text)
            }
            if (filter.label == "Unit") {
                setUnit(filter.text)
            }
        }
        //Customer Filter
        paramsObj.customFilters.each { filter ->
            if (filter.label == "Department") {
                setDepartment(filter.text)
            }
            if (filter.label == "Location") {
                setLocation(filter.text)
            }
            if (filter.label == "Class") {
                setClass(filter.text)
            }
        }
    }

    def SearchParameter(def paramsObj, def label) {
        //Customer Filter
        int len = paramsObj.customFilters.size()
        for (int i = 0; i < len; i++) {
            if (paramsObj.customFilters[i].label == label) {
                if (paramsObj.customFilters[i].text.contains(":")) {
                    return paramsObj.customFilters[i].text.split(":")[1].trim()
                } else {
                    return paramsObj.customFilters[i].text
                }
            }
        }
    }

}
