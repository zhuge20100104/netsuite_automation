package com.netsuite.chinalocalization.testall


import com.netsuite.common.OWAndSI
import com.netsuite.common.P0
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.SI
import org.junit.experimental.categories.Categories
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Categories.class)
@Categories.IncludeCategory([P0.class,P1.class,P2.class])
@Categories.ExcludeCategory([SI.class])
@Suite.SuiteClasses([com.netsuite.chinalocalization.extendreport.SubledgerConsolidatedTest.class,com.netsuite.chinalocalization.extendreport.CashBankConsolidatedTest.class,com.netsuite.chinalocalization.extendreport.CashBankExportExcelTest.class,com.netsuite.chinalocalization.extendreport.AccountConsolidatedTest.class,com.netsuite.chinalocalization.extendreport.AccountExportPDFTest.class,com.netsuite.chinalocalization.extendreport.SubLedgerClassficationTest.class,com.netsuite.chinalocalization.extendreport.CashBankValidationTest.class,com.netsuite.chinalocalization.extendreport.CashBankClassficationTest.class,com.netsuite.chinalocalization.extendreport.CashBankConsolidatedTest.class,com.netsuite.chinalocalization.extendreport.SubledgerConsolidatedTest.class,com.netsuite.chinalocalization.extendreport.SubLedgerDrillDownTest.class,com.netsuite.chinalocalization.extendreport.SubLedgerPageDisplayTest.class])
class AllTestSuite {

}
