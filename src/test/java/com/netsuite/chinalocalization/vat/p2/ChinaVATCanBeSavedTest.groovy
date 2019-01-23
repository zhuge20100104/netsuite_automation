package com.netsuite.chinalocalization.vat.p2


import com.netsuite.chinalocalization.vat.VATAppP2TestSuite
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import com.netsuite.common.*

import org.junit.experimental.categories.Category

@TestOwner("fredriczhu.zhu@oracle.com")
class ChinaVATCanBeSavedTest  extends VATAppP2TestSuite {
    //region role related methods
/*    def getDefaultRole() {
        return getAccountant()
    }*/
    //endregion


    /**
     * @desc Transactions without error message
     *  1) Go to:Customers > Sales > 生成中国增值税事务处理
     *  2) Set the params and click Refresh button
     * @case 6.3.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_6_3_1() {

        def labels = testData.labels
        def data = testData.data

        navigateToGenerateChinaVATPage()
        selectSubsidiaryItem(labels.subsidiaryName)
        enterValueInDateFromInGenerateVAT(data.fromDate)
        enterValueInDateToInGenerateVAT(data.toDate)
        clickRefreshButton()
        Thread.sleep(10 * 1000)
        assertTextVisible("Transaction table not shown!!",labels.tableTag)
    }


    /**
     * @desc Hyperlink for Internal ID
     *  1) Go to:Customers > Sales > 生成中国增值税事务处理
     *  2) Set the params and click Refresh button
     * @case 6.3.7
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_6_3_7() {
        def labels = testData.labels
        def data = testData.data

        navigateToGenerateChinaVATPage()
        selectSubsidiaryItem(labels.subsidiaryName)
        enterValueInDateFromInGenerateVAT(data.fromDate)
        enterValueInDateToInGenerateVAT(data.toDate)
        clickRefreshButton()
        Thread.sleep(14 * 1000)
        def internalIdXML = getFirstInternalIdXML()
        println internalIdXML

        assertTextContains("InternalId field doesn't contains hyperlink",internalIdXML,"href")

        Thread.sleep(5 * 1000)

        def internalIdEle = getFirstInternalIdElement()
        internalIdEle.click()
        this.waitForPageToLoad()

        Thread.sleep(10 * 1000)
        def pageURL = getPageUrl()
        println(pageURL)
        assertTextContains("Page url is not transaction view page",pageURL,data.transactionViewPageURL)
    }

}
