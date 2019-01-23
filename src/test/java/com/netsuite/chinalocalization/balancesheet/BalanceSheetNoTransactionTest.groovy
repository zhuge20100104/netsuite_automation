package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.html.parsers.DTOs.ListRow
import com.netsuite.testautomation.html.parsers.TableParser
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject

@TestOwner("molly.feng@oracle.com")
class BalanceSheetNoTransactionTest extends BalanceSheetAppTestSuite {
    @Inject
    private BalanceSheetPage bsPage
    /**
     * @desc check Report Table - no specific language
     * @case 3.3.1.1
     * @author Bryan Chen
     */
    @Test
    @Category([OW.class, P2.class])
    void "test_case_3_3_1_1"(){

        navigateToPortalPage()
        //set up filter
        bsPage.fillBalanceSheetName(data.test_case_3_3_1_1.filter.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.test_case_3_3_1_1.filter.subsidiary)
        }
        asofField().selectItem(data.test_case_3_3_1_1.filter.asof)
        unitField().selectItem(data.test_case_3_3_1_1.filter.unit)
        clickRefresh()
        waitForPageToLoad()

        Thread.sleep(10000);
        //validate report header
        def header = reportHeader()
        if(header.size()!=8){
            assertTrue("Header count", false);
        }else{
            assertAreEqual("Table header of assets", trimText(header.get(0).getName()), data.test_case_3_3_1_1.expected.header.assets)
            assertAreEqual("Table header of assets line number", trimText(header.get(1).getName()), data.test_case_3_3_1_1.expected.header.assetsLines)
            assertAreEqual("Table header of closing balance for assert", trimText(header.get(2).getName()), data.test_case_3_3_1_1.expected.header.closingBalance)
            assertAreEqual("Table header of opening balance for assert", trimText(header.get(3).getName()), data.test_case_3_3_1_1.expected.header.openingBalance)
            assertAreEqual("Table header of liabilities", trimText(header.get(4).getName()), data.test_case_3_3_1_1.expected.header.liabilities)
            assertAreEqual("Table header of liabilities line number", trimText(header.get(5).getName()), data.test_case_3_3_1_1.expected.header.liabilitiesLines)
            assertAreEqual("Table header of closing balance for liabilities", trimText(header.get(6).getName()), data.test_case_3_3_1_1.expected.header.closingBalance)
            assertAreEqual("Table header of opening balance for liabilities", trimText(header.get(7).getName()), data.test_case_3_3_1_1.expected.header.openingBalance)
        }

        //validate report content
        def reportContent = reportContent()
        for( int i = 0 ; i < reportContent.size() ; i++ ) {
            def row = reportContent.get(i)
            assertAreEqual("Row " + (i+1) + " label of assets", trimText(row.get(""+0)), data.test_case_3_3_1_1.expected.rows[i].assetsLabel)
            assertAreEqual("Row " + (i+1) + " assets line number", trimText(row.get(""+1)), data.test_case_3_3_1_1.expected.rows[i].assetsLineNumber)
            assertAreEqual("Row " + (i+1) + " closing balance for assert", trimText(row.get(""+2)), data.test_case_3_3_1_1.expected.rows[i].assetsClosingBalance)
            assertAreEqual("Row " + (i+1) + " opening balance for assert", trimText(row.get(""+3)), data.test_case_3_3_1_1.expected.rows[i].assetsOpeningBalance)
            assertAreEqual("Row " + (i+1) + " label of liabilities", trimText(row.get(""+4)), data.test_case_3_3_1_1.expected.rows[i].liabilitiesLabel)
            assertAreEqual("Row " + (i+1) + " liabilities line number", trimText(row.get(""+5)), data.test_case_3_3_1_1.expected.rows[i].liabilitiesLineNumber)
            assertAreEqual("Row " + (i+1) + " closing balance for liabilities", trimText(row.get(""+6)), data.test_case_3_3_1_1.expected.rows[i].liabilitiesClosingBalance)
            assertAreEqual("Row " + (i+1) + " opening balance for liabilities", trimText(row.get(""+7)), data.test_case_3_3_1_1.expected.rows[i].liabilitiesOpeningBalance)
        }
    }

    /**
     * @desc Currency Value display: US Dollar
     * @case 9.2.3
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void "test_case_9_2_3"() {
        navigateToPortalPage()
        //set up filter
        bsPage.fillBalanceSheetName(data.test_case_3_3_1_1.filter.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.test_case_3_3_1_1.filter.subsidiary)
        }
        asofField().selectItem(data.test_case_3_3_1_1.filter.asof)
        unitField().selectItem(data.test_case_3_3_1_1.filter.unit)
        clickRefresh()
        waitForPageToLoad()

        Thread.sleep(3000);
        if (context.isOneWorld()) {
            assertAreEqual("Currency label", currencyLable(), data.test_case_9_2_3.expected.currency)
        } else {
            assertAreEqual("Currency label", currencyLable(), data.test_case_9_2_3.expected.currencySI)
        }
        
        switchWindow()
    }
    def switchWindow() {
        context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
        def  currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
    }
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BalanceSheetNoTransactionTest_data_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BalanceSheetNoTransactionTest_data_en_US.json"
        ]
    }

}