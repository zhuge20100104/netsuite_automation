package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.SI
import com.netsuite.testautomation.aut.system.NSCredentials
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category
/**
 * Description:
 * Check footer on Single Voucher
 * This is a P1-Sanity case.
 * <p>
 * <p>Copyright (C) 2000 - 2018, NetSuite, Inc.</p>
 * @CaseID: 1.11.2.1.1
 * @author lifang.tang@oracle.com
 * @version 2018/5/24
 * @since 2018/5/24
 */
@TestOwner ("lifang.tang@oracle.com")
class SingleVoucherCheckFooterTest extends VoucherP2BaseTest{
    VoucherPrintPage voucherPrintPage

    def loginAsUser(user,pwd){
        NSCredentials userLogin = new NSCredentials([user: user, password: pwd])
        boolean loginSuccess = ((NetSuiteAppCN) context).logInViaForm(userLogin)
        if (!loginSuccess)
            return loginSuccess
        loginSuccess = context.answerSecurityQuestion(["What was your childhood nickname?"                    : "nickname",
                                                       "In what city or town did your mother and father meet?": "town",
                                                       "In what city or town was your first job?"             : "city"])
        if (!loginSuccess)
            return loginSuccess
    }

    def openJournalEditPage(journalID){
        String url = context.executeScript("return nlapiResolveURL('RECORD','journalentry',${journalID},'EDIT');")
        context.navigateTo(url)
        context.waitForPageToLoad()
    }

    @Category([P0.class, SI.class])
    @Deprecated
    //User is hard coded so deprecated this case
    void case_1_11_2_1_1(){
        def testData = cData.data("test_1_11_2_1_1")

        def recordData = testData.record()
        dirtyData = record.createRecord(recordData)
        def journalId = dirtyData[0].internalid
        println(journalId)

        def data = testData.data()

        //manager Approve
        loginAsUser(data[0].username,data[0].password)
        openJournalEditPage(journalId)
        //  record.updateRecord("journalentry",journalId,data[0].update)
        currentRecord.setValue(data[0].update)
        //Save
        context.getElement("//*[@id='btn_multibutton_submitter']").click()

        //director approve
        loginAsUser(data[1].username,data[1].password)
        openJournalEditPage(journalId)
        //  record.updateRecord("journalentry",journalId,data[1].update)
        currentRecord.setValue(data[1].update)
        //Save
        context.getElement("//*[@id='btn_multibutton_submitter']").click()


        //refresh report
        navigateToVoucherPrintPage()
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        def MULTICURRENCY = context.executeScript(script).toBoolean()
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY)
        voucherPrintPage.setParameters(testData.labels(),null)
        voucherPrintPage.clickRefreshBtn()

        // If fail, Please check if workflow approval enabled and check the configuration Setup->Users/Roles->管理记账/审核/制单人
        def expect = testData.expect()
        try {
            def footer = voucherPrintPage.getVoucherReportFooter(0)
            checkAreEqual("Verify Creator", expect[0].creator, footer.creator)
            checkAreEqual("Verify Approver", expect[0].approver, footer.approver)
            checkAreEqual("Verify Poster", expect[0].poster, footer.poster)
        }
        catch (Exception e){
            e.printStackTrace()
        }

    }
}
