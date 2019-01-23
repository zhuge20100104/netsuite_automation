package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.chinalocalization.page.common.ManageRolePage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Created by stepzhou on 5/8/2018.
 * @desc Used to verify subsidiaries settings.
 */
@TestOwner("stephen.zhou@oracle.com")
class CashflowStatementCheckTest extends CashflowBaseTest {
    @Inject
    CashFlowStatementPage cashFlowStatementPage

    /**
     * @case 1.0.2.5
     * @author Stephen
     * @desc Verify subsidiaries settings.
     *       check if the cashflow report can be opened with different roles.
     *
     * */
    @Category([OWAndSI.class, P2.class])
    @Test
    void case_1_0_2_5() {
        // Cashflow statement page can be opened with China Accountant role.
        context.navigateTo(CURL.HOME_CURL)
        cashFlowStatementPage.navigateToURL()

        // Cashflow statement page can be opened with Administrator role.
        switchToRole(getAdministrator())
        cashFlowStatementPage.navigateToURL()
    }

    @Override
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\roles_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\roles_en_US.json"
        ]
    }

}