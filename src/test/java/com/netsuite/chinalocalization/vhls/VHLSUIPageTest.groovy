package com.netsuite.chinalocalization.vhls

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P2
import org.junit.Test
import org.junit.experimental.categories.Category

class VHLSUIPageTest extends JVHLSBaseTestSuite{

    @Test
    @Category([P0.class,OW.class])
    void test_2_1_1() {
        initTest("test_2_1_1")
        executeCase(testData)
    }
    @Test
    @Category([P0.class,OW.class])
    void test_2_1_2() {
        initTest("test_2_1_2")
        executeCase(testData)
        //sublist_report(testData.sublist_report)
    }
    /*Check date from should before date to
    * 1.Transaction->Financial->China Voucher List
    2. Set parameter:
    Subsidiary: 中国  Voucher List
    DATE FROM:       Oct 2018
    DATE TO：       Sep 2018
    3 Verify error msg
    Please enter a valid period range. The From period must precede the To period.(请输入一个有效的期间范围。起始期间必须在截止期间前。)
    */
    @Test
    @Category([P2.class,OWAndSI.class])
    void test_3_1_3() {
        initTest("test_3_1_3")
        executeCase(testData)

    }

    /*Check period from should before period to
    * 1.Transaction->Financial->China Voucher List
    2. Set parameter:
    Subsidiary: 中国  Voucher List
    PERIOD FROM：       Sep 2018
    PERIOD TO:        Aug 2018
    3 Verify error msg
    Please enter a valid period range. The From period must precede the To period.(请输入一个有效的期间范围。起始期间必须在截止期间前。)
    */
    @Test
    @Category([P2.class,OWAndSI.class])
    void test_3_1_6() {
        initTest("test_3_1_6")
        sleep(10* 1000)
        executeCase(testData)

    }

}

