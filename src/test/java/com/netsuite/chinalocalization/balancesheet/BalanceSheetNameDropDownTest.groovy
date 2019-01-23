package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.common.*
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject

@TestOwner("jingzhou.wang@oracle.com")
class BalanceSheetNameDropDownTest extends BalanceSheetAppTestSuite {

    @Inject
    private BalanceSheetPage blsPage

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @author jingzhou.wang@oracle.com
     * @lastUpdateBy
     * @caseID 1.3.2&1.3.3 Create and delete a Balance sheet name
     * @desc 1. Create a name from BLS drop down list-> verify it been added
     *           2. Delete a name from BLS drop down list-> verify it been deleted
     */
    @Test
    @Category([OWAndSI, P1])
    void case_2_2_1_1_4() {
        //Add a name
        String name = "BLS Name to be added"
        blsPage.navigateToURL()
        blsPage.addName(name)
        def names = blsPage.getNameOptions()
        checkTrue("New BLS name should be added to ddl.", names.contains(name))

        //Delete the added name
        blsPage.selectName(name)
        blsPage.deleteName(name)
    }
}


