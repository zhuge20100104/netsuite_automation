package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteSetup
import groovy.json.JsonSlurper
import org.junit.Assert
import org.junit.Test
import org.junit.After
import org.junit.experimental.categories.Category

@TestOwner("jing.han@oracle.com")
class CashflowJournalFilterTest extends CashflowBaseTest {

    @Inject
    protected JournalEntryPage journalEntryPage

    static HashSet expectInFlowItems
    static HashSet expectOutFlowItems
    static HashSet expectAllFlowItems

    @SuiteSetup
    void setUpTestSuite() {
        super.setUpTestSuite()
        expectInFlowItems = getCFSItemFromJson("IN")
        expectOutFlowItems = getCFSItemFromJson("OUT")
        expectAllFlowItems = getCFSItemFromJson("ALL")
    }

    def response = ""
    def responseObj = {}

    def getDefaultRole() {
        return getAdministrator()
    }

    @After
    void tearDown() {
        if (response != "") {
            context.deleteTransaction(response)
        }
        super.tearDown()
    }

    /**
     * @CaseID Cashflow 1.46.1.1
     * @author jing.han@oracle.com
     * Description: Journal CFS filter
     *      CFS inflows item display & Positive amount in Credit
     *          1. New a journal
     *          2. positive amount in Debit -> Outflow
     *          3. positive amount in Credit-> Inflow
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_46_1_1() {
        def dataString = context.getFileContent("test case 1.46.1.1", "cashflow//case_1_46_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def uiObj = context.getUIDataObj(caseObj, "CFS1.46.1.1")

        switchToRole(accountant)

        // Set Header
        String subsidiary = uiObj.main.subsidiary
        String subId = Utility.getSubsidiaryId(context, subsidiary, null)
        journalEntryPage.navigateToURL()
        journalEntryPage.setSubsidiary(subId)

        EditMachineCN sublist = context.withinEditMachine("line")

        // 1.Check All inflows/outflows are available
        checkCFSItems(expectAllFlowItems, sublist)

        // 2.Enter positive amount in debit, only outflows are available
        sublist.setFieldWithText("account", uiObj.item[2].account)
        sublist.setFieldWithValue("debit", uiObj.item[2].debit)
        checkCFSItems(expectOutFlowItems, sublist)

        // next line
        sublist.add()
        context.waitForPageToLoad()

        // 3.Enter positive amount in credit, only inflows are available
        sublist.setFieldWithText("account", uiObj.item[3].account)
        sublist.setFieldWithValue("credit", uiObj.item[3].credit)
        checkCFSItems(expectInFlowItems, sublist)

    }

    /**
     * @CaseID Cashflow 1.46.1.2
     * @author jing.han@oracle.com
     * Description: Journal CFS filter
     *      CFS inflows item display & Negative amount in Debit
     *          1. Negative amount in Debit -> Inflow
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_46_1_2() {
        def dataString = context.getFileContent("test case 1.46.1.2", "cashflow//case_1_46_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def uiObj = context.getUIDataObj(caseObj, "CFS1.46.1.2")

        switchToRole(accountant)

        // Set Header
        String subsidiary = uiObj.main.subsidiary
        String subId = Utility.getSubsidiaryId(context, subsidiary, null)
        journalEntryPage.navigateToURL()
        journalEntryPage.setSubsidiary(subId)
        EditMachineCN sublist = context.withinEditMachine("line")

        // Enter negative amount in debit, only inflows are available
        sublist.setFieldWithText("account", uiObj.item[2].account)
        sublist.setFieldWithValue("debit", uiObj.item[2].debit)
        checkCFSItems(expectInFlowItems, sublist)

        // next line
        sublist.add()
        context.waitForPageToLoad()

        // Enter positive amount in credit, only inflows are available
        sublist.setFieldWithText("account", uiObj.item[3].account)
        sublist.setFieldWithValue("debit", uiObj.item[3].debit)
        checkCFSItems(expectInFlowItems, sublist)

    }

    /**
     * @CaseID Cashflow 1.46.1.3
     * @author jing.han@oracle.com
     * Description: Journal CFS filter
     *      CFS outflows item display & Positive amount in Debit
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_46_1_3() {
        def dataString = context.getFileContent("test case 1.46.1.3", "cashflow//case_1_46_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def uiObj = context.getUIDataObj(caseObj, "CFS1.46.1.3")

        switchToRole(accountant)

        // Set Header
        String subsidiary = uiObj.main.subsidiary
        String subId = Utility.getSubsidiaryId(context, subsidiary, null)
        journalEntryPage.navigateToURL()
        journalEntryPage.setSubsidiary(subId)
        EditMachineCN sublist = context.withinEditMachine("line")

        // Debit -> outflow
        sublist.setFieldWithText("account", uiObj.item[0].account)
        sublist.setFieldWithValue("debit", uiObj.item[0].debitOld)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[0].custcol_cseg_cn_cfi)
        checkCFSItems(expectOutFlowItems, sublist)
        // Debit 20 -> 10
        def before = uiObj.item[0].custcol_cseg_cn_cfi
        sublist.setFieldWithValue("debit", uiObj.item[0].debitNew)
        def after = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("CFS should remained", before, after)

        // Negative Debit -> Inflow
        sublist.setFieldWithText("account", uiObj.item[1].account)
        sublist.setFieldWithValue("debit", uiObj.item[1].debitOld)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[1].custcol_cseg_cn_cfi)
        checkCFSItems(expectInFlowItems, sublist)
        // Debit -10 -> 10
        sublist.setFieldWithValue("debit", uiObj.item[1].debitNew)
        after = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("CFS should cleared", "", after)
        checkCFSItems(expectOutFlowItems, sublist)

        // next line
        sublist.add()
        context.waitForPageToLoad()

        // Credit -> inflow
        sublist.setFieldWithText("account", uiObj.item[2].account)
        sublist.setFieldWithValue("credit", uiObj.item[2].credit)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[2].custcol_cseg_cn_cfi)
        checkCFSItems(expectInFlowItems, sublist)
        // credit -> debit
        sublist.setFieldWithValue("debit", uiObj.item[2].debit)
        after = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("CFS should cleared", "", after)
        checkCFSItems(expectOutFlowItems, sublist)

        // next line
        sublist.add()
        context.waitForPageToLoad()

        // Negative Credit -> Outflow
        sublist.setFieldWithText("account", uiObj.item[3].account)
        sublist.setFieldWithValue("credit", uiObj.item[3].credit)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[3].custcol_cseg_cn_cfi)
        before = uiObj.item[3].custcol_cseg_cn_cfi
        checkCFSItems(expectOutFlowItems, sublist)
        // Debit -> Outflow
        sublist.setFieldWithValue("debit", uiObj.item[3].debit)
        after = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("CFS should remained", before, after)
        checkCFSItems(expectOutFlowItems, sublist)

    }

    /**
     * @CaseID Cashflow 1.46.1.4
     * @author jing.han@oracle.com
     * Description: Foreign Currency, Journal CFS filter
     */
    @Test
    @Category([OW.class, P3.class])
    void case_1_46_1_4() {
        def dataString = context.getFileContent("test case 1.46.1.4", "cashflow//case_1_46_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def uiObj = context.getUIDataObj(caseObj, "CFS1.46.1.4")

        switchToRole(accountant)

        // Set Header
        String subsidiary = uiObj.main.subsidiary
        String currency = uiObj.main.currency
        String subId = Utility.getSubsidiaryId(context, subsidiary, null)
        journalEntryPage.navigateToURL()
        journalEntryPage.setSubsidiary(subId)
        context.setFieldWithText("currency", currency)
        EditMachineCN sublist = context.withinEditMachine("line")

        // credit -> inflow
        sublist.setFieldWithText("account", uiObj.item[0].account)
        sublist.setFieldWithValue("credit", uiObj.item[0].creditOld)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[0].custcol_cseg_cn_cfi)
        checkCFSItems(expectInFlowItems, sublist)
        // credit 1000 -> -1000
        sublist.setFieldWithValue("credit", uiObj.item[0].creditNew)
        // test test test
        def after = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("CFS should cleared", "", after)
        checkCFSItems(expectOutFlowItems, sublist)

        // Negative Debit -> outflow
        sublist.setFieldWithText("account", uiObj.item[1].account)
        sublist.setFieldWithValue("credit", uiObj.item[1].creditOld)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[1].custcol_cseg_cn_cfi)
        checkCFSItems(expectOutFlowItems, sublist)
        // Credit -2000 -> 1000
        def before = uiObj.item[1].custcol_cseg_cn_cfi
        sublist.setFieldWithValue("credit", uiObj.item[1].creditNew)
        after = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("CFS should remained", before, after)
        checkCFSItems(expectOutFlowItems, sublist)

        // next line
        sublist.add()
        context.waitForPageToLoad()

        // Negative Debit -> inflow
        sublist.setFieldWithText("account", uiObj.item[2].account)
        sublist.setFieldWithValue("debit", uiObj.item[2].debit)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[2].custcol_cseg_cn_cfi)
        checkCFSItems(expectInFlowItems, sublist)
        // Debit -3000 -> Credit -3000
        sublist.setFieldWithValue("credit", uiObj.item[2].credit)
        after = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("CFS should cleared", "", after)
        checkCFSItems(expectOutFlowItems, sublist)

        // next line
        sublist.add()
        context.waitForPageToLoad()

        // Debit -> Outflow
        sublist.setFieldWithText("account", uiObj.item[3].account)
        sublist.setFieldWithValue("debit", uiObj.item[3].debit)
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[3].custcol_cseg_cn_cfi)
        checkCFSItems(expectOutFlowItems, sublist)
        // Debit -6000 -> Credit -6000
        before = uiObj.item[3].custcol_cseg_cn_cfi
        sublist.setFieldWithValue("credit", uiObj.item[3].credit)
        after = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("CFS should remained", before, after)
        checkCFSItems(expectOutFlowItems, sublist)

    }

    /**
     * @CaseID Cashflow 1.46.2.1
     * @author jing.han@oracle.com
     * Description: Journal CFS filter
     *      Edit Mode & Positive amount in Debit/Credit
     */
    @Test
    @Category([OW.class, P1.class])
    void case_1_46_2_1() {
        def caseId = "test case 1.46.2.1"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_46_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data
        def uiObj = context.getUIDataObj(caseObj, "CFS1.46.2.1")
        response = createTransaction(caseId, dataObj)
        responseObj = jsonSlurper.parseText(response)
        String tranId = responseObj[0].internalid

        //Go to edit page
        journalEntryPage.navigateToEditURL(tranId)
        EditMachineCN sublist = context.withinEditMachine("line")

        // Check All inflows/outflows are available
        checkCFSItems(expectAllFlowItems, sublist)

        // Debit -> outflow
        sublist.setFieldWithText("account", uiObj.item[0].account)
        sublist.setFieldWithValue("debit", uiObj.item[0].debit)
        checkCFSItems(expectOutFlowItems, sublist)

        // Set outflow cfs
        sublist.setFieldWithText("custcol_cseg_cn_cfi", uiObj.item[0].custcol_cseg_cn_cfi)

        // Change debit -> 0, cfs cleared
        sublist.setFieldWithValue("debit", uiObj.item[0].zero)
        def actual = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("Amount is 0, CFS is not cleared", "", actual)

        // Credit -> inflow
        sublist.setFieldWithValue("credit", uiObj.item[0].credit)
        checkCFSItems(expectInFlowItems, sublist)

        // Change credit -> 0, cfs select null
        sublist.setFieldWithValue("credit", uiObj.item[0].zero)
        actual = sublist.getFieldText("custcol_cseg_cn_cfi")
        Assert.assertEquals("Amount is 0, CFS is not cleared", "", actual)

    }

    /**
     * @CaseID Cashflow 1.46.2.2
     * @author jing.han@oracle.com
     * Description: Journal CFS filter
     *      Make Copy Mode & Negative amount in Debit/Credit
     */
    @Test
    @Category([OW.class, P2.class])
    void case_1_46_2_2() {
        def caseId = "test case 1.46.2.1"
        def dataString = context.getFileContent(caseId, "cashflow//case_1_46_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data
        def uiObj = context.getUIDataObj(caseObj, "CFS1.46.2.1")
        response = createTransaction(caseId, dataObj)
        responseObj = jsonSlurper.parseText(response)
        String tranId = responseObj[0].internalid

        //Go to make copy page
        journalEntryPage.navigateToCopyURL(tranId)
        EditMachineCN sublist = context.withinEditMachine("line")

        // Negative Debit -> inflow
        sublist.setFieldWithText("account", uiObj.item[1].account)
        sublist.setFieldWithValue("debit", uiObj.item[1].debit)
        checkCFSItems(expectInFlowItems, sublist)

        // Negative Credit -> outflow
        sublist.setFieldWithValue("credit", uiObj.item[1].credit)
        checkCFSItems(expectOutFlowItems, sublist)

    }

    void checkCFSItems(HashSet expectItems, EditMachineCN sublist) {
        HashSet actualCFSItems = journalEntryPage.getJournalLineCFSOptions(journalEntryPage.FIELD_ID_ITEM_CFS, sublist)
        trimCFSOptions(actualCFSItems)
        assertCFS(expectItems, actualCFSItems)
    }

    void assertCFS(HashSet expect, HashSet actual) {
        if (expect && actual) {
            Assert.assertEquals("CFS item quantity is not match expected", expect.size(), actual.size())
            Assert.assertEquals("CFS items are not match expected", expect, actual)
        }
    }

}