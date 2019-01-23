package com.netsuite.chinalocalization.vat.p2

import com.netsuite.base.lib.alert.AlertHandler
import com.netsuite.chinalocalization.vat.VATAppP2TestSuite
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category
import org.openqa.selenium.Alert

@TestOwner("fredriczhu.zhu@oracle.com")
class IssueChinaVATTest extends VATAppP2TestSuite{
    //region role related methods
/*    def getDefaultRole() {
        return getAccountant()
    }*/
    //endregion



    def handleErrorAndAlert() {
        clickOkBtn()


        context.webDriver.reloadBrowser()
        Thread.sleep(4*1000)

        AlertHandler alertHandler = new AlertHandler()
        Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver,10)
        if(alert!=null) {
            alert.accept()
        }


        Thread.sleep(8*1000)

    }


    /**
     * @desc Date From > Date To  - CN
     *  Date: 2017/8/1 ~2017/7/1
     * 1) Go to:Customers > Sales > 生成中国增值税事务处理
     * 2) Set the params
     * 3) Click Refresh Button
     * @case 5.2.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_5_2_1() {

        navigateToGenerateChinaVATPage()
        def data = testData.data
        enterValueInDateFromInGenerateVAT(data.fromDate)
        enterValueInDateToInGenerateVAT(data.toDate)
        clickRefreshButton()
        assertTextVisible("Start date not more than end date",data.errorStartDateMoreThanEndDateMsg)

        handleErrorAndAlert()
    }
}
