package com.netsuite.chinalocalization.vat

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import org.junit.Test
import org.junit.experimental.categories.Category

class JVATItemTest extends com.netsuite.chinalocalization.vat.JSetupBaseTestSuite{
/*
     * @desc Verify VAT Date From Field

     * case 4.4.1.1
     * @author Christina.chen

*
* */
    @Test
    @Category([P0.class,OW.class])
    void test_4_4_1_1() {
        initTest("test_4_4_1_1")
        executeCase(testData)
    }

}

