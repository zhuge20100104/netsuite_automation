package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.testautomation.html.parsers.TableParser
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject

/**
 * @desc BalanceSheetNormalTest
 * @author Bryan
 * @modification  Molly Feng 2018/6/8 update test_case_3_2_1_1_1_1 for running in SI env
 */
@TestOwner("molly.feng@oracle.com")
class BalanceSheetNormalTest extends BalanceSheetAppTestSuite {
    def netSuiteNumberFormat
    def MinusFormat
    @Inject
    private BalanceSheetPage bsPage

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
    /**
     * @desc Check Report Table - CN (language preference)
     * Note: custom report defined in Chinese
     * Colons(:/：) used in fixed row in custom report
     * Uppercase and lowercase used in fixed rows and columns in custom report
     * Add space at the end of item name
     * @case 3.2.1.1.1.1
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void "test_case_3_2_1_1_1_1"(){
        //get the format
        navigateToSettingPage()
        netSuiteNumberFormat = getSystemNumberFormat()
        MinusFormat = getSystemNegativeNumberFormat()

        //Common Report
        navigateToSavedReportPage(data.filter.withUnitValueThousand.reportName)
        if(context.isOneWorld()){
            asDropdownList(locator: locators.commonPageSubsidiaryField).selectItem(data.filter.withUnitValueThousand.subsidiary)
        }
        asDropdownList(locator: locators.commonPageUnitField).selectItem(data.filter.withUnitValueThousand.asof)
        asElement(locators.commonPageRefreshButton).click()
        TableParser table = new TableParser(context.webDriver);
        List<HashMap<String, String>> commonBalanceSheetTable = table.parseTable(locators.commonPageTable,null,locators.commonPageTableRows)
        Map<String,List<String>> balanceMap = getCommonReportData(commonBalanceSheetTable)

        //Customized Report
        navigateToPortalPage()
        //set up filter
        bsPage.fillBalanceSheetName(data.filter.withUnitValueThousand.reportName)
        if (context.isOneWorld()) {
            subsidiaryField().selectItem(data.filter.withUnitValueThousand.subsidiary)
        }

        asofField().selectItem(data.filter.withUnitValueThousand.asof)
        unitField().selectItem(data.filter.withUnitValueThousand.unit)
        clickRefresh()
        waitForPageToLoad()


        //validate report header
        Thread.sleep(10000)
        def header = reportHeader()
        if(header.size()!=8){
            assertTrue("Header count", false);
        }else{
            assertAreEqual("Table header of assets", trimText(header.get(0).getName()), data.test_case_3_2_1_1_1_1.expected.header.assets)
            assertAreEqual("Table header of assets line number", trimText(header.get(1).getName()), data.test_case_3_2_1_1_1_1.expected.header.assetsLines)
            assertAreEqual("Table header of closing balance for assert", trimText(header.get(2).getName()), data.test_case_3_2_1_1_1_1.expected.header.closingBalance)
            assertAreEqual("Table header of opening balance for assert", trimText(header.get(3).getName()), data.test_case_3_2_1_1_1_1.expected.header.openingBalance)
            assertAreEqual("Table header of liabilities", trimText(header.get(4).getName()), data.test_case_3_2_1_1_1_1.expected.header.liabilities)
            assertAreEqual("Table header of liabilities line number", trimText(header.get(5).getName()), data.test_case_3_2_1_1_1_1.expected.header.liabilitiesLines)
            assertAreEqual("Table header of closing balance for liabilities", trimText(header.get(6).getName()), data.test_case_3_2_1_1_1_1.expected.header.closingBalance)
            assertAreEqual("Table header of opening balance for liabilities", trimText(header.get(7).getName()), data.test_case_3_2_1_1_1_1.expected.header.openingBalance)
        }

        //validate report content
        def reportContent = reportContent()
        for( int i = 0 ; i < reportContent.size() ; i++ ) {
            def row = reportContent.get(i)
            assertAreEqual("Row " + (i+1) + " label of assets", trimText(row.get(""+0)), data.test_case_3_2_1_1_1_1.expected.rows[i].assetsLabel)
            assertAreEqual("Row " + (i+1) + " assets line number", trimText(row.get(""+1)), data.test_case_3_2_1_1_1_1.expected.rows[i].assetsLineNumber)
            if(isNeedToValidate(row,Index.ASSETS.value,Index.ASSETS_CLOSING_BALANCE.value,Index.ASSETS_OPENING_BALANCE.value)){
                assertClosingBalanceOfAssets("Row " + (i+1) + " closing balance for assert",row,balanceMap)
                assertOpeningBalanceOfAssets("Row " + (i+1) + " opening balance for assert",row,balanceMap)
            }
            assertAreEqual("Row " + (i+1) + " label of liabilities", trimText(row.get(""+4)), data.test_case_3_2_1_1_1_1.expected.rows[i].liabilitiesLabel)
            assertAreEqual("Row " + (i+1) + " liabilities line number", trimText(row.get(""+5)), data.test_case_3_2_1_1_1_1.expected.rows[i].liabilitiesLineNumber)
            if(isNeedToValidate(row,Index.LIABILITIES.value,Index.LIABILITIES_CLOSING_BALANCE.value,Index.LIABILITIES_OPENING_BALANCE.value)){
                assertClosingBalanceOfLiabilities("Row " + (i+1) + " closing balance for liabilities",row,balanceMap)
                assertOpeningBalanceOfLiabilities("Row " + (i+1) + " opening balance for liabilities",row,balanceMap)
            }
        }
    }

    def isNeedToValidate(row,labelIndex, closingBalanceIndex, openingBalanceIndex){
        if(!trimText(row.get(""+labelIndex)).equals("")){
            if(!trimText(row.get(""+closingBalanceIndex)).equals("") && !trimText(row.get(""+openingBalanceIndex)).equals("")){
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
        def labelInCommonReport = data.test_case_3_2_1_1_1_1.expected.label.get(label)
        if(labelInCommonReport !=null){
            label = labelInCommonReport
        }
        String valueString = format.removeCurrencySymbol(balanceMap.get(label.toUpperCase()).get(valueIndex))
        Double value = format.parseMoney(valueString)
        if (unitFieldIndex().equals(""+Unit.THOUSAND.value)) {
            value /= 1000
        } else if(unitFieldIndex().equals(""+Unit.TENTHOUSAND.value)){
            value /= 10000
        }
        return format.formatCurrency(value,Integer.parseInt(netSuiteNumberFormat),Integer.parseInt(MinusFormat))
    }

    /**
     * @desc Currency Value display: Yuan
     * @case 9.2.4
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void "test_case_9_2_4"() {
        navigateToPortalPage()
        //set up filter
        bsPage.fillBalanceSheetName(data.filter.nomal.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.filter.nomal.subsidiary)
        }
        asofField().selectItem(data.filter.nomal.asof)
        unitField().selectItem(data.filter.nomal.unit)
        clickRefresh()
        waitForPageToLoad()

        Thread.sleep(6000);
        assertAreEqual("Currency label", currencyLable(), data.test_case_9_2_4.expected.currency)
    }

    /**
     * @desc Check Report Table (with format)
     * Note:
     * Home>Set Perference
     * Number format:XX XXX XXX.XX /Negtive Number Format:(XX XXX XXX.XX)/Date Format: YYYY年XX月XX日
     * List>Currency
     * CURRENCY PRECISION:2
     * @case 4.1.1.1.1
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void "test_case_4_1_1_1_1"() {
        navigateToPortalPage()
        //set up filter
        bsPage.fillBalanceSheetName(data.filter.nomal.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.filter.nomal.subsidiary)
        }
        asofField().selectItem(data.filter.nomal.asof)
        unitField().selectItem(data.filter.nomal.unit)
        clickRefresh()
        waitForPageToLoad()

        Thread.sleep(6000);
        clickExportExcel()

        def downloadsPath = "data\\downloads\\".replace("\\",File.separator) + data.test_case_4_1_1_1_1.expected.fileName
        File excelFile = new File(downloadsPath)
        assertTrue("Excel downloaded",excelFile.exists())
        excelFile.delete();
    }

    /**
     * @desc Check Report Table (with format)
     * Note:
     *  Home>Set Perference
     * Number format:XX XXX XXX.XX /Negtive Number Format:(XX XXX XXX.XX)/Date Format: YYYY年XX月XX日
     *  List>Currency
     * CURRENCY PRECISION:2
     * @case 4.1.1.2.1
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void "test_case_4_1_1_2_1"() {
        navigateToPortalPage()
        //set up filter
        bsPage.fillBalanceSheetName(data.filter.nomal.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.filter.nomal.subsidiary)
        }
        asofField().selectItem(data.filter.nomal.asof)
        unitField().selectItem(data.filter.nomal.unit)
        clickRefresh()
        waitForPageToLoad()

        Thread.sleep(10000);
        clickExportPdf()
        def downloadsPath = "data\\downloads\\".replace("\\",File.separator) + data.test_case_4_1_1_2_1.expected.fileName
        File pdfFile = new File(downloadsPath)
        assertTrue("PDF downloaded",pdfFile.exists())
        pdfFile.delete();
    }

    /**
     * @desc Check Exported Excel file when Unit = Thousand
     * @case 6.2.1.2.1
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void case_6_2_1_2_1() {
        navigateToPortalPage()
        //set up filter
        bsPage.fillBalanceSheetName(data.filter.withUnitValueThousand.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.filter.withUnitValueThousand.subsidiary)
        }
        asofField().selectItem(data.filter.withUnitValueThousand.asof)
        unitField().selectItem(data.filter.withUnitValueThousand.unit)
        clickRefresh()
        waitForPageToLoad()

        Thread.sleep(10000);
        clickExportExcel()
        def downloadsPath = "data\\downloads\\".replace("\\",File.separator) +data.test_case_6_2_1_2_1.expected.fileName
        File excelFile = new File(downloadsPath)
        assertTrue("Excel downloaded",excelFile.exists())
        excelFile.delete();
    }

    /**
     * @desc Check Exported PDF file when Unit = Thousand
     * @case 6.2.1.2.2
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void "test_case_6_2_1_2_2"() {
        navigateToPortalPage()
        bsPage.fillBalanceSheetName(data.filter.withUnitValueThousand.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.filter.withUnitValueThousand.subsidiary)
        }
        asofField().selectItem(data.filter.withUnitValueThousand.asof)
        unitField().selectItem(data.filter.withUnitValueThousand.unit)
        clickRefresh()
        waitForPageToLoad()

        Thread.sleep(10000);
        clickExportPdf()
        def downloadsPath = "data\\downloads\\".replace("\\",File.separator) + data.test_case_6_2_1_2_2.expected.fileName
        File pdfFile = new File(downloadsPath)
        assertTrue("PDF downloaded",pdfFile.exists())
        pdfFile.delete();
    }

    def getCommonReportData(List<HashMap<String, String>> commonReportData){
        Map<String,List<String>> balanceMap = new HashMap<String,List<String>>();
        for( int i = 0 ; i < commonReportData.size() ; i++ ) {
            List<String> balance = new ArrayList<String>()
            HashMap<String, String> row = commonReportData.get(i)
            String label = trimText(row.get("0"))
            String closingBalance = trimText(row.get("1"))
            String openingBalance = trimText(row.get("2"))
            balance.add(closingBalance)
            balance.add(openingBalance)
            //toUpperCase
            balanceMap.put(label.toUpperCase(),balance)
        }
        return balanceMap
    }
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BalanceSheetNormalTest_data_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BalanceSheetNormalTest_data_en_US.json"
        ]
    }
}