package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.common.disableClassifications
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category
import javax.inject.Inject

@TestOwner("stephen.zhou@oracle.com")
class DisableFieldsTest extends BalanceSheetAppTestSuite {

    @Inject
    private BalanceSheetPage blsPage

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @desc Check if disable function work. Because it's hard to prepare 3 environments for disable Location, disable Department and disable Class.
     * And we can prepare one environment to disable above 3 functions together and then run this test. So I just combine the 3 disable cases to one automation case.
     * @Author stephen.zhou@oracle.com
     * @CaseID 13.2.1, 13.2.2, 13.5.1
     */
    @Category([disableClassifications.class])
    @Test
    void case_13_2_verify_if_location_field_exists() {
        blsPage.navigateToURL()
        blsPage.fillBalanceSheetName("中国资产负债表模板_bl03_Filter")
        blsPage.selectSubsidiary("China BlSheet 03")
        assertFalse("Class should not appear", blsPage.isClassLabelAppear())
        assertFalse("Department should not appear", blsPage.isDepartmentLabelAppear())
        assertFalse("Location should not appear", blsPage.isLocationLabelAppear())
    }

}
