package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.cashflow.CONSTANTS.CashFlowEnum
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("mia.wang@oracle.com")
class CashflowPeriodDefaultTest extends CashflowBaseTest {
    @Inject
    CashFlowStatementPage cashflowPage

    /**
     * @desc When first load, if current period does not exists, period should be default as the first period in the list.
     * @Author mia.wang@oracle.com
     * @CaseID Cashflow 1.3.2.1.3 Current Period Not Exist Test
     */
    @Test
    @Category([OW.class, P1.class])
    public void case_1_3_2_1_3() {
        cashflowPage.navigateToURL();
        String subsidiary = "Japan BU";
        String expectedPeriodFrom = "FY 2013";
        String expectedPeriodTo = "FY 2013";

        String subId = Utility.getNonCashflowSubsidiaryId(aut, subsidiary);
        cashflowPage.setParameters(subId, null, null);
        String actualPeriodFrom = cashflowPage.getPeriodFrom();
        String actualPeriodTo = cashflowPage.getPeriodTo();
        checkAreEqual("PeriodFrom is incorrect",actualPeriodFrom, expectedPeriodFrom);
        checkAreEqual("PeriodTo is incorrect", actualPeriodTo,expectedPeriodTo );
    }
    /**
     * @desc When First Load without period,Defualt value in the 'Period From' and 'Period To': empty and check error message will popup:
     *        1. Navigate to Financial -> Report -> 现金流量表
     *          (Note: Make the default subsidary without period by Role control)
     *        2.Click refresh
     * @Author yang.g.liu@oracle.com
     * @CaseID Cashflow 1.3.2.1.5 First Load without period
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_3_2_1_5() {
        switchToRole(getAccountantNoPeriodRole())
        cashflowPage.navigateToURL()

        String expectedPeriodFrom = ""
        String expectedPeriodTo = ""
        String actualPeriodFrom = cashflowPage.getPeriodFrom()
        String actualPeriodTo = cashflowPage.getPeriodTo()
        Assert.assertEquals("PeriodFrom is incorrect", expectedPeriodFrom, actualPeriodFrom)
        Assert.assertEquals("PeriodTo is incorrect", expectedPeriodTo, actualPeriodTo)
    }
}
