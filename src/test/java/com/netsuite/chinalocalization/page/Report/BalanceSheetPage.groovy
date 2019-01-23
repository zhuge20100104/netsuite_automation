package com.netsuite.chinalocalization.page

import com.google.inject.Inject
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.driver.BrowserActions
import com.netsuite.testautomation.html.Locator
import org.openqa.selenium.Keys
import com.netsuite.chinalocalization.common.control.CNDropdownList

import javax.xml.bind.Element

class BalanceSheetPage extends PageBaseAdapterCN {

    @Inject
    ElementHandler elementHandler

    private static String TITLE = "China Balance Sheet"

    BalanceSheetPage() {
    }

    //XPATH

    //XPATH_INPUT
    private static final String XPATH_INPUT_NAME = "//input[@name='custpage_reportname']"
    private static final String XPATH_LOCATIONS = "//td[contains(@id,'row_custpage_location4')]/a"
    private static final String XPATH_DEPARTMENTS = "//td[contains(@id,'row_custpage_department5')]/a"
    private static final String XPATH_CLASSES = "//td[contains(@id,'row_custpage_class6')]/a"
    //XPATH_BUTTON
    private static final String XPATH_BTN_REFRESH = "//input[@name='custpage_refresh']"
    private static final String XPATH_BTN_EXPORT_EXCEL = "//*[@id='custpage_export_excel']"
    private static final String XPATH_BTN_EXPORT_PDF = "//*[@id='custpage_export_pdf']"

    //XPATH_TABLE
    private static final String XPATH_TABLE_BLS = "//table[@id='blsheet_data']"
    private static final String XPATH_TR_IN_TABLE = "//table[@id='blsheet_data']/tbody/tr"
    private static final String XPATH_TABLE_HEADER = "//*[@id='blsheet_header']/tbody/tr[1]/td"
    private static final String XPATH_RPT_HEADER = ".//*[@id='blsheet_header']/tbody/tr[%s]"
    //*[@id="blsheet_header"]
    private static final int FILTERS_INDEX = 4
    //FIELD_ID
    private static final String FIELD_ID_NAME = "custpage_reportname"
    private static final String FIELD_ID_UNIT = "custpage_unit"
    private static final String FIELD_ID_SUBSIDIARY = "custpage_subsidiary"
    private static final String FIELD_ID_ASOF = "custpage_asof"
    //FIELD_ID (Customize Filters)
    private static final String FIELD_ID_LOCATION = "custpage_location"
    private static final String FIELD_ID_DEPARTMENT = "custpage_department"
    private static final String FIELD_ID_CLASS = "custpage_class"

    private static final String XPATH_LOCATION_LABEL = "//*[@id='custpage_location_fs_lbl']"
    private static final String XPATH_CLASS_LABEL = "//*[@id='custpage_class_fs_lbl']"
    private static final String XPATH_DEPARTMENT_LABEL = "//*[@id='custpage_department_fs_lbl']"

    private static final String XPATH_ADD_NAME = "//*[@id='custpage_reportname_popup_new']"
    private static final String XPATH_POP_NAME = "//*[@id='custpage_reportname_popup_link']"

    //XPATH
    //AddSheetNamePage
    private static final String XPATH_NAME = "//*[@id='name']"
    private static final String XPATH_SUBMIT = "//*[@id='submitter']"

    //XPATH
    //EditSheetNamePage
    private static final String XPATH_EDIT = "//*[@id='edit']"
    private static final String XPATH_ACTIONS = "//*[@class='pgm_action_menu']"

    //XPATH
    //EditSheetNamePage
    private static final String XPATH_POPUP_OK = "//*[@id=\"ext-gen5\"]/div/div/button"


    def setLocation(String location) {
        CNDropdownList ddl = context.withinCNDropdownlist(FIELD_ID_LOCATION)
        ddl.selectItem(location)
    }

    def getLocation() {
        return context.getFieldText(FIELD_ID_LOCATION)
    }

    def setDepartment(String department) {
        CNDropdownList ddl = context.withinCNDropdownlist(FIELD_ID_DEPARTMENT)
        ddl.selectItem(department)
    }

    def getDepartment() {
        return context.getFieldText(FIELD_ID_DEPARTMENT)
    }

    def setClazz(String clazz) {
        CNDropdownList ddl = context.withinCNDropdownlist(FIELD_ID_CLASS)
        ddl.selectItem(clazz)
    }

    def getClazz() {
        return context.getFieldText(FIELD_ID_CLASS)
    }

    def navigateToURL() {
        String url = context.resolveURL("customscript_sl_cn_blsheet", "customdeploy_sl_cn_blsheet")
        context.navigateToNoWait(url)
        elementHandler.waitForElementToBePresent(context.webDriver,XPATH_INPUT_NAME)
    }

    def clickRefresh() {
        asElement(XPATH_BTN_REFRESH).click()
    }

    def exportPDF() {
        asElement(XPATH_BTN_EXPORT_PDF).click()
    }

    def exportExcel() {
        asElement(XPATH_BTN_EXPORT_EXCEL).click()
    }

//    def fillBalanceSheetName(String name) {
//        asElement(XPATH_INPUT_NAME).sendKeys(name)
//    }

    def fillBalanceSheetName(String name) {
        if (!getNameOptions().contains(name)) {
            addName(name)
        }
        selectName(name)
    }


    def emptyBalanceSheetName() {
        asElement(XPATH_INPUT_NAME).clear()
    }

    def selectUnit(String unit) {
        CNDropdownList ddl = context.withinCNDropdownlist(FIELD_ID_UNIT)
        ddl.selectItem2(unit)
    }

    def selectASOF(String asof) {
        CNDropdownList ddl = context.withinCNDropdownlist(FIELD_ID_ASOF)
        ddl.selectItem(asof)
    }

    def selectSubsidiary(String subsidiary) {
        CNDropdownList ddl = context.withinCNDropdownlist(FIELD_ID_SUBSIDIARY)
        ddl.selectItem(subsidiary)
    }

    def selectDepartment(def department) {
        multiSelectFromMultiList(department, XPATH_DEPARTMENTS)
    }

    def getDepartments() {
        getOptionsFromMultiList(XPATH_DEPARTMENTS)
    }

    def getDepartmentOptions() {
        return context.withinCNDropdownlist(FIELD_ID_DEPARTMENT).getOptions()
    }

    def selectLocation(String location) {
        multiSelectFromMultiList(location, XPATH_LOCATIONS)
    }

    def selectClass(String cla) {
        multiSelectFromMultiList(cla, XPATH_CLASSES)
    }

    def selectDepartments(List<String> departments) {
        multiSelectFromMultiList(departments, XPATH_DEPARTMENTS)
    }

    def multiSelectFromMultiList(def names, String xpath) {
        List<HtmlElementHandle> elements = asElements(xpath)
        BrowserActions action = context.webDriver.browserActionsInstance
        action.keyDown(Keys.CONTROL).build().perform()

        elements.each {
            if (names.contains(it.asText)) {
                it.click()
            }
        }
        action.keyUp(Keys.CONTROL).build().perform()
        context.webDriver.browserActionsInstance.keyUp(Keys.CONTROL).build().perform()
    }

    def getOptionsFromMultiList(String xpath) {
        List<HtmlElementHandle> elements = asElements(xpath)
        def ret = []
        elements.each {
            ret.add(it.asText())
        }
        return ret
    }


    def setSearchParameter(def reportObj) {

        if (reportObj.name) {
            fillBalanceSheetName(reportObj.name)
        }

        //Standard
        reportObj.standardFilters.each { filter ->
            if (filter.label == "Subsidiary") {
                selectSubsidiary(filter.text)
            }
            if (filter.label == "As of") {
                selectASOF(filter.text)
            }
            if (filter.label == "Unit") {
                selectUnit(filter.text)
            }
        }

        //Customer Filter
        reportObj.customFilters.each { filter ->
            if (filter.label == "Department") {
                setDepartment(filter.text)
            }
            if (filter.label == "Location") {
                setLocation(filter.text)
            }
            if (filter.label == "Class") {
                setClazz(filter.text)
            }
        }
    }

    def getTableValue(def rowsObj, def colsObj) {
        def rows = []
        def cols = []
        rowsObj.each() {
            rows.add(it.text)
        }
        colsObj.each() {
            cols.add(it.columnHeader)
        }
        this.waitForPageToLoad()
        def result = [:]

        for (int i = 2; i < 37; i++) {
            String xpath_tr = XPATH_TR_IN_TABLE + "[" + i + "]"
            for (int j = 1; j < 9; j++) {
                def resultRow = [:]
                HtmlElementHandle td_account = asElement(xpath_tr + "/td[" + j + "]")
                if (rows.contains(td_account.asText)) {
                    HtmlElementHandle td_close = asElement(xpath_tr + "/td[" + (j + 2) + "]")
                    HtmlElementHandle td_open = asElement(xpath_tr + "/td[" + (j + 3) + "]")
                    resultRow[cols[0]] = addRMBCashIcon(td_close.getAsText())
                    resultRow[cols[1]] = addRMBCashIcon(td_open.getAsText())
                    result[td_account.asText] = resultRow
                }
            }
        }
        return result
    }

    def getSingleColTableValue(def rowsObj, def colsObj) {
        def rows = []
        def cols = []
        rowsObj.each() {
            rows.add(it.text)
        }
        colsObj.each() {
            cols.add(it.columnHeader)
        }
        this.waitForPageToLoad()
        def result = [:]

        for (int i = 2; i < 37; i++) {
            String xpath_tr = XPATH_TR_IN_TABLE + "[" + i + "]"
            for (int j = 1; j < 9; j++) {
                def resultRow = [:]
                HtmlElementHandle td_account = asElement(xpath_tr + "/td[" + j + "]")
                if (rows.contains(td_account.asText)) {
                    HtmlElementHandle td_close = asElement(xpath_tr + "/td[" + (j + 2) + "]")
                    resultRow[cols[0]] = addRMBCashIcon(td_close.getAsText())
                    result[td_account.asText] = resultRow
                }
            }
        }
        return result
    }

    private def addRMBCashIcon(valueString) {
        if (valueString.indexOf("-") != -1) {
            valueString = valueString.replace("-", "-¥")
        } else {
            valueString = "¥" + valueString
        }
        return valueString
    }

    def getBalanceSheetData(def report) {
        navigateToURL()
        setSearchParameter(report)
        clickRefresh()
        def actualData = this.getTableValue(report.rows, report.columns)
        return actualData
    }

    def getSingleColBalanceSheetData(def report) {
        navigateToURL()
        setSearchParameter(report)
        clickRefresh()
        def actualData = this.getSingleColTableValue(report.rows, report.columns)
        return actualData
    }


    def refreshBalanceSheetData(def report) {
        navigateToURL()
        setSearchParameter(report)
        clickRefresh()
        Thread.sleep(5 * 1000)
    }

    boolean isDepartmentAppear() {
        return isExist(XPATH_DEPARTMENTS)
    }

    boolean isDepartmentLabelAppear() {
        return isExist(XPATH_DEPARTMENT_LABEL)
    }

    boolean isLoacationAppear() {
        return isExist(XPATH_LOCATIONS)
    }

    boolean isLocationLabelAppear() {
        return isExist(XPATH_LOCATION_LABEL)
    }

    boolean isClassAppear() {
        return isExist(XPATH_CLASSES)
    }

    boolean isClassLabelAppear() {
        return isExist(XPATH_CLASS_LABEL)
    }

    boolean isExist(String XPATH) {
        return asElement(XPATH) != null
    }


    boolean isExportExcelAppear() {
        return isExist(XPATH_BTN_EXPORT_EXCEL)
    }

    boolean isExportPDFAppear() {
        return isExist(XPATH_BTN_EXPORT_PDF)
    }

    boolean isReportAppear() {
        return isExist(XPATH_TABLE_HEADER)
    }


    def String trimText(text) {
        return text.trim().replaceAll("[\\u00A0]+", "")
    }

    def navigateToBalanceSheetPageAndSetFilters(def testData) {
        navigateToURL()
        fillBalanceSheetName(testData.reportName)
        if(context.isOneWorld()){
            selectSubsidiary(testData.subsidiary)
        }
        selectASOF(testData.asof)
        selectUnit(testData.unit)
        clickRefresh()
        waitForPageToLoad()
    }

    /**
     * Get BalanceSheet Header Data.
     *
     * @param lineNum - The lineNum of header. Valid value should be 4.
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


    def addName_ori(String name) {
        clickAddName()
        context.switchToWindowWithURL(getAddNamePageURL())

        fillName(name)
        clickSave()
    }

    def addName(String name) {
        String firstHandle = context.webDriver.getWindowHandle()
        clickAddName()
        String secondHandle = get2ndHandle(firstHandle, context.webDriver.getWindowHandles())
        context.switchToWindowInstance(secondHandle)
        fillName(name)
        clickSave()
    }

    def deleteName(String name) {
        String firstHandle = context.webDriver.getWindowHandle()
        clickPopupName()
        String secondHandle = get2ndHandle(firstHandle, context.webDriver.getWindowHandles())
        context.switchToWindowInstance(secondHandle)
        clickEdit()
        hoverToActions()
        acceptUpcomingConfirmationDialog()
        clickButtonByAPI("delete")
        context.webDriver.closeAllWindowsButOne(firstHandle)
        context.switchToWindowInstance(firstHandle)
    }

    def selectName(String name) {
        DropdownList ddl = context.withinDropdownlist(FIELD_ID_NAME)
        ddl.selectItem(name)
    }

    def getNameOptions() {
        return context.getNldropdownOptions(FIELD_ID_NAME)
    }

    def clickAddName() {
        asElement(XPATH_ADD_NAME).mouseOver()
        asClick(XPATH_ADD_NAME)
    }

    def clickPopupName() {
        asElement(XPATH_POP_NAME).mouseOver()
        asClick(XPATH_POP_NAME)
    }

    def clickEdit() {
        asElement(XPATH_EDIT).click()
    }

    def clickSave() {
        asElement(XPATH_SUBMIT).click()
    }

    def fillName(String name) {
        asElement(XPATH_NAME).sendKeys(name)
    }

    def hoverToActions() {
        asElement(XPATH_ACTIONS).mouseOver()
    }

    /**
     * @author Jingzhou.wang
     * @desc
     * @param First windowHandle
     * @param WindowHandles contains first window handle
     * @return Second windows handle
     */
    String get2ndHandle(String firstHandle, Set<String> handles) {
        for (String han : handles) {
            if (han != firstHandle) {
                return han
            }
        }
    }


}
