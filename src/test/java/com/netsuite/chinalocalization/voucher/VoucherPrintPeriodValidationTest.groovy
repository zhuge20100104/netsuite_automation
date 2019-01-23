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
  * @desc:Test cases 1.3.2 Period Validation
 */
@TestOwner("lifang.tang@oracle.com")
class VoucherPrintPeriodValidationTest extends VoucherPrintBaseTest{
    private static Logger log = Logger.getLogger(VoucherPrintPeriodValidationTest)


    /*
    @desc Enter an invalid Period To
    @case 1.3.2.1.5
     */
    @Category([P2.class, OW.class])
    @Test
    void case_1_3_2_1_5(){
        voucherPrintPage.navigateToURL()

        //Change Period To
        def currentPeriodTo = voucherPrintPage.getPeriodTo()
        def params = ["periodTo":"Apr 2017"]
        voucherPrintPage.setParameters(params,null)
        log.debug("Change Period To")

        //Verify error message
        def expectedError = isCurrentLanguageEnglish() ? VoucherMsgEnum.INVALID_PERIOD_RANGE.getEnLabel() : VoucherMsgEnum.INVALID_PERIOD_RANGE.getCnLabel()
        assertAreEqual("Remind to select a start period eralier than end period", expectedError, voucherPrintPage.getErrorMessage())
        log.debug("Verify error message")

        //Verify Period To change back auto.
        voucherPrintPage.closeErrorMessage()
        assertAreEqual("The Period To field set back", currentPeriodTo, voucherPrintPage.getPeriodTo())
    }
}
