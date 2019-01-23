package com.netsuite.chinalocalization.extendreport

import com.netsuite.chinalocalization.extendreport.JBSBaseTestSuite
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import org.junit.Test
import org.junit.experimental.categories.Category

class ExtendreportMiniPermissionTest extends JBSBaseTestSuite{

    /*  Check Extendreport minimal permission role
    *
    * 1.Transaction->Reports->China Account Balance
    *
    * 2. Set parameter:
    * Report Name: 中国科目余额表（China Account Balance)
    * Subsidiary: 中国 Income
    * DATE FROM * : 6/10/2016
    * DATE TO * : 6/10/2016
    * 3. Click Refresh
    * 4. Click Export Excel
    * 5. Click Export PDF
    */
	@Test
	@Category([P0.class,OWAndSI.class])
    void test_atbl_mini_permission() {
        initTest("test_atbl_mini_permission")
        executeCase(testData)
    }
	/*  Check Extendreport minimal permission role
   *
   * 1.Transaction->Reports->China Subledger
   *
   * 2. Set parameter:
   * Report Name: 中国明细账（China Subledger)
   * Subsidiary: 中国 Income
   * DATE FROM * : 6/10/2016
   * DATE TO * : 6/10/2016
   * 3. Click Refresh
   * 4. Click Export Excel
   * 5. Click Export PDF
   */
	@Test
	@Category([P0.class,OWAndSI.class])
	void test_sblg_mini_permission() {
		initTest("test_sblg_mini_permission")
		executeCase(testData)
	}
	/*  Check Extendreport minimal permission role
   *
   * 1.Transaction->Reports->China Cash & Bank Journal Ledger
   *
   * 2. Set parameter:
   * Report Name: 中国现金银行日记账（China Cash & Bank Journal Ledger)
   * Subsidiary: 中国 Income
   * DATE FROM * : 6/10/2016
   * DATE TO * : 6/10/2016
   * 3. Click Refresh
   * 4. Click Export Excel
   * 5. Click Export PDF
   */
	@Test
	@Category([P0.class,OWAndSI.class])
	void test_cbjl_mini_permission() {
		initTest("test_cbjl_mini_permission")
		executeCase(testData)
	}

	def getDefaultRole() {
		// miminal permission role for Extendreport
		println("miminal permission role for Extendreport")
		return loginUtil.getRoles(rolePathPrefix).MiniRole_ab_sb_cb
	}
}

