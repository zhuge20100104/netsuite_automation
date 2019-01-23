package com.netsuite.chinalocalization.voucher

import com.netsuite.chinalocalization.voucher.VoucherPrintBaseTest
import com.netsuite.chinalocalization.voucher.VoucherSetUpBasetest
import com.netsuite.chinalocalization.page.voucher.SettingPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * @TestOwner: Chenguang.wang@oracle.com
 * @Date: 3/16/2018
 * @Desc:  In VoucherSettingTest Page (Setup > Users/Roles > 管理记账/审核/制单人)
 */
@TestOwner("Chenguang.Wang")
class VoucherSettingTest extends VoucherSetUpBasetest{
    def settingPage

    def pathToTestDataFiles() {
        println("*****************pathToTestDataFiles")
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\voucher\\data\\VoucherSettingTest_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\voucher\\data\\VoucherSettingTest_en_US.json"
        ]
        println("*****************pathToTestDataFiles end")
    }

    /**
     * @Description: In Voucher Setting, Cancle it and check whether it have already cancled.
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/16/2018
     * @CaseID 1.15.1.9
     */
    @Test
    void case_1_15_1_9() {
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_15_1_9____1_15_1_10

        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        int ListNum = settingPage.countList()
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactiontype, rowInput.user, rowInput.date, "")
        settingPage.clickCancleBtn()
        Assert.assertEquals(ListNum, settingPage.countList())
    }

    /**
     * @Description: Expected 1.15.1.10, click save button this time
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/19/2018
     * @CaseID 1.15.1.10
     */
    @Test
    void case_1_15_1_10() {
        settingPage = new SettingPage(context)
        pathToTestDataFiles()
        def rowInput = data.case_1_15_1_9____1_15_1_10
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactiontype, rowInput.user, rowInput.date, "")
        settingPage.clickSaveBtn()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Japan_BU)
        assertTrue("The Subsidiary is expected", settingPage.CheckSubsidiary(settingPage.Subsidiary_Japan_BU))
    }

    /**
     * @author yang.g.liu@oracle.com
     * Description:
     *       Seed data:"type":"Creator",
     *                "transectiontype":"Journal",
     *                "user":"auto1.03",
     *                "date":"8/24/2017"
     *      Case_1.15.2.2 Link right page,the Manage creator/ approver / poster page could be open normally.
     *      Case_1.16.1.1.1 Create a same type Creator,The effective period can't overlap and show an error message
     *
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void case_1_16_1_1_1() {
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_1
        settingPage.navigatePage()
        settingPage.waitForPageToLoad()
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactionType,rowInput.user, rowInput.date, rowInput.endDate)
        settingPage.clickSaveButton()
        def expectedError = isTargetLanguageEnglish() ? VoucherMsgEnum.OVERLAP_DATE_RANGE.getEnLabel() : VoucherMsgEnum.OVERLAP_DATE_RANGE.getCnLabel()
        assertAreEqual("The effective period can't overlap", expectedError, settingPage.getErrorMessage())
    }

    /**
     * @author yang.g.liu@oracle.com
     * Description:
     *       Seed data:"type":"Poster",
     *                "transectiontype":"Journal",
     *                "user":"auto1.03",
     *                "date":"8/26/2017"
     *                "endDate":"8/27/2017"
     *      Case_1.15.2.2 Link right page,the Manage creator/ approver / poster page could be open normally.
     *      Case_1.16.1.1.1 Create a same type Poster,The effective period can't overlap and show an error message
     *
     */
    @Test
    @Category([OWAndSI.class, P0.class])
    void case_1_16_2_1_1() {
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_2_1_1
        settingPage.navigatePage()
        settingPage.waitForPageToLoad()
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactionType,rowInput.user, rowInput.date, rowInput.endDate)
        settingPage.clickSaveButton()
        def expectedError = isTargetLanguageEnglish() ? VoucherMsgEnum.OVERLAP_DATE_RANGE.getEnLabel() : VoucherMsgEnum.OVERLAP_DATE_RANGE.getCnLabel()
        assertAreEqual("The effective period can't overlap", expectedError, settingPage.getErrorMessage())
    }

    /**
     * @Description:  Voucher Create a same type Creator
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/19/2018
     * @CaseID 1.16.1.1.4
     */
    @Test
    void case_1_16_1_1_4() {
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_4
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        settingPage.addVaucher(settingPage.countList(), rowInput.No1.type, rowInput.No1.transactiontype, rowInput.No1.user, rowInput.No1.date, "")
        settingPage.clickSaveBtn()
        settingPage.addVaucher(settingPage.countList(), rowInput.No2.type, rowInput.No2.transactiontype, rowInput.No2.user, rowInput.No2.date, rowInput.No2.enddate)
        settingPage.clickSaveBtn()
        settingPage.verifyAllItemGrayed(settingPage.countList())
    }

    /**
     * @Description: Change a same type Creator Saved Data's End Date > Current date(2017-8-1) Modify Data's End Date > Current date
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/20/2018
     * @CaseID 1.16.1.1.7
     */
    @Test
    void case_1_16_1_1_7() {
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_7
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactiontype, rowInput.user, rowInput.date, rowInput.enddate)
        settingPage.clickSaveBtn()
        settingPage.changeValueInSavedRow(settingPage.countList(), settingPage.Enddate_Index, rowInput.changedenddate)
        settingPage.verifyAllItemGrayedExceptSomeone(settingPage.countList(), settingPage.Enddate_Index)
    }

    /**
     * @Description: Change a same type Creator Saved Data's End Date > Current date(2017-8-1) Modify Data's End Date = Current date
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/20/2018
     * @CaseID 1.16.1.1.8
     */
    @Test
    void case1_16_1_1_8() {
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_8
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        String currentDate = settingPage.getCurrentDate()
        println(currentDate) // todo delete this line
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactiontype, rowInput.user, rowInput.date, rowInput.enddate)
        settingPage.clickSaveBtn()
        settingPage.changeValueInSavedRow(settingPage.countList(), settingPage.Enddate_Index, currentDate)
        settingPage.verifyAllItemGrayedExceptSomeone(settingPage.countList(), settingPage.Enddate_Index)
    }

    /**
     * @Description: Change a same type Creator Saved Data's End Date > Current date(2017-8-1) Modify Data's End Date < Current date
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     * @CaseID 1.16.1.1.9
     */
    @Test
    void case_1_16_1_1_9(){
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_9
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        println("type: " + rowInput.type + "\n" +
                "trantype: " + rowInput.transactiontype + "\n" +
                "user: " + rowInput.user + "\n" +
                "date: " + rowInput.date + "\n" +
                "enddate: " + rowInput.enddate + "\n")
        // TODO Delete this println before commit
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactiontype, rowInput.user, rowInput.date, rowInput.enddate)
        settingPage.clickSaveBtn()
        settingPage.changeValueInSavedRow(settingPage.countList(), settingPage.Enddate_Index, rowInput.changedenddate)
        settingPage.verifyAllItemGrayed(settingPage.countList())
    }

    /**
     * @Description: Change a same type Creator Saved Data's End Date = Current date(2017-8-1) Modify Data's End Date < Current date
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     * @CaseID 1.16.1.1.11
     */
    @Test
    void case_1_16_1_1_11(){
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_11
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactiontype, rowInput.user, rowInput.date, rowInput.enddate)
        settingPage.clickSaveBtn()
        settingPage.changeValueInSavedRow(settingPage.countList(), settingPage.Enddate_Index, rowInput.changedenddate)
        settingPage.verifyAllItemGrayed(settingPage.countList())
    }

    /**
     * @Description: Change a same type Creator Saved Data's End Date = Current date(2017-8-1) Modify Data's End Date = Current date
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     * @CaseID 1.16.1.1.12
     */
    @Test
    void case_1_16_1_1_12(){
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_12
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        String currentDate = settingPage.getCurrentDate()
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactiontype, rowInput.user, rowInput.date, currentDate)
        settingPage.clickSaveBtn()
        settingPage.changeValueInSavedRow(settingPage.countList(), settingPage.Enddate_Index, currentDate)
        settingPage.verifyAllItemGrayedExceptSomeone(settingPage.countList(), settingPage.Enddate_Index)
    }

    /**
     * @Description: Change a same type Creator Saved Data's End Date = Current date(2017-8-1) Modify Data's End Date > Current date
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     * @CaseID 1.16.1.1.13
     */
    @Test
    void case_1_16_1_1_13(){
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_13
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        String currentDate = settingPage.getCurrentDate()
        settingPage.addVaucher(settingPage.countList(), rowInput.type, rowInput.transactiontype, rowInput.user, rowInput.date, currentDate)
        settingPage.clickSaveBtn()
        settingPage.changeValueInSavedRow(settingPage.countList(), settingPage.Enddate_Index, rowInput.changedenddate)
        settingPage.verifyAllItemGrayedExceptSomeone(settingPage.countList(), settingPage.Enddate_Index)
    }

    /**
     * @Description: Change a same type Creator Saved Data's End Date < Current date(2017-8-1) Modify Effective date > Saved Data's End Date
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     * @CaseID 1.16.1.1.15
     */

    void case_1_16_1_1_15(){
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_15
        switchToRole(administrator)
        // There is issue in this case, need further debug.
        /*settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        settingPage.addVaucher(settingPage.countList(), rowInput.No1.type, rowInput.No1.transactiontype, rowInput.No1.user, rowInput.No1.date, rowInput.No1.enddate)
        settingPage.clickSaveBtn()
        settingPage.addVaucher(settingPage.countList(), rowInput.No2.type, rowInput.No2.transactiontype, rowInput.No2.user, rowInput.No2.date, rowInput.No2.enddate)
        settingPage.clickSaveBtn()
        settingPage.verifyAllItemGrayed(settingPage.countList())*/
    }

    /**
     * @Description: "Create a same type Creator Saved Data's End Date < Current date(2017-8-1) Create End Date < Saved Data's  Effective date"
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     * @CaseID 1.16.1.1.17
     */
    @Test
    void case_1_16_1_1_17(){
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_1_17
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        settingPage.addVaucher(settingPage.countList(), rowInput.No1.type, rowInput.No1.transactiontype, rowInput.No1.user, rowInput.No1.date, rowInput.No1.enddate)
        settingPage.clickSaveBtn()
        settingPage.addVaucher(settingPage.countList(), rowInput.No2.type, rowInput.No2.transactiontype, rowInput.No2.user, rowInput.No2.date, rowInput.No2.enddate)
        settingPage.clickSaveBtn()
        settingPage.verifyAllItemGrayed(settingPage.countList())
    }

    /**
     * @Description: Create a different transaction type Approver
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     * @CaseID 1.16.1.2.2
     */
    @Test
    void case_1_16_1_2_2(){
        settingPage = new SettingPage(context)
        def rowInput = data.case_1_16_1_2_2
        switchToRole(administrator)
        settingPage.navigatePage()
        settingPage.chooseSubsidiary(settingPage.Subsidiary_Korea_BU)
        settingPage.addVaucher(settingPage.countList(), rowInput.No1.type, rowInput.No1.transactiontype, rowInput.No1.user, rowInput.No1.date, "")
        settingPage.clickSaveBtn()
        settingPage.addVaucher(settingPage.countList(), rowInput.No2.type, rowInput.No2.transactiontype, rowInput.No2.user, rowInput.No2.date, "")
        settingPage.clickSaveBtn()
        settingPage.verifyAllItemGrayed(settingPage.countList())
    }
}
