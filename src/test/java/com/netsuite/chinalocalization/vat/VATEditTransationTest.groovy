package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P3
import org.junit.experimental.categories.Category
import com.netsuite.testautomation.junit.TestOwner

import org.junit.Test
import org.junit.Rule
import org.junit.rules.TestName
import org.junit.rules.Timeout


@TestOwner("christina.chen@oracle.com")
class VATEditTransationTest extends VATEditPageTestSuite {
    def caseData
    def caseFilter
    def expResult
    @Rule
    public  TestName name = new TestName()

    @Rule
    public Timeout globalTimeout= new Timeout(20*60 *1000)
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATEditTransationTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATEditTransationTest_en_US.json"
        ]
    }

    def init(){
        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        cleanAll = false
        if (caseData.containsKey("expectedResult")){expResult = caseData.expectedResult}
        /*switchToRole(administrator)
        dirtyData = record.createRecord(caseData.data)
        switchToRole(accountant)
        */
        navigateToPortalPage()
        waitForPageToLoad()
    }
    /**
     * @desc Test case check before refresh no edit button, export button
     *  after press the refresh button can see eidt button  export button in UI
     *  since no data  edit button and export button disabled
     * @case 19.0.1
     * @case 19.0.2
     * @author Christina Chen
     * @No-regression
     * @Non-regression: duplicate case
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_19_0_1() {
        navigateToPortalPage()
        waitForPageToLoad()
        Thread.sleep(4 * 1000)
        caseData = testData.get(name.getMethodName())
        assertAreEqual("Refresh button enabled",  asAttributeValue(locators.refresh, "disabled"), null)
        assertAreEqual("Import button enabled",  asAttributeValue(locators.importButton, "disabled"),null)
        assertFalse("Export Button not present", context.doesFieldExist("custpage_export"))
        assertFalse("Edit Button not present", context.doesFieldExist("custpage_edit"))
        //       .isFieldDisplayed(locators.edit))
        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(editData.filter.subsidiary)
        }
        asDropdownList(locator: locators.invoiceType).selectItem(editData.filter.invoicetype_c)
        context.setFieldWithValue("custpage_datefrom", caseData.filter.datefrom)
        context.setFieldWithValue("custpage_dateto", caseData.filter.dateto)

        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.export)
        Thread.sleep(3 * 1000)
        //need check  noDataExport :"No Search Results Match Your Criteria."
        assertTrue("No data ", context.isTextVisible(caseData.expectedResults.noDataExport))
        assertAreEqual("edit button disabled", "true", asAttributeValue(locators.edit, "disabled"))
        assertAreEqual("export button disabled", "true", asAttributeValue(locators.export, "disabled"))
    }
    /**
     * @desc Test case check click edit button will go to a new page and can see loading message
     * @case 1.2.3.2
     * @case 19.0.3
     * @author Christina Chen
     * @Non-regression: duplicate case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_0_3() {
        caseData = testData.get(name.getMethodName())
        navigateToPortalPage()
        assertFalse("Edit Button not present", context.doesFieldExist("custpage_edit"))
        setQueryParams( name.getMethodName())
        clickRefresh()
        Thread.sleep(2 * 1000)
        //assertTrue("Show loading message", context.isTextVisible(editData.expectedResults.loadingMsg))
        //assertAreEqual("Refresh button disabled", "true", asAttributeValue(locators.refresh, "disabled"))
        waitForPageToLoad()
        waitForElement(locators.export)
        Thread.sleep(3 * 1000)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        assertAreEqual("Edit button enabled", null, asAttributeValue(locators.edit, "disabled"))
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        assertTrue("Merge Button present", context.doesFieldExist("merge"))

    }
    /**
     * @desc Test case merge tow no a sale list
     * @case 19.2.1.1
     * @case 19.2.3.1
     * @author Christina Chen
     * @Non-regression: duplicate case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_2_1_1() {
        init()
        setQueryParams( name.getMethodName())
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        Thread.sleep(3 * 1000)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))

        dirtyData =previewData =updatePreResults(caseData.previewData)
        cleanAll =false
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        assertTrue("Merge Button present", context.doesFieldExist("merge"))
        assertTrue("UnMerge Button present", context.doesFieldExist("unmerge"))
        assertTrue("Cancle Button present", context.doesFieldExist("custpage_cancel"))
        //prepare merge
        int mergedLineNum =prepareMerge()
        clickMerge()
        Thread.sleep(5 * 1000)
       // Check merged result
        checkMergedResult(mergedLineNum,false)
        clickSave()
        waitForElement(locators.export)
        Thread.sleep(5 * 1000)
        checkMergedPreview( false)
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        waitForElement(editLocators.unmergeButton)
        prepareUnMerge(mergedLineNum, false)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkUnMergedResult(mergedLineNum)
        clickSave()
        waitForElement(locators.export)
        checkUnMergedPreview()
    }
    /*
     * @desc Test case merge no sale list
     * @case 19.2.1.2
     * @author Christina Chen
     * @Non-regression: duplicate case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_2_1_2() {
        init()
        //navigateToPortalPage()
        //waitForPageToLoad()
        setQueryParams(name.getMethodName())
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        Thread.sleep(5 * 1000)
        previewData =updatePreResults(caseData.previewData)

        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)

        Thread.sleep(2 * 1000)
        assertTrue("Merge Button present", context.doesFieldExist("merge"))
        assertTrue("UnMerge Button present", context.doesFieldExist("unmerge"))
        assertTrue("Cancle Button present", context.doesFieldExist("custpage_cancel"))
        int mergedLineNum =prepareMerge()
        clickMerge()
        Thread.sleep(5 * 1000)
        checkMergedResult(mergedLineNum, false)
        prepareUnMerge(mergedLineNum, false)
        Thread.sleep(5 * 1000)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        clickCancel()
        // accept warning
        clickConfirmOk()

    }

    /**
     * @desc Test case merge with sale list
     * @case 19.2.1.3
     * @case 19.2.3.3
     * @author Christina Chen
     * @Non-regression: duplicate case
     */
    @Category([P1.class, OWAndSI.class ])
    @Test
    void test_case_19_2_1_3() {
        //create a  tra'n with sale list
        //create a tran with disaccout as sale list
        init()
        //navigateToPortalPage()
        setQueryParams(name.getMethodName())
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        Thread.sleep(3 * 1000)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        dirtyData =previewData =updatePreResults(caseData.previewData)
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        assertTrue("Merge Button present", context.doesFieldExist("merge"))
        assertTrue("UnMerge Button present", context.doesFieldExist("unmerge"))
        assertTrue("Cancle Button present", context.doesFieldExist("custpage_cancel"))
        //prepare merge
        int mergedLineNum =prepareMerge([])
        clickMerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkMergedResult(mergedLineNum,true)
        clickSave()
        waitForElement(locators.export)
        checkMergedPreview( true)
        Thread.sleep(5 * 1000)
        clickExport()
        exportAndClear()
        navigateToPortalPage()
        setQueryParams("test_case_19_2_1_3")
        clickRefresh()
        waitForElement(locators.edit)
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        //unmerged
        prepareUnMerge(mergedLineNum, true)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkUnMergedResult(mergedLineNum)
        clickSave()
        waitForElement(locators.export)
        checkUnMergedPreview()
    }

    /**
     * @desc Test case merge tow tran with discount but  lines <4
     * @case 19.2.1.4
     * @author Christina Chen
     * @Non-regression: duplicate case
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_19_2_1_4() {
        init()
        setQueryParams(name.getMethodName())
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        Thread.sleep(2 * 1000)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        dirtyData =previewData =updatePreResults(caseData.previewData)
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)

        assertTrue("Merge Button present", context.doesFieldExist("merge"))
        assertTrue("UnMerge Button present", context.doesFieldExist("unmerge"))
        assertTrue("Cancle Button present", context.doesFieldExist("custpage_cancel"))
        int mergedLineNum =prepareMerge([])
        clickMerge()
        Thread.sleep(5 * 1000)
        checkMergedResult(mergedLineNum,false)
        clickSave()

        waitForElement(locators.export)
        checkMergedPreview(false)

        Thread.sleep(5 * 1000)
        clickExport()
        exportAndClear()
        navigateToPortalPage()
        setQueryParams("test_case_19_2_1_4")
        clickRefresh()

        waitForElement(locators.edit)
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        //unmerged
        prepareUnMerge(mergedLineNum, false)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkUnMergedResult(mergedLineNum)
        clickSave()
        waitForElement(locators.export)
/*        checkUnMergedPreview()
        Thread.sleep(5 * 1000)
        clickExport()
        exportAndClear()*/
    }

    /**
     * @desc Test case merge tow tran with discount to generate a sale list
     * @case 19.2.1.5
     * @case 19.2.3.3
     * @case 19.2.4.1
     * @case 19.2.4.2
     * @case 19.2.6.2
     * @author Christina Chen
     */
    @Category([P3.class,OWAndSI.class])
    @Test
    void test_case_19_2_1_5() {

        init()
        navigateToPortalPage()
        setQueryParams(name.getMethodName())
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        Thread.sleep(4 * 1000)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        dirtyData =previewData =updatePreResults(caseData.previewData)
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        assertTrue("Merge Button present", context.doesFieldExist("merge"))
        assertTrue("UnMerge Button present", context.doesFieldExist("unmerge"))
        assertTrue("Cancle Button present", context.doesFieldExist("custpage_cancel"))
        //prepare merge
        int mergedLineNum =prepareMerge([])
        clickMerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkMergedResult(mergedLineNum,true)
        clickSave()

        waitForElement(locators.export)
        checkMergedPreview( true)

/*        Thread.sleep(5 * 1000)
        clickExport()
        exportAndClear()
        navigateToPortalPage()
        Thread.sleep(5 * 1000)
        setQueryParams("test_case_19_2_1_5",
            [editData.tranType.CustInvc, editData.tranType.CashSale],
            editData.filter.invoicetype_s,
            editData.saleslist.no)
*/
        clickRefresh()

        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        //unmerged
        prepareUnMerge(mergedLineNum, true)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkUnMergedResult(mergedLineNum)
/*        clickSave()
        waitForElement(locators.export)
        Thread.sleep(5 * 1000)
        checkUnMergedPreview()
        clickExport()
        exportAndClear()*/
     }


    /**
     * @desc Test case check click cancel button
     * @case 19.2.5
     * @author Christina Chen
     * @Non-regression: duplicate case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_2_5() {

        navigateToPortalPage()
        caseData = testData.get(name.getMethodName())
        assertFalse("Edit Button not present", context.doesFieldExist("custpage_edit"))
        setQueryParams(name.getMethodName())
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.export)
        Thread.sleep(3 * 1000)
        assertAreEqual("Edit button enabled", null, asAttributeValue(locators.edit, "disabled"))
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        assertTrue("Merge Button present", context.doesFieldExist("merge"))
        assertTrue("UnMerge Button present", context.doesFieldExist("unmerge"))
        assertTrue("Cancle Button present", context.doesFieldExist("custpage_cancel"))

        asClickTransCheckbox(1)
        asClickTransCheckbox(2)

        clickMerge()
        Thread.sleep(5 * 1000)
        clickCancel()
        // check present
        assertTrue("Cancel Warning dialog present", context.isPopUpPresent())
        // check title
        assertTrue("Title expected", context.isTextVisible(caseData.expectedResults.warningDialog.title))
        // check warning message
        assertTrue("Warning message expected", context.isTextVisible(caseData.expectedResults.warningDialog.message))
        // accept warning
        clickConfirmCancel()
        clickCancel()
        assertTrue("Cancel Warning dialog present", context.isPopUpPresent())
        // check title
        assertTrue("Title expected", context.isTextVisible(caseData.expectedResults.warningDialog.title))
        // check warning message
        assertTrue("Warning message expected", context.isTextVisible(caseData.expectedResults.warningDialog.message))
        // accept warning
        clickConfirmOk()

        assertTrue("Current page expected", context.isTextVisible(caseData.expectedResults.title))
    }

    /**
     * @desc check sale list
     * @case 19.3.17
     * @author Christina Chen
     * @Non-regression: duplicate case
     */
    @Category([P1.class,OWAndSI.class])

    @Test
    void test_case_19_3_17() {

        navigateToPortalPage()
        caseData = testData.get(name.getMethodName())
        assertFalse("Edit Button present", context.doesFieldExist("custpage_edit"))
        setQueryParams(name.getMethodName())
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        Thread.sleep(3 * 1000)
        assertAreEqual("Edit button enabled", null, asAttributeValue(locators.edit, "disabled"))
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        Thread.sleep(2 * 1000)
        headerHelper("selectLine", 1)
        def main = caseData.data.get(0).main
        def item = caseData.data.get(0).item

        headerlabels.each{ key,headlabel ->
            //internalId is changable in every new create record so not check it
            assertAreEqual( "${headlabel} exist", headlabel, headerHelper("getSublistLabel",key,1))
            if ( !(key in["select","internalId"])) {
                value = headerTextInColumnOfRow(key, 1)
                expect_value = main.get(key)
                assertAreEqual( "${headlabel} expected is ${expect_value}", value, expect_value)
            }
        }
        itemlabels.each{ key, headlabel ->
            assertAreEqual( "${headlabel} exist", headlabel, itemHelper("getSublistLabel",key,1))
            def value = itemTextInColumnOfRow(key, 1)
            def expect_value = item.get(key)
            assertAreEqual("${headlabel} expected is ${expect_value}", value, expect_value)
        }
    }
}