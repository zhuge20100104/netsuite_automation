package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("kim.shi@oracle.com")
class CashflowBeginningBalanceTest extends CashflowBaseTest {

    @Inject
    CashFlowStatementPage cashflowPage;

    /**
     * @Author kim.shi@oracle.com
     * @CaseID Cashflow 1.8.2.1
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_8_2_1() {
        try {
            // TODO, test plan use Cash Flow BU
            String subsidiary = "China BU";
            String currentPeriod = "Mar 2017";
            String priorPeriod = "Mar 2016";
            double currentBeginningBalance = getBeginningBalance(subsidiary, currentPeriod, currentPeriod);
            double priorBeginningBalance = getBeginningBalance(subsidiary, priorPeriod, priorPeriod);
            cashflowPage.navigateToURL();
            String subId = Utility.getNonCashflowSubsidiaryId(aut, subsidiary);
            cashflowPage.setParameters(subId, "Q2 2017", "Q3 2017");
            cashflowPage.clickRefresh();
            // TODO need to find a better way to wait for report to refresh
//            Thread.sleep(40000);
            String actualCurrentBeginingBalance = cashflowPage.getReportData(37, 3);
            String actualPriorBeginningBalance = cashflowPage.getReportData(37, 4);
            checkTrue("Beginning balance for current period is not correct, expected=" + currentBeginningBalance + ", actual=" + actualCurrentBeginingBalance, currentBeginningBalance.equals(formatCurrentStr(actualCurrentBeginingBalance)));
            checkTrue("Beginning balance for prior period is not correct, expected=" + priorBeginningBalance + ", actual=" + actualPriorBeginningBalance, priorBeginningBalance.equals(formatCurrentStr(actualPriorBeginningBalance)));
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    private double getBeginningBalance(String subsidiary, String fromPeriod, String toPeriod) {
        String reportName = "银行类型总账";
        String id = context.getSavedReportId(reportName);
        Assert.assertNotNull(reportName + " does not exist", id);
        context.navigateTo("/app/reporting/reportrunner.nl?cr=" + id + "&reload=T&whence=");
        if (context.isOneWorld()) {
            context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@name='inpt_crit_2']")).sendKeys(subsidiary);
            context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@name='inpt_crit_1_from']")).sendKeys(fromPeriod);
            context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@name='inpt_crit_1_to']")).sendKeys(toPeriod);
        } else {
            context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@name='inpt_crit_2_from']")).sendKeys(fromPeriod);
            context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@name='inpt_crit_2_to']")).sendKeys(toPeriod);
        }
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='refresh']")).click();
        context.waitForPageToLoad();

        List reportDataList = context.webDriver.getHtmlElementsByLocator(Locator.xpath(".//*[@id='rptdataarea']/tbody/tr"));
        double balance = 0.00;
        // The first 3 trs are hidden, not data lines
        for (int i = 4; i <= reportDataList.size(); i++) {
            // The last td with index 5 is balance column
            String loc = String.format(".//*[@id='rptdataarea']/tbody/tr[%s]/td[5]", i);
            String balanceStr = context.webDriver.getHtmlElementByLocator(Locator.xpath(loc)).getAsText();
            balance = balance + formatCurrentStr(balanceStr);
        }
        return balance;
    }
}
