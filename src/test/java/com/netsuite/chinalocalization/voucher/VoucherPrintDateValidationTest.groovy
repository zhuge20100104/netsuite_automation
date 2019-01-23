package com.netsuite.chinalocalization.voucher

import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.testautomation.junit.TestOwner
import org.apache.log4j.Logger
import org.junit.Test
import org.junit.experimental.categories.Category

/*
  @Author lifang.tang@oracle.com
  * @Created 2018-Mar-22
  * @desc:Test cases 1.3.3 Date Validation
 */
@TestOwner("lifang.tang@oracle.com")
class VoucherPrintDateValidationTest extends VoucherPrintBaseTest {
    private static Logger log = Logger.getLogger(VoucherPrintDateValidationTest)

    /*
    @desc No search result
    @case 1.3.3.1.5
     */
    @Category([P2.class, OW.class])
    @Test
    void case_1_3_3_1_5(){
        voucherPrintPage.navigateToURL()

        //Set Date
        def params = ["subsidiary":"Parent Company : China BU","periodFrom":"May 2017","periodTo":"May 2017","tranDateFrom":"05/06/2017","tranDateTo":"05/06/2017"]
        voucherPrintPage.setParameters(params,null)
        log.debug("Set Date")

        voucherPrintPage.clickRefreshBtn()
        def expectedError = isCurrentLanguageEnglish() ? VoucherMsgEnum.NO_SEARCH_RESULT.getEnLabel() : VoucherMsgEnum.NO_SEARCH_RESULT.getCnLabel()
        assertAreEqual("Remind no results", expectedError, voucherPrintPage.getSearchResultMsg())

    }

    /*
    @desc Enter an invalid Date To
    @case 1.3.3.1.6
     */
    @Category([P2.class, OW.class])
    @Test
    void case_1_3_3_1_6(){
        voucherPrintPage.navigateToURL()

        //Set Date
        def params = ["subsidiary":"Parent Company : China BU","periodFrom":"Jun 2017","periodTo":"Jun 2017","tranDateFrom":"","tranDateTo":"06/10/2017"]
        voucherPrintPage.setParameters(params,null)
        log.debug("Set Date")

        def currentDateTo = voucherPrintPage.getDateTo()

        voucherPrintPage.clickRefreshBtn()
        def expectedError = isTargetLanguageEnglish() ? VoucherMsgEnum.INVALID_DATE_RANGE.getEnLabel() : VoucherMsgEnum.INVALID_DATE_RANGE.getCnLabel()
        assertAreEqual("Remind invalid date range", expectedError, voucherPrintPage.getErrorMessage())

        //Close error message
        voucherPrintPage.closeErrorMessage()
        assertAreEqual("The Date To field set back", currentDateTo, voucherPrintPage.getDateTo())

    }

}
