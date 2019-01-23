package com.netsuite.chinalocalization.vat

import com.netsuite.testautomation.junit.TestOwner
//import jdk.nashorn.internal.ir.annotations.Ignore
import org.eclipse.jetty.util.ajax.JSON
import org.jetbrains.annotations.NotNull
import org.junit.Rule
import org.junit.Test
import com.netsuite.common.OWAndSI
import com.netsuite.common.P2
import com.netsuite.common.P1
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("molly.feng@oracle.com")
class VATEditValidationTest extends VATEditPageTestSuite{
	@Rule
	public  TestName name = new TestName()
	private  def caseData
	private  def caseFilter
	private  def expResult
	private  def records
	def initData(replaceData){
		println("Start: " + name.getMethodName())
		caseData = testData.get(name.getMethodName())
		if (replaceData) {
			caseData = testData.get(replaceData)
		}
		caseFilter = caseData.filter
		if ("expectedResult" in caseData) expResult= caseData.expectedResult

		// insert test data if have any, some test case use data in other case ：test_case_19_4_2_5_3
		if (caseData.data) {
// 2018/8/24 move groovy data to js
//			switchToRole(administrator)
//			 //comments create record 2018/5/16
//			 records = record.createRecord(caseData.data)
//			switchToRole(accountant)
		}

	}
	def pathToTestDataFiles() {
		return [
				"zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATEditValidationTest_zh_CN.json",
				"enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATEditValidationTest_en_US.json"
		]
	}
	/**
	 * @desc If the selected transactions have total amount exceed max amount.
	 *   Check error message
	 * @case 19.4.2.1
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_1() {
		initData()
		def tranHeader = testData["tranHeader"]

		// need to set Max China VAT Invoice Amount before test
		//switchToRole(administrator)
		setMaxChinaVATInvoiceAmount(caseData.param)
		 //switchToRole(accountant)

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",caseData.data.size(), temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}

		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation: total amount exceeds Max China VAT Invoice Amount", context.isTextVisible(expResult.errMsg))

		clickOKInErrMsgBox()
		// reset Max China VAT Invoice Amount
		//switchToRole(administrator)
		// set Max China VAT Invoice Amount
		setMaxChinaVATInvoiceAmount(caseData.paramReset)

	}

/**
 * @desc If the selected transactions have more than 8 lines but not enable sales list.
 * (8 lines is after consolidation, include discount, handling and shipping. Consider grouping same items)
 * @case 19.4.2.2 Consolidation Transaction [IN10904, IN10905] > 8 lines;
 *  Enable Sales List: No
 *  Not check Group Same Item
 *  IN10904 - Item1:  has discount
 *            Item2:  has discount
 *            Shipping Cost
 *            handling cost
 *  IN10905 - Item1:  has discount
 *            Item2:  has discount
 *            Shipping Cost
 *            handling cost
 * @case 19.4.2.3 Consolidation Transaction [IN10904, IN10905] > 8 lines;  - Not available yet
 *  Enable Sales List: No
 *  check Group Same Item
 * @author Molly Feng
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_2() {
		initData()
		def tranHeader = testData["tranHeader"]

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",caseData.data.size(), temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}

		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation: Consolidation Transaction > 8 lines; disable Sales List; Not check Group Same Item", context.isTextVisible(expResult.errMsg))

		//Case 19.4.2.3
		// click OK in error message dialog
		clickOKInErrMsgBox()
		// Check Group Same Item
		checkGroupSameItem()

		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation: Consolidation Transaction > 8 lines; disable Sales List; Check Group Same Item", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}

	/**
	 * @desc If user select both negative and postive transactions to merge, but does not enable "Group same items"
	 *
	 * @case 19.4.2.4  try to merge both negative and positive transactions
	 * IN20908 - Item1:  quantity:1, amount 5600.99
	 *           Item2:  quantity:2, amount 10600.99*2
	 * CS20909 - Item1: quantity:1, amount 5600.99
	 *           Item3: quantity:1, amount 15600.99
	 * CM20910 has same information sheet number - Item1: quantity:3, amount 5600.99*3
	 * CR20911 has same information sheet number- Item1: quantity:1, amount 5600.99
	 *                                          - Item2: quantity:1, amount 10600.99
	 *
	 *  consolidated transaction - Item1:  quantity -2
	 *                             Item2:  quantity 1
	 *                             Item3:  quantity 1
	 * @case 19.4.2.5 try to merge both negative and positive transactions; but has items those amount < 0
	 *  check Group Same Item
	 * @author Molly Feng
	 * @Modified by Molly : doc number duplicated with test_case_19_4_2_5_2
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_4() {
		initData()
		def tranHeader = testData["tranHeader"]

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",caseData.data.size(), temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}

		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation: Merge both Negative and positive transaction; not Check Group Same Items", context.isTextVisible(expResult.errMsg1))

		//Case 19.4.2.5
 		//click OK in error message dialog
		clickOKInErrMsgBox()
		// Check Group Same Item
		checkGroupSameItem()

		clickMerge()
		Thread.sleep(2 * 1000)

		// check Positive and Negative Transaction Merge
		assertTrue("check validation: Merge both Negative and positive transaction; has negative items", context.isTextVisible(expResult.errMsg2))
		clickOKInErrMsgBox()
	}

	/**
	 * @desc If user select both negative and postive transactions to merge, but does not enable "Group same items"
	 *
	 * @case 19.4.2.5-2  try to merge both negative and positive transactions
	 * Check Group same items
	 * Considering shipping cost
	 * IN10908 - Item1:  quantity:1, amount 5600.99
	 *           Shipping cost: 200
	 * CM10910 has same information sheet number - Item1: quantity:3, amount 5600.99*3
	 *           Shipping cost: 300
	 *  ----
	 *  Consolidated transaction : shipping cost < 0
	 *
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_5_2() {
		initData()
		def tranHeader = testData["tranHeader"]

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",caseData.data.size(), temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}

		Thread.sleep(1 * 1000)
		// Check Group Same Item
		checkGroupSameItem()
		Thread.sleep(1 * 1000)
		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation: ${expResult.errMsg}", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}

	/**
	 * @desc If user select both negative and postive transactions to merge, but does not enable "Group same items"
	 *
	 * @case 19.4.2.5-3  try to merge both negative and positive transactions
	 * Check Group same items
	 * Considering handling cost
	 * CS10909 - Item1: quantity:1, amount 5600.99
	 *           handling cost: 60
	 * CR10911 has same information sheet number- Item1: quantity:1, amount 5600.99
	 *           handling cost: 100
	 * ----
	 *  Consolidated transaction : handling cost < 0
	 *
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_5_3() {
		initData("test_case_19_4_2_5_2")
		// use the same test data in 19.4.2.5-2


		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",caseData.data.size(), temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}
		Thread.sleep(3 * 1000)

		// Check Group Same Item
		checkGroupSameItem()
		Thread.sleep(2 * 1000)
		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation: Merge both Negative and positive transaction; handling cost <0", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}

/**
 * @desc Consolidate Single Transaction
 * @case 19.4.2.6-1
 *  Enable Sales List: Yes
 *  Not Check Group Same Item
 *   IN10912 (Same Tax Rate) - Item1:  quantity:2, amount 5600.99*2, discount 20%
 *    Item2:  quantity:2, amount 10600.99*2, no discount
 *    Item1:  quantity:1, amount 5600.99, discount -200
 *    Item1:  quantity:1, amount 5600.99, no discount
 * @case 19.4.2.6-2 Check Group Same Item  Merge Single transaction [IN10912] : that every line is different (item, unit price, unit discount, tax rate),
 * Check Group Same Item
 *
 * It will show error message: No transactions available to merge.
 * @author Molly Feng
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_6() {
		initData()
		def tranHeader = testData["tranHeader"]

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)


		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",caseData.data.size(), temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}

		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation:Consolidate Single Transaction ", context.isTextVisible(expResult.errMsg1))

		// Case 19.4.2.6-2
		// click OK in error message dialog
		clickOKInErrMsgBox()
		// Check Group Same Item
		checkGroupSameItem()

		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation:Consolidate Single Transaction；No same items ", context.isTextVisible(expResult.errMsg2))
		clickOKInErrMsgBox()
	}
/**
 * @desc Consolidate Single Transaction
 * @case 19.4.2.6-2-2 Check Group Same Item  Merge Single transaction [IN10912] : that every line is different (item, unit price, unit discount, tax rate),
 * Check Group Same Item
 * IN10930 (Same Tax Rate, no discount, different unit price)-
 *   Item1:  quantity:1, amount 6000
 *   Item1:  quantity:2, amount 6000
 *
 * It will show error message: No transactions available to merge.
 * @author Molly Feng
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_6_2_2() {
		initData()
		def tranHeader = testData["tranHeader"]

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",caseData.data.size(), temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}
		checkGroupSameItem()
		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation:Consolidate Single Transaction;no same items:different unit price", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}
	/**
	 * @desc Consolidate Single Transaction
	 * @case 19.4.2.6-3
	 *  check Group Same Item
	 *   IN10701 (Same Item with different Tax Rate) - Item1:  quantity:1, amount 5600.99, taxRate 0.17
	 *    Item2:  quantity:1, amount 10600.99, no discount
	 *    Item3:  quantity:1, amount 13500.99, no discount
	 *    Item1:  quantity:1, amount 5600.99,taxRate 0.13
	 * It will show error message: No transactions available to merge.
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_6_3() {
		initData()

		// existing test data ， no need to insert test data

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal", 1, temResult.size())
		clickEditWithWaitingForMerge()
		asClickTransCheckbox(1)

		Thread.sleep(2 * 1000)
		// Case 19.4.2.6-3
		// Check Group Same Item
		checkGroupSameItem()
		Thread.sleep(2 * 1000)
		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation:Consolidate Single Transaction；No same items(Same item with different tax rate)", context.isTextVisible(expResult.errMsg))
		//clickOKInErrMsgBox()
	}

	/**
	 * @desc Consolidate Single Transaction
	 * @case 19.4.2.6-4
	 *  check Group Same Item
	 *   CS10701 (different Item, including shipping cost and handling cost) - Item1:  quantity:1, amount 5600.99, taxRate 0.17,has discount
	 *    Item2:  quantity:1, amount 10600.99, 0.13, has discount
	 *    shipping cost:  quantity:1, amount 160, 0.17
	 *    handling cost:  quantity:1, amount 48, 0.13
	 * It will show error message: No transactions available to merge.
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_6_4() {
		initData()

		// existing test data ， no need to insert test data

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",1 , temResult.size())
		clickEditWithWaitingForMerge()
		asClickTransCheckbox(1)

		// Case 19.4.2.6-4
		// Check Group Same Item
		checkGroupSameItem()

		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation:Consolidate Single Transaction；No same items;including shipping and handling cost ", context.isTextVisible(expResult.errMsg))
		//clickOKInErrMsgBox()
	}
/**
 * @desc Consolidate Red-letter Transactions with different information sheet number
 * @case 19.4.2.7
 *  Not Check Group Same Item
 *  CM10913 information sheet Number1 - Item1:  quantity:1, amount 5600.99
 *  CR10914 information sheet Number2- Item2:  quantity:2, amount 10600.99*2
 * @author Molly Feng
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_7() {
		initData()
		def tranHeader = testData["tranHeader"]

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",caseData.data.size(), temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}
		Thread.sleep(2 * 1000)

		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation:Consolidate red-letter transaction with different information sheet number ", context.isTextVisible(expResult.errMsg))

		clickOKInErrMsgBox()
	}

	/**
	 * @desc If no valid selected transactions to merge
	 * @case 19.4.2.8
	 *  select nothing
	 *  CM10906 has same information sheet Number - Item1:  quantity:1, amount 5600.99
	 *  CR10907 has same information sheet Number- Item2:  quantity:2, amount 10600.99*2
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_8() {
		initData()
		def tranHeader = testData["tranHeader"]

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		Thread.sleep(4 * 1000)
		clickEditWithWaitingForMerge()

		Thread.sleep(2 * 1000)
		// select no transaction
		clickMerge()
		Thread.sleep(2 * 1000)
		assertTrue("check validation: no valid transaction to merge", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}
	def setSearchParams(caseFilter) {

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
}
