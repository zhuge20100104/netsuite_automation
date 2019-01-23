package com.netsuite.chinalocalization.balancesheet

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.lib.NFormat
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.testautomation.html.parsers.TableParser
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Assert
import org.junit.Before

/**
 * @desc Base test suite implementation for VAT
 * @author Jianwei Liu
 * @modification  Molly Feng 2018/6/8 navigate to saved report by report id
 */
class BalanceSheetAppTestSuite extends BaseAppTestSuite {


    @Inject
    BalanceSheetLocators locators


   // def roles = [zhCN: null, enUS: null]
    enum Unit {
        THOUSAND(1),TENTHOUSAND(2)
        Unit(int value) {
            this.value = value
        }
        private final int value
        int getValue() {
            value
        }
    }
    enum Index {
        ASSETS(0),ASSETS_LINE_NO(1),ASSETS_CLOSING_BALANCE(2),ASSETS_OPENING_BALANCE(3),LIABILITIES(4),LIABILITIES_LINE_NO(5),LIABILITIES_CLOSING_BALANCE(6),LIABILITIES_OPENING_BALANCE(7)
        Index(int value) {
            this.value = value
        }
        private final int value
        int getValue() {
            value
        }
    }

    enum ValueIndex {
        CLOSING_BALANCE(0),OPENING_BALANCE(1)
        ValueIndex(int value) {
            this.value = value
        }
        private final int value
        int getValue() {
            value
        }
    }


    @Before
    void setUp() {
        super.setUp()
        //if(!data){
            def path = pathToTestDataFiles()
            if (path) {
                if (isTargetLanguageChinese()) {
                    if (doesFileExist(path.zhCN)) {
                        data = context.asJSON(path: path.zhCN)
                    }
                } else {
                    if (doesFileExist(path.enUS)) {
                        data = context.asJSON(path: path.enUS)
                    }
                }
            }

        //}

        // set report by period = financial only for bs
        if(context.getPreference("REPORTBYPERIOD") != "FINANCIALS" ){
            userPrePage.navigateToURL()
            context.setFieldWithValue("REPORTBYPERIOD", "FINANCIALS" )
            userPrePage.clickSave()
        }

        navigateToSettingPage()

    }

    def pathToTestDataFiles() {}
    def switchWindow() {
        context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
        def  currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
    }

    @After
    void tearDown() {
        println("tearDown: cleaning up dirty data...")
        switchWindow()
    }

    def subsidiaryField(){
        return asDropdownList(locator: locators.subsidiaryField)
    }

    def asofField(){
        return asDropdownList(locator: locators.asofField)
    }

    def unitField(){
        return asDropdownList(locator: locators.unitField)
    }

    def unitFieldIndex(){
        return asElement(locators.unitFieldIndex).getAttributeValue("value")
    }

    def alertMessage() {
        return asText(locators.alertMessage)
    }

    def okButtonInAlertMessage() {
        return asElement(locators.okButtonInAlertMessage)
    }

    def reportHeader() {
        TableParser table = new TableParser(context.webDriver);
        return table.getTableHeader(locators.tableXPath,locators.headerItemsIteratorXPath)
    }

    def reportContent() {
        TableParser table = new TableParser(context.webDriver);
        return table.parseTable(locators.tableXPath,null,locators.rowsIteratorXPath)
    }

    def currencyLable(){
        return asText(locators.currencyLable)
    }

    def String trimText(text) {
        return text.trim().replaceAll("[\\u00A0]+", "")
    }

    def excelButton(){
        return asElement(locators.excelButton)
    }

    def pdfButton(){
        return asElement(locators.pdfButton)
    }

    def navigateToSettingPage(){
        context.navigateTo("/app/center/userprefs.nl")
    }

    def clickExportExcel(){
        excelButton().click()
    }

    def clickExportPdf(){
        pdfButton().click()
    }

    def navigateToSavedReportPage(reportName){
        // navigate to saved report by report id
        String id = context.getSavedReportId(reportName)
        Assert.assertNotNull(reportName + " does not exist", id)
        context.navigateTo("/app/reporting/reportrunner.nl?cr=" + id + "&reload=T&whence=")
    }

    def navigateToPortalPage() {
        context.navigateTo(resolveSuiteletURL("customscript_sl_cn_blsheet", "customdeploy_sl_cn_blsheet"))
    }

    def clickRefresh() {
        asClick(locators.clickRefresh)
    }

    def getSystemNumberFormat(){
        return asElement(locators.systemNumberFormat).getAttributeValue("value")
    }

    def getSystemNegativeNumberFormat(){
        return asElement(locators.systemNegativeNumberFormat).getAttributeValue("value")
    }

    def getCaseObj(String filePath,String caseId){
        String fileString = new File(filePath).getText('UTF-8')
        def jsonSlurper = new JsonSlurper()
        def fileObj = jsonSlurper.parseText(fileString)
        def caseObj=fileObj[caseId]
        return caseObj
    }


    def verifyReportHeader(def testData) {
        def header = reportHeader()
        if (header.size() != 8) {
            assertTrue("Header count", false);
        } else {
            assertAreEqual("Table header of assets", trimText(header.get(0).getName()), testData.header.assets)
            assertAreEqual("Table header of assets line number", trimText(header.get(1).getName()), testData.header.assetsLines)
            assertAreEqual("Table header of closing balance for assert", trimText(header.get(2).getName()), testData.header.closingBalance)
            assertAreEqual("Table header of opening balance for assert", trimText(header.get(3).getName()), testData.header.openingBalance)
            assertAreEqual("Table header of liabilities", trimText(header.get(4).getName()), testData.header.liabilities)
            assertAreEqual("Table header of liabilities line number", trimText(header.get(5).getName()), testData.header.liabilitiesLines)
            assertAreEqual("Table header of closing balance for liabilities", trimText(header.get(6).getName()),testData.header.closingBalance)
            assertAreEqual("Table header of opening balance for liabilities", trimText(header.get(7).getName()), testData.header.openingBalance)
        }
    }

    def verifyReportContent(def reportContent, def savedReportData, def expectedData) {
        for (int i = 0; i < reportContent.size(); i++) {
            def row = reportContent.get(i)

            //verify report content
            assertAreEqual("Row " + (i + 1) + " label of assets", trimText(row.get("" + 0)), expectedData.rows[i].assetsLabel)
            assertAreEqual("Row " + (i + 1) + " assets line number", trimText(row.get("" + 1)), expectedData.rows[i].assetsLineNumber)
            if (isNeedToValidate(row, Index.ASSETS.value, Index.ASSETS_CLOSING_BALANCE.value, Index.ASSETS_OPENING_BALANCE.value)) {
                assertClosingBalanceOfAssets("Row " + (i + 1) + " closing balance for assert", row, savedReportData)
                assertOpeningBalanceOfAssets("Row " + (i + 1) + " opening balance for assert", row, savedReportData)
            }
            assertAreEqual("Row " + (i + 1) + " label of liabilities", trimText(row.get("" + 4)), expectedData.rows[i].liabilitiesLabel)
            assertAreEqual("Row " + (i + 1) + " liabilities line number", trimText(row.get("" + 5)), expectedData.rows[i].liabilitiesLineNumber)
            if (isNeedToValidate(row, Index.LIABILITIES.value, Index.LIABILITIES_CLOSING_BALANCE.value, Index.LIABILITIES_OPENING_BALANCE.value)) {
                assertClosingBalanceOfLiabilities("Row " + (i + 1) + " closing balance for liabilities", row, savedReportData)
                assertOpeningBalanceOfLiabilities("Row " + (i + 1) + " opening balance for liabilities", row, savedReportData)
            }
        }
    }
    def isNeedToValidate(row,labelIndex, closingBalanceIndex, openingBalanceIndex){
        if(!trimText(row.get(""+labelIndex)).isEmpty()){
            if(!trimText(row.get(""+closingBalanceIndex)).isEmpty() && !trimText(row.get(""+openingBalanceIndex)).isEmpty()){
                return true;
            }
        }
        return false;
    }

    def assertClosingBalanceOfAssets(message, row, balanceMap){
        def value = getValueOfCommonReport(row,Index.ASSETS.value,balanceMap,ValueIndex.CLOSING_BALANCE.value)
        assertAreEqual(message, trimText(row.get(""+Index.ASSETS_CLOSING_BALANCE.value)),value)
    }

    def assertOpeningBalanceOfAssets(message, row, balanceMap){
        def value = getValueOfCommonReport(row,Index.ASSETS.value,balanceMap,ValueIndex.OPENING_BALANCE.value)
        assertAreEqual(message, trimText(row.get(""+Index.ASSETS_OPENING_BALANCE.value)),value)
    }

    def assertClosingBalanceOfLiabilities(message, row, balanceMap){
        def value = getValueOfCommonReport(row,Index.LIABILITIES.value,balanceMap,ValueIndex.CLOSING_BALANCE.value)
        assertAreEqual(message, trimText(row.get(""+Index.LIABILITIES_CLOSING_BALANCE.value)),value)
    }

    def assertOpeningBalanceOfLiabilities(message, row, balanceMap){
        def value = getValueOfCommonReport(row,Index.LIABILITIES.value,balanceMap,ValueIndex.OPENING_BALANCE.value)
        assertAreEqual(message, trimText(row.get(""+Index.LIABILITIES_OPENING_BALANCE.value)),value)
    }

    def getValueOfCommonReport(row,labelIndex,balanceMap,valueIndex){
        def label = trimText(row.get(""+labelIndex))
        //change the Chinese character to English character
        label = format.formatCharacter(label)
        String valueString = format.removeCurrencySymbol(balanceMap.get(label.toUpperCase()).get(valueIndex))
       // Double value = format.parseMoney(valueString)
        if (unitFieldIndex().equals(""+Unit.THOUSAND.value)) {
            valueString /= 1000
        } else if(unitFieldIndex().equals(""+Unit.TENTHOUSAND.value)){
            valueString /= 10000
        }
        return valueString
        //return format.formatCurrency(value,Integer.parseInt(netSuiteNumberFormat),Integer.parseInt(MinusFormat))
    }
}