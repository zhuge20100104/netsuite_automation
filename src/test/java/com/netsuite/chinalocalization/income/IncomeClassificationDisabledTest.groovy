package com.netsuite.chinalocalization.income

import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("molly.feng@oracle.com")
class IncomeClassificationDisabledTest extends IncomeAppTestSuite{
	def pathToTestDataFiles() {
		return [
				"zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeClassificationDisabledTest_zh_CN.json",
				"enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\income\\data\\IncomeClassificationDisabledTest_en_US.json"
		]
	}


/**
 * @desc Check the Parameters: Location, Department, Class are not visible when all classification disabled
 *@case 8.1.2.1   Run this case in SI only
 *  Note: test this case in Release SI_auto_team2 env
 * 	 Before Test: Disable Features: Location, Department, Class
 *
 * 	 Test :
 * 	 Role:China Accountant and target language
 * 	 Parameters:
 * 	 INCOME STATEMENT NAME *(利润表名称 *) :中国利润表测试Month  -- CN
 * 	 INCOME STATEMENT NAME *(利润表名称 *) :China Income Statement Testing Month  -- EN
 * 	 SUBSIDIARY (子公司):中国 Income
 * ---- Expected Result ---
 * @case 8.1.2.1 Confirm Parameters :Location, Department, Class are not visible
 *  skip this case
 *
 * 	 After Test : Enable Features: Location, Department, Class
 * @author Molly Feng
 */
	@Test
	@Category([SI.class, P3.class])
	void test_case_8_1_2_1() {

		if (context.isOneWorld()) {
			// Skip the test case when running in OW env
			return
		}

		// Feature： Location, Department, Class are diabled
		def expResult = testData["test_case_8_1_2_1"].expectedResult
		// Disable Features as Admin role
		//switchToRole(administrator)
		enableFeaPage.navigateToURL()
      //Temporarily we cannot disable Location features in Sprint SI env, but can test this case in Release SI env
		if (context.isFeatureEnabled("LOCATIONS")) {
			enableFeaPage.disbleLocations()

		}
		if (context.isFeatureEnabled("DEPARTMENTS")) {
			enableFeaPage.disbleDepartments()
		}
		if  (context.isFeatureEnabled("CLASSES")) {
			enableFeaPage.disbleClasses()
		}
		enableFeaPage.clickSave()
		 //switchToRole(accountant)

		// Navigate to Income Statement Page
		navigateToPortalPage()
		waitForPageToLoad()

		// check the Parameters not visible
		chechAllClassificationDisplay(expResult)

		// after test, enable all features in case teardown
	}

	def chechAllClassificationDisplay(expResult) {
		incomeStatementPage.with{
			if (!expResult.locations) {
				assertFalse("Check Location not visible", isExist(getXPATH_PARAM_LOCATION()))
			} else {
				assertTrue("Check Location  visible", isExist(getXPATH_PARAM_LOCATION()))
			}
			if (!expResult.departments) {
				assertFalse("Check Department not visible", isExist(getXPATH_PARAM_DEPARTMENT()))
			} else {
				assertTrue("Check Department  visible", isExist(getXPATH_PARAM_DEPARTMENT()))
			}
			if (!expResult.classes) {
				assertFalse("Check Class not visible", isExist(getXPATH_PARAM_CLASS()))
			} else {
				assertTrue("Check Class  visible", isExist(getXPATH_PARAM_CLASS()))
			}
		}
	}


}

