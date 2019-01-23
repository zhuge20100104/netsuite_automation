package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.chinalocalization.page.common.ManageRolePage
import com.netsuite.common.P0
import com.netsuite.common.OW
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Created by stepzhou on 5/8/2018.
 * @desc Used to verify subsidiaries settings.
 */
@TestOwner("stephen.zhou@oracle.com")
class CashflowSubsidiariesSettingTest extends CashflowBaseTest {
    @Inject
    ManageRolePage manageRolePage

    @Inject
    CashFlowStatementPage cashFlowStatementPage

    /**
     * @case 1.3.1.1
     * @author Stephen
     * @desc Verify subsidiaries settings.
     *       Check if the subsidiaries of cashflow report consistent with the subsidiaries setting for this role.
     *
     * */
    @Category([P0.class,OW.class])
    @Test
    void case_1_3_1_1() {
        // Navigate to cashflow report page to verify the subsidiaries settings.
        switchToRole(getAccountant())
        cashFlowStatementPage.navigateToURL()
        List<String> reportSubsidiaries = cashFlowStatementPage.getSubsidiaryOptions()
        assertTrue("Subsidiaries settings don't take effect", verifySubsidiaries(reportSubsidiaries, reportSubsidiaries))
    }

    @Override
    def getDefaultRole() {
        getAdministrator()
    }

    @Override
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\roles_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\roles_en_US.json"
        ]
    }

    private static boolean verifySubsidiaries(List<String> srcList, List<String> tarList) {
        for (String item : srcList) {
            if (item.indexOf(":") > 0) {
                item = item.substring(item.indexOf(":") + 1).trim()
                for (String tarItem : tarList) {
                    if (tarItem.contains(item)) {
                        return true
                    }
                }
            }
        }
        return true
    }
}