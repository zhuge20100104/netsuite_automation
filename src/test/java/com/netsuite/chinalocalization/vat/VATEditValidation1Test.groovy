package com.netsuite.chinalocalization.vat

import com.netsuite.common.OWAndSI
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner

//import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("molly.feng@oracle.com")
class VATEditValidation1Test extends VATEditPageTestSuite{
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
	 * @desc If user select one or some transactions that have been consolidated or split to merge.
	 * @case 19.4.2.9
	 *  Disable Sales List
	 *  Con1-
	 * 	 |-IN10915 - Item1:  quantity:1, amount 5600.99
	 * 	|-CS10916 - Item2:  quantity:2, amount 10600.99*2
	 *IN10917 - split(don't have checkbox for split transactions.)
	 * Item1: quantity:1, amount 5600.99 has discount
	 * Item2: quantity:1, amount 5600.99 has discount
	 * Item3: quantity:1, amount 5600.99 has discount
	 * Item4: quantity:1, amount 5600.99 has discount
	 * shipping cost, handling cost
	 *IN10918 - original
	 *	 Item1: quantity:1, amount 5600.99 has discount
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_9() {
		initData()
		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",5, temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <=2; index ++){
			asClickTransCheckbox(index)
		}
		Thread.sleep(2 * 1000)

		clickMerge()
		Thread.sleep(2 * 1000)

		// check checkbox of docNum CON110915

		asClickTransCheckbox(1)

		//index = headerSublist.getRowIndexWithTextInColumn("10918", tranHeader.docNum) this way not work
		//index = getIndexByTextInColumn("10918")

		// check checkbox of docNum 10918
		asClickTransCheckbox(4)

		clickMerge()
		Thread.sleep(2 * 1000)

		assertTrue("check validation:Merge consolidated transaction", context.isTextVisible(expResult.errMsg))
		//clickOKInErrMsgBox()
	}

	/**
	 * @desc Merge transactions of different customers
	 * @case 19.4.2.10
	 * IN10919 Customer1- Item1:  quantity:1, amount 5600.99
	 * IN10920 Customer2- Item1:  quantity:1, amount 5600.99
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_10() {
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

		assertTrue("check validation:Merge transactions of different customers", context.isTextVisible(expResult.errMsg))

		clickOKInErrMsgBox()
	}


	/**
	 * @desc For common invoice, if there is no "create by" for components of Credit memo/cash refund.
	 * Try to merge with negative transaction
	 * if this common invoice merges with other negative invoice, it will throw error message.
	 * @case 19.4.2.11
	 * CM11009 - no apply
	 * CR11010 - no created from
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_11() {
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

		assertTrue("check validation:Merge no apply/created from transaction with other negative transaction", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}

	/**
	 * @desc For common invoice, Try to merge red-letter transactions that have different VAT Code and VAT Number
	 *
	 * @case 19.4.2.12-1
	 * CM only has 1 apply
	 * CR has 1 created from
	 * CM11011 -  apply to IN11011 (VAT Code1) -  no error message in preview page
	 * CR11012 - created from CS11012 (VAT Code2) - no error message in preview page
	 * when VAT code1 <> VAT code2, it should be show error message:
	 * Unable to Merge. Merging Credit Memos or Cash Refunds must have the same Remarks （Invoice Code and Invoice Number）.
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_12_1() {
		initData()
		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",2, temResult.size())
		clickEditWithWaitingForMerge()
		Thread.sleep(2 * 1000)
		asClickTransCheckbox(1)
		println("select trans 1")
		asClickTransCheckbox(2)
		println("select trans 2")
		Thread.sleep(2 * 1000)

		clickMerge()
		println("click merge buttion")
		Thread.sleep(2 * 1000)

		assertTrue("check validation:Merge t negative transactions with different VAT Code and VAT Number;Common", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}

	/**
	 * @desc For common invoice, Try to merge red-letter transactions that have different VAT Code and VAT Number
	 *
	 * @case 19.4.2.12-1-2
	 * CM has Multiple Applies
	 * CR has 1 created from
	 * 	CM11013 -  apply to IN11013_0 (VAT Code1) -  no error message in preview page
	 * 	 	   -  apply to IN11013_1 (VAT Code1)
	 * 	CR11014 - created from CS11014 (VAT Code3) - no error message in preview page
	 * when VAT code1 <> VAT code3, it should be show error message:
	 * Unable to Merge. Merging Credit Memos or Cash Refunds must have the same Remarks （Invoice Code and Invoice Number）.
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_12_1_2() {
		initData()

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",2, temResult.size())
		clickEditWithWaitingForMerge()
		Thread.sleep(2 * 1000)

		asClickTransCheckbox(1)
		println("select trans 1")
		asClickTransCheckbox(2)
		println("select trans 2")
		Thread.sleep(2 * 1000)

		clickMerge()
		println("click merge buttion")
		Thread.sleep(2 * 1000)

		assertTrue("check validation:Merge t negative transactions with different VAT Code and VAT Number;Common", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}
	/**
	 * @desc For common invoice, if more than one invoice number or code found from the "Creat by" transactions. (actually, it only means Credit Memo)
	 * Try to Merge  CM11015 and CM11016,
	 * it should show 1 error:
	 * error1: This Credit Memo can only be applied to one VAT Invoice.
	 *
	 * @case 6.4.3.3  check error message is displayed in preview page(CM11015)
	 *
	 * @case 19.4.2.12-2
	 * 	 CM11015 -  apply to IN11015_0 (VAT Code1, VAT Code2)   - error messag in preview page
	 * 	 	       apply to IN11015_1 (VAT Code3, VAT Code4)
	 * 	 CM11016 - apply to IN11016(VAT Code1) normal red-letter invoice
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_12_2() {
		initData()

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		// check error message in preview page
		assertTrue("check validation: more than one VAT Code and VAT Number;Common", context.isTextVisible(expResult.errMsg))

		Thread.sleep(3 * 1000)
		def temResult = getAllResults()
		assertAreEqual("Result size equal",2 , temResult.size())
		clickEditWithWaitingForMerge()
		Thread.sleep(2 * 1000)
		asClickTransCheckbox(1)
		println("select trans 1")
		asClickTransCheckbox(2)
		println("select trans 2")
		Thread.sleep(2 * 1000)

		clickMerge()
		println("click merge buttion")
		Thread.sleep(2 * 1000)

		assertTrue("check validation:Merge negative transaction with more than one VAT Code and VAT Number;Common", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}

	/**
	 * @desc For common invoice, if no invoice number or code found from the "Creat by" transactions.
	 *
	 * @case 19.4.2.13
	 * 	 CM11017 -  apply to IN11017 (no VAT Code and VAT number) - show error msg in preview page
	 * 	 CR11018 - created from CS11018 (no VAT Code and VAT number) - show error msg in preview page
	 * 	 Try to merge these two red letter invoices
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_2_13() {
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
		assertAreEqual("Result size equal",2, temResult.size())
		clickEditWithWaitingForMerge()
		for(int index =1; index <= 2 ; index ++){
			asClickTransCheckbox(index)
		}
		Thread.sleep(2 * 1000)

		clickMerge()
		Thread.sleep(2 * 1000)

		assertTrue("check validation:Merge negative transaction with no VAT Code and VAT Number;Common", context.isTextVisible(expResult.errMsg))
		clickOKInErrMsgBox()
	}

	/**
	 * @desc If no valid selected transactions to unmerge:
	 * @case 19.4.5.1-1
	 * 	 select nothing,
	 *  Con1-
	 * 	 |-IN11020 - Item1:  quantity:1, amount 5600.99
	 * 	|-CS11021 - Item2:  quantity:2, amount 10600.99*2
	 *IN11022 - original
	 *	 Item1: quantity:1, amount 5600.99 has discount
	 *@case 19.4.5.1-2
	 * Select Con1 and IN11022 , click unmerge
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_5_1() {
		initData()
		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(3 * 1000)
		clickEditWithWaitingForMerge()

		// case 19.4.5.1 -1 select nothing to unmerge
		clickUnmerge()
		assertTrue("check validation:Unmerge nothing", context.isTextVisible(expResult.errMsg))

		clickOKInErrMsgBox()

		// case 19.4.5.1 -2 select consolidated transaction and other original transaction to unmerge
		int index=0
		for( index =1; index < caseData.data.size(); index ++){
			asClickTransCheckbox(index)
		}
		Thread.sleep(2 * 1000)

		clickMerge()
		Thread.sleep(2 * 1000)

		// check checkbox of docNum CON111020
		asClickTransCheckbox(1)

		// check checkbox of docNum 11022
		//index = getIndexByTextInColumn("11022")
		asClickTransCheckbox(4)

		clickUnmerge()
		Thread.sleep(2 * 1000)

		assertTrue("check validation:Unmerge consolidated transaction and original transaction", context.isTextVisible(expResult.errMsg))
		//clickOKInErrMsgBox()
	}


	/**
	 * @desc Check Cancel message
	 * @case 19.4.4.1
	 *
	 * Merge
	 * 	 |-IN10924 - Item1:  quantity:1, amount 5600.99
	 * 	|-CS10925 - Item2:  quantity:1, amount 5600.99
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_4_1() {
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
		Thread.sleep(2 * 1000)

		clickMerge()
		Thread.sleep(2 * 1000)

		clickCancel()
		assertTrue("check Cancel Message", context.isTextVisible(expResult.errMsg1))
		assertTrue("check Cancel Message", context.isTextVisible(expResult.errMsg2))
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
