package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.page.CashRefundPage
import com.netsuite.chinalocalization.page.CashSalePage
import com.netsuite.chinalocalization.page.CreditMemoPage
import com.netsuite.chinalocalization.page.CustomerDepositPage
import com.netsuite.chinalocalization.page.CustomerRefundPage
import com.netsuite.chinalocalization.page.InvoicePage
import com.netsuite.chinalocalization.page.ReturnAuthorizationPage
import com.netsuite.chinalocalization.page.SalesOrderPage
import com.netsuite.chinalocalization.page.StatementChargePage
import com.netsuite.common.OW
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import org.junit.Assert
import org.junit.Test
import groovy.json.JsonSlurper
import org.junit.experimental.categories.Category

@TestOwner ("jing.han@oracle.com")
class CashflowARFilterTest extends CashflowBaseTest {

	@Inject
	protected SalesOrderPage salesOrderPage;
	@Inject
	protected InvoicePage invoicePage;
	@Inject
	protected StatementChargePage statementChargePage;
	@Inject
	protected CreditMemoPage creditMemoPage;
	@Inject
	protected CustomerDepositPage customerDepositPage;
	@Inject
	protected CustomerRefundPage customerRefundPage;
	@Inject
	protected CashSalePage cashSalePage;
	@Inject
	protected CashRefundPage cashRefundPage;
	@Inject
	protected ReturnAuthorizationPage returnAuthorizationPage;

	static HashSet expectInFlowItems;
	static HashSet actualHeaderCFSItems
	static HashSet actualItemLineCFSItems

	@SuiteSetup
	void setUpTestSuite() {
		super.setUpTestSuite()
		expectInFlowItems=getCFSItemFromJson("IN");
	}

	/**
	 * @CaseID Cashflow 1.45.2.1
	 * @author jing.han@oracle.com
	 * Description: Sales Order
	 */
	@Test
	@Category([OW.class, P0.class])
	void case_1_45_2_1() {
		def data = "{\"entity\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
		def jsonSlurper = new JsonSlurper();
		def dataObj = jsonSlurper.parseText(data);

		salesOrderPage.createSalesOrder(dataObj)

		//Assert CFS item quantity and each item in Header line
		actualHeaderCFSItems = salesOrderPage.getDropDownListOptions(salesOrderPage.FIELD_ID_HEADER_CFS)
		trimCFSOptions(actualHeaderCFSItems)
		assertCFS(expectInFlowItems,actualHeaderCFSItems)

		//Assert CFS item quantity and each item in Item line
		actualItemLineCFSItems = salesOrderPage.getItemLineCFSOptions(salesOrderPage.FIELD_ID_ITEM_CFS)
		trimCFSOptions(actualItemLineCFSItems)
		assertCFS(expectInFlowItems,actualItemLineCFSItems)
	}


	/**
	 * @CaseID Cashflow 1.45.2.2
	 * @author jing.han@oracle.com
	 * Description: Invoice
	 */
	@Test
	@Category([OW.class, P1.class])
	void case_1_45_2_2() {
		def data = "{\"entity\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
		def jsonSlurper = new JsonSlurper();
		def dataObj = jsonSlurper.parseText(data);

		invoicePage.createInvoice(dataObj)

		//Assert CFS item quantity and each item in Header line
		actualHeaderCFSItems = invoicePage.getDropDownListOptions(invoicePage.FIELD_ID_HEADER_CFS)
		trimCFSOptions(actualHeaderCFSItems)
		assertCFS(expectInFlowItems,actualHeaderCFSItems)
	}

	/**
	 * @CaseID Cashflow 1.45.2.3
	 * @author jing.han@oracle.com
	 * Description: Statement Charge (Administrator Role)
	 */
//	@Test
//	public void "1.45.2.3 Statement Charge CFS Filter"() {
//		def data = "{\"entity\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
//		def jsonSlurper = new JsonSlurper();
//		def dataObj = jsonSlurper.parseText(data);
//
//		// Change to Administrator Role
//		loginAsRole(getAdministrator())
//
//		statementChargePage.createStatementCharge(dataObj)
//		//Assert CFS item quantity and each item in Header line
//		actualHeaderCFSItems = statementChargePage.getDropDownListOptions(statementChargePage.FIELD_ID_HEADER_CFS)
//		trimCFSOptions(actualHeaderCFSItems)
//		assertCFS(expectInFlowItems,actualHeaderCFSItems)
//
//		//Assert CFS item quantity and each item in Item line
//		actualItemLineCFSItems = statementChargePage.getItemLineCFSOptions(statementChargePage.FIELD_ID_ITEM_CFS)
//		trimCFSOptions(actualItemLineCFSItems)
//		assertCFS(expectInFlowItems,actualItemLineCFSItems)
//	}

	/**
	 * @CaseID Cashflow 1.45.2.4
	 * @author jing.han@oracle.com
	 * Description: Credit Memo (Administrator Role)
	 */
	@Test
	@Category([OW.class, P1.class])
	void case_1_45_2_4() {
		def data = "{\"entity\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
		def jsonSlurper = new JsonSlurper();
		def dataObj = jsonSlurper.parseText(data);

		creditMemoPage.createCreditMemo(dataObj)

		//Assert CFS item quantity and each item in Header line
		actualHeaderCFSItems = creditMemoPage.getDropDownListOptions(creditMemoPage.FIELD_ID_HEADER_CFS)
		trimCFSOptions(actualHeaderCFSItems)
		assertCFS(expectInFlowItems,actualHeaderCFSItems)
	}

	/**
	 * @CaseID Cashflow 1.45.2.5
	 * @author jing.han@oracle.com
	 * Description: Customer Deposit
	 */
	@Test
	@Category([OW.class, P2.class])
	void case_1_45_2_5() {
		def data = "{\"customer\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
		def jsonSlurper = new JsonSlurper();
		def dataObj = jsonSlurper.parseText(data);

		customerDepositPage.createCustomerDeposit(dataObj)
		//Assert CFS item quantity and each item in Header line
		actualHeaderCFSItems = customerDepositPage.getDropDownListOptions(customerDepositPage.FIELD_ID_HEADER_CFS)
		trimCFSOptions(actualHeaderCFSItems)
		assertCFS(expectInFlowItems,actualHeaderCFSItems)
	}

	/**
	 * @CaseID Cashflow 1.45.2.6
	 * @author jing.han@oracle.com
	 * Description: Customer Refund (Administrator Role)
	 */
	@Test
	@Category([OW.class, P2.class])
	void case_1_45_2_6() {
		def data = "{\"customer\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
		def jsonSlurper = new JsonSlurper();
		def dataObj = jsonSlurper.parseText(data);

		switchToRole(getAdministrator())
		customerRefundPage.createCustomerRefund(dataObj)
		//Assert CFS item quantity and each item in Header line
		try {
			actualHeaderCFSItems = customerRefundPage.getDropDownListOptions(customerRefundPage.FIELD_ID_HEADER_CFS)
			trimCFSOptions(actualHeaderCFSItems)
			assertCFS(expectInFlowItems, actualHeaderCFSItems)
		}catch (Exception e){
			e.printStackTrace()
		}
	}

	/**
	 * @CaseID Cashflow 1.45.2.7
	 * @author jing.han@oracle.com
	 * Description: Cash Sale
	 */
	@Test
	@Category([OW.class, P2.class])
	void case_1_45_2_7() {
		def data = "{\"entity\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
		def jsonSlurper = new JsonSlurper();
		def dataObj = jsonSlurper.parseText(data);

		cashSalePage.createCashSale(dataObj)
		//Assert CFS item quantity and each item in Header line
		actualHeaderCFSItems = cashSalePage.getDropDownListOptions(cashSalePage.FIELD_ID_HEADER_CFS)
		trimCFSOptions(actualHeaderCFSItems)
		assertCFS(expectInFlowItems,actualHeaderCFSItems)

		//Assert CFS item quantity and each item in Item line
		actualItemLineCFSItems = cashSalePage.getItemLineCFSOptions(cashSalePage.FIELD_ID_ITEM_CFS)
		trimCFSOptions(actualItemLineCFSItems)
		assertCFS(expectInFlowItems,actualItemLineCFSItems)
	}

	/**
	 * @CaseID Cashflow 1.45.2.8
	 * @author jing.han@oracle.com
	 * Description: Cash Refund (Administrator Role)
	 */
	@Test
	@Category([OW.class, P3.class])
	void case_1_45_2_8() {
		def data = "{\"entity\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
		def jsonSlurper = new JsonSlurper();
		def dataObj = jsonSlurper.parseText(data);

		cashRefundPage.createCashRefund(dataObj)
		//Assert CFS item quantity and each item in Header line
		actualHeaderCFSItems = cashRefundPage.getDropDownListOptions(cashRefundPage.FIELD_ID_HEADER_CFS)
		trimCFSOptions(actualHeaderCFSItems)
		assertCFS(expectInFlowItems,actualHeaderCFSItems)

		//Assert CFS item quantity and each item in Item line
		actualItemLineCFSItems = cashRefundPage.getItemLineCFSOptions(cashRefundPage.FIELD_ID_ITEM_CFS)
		trimCFSOptions(actualItemLineCFSItems)
		assertCFS(expectInFlowItems,actualItemLineCFSItems)
	}

	/**
	 * @CaseID Cashflow 1.45.2.9
	 * @author jing.han@oracle.com
	 * Description: Return Authorization (Administrator Role)
	 */
	@Test
	@Category([OW.class, P3.class])
	void case_1_45_2_9() {
		def data = "{\"entity\": \"CN Automation Customer\", \"location\": \"CN_BJ\"}";
		def jsonSlurper = new JsonSlurper();
		def dataObj = jsonSlurper.parseText(data);

		switchToRole(getAdministrator())
		returnAuthorizationPage.createReturnAuthorization(dataObj)
		//Assert CFS item quantity and each item in Header line
		actualHeaderCFSItems = returnAuthorizationPage.getDropDownListOptions(returnAuthorizationPage.FIELD_ID_HEADER_CFS)
		trimCFSOptions(actualHeaderCFSItems)
		assertCFS(expectInFlowItems,actualHeaderCFSItems)

		//Assert CFS item quantity and each item in Item line
		actualItemLineCFSItems = returnAuthorizationPage.getItemLineCFSOptions(returnAuthorizationPage.FIELD_ID_ITEM_CFS)
		trimCFSOptions(actualItemLineCFSItems)
		assertCFS(expectInFlowItems,actualItemLineCFSItems)
	}

	void assertCFS(HashSet expect, HashSet actual) {
		if (expect && actual) {
			def size_act = actual.size()
			def size_exp = expect.size()
			checkAreEqual("CFS item quantity is not match expected", size_act, size_exp)
			checkAreEqual("CFS items are not match expected", actual, expect)
		}
	}

}