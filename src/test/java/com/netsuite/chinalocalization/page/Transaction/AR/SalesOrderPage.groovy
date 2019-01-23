package com.netsuite.chinalocalization.page
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.testautomation.html.Locator

/**
 * @Description Page object for Sales Order page: 'Transactions->Sales->Enter Sales Order'.
 * @Author
 * @LastModifiedBy lisha.hao@oracle.com
 */
class SalesOrderPage extends TransactionBasePage {

    private static String TITLE="Sales Order"
    String ITEMTAB = "Items"
    private static String URL=CURL.SALES_ORDER_CURL
    def url

    //XPATH
    protected static final String XPATH_ORDERNO = "/html/body/div[1]/div[2]/div[3]/form/table/tbody/tr[3]/td/table/tbody/tr[1]/td/table/tbody/tr[2]/td[1]/table/tbody/tr[1]/td/div/span[2]"

    def navigateTo(def salesOrderId){
        url = new StringBuffer()
        url.append(CURL.SALES_ORDER_CURL).append("?id=").append(salesOrderId).append("&whence=")
        context.navigateTo(url.toString())
    }

    public SalesOrderPage(){
        super(TITLE,URL)
    }

    public createSalesOrder(dataObj){
        createTransaction(dataObj)
    }

    public getOrderNo() {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_ORDERNO)).getAsText()
    }

    def clickApprove() {
        asElement("//input[@id='approve']").click()
    }

    def clickFulfill() {
        asElement("//input[@id='process']").click()
    }

    def clickCreateDeposit() {
        asElement("//input[@id='createdeposit']").click()
    }
}
