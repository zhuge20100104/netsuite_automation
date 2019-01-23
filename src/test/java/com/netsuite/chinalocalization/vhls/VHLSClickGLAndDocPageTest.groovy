package com.netsuite.chinalocalization.vhls

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P2
import org.junit.Test
import org.junit.experimental.categories.Category

class VHLSClickGLAndDocPageTest extends JVHLSBaseTestSuite{

    @Test
    @Category([P0.class,OW.class])
    void test_10_3_1() {
        initTest("test_10_3_1")
        executeCase(testData)
    }

}

