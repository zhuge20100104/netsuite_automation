package com.netsuite.chinalocalization.preferencesetup

import com.netsuite.chinalocalization.vhls.JVHLSBaseTestSuite
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P2
import com.netsuite.common.SI
import org.junit.Test
import org.junit.experimental.categories.Category

class JFavoritePageTest extends JSetupBaseTestSuite{
/*
     * @desc Test preferences for VHLS
     * when enable multi-currencies and location department class
     * preferences page default value:
     * Printing template: Multiple Currencies
     * Print Location: Checkbox disabled  unchecked
     * Print Department: Checkbox disabled  unchecked
     * Print Class: Checkbox disabled  unchecked
     * Select Single Currency
     * Print Location: Checkbox enabled  checkable
     * Print Department: Checkbox enabled  checkable
     * Print Class: Checkbox enabled  checkable
     * @case 9.1
     * @author Christina.chen

*
* */
    @Test
    @Category([P0.class,OW.class])
    void test_9_1() {
        initTest("test_9_1")
        executeCase(testData)
    }
    /* @desc Test preferences for VHLS
     In SI env
     * Printing template: can  select Multiple Currencies or  Single Currency
     * @case 9.2
     * @author Christina.chen
     */
    @Test
    @Category([P0.class,OWAndSI.class])
    void test_9_2() {
        initTest("test_9_2")
        executeCase(testData)
    }
    /* @desc Test preferences for VHLS
        Check label exist  and correct
        Check Input Range
        Check Max Loaded Lines cfg  effect  VHLS display
     * @case 9.3.2.1
     * 1. Setup-> China Localization-> Preferences
       2. set Max Loaded Lines 10 and click save
       expect
       Invalid number (must be between 100 and 5000)
       无效号码（必须在100和5000之间）
     * @author Christina.chen
     */
    @Test
    @Category([P0.class,OWAndSI.class])
    void test_9_3_2_1() {
        initTest("test_9_3_2_1")
        executeCase(testData)
    }

/* @desc Test preferences for VHLS
        Check label exist  and correct
        Check Input Range
        Check Max Loaded Lines cfg  effect  VHLS display
     * @case 9.3.2.2
     * 1. Setup-> China Localization-> Preferences
       2. set Max Loaded Lines 6000 and click save
       expect
       Invalid number (must be between 100 and 5000)
       无效号码（必须在100和5000之间）
     * @author Christina.chen
     */
    @Test
    @Category([P0.class,OWAndSI.class])
    void test_9_3_2_2() {
        initTest("test_9_3_2_2")
        executeCase(testData)
    }
    /*
    1. Setup-> China Localization-> Preferences
    2.set Max Loaded Lines 120 and click save
    subsidiary:"北京VAT",
    periodfrom:"Jan 2017",
    periodto:"Sep 2018",
    when result is more than 120 rows
    Press refresh button will show message:

    * @author Christina.chen
    * */
    @Test
    @Category([P0.class,OWAndSI.class])
    void test_9_3_3_1() {
        initTest("test_9_3_3_1")
        executeCase(testData)
    }
    /*
    1. Setup-> China Localization-> Preferences
    2.set Max Loaded Lines 120 and click save
    3. make a search On VHLS Page
    subsidiary:"中国 VoucherList",
    periodfrom:"Aug 2018",
    periodto:"Sep 2018",
    datefrom:"8/1/2018",
    dateto:"8/15/2018",
    when result is less than 120 rows）
    can show result normally
    * @author Christina.chen
    * */
    @Test
    @Category([P0.class,OW.class])
    void test_9_3_3_2() {
        initTest("test_9_3_3_2")
        executeCase(testData)
    }


    /**
     * This case is moved from VHLS Print, because it need to be run as admin role
     * Case 11.1 - Batch Print  Account lines>500
     * 1.Transaction->Financial->China Voucher List
     * 2. Set parameter:
     * Subsidiary: 北京VAT
     * Period From/To: FY 2017 - Oct 2018
     * 3. Click Refresh
     * 4. Click Print
     * 5. Click Cancel button
     * 6. Click Print again
     * 7. Click OK button
     * @case 11.1 Batch Print - lines >500
     * @author Molly.Feng
     */
    @Test
    @Category([P0.class,OWAndSI.class])
    void test_11_1() {
        initTest("test_11_1")
        executeCase(testData)
    }

}

