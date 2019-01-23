package com.netsuite.chinalocalization.page.voucher

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator

class GLnumberingPage extends PageBaseAdapterCN {

    private static final String RUN_BTN = '/*//*[@id="run"]'
    private static final String CHECKBOX = '//*[@id="sequence_cb1_fs"]/img'
    private static final String LAST_ASSIGNED = '//*[@id="sequencerow0"]/td[12]'
    private static final String LAST_ASSIGNED_SI = '//*[@id="sequencerow0"]/td[11]'

    private static final String REFRESH_BTN = '//*[@id="refresh"]'
    private static final String DATESORT_BTN = '//*[@id="dir0"]'
    private static final String PROCESS = '//*[@id="row0"]/td[4]'

    GLnumberingPage navigateToPage() {
        //def url = 'https://system.na3.netsuite.com/app/accounting/transactions/glnumbering/glnum.nl?whence='
        def url = '/app/accounting/transactions/glnumbering/glnum.nl?whence='
        context.navigateTo(url);
        return this;
    }

    void setPeriodType(text) {
        context.setFieldWithText('periodtype', text)
    }

    void setOpenPeriods(text) {
        context.setFieldWithText('periodname', text)
    }

    void clickRun() {
        context.webDriver.click(Locator.xpath(RUN_BTN))
    }

    void clickCheckBox() {
        context.webDriver.click(Locator.xpath(CHECKBOX))
    }

    void clickRefresh() {
        context.webDriver.click(Locator.xpath(REFRESH_BTN))
    }

    /**
     * @desc get last assigned number
     * @return last assigned number
     */
    def getLastAssigned() {
        def el
        Thread.sleep(2000)
        if (context.isOneWorld()) {
            el = context.webDriver.getHtmlElementByLocator(Locator.xpath(LAST_ASSIGNED))
        }
        else {
            el = context.webDriver.getHtmlElementByLocator(Locator.xpath(LAST_ASSIGNED_SI))
        }
        String value = el.getAsText()
        return value
    }

    /**
     * @desc judge the sort of date is descending order
     * @return if descending order return true, or return false
     */
    def isDateDesc() {
        def el = context.webDriver.getHtmlElementByLocator(Locator.xpath(DATESORT_BTN))
        def attr = el.getAttributeValue('class')
        if (attr.equals('listheadersortdown')) {
            return true
        }
        return false
    }

    /**
     * @desc click the button of sortting date
     */
    void clickDateSort() {
        context.webDriver.click(Locator.xpath(DATESORT_BTN))
        context.webDriver.waitForPageToLoad();
    }

    /**
     * @desc judge process if completed
     * @return if completed return true, or return false
     */
    def isComplete() {
        def el = context.webDriver.getHtmlElementByLocator(Locator.xpath(PROCESS))
        def value = el.getAsText()
        if (value.equals('100.0%')) {
            return true
        }
        return false
    }


    def runGLNumber(def periodType, String periodName) {
        navigateToPage()
        if (context.isOneWorld()) {
            setPeriodType(periodType)
            setOpenPeriods(periodName)
        }
        setOpenPeriods(periodName.substring(0,7).trim())
        def glNumber = getLastAssigned()
        //If the first run , glNumber=0
        if(glNumber==""){
            glNumber="0"
        }
        clickCheckBox()
        clickRun()
        return Integer.parseInt(glNumber)
    }
}
