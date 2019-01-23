package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner ("zhen.t.tang@oracle.com")
class VATZeroItemLineDisplayTest extends VATEditPageTestSuite{
    @Rule
    public  TestName name = new TestName()
    def caseData
    def caseFilter

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATZeroItemLineDisplay_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATZeroItemLineDisplay_en_US.json"
        ]
    }
    def caseDataInit(String caseNum){
        caseData = testData[caseNum]
        caseFilter = testData[caseNum].filter
        cleanAll = false

        //insertData(caseData)
        navigateToPortalPage()
        waitForPageToLoad()
        setFilter(caseFilter)
        clickRefresh()
        waitForPageToLoad()
        waitForElement(locators.edit)
        Thread.sleep(3 * 1000)
    }

    def insertData(caseData){
        // switch to admin
        //switchToRole(administrator)
        dirtyData = record.createRecord(caseData.data)
        // switch back to accountant
        //switchToRole(accountant)

    }
    def setFilter(caseFilter){
        if (caseFilter.subsidiary) {
            if (context.isOneWorld()) {
                asDropdownList(locator: locators.subsidiary).selectItem(caseFilter.subsidiary)
            }
        }
        if (caseFilter.invoicetype) {
            asDropdownList(locator: locators.invoiceType).selectItem(caseFilter.invoicetype)
        }
        if (caseFilter.transType) {
            // not consider multi transaction type setting yet
            asMultiSelectField("custpage_transactiontype").setValues(caseFilter.transType)
        }
        if (caseFilter.datefrom) context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
        if (caseFilter.dateto) context.setFieldWithValue("custpage_dateto", caseFilter.dateto)
        if (caseFilter.docNumFrom) context.setFieldWithValue("custpage_documentnumberfrom_formattedValue", caseFilter.docNumFrom)
        if (caseFilter.docNumTo) context.setFieldWithValue("custpage_documentnumberto_formattedValue", caseFilter.docNumTo)
        if (caseFilter.enableSalesList) asDropdownList(locator: locators.enableSalesList).selectItem(caseFilter.enableSalesList)
    }

    def doMergeTest(boolean isSalesListBefore,isSalesListAfter){
        dirtyData = previewData = updatePreResults(caseData.previewData)
        int i=30
        while (!context.doesFieldExist("merge") && i>0){
            Thread.sleep(2 * 1000)
            clickEdit()

            waitForPageToLoad()
            i--
        }
        Thread.sleep(2 * 1000)
        def mergedLineNum = prepareMerge([])
        checkGroupSameItem()
        clickMerge()
        Thread.sleep(5 * 1000)
        checkMergedResult(mergedLineNum,isSalesListBefore,true)
        clickSave()
        waitForElement(locators.export)
        checkMergedPreview( isSalesListAfter)

    }
    /**
     * @desc zero line display test
     * transaction with no item
     * @case 19.5.1.1
     * @author zhen tang
     * @Non-regression: Cover by other case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_5_1_1(){
        caseDataInit("test_case_19_5_1_1")
        def expResult = testData["test_case_19_5_1_1"].expResult
        previewData = updatePreResults(caseData.previewData)
        Thread.sleep(3 * 1000)
        assertAreEqual("check doc Number", previewData.docNum.join(''),expResult.docNum)
        assertAreEqual("check item lines", previewData.lineQuantity.join(''),expResult.lineQuantity)

    }
    /**
     * @desc zero line display test
     * 1.transaction with no item
     * 2.has shipping and handling
     * @case 19.5.1.2
     * @author zhen tang
     * @Non-regression: Cover by other case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_5_1_2(){
        caseDataInit("test_case_19_5_1_2")
        def expResult = testData["test_case_19_5_1_2"].expResult
        previewData = updatePreResults(caseData.previewData)
        Thread.sleep(4 * 1000)
        assertAreEqual("check doc Number", previewData.docNum.join(''),expResult.docNum)
        assertAreEqual("check item lines", previewData.lineQuantity.join(''),expResult.lineQuantity)
    }
    /**
     * @desc zero line display test
     * check item line with Quantity 0 but amount is not zero
     * @case 19.5.1.3
     * @author zhen tang
     * @Regression: Vat Feature
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_5_1_3(){
        caseDataInit("test_case_19_5_1_3")
        def expResult = testData["test_case_19_5_1_3"].expResult
        previewData = updatePreResults(caseData.previewData)
        Thread.sleep(4 * 1000)
        assertAreEqual("check doc Number", previewData.docNum.join(''),expResult.docNum)
        assertAreEqual("check item lines", previewData.lineQuantity.join(''),expResult.lineQuantity)
    }
    /**
     * @desc zero line display test
     * After merge remove item row with quantity 0
     * @case 19.5.2.1
     * @author zhen tang
     * @Non-regression: Cover by other case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_5_2_1(){
        caseDataInit("test_case_19_5_2_1")
        doMergeTest(false,false)
    }
    /**
     * @desc zero line display test
     * After merge remove item row with quantity 0
     * has shipping and handling cost
     * @case 19.5.2.2
     * @author zhen tang
     * @Non-regression: Cover by other case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_5_2_2(){
        caseDataInit("test_case_19_5_2_2")
        doMergeTest(false,false)
    }
    /**
     * @desc zero line display test
     * After merge remove item row with quantity 0
     * before merge > 9 lines
     * after merge < 9 lines
     * @case 19.5.2.3
     * @author zhen tang
     * @Non-regression: Cover by other case
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_19_5_2_3(){
        caseDataInit("test_case_19_5_2_3")
        doMergeTest(false,false)
    }

}
