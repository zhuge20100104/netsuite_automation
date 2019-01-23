package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject

/**
 * @Author: qin.w.wang@oracle.com
 * @Date: 2018/4/18
 * @Description:
 */
@TestOwner ("qin.w.wang@oracle.com")
class BalanceSheetFieldLinkageDisplayTest extends BalanceSheetAppTestSuite{
    @Inject
    private BalanceSheetPage bsPage
    @Inject
    private SubsidiaryPage subsidiaryPage

    @Override
    def getDefaultRole() {
        return getAdministrator() //getAccountant()
    }

    /**
     * @Anthour: qin.w.wang@oracle.com
     * @lastUpdateBy:
     * @caseID 10.1.4 Field Linkage
     * @Desciption:  Test steps:
     * 1. Set fiscalcalendar to "Oracle Fiscal Calendar" of subsidiary "China BlSheet 03"
     * 2. enter Reports > Financial > 中国资产负债表
     * 3. Select subsidairy "China BlSheet 01"
     * 4. Check the asof value list is correct
     * 5. Select subsidairy "China BlSheet 03"
     * 6. Check the asof value list is correct
     * 7. Set subsidiary "China BlSheet 03" fiiscalcalendar back to before
     */
    @Test
    @Category([P2.class, OW.class])
    void case_10_1_4(){
        if(context.isOneWorld()){
            subsidiaryPage.navigateToPage(data.test_case_10_1_4.subsidiary.name_China03, true)
            def fCalendar = subsidiaryPage.getFiscalCalendarTex()
            System.out.println("CalendaType_before:" +  fCalendar)
            UpdateSubsidaryFcalendar(data.test_case_10_1_4.subsidiary.name_China03, data.test_case_10_1_4.subsidiary.fiscalcalendar)

             //switchToRole(accountant)
            bsPage.navigateToURL()
            bsPage.selectSubsidiary(data.test_case_10_1_4.subsidiary.name_China01)
            checkAreEqual("Check asof value:",context.getCNdropdownOptions(BalanceSheetPage.FIELD_ID_ASOF)[2].substring(0,3), data.test_case_10_1_4.expected.value_sub_ch1)

            bsPage.selectSubsidiary(data.test_case_10_1_4.subsidiary.name_China03)
            checkAreEqual("Check asof value:",context.getCNdropdownOptions(BalanceSheetPage.FIELD_ID_ASOF)[2].substring(0,3), data.test_case_10_1_4.expected.value_sub_ch3)

            context.webDriver.closeBrowser()
            login()
            //set back the fiscal calendar value
            UpdateSubsidaryFcalendar(data.test_case_10_1_4.subsidiary.name_China03, fCalendar)
        }
    }


    def ClickPopUpMessage(){
        context.webDriver.acceptUpcomingConfirmationDialog()
        subsidiaryPage.navigateToPage(data.test_case_10_1_4.subsidiary.name_China03, true)
        context.waitForPageToLoad()
    }

    def UpdateSubsidaryFcalendar(subname, fcalvalue) {
            subsidiaryPage.navigateToPage(subname, true)
            subsidiaryPage.setFiscalCalendarTex(fcalvalue)
            subsidiaryPage.clickSave()
        }


    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BlanaceSheetFieldLinkageDisplayTest_zh_CN.json".replace("\\",SEP),
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BlanaceSheetFieldLinkageDisplayTest_en_US.json".replace("\\",SEP)
        ]
    }
}
