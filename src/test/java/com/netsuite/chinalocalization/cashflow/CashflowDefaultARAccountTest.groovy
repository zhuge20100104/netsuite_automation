package com.netsuite.chinalocalization.cashflow

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P0
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("yuanfang.chi@oracle.com")
public class CashflowDefaultARAccountTest extends CashflowBaseTest {

	/**
	 *  Purchase Order CFS Default Test
	 */
	@Test
	@Category([OW.class, P0.class])
	public void case_1_37_1() {
		verifyTransactionCFSDefault(CURL.PURCHASE_ORDER_CRUL, "vendor", "CN Automation Vendor", "case_1_37_1_data.json");
	}
	/**
	 *  Bill CFS Default Test
	 */
	@Test
	@Category([OW.class, P1.class])
	public void case_1_37_2() {
		verifyTransactionCFSDefault(CURL.VENDOR_BILL_CURL, "vendor", "CN Automation Vendor", "case_1_37_2_data.json");
	}
	/**
	 *  Vendor Credit CFS Default Test
	 */
	@Test
	@Category([OW.class, P2.class])
	public void case_1_37_3() {
		verifyTransactionCFSDefault(CURL.VENDOR_CREDIT_CURL, "vendor", "CN Automation Vendor", "case_1_37_3_data.json");
	}
	/**
	 *  Vendor Return CFS Default Test
	 */
	@Test
	@Category([OW.class, P3.class])
	public void case_1_37_4() {
		verifyTransactionCFSDefault(CURL.VENDOR_RETURN_AUTHORISATION_CURL, "vendor", "CN Automation Vendor", "case_1_37_4_data.json");
	}
}