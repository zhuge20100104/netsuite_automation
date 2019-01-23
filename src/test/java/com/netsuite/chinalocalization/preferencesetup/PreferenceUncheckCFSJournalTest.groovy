package com.netsuite.chinalocalization.preferencesetup

import com.google.inject.Inject
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

class PreferenceUncheckCFSJournalTest extends PreferenceSetupBaseTest {
    @Inject
    protected JournalEntryPage journalEntryPage
    @Inject
    protected NRecord record
    @Rule
    public TestName name = new TestName()
    private def static caseData

    def pathToTestDataFiles(){
        def dataFilesPath = "src\\test\\java\\com\\netsuite\\chinalocalization\\"

        return [
                "zhCN":"${dataFilesPath}preferencesetup\\data\\PreferenceUncheckCFSJournalTest_zh_CN.json",
                "enUS":"${dataFilesPath}preferencesetup\\data\\PreferenceUncheckCFSJournalTest_en_US.json"
        ]
    }
    def response = ""
    def responseObj = {}

    @After
    void "Tear Down"() {
        if (response != "") {
            record.deleteRecord("journalentry",response.internalid.toInteger())
        }
    }

    def initData(){
        caseData = testData.get(name.getMethodName()).data[0]
    }

    /**
     *@desc preference uncheck CFS,save a Journal test
     *@case 3.1.1  Save Journal
     1. Login as Admin
     * @author yang.liu
     */
    @Test
    @Category([P1.class, OWAndSI.class])
    void test_case_3_1_1(){
        initData()
        navigateToPreferenceSetUp()
        setCFS(false)
        preSetupPage.clickSave()

        // Go to Financial > Other > Make Journal Enties, Create journal:
        journalEntryPage.navigateToURL()
        response = journalEntryPage.addJournal(caseData)

        // The journal can be saved successfully.
        Assert.assertNotNull("The transaction is not saved successfully.", response.internalid)

        navigateToPreferenceSetUp()
        setCFS(true)
        preSetupPage.clickSave()
    }
}
