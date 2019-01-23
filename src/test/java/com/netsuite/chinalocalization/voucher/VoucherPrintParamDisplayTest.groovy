package com.netsuite.chinalocalization.voucher

import com.netsuite.common.OW
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.junit.TestOwner
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

/*
  * @Author lifang.tang@oracle.com
  * @Created 2018-Mar-21
  * @desc:Test cases 1.3.1 Paarameters Display
   */
@TestOwner("lifang.tang@oracle.com")
class VoucherPrintParamDisplayTest extends VoucherPrintBaseTest {
    private static Logger log = Logger.getLogger(VoucherPrintParamDisplayTest)

    @After
    void tearDown(){
        //set subsidiary back
        def params = ["subsidiary":"Parent Company : China BU"]
        voucherPrintPage.setParameters(params,null)
        log.debug("Set subsidiary back")
        super.tearDown()
    }


    /*
    @desc Default Posting Period. If fail ,please check if Fiscal Year 2018 is openned for Japan BU.
    @case 1.3.1.1.4
     */
    @Category([P3.class, OW.class])
    @Test
     void case_1_3_1_1_4(){
        voucherPrintPage.navigateToURL()

        //Get month and year
        String timezone = context.executeScript("return nlapiGetContext().getPreference('timezone');")
        TimeZone zone = TimeZone.getTimeZone(timezone)
        Calendar cal = Calendar.getInstance(zone)
        def month = cal.getDisplayName(Calendar.MONTH,Calendar.SHORT_FORMAT,Locale.US)
        def year = String.valueOf(cal.get(Calendar.YEAR))
        def period = month + " " + year

        //set subsidiary to "China BU"
        def params = ["subsidiary":"Parent Company : China BU"]
        voucherPrintPage.setParameters(params,null)
        log.debug("Set subsidiary to \"China BU\"")
        assertAreEqual("Default period from", period, voucherPrintPage.getPeriodFrom())
        assertAreEqual("Default period to", period, voucherPrintPage.getPeriodTo())

        params = ["subsidiary":"Parent Company : Japan BU"]
        voucherPrintPage.setParameters(params,null)
        log.debug("Set subsidiary to \"Japan BU\"")
        assertAreEqual("Default period from", "Mar 2017", voucherPrintPage.getPeriodFrom())
        assertAreEqual("Default period to", "Mar 2017", voucherPrintPage.getPeriodTo())

    }


    /*
    @desc No accounting periods setup
    @case 1.3.1.1.5
     */
    @Category([P3.class, OW.class])
    @Test
    void case_1_3_1_1_5(){
        voucherPrintPage.navigateToURL()

        //set subsidiary to "BU Without Period"
        def params = ["subsidiary":"Parent Company : BU Without Period"]
        voucherPrintPage.setParameters(params,null)
        log.debug("Set subsidiary to \"BU Without Period\"")

        //verify error message
        def expectedError = isCurrentLanguageEnglish() ? VoucherMsgEnum.NO_ACCOUNTING_PERIOD.getEnLabel() : VoucherMsgEnum.NO_ACCOUNTING_PERIOD.getCnLabel()
        assertAreEqual("Should show no accounting period error message", expectedError, voucherPrintPage.getErrorMessage())
        log.debug("Verify error message:")

        //Verify period From/To is blank.
        voucherPrintPage.closeErrorMessage()
        assertTrue("The Period From field set empty", voucherPrintPage.getPeriodFrom().isEmpty())
        assertTrue("The Period To field set empty", voucherPrintPage.getPeriodTo().isEmpty())
        log.debug("Verify period From/To is blank.")

    }

}
