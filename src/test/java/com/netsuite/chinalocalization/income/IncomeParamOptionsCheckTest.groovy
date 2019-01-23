package com.netsuite.chinalocalization.income

import com.google.inject.Inject
import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName
import org.openqa.selenium.Alert

@TestOwner ("zhen.t.tang@oracle.com")
class IncomeParamOptionsCheckTest extends IncomeAppTestSuite{
    @Inject
    AlertHandler alertHandler
    @Rule
    public  TestName name = new TestName()
    def caseData
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeParamOptionsTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeParamOptionsTest_en_US.json"
        ]
    }
    @Override
    void tearDownTestSuite(){
        if(context.isOneWorld()) {
            setSubsidiaryFiscalCalendar(testData.teardown.subsidiary,testData.teardown.defaultCalendar)
        }
        super.tearDownTestSuite()
    }


    def initData(String testName){
        caseData=testData.get(name.getMethodName())
		if (testName) {
			caseData=testData.get(testName)
		}
    }
    def checkDropdownDisplayFormat(Map targetOptions,targetField){
        def i
        for(option in targetOptions){
            i=0
            setSearchParams([(targetField):option.key])
            for(c in context.getFieldText("custpage_${targetField}")){
                if(c == "\u00A0") i++
                else break
            }
            assertAreEqual("check display format of $option.key in field $targetField",option.value,i)
        }
    }

    def checkPeriodOptions(expected){
        def periodOptions = incomeStatementPage.getPeriodOptions()
        def start = periodOptions.findIndexOf{it -> it == expected[0]}
        def end = periodOptions.findIndexOf{it -> it == expected[-1]}
        assertAreEqual("Period amount is match expected",  end -start +1 , expected.size())
        assertAreEqual("Period options selected are match expected", periodOptions[start..end], expected )
    }
    def checkUnitOptions(expected){
        def unitOptions = incomeStatementPage.getUnitOptions()
        assertAreEqual("Check unit options",unitOptions,expected)
    }
    /**
     * @desc Income statement param drop down value and display check
     * @case 2.1.2.5 Check Period Options  - Sandardard Fiscal Calendar
     * @case 2.1.2.6 Check Unit Options
     * @case 2.1.2.7 Check Subsidiary Display format
     * @case 2.1.2.8 Check Period Display format
     * @author Zhen Tang
     */
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_2_1_2_5(){
        navigateToPortalPage()
        initData()
        checkPeriodOptions(caseData.expected)
    }
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_2_1_2_6(){
        navigateToPortalPage()
        initData()
        checkUnitOptions(caseData.expected)
    }
    @Category([P2.class,OW.class])
    @Test
    void test_case_2_1_2_7(){
        navigateToPortalPage()
        initData()
        checkDropdownDisplayFormat(caseData,'subsidiary')
    }
    @Category([P2.class,OWAndSI.class])
    @Test
    void test_case_2_1_2_8(){
        navigateToPortalPage()
        initData()
        checkDropdownDisplayFormat(caseData,'period')

    }
    @Category([P2.class,OW.class])
    @Test
    void test_case_2_1_2_9(){
        if(!context.isOneWorld()){
            return
        }
        initData()
        setSubsidiaryFiscalCalendar(caseData.filter.subsidiary,caseData.calenderToTest)
        navigateToPortalPage()
        setSearchParams(caseData.filter)
        checkPeriodOptions(caseData.expected)
        switchWindow()

        setSubsidiaryFiscalCalendar(caseData.filter.subsidiary,caseData.defaultCalendar)
    }
/**
 * @desc Income statement param drop down value and display check
 * @case 2.1.4.1 Parameters Linkage - Subsidiary&Period
 * @author Zhen Tang
 */
    @Category([P1.class,OW.class])
    @Test
    void test_case_2_1_4(){
        if(!context.isOneWorld()){
            return
        }
        initData()
        navigateToPortalPage()
        setSearchParams(caseData.filter1)
        checkParamsSetting(caseData.filterExpected1)

    }
/**
 * @desc Income statement param drop down value and display check
 * @case 2.1.4.2 Parameters Linkage - Subsidiary&Period
 * @author Zhen Tang
 */
	@Category([P1.class,OW.class])
	@Test
	void test_case_2_1_4_2(){
		if(!context.isOneWorld()){
			return
		}
		initData("test_case_2_1_4")
		setSubsidiaryFiscalCalendar(caseData.filter2.subsidiary,caseData.calenderToTest)
		navigateToPortalPage()
		setSearchParams(caseData.filter2)
		checkParamsSetting(caseData.filterExpected2)
		switchWindow()
		setSubsidiaryFiscalCalendar(caseData.filter2.subsidiary,caseData.defaultCalendar)
	}
}
