package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.balancesheet.BalanceSheetAppTestSuite
import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.testautomation.html.parsers.DTOs.ListRow
import com.netsuite.testautomation.html.parsers.TableParser
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject

@TestOwner("molly.feng@oracle.com")
class BalanceSheetInvalidTemplateTest extends BalanceSheetAppTestSuite {
    @Inject
    private BalanceSheetPage bsPage
    /**
     * @desc Check Report Table - no specific language
     * Note: All fixed rows not defined properly.
     * @case 3.2.2.1.1.1
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void "test_case_3_2_2_1_1_1"(){
        navigateToPortalPage()
        //set up filter

        bsPage.fillBalanceSheetName(data.test_case_3_2_2_1_1_1.filter.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.test_case_3_2_2_1_1_1.filter.subsidiary)
        }
        asofField().selectItem(data.test_case_3_2_2_1_1_1.filter.asof)
        unitField().selectItem(data.test_case_3_2_2_1_1_1.filter.unit)
        clickRefresh()

        //validate alert message
        Thread.sleep(10000)
        assertAreEqual("Alert message", alertMessage() , data.test_case_3_2_2_1_1_1.expected.alertMessage)
        okButtonInAlertMessage().click()

        //validate report header
        def header = reportHeader()
        if(header.size()!=8){
            assertTrue("Header count", false);
        }else{
            assertAreEqual("Table header of assets", trimText(header.get(0).getName()), data.test_case_3_2_2_1_1_1.expected.header.assets)
            assertAreEqual("Table header of assets line number", trimText(header.get(1).getName()), data.test_case_3_2_2_1_1_1.expected.header.assetsLines)
            assertAreEqual("Table header of closing balance for assert", trimText(header.get(2).getName()), data.test_case_3_2_2_1_1_1.expected.header.closingBalance)
            assertAreEqual("Table header of opening balance for assert", trimText(header.get(3).getName()), data.test_case_3_2_2_1_1_1.expected.header.openingBalance)
            assertAreEqual("Table header of liabilities", trimText(header.get(4).getName()), data.test_case_3_2_2_1_1_1.expected.header.liabilities)
            assertAreEqual("Table header of liabilities line number", trimText(header.get(5).getName()), data.test_case_3_2_2_1_1_1.expected.header.liabilitiesLines)
            assertAreEqual("Table header of closing balance for liabilities", trimText(header.get(6).getName()), data.test_case_3_2_2_1_1_1.expected.header.closingBalance)
            assertAreEqual("Table header of opening balance for liabilities", trimText(header.get(7).getName()), data.test_case_3_2_2_1_1_1.expected.header.openingBalance)
        }

        //validate report content
        def reportContent = reportContent()
        for( int i = 0 ; i < reportContent.size() ; i++ ) {
            def row = reportContent.get(i)
            assertAreEqual("Row " + (i+1) + " label of assets", trimText(row.get(""+0)), data.test_case_3_2_2_1_1_1.expected.rows[i].assetsLabel)
            assertAreEqual("Row " + (i+1) + " assets line number", trimText(row.get(""+1)), data.test_case_3_2_2_1_1_1.expected.rows[i].assetsLineNumber)
            assertAreEqual("Row " + (i+1) + " closing balance for assert", trimText(row.get(""+2)), "")
            assertAreEqual("Row " + (i+1) + " opening balance for assert", trimText(row.get(""+3)), "")
            assertAreEqual("Row " + (i+1) + " label of liabilities", trimText(row.get(""+4)), data.test_case_3_2_2_1_1_1.expected.rows[i].liabilitiesLabel)
            assertAreEqual("Row " + (i+1) + " liabilities line number", trimText(row.get(""+5)), data.test_case_3_2_2_1_1_1.expected.rows[i].liabilitiesLineNumber)
            assertAreEqual("Row " + (i+1) + " closing balance for liabilities", trimText(row.get(""+6)), "")
            assertAreEqual("Row " + (i+1) + " opening balance for liabilities", trimText(row.get(""+7)), "")
        }
    }

    /**
     * @desc Check Report Table - no specific language
     * Note: All fixed columns not defined properly.
     * Add other columns in custom report
     * @case 3.2.2.3.1.1
     * @author Bryan Chen
     */
    @Test
    @Category([OWAndSI.class, P1.class])
    void "test_case_3_2_2_3_1_1"(){
        navigateToPortalPage()
        //set up filter
        bsPage.fillBalanceSheetName(data.test_case_3_2_2_3_1_1.filter.reportName)
        if(context.isOneWorld()){
            subsidiaryField().selectItem(data.test_case_3_2_2_3_1_1.filter.subsidiary)
        }
        asofField().selectItem(data.test_case_3_2_2_3_1_1.filter.asof)
        unitField().selectItem(data.test_case_3_2_2_3_1_1.filter.unit)
        clickRefresh()

        //validate alert message
        Thread.sleep(10000)
        assertAreEqual("Alert message", alertMessage() , data.test_case_3_2_2_3_1_1.expected.alertMessage)
        okButtonInAlertMessage().click()

        //validate report header
        def header = reportHeader()
        if(header.size()!=8){
            assertTrue("Header count", false);
        }else{
            assertAreEqual("Table header of assets", trimText(header.get(0).getName()), data.test_case_3_2_2_3_1_1.expected.header.assets)
            assertAreEqual("Table header of assets line number", trimText(header.get(1).getName()), data.test_case_3_2_2_3_1_1.expected.header.assetsLines)
            assertAreEqual("Table header of closing balance for assert", trimText(header.get(2).getName()), data.test_case_3_2_2_3_1_1.expected.header.closingBalance)
            assertAreEqual("Table header of opening balance for assert", trimText(header.get(3).getName()), data.test_case_3_2_2_3_1_1.expected.header.openingBalance)
            assertAreEqual("Table header of liabilities", trimText(header.get(4).getName()), data.test_case_3_2_2_3_1_1.expected.header.liabilities)
            assertAreEqual("Table header of liabilities line number", trimText(header.get(5).getName()), data.test_case_3_2_2_3_1_1.expected.header.liabilitiesLines)
            assertAreEqual("Table header of closing balance for liabilities", trimText(header.get(6).getName()), data.test_case_3_2_2_3_1_1.expected.header.closingBalance)
            assertAreEqual("Table header of opening balance for liabilities", trimText(header.get(7).getName()), data.test_case_3_2_2_3_1_1.expected.header.openingBalance)
        }

        //validate report content
        def reportContent = reportContent()
        for( int i = 0 ; i < reportContent.size() ; i++ ) {
            def row = reportContent.get(i)
            assertAreEqual("Row " + (i+1) + " label of assets", trimText(row.get(""+0)), data.test_case_3_2_2_3_1_1.expected.rows[i].assetsLabel)
            assertAreEqual("Row " + (i+1) + " assets line number", trimText(row.get(""+1)), data.test_case_3_2_2_3_1_1.expected.rows[i].assetsLineNumber)
            assertAreEqual("Row " + (i+1) + " closing balance for assert", trimText(row.get(""+2)), "")
            assertAreEqual("Row " + (i+1) + " opening balance for assert", trimText(row.get(""+3)), "")
            assertAreEqual("Row " + (i+1) + " label of liabilities", trimText(row.get(""+4)), data.test_case_3_2_2_3_1_1.expected.rows[i].liabilitiesLabel)
            assertAreEqual("Row " + (i+1) + " liabilities line number", trimText(row.get(""+5)), data.test_case_3_2_2_3_1_1.expected.rows[i].liabilitiesLineNumber)
            assertAreEqual("Row " + (i+1) + " closing balance for liabilities", trimText(row.get(""+6)), "")
            assertAreEqual("Row " + (i+1) + " opening balance for liabilities", trimText(row.get(""+7)), "")
        }
    }

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BalanceSheetInvalidTemplateTest_data_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BalanceSheetInvalidTemplateTest_data_en_US.json"
        ]
    }
}