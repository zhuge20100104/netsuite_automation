package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner ("kim.shi@oracle.com")
class CashflowNetIncreaseTest extends CashflowBaseTest {
	@Inject
	CashFlowStatementPage cashflowPage;

	/**
	 * @Author kim.shi@oracle.com
	 * @CaseID Cashflow 1.16.11 Net Increase Test
	 */
	@Test
	@Category([OW.class, P1.class])
	public void case_1_16_11() {
		// TODO, test plan use Cash Flow BU
		String subsidiary = "China BU";
		String periodFrom = "Apr 2016";
		String periodTo = "Apr 2016"
		cashflowPage.navigateToURL();
		String subId = Utility.getNonCashflowSubsidiaryId(aut, subsidiary);
		cashflowPage.setParameters(subId, periodFrom, periodTo);
		cashflowPage.clickRefresh();

		String currentNetIncrease = cashflowPage.getReportData(36, 3);
		String priorNetIncrease = cashflowPage.getReportData(36, 4);
		double expectedCurrentNetIncrease = calculateNetIncrease(cashflowPage, true);
		double expectedPriorNetIncrease = calculateNetIncrease(cashflowPage, false);
		Assert.assertTrue("Net Increase for current period is not correct, expected=" + expectedCurrentNetIncrease + ",actual=" + currentNetIncrease, expectedCurrentNetIncrease.equals(formatCurrentStr(currentNetIncrease)));
		Assert.assertTrue("Net Increase for prior period is not correct, expected=" + expectedPriorNetIncrease + ",actual=" + priorNetIncrease, expectedPriorNetIncrease.equals(formatCurrentStr(priorNetIncrease)));
	}

	// Line 11 + Line 24 + Line 34 + Line 35
	private double calculateNetIncrease(CashFlowStatementPage cashflowPage, Boolean currentPeriod) {
		if (currentPeriod)
			return formatCurrentStr(cashflowPage.getReportDataCurrentPeriod(11)) +
					formatCurrentStr(cashflowPage.getReportDataCurrentPeriod(24)) +
					formatCurrentStr(cashflowPage.getReportDataCurrentPeriod(34)) +
					formatCurrentStr(cashflowPage.getReportDataCurrentPeriod(35));
		else
			return formatCurrentStr(cashflowPage.getReportDataPriorPeriod(11)) +
					formatCurrentStr(cashflowPage.getReportDataPriorPeriod(24)) +
					formatCurrentStr(cashflowPage.getReportDataPriorPeriod(34)) +
					formatCurrentStr(cashflowPage.getReportDataPriorPeriod(35));
	}
}
