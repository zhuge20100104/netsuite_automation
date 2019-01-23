package com.netsuite.chinalocalization.page.voucher

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.chinalocalization.vat.components.XDropdownList
import com.netsuite.testautomation.html.Locator

class SettingPage extends PageBaseAdapterCN {

    //region WebDriver related methods
    def getWebDriver() {
        return context.webDriver
    }

    //region click element part
    def asDropdownList(options) {
        if (options.fieldId) {
            return getContext().withinDropdownlist(options.fieldId)
        } else if (options.locator) {
            return new XDropdownList(getContext(), getContext().webDriver, Locator.xpath(options.locator))
        }
    }


    def URL = "/app/site/hosting/scriptlet.nl?script=321&deploy=1&compid=4803899&whence="
    def DropList_ChinaBU = "//*[@id='subsidiary_fs']"
    def List_Table = "//*[contains(@id,'recmachcustrecord_subsidiary_line_row_')]"
    def Type = "//*[@id='recmachcustrecord_subsidiary_line_custrecord_type_fs']"
    def Transection_Type = "//*[@id='recmachcustrecord_subsidiary_line_custrecord_transaction_type_fs']"
    def User = "//*[@id='recmachcustrecord_subsidiary_line_custrecord_user_fs']"
    def Index_Head = "//*[@id='recmachcustrecord_subsidiary_line_splits']/tbody/tr[%d]/td[%d]"
    def Date = "//*[@id='custrecord_start_date']"
    def CancleBtn = "//*[@id='tdbody_cancel']"
    def SaveBtn = "//*[@id='secondarysubmitter']"
    def Row_Head = "//*[@id='recmachcustrecord_subsidiary_line_row_%d']"
    def DeleteBtn = "//*[@id='recmachcustrecord_subsidiary_line_remove']"
    def End_Date = "//*[@id='custrecord_end_date']"
    def final Subsidiary_China_BU = "Parent Company : China BU"
    def final Subsidiary_Korea_BU = "Parent Company : Korea BU"
    def final Subsidiary_Japan_BU = "Parent Company : Japan BU"
    def Message_Box = "//*[contains(@id,'message-')]"
    def Message_Box_Confirm = "//*[contains(@id,'message-')]/div[2]/div/div/button"
    private static final String XPATH_ERR = "//span[@class='uir-message-text']";

    final int Type_Index = 1
    final int TransactionType_Index = 2
    final int User_Index = 3
    final int Date_Index = 4
    final int Enddate_Index = 5

    SettingPage(def context) {
        this.context = context
    }

    void navigatePage() {
        def url = context.resolveURL("customscript_sl_cn_voucher_manage", "customdeploy_sl_cn_voucher_manage")
        context.navigateTo(url as String)
    }

    void chooseSubsidiary(choice) {
        asDropdownList(locator: DropList_ChinaBU).selectItem(choice)
    }

    int countList() {
        List table = context.webDriver.getHtmlElementsByLocator(Locator.xpath(List_Table))
        return table.size()
    }

    void chooseDropListItem(int row_index, int column_index, droplist, item) {
        if (column_index == 1){
            asDropdownList(locator: droplist).selectItem(item)
        } else {
            asClick(String.format(Index_Head, row_index+2, column_index))
            asDropdownList(locator: droplist).selectItem(item)
        }
    }

    void chooseDate(int row_index, int column_index, date) {
        asClick(String.format(Index_Head, row_index+2, column_index))
        context.setFieldIdentifiedByWithValue(Locator.xpath(Date), date)
        asClick(String.format(Index_Head, row_index+2, column_index))
    }


    void chooseEndDate(int row_index, int column_index, end_date) {
        asClick(String.format(Index_Head, row_index+2, column_index))
        context.setFieldIdentifiedByWithValue(Locator.xpath(End_Date), end_date)
    }

    void clickCancleBtn() {
        asClick(CancleBtn)
    }

    void clickSaveBtn() {
        asClick(SaveBtn)
        if (!context.webDriver.getHtmlElementByLocator(Locator.xpath(Message_Box))) {
            return
        } else {
            asClick(Message_Box_Confirm)
        }
    }
    void clickSaveButton() {
        asClick(SaveBtn)
    }

    void deleteRow(int row_num) {
        asClick(Locator.xpath(String.format(Row_Head, row_num)))
        asClick(Locator.xpath(DeleteBtn))
    }

    void addVaucher(int row_index, type, transection_type, user, date, end_date) {
        chooseDropListItem(row_index, Type_Index, Type, type)
        chooseDropListItem(row_index, TransactionType_Index, Transection_Type, transection_type)
        chooseDropListItem(row_index, User_Index, User, user)
        chooseDate(row_index, Date_Index, date)
        chooseEndDate(row_index, Enddate_Index, end_date)
    }

    /**
     * @Description: If return true, it's grayed out
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     */
    boolean verifyItemGrayed(row_index, column_index) {
        String Attribute = context.webDriver.getHtmlElementByLocator(Locator.xpath(String.format(Index_Head, row_index, column_index))).getClass()
        return Attribute.contains("uir-disabled")
    }

    boolean verifyAllItemGrayed(row_index){
        for (int i = Type_Index; i <= Enddate_Index; i++) {
            verifyItemGrayed(row_index, i)
        }
    }

    boolean verifyAllItemGrayedExceptSomeone(row_index, int except){
        for (int i = Type_Index; i <= Enddate_Index; i++) {
            if (i == except){
                i++
            }
            verifyItemGrayed(row_index, i)
        }
        String except_class = context.webDriver.getHtmlElementByLocator(Locator.xpath(String.format(Index_Head, row_index, except))).getClass()
        except_class.contains("uir-machine-focused-cell")
    }

    /**
     * @Description: If return true, The expect value is exit in expect place.
     * @Author: chenguwa
     * @UpdateBy chenguwa
     * @UpdateAt 3/22/2018
     */
    boolean checkValueInSavedRow(row_index, column_index, String expect_value) {
        return (expect_value.equals(context.webDriver.getHtmlElementByLocator(Locator.xpath(String.format(Index_Head, row_index, column_index)))))
    }

    void changeValueInSavedRow(int row_num, int column_num, value) {
//        asClick(String.format(Row_Head, row_num+1))
        asClick(String.format(Index_Head, row_num+1, column_num))
        context.setFieldIdentifiedByWithValue(Locator.xpath(End_Date), value)
//        context.webDriver.getHtmlElementByLocator(Locator.xpath(String.format(Index_Head, row_num+1, column_num))).clear()
//        context.setFieldIdentifiedByWithValue(Locator.xpath(String.format(Index_Head, row_num+1, column_num)), value)
    }

    boolean CheckSubsidiary(String expect_subsidiary) {
        return context.webDriver.getHtmlElementByLocator(Locator.name("inpt_subsidiary")).getAttributeValue("value").contains(expect_subsidiary)
    }

    String getCurrentDate() {
        String output = new Date().format('MM/dd/yyyy')
        return output
    }

    String getErrorMessage() {
        return (context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_ERR))).getAsText();
    }
}
