package com.netsuite.chinalocalization.preferencesetup

import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.base.lib.alert.AlertHandler
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.experimental.categories.Category
import org.openqa.selenium.Alert
import com.google.inject.Inject


class PreferenceGeneralTabTest extends PreferenceSetupBaseTest {

        @Rule
        public TestName name = new TestName()
        private def static caseData

        @Inject
        AlertHandler alertHandler

        def pathToTestDataFiles(){
            def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

            return [
                    "zhCN":"${dataFilesPath}preferencesetup\\data\\PreferenceGeneralTabTest_zh_CN.json",
                    "enUS":"${dataFilesPath}preferencesetup\\data\\PreferenceGeneralTabTest_en_US.json"
            ]
        }

        def initData(){
            caseData = testData.get(name.getMethodName())
        }

        def checkButStat(expResult){
            def saveB = expResult.saveBut
            def cancelB = expResult.cancelBut

            if (saveB.displayed){
                assertTrue("check save button exist", context.doesButtonExist(saveB.text))
            }

            if (cancelB.displayed){
                assertTrue("check cancel button exist", context.doesButtonExist(cancelB.text))
            }
        }

        def checkLabels(expResult){
            if (expResult.page_title){
                assertAreEqual("check page title exists", expResult.page_title.text, asText("//*[@class='uir-record-type']"))
            }

            if (expResult.general_tab){
                assertTrue("check tab exists", context.doesTextExist(expResult.general_tab.text))
            }
            if (expResult.enable_feature){
                assertTrue("check tab exists", context.doesTextExist(expResult.enable_feature.text))
            }
            if (expResult.vat){
                assertTrue("check tab exists", context.doesTextExist(expResult.vat.text))
            }
            if (expResult.cfs){
                assertTrue("check tab exists", context.doesTextExist(expResult.cfs.text))
            }
        }

        def checkItemStat(expResult){
            if (expResult.vat){
                assertAreEqual("check vat checkbox", expResult.vat, preSetupPage.getVatIntegration())
            }
            if (expResult.cfs){
                assertAreEqual("check cfs checkbox", expResult.cfs, preSetupPage.getCashFlowStatement())
            }

        }

        /**
         *@desc preference setup test
         *@case 1.2.1  Button Labels display
            1. Login as Admin
         * @author xiaojuan.song
         */
        @Test
        @Category([P1.class, OWAndSI.class])
        void test_case_1_2_1(){
            initData()
            def expBut = caseData.Buttons

            navigateToPreferenceSetUp()
            checkButStat(expBut)
        }

        /**
         *@desc preference setup test
         *@case 1.2.2  Parameter Labels display
         1. Login as Admin
         * @author xiaojuan.song
         */
        @Test
        @Category([P1.class, OWAndSI.class])
        void test_case_1_2_2(){
            initData()
            def expLabels = caseData.Labels

            navigateToPreferenceSetUp()
            checkLabels(expLabels)
        }

        /**
         *@desc preference setup test
         *@case 2.1.1  Check cancel action
         1. Login as Admin
         * @author xiaojuan.song
         */
        @Test
        @Category([P1.class, OWAndSI.class])
        void test_case_2_1_1(){
            initData()
            def expStat = caseData.ItemStat

            navigateToPreferenceSetUp()
            setVAT(false)
            setCFS(false)
            preSetupPage.clickCancel()
            context.webDriver.reloadBrowser()

            Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver, 10)
            if(alert!=null) {
                alert.accept()
            }

            navigateToPreferenceSetUp()
            checkItemStat(expStat)
        }

        /**
         *@desc preference setup test
         *@case 2.1.2  Both unchecked action
         1. Login as Admin
         * @author xiaojuan.song
         */
        @Test
        @Category([P1.class, OWAndSI.class])
        void test_case_2_1_2(){
            initData()
            def expStat = caseData.ItemStat

            navigateToPreferenceSetUp()
            setVAT(false)
            setCFS(false)
            preSetupPage.clickSave()
            assertTrue("check page after clicking save", context.isSetupManagerPage())

            navigateToPreferenceSetUp()
            checkItemStat(expStat)
        }

        /**
         *@desc preference setup test
         *@case 2.1.3  Both checked action
         1. Login as Admin
         * @author xiaojuan.song
         */
        @Test
        @Category([P1.class, OWAndSI.class])
        void test_case_2_1_3(){
            initData()
            def expStat = caseData.ItemStat

            navigateToPreferenceSetUp()
            setVAT(true)
            setCFS(true)
            preSetupPage.clickSecSave()
            assertTrue("check page after clicking save", context.isSetupManagerPage())

            navigateToPreferenceSetUp()
            checkItemStat(expStat)
        }
}
