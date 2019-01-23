package com.netsuite.chinalocalization.balancesheet

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P2
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("molly.feng@oracle.com")
class BalanceSheetPageDisplayTest extends JBSBaseTestSuite{

    /*  Check Page display - Parameter label
    *                      - Parameter default value
    * 1.Transaction->Reports->China Balance Sheet
    *
    * 2. Set parameter:
    * Report Name: 中国资产负债表（China BISheet 03)
    * Subsidiary: China BlSheet 03
    * As of : Jun 2017
    * Unit ： 千元
    * 3. Click Refresh
    * @case 2.2.1.1.1，2.2.1.2.1 - Parameter label(OW & SI)
    * @case 2.2.1.1.2, 2.2.1.2.2 - Check parameter default value(OW & SI)
    * @case Page Name and Buttons (Case ID: 2.1.1.1)
    * @case (Case ID: 2.1.2.1)Verify Export Buttons are available after refresh
    * @case Check Report Header (Case ID: 2.3.2.1.1, 2.3.2.2.1)
    * @BDD migration
    */
	@Test
	@Category([P0.class, OW.class])
    void test_2_2_1_1() {
        initTest("test_2_2_1_1")
        executeCase(testData)
    }

	/*  Check built-in Report Header
    * 1.Transaction->Reports->China Balance Sheet
    *
    * 2. Set parameter:
    * Report Name: 中国资产负债表模板_中文
    * 3. Click Refresh
    * @BDD migration
    */
	@Test
	@Category([P0.class,OWAndSI.class])
	void test_builtin_template() {
		initTest("test_builtin_template")
		executeCase(testData)
	}
	/*  Check  Option list value and sync
    * 1.Transaction->Reports->China Balance Sheet
    *
    * 2. Set parameter:
    * Report Name: 中国资产负债表模板_中文
    * 3. Click Refresh
    * @case  Check Subsidiary Options (Case ID: 2.2.2.1.1)
    * @case  Check Period Options (Case ID: 2.2.2.1.2, 2.2.2.2.2)
    * @case Check Unit Options (Case ID: 2.2.2.1.3, 2.2.2.2.3)
    * @BDD migration
    */
	@Test
	@Category([P0.class,OWAndSI.class])
	void test_2_2_2_1_1() {
		initTest("test_2_2_2_1_1")
		executeCase(testData)
	}

}

