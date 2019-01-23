package com.netsuite.chinalocalization.vat

import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TestName
import org.junit.rules.Timeout
import org.junit.experimental.categories.Category
@TestOwner("christina.chen@oracle.com")
class VATGroupItemTest extends VATEditPageTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATGroupItemTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATGroupItemTest_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    @Rule
    public Timeout globalTimeout= new Timeout(20*60 *1000)

    def caseData
    def caseFilter
    def expResult

    def init(){
        caseData = testData.get(name.getMethodName())
        caseFilter = caseData.filter
        cleanAll = false
        if (caseData.containsKey("expectedResult")){expResult = caseData.expectedResult}
        /*switchToRole(administrator)
        def records = dirtyData = record.createRecord(caseData.data)
        switchToRole(accountant)
        */
        navigateToPortalPage()
        waitForPageToLoad()
    }
    def waitForElementToAppear(Closure checkElementExist, timeout = 60) {
        def waitTimes = timeout.intdiv(2)
        for (i in 0..waitTimes) {
            if (checkElementExist()) {  
                return true
            }
            Thread.sleep(2 * 1000)
        }
        return false
    }



    def setQuery(options=[:]) {
        //isTextVisible(editData.groupSameItem)

        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(editData.filter.subsidiary)
        }
        if (options.invoiceType) {
            asDropdownList(locator: locators.invoiceType).selectItem(options.invoiceType)
        }

        if (options.customer){
            asMultiSelectField("custpage_customer").selectItems( *options.customer)
        }
        if (options.transactionType){
            asMultiSelectField("custpage_transactiontype").setValues(*options.transactionType)
        }
        if (options.isSaleslist in [ "yes", "no"] ) {
            context.setFieldWithValue(
                    "custpage_saleslist", editData.saleslist.get(options.isSaleslist))
        }
        //editData.tranType.CustInvc, editData.tranType.CashSale
        if (caseFilter.datefrom )context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
        if (caseFilter.dateto)context.setFieldWithValue("custpage_dateto", caseFilter.dateto)
        if (!caseFilter.to_tranid) { caseFilter.to_tranid = caseFilter.tranid }
        if (caseFilter.tranid) context.setFieldWithValue("custpage_documentnumberfrom", caseFilter.tranid)
        if (caseFilter.to_tranid) context.setFieldWithValue("custpage_documentnumberto", caseFilter.to_tranid)


    }
    private void test_case_no_unmerge(){
        init()
        setQuery()
        clickRefresh()
        waitForElement(locators.edit)
        dirtyData = previewData =updatePreResults(caseData.previewData)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        assertTrue("Merge Button present", context.isTextVisible(editData.groupSameItem))
        Thread.sleep(2 * 1000)
        int mergedLineNum =prepareMerge()
        checkGroupSameItem()
        clickMerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkMergedResult(mergedLineNum,false,true)
/*        Thread.sleep(2 * 1000)
        clickCancel()
        clickConfirmOk()*/
        //assertTrue("Current page expected", context.isTextVisible(caseData.expectedResults.title))
    }
    private void test_case_no_salelist(){

        init()
        setQuery()

        clickRefresh()
        waitForElement(locators.edit)
        dirtyData = previewData =updatePreResults(caseData.previewData)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        assertTrue("Merge Button present", context.isTextVisible(editData.groupSameItem))
        Thread.sleep(2 * 1000)
        int mergedLineNum =prepareMerge()
        checkGroupSameItem()
        clickMerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkMergedResult(mergedLineNum,false,true)
        Thread.sleep(2 * 1000)
        asClickTransCheckbox(mergedLineNum)
        //prepareUnMerge(mergedLineNum, false)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        checkUnMergedResult(mergedLineNum)
/*        clickSave()
        Thread.sleep(5 * 1000)
        waitForElement(locators.export)*/
    }
    private void test_case_with_salelist(){
        init()
        setQuery()
        long start,end = System.nanoTime()
        clickRefresh()
        waitForElement(locators.edit)
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        dirtyData = previewData =updatePreResults(caseData.previewData)
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        //("Merge Button present", context.doesFieldExist("merge"))
        assertTrue("UnMerge Button present", context.doesFieldExist("unmerge"))
        //assertTrue("Cancle Button present", context.doesFieldExist("custpage_cancel"))
        Thread.sleep(2 * 1000)
        def mergedLineNum =prepareMerge()
        checkGroupSameItem()
        clickMerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkMergedResult(mergedLineNum,true,true)
        clickSave()
        waitForElement(locators.export)
        Thread.sleep(5 * 1000)
        checkMergedPreview( true)
        setQuery(isSaleslist:"no")
        clickRefresh()
        waitForElement(locators.edit)
        clickEditAndLoad(functionName: "doesFieldExist",text: "merge", buttonName: "clickEdit")
        clickEditAndLoad(functionName: "isTextVisible",text: editData.groupSameItem)
        waitForElement(editLocators.unmergeButton)
        Thread.sleep(2 * 1000)
        prepareUnMerge(mergedLineNum, true)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkUnMergedResult(mergedLineNum)
/*        clickSave()
        Thread.sleep(10 * 1000)
        waitForElement(locators.export)
        checkUnMergedPreview()*/
    }
    /**
     * @desc single merge
     *   Check error message
     * @case 19.2.2.1 single merge
     * [invoice]: [IN10850]
     item1, 1
     item1, 1
     item2, 1
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_1() {
        test_case_no_salelist()
    }
    /**
     * @desc No Shipping Cost No handling Cost megere
     *   Check error message
     * @case 19.2.2.2
     *  [IN10851, CS10852]
     *  IN10851 - Item1: quantity 1 no discount
     Item2: quantity 1 no discount
     no Shipping Cost
     no handling Cost
     CS10852 - Item1:quantity 1  no discount
     Item2: quantity 1 no discount
     no Shipping Cost
     no handling Cost
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_2() {
        test_case_no_salelist()
        def  test = data
    }
    /**
     * @desc If the selected transactions have total amount exceed max amount.
     *   Check error message
     * @case 19.2.2.3
     * IN10853 - Item1:  no discount, quantity:1
     Item2:  discount 10%, quantity:1
     has Shipping Cost, quantity:1
     has handling Cost, quantity:1
     CS10854 - Item1:  no discount, quantity:1
     Item2:  no discount, quantity:1
     has Shipping Cost, quantity:1
     has handling Cost, quantity:1  
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_3() {
        test_case_no_salelist()
    }

    /**
     * @desc Simple Merge(same item,
     same unit price and unit discount
     has Shipping and handling
     with different tax rate)
     Consolidation Transaction <= 8 lines;
     * @case 19.2.2.4
     * IN10855 - Item1:  no discount, quantity:1
     has Shipping Cost (tax rate 17%)
     has handling Cost (tax rate 13%)
     CS10856 - Item1:  no discount, quantity:1

     has Shipping Cost (tax rate 13%)
     has handling Cost (tax rate 17%)
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_4() {
        test_case_no_salelist()
    }

    /**
     * @desc Consolidation Transaction > 8 lines
     * @case 19.2.2.5
     * IN10859 - Item1: quantity:1, has discount
     Item2: quantity:1 has discount
     Item3: quantity:1 has discount
     Shipping Cost
     handling Cost
     CS10860 - Item1:  quantity:2, has discount
     Item4:  quantity:1 has discount
     Shipping Cost
     handling Cost
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_5() {
        test_case_with_salelist()
    }
    /**
     * @desc negative Single Merge(just same item)
     Consolidation Transaction < 8 lines
     * @case 19.2.2.6
     *CM10861 - Item1:  quantity:-1, amount -5600.99
     Item1:  quantity:-1, amount -5600.99
     Item2:  quantity:-2, amount -10600.99*2"
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_6() {
        test_case_no_salelist()

    }

    /**
     * 
     * @desc negative Group item Merge(just same item)
     Consolidation Transaction < 8 lines
     * @case 19.2.2.7
     * CM10863 - Item1: quantity -1 no discount
     Item2: quantity -1 no discount
     no Shipping Cost
     no handling Cost
     CR10864 - Item1: quantity -1 no discount
     Item2: quantity -1 no discount
     no Shipping Cost
     no handling Cost"
     * @author Christina Chen
     * Non-regression: negative case
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_7() {
        test_case_no_salelist()

    }
    /**
     * @desc negative Merge(same item, has differen discount
     has Shipping and handling
     with same tax rate)
     Consolidation Transaction <= 8 lines
     * @case 19.2.2.8
     * CM10867 - Item1:  no discount, quantity: -1
     Item2:  discount -10%, quantity: -1
     has Shipping Cost, quantity: -1
     has handling Cost, quantity: -1
     CF10868 - Item1:  no discount, quantity: -1
     Item2:  no discount, quantity: -1
     has Shipping Cost, quantity: -1
     has handling Cost, quantity: -1
     * @author Christina Chen
     * @Non-regression: negative case
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_8() {
        test_case_no_unmerge()
    }
    /**
     * @desc Simple Merge(same item,
     same unit price and unit discount
     has Shipping and handling
     with different tax rate)
     Consolidation Transaction <= 8 lines;
     * @case 19.2.2.9
     * CM10857 - Item1:  no discount, quantity: -1
     has Shipping Cost (tax rate 17%)
     has handling Cost (tax rate 13%)
     CR10858 - Item1:  no discount, quantity: -1
     has Shipping Cost (tax rate 13%)
     has handling Cost (tax rate 17%)
     * @author Christina Chen
     * @Non-regression: negative case
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_9() {
        test_case_no_unmerge()

    }
    /**
     * @desc Positive & negative Merge(
     has Shipping and handling
     )
     Consolidation Transaction <= 8 lines;
     * @case 19.2.2.10
     IN10869 - Item1: quantity:2, has discount
     Item2: quantity:1 has discount
     Item3: quantity:1 has discount
     Shipping Cost (Sa)
     handling Cost (Ha)
     CM10870 - Item1:  quantity:-1, has discount
     Shipping Cost -(Sb)
     handling Cost -(Hb)
     Same Sheet Number
     (Sa -Sb) >0
     (Ha -Hb) >0
     * @author Christina Chen
     * @Regression: Vat Feature
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_19_2_2_10() {
        test_case_no_unmerge()

    }
}
