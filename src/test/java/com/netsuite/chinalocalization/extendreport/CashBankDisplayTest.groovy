package com.netsuite.chinalocalization.extendreport

import com.google.inject.Inject
import org.junit.Rule
import org.junit.experimental.categories.Category
import org.junit.Test
import org.junit.rules.TestName
import java.text.SimpleDateFormat

import com.netsuite.common.P1
import com.netsuite.common.OWAndSI
import com.netsuite.common.OW
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.chinalocalization.page.Extend.ExtendReportPage

/**
 * @desc: Cash and Bank Journal Report's Display Test
 * @author: xiaojuan.song
 */
@TestOwner("xiaojuan.song")
class CashBankDisplayTest extends ExtendReportAppTestSuite {

    private final def dateFormat = "MM/dd/yyyy"

    @Rule
    public TestName name = new TestName()
    private static def caseData

    @Inject
    ExtendReportPage extendReportPage

    @SuiteSetup
    void setUpTestSuite(){
        super.setUpTestSuite()
        enableAllClassificationFeatures()

        setShowAccountNum(true)
        setUseLastSubAccount(true)
        userPrePage.navigateToURL()
        userPrePage.setDateFormat(dateFormat, true)
    }

    def initData(){
        caseData = testData.get(name.getMethodName())
    }

    def pathToTestDataFiles(){
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "zhCN":"$dataFilesPath" + "extendreport\\data\\CashBankDisplay_zh_CN.json",
                "enUS":"$dataFilesPath" + "extendreport\\data\\CashBankDisplay_en_US.json"
        ]
    }

    def getCurrentFirstDay(){
        SimpleDateFormat format = new SimpleDateFormat(dateFormat)
        String firstDay

        def cale = Calendar.getInstance()
        cale.add(Calendar.MONTH, 0)
        cale.set(Calendar.DAY_OF_MONTH, 1)

        firstDay = format.format(cale.getTime())

        return reviseDate(firstDay)
    }

    def getCurrentLastDay(){
        SimpleDateFormat format = new SimpleDateFormat(dateFormat)
        String lastDay

        def cale = Calendar.getInstance()
        cale.set(Calendar.DAY_OF_MONTH, cale.getActualMaximum(cale.DAY_OF_MONTH))

        lastDay = format.format(cale.getTime())

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

    def checkOptions(expOptions, fieldId){
        def actualOptions = asDropdownList(["fieldId": fieldId]).getOptions()

        for (option in expOptions){
            assertTrue("check options", option in actualOptions)
        }
    }

    def checkLabels(expLabels){
        if (!context.isOneWorld()){
            expLabels.remove("subsidiary")
            expLabels.remove("location")
            expLabels.remove("department")
            expLabels.remove("class")
        }

        for (exp in expLabels){
            assertAreEqual("Check Labels Displayed", exp.value, asLabel("custpage_$exp.key"))
        }
    }

    def checkAccountSort(expBankAccount, expUnBankAccount){
        def actualFrom = extendReportPage.getAccountFromOptions()
        def actualTo = extendReportPage.getAccoutToList()

        for ( expUnBank in expUnBankAccount){
            assertFalse("Check AccountFrom", actualFrom.contains(expUnBank))
            assertFalse("Check AccountTo", actualTo.contains(expUnBank))
        }

        if (context.isOneWorld()){
            assertAreEqual("Check AccountFrom Sort", expBankAccount[0], actualFrom[1])
            assertAreEqual("Check AccountFrom Sort", expBankAccount[1], actualFrom[2])
            assertAreEqual("Check AccountTo Sort", expBankAccount[0], actualTo[1])
            assertAreEqual("Check AccountTo Sort", expBankAccount[1], actualTo[2])
        }
        else {
            assertAreEqual("Check AccountFrom Sort", expBankAccount[0], actualFrom[6])
            assertAreEqual("Check AccountTo Sort", expBankAccount[0], actualTo[6])
        }

    }

    /**
     *@desc display test
     *@case 2.1.2.1  Parameter Labels display
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. Standard Fiscal Calender
     * @author xiaojuan.song
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_2_1_2_1(){
        initData()
        def expLabels = caseData.Labels
        navigateToCashAndBankJournalPage()
        checkLabels(expLabels)
    }

    /**
     *@desc display test
     *@case 2.1.2.2  Default Values Check
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. Standard Fiscal Calender
     * @author xiaojuan.song
     */
    @Category([P1.class, OWAndSI.class])
    @Test
    void test_case_2_1_2_2() {
        initData()
        def expDefault = caseData.FieldDefault
        navigateToCashAndBankJournalPage()

        if (expDefault.datefrom == "6/1/2018"){
            expDefault.datefrom = getCurrentFirstDay()
        }
        if (expDefault.dateto == "6/30/2018"){
            expDefault.dateto = getCurrentLastDay()
        }

        checkParamsSetting(expDefault)
    }

    /**
     *@desc display test
     *@case 2.1.2.3  Subsidiary Options Check
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. Standard Fiscal Calender
     * @author xiaojuan.song
     */
    @Category([P1.class,OW.class])
    @Test
    void test_case_2_1_2_3(){
        initData()
        def expSub = caseData.subOptions
        navigateToCashAndBankJournalPage()
        checkOptionsValue(expSub, "subsidiary")
    }

    /**
     *@desc display test
     *@case 2.1.2.4  Date Value Check - Subsidiary has no current period
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. WithoutCurr Calender
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_2_1_2_4(){
        initData()
        def expParm = caseData.SearchParm.subsidiary
        def expCalender = caseData.CalenderToTest
        def expDates = caseData.Dates
        setSubsidiaryFiscalCalendar(expParm, expCalender)
        navigateToCashAndBankJournalPage()
        setSearchParams(caseData.SearchParm)

        if (expDates.datefrom == "6/1/2018"){
            expDates.datefrom = getCurrentFirstDay()
        }
        if (expDates.dateto == "6/30/2018"){
            expDates.dateto = getCurrentLastDay()
        }

        checkParamsSetting(expDates)

        clickRefresh()
    }

    /**
     *@desc display test
     *@case 2.1.2.5  Date Value Check - Subsidiary has no period
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. No Period Calender
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_2_1_2_5(){
        initData()
        def expParm = caseData.SearchParm.subsidiary
        def expCalender = caseData.CalenderToTest
        def expDates = caseData.Dates
        setSubsidiaryFiscalCalendar(expParm, expCalender)
        navigateToCashAndBankJournalPage()
        setSearchParams(caseData.SearchParm)

        if (expDates.datefrom == "6/1/2018"){
            expDates.datefrom = getCurrentFirstDay()
        }
        if (expDates.dateto == "6/30/2018"){
            expDates.dateto = getCurrentLastDay()
        }

        checkParamsSetting(expDates)

        clickRefresh()
    }

    /**
     *@desc display test
     *@case 2.1.2.6 Account Options Check - Checked "ONLY SHOW LAST SUBACCOUNT"
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. Standard Fiscal Calender
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_2_1_2_6(){
        initData()
        navigateToCashAndBankJournalPage()

        def expParm = caseData.SearchParm
        def expBankAccount = caseData.BankAccounts
        def expUnBankAccount = caseData.UnBankAccounts

        if (context.isOneWorld()) {
            setSearchParams(expParm)
        }

        checkAccountSort(expBankAccount, expUnBankAccount)
        assertFalse("Checked 'ONLYSHOWLASTSUBACCT'", expBankAccount[2].contains(":"))

        clickRefresh()
    }

    /**
     *@desc display test
     *@case 2.1.2.6 Account Options Check - UNChecked "ONLY SHOW LAST SUBACCOUNT"
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. Standard Fiscal Calender
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_2_1_2_7(){
        userPrePage.navigateToURL()
        setUseLastSubAccount(false)

        initData()
        navigateToCashAndBankJournalPage()
        def expParm = caseData.SearchParm
        def expBankAccount = caseData.BankAccounts
        def expUnBankAccount = caseData.UnBankAccounts

        if (context.isOneWorld()) {
            setSearchParams(expParm)
        }

        checkAccountSort(expBankAccount, expUnBankAccount)
        assertTrue("UnChecked 'ONLYSHOWLASTSUBACCT'", expBankAccount[2].contains(":"))

        clickRefresh()
    }


    /**
     *@desc classification parameter test
     *@case 8.1.2 Classification Options Check
     1.Checked Use Account Number
     2.Checked ONLY SHOW LAST SUBACCOUNT
     3. Date Format: M/D/YYYY
     4. Login as China Accountant
     5. Standard Fiscal Calender
     6. Enable Classfication Feature
     * @author xiaojuan.song
     */
    @Category([P1.class, OW.class])
    @Test
    void test_case_8_1_2(){
        initData()
        def expSearch = caseData.searchparm
        def expLoc = caseData.LocOptions
        def expDep = caseData.DepOptions
        def expClass = caseData.ClassOptions

        navigateToCashAndBankJournalPage()
        setSearchParams(expSearch)

        checkOptions(expLoc, "inpt_custpage_location")
        checkOptions(expDep, "inpt_custpage_department")
        checkOptions(expClass, "inpt_custpage_class")
    }



}
