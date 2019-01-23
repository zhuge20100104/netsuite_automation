package com.netsuite.chinalocalization.vat


import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("molly.feng@oracle.com")
class VATPreviewValidation2Test extends VATEditPageTestSuite{

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
	 * @desc Edit Transactions(Invoice;Cash Sale;Credit Memo;Cash Refund) that has been consolidated with others
	 * 	 @case 19.4.3.1  in this test suite, we only check, we cannot edit merged transaction.
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
	 *
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
		clickEditWithWaitingForMerge()

		for(int index =1; index <= temResult.size(); index ++){
			asClickTransCheckbox(index)
		}
		checkGroupSameItem()
		clickMerge()
		Thread.sleep(5 * 1000)
		// save the merge
		clickSave()
		Thread.sleep(5 * 1000)
		// Goto Edit Invoice IN11001
		context.navigateToInvoiceEditPage(item.internalid)
		Thread.sleep(5 * 1000)
		// Check Error message when edit consolidated transaction
		assertTrue("check validation: Edit consolidated Invoice", context.isTextVisible(expResult.errMsg))

	}
	@After
	void tearDown() {
		cleanAll = false
		super.tearDown()

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
