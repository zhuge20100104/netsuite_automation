package com.netsuite.chinalocalization.vat

import org.junit.Test

class VATPerformanceTest extends VATEditPageTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATPerformanceTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATPerformanceTest_zh_CN.json"
        ]
    }

    private  def caseData
    private  def caseFilter
    private  def expResult
    def init(String caseNo){
        caseData = testData.get(caseNo)
        caseFilter = caseData.filter
        cleanAll = false
        if (caseData.containsKey("expectedResult")){expResult = caseData.expectedResult}
        //switchToRole(administrator)
        def records = dirtyData = record.createRecord(caseData.data)
        //switchToRole(accountant)
        navigateToPortalPage()
        waitForPageToLoad()
    }
    def setQuery(options=[:]) {

        if (caseFilter.subsidiary )
            asDropdownList(locator: locators.subsidiary).selectItem(caseFilter.subsidiary)

        if (caseFilter.invoicetype) {
            asDropdownList(locator: locators.invoiceType).selectItem(caseFilter.invoicetype)
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
    private void test_case_no_salelist(String caseNo){
        //init(caseNo)

        caseFilter = testData.filter
        long start = System.nanoTime(), end
        navigateToPortalPage()
        waitForPageToLoad()
        end = System.nanoTime()
        println("Init Page taken ${( end - start  )/1.0e9} seconds")
        setQuery()
        println("Check Refresh")
        start = System.nanoTime()
        clickRefresh()

        println("waiting for edit button shows up")
        waitForElement(locators.edit)
        println("Done for waiting edit button shows up")
        /*
        long start_getAll =  System.nanoTime(),end_getAll =0
        previewData =getAllResults()
        end_getAll = System.nanoTime()
        */
        assertFalse("Should find data after query", context.isTextVisible(editData.errorMessage.noDataExport))
        int i=30
        println( "check page change ${context.doesFieldExist("merge")}")
        clickEdit()
        end =  System.nanoTime()
        while (!context.doesFieldExist("merge") && i>0){
            Thread.sleep(5 * 1000)
            println( "check page change in loop ${context.doesFieldExist("merge")}")
            clickEdit()
            end =  System.nanoTime()
            waitForPageToLoad()
            i--
        }
        long edit_start = System.nanoTime(),edit_end
        println("Query taken ${( end - start  )/1.0e9} seconds")
        //println("Query taken ${( end - start - end_getAll + start_getAll )/1.0e9} seconds")
        i=30
        edit_end = System.nanoTime()
        while (!context.isTextVisible(editData.groupSameItem) && i >0){
            Thread.sleep(5 * 1000)
            edit_end = System.nanoTime()
            i--
        }
        println("Go Edit taken ${( edit_end - end )/1.0e9} seconds from click edit button")
        println("Edit Page taken ${( edit_end - edit_start )/1.0e9} seconds")
        assertTrue("Merge Button present", context.isTextVisible(editData.groupSameItem))
        //prepare merge
        //As po suggested this time just merge  five trans every merge
        i =1
        int mergeLine =0 //where cause the merge
        waitForElement(".//input[@id='custpage_applied_1']")
        Thread.sleep(2 * 1000)
        def headerCount = getHeaderCount()
        while (i <= getHeaderCount()){
            asClickTransCheckbox(i)
         //checkGroupSameItem()
            if (i-5 == mergeLine){
                println("Start merge:")
                checkGroupSameItem()
                start = System.nanoTime()
                clickMerge()
                edit_end = System.nanoTime()
                println("During click merge ${( edit_end - start)/1.0e9} seconds")
                i++
                //Thread.sleep(10 * 1000)
                value = headerTextInColumnOfRow("internalId", i-5)
                println("InternalId ${value}")

                mergeLine = i
                end =  System.nanoTime()
                while(!value.startsWith("CON")){
                    Thread.sleep(5 * 1000)
                    value = headerTextInColumnOfRow("internalId", i-5)
                    println("InternalId ${value} in wait")
                    end =  System.nanoTime()
                }
                println("Finish merge ${i%5} taken: ${( end - start)/1.0e9} seconds after click taken: ${( end - edit_end)/1.0e9} seconds ")
            }
            i++
            if (i == 25) break
        }
        Thread.sleep(5 * 1000)
        //end merge
        println("finish merge")
        //Start save

        start = System.nanoTime()
        clickSave()
        i=30
        end =  System.nanoTime()
        while (!context.doesFieldExist("merge") && i>0){
            Thread.sleep(1 * 1000)
            clickEdit()
            end =  System.nanoTime()
            waitForPageToLoad()
            i--
        }
        println("Finish Save taken: ${( end - start)/1.0e9} seconds")
        waitForElement(editLocators.unmergeButton)
        edit_end = System.nanoTime()
        i=30
        while (!context.isTextVisible(editData.groupSameItem) && i >0){
            Thread.sleep(1 * 1000)
            edit_end = System.nanoTime()
            i--
        }
        println("Go Edit taken ${( edit_end - end )/1.0e9} seconds from click edit button")


        i =1
        while (i <= getHeaderCount()){
            value = headerTextInColumnOfRow("internalId", i)
            if(value.startsWith("CON"))
            asClickTransCheckbox(i)
             i ++
        }
        //prepareUnMerge(mergedLineNum, false)
        start = System.nanoTime()
        clickUnmerge()
        end = System.nanoTime()
        println("During click Unmerge button  taken: ${( end - start)/1.0e9} seconds")
        println(" ${getHeaderCount()} : ${headerCount}")
        Thread.sleep(2 * 1000)

        while (getHeaderCount() > headerCount){
            Thread.sleep(5 * 1000)
            println("Unmerging: ${getHeaderCount()}")
            end = System.nanoTime()
        }
        println("Finish Unmerge  taken: ${( end - start)/1.0e9} seconds")
        // Check merged result
        //Start save
        start = System.nanoTime()
        clickSave()

        waitForElement(locators.edit)
        i=30
        while (!context.doesFieldExist("merge") && i>0){
            Thread.sleep(1 * 1000)
            clickEdit()
            end =  System.nanoTime()
            waitForPageToLoad()
            i--
        }
        println("Finish Save taken: ${( end - start)/1.0e9} seconds")

    }
    private void test_case_with_salelist(String caseNo){
        init(caseNo)
        setQuery()
        long start,end = System.nanoTime()
        clickRefresh()
        waitForElement(locators.edit)
        previewData =getAllResults()

        assertTrue("Preview page not empty", previewData.size()>0)
        int i=30
        while (!context.doesFieldExist("merge") && i>0){
            Thread.sleep(2 * 1000)
            clickEdit()
            end =  System.nanoTime()
            waitForPageToLoad()
            i--
        }
        long edit_start = System.nanoTime()
        println("Query taken ${( end - start )/1.0e9} seconds")
        println("Go Edit taken ${( edit_start - end )/1.0e9} seconds")
        //("Merge Button present", context.doesFieldExist("merge"))
        assertTrue("UnMerge Button present", context.doesFieldExist("unmerge"))
        //assertTrue("Cancle Button present", context.doesFieldExist("custpage_cancel"))
        //prepare merge
        def mergedLineNum =prepareMerge()
        checkGroupSameItem()
        clickMerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkMergedResult(mergedLineNum,true,true)
        clickSave()
        waitForElement(locators.export)
        Thread.sleep(10 * 1000)
        checkMergedPreview( true)
        setQuery(isSaleslist:"no")
        clickRefresh()
        i=30
        while (!context.doesFieldExist("merge") && i>0){
            Thread.sleep(2 * 1000)
            clickEdit()
            end =  System.nanoTime()
            waitForPageToLoad()
            i--
        }
        waitForElement(editLocators.unmergeButton)
        prepareUnMerge(mergedLineNum, true)
        clickUnmerge()
        Thread.sleep(5 * 1000)
        // Check merged result
        checkUnMergedResult(mergedLineNum)
        clickSave()
        Thread.sleep(10 * 1000)
        waitForElement(locators.export)
        checkUnMergedPreview()
    }
    /**
     * @desc If the selected transactions have total amount exceed max amount.
     *   Check error message
     * @case 19.2.2.1 single merge
     * [invoice]: [IN10850]
     item1, 1
     item1, 1
     item2, 1
     * @author Christina Chen
     */
    //@Test
    void test_case_19_2_2_1() {
        test_case_no_salelist("test_case_19_2_2_1")
    }
    /**
     * @desc If the selected transactions have total amount exceed max amount.
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
     */
    //@Test
    void test_case_19_2_2_2() {
        test_case_no_salelist("test_case_19_2_2_1")
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
     */
    //@Test
    void test_case_19_2_2_3() {
        test_case_no_salelist("test_case_19_2_2_1")
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
     */
    //@Test
    void test_case_19_2_2_4() {
        test_case_no_salelist("test_case_19_2_2_1")
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
     */
   //@Test
    void test_case_19_2_2_5() {
        test_case_no_salelist("test_case_19_2_2_1")
    }
    /**
     * @desc negative Single Merge(just same item)
     Consolidation Transaction < 8 lines
     * @case 19.2.2.6
     *CM10861 - Item1:  quantity:-1, amount -5600.99
     Item1:  quantity:-1, amount -5600.99
     Item2:  quantity:-2, amount -10600.99*2"
     * @author Christina Chen
     */

    //@Test
    void test_case_19_2_2_6() {
        test_case_no_salelist("test_case_19_2_2_6")

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
     */
    //@Test
    void test_case_19_2_2_7() {
        test_case_no_salelist("test_case_19_2_2_7")

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
     */
    //@Test
    void test_case_19_2_2_8() {


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
     */
    //@Test
    void test_case_19_2_2_9() {
        test_case_no_salelist("test_case_19_2_2_9")

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
     */
    //@Test
    void test_case_19_2_2_10() {
        test_case_no_salelist("test_case_19_2_2_10")

    }
}
