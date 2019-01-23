package com.netsuite.chinalocalization.vhls

import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P2
import com.netsuite.common.SI
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("molly.feng@oracle.com")
class VHLSPrintTest extends JVHLSBaseTestSuite{


	/*  Case 8.1.2 - Online Print
   * 1.Transaction->Financial->China Voucher List
   * 2. Set parameter:
   * Subsidiary: 中国 VoucherList
   * Period From/To: May 2018 - May 2018
   * Date From/To: 5/1/2018 - 5/1/2018
   * 3. Click Refresh
   * 4. Click Print
   * @Case 8.1.2 - Online Print with Beijing time zone
   * we check time stamp manually
   */
	@Test
	@Category([P0.class,OWAndSI.class])
	void test_8_1_2() {
		initTest("test_8_1_2")
		executeCase(testData)
	}
/*  Case 8.1.3 - check Print button disabled when no data returned.
   * 1.Transaction->Financial->China Voucher List
   * 2. Set parameter:
   * Subsidiary: 中国 VoucherList
   * Period From/To: May 2018 - May 2018
   * Date From/To: 8/1/2018 - 8/1/2018
   * 3. Click Refresh
   * 4. Click Print
   * @Case 8.1.3 - check Print button disabled when no data returned.
   * we check time stamp manually
   */
	@Test
	@Category([P0.class,OWAndSI.class])
	void test_8_1_3() {
		initTest("test_8_1_3")
		executeCase(testData)
	}
}

