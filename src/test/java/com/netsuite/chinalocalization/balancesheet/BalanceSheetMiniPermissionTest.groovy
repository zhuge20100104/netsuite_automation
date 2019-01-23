package com.netsuite.chinalocalization.balancesheet

import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("molly.feng@oracle.com")
class BalanceSheetMiniPermissionTest extends JBSBaseTestSuite{

    /*  Check Balance Sheet minimal permission role
    *
    * 1.Transaction->Reports->China Balance Sheet
    *
    * 2. Set parameter:
    * Report Name: 中国资产负债表（China BISheet 03)
    * Subsidiary: China BlSheet 03
    * As of : Jun 2017
    * Unit ： 千元
    * 3. Click Refresh
    * 4. Click Export Excel
    * 5. Click Export PDF
    */
	@Test
	@Category([P0.class,OWAndSI.class])
    void test_bs_mini_permission() {
        initTest("test_bs_mini_permission")
        executeCase(testData)
    }
	/*  Check Balance Sheet minimal permission role
	  *
	  * 1.Transaction->Reports->China Income Statement
	  *
	  * 2. Set parameter:
	  * Report Name: 中国利润表测试Month
	  * Subsidiary: 中国 Income 03
	  * 3. Click Refresh
	  * 4. Click Export Excel
	  * 5. Click Export PDF
	  */
	@Test
	@Category([P0.class,OWAndSI.class])
	void test_is_mini_permission() {
		initTest("test_is_mini_permission")
		executeCase(testData)
	}

	def getDefaultRole() {
		// miminal permission role for bs and is
		println(" miminal permission role for bs and is")
		return loginUtil.getRoles(rolePathPrefix).MiniRole_bs_is
	}
}

