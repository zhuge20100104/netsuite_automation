package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.cashflow.CONSTANTS.CashFlowEnum
import com.netsuite.chinalocalization.cashflow.CONSTANTS.VoucherEnum
import com.netsuite.chinalocalization.lib.NCurrentRecord
import com.netsuite.chinalocalization.page.JournalEntryPage
import com.netsuite.chinalocalization.page.Setup.CompanyInformationPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.OWAndSI
import com.netsuite.common.OW
import com.netsuite.common.SI
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.CFSMandatory
import com.netsuite.testautomation.junit.TestOwner
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@TestOwner("lisha.hao@oracle.com")
class CashflowSubsidiaryValidationTest extends CashflowBaseTest {
    @Inject
    SubsidiaryPage subsidiaryPage;
    @Inject
    CompanyInformationPage companyInfoPage;
    @Inject
    protected JournalEntryPage journalEntryPage
    @Inject
    NCurrentRecord nCurrentRecord;
    def response = "";
    def response1 = "";
    def response2 = "";
    def expectedError = "";
    private String subsidiaryParentName = "Parent Company";
    private String subsidiaryName = "CFS Validation BU";
    private String subsidiaryName1 = "CFS Validation BU 01";

    @After
    void tearDown() {
        disableCFSMandatory("China BU")
        if (response) {
            deleteTransaction(response)
        }
        super.tearDown()
    }

    /**
     * @CaseID Cashflow 1.50.4.1
     * @author lisha.hao@oracle.com
     * Description: CFS Subsidiary Journal Validation
     *      For OW, checkbox 'CHINA CASH FLOW ITEM MANDATORY' on Subsidiary page.
     *      Uncheck->Check->Uncheck, Journal mandatory validation.
     */
    @Test
    @Category([OW.class, P1.class, CFSMandatory.class])
    public void case_1_50_4_1() {
        def caseId = "test case 1.50.4.1";
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_4_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]
        /*
         * Step1. Checkbox should be unchecked by default.
         */
        switchToRole(administrator);
        enableCFSMandatory(subsidiaryName)
        // Checkpoint2: Checkbox should be checked.
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName)
        assertTrue("CFS Mandatory Item should be checked!", subsidiaryPage.isCheckedCFSMandatory())
        switchToRole(getAccountant())
        // 'Financial->Other->Make Journal Entries', create journal.
        journalEntryPage.navigateToURL()
        response1 = journalEntryPage.addJournal(dataObj)
        // Checkpoint3: Journal CANNOT be created, error message should popup.
        assertErrorMsg()
        context.closePopUp()
        // Set value for 'China Cash Flow Item' in line2.
        setCFSIteminLine(2)
        context.clickSaveByAPI()
        response1 = journalEntryPage.getJournalId();
        // Checkpoint4. Journal can be saved successfully.
        assertTranIdNotNull(response1.internalid)
        response1 = JsonOutput.toJson(response1)
        /*
         * Step3. Uncheck the checkbox on subsidiary page.
         */
        switchToRole(administrator)
        disableCFSMandatory(subsidiaryName)
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName)
        // Checkpoint5. Checkbox should be unchecked.
        assertFalse("CFS Mandatory Item should be unchecked!", subsidiaryPage.isCheckedCFSMandatory())
        switchToRole(getAccountant())
        // 'Financial->Other->Make Journal Entries', create journal.
        journalEntryPage.navigateToURL()
        response2 = journalEntryPage.addJournal(dataObj)
        // Checkpoint6. Journal can be saved successfully.
        assertTranIdNotNull(response2.internalid)
        response = "[" + response1 + "," + JsonOutput.toJson(response2) + "]"
    }

    /**
     * @CaseID Cashflow 1.50.4.2
     * @author lisha.hao@oracle.com
     * Description: CFS Parent and Child Subsidiary Validation
     *      For OW, checkbox 'CHINA CASH FLOW ITEM MANDATORY' on Subsidiary page.
     *      Uncheck->Check->Uncheck, check the Parent and Child subsidiary.
     */
    @Test
    @Category([OW.class, P2.class, CFSMandatory.class])
    public void case_1_50_4_2() {
        /*
         * Step1. Tick on checkbox on subsidiary 'CFS Validation BU'.
         */
        switchToRole(administrator);
        enableCFSMandatory(subsidiaryName);
        // Checkpoint2_3: Checkbox on 'CFS Validation BU' should be checked.
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName)
        assertTrue("CFS Mandatory Item should be checked!", subsidiaryPage.isCheckedCFSMandatory())
        subsidiaryPage.clickCancel()
        // Checkpoint2_4: Checkbox on child subsidiary should be unchecked.
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName1)
        assertFalse("CFS Mandatory Item on child subsidiary should be unchecked!", subsidiaryPage.isCheckedCFSMandatory())
        subsidiaryPage.clickCancel()
        // Checkpoint2_5: Checkbox on 'Parent Company' should be unchecked.
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryParentName)
        assertFalse("CFS Mandatory Item on child subsidiary should be unchecked!", subsidiaryPage.isCheckedCFSMandatory())
        subsidiaryPage.clickCancel()
        // Checkpoint2_5: There should NOT exist such checkbox 'CHINA CASH FLOW ITEM MANDATORY' on Company Information page.
        companyInfoPage.navigateToCompanyInfoPage()
        assertFalse("There should NOT exist such checkbox on Company Information page!", companyInfoPage.isExistCFSMandatory())
        companyInfoPage.clickCancel()
        /*
         * Step2. Uncheck the checkbox on subsidiary page.
         */
        disableCFSMandatory(subsidiaryName)
        // Checkpoint3_2: Checkbox on 'CFS Validation BU' should be unchecked.
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName)
        assertFalse("CFS Mandatory Item should be checked!", subsidiaryPage.isCheckedCFSMandatory())
        subsidiaryPage.clickCancel()
        // Checkpoint3_3: Checkbox on child subsidiary should be unchecked.
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName1)
        assertFalse("CFS Mandatory Item on child subsidiary should be unchecked!", subsidiaryPage.isCheckedCFSMandatory())
        subsidiaryPage.clickCancel()
        // Checkpoint3_3: Checkbox on parent subsidiary should be unchecked.
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryParentName)
        assertFalse("CFS Mandatory Item on child subsidiary should be unchecked!", subsidiaryPage.isCheckedCFSMandatory())
        subsidiaryPage.clickCancel()
    }

    /**
     * @CaseID Cashflow 1.50.4.3
     * @author lisha.hao@oracle.com
     * Description: CFS Subsidiary Journal Validation
     *      For SI, checkbox 'CHINA CASH FLOW ITEM MANDATORY' on Subsidiary page.
     *      Uncheck->Check->Uncheck, Journal mandatory validation.
     */
    @Test
    @Category([SI.class, P2.class, CFSMandatory.class])
    public void case_1_50_4_3() {
        def caseId = "test case 1.50.4.3";
        def dataString = context.getFileContent(caseId, "cashflow//case_1_50_4_data.json")
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data[0]
        /*
         * Step1. Checkbox should be unchecked by default.
         */
        switchToRole(administrator);
        companyInfoPage.navigateToCompanyInfoPage()
        // Checkpoint1. Checkbox 'CHINA CASH FLOW ITEM MANDATORY' should NOT be checked by default.
        assertFalse("CFS Mandatory Item should be unchecked by default!", companyInfoPage.isCheckedSICFSMandatory())
        /**
         * Step2. Tick on 'CHINA CASH FLOW ITEM MANDATORY' on Company Information page.
         */
        companyInfoPage.enableSICFSMandatory()
        companyInfoPage.clickSISave()
        // Checkpoint2. Checkbox should be checked.
        companyInfoPage.navigateToCompanyInfoPage()
        assertTrue("CFS Mandatory Item should be checked!", companyInfoPage.isCheckedSICFSMandatory())
        switchToRole(getAccountant())
        // 'Financial->Other->Make Journal Entries', create journal.
        journalEntryPage.navigateToURL()
        response1 = journalEntryPage.addSIJournal(dataObj)
        // Checkpoint3: Journal CANNOT be created, error message should popup.
        assertErrorMsg()
        journalEntryPage.clickOKButton()
        // Set value for 'China Cash Flow Item' in line2.
        setCFSIteminLine(2)
        journalEntryPage.clickSaveButton()
        response1 = journalEntryPage.getJournalId();
        // Checkpoint4. Journal can be saved successfully.
        assertTranIdNotNull(response1.internalid)
        response1 = JsonOutput.toJson(response1)
        /*
         * Step3. Uncheck the checkbox on subsidiary page.
         */
        switchToRole(administrator)
        disableCFSMandatory()
        companyInfoPage.navigateToCompanyInfoPage()
        // Checkpoint5. Checkbox should be unchecked.
        assertFalse("CFS Mandatory Item should be unchecked!", companyInfoPage.isCheckedSICFSMandatory())
        switchToRole(getAccountant())
        // 'Financial->Other->Make Journal Entries', create journal.
        journalEntryPage.navigateToURL()
        response2 = journalEntryPage.addSIJournal(dataObj)
        // Checkpoint6. Journal can be saved successfully.
        assertTranIdNotNull(response2.internalid)
        response = "[" + response1 + "," + JsonOutput.toJson(response2) + "]"
    }

    /**
     * Set value for 'China Cash Flow Item' in line.
     */
    void setCFSIteminLine(int lineNum) {
        if (context.getUserLanguage().equalsIgnoreCase("en_US")) {
            journalEntryPage.editLine(lineNum, CashFlowEnum.CASH_PAID_TO_AND_FOR_EMPLOYEES.getEnLabel())
        } else {
            journalEntryPage.editLine(lineNum, CashFlowEnum.CASH_PAID_TO_AND_FOR_EMPLOYEES.getCnLabel())
        }
    }
}