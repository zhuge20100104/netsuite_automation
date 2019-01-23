package com.netsuite.chinalocalization.income

import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.html.parsers.TableParser
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("molly.feng@oracle.com")
class IncomeClassificationTest extends IncomeAppTestSuite{
	def pathToTestDataFiles() {
		return [
				"zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeClassificationTest_zh_CN.json",
				"enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeClassificationTest_en_US.json"
		]
	}
    def  changflag = false
	/**
	 * @desc  Case1: Location:All;Department:All;Class:All
	 *        Check the Report Header and Report Table Contents
	 *        it will contain the 3 classification parameters in Report header
	 *        and the report amount will be same as saved report in Finalcial Row[一、营业收入]
	 *
	 * 	 @case 8.2.1.1
	 * 	 Before Test: Enable Features: Location, Department, Class
	 * 	 ChangeToEnglish to GetSavedReport
	 * 	 Test :
	 * 	 Role:China Accountant and target language
	 * 	 Parameters:
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
	 * 	 SUBSIDIARY (子公司):中国 Income
	 * 	 PERIOD(期间):Jun 2017
	 * 	 UNIT(单位):Unit(元)
	 * 	 LOCATION(地点): All(Empty)
	 * 	 DEPARTMENT(部门): All(Empty)
	 * 	 CLASS(类别):All(Empty)
	 * ---- Expected Result ---
	 *  Report Header 4th line:  Not display
	 *
	 * 	 After Test : Enable Features: Location, Department, Class
	 * @author Molly Feng
	 */
	@Test
	@Category([OW.class, P1.class])
	void test_case_8_2_1_1() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseData = testData["test_case_8_2_1_1"]
		def caseFilter = testData["test_case_8_2_1_1"].filter
		def expResult = testData["test_case_8_2_1_1"].expectedResult

		// Get Saved Report Data as Admin role in English
		//switchToRole(administrator)
		if (context.getUserLanguage() != "en_US") {
			changeLanguageToEnglish()
			changflag = true
		}
		def savedReportData = getSavedReport(caseData.report)
		 //switchToRole(accountant)
		if (changflag) {
			changeLanguageToChinese()
		}
		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(incomeStatementPage.getXPATH_BTN_EXP_PDF())
		Thread.sleep(5 * 1000)
		// get the Report Data from the page
		def actualReportData = getISReport(caseData.report)

		// check the Report
		checkReportHeader(expResult)
		checkReportTable(actualReportData, savedReportData)
	}

	/**
	 * @desc  Case2: Location:中国 Income华北区;Department:All;Class:All
	 *        Check the Report Header and Report Table Contents
	 *        it will contain the 1 classification parameters in Report header
	 *        and the report amount will be same as saved report in Finalcial Row[一、营业收入]
	 *
	 * 	 @case 8.2.1.2
	 * 	 Before Test: Enable Features: Location, Department, Class
	 * 	 ChangeToEnglish to GetSavedReport
	 * 	 Test :
	 * 	 Role:China Accountant and target language
	 * 	 Parameters:
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
	 * 	 SUBSIDIARY (子公司):中国 Income
	 * 	 PERIOD(期间):Jun 2017
	 * 	 UNIT(单位):Unit(元)
	 * 	 LOCATION(地点): 中国 Income华北区
	 * 	 DEPARTMENT(部门): All(Empty)
	 * 	 CLASS(类别):All(Empty)
	 * ---- Expected Result ---
	 *  Report Header 4th line:
	 *  Left:LOCATION(地点):中国 Income华北区
	 *
	 *
	 * 	 After Test : Enable Features: Location, Department, Class
	 * @author Molly Feng
	 */
	@Test
	@Category([OW.class, P3.class])
	void test_case_8_2_1_2() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseData = testData["test_case_8_2_1_2"]
		def caseFilter = testData["test_case_8_2_1_2"].filter
		def expResult = testData["test_case_8_2_1_2"].expectedResult

		// Get Saved Report Data as Admin role in English
		//switchToRole(administrator)
		if (context.getUserLanguage() != "en_US") {
			changeLanguageToEnglish()
			changflag = true
		}
		def savedReportData = getSavedReport(caseData.report)
		 //switchToRole(accountant)
		if (changflag) {
			changeLanguageToChinese()
		}
		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(incomeStatementPage.getXPATH_BTN_EXP_PDF())
		Thread.sleep(10 * 1000)
		// get the Report Data from the page
		def actualReportData = getISReport(caseData.report)

		// check the Report
		checkReportHeader(expResult)
		checkReportTable(actualReportData, savedReportData)
	}

	/**
	 * @desc  Case3: Location:中国 Income华北区;Department:Income生产部;Class:All
	 *        Check the Report Header and Report Table Contents
	 *        it will contain the 2 classification parameters in Report header
	 *        and the report amount will be same as saved report in Finalcial Row[一、营业收入]
	 *
	 * 	 @case 8.2.1.3
	 * 	 Before Test: Enable Features: Location, Department, Class
	 * 	 ChangeToEnglish to GetSavedReport
	 * 	 Test :
	 * 	 Role:China Accountant and target language
	 * 	 Parameters:
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
	 * 	 SUBSIDIARY (子公司):中国 Income
	 * 	 PERIOD(期间):Jun 2017
	 * 	 UNIT(单位):Unit(元)
	 * 	 LOCATION(地点): 中国 Income华北区
	 * 	 DEPARTMENT(部门): Income生产部
	 * 	 CLASS(类别):All(Empty)
	 * ---- Expected Result ---
	 *  Report Header 4th line:
	 *  Left:LOCATION(地点):中国 Income华北区
	 *  Center:DEPARTMENT(部门):Income生产部
	 *
	 * 	 After Test : Enable Features: Location, Department, Class
	 * @author Molly Feng
	 */
	@Test
	@Category([OW.class, P3.class])
	void test_case_8_2_1_3() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseData = testData["test_case_8_2_1_3"]
		def caseFilter = testData["test_case_8_2_1_3"].filter
		def expResult = testData["test_case_8_2_1_3"].expectedResult

		// Get Saved Report Data as Admin role in English
		//switchToRole(administrator)
		if (context.getUserLanguage() != "en_US") {
			changeLanguageToEnglish()
			changflag = true
		}
		def savedReportData = getSavedReport(caseData.report)
		 //switchToRole(accountant)
		if (changflag) {
			changeLanguageToChinese()
		}
		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(incomeStatementPage.getXPATH_BTN_EXP_PDF())
		Thread.sleep(10 * 1000)
		// get the Report Data from the page
		def actualReportData = getISReport(caseData.report)

		// check the Report
		checkReportHeader(expResult)
		checkReportTable(actualReportData, savedReportData)
	}
	/**
	 * @desc  Case4: Location:中国 Income华北区;Department:Income生产部;Class:智能手机
	 *        Check the Report Header and Report Table Contents
	 *        it will contain the 3 classification parameters in Report header
	 *        and the report amount will be same as saved report in Finalcial Row[一、营业收入]
	 *
	 * 	 @case 8.2.1.4
	 * 	 Before Test: Enable Features: Location, Department, Class
	 * 	 ChangeToEnglish to GetSavedReport
	 * 	 Test :
	 * 	 Role:China Accountant and target language
	 * 	 Parameters:
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
	 * 	 SUBSIDIARY (子公司):中国 Income
	 * 	 PERIOD(期间):Jun 2017
	 * 	 UNIT(单位):Unit(元)
	 * 	 LOCATION(地点):中国 Income华北区
	 * 	 DEPARTMENT(部门):Income生产部
	 * 	 CLASS(类别):智能手机
	 * ---- Expected Result ---
	 *  Report Header 4th line:
	 *  Left:LOCATION(地点):中国 Income华北区
	 *  Center:DEPARTMENT(部门):Income生产部
	 *  Right:CLASS(类别):智能手机
	 * 	 After Test : Enable Features: Location, Department, Class
	 * @author Molly Feng
	 */
	@Test
	@Category([OW.class, P0.class])
	void test_case_8_2_1_4() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseData = testData["test_case_8_2_1_4"]
		def caseFilter = testData["test_case_8_2_1_4"].filter
		def expResult = testData["test_case_8_2_1_4"].expectedResult

		// Get Saved Report Data as Admin role in English
		//switchToRole(administrator)
		if (context.getUserLanguage() != "en_US") {
			changeLanguageToEnglish()
			changflag = true
		}
		def savedReportData = getSavedReport(caseData.report)
		 //switchToRole(accountant)
		if (changflag) {
			changeLanguageToChinese()
		}
		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(incomeStatementPage.getXPATH_BTN_EXP_PDF())
		Thread.sleep(10 * 1000)
		// get the Report Data from the page
		def actualReportData = getISReport(caseData.report)

		// check the Report
		checkReportHeader(expResult)
		checkReportTable(actualReportData, savedReportData)
	}
	/**
	 * @desc  Case5: Location:;Department:Income生产部;Class:智能手机
	 *        Check the Report Header and Report Table Contents
	 *        it will contain the 2 classification parameters in Report header
	 *        and the report amount will be same as saved report in Finalcial Row[一、营业收入]
	 *
	 * 	 @case 8.2.1.5
	 * 	 Before Test: Enable Features: Location, Department, Class
	 * 	 ChangeToEnglish to GetSavedReport
	 * 	 Test :
	 * 	 Role:China Accountant and target language
	 * 	 Parameters:
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
	 * 	 SUBSIDIARY (子公司):中国 Income
	 * 	 PERIOD(期间):Jun 2017
	 * 	 UNIT(单位):Unit(元)
	 * 	 LOCATION(地点):All
	 * 	 DEPARTMENT(部门):Income生产部
	 * 	 CLASS(类别):智能手机
	 * ---- Expected Result ---
	 *  Report Header 4th line:
	 *  Left:DEPARTMENT(部门):Income生产部
	 *  Center:CLASS(类别):智能手机
	 * 	 After Test Suite: Enable Features: Location, Department, Class
	 * @author Molly Feng
	 */
	@Test
	@Category([OW.class, P1.class])
	void test_case_8_2_1_5() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseData = testData["test_case_8_2_1_5"]
		def caseFilter = testData["test_case_8_2_1_5"].filter
		def expResult = testData["test_case_8_2_1_5"].expectedResult

		// Get Saved Report Data as Admin role in English
		//switchToRole(administrator)
		if (context.getUserLanguage() != "en_US") {
			changeLanguageToEnglish()
			changflag = true
		}
		def savedReportData = getSavedReport(caseData.report)
		 //switchToRole(accountant)
		if (changflag) {
			changeLanguageToChinese()
		}
		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(incomeStatementPage.getXPATH_BTN_EXP_PDF())
		Thread.sleep(10 * 1000)
		// get the Report Data from the page
		def actualReportData = getISReport(caseData.report)

		// check the Report
		checkReportHeader(expResult)
		checkReportTable(actualReportData, savedReportData)
	}
	/**
	 * @desc  Case6: Location:;Department:All;Class:智能手机
	 *        Check the Report Header and Report Table Contents
	 *        it will contain the 1 classification parameters in Report header
	 *        and the report amount will be same as saved report in Finalcial Row[一、营业收入]
	 *
	 * 	 @case 8.2.1.6
	 * 	 Before Test: Enable Features: Location, Department, Class
	 * 	 ChangeToEnglish to GetSavedReport
	 * 	 Test :
	 * 	 Role:China Accountant and target language
	 * 	 Parameters:
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
	 * 	 SUBSIDIARY (子公司):中国 Income
	 * 	 PERIOD(期间):Jun 2017
	 * 	 UNIT(单位):Unit(元)
	 * 	 LOCATION(地点):All
	 * 	 DEPARTMENT(部门):All
	 * 	 CLASS(类别):智能手机
	 * ---- Expected Result ---
	 *  Report Header 4th line:
	 *  Left:CLASS(类别):智能手机
	 *
	 * 	 After Test Suite: Enable Features: Location, Department, Class
	 * @author Molly Feng
	 */
	@Test
	@Category([OW.class, P1.class])
	void test_case_8_2_1_6() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseData = testData["test_case_8_2_1_6"]
		def caseFilter = testData["test_case_8_2_1_6"].filter
		def expResult = testData["test_case_8_2_1_6"].expectedResult

		// Get Saved Report Data as Admin role in English
		//switchToRole(administrator)
		if (context.getUserLanguage() != "en_US") {
			changeLanguageToEnglish()
			changflag = true
		}
		def savedReportData = getSavedReport(caseData.report)
		 //switchToRole(accountant)
		if (changflag) {
			changeLanguageToChinese()
		}
		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(incomeStatementPage.getXPATH_BTN_EXP_PDF())
		Thread.sleep(4 * 1000)
		// get the Report Data from the page
		def actualReportData = getISReport(caseData.report)

		// check the Report
		checkReportHeader(expResult)
		checkReportTable(actualReportData, savedReportData)
	}

	/**
	 * @desc Check the Parameters Labels of Location, Department, Class
	 *        Check the Parameters default value of Location, Department, Class
	 *        Check the list of value for Location, department, class
	 *
	 * 	 @case 8.1.1.1 Check the Parameters Labels of Location, Department, Class
	 * 	 @case 8.1.1.2 Check the list of value for Location, department, class
	 * 	 Before Test: Enable Features: Location, Department, Class
	 *
	 * 	 Test :
	 * 	 Role:China Accountant and target language
	 * 	 Parameters:
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
	 * 	 SUBSIDIARY (子公司):中国 Income
	 * ---- Expected Result ---
	 * @case 8.1.1.1 Confirm Parameters Labels
	 * @case 8.1.1.2 Confirm linkage between subsidiary and Parameters
	 *
	 *
	 * 	 After Test : Enable Features: Location, Department, Class
	 * @author Molly Feng
	 */
	@Test
	@Category([OW.class, P0.class])
	void test_case_8_1_1_1() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseFilter = testData["test_case_8_1_1_1"].filter
		def expResult = testData["test_case_8_1_1_1"].expectedResult

		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)

		// Check Default value of Parameters:Location, Department, Class
		chechAllClassificationDisplay(expResult)
		checkDefaultValueForClassification(expResult)
		// check the Parameters
		checkLOVForClassification(expResult)

	}


	/**
	 * @desc  Check Export Excel
	 *        Case4: Location:中国 Income华北区;Department:Income生产部;Class:智能手机
	 *        Check the Report Header and Report Table Contents
	 *        it will contain the 3 classification parameters in Report header
	 *        and the report amount will be same as saved report in Finalcial Row[一、营业收入]
	 *
	 * 	 @case 8.2.2.1 Check Export Excel in Chinese &English
	 * 	 Before Test: Enable Features: Location, Department, Class
	 * 	 ChangeToEnglish to GetSavedReport
	 * 	 Test :
	 * 	 Role:China Accountant and target language
	 * 	 Parameters:
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
	 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
	 * 	 SUBSIDIARY (子公司):中国 Income
	 * 	 PERIOD(期间):Jun 2017
	 * 	 UNIT(单位):Unit(元)
	 * 	 LOCATION(地点):中国 Income华北区
	 * 	 DEPARTMENT(部门):Income生产部
	 * 	 CLASS(类别):智能手机
	 * ---- Expected Result ---
	 *  Report Header 4th line:
	 *  Left:LOCATION(地点):中国 Income华北区
	 *  Center:DEPARTMENT(部门):Income生产部
	 *  Right:CLASS(类别):智能手机
	 * 	 After Test : Enable Features: Location, Department, Class
	 * @author Molly Feng
	 */
	@Test
	@Category([OW.class, P3.class])
	void test_case_8_2_2_1() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseFilter = testData["test_case_8_2_1_4"].filter
		def expResult = testData["test_case_8_2_1_4"].expectedResult

		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(incomeStatementPage.getXPATH_BTN_EXP_PDF())
		Thread.sleep(10 * 1000)
		clickExportExcel()

		def downloadsPath = context.downloadFilePath() + expResult.fileNameExcel
		File excelFile = new File(downloadsPath)
		checkTrue("Excel downloaded",excelFile.exists())
		excelFile.delete();

	}
/**
 * @desc  Check Export PDF
 *        Case4: Location:中国 Income华北区;Department:Income生产部;Class:智能手机
 *        Check the Report Header and Report Table Contents
 *        it will contain the 3 classification parameters in Report header
 *        and the report amount will be same as saved report in Finalcial Row[一、营业收入]
 *
 * 	 @case 8.2.2.2 Check Export PDF in Chinese &English
 * 	 Before Test: Enable Features: Location, Department, Class
 * 	 ChangeToEnglish to GetSavedReport
 * 	 Test :
 * 	 Role:China Accountant and target language
 * 	 Parameters:
 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
 * 	 SUBSIDIARY (子公司):中国 Income
 * 	 PERIOD(期间):Jun 2017
 * 	 UNIT(单位):Unit(元)
 * 	 LOCATION(地点):中国 Income华北区
 * 	 DEPARTMENT(部门):Income生产部
 * 	 CLASS(类别):智能手机
 * ---- Expected Result ---
 *  Report Header 4th line:
 *  Left:LOCATION(地点):中国 Income华北区
 *  Center:DEPARTMENT(部门):Income生产部
 *  Right:CLASS(类别):智能手机
 * 	 After Test : Enable Features: Location, Department, Class
 * @author Molly Feng
 */
	@Test
	@Category([OW.class, P3.class])
	void test_case_8_2_2_2() {

		if (!context.isOneWorld()) {
			// Skip the test case when running in SI env
			return
		}
		def caseFilter = testData["test_case_8_2_1_4"].filter
		def expResult = testData["test_case_8_2_1_4"].expectedResult

		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		//waitForPageToLoad()
		//waitForElement(incomeStatementPage.getXPATH_BTN_EXP_PDF())
		Thread.sleep(3 * 1000)


		def downloadsPath = context.downloadFilePath() + expResult.fileNamePDF
		File pdfFile = new File(downloadsPath)
		println(downloadsPath)
		pdfFile.deleteOnExit()
		clickExportPDF()
		checkTrue("Excel downloaded",pdfFile.exists())
		pdfFile.deleteOnExit()

	}

	def chechAllClassificationDisplay(expResult) {
		incomeStatementPage.with{
			if (!expResult.locations) {
				checkFalse("Check Location not visible", isExist(getXPATH_PARAM_LOCATION()))
			} else {
				checkTrue("Check Location  visible", isExist(getXPATH_PARAM_LOCATION()))
			}
			if (!expResult.departments) {
				checkFalse("Check Department not visible", isExist(getXPATH_PARAM_DEPARTMENT()))
			} else {
				checkTrue("Check Department  visible", isExist(getXPATH_PARAM_DEPARTMENT()))
			}
			if (!expResult.classes) {
				checkFalse("Check Class not visible", isExist(getXPATH_PARAM_CLASS()))
			} else {
				checkTrue("Check Class  visible", isExist(getXPATH_PARAM_CLASS()))
			}
		}
	}
	def checkDefaultValueForClassification(expResult) {

		checkAreEqual("Check Default Value : Location", incomeStatementPage.getLocation().trim(), expResult.defaultLocation.trim())
		checkAreEqual("Check Default Value : Department", incomeStatementPage.getDepartment().trim(), expResult.defaultDepartment.trim())

		checkAreEqual("Check Default Value : Class", incomeStatementPage.getMyClass().trim(), expResult.defaultClass.trim())
	}
	def checkLOVForClassification(expResult) {
		if (expResult.locations) {
			def locations = expResult.locations
			locations.each { expLocation ->
				def actualLocations = incomeStatementPage.getLocationOptions()
				def loc = actualLocations.find { actLoc -> actLoc.contains(expLocation)
				}
				if (loc) checkTrue("Check List of Value: Location  $expLocation  existed", true)
			}
		}
		if (expResult.departments) {
			def departments = expResult.departments
			departments.each { expDepartment ->
				def actualDepartments = incomeStatementPage.getDepartmentOptions()
				def dep = actualDepartments.find { actDep -> actDep.contains(expDepartment)
				}
				if (dep) checkTrue("Check List of Value: Department  $expDepartment  existed", true)
			}
		}
		if (expResult.classes) {
			def expClasses = expResult.classes
			expClasses.each { expClass ->
				def classesOptions = incomeStatementPage.getClassOptions()
				def classvalue = classesOptions.find { actClass -> actClass.contains(expClass)
				}
				if (classvalue) checkTrue("Check List of Value: Department  $expClass  existed", true)
			}
		}

	}
	def checkReportHeader(expResult) {
		if (expResult.LabelIn3rdLine) {
			checkAreEqual("Check 3rd line label: Prepared By!", incomeStatementPage.getReportHeaderPreparedByText(), expResult.LabelIn3rdLine.preparedBy)
			checkAreEqual("Check 3rd line label: Period!", incomeStatementPage.getReportHeaderPeriodText(), expResult.LabelIn3rdLine.period)
			checkAreEqual("Check 3rd line label: Currency!", incomeStatementPage.getReportHeaderCurrencyText(), expResult.LabelIn3rdLine.currency)
		}
		if (expResult.LabelIn4thLine) {
			if (expResult.LabelIn4thLine.reportHeaderL4Left) checkAreEqual("Check 4th line label on the Left!", incomeStatementPage.getReportHeaderL4LeftText(), expResult.LabelIn4thLine.reportHeaderL4Left)
			if (expResult.LabelIn4thLine.reportHeaderL4Center) checkAreEqual("Check 4th line label on the Center!", incomeStatementPage.getReportHeaderL4CenterText(), expResult.LabelIn4thLine.reportHeaderL4Center)
			if (expResult.LabelIn4thLine.reportHeaderL4Right) checkAreEqual("Check 4th line label on the Right!", incomeStatementPage.getReportHeaderL4RightText(), expResult.LabelIn4thLine.reportHeaderL4Right)
		}
	}
	def checkReportTable(actualReportData, expSavedReportData) {

		// check the Report table
		checkAreEqual("Compare the size of the actual Report table and saved custom report", actualReportData.size(), expSavedReportData.size())
		for (int i = 0; i < actualReportData.size(); i++) {
			checkTrue("Compare Items",actualReportData[i].get(incomeStatementPage.REPORT_TABLE_MAP_KEY_ITEMS).contains(expSavedReportData[i].get(incomeStatementPage.REPORT_TABLE_MAP_KEY_ITEMS).trim()) )
			checkAreEqual("Compare Current Amount",actualReportData[i].get(incomeStatementPage.REPORT_TABLE_MAP_KEY_CURRENT_AMOUNT),expSavedReportData[i].get(incomeStatementPage.REPORT_TABLE_MAP_KEY_CURRENT_AMOUNT).trim().substring(1) )
			checkAreEqual("Compare Prior Amount",actualReportData[i].get(incomeStatementPage.REPORT_TABLE_MAP_KEY_PRIOR_AMOUNT), expSavedReportData[i].get(incomeStatementPage.REPORT_TABLE_MAP_KEY_PRIOR_AMOUNT).trim().substring(1) )
		}
	}

}

