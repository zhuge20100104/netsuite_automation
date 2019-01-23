package com.netsuite.chinalocalization.income


import com.netsuite.common.OW
import com.netsuite.common.P0
import org.junit.Test
import org.junit.experimental.categories.Category

class IncomeValidation extends JIncomeBaseTestSuite{
/**
 * @desc classification parameters -- refresh -- three parameters
 * @case test_2_1_4_2
 *      1.Check Subsidiary And Period Linkage
 *      2.Scenario: 2.1.4.2 Check Subsidiary And Period Linkage
 * @BDD migration
 */
    @Test
    @Category([P0.class,OW.class])
    void test_2_1_4_2() {
        initTest("test_2_1_4_2")
        executeCase(testData)
    }
    /**
     * @desc
     * @case test_4_4_1
     *      1.Check Subsidiary And Period Linkage
     *      2.Scenario: 4.4.1 Subsidiary&Period Linkage - OW CN
     * @BDD migration
     */
    @Test
    @Category([P0.class,OW.class])
    void test_4_4_1() {
        initTest("test_4_4_1")
        executeCase(testData)
    }
    /**
     * @desc
     * @case test_2_2_3_1_1
     *      1.Scenario: 2.2.3.2.1 Check Report Table Header - OW
     *      2.Scenario: 2.2.3.3.1 Check Report Table Contents - OW
     *      3.Scenario: 7.1 Confirm Template within Bundle installation - CN
     * @BDD migration
     */

    @Test
    @Category([P0.class,OW.class])
    void test_2_2_3_1_1() {
        initTest("test_2_2_3_1_1")
        executeCase(testData)
    }
    /**
     * @desc
     * @case test_7_1
     *      1.Scenario: 7.1 Confirm Template within Bundle installation - CN
     * @BDD migration
     */

    @Test
    @Category([P0.class,OW.class])
    void test_7_1() {
        initTest("test_2_2_3_1_1")
        executeCase(testData)
    }
/**
 * @desc classification parameters -- refresh -- three parameters
 * @case test_4_6_1
 *      Scenario: 4.6.1 Check Column number defined in Cutome Report OW CN
 * @BDD migration
 */
    @Test
    @Category([P0.class,OW.class])
    void test_4_6_1() {
        initTest("test_4_6_1")
        executeCase(testData)
    }

    /**
     * @desc classification parameters -- refresh -- three parameters
     * @case test_2_1_1_1
     *      Scenario: 2.1.1.1 Page Name and Buttons
     *      Scenario: 2.1.2.1 Prameters Label - OW
     *      Scenario: 2.1.2.3 Check Prameter Default Value !--need to discuss the examples data with Molly
     * (subsidiaryValue & periodValue)
     * @BDD migration
     */
    @Test
    @Category([P0.class,OW.class])
    void test_2_1_1_1() {
        initTest("test_2_1_1_1")
        executeCase(testData)
    }

}

