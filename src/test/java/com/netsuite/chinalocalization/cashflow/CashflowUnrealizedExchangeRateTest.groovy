package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.chinalocalization.page.common.ManageRolePage
import com.netsuite.chinalocalization.page.common.UnrealizedExchangePage
import com.netsuite.common.P2
import com.netsuite.common.OW
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Created by stepzhou on 3/27/2018.
 * @desc Used to verify custom exchange rate gains and loss.
 */
@TestOwner("stephen.zhou@oracle.com")
class CashflowUnrealizedExchangeRateTest extends CashflowBaseTest {
    @Inject
    UnrealizedExchangePage unrealizedExchangePage

    String startDate = "Q2 2017"
    String endDate = "Q3 2017"
    String subsidiary = "Cash Flow BU"

    /**
     * @case 1.8.1.2
     * @author Stephen
     * @desc Verify exchange rate for bank account.
     *       check custom exchange rate for bank account.
     *
     * */
    @Category([P2.class])
    //@Test
    //It seems that this case is OW+EN which we will not support
    void case_1_8_1_2() {
        unrealizedExchangePage.navigateToPage()

        unrealizedExchangePage.selectFrom(startDate)
        unrealizedExchangePage.selectTo(endDate)
        unrealizedExchangePage.selectSubsidiary(subsidiary)

        unrealizedExchangePage.clickRefreshBtn()
        assertTrue("Unrealized exchange rate gains and losses summary is not correct", unrealizedExchangePage.verifyExchangeRateGainLoss("", ""))
    }

    @Override
    def getDefaultRole() {
        getAdministrator()
    }

}