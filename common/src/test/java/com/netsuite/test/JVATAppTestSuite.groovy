package com.netsuite.test

import org.junit.Test

class JVATAppTestSuite extends JVATBaseTestSuite{

    @Test
    void test_2_1_1() {
        this.initTest("case 2.1.1")
        executeCase(testData)
    }


    @Test
    void test_2_1_2() {
        this.initTest("case 2.1.2")
        executeCase(testData)
    }

    /**
     * Check main xpath and id field
     */
    @Test
    void test_2_1_3() {
        this.initTest("case 2.1.3")
        executeCase(testData)
    }

    /**
     * Check sublist field
     */
    @Test
    void test_2_1_4() {
        this.initTest("case 2.1.4")
        executeCase(testData)
    }


    @Test
    void test_2_1_5() {
        this.initTest("case 2.1.5")
        executeCase(testData)
        Thread.sleep(30*1000)
    }


    @Test
    void test_2_1_6() {
        this.initTest("case 2.1.6")
        executeCase(testData)
        Thread.sleep(30*1000)
    }


    @Test
    void test_2_1_7() {
        this.initTest("case 2.1.7")
        executeCase(testData)
    }


    @Test
    void test_2_1_8() {
        this.initTest("case 2.1.8")
        executeCase(testData)
    }
}

