package com.netsuite.chinalocalization.page.Report

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser


class IncomeStatementPage extends PageBaseAdapterCN {


	IncomeStatementPage() {
    }

    //BUTTON
    String XPATH_BTN_REFRESH = ".//*[@id='custpage_refresh']"
    String XPATH_BTN_EXP_EXCEL = ".//*[@id='custpage_export_excel']"
    String XPATH_BTN_EXP_PDF = ".//*[@id='custpage_export_pdf']"
    String XPATH_BTN_ERROR_DIALOG = "//button[@value='true']"

    //Input Search condition
    String FIELD_ID_REPORT_NAME = "custpage_reportname"
    String XPATH_PARAM_SUBSIDIARY = "//*[@id=\"custpage_subsidiary_fs\"]"
    String XPATH_PARAM_PERIOD = "//*[@id=\"custpage_period_fs\"]"
    String XPATH_PARAM_UNIT = "//*[@id=\"custpage_unit_fs\"]"
    String XPATH_PARAM_LOCATION = "//*[@id=\"custpage_location_fs\"]"
    String XPATH_PARAM_DEPARTMENT = "//*[@id=\"custpage_department_fs\"]"
    String XPATH_PARAM_CLASS = "//*[@id=\"custpage_class_fs\"]"

    //Report Header
    String XPATH_REPORT_HEADER_NAME = ".//*[@id='incomestatement_header']/tbody/tr[1]/td/b"

    String XPATH_REPORT_HEADER_PREPARED_BY = ".//*[@id='incomestatement_header']/tbody/tr[3]/td[1]"
    String XPATH_REPORT_HEADER_PERIOD = ".//*[@id='incomestatement_header']/tbody/tr[3]/td[2]"
    String XPATH_REPORT_HEADER_CURRENCY = ".//*[@id='incomestatement_header']/tbody/tr[3]/td[3]"

    String XPATH_REPORT_HEADER_L4_LEFT = ".//*[@id='incomestatement_header']/tbody/tr[4]/td[1]"
    String XPATH_REPORT_HEADER_L4_CENTER = ".//*[@id='incomestatement_header']/tbody/tr[4]/td[2]"
    String XPATH_REPORT_HEADER_L4_RIGHT = ".//*[@id='incomestatement_header']/tbody/tr[4]/td[3]"
    // Report table
    String XPATH_IS_REPORT_TABLE = ".//table[@id='incomestatement_data']"
    String XPATH_IS_REPORT_TABLE_ROW = "//tbody//tr"

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

    // Map key for report table
    static String REPORT_TABLE_MAP_KEY_ITEMS = "Items"
    static String REPORT_TABLE_MAP_KEY_LINES = "Lines"
    static String REPORT_TABLE_MAP_KEY_CURRENT_AMOUNT= "Current Period"
    static String REPORT_TABLE_MAP_KEY_PRIOR_AMOUNT = "Prior Period"

    def clickRefresh() {
        asElement(XPATH_BTN_REFRESH).click()
    }
    def clickExpToExcel() {
        asElement(XPATH_BTN_EXP_EXCEL).click()
    }
    def clickExpToPDF() {
        asElement(XPATH_BTN_EXP_PDF).click()
    }
    def setReportName(name) {
        context.setFieldWithValue(FIELD_ID_REPORT_NAME, name)
    }

    def getPeriodOptions(){
        asDropdownList(fieldId:'custpage_period').getOptions()
    }

    def getUnitOptions(){
        asDropdownList(fieldId:'custpage_unit').getOptions()
    }

    def getSubsidiaryOptions(){
        asDropdownList(fieldId:'custpage_subsidiary').getOptions()
    }

    def getLocation(){
        asDropdownList(locator: XPATH_PARAM_LOCATION).getValue()
    }
    def getLocationOptions(){
        asDropdownList(locator: XPATH_PARAM_LOCATION).getOptions()
    }

    def getDepartment(){
        asDropdownList(locator: XPATH_PARAM_DEPARTMENT).getValue()
    }
    def getDepartmentOptions(){
        asDropdownList(locator: XPATH_PARAM_DEPARTMENT).getOptions()
    }

    def getMyClass(){
        asDropdownList(locator: XPATH_PARAM_CLASS).getValue()
    }
    def getClassOptions(){
        asDropdownList(locator: XPATH_PARAM_CLASS).getOptions()
    }
    def getReportHeaderPreparedByText() {
        asElement(XPATH_REPORT_HEADER_PREPARED_BY).getAsText()
    }
    def getReportHeaderPeriodText() {
        asElement(XPATH_REPORT_HEADER_PERIOD).getAsText()
    }
    def getReportHeaderCurrencyText() {
        asElement(XPATH_REPORT_HEADER_CURRENCY).getAsText()
    }
    def getReportHeaderL4LeftText() {
        asElement(XPATH_REPORT_HEADER_L4_LEFT).getAsText()
    }
    def getReportHeaderL4CenterText() {
        asElement(XPATH_REPORT_HEADER_L4_CENTER).getAsText()
    }
    def getReportHeaderL4RightText() {
        asElement(XPATH_REPORT_HEADER_L4_RIGHT).getAsText()
    }

    def getTableValue(){

        TableParser table = new TableParser(context.webDriver);
        List<HashMap<String, String>> incomeTable = table.parseTable(XPATH_IS_REPORT_TABLE,null,XPATH_IS_REPORT_TABLE_ROW)
        def incomeRows = []
        incomeTable.each {
            def rowMap = [:]
            rowMap["Items"] = it.get("0")
            rowMap["Lines"] = it.get("1")
            rowMap["Current Period"] = it.get("2")
            rowMap["Prior Period"] = it.get("3")
            incomeRows.add(rowMap)
        }
        return incomeRows
    }

    def getTableValue(rowObj){

        TableParser table = new TableParser(context.webDriver);
        List<HashMap<String, String>> incomeTable = table.parseTable(XPATH_IS_REPORT_TABLE,null,XPATH_IS_REPORT_TABLE_ROW)
        def incomeRows = []
        incomeTable.each {
            def rowMap = [:]
            rowMap[REPORT_TABLE_MAP_KEY_ITEMS] = it.get("0")
            rowMap[REPORT_TABLE_MAP_KEY_LINES] = it.get("1")
            rowMap[REPORT_TABLE_MAP_KEY_CURRENT_AMOUNT] = it.get("2")
            rowMap[REPORT_TABLE_MAP_KEY_PRIOR_AMOUNT] = it.get("3")
            incomeRows.add(rowMap)
        }
        def resultList = []
        rowObj.each { targetRow ->
            incomeRows.each {it ->
                def item = it.find {
                    key, value -> value.contains(targetRow.text)
                }
                if (item) {
                    resultList.add(it)
                }
            }
        }
        return resultList
    }

    boolean isClassVisible(){
        return isExist(XPATH_PARAM_CLASS)
    }
    boolean isDepartmentVisible(){
        return isExist(XPATH_PARAM_DEPARTMENT)
    }
    boolean isLocationVisible(){
        return isExist(XPATH_PARAM_LOCATION)
    }
    boolean isExist(String XPATH){
        return asElement(XPATH)!=null
    }
    def clickErrorMessageOkButton(){
        asElement(XPATH_BTN_ERROR_DIALOG).click()
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
        DropdownList ddl = context.withinDropdownlist(FIELD_ID_REPORT_NAME)
        ddl.selectItem(name)
    }

    def getNameOptions() {
        return context.getNldropdownOptions(FIELD_ID_REPORT_NAME)
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
     * @author Yang.g.liu
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
