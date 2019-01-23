package com.netsuite.chinalocalization.vat

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P2
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.eclipse.jetty.util.ajax.JSON
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("molly.feng@oracle.com")
class VATPreviewValidationTest extends VATEditPageTestSuite{

	@Rule
	public  TestName name = new TestName()
	private  def caseData
	private  def caseFilter
	private  def expResult
	private  def records
	def initData(){

		caseData = testData.get(name.getMethodName())
		caseFilter = caseData.filter
		if ("expectedResult" in caseData) expResult= caseData.expectedResult

		// insert test data if have any, some test case use data in other case ：test_case_19_4_2_5_3
		if (caseData.data) {
			// 2018/8/24 move groovy test data to js
//			switchToRole(administrator)
//			 //comments create record 2018/5/16
//			 records = record.createRecord(caseData.data)
//			switchToRole(accountant)
		}
	}
	def pathToTestDataFiles() {
		return [
				"zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATPreviewValidationTest_zh_CN.json",
				"enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\VATPreviewValidationTest_en_US.json"
		]
	}
	/**
	 * @desc If there is no valid data to merge or split.
	 * 	 IN10901 - no bank account  ：Invoices with mandatory fields error，
	 *   show error when click Edit: No valid transactions to edit. Please check and correct the transactions according to the messages.
	 *
	 * @case 19.4.1.1
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P1.class])
	void test_case_19_4_1_1() {
		initData()

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForElement(locators.edit)
		Thread.sleep(5 * 1000)
		clickEdit()
		Thread.sleep(5 * 1000)

		assertTrue("check validation: no valid transaction to edit", context.isTextVisible(expResult.errMsg))

	}
/**
 * @desc If there is no valid data to merge or split.
 * 	 @case 19.4.1.2-1 Invoices with other error messages (Quantity / Amount is not match to the VAT) in preview page; these errors should no be duplicated.
 * 	 IN10931 - Item1: quantity:-1;amount:200
 * 	           Item2: quantity:-1;amount:100
 * 	           Item3: quantity:1; amount:5600.99
 * 	 @case 6.4.5.1
 *   @case 19.4.1.2-2 show error when click Edit: No valid transactions to edit. Please check and correct the transactions according to the messages.
 * @author Molly Feng
 * @modify update doc number from IN10920->IN10931  Molly Feng 2018/5/28 it is dulplicated with other case
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_1_2() {
		initData()

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(5 * 1000)

		// check Error message in preview page
		assertTrue("check validation: Quantiy, Amount not match ", context.isTextVisible(expResult.errMsg1))

		Thread.sleep(5 * 1000)
		clickEdit()
		Thread.sleep(5 * 1000)

		// check Error message when click Edit button
		assertTrue("check validation: no valid transaction to edit", context.isTextVisible(expResult.errMsg2))

	}
/**
 * @desc Invoices with other error messages (Discount exceeds tax exclusive amount) -Only 1 error showed in preview page
 * 	 @case 19.4.1.3-1
 * 	 IN10921 - Item1: quantity:1;amount:-2000
 * 	           Item2: quantity:1;amount:-1000
 * 	           Item3: quantity:1; amount:5600.99
 * @case 6.4.6.1
 * @case 19.4.1.3-2 show error when click Edit: No valid transactions to edit. Please check and correct the transactions according to the messages.
 *
 *
 * @author Molly Feng
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_1_3() {
		initData()

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(5 * 1000)

		// check Error message in preview page
		assertTrue("check validation:Discount > Amount ", context.isTextVisible(expResult.errMsg1))
		Thread.sleep(2 * 1000)
		clickEdit()
		Thread.sleep(5 * 1000)

		// check Error message when click Edit button
		assertTrue("check validation: no valid transaction to edit", context.isTextVisible(expResult.errMsg2))

	}

/**
 * @desc
 * 	 @case 19.4.1.4-1 No information sheet number error is displayed in preview page
 * 	 CM10922 no information sheet number - Item1: quantity:1;amount:5600.99
 * 	 CR10923 no information sheet number - Item1: quantity:1;amount:5600.99
 * @case 19.4.1.4-2 For special invoice, if there is no information sheet number. Error message support below cases:
 * 2. Show up when user try to merge negatvie transactions.
 *  Select CM10922, CR10923 and Click Merge
 *
 * @author Molly Feng
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_19_4_1_4() {
		initData()

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		// search CM10922
		context.setFieldWithValue("custpage_documentnumberfrom_formattedValue", caseFilter.docNumFrom)
		context.setFieldWithValue("custpage_documentnumberto_formattedValue", caseFilter.docNumFrom)

		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(5 * 1000)

		// check Error message in preview page
		assertTrue("check validation: No information sheet number", context.isTextVisible(expResult.errMsg))

		// search CR10923
		context.setFieldWithValue("custpage_documentnumberfrom_formattedValue", caseFilter.docNumTo)
		context.setFieldWithValue("custpage_documentnumberto_formattedValue", caseFilter.docNumTo)

		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(5 * 1000)

		// check Error message in preview page for CR10923
		assertTrue("check validation: No information sheet number", context.isTextVisible(expResult.errMsg))

		// search CM10922, CR10923
		context.setFieldWithValue("custpage_documentnumberfrom_formattedValue", caseFilter.docNumFrom)
		context.setFieldWithValue("custpage_documentnumberto_formattedValue", caseFilter.docNumTo)
		clickRefresh()
		clickEditWithWaitingForMerge()

		def headerSublist = asSublist("custpage_header_sublist")
		Thread.sleep(5 * 1000)
		// check checkbox of docNum 10922
		def index = headerSublist.getRowIndexWithTextInColumn("10922", headerlabels.docNum)
		asClickTransCheckbox(index)
		// check checkbox of docNum 10923
		index = headerSublist.getRowIndexWithTextInColumn("10923", headerlabels.docNum)
		asClickTransCheckbox(index)

		clickMerge()
		Thread.sleep(5 * 1000)
		// Error show when merge 2 negative transaction without information sheet number
		assertTrue("check validation: No information sheet number Error when merge 2 negative transactions", context.isTextVisible(expResult.errMsg))
	}

	/**
	 * @desc No Golden Tax Transaction
	 * 	 There is no golden tax transaction during selected dates.
	 * @case 6.4.2.1 show error ：没有与搜索条件匹配的结果。
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_6_4_2_1() {

		initData()
		navigateToPortalPage()
		waitForPageToLoad()
		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		Thread.sleep(3 * 1000)

		// check Error message in preview page
		assertTrue("check no data message ", context.isTextVisible(expResult.errMsg))

	}
/**
 * @desc Common Credit Memo No Apply -CN
 *    The credit memos selected in the params settings are no apply invoices.
 *
 * @case 6.4.3.1 show error ：无效格式。请先核销对应的正向增值税发票。
 * @author Molly Feng
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_6_4_3_1() {
		initData()
		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		// check Error message in preview page
		assertTrue("check no apply message for Credit Memo ", context.isTextVisible(expResult.errMsg))

	}
/**
 * @desc Common Cash Refund No created from -CN
 *    The Cash Refund selected in the params settings are no created from cash sales.
 *
 * @case 6.4.4.1 show error ：无效格式。请先核销对应的正向增值税发票。
 * @author Molly Feng
 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_6_4_4_1() {
		initData()
		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)

		// check Error message in preview page
		assertTrue("check no apply message for Cash Refund ", context.isTextVisible(expResult.errMsg))

	}

	/**
	 * @desc Credit Memo with  error messages (Quantity / Amount is not match to the VAT)
	 * 	 @case 6.4.5.4 Credit Memo with other error messages (Quantity / Amount is not match to the VAT) in preview page; these errors should no be duplicated.
	 * 	 CM10926 - Item1: quantity:1; price:Custom amount:-200  note: this setting is used when creating the cm transaction.
	 * 	           Item2: quantity:-1;amount:500
	 * 	           Item3: quantity:-1; amount:-300
	 * 	           Item4: quantity:1; amount:13500.99
	 * 	 @case 6.4.5.5
	 * 	 @case 6.4.5.6
	 *
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P2.class])
	void test_case_6_4_5_4() {
		initData()

		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(5 * 1000)

		// check Error message in preview page
		assertTrue("check validation: Quantiy, Amount not match ", context.isTextVisible(expResult.errMsg1))

	}
	/**
	 * @desc Edit Transactions(Invoice;Cash Sale;Credit Memo;Cash Refund) that has been consolidated with others
	 * 	 @case 19.4.3.1  in this test suite, we only check we can edit transaction not merged
	 *
	 * 	 CON-
	 * 	 |-IN11001 - Item1: quantity:3 amount:5600.99*3
	 * 	 |-CS11002 - Item2: quantity:3 amount:15600.99*3
	 * 	 |-CM11003 - Item1: quantity:1 amount:5600.99*1
	 * 	 |-CR11004 - Item2: quantity:1 amount:15600.99*1
	 *   after Merge and, try to edit children transactions ;
	 *   it will show error message on edit transaction page
	 *
	 *   Navigate to VAT page, refresh with the same search condition
	 *   Got to Edit Page
	 *   Select the consolidated transaction and Click Unmerge
	 *
	 * @author Molly Feng
	 */
	@Test
	@Category([OWAndSI.class, P1.class])
	void test_case_19_4_3_1() {
		initData()

		def url = resolveSuiteletURL("customscript_sl_cn_vat", "customdeploy_sl_cn_vat")
		navigateToPortalPage()
		waitForPageToLoad()

		setSearchParams(caseFilter)
		clickRefresh()
		waitForPageToLoad()
		waitForElement(locators.edit)
		Thread.sleep(5 * 1000)

		def temResult = getAllResults()
		assertAreEqual("Result size equal",temResult.size(), 4)
		def item = temResult.find { it -> it.docno == "11001"}
		assertAreEqual("Should find record docno is 11001","11001", item.docno)

		context.navigateToInvoiceEditPage(item.internalid)
		Thread.sleep(5 * 1000)
		// Check we can Open Edit Transaction page
		assertTrue("check validation: Edit consolidated Invoice", context.isTextVisible("11001"))
//
	}
	def getConsolidatedDocNum(headerSublist, conDocNumArray){
		def conDocNum = ""
		for (int i; i< conDocNumArray.size(); i++) {
			if (headerSublist.doesTextExistInColumn(conDocNumArray[i], headerlabels.docNum)) {
				conDocNum = conDocNumArray[i]
				break
			}
		}
		return conDocNum

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
		if (caseFilter.datefrom) context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
		if (caseFilter.dateto) context.setFieldWithValue("custpage_dateto", caseFilter.dateto)
		if (caseFilter.docNumFrom) context.setFieldWithValue("custpage_documentnumberfrom_formattedValue", caseFilter.docNumFrom)
		if (caseFilter.docNumTo) context.setFieldWithValue("custpage_documentnumberto_formattedValue", caseFilter.docNumTo)
		if (caseFilter.enableSalesList) context.setFieldWithValue("custpage_saleslist",caseFilter.enableSalesList)

	}
	/**
	 * @desc Get internal id by externalid.
	 * @param {string} [required] name
	 * @return{Number} internal id
	 */
	private getIdByExternalId(params) {
		def columns = [record.helper.column("internalid").create()]
		def filters = [record.helper.filter(params.fieldId).is(params.value)]
		def ids = record.searchRecord(params.entity, filters, columns)
		if (ids) {
			return ids[0].internalid
		}
	}
}
