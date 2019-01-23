package com.netsuite.chinalocalization.vat

import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser
import com.netsuite.testautomation.junit.TestOwner
import net.qaautomation.common.Occurrence
import org.junit.Test
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P0
import org.junit.experimental.categories.Category
@TestOwner("molly.feng@oracle.com")
class VATEditPageDisplayTest extends VATEditPageTestSuite{

	def pathToTestDataFiles() {
		return [
				"zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATEditPageDisplayTest_zh_CN.json",
				"enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATEditPageDisplayTest_en_US.json"
		]
	}
	/**
	 * @desc Check Edit page initial load
	 *   Check Labels and column names of List1: Primary Information
	 *   Check Labels and column names of List2: Items
	 * @case 19.1.1.1 -
	 * @case 19.1.1.2
	 * @case 19.1.1.3
	 * @case 19.1.1.4
	 * @case 19.1.1.6
	 * @case 19.1.1.7
	 * @case 19.1.1.9 no item displayed in item list
	 * @author Molly Feng
	 * @Non-regression:
	 */
	@Test
	@Category([OWAndSI.class, P0.class])
	void test_case_19_1() {
		def caseFilter = testData["test_case_19_1"].filter
		def expResult = testData["test_case_19_1"].labels
		def tranHeader = testData["tranHeader"]
		def itemlabel = testData["itemHeader"]
		navigateToPortalPage()
		waitForPageToLoad()

		if (context.isOneWorld()) {
			asDropdownList(locator: locators.subsidiary).selectItem(caseFilter.subsidiary)
		}
		asDropdownList(locator: locators.invoiceType).selectItem(caseFilter.invoicetype)
		context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
		context.setFieldWithValue("custpage_dateto", caseFilter.dateto)

		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		clickEditWithWaitingForMerge()

		checkEidtPageDisplay(expResult)

		checkItemsList(itemlabel, expResult.items)
	}
/**
 * @desc Original Transaction (not splited, not merged) will go edit page
 *   Original Transaction (not splited, not merged) will go edit page
 *   Check Transaction contents of in the list1: transaction
 *   Check Item contents in the List2: Items
 * @case 19.3.1.1 Original Transaction (not splited, not merged)
 * @case 19.1.1.5 Check Transaction contents of in the list1: transaction
 * @case 19.1.1.8 Check Item contents in the List2: Items
 * @case 19.1.2.1 Check Linkage in Edit page IN10701
 * @case 19.1..2 Check Linkage in Edit page CS10701
 *
 * Before test: Set CHINA MAX VAT INVOICE AMOUNT : null
 *
 * @author Molly Feng
 * @Regression: Vat Feature
 */
	@Test
	@Category([OWAndSI.class, P0.class])
	void test_case_19_3_1_1() {
		def caseFilter = testData["test_case_19_3_1_1"].filter
		def expResult = testData["test_case_19_3_1_1"].expectedResult
		def tranHeader = testData["tranHeader"]
		def itemlabel = testData["itemHeader"]

		// before test: set CHINA MAX VAT INVOICE AMOUNT : null
		if (context.isOneWorld()) {
			//set CHINA MAX VAT INVOICE AMOUNT : null in subsidiary page
			setSubMaxChinaVATInvoiceAmount(caseFilter.subsidiary, "")
		} else {
			//set CHINA MAX VAT INVOICE AMOUNT : null in Company Info page
			setCompanyMaxChinaVATInvoiceAmount("")
		}

		navigateToPortalPage()

		if (context.isOneWorld()) {
			asDropdownList(locator: locators.subsidiary).selectItem(caseFilter.subsidiary)
		}
		asDropdownList(locator: locators.invoiceType).selectItem(caseFilter.invoicetype)
		context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
		context.setFieldWithValue("custpage_dateto", caseFilter.dateto)
		//context.setFieldWithValue("", caseFilter.customer)
		//asClick(locators.tranTypeIn);
		//asClick(locators.tranType);
		context.setFieldWithValue("custpage_documentnumberfrom_formattedValue", caseFilter.docNum)
		context.setFieldWithValue("custpage_documentnumberto_formattedValue", caseFilter.docNum)

//		def customerNames = ['1 CN Automation Customer','2 Cash Flow BU Customer']
//		multiSelect(".//*[@name=\'custpage_customer\']",customerNames)

		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		clickEditWithWaitingForMerge()

		// run test case 19.1.2.1   IN 10701
		def index = getTranIndexByValue(caseFilter.customer, caseFilter.tranType, caseFilter.docNum)
		checkTransactionInformation(index, expResult)
		Thread.sleep(5 * 1000)
		asClickTransaction(index)
		Thread.sleep(10 * 1000)
		checkItemsList(itemlabel, expResult.items)
		Thread.sleep(5 * 1000)

		// run test case 19.1.2.2
		caseFilter = testData["test_case_19_1_2_2"].filter
		expResult = testData["test_case_19_1_2_2"].expectedResult
		// click CS 10701
		index = getTranIndexByValue(caseFilter.customer, caseFilter.tranType, caseFilter.docNum)

		checkTransactionInformation(index, expResult)
		Thread.sleep(5 * 1000)
		asClickTransaction(index)
		Thread.sleep(10 * 1000)
		checkItemsList(itemlabel, expResult.items)

	}

	def checkEidtPageDisplay(expResult) {

		// check transaction header list: 9 columns
		def tranHeader = testData["tranHeader"]
		def itemlabel = testData["itemHeader"]
		def tranSublist = asSublist(editLocators.tranList)
		for( key in tranHeader.keySet()){
			assertTrue("Check List1:Primary Information Column Header exist check ：" + tranHeader.get(key) ,  tranSublist.doesColumnExist(tranHeader.get(key)))
		}

		assertTrue("Check Edit Page title", context.isTextVisible(expResult.title))
		//assertAreEqual("Cancel button label is wrong", context.getFieldLabel("custpage_cancel"), expResult.cancelButton)
		assertTrue("Check List1:Primary Information Label", context.isTextVisible(expResult.primaryInforHeader))

		// check Items list: 8 columns
		def itemSublist = asSublist(editLocators.itemList)
		assertTrue("Check List2:Items Label", context.isTextVisible(expResult.itemsHeader))
		for( key in itemlabel.keySet()){
			assertTrue("Check List2:Items Column Header exist check ：" + itemlabel.get(key) ,  itemSublist.doesColumnExist(itemlabel.get(key)))
		}

	}
	def setCompanyMaxChinaVATInvoiceAmount( amount) {

		//switchToRole(administrator)
		companyInformationPage.navigateToCompanyInfoPage()
		companyInformationPage.setMaxVATInvAmount(amount)
		companyInformationPage.clickSISave()
		 //switchToRole(accountant)
	}
	def setSubMaxChinaVATInvoiceAmount(subsidiaryName, amount) {
		//switchToRole(administrator)
		subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName)
		subsidiaryPage.setMaxVATInvAmount(amount)
		subsidiaryPage.clickSave()
		 //switchToRole(accountant)
	}
}
