package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("yanyan.yu@oracle.com")

class VATExportTest extends VATAppTestSuite {

    /**
     * @desc Refresh with one correct transaction and one invalid transaction and export.
     * 1. Export will be enabled with correct file name.
     * 2. After export, refresh again.
     *    2.1 Exported record will disappear from page and invalid transaction will exist.
     *    2.2 Export button will be disabled.
     * @case 2.5.2-2
     * @case 1.2.3.1
     * @case 1.2.2.1.4
     * @author Yanyan Yu
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_2_5_2_2() {
        def caseFilter = testData["test VATExport 2.5.2-2"].filter
        def caseData = testData["test VATExport 2.5.2-2"].data

        // switch to admin
        //switchToRole(administrator)
        def records = record.createRecord(caseData)
        dirtyData = records

        // switch back to accountant
        //switchToRole(accountant)
        navigateToPortalPage()

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(caseFilter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(caseFilter.invoicetype)
        context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
        context.setFieldWithValue("custpage_dateto", caseFilter.dateto)

        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.export)
        Thread.sleep(10 * 1000)

        clickExport()
        Thread.sleep(10 * 1000)

        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.export)
        Thread.sleep(10 * 1000)

        assertFalse("exported record disappears", doesTransactionExistInResults(records[0].internalid))
        assertTrue("invalid record still exists", doesTransactionExistInResults(records[1].internalid))
        assertAreEqual("export button disabled", "true", asAttributeValue(locators.export, "disabled"))
    }

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATExportTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATExportTest_en_US.json"
        ]
    }

}