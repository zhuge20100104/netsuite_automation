package com.netsuite.chinalocalization.page.common

import com.netsuite.chinalocalization.common.ext.HTMLTable
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.aut.pageobjects.TablePortlet
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser

/**
 * Created by stepzhou on 3/27/2018.
 */
class UnrealizedExchangePage extends PageBaseAdapterCN {
    private static String URL = "/app/reporting/reportrunner.nl?cr=-133&whence="

    private static final String SEL_FROM_LOCATOR = "inpt_crit_1_from"
    private static final String SEL_TO_LOCATOR = "inpt_crit_1_to"
    private static final String SEL_SUBSIDIARY = "inpt_crit_2"

    private static final String REFRESH = "//*[@id='refresh']"

    void navigateToPage() {
        context.navigateTo(URL)
    }

    void selectFrom(String startDate) {
        context.withinDropdownlist(SEL_FROM_LOCATOR).selectItem(startDate)
    }

    void selectTo(String endDate) {
        context.withinDropdownlist(SEL_TO_LOCATOR).selectItem(endDate)
    }

    void selectSubsidiary(String subsidiary) {
        context.withinDropdownlist(SEL_SUBSIDIARY).selectItem(subsidiary)
    }

    void clickRefreshBtn() {
        context.withinHtmlElementIdentifiedBy(Locator.xpath(REFRESH)).click()
    }

    /**
     * This method is pending for the final confirmation.
     * For now, just not implemented.
     * */
    boolean verifyExchangeRateGainLoss(String pureGL, String rowHeader) {
        String headerTableID = "rptcolheader"
        String contentTableID = "rptdataarea"

        /*
        HTMLTable headerTable = new HTMLTable(context, headerTableID)
        int columnIndex = headerTable.getColumnIndex(pureGL)
        HTMLTable contentTable = new HTMLTable(context, contentTableID)
        int rowIndex = contentTable.getRowIndex(rowHeader, columnIndex)
        contentTable.getCellContent(rowIndex, columnIndex)
        */

        return true
    }
}