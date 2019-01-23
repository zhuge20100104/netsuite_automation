package com.netsuite.chinalocalization.voucher

import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/*
@Author lifang.tang@oracle.com
* @Created 2018-Mar-28
* @desc:Test cases 1.4 Voucher Split
*/
@TestOwner("lifang.tang@oracle.com")
class VoucherSplitTest extends VoucherP2BaseTest {
    //lifang
    static final String PDF_ITEM_TABLE = "//pdf//table[@class='itemtable']"
    static final String BLANK_LINE = "//pdf//table[@class='itemtable'][1]//tbody/tr/td[1][not(string())]"

    //lifang
    def getCurrentPageValue(){
        return context.executeScript("return nlapiGetFieldText(\"selectpage\")")
    }

    //lifang
    def getVoucherCountOnCurrentPage(){
        return asElements(PDF_ITEM_TABLE).size()
    }


    def getBlankLineNumberInItemTable(){
         return asElements(BLANK_LINE).size()
    }

    //lifang
    def getDropdownListValuesByFieldId(String fieldId){
        return context.withinCNDropdownlist(fieldId).getOptions()
    }

    def isMultipleCalendarEnabled(){
        Boolean enabled = context.executeScript('''var con = nlapiGetContext(); 
                                                return con.getFeature("MULTIPLECALENDARS"); ''')
        return enabled
    }

    /*
    @desc Split lines = 8
    @case 1.4.1.2
     */
    @Category([P2.class, OW.class])
    @Test
    void case_1_4_1_2(){
        def testData = cData.data("test_1_4_1_2")

        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)

        assertAreEqual("Verify current page is 1/1", getCurrentPageValue(),"1/1")
        assertAreEqual("Only one voucher on current page",getVoucherCountOnCurrentPage(),1)

    }

    /*
    @desc Split lines = 2
    @case 1.4.1.3
     */
    @Category([P3.class, OW.class])
    @Test
    void case_1_4_1_3(){
        def testData = cData.data("test_1_4_1_3")

        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)

        navigateToVoucherPrintPage()
        String subsidiary = testData.labels().subsidiary
        String periodFrom = testData.labels().periodFrom
        String periodTo = testData.labels().periodTo
        String startDate = testData.labels().startDate
        String endDate = testData.labels().endDate
        printPage.refreshReport(subsidiary,periodFrom,periodTo,startDate,endDate)

        assertAreEqual("Verify current page is 1/1", getCurrentPageValue(),"1/1")
        assertAreEqual("Only one voucher on current page",getVoucherCountOnCurrentPage(),1)
        assertAreEqual("Verify there are 5 blank lines", getBlankLineNumberInItemTable(),5)

    }

    /*
    @desc Check Multi-Calendar(Multi-Calendar enabled)
    @case 1.8.1.2
     */
    @Category([P3.class, OW.class])
    @Test
    void case_1_8_1_2(){
        assertTrue("Multiple Calendar Feature is enabled",isMultipleCalendarEnabled())

        def testData = cData.data("test_1_8_1_2")

        navigateToVoucherPrintPage()

        String subsidiary = testData.labels().subsidiary1
        printPage.selectSubsidiaryItem(subsidiary)
        def periodFromList = getDropdownListValuesByFieldId("periodfrom")
        def periodToList = getDropdownListValuesByFieldId("periodto")
        assertTextContains("Verify Standard Fiscal Calendar",periodFromList.last(),"Dec")
        assertTextContains("Verify Standard Fiscal Calendar",periodToList.last(),"Dec")

        subsidiary = testData.labels().subsidiary2
        printPage.selectSubsidiaryItem(subsidiary)
        periodFromList = getDropdownListValuesByFieldId("periodfrom")
        periodToList = getDropdownListValuesByFieldId("periodto")
        assertTextContains("Verify Japan Fiscal Calendar",periodFromList.last(),"Mar")
        assertTextContains("Verify Japan Fiscal Calendar",periodToList.last(),"Mar")
    }

}

