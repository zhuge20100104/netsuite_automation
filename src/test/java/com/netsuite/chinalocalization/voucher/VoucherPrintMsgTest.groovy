package com.netsuite.chinalocalization.voucher

import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Description:
 * Verify Voucher print notice message
 * This is a P2 case.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 *
 * @author qunxing.liu
 * @version 2018/4/17
 * @since 2018/4/17
 */
@TestOwner ("qunxing.liu@oracle.com")
class VoucherPrintMsgTest extends VoucherPrintBaseTest {

	@Category([P2.class, OW.class])
	@Test
	 void case_1_14_2_3() {
		super.voucherPrintPage.navigateToPage(super.URL)
		def params
		params = [
				"subsidiary":"Parent Company : China BU",
				"periodFrom":"Aug 2016",
				"periodTo": "Aug 2016",
				"tranDateFrom":"8/1/2016",
				"tranDateTo":"8/3/2016"]
		voucherPrintPage.setParameters(params,null)
		voucherPrintPage.clickPrintBtn()

		def expectedError = isTargetLanguageEnglish() ? VoucherMsgEnum.GREAT_THAN_50_RECORDS.getEnLabel() : VoucherMsgEnum.GREAT_THAN_50_RECORDS.getCnLabel()

	}
}
