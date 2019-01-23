package com.netsuite.chinalocalization.vat.p2

import com.netsuite.chinalocalization.vat.TranType
import com.netsuite.chinalocalization.vat.VATAppP2TestSuite
import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("fredriczhu.zhu@oracle.com")
class VATCashRefundFromCashSalesTest extends VATAppP2TestSuite {



    /**
     * @desc Has been refund - Cash Refund CN
     * 1) Go to Transactions > Customers > Refund Cash Sales
     * 2) Create a new Cash Refund
     * 3) Select Customer: 北京一般纳税公司02
     * 4) Enter a valid Cash Sales Document Number for this customer that hasnot been refund before.
     * 5) Click save
     * @case 4.8.4.3.1
     * @author FredricZhu Zhu
     */
    @Category([P2.class, OW.class])
    @Test
    void test_4_8_4_3_1() {
        // Create a cash sales record and get the cash sales tran id to create a new cash refund

        def recordData = testData.recordData
        def recordCashSales = record.createRecord(recordData)
        dirtyData = recordCashSales
        def cashSalesTransId = recordCashSales.get(0).tranid

        navigateToRefundCashSalesPage()
        String commonCompany2 = testData.labels.fieldLabel.commonCompany2
        selectCompanyItem(commonCompany2)

        String location = testData.labels.fieldLabel.location
        selectLocationItem(location)

        switchToGoodsTab()

        String itemLabel = testData.labels.fieldLabel.itemLabel
        selectGoodsItem(itemLabel)

        clickAddItemBtnInGoods()

        switchToVATTab()

        enterValueInCreatedFrom(cashSalesTransId)

        clickSaveBtnInVAT()

        Thread.sleep(14 * 1000)

        String internalId = getCurrentRecInternalId()

        def newRecord = getANewRecord(internalId,TranType.CASH_REFUND)
        dirtyData.add(newRecord)
    }
}
