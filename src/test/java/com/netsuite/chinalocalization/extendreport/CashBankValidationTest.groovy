package com.netsuite.chinalocalization.extendreport

import com.netsuite.common.P3
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

import com.netsuite.common.OWAndSI
import com.netsuite.common.SI
import com.netsuite.common.P1
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup

import java.text.SimpleDateFormat

/**
 * @desc: Cash and Bank Journal Report's Validation Test
 * @author: xiaojuan.song
 */
@TestOwner("xiaojuan.song")
class CashBankValidationTest extends ExtendReportAppTestSuite {

    private final def dateFormat = "DD.MM.YYYY"

    @Rule
    public TestName name = new TestName()
    private caseData

    def initData(){
        caseData = testData.get(name.getMethodName())
    }

    @SuiteSetup
    void setUpTestSuite(){
        super.setUpTestSuite()

        setShowAccountNum(true)
        setUseLastSubAccount(true)
        userPrePage.navigateToURL()
        userPrePage.setDateFormat(dateFormat, true)
    }

    def pathToTestDataFiles(){
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "zhCN":"$dataFilesPath" + "extendreport\\data\\CashBankValidation_zh_CN.json",
                "enUS":"$dataFilesPath" + "extendreport\\data\\CashBankValidation_en_US.json"
        ]
    }

    def checkValidationMsg(filter=null,refrashflag =false){
        navigateToCashAndBankJournalPage()
        setSearchParams(filter)
        if(refrashflag) clickRefresh()
        assertAreEqual("check validation messages ",caseData.errMsg, getErrMsg())
        clickErrDialogOK()
    }

    def getCurrentFirstDay(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd")
        String firstDay

        def cale = Calendar.getInstance()
        cale.add(Calendar.MONTH, 0)
        cale.set(Calendar.DAY_OF_MONTH, 1)

        firstDay = format.format(cale.getTime())

        firstDay = firstDay[8,9] + firstDay[4..6] + "." + firstDay[0..3]

        return reviseDate(firstDay)
    }

    def getCurrentLastDay(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd")
        String lastDay

        def cale = Calendar.getInstance()
        cale.set(Calendar.DAY_OF_MONTH, cale.getActualMaximum(cale.DAY_OF_MONTH))

        lastDay = format.format(cale.getTime())

        lastDay = lastDay[8,9] + lastDay[4..6] + "." + lastDay[0..3]

        return reviseDate(lastDay)
    }

    def reviseDate(datee){

        if (datee[0] == "0" && datee[3] == "0"){
            return ( datee[1,2] + datee.substring(4) )
        }

        if (datee[0] != "0" && datee[3] == "0"){
            return ( datee[0,1,2] + datee.substring(4) )
        }

        if (datee[0] == "0" && datee[3] != "0"){
            return ( datee.substring(1) )
        }
    }


//    // Check No Period
//    @Category([P1.class, OW.class])
//    @Test
//    void test_case_5_1_1(){
//        initData()
//        def expParm = caseData.SearchParm.subsidiary
//        def expCalender = caseData.CalenderToTest
//        def expErr = caseData.errMsg
//        def expDefault = caseData.FieldDefault
//
//        //OK
//        navigateToCashAndBankJournalPage()
//        clickRefresh()
//
//        setSubsidiaryFiscalCalendar(expParm, expCalender)
//        navigateToCashAndBankJournalPage()
//        setSearchParams(caseData.SearchParm)
//        clickRefresh()
//        assertAreEqual("check validation messages ", expErr, getErrMsg())
//        clickErrDialogOK()
//        checkParamsSetting(expDefault)
//
//    }

    /**
     *@desc validation test
     *@case 5.2.1 Check disabled Feature: Advanced PDF/HTML
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: DD.MM.YYYY
     4. Login as China Accountant
     * @author xiaojuan.song
     */
    @Category([P2.class, SI.class])
    @Test
    void test_case_5_2_1(){
        initData()
        def expDefault = caseData.FieldDefault

        setAdvancedPrint(false)
        navigateToCashAndBankJournalPage()
        assertAreEqual("check validation messages ",caseData.errMsg, getErrMsg())
        clickErrDialogOK()

        setAdvancedPrint(true)
    }

    /**
     *@desc validation test
     *@case 5.3.1  Date Validation:DateFrom > DateTo
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: DD.MM.YYYY
     4. Login as China Accountant
     * @author xiaojuan.song
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_5_3_1(){
        initData()
        def expSearchParm = caseData.SearchParm
        def expDefault = caseData.FieldDefault

        navigateToCashAndBankJournalPage()
        setSearchParams(expSearchParm)
        clickRefresh()
        assertAreEqual("check validation messages ",caseData.errMsg, getErrMsg())
        clickErrDialogOK()

    }

    /**
     *@desc validation test
     *@case 5.3.2  Date Validation:DateFrom=XXX, DateTo=""
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: DD.MM.YYYY
     4. Login as China Accountant
     * @author xiaojuan.song
     */
    @Category([P3.class, OWAndSI.class])
    @Test
    void test_case_5_3_2(){
        initData()
        def expSearchParm = caseData.SearchParm
        def expDefault = caseData.FieldDefault

        navigateToCashAndBankJournalPage()
        context.setFieldWithValue("custpage_datefrom", expSearchParm.datefrom)
        clickRefresh()
        assertAreEqual("check validation messages ",caseData.errMsg, getErrMsg())
        clickErrDialogOK()

    }

    /**
     *@desc validation test
     *@case 5.4.1  Account Validation : AccountFrom > AccountTo
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: DD.MM.YYYY
     4. Login as China Accountant
     * @author xiaojuan.song
     */
    @Category([P2.class, OW.class])
    @Test
    void test_case_5_4_1(){
        initData()
        def expSearchParm = caseData.SearchParm
        checkValidationMsg(expSearchParm)


    }
}
