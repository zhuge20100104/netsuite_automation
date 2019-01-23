package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.income.IncomeAppTestSuite
import com.netsuite.chinalocalization.page.Report.IncomeStatementPage
import com.netsuite.common.*
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject

@TestOwner("yang.g.liu@oracle.com")
class IncomeNameDropDownTest extends IncomeAppTestSuite {

    @Inject
    private IncomeStatementPage incomeStatementPage

    @Override
    def getDefaultRole() {
        return getAdministrator()
    }

    /**
     * @author yang.g.liu@oracle.com
     * @lastUpdateBy
     * @caseID  2.1.2.11&2.1.2.12  Create and delete a Income Statement name
     * @desc 1. Create a name from Income Statement drop down list-> verify it been added
     *           2. Delete a name from Income Statement drop down list-> verify it been deleted
     */
    @Test
    @Category([OWAndSI, P1])
    void  Case_2_1_2_11_12() {
        //Add a name
        String name = "IS Name to be added"
        navigateToPortalPage()
        incomeStatementPage.addName(name)
        def names = incomeStatementPage.getNameOptions()
        checkTrue("New IS name should be added to ddl.", names.contains(name))

        //Delete the added name
        incomeStatementPage.selectName(name)
        incomeStatementPage.deleteName(name)
    }
}


