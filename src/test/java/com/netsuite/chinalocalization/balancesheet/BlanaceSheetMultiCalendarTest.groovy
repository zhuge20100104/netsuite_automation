package com.netsuite.chinalocalization.balancesheet

import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.chinalocalization.page.Report.ReportPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.CN
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject
/**
 * @Author: fangfang.zhang@oracle.com
 * @Date: 2018/4/27
 * @Description:
 */
@TestOwner ("fangfang.zhang@oracle.com")
class BlanaceSheetMultiCalendarTest extends BalanceSheetAppTestSuite {
    @Inject
    private BalanceSheetPage bsPage
    @Inject
    private SubsidiaryPage subsidiaryPage
    @Inject
    private ReportPage reportPage

    @Override
    def getDefaultRole() {
        return getAdministrator() //getAccountant()
    }
    /**
     *  First Load without current month period
     */
    @Test
    @Category([OW.class, P2.class, CN.class])
    void case_10_1_2(){
        if(context.isOneWorld()){
            subsidiaryPage.navigateToPage(data.test_case_10_1_2.subsidiary.name, true)
            def fCalendar = subsidiaryPage.getFiscalCalendarTex()
            subsidiaryPage.setFiscalCalendarTex(data.test_case_10_1_2.subsidiary.fiscalcalendar)
            subsidiaryPage.clickSave()

             //switchToRole(accountant)
            bsPage.navigateToURL()
            checkAreEqual("Check the AsOf value is empty", context.getFieldText(BalanceSheetPage.FIELD_ID_ASOF), data.test_case_10_1_2.expected)

            //set back the fiscal calendar value
            //switchToRole(administrator)
            subsidiaryPage.navigateToPage(data.test_case_10_1_2.subsidiary.name, true)
            subsidiaryPage.setFiscalCalendarTex(fCalendar)
            subsidiaryPage.clickSave()
        }
    }
    /**
     *  Report of fiscal year in Oracle Fiscal Calendar
     */
    @Test
    @Category([OW.class, P2.class, CN.class])
    void case_10_2_6(){
        if(context.isOneWorld()){
            subsidiaryPage.navigateToPage(data.test_case_10_2_6.subsidiary.name, true)
            def fCalendar = subsidiaryPage.getFiscalCalendarTex()
            subsidiaryPage.setFiscalCalendarTex(data.test_case_10_2_6.subsidiary.fiscalcalendar)
            subsidiaryPage.clickSave()

            //get line values from saved report
             //switchToRole(accountant)
            changeLanguageToEnglish()
            def savedReportData = reportPage.getReportData(data.test_case_10_2_6.savedReport)
            def blsReportData = bsPage.getBalanceSheetData(data.test_case_10_2_6.blsReport)
            checkReportDataEqual(blsReportData, savedReportData)

            //set back the fiscal calendar value
            //switchToRole(administrator)
            subsidiaryPage.navigateToPage(data.test_case_10_2_6.subsidiary.name, true)
            subsidiaryPage.setFiscalCalendarTex(fCalendar)
            subsidiaryPage.clickSave()
        }
    }

    private void checkReportDataEqual(blsReportData, savedReportData){
        def TOTAL_ACCOUNT_LABEL_MAP = ["Total current assets": "流动资产合计",
                                       "Total non-current assets": "非流动资产合计",
                                       "Total assets": "资产总计",
                                       "Total current liabilities": "流动负债合计",
                                       "Total non-current liabilities": "非流动负债合计",
                                       "Total liabilities": "负债合计",
                                       "Total owner's equity": "所有者权益（或股东权益）合计",
                                       "Total liabilities and owner's equity": "负债和所有者权益（或股东权益）总计"]

        def BALANCE_COLUMN_LABEL_MAP = ["期末余额":"Closing Balance" , "期初余额":"Opening Balance"]

        savedReportData.each{k, v ->
            def lineValue = [:]
            v.each{column, value ->
                BALANCE_COLUMN_LABEL_MAP.each {cn, en ->
                    if(column.startsWith(cn)){
                        lineValue[en] = value
                    }
                }
            }
            v.clear()
            v.putAll(lineValue)
        }

        blsReportData.each{k, v ->
            if(TOTAL_ACCOUNT_LABEL_MAP.containsKey(k)){
                checkAreEqual("Total account value is equal; total account:"+ k, v, savedReportData[TOTAL_ACCOUNT_LABEL_MAP[k]])
            } else{
                checkAreEqual("Account value is equal; account:"+ k, v, savedReportData[k])
            }
        }
    }

    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BlanaceSheetMultiCalendar_zh_CN.json".replace("\\",SEP),
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BlanaceSheetMultiCalendar_en_US.json".replace("\\",SEP)
        ]
    }
}
