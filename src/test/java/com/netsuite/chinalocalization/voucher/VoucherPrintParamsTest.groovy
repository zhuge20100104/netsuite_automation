package com.netsuite.chinalocalization.voucher
import org.junit.Test

class VoucherPrintParamsTest extends VoucherPrintBaseTest {

	/**
	 * @Author kim.shi@oracle.com
	 * @UpdateBy mia.wang@oracle.com
	 * @UpdateAt 2018-Mar-07
	 * @CaseID Voucher 1.3.2.2.5
	 */
	@Test
	public void case_1_3_2_2_5_() {
		super.voucherPrintPage.navigateToPage(super.URL)
		def params
		params = ["periodFrom":"Jun 2017", "periodTo": "Jun 2017"]
		voucherPrintPage.setParameters(params);
		params = ["periodTo": "Apr 2017"]
		voucherPrintPage.setParameters(params);

		def expectedError = isTargetLanguageEnglish() ? VoucherMsgEnum.INVALID_PERIOD_RANGE.getEnLabel() : VoucherMsgEnum.INVALID_PERIOD_RANGE.getCnLabel();
		assertAreEqual("Should show invalid period error message", expectedError, voucherPrintPage.getErrorMessage());

		voucherPrintPage.clickOKBtn();
		assertAreEqual("The Period To should back to valid value", "Jun 2017", voucherPrintPage.getPeriodTo());
	}
}
