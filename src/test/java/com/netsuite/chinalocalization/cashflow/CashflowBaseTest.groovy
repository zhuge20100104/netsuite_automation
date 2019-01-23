package com.netsuite.chinalocalization.cashflow

import com.google.inject.Inject
import com.netsuite.chinalocalization.cashflow.CONSTANTS.CashFlowEnum
import com.netsuite.chinalocalization.cashflow.CONSTANTS.RecordErrorMsgEnum
import com.netsuite.chinalocalization.cashflow.CONSTANTS.VoucherEnum
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.Utility
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.chinalocalization.page.Report.CashFlowStatementPage
import com.netsuite.chinalocalization.page.SalesOrderPage
import com.netsuite.chinalocalization.page.Setup.CompanyInformationPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.chinalocalization.page.Transaction.AR.FulfillPage
import com.netsuite.chinalocalization.page.Transaction.AR.InvoiceSalesOrder
import com.netsuite.chinalocalization.page.Transaction.AR.ProcessStatusPage
import com.netsuite.chinalocalization.page.Transaction.AR.ProcessedRecordsPage
import com.netsuite.testautomation.enums.Page
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils
import org.junit.After
import org.junit.Assert
import com.netsuite.base.lib.os.OSUtil
import org.junit.Before
import org.openqa.selenium.Keys
import com.netsuite.base.lib.element.ElementHandler

import java.util.regex.Matcher
import java.util.regex.Pattern

class CashflowBaseTest extends BaseAppTestSuite {
    @Inject
    CompanyInformationPage companyInformationPage;
    @Inject
    SubsidiaryPage subsidirayPage;
    @Inject
    protected SalesOrderPage salesOrderPage;
    @Inject
    protected FulfillPage fulfillPage;
    @Inject
    protected InvoiceSalesOrder invoiceSalesOrder;
    @Inject
    protected ProcessStatusPage processStatusPage;
    @Inject
    protected ProcessedRecordsPage processedRecordsPage;
    @Inject
    protected OSUtil osUtil
    @Inject
    protected NRecord record
    @Inject
    protected ElementHandler elementHandler

    protected static String ADD
    protected static String CANCEL
    protected static String ITEMTAB
    protected static String EXPENSETAB
    protected static String PROMOTIONTAB
    protected static String LANGUAGE
    protected static String CREDIT
    protected static String SAVE
    protected static String DELETE
    protected static String REFNO
    protected static String EDIT
    protected static String CASHFLOWITEMCOLUMN
    protected static boolean isOW = true

    private cfsItems = [:]
    def response = ""

    @SuiteSetup
    void setUpTestSuite() {
        super.setUpTestSuite()
        setValue()
    }

    @Before
    void setUp() {
        try {
            def homeUrl = Page.HOME.getUrl()
            context.navigateToNoWait(homeUrl)
        }
        catch (Exception ex) {
        }
        if (context.isCloseBrowserOnTeardown() || shouldLoginCondition()) {
            login()
        }
        try {
            isOW = context.isOneWorld()
        } catch (Exception e) {
        }
    }

    @After
    void tearDown() {
        switchWindow()
    }

    def setValue() {
        try {
            isOW = context.isOneWorld()
        } catch (Exception e) {
        }
        if (isTargetLanguageEnglish()) {
            ADD = "Add"
            ITEMTAB = "Items"
            EXPENSETAB = "Expenses"
            PROMOTIONTAB = "Promotions"
            LANGUAGE = "en_US"
            CANCEL = "Cancel"
            CREDIT = "Credit"
            SAVE = "Save"
            DELETE = "Delete"
            REFNO = "Ref No."
            EDIT = "Edit"
        } else {
            ADD = "添加"
            ITEMTAB = "货品"
            EXPENSETAB = "费用"
            PROMOTIONTAB = "促销"
            LANGUAGE = "zh_CN"
            CANCEL = "取消"
            CREDIT = "贷记"
            SAVE = "保存"
            DELETE = "删除"
            REFNO = "参考编号"
            EDIT = "编辑"
        }
    }

    def switchWindow() {
        context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
        def currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
    }

    HashSet getCFSItemFromJson(String inOrOut) {
        HashSet items = new HashSet()
        if (LANGUAGE == "en_US") {
            cfsItems = context.asJSON(path: "data\\cashflow\\cash_flow_items_en.json")
        } else {
            cfsItems = context.asJSON(path: "data\\cashflow\\cash_flow_items_cn.json")
        }
        if (inOrOut == "IN") {
            for (inf in cfsItems.inflow) {
                items.add(inf.item)
            }
        } else if (inOrOut == "OUT") {
            for (otf in cfsItems.outflow) {
                items.add(otf.item)
            }
        } else if (inOrOut == "ALL") {
            for (otf in cfsItems.inflow) {
                items.add(otf.item)
            }
            for (otf in cfsItems.outflow) {
                items.add(otf.item)
            }
        }
        return items
    }

    //Trim the "" and "-New-" from CFS drop down list
    HashSet trimCFSOptions(HashSet options) {
        options.remove("")
        options.remove("- New -")
        options.remove("")
        options.remove("-新建-")
        return options
    }

    protected void verifyTransactionCFSDefault(String url, String entityType, String entityName, String jsonFile) {
        context.navigateTo(url)

        String entityId = Utility.getEntityId(context, entityType, entityName)
        context.setFieldWithValue("entity", entityId)
        Thread.sleep(2000)
        context.setFieldWithText("location", "CN_BJ")

        verifyCFSDefault(jsonFile, context, LANGUAGE)
    }

    protected void verifyCFSDefault(String fileName, NetSuiteAppCN appCN, String language) {
        String caseData = new File('data//cashflow//' + fileName).getText('UTF-8')
        def jsonSlurper = new JsonSlurper()
        def caseDataObj = jsonSlurper.parseText(caseData)
        if (caseDataObj.data.item != null && caseDataObj.data.item.size() == caseDataObj.expected.item.size()) {
            this.verifyItemLineDefault(appCN, language, caseDataObj.data.item, caseDataObj.expected.item)
        } else {
            assertFalse("Expect result in test Data is Incomplete", true)
        }
    }

    protected void verifyItemLineDefault(NetSuiteAppCN appCN, String language,
                                         def itemLineDataList, def itemLineExpectCFSList) {
        if (context.doesFormSubTabExist(ITEMTAB)) {
            context.clickFormSubTab(ITEMTAB)
        }
        EditMachineCN sublist = context.withinEditMachine("item")
        for (int i = 0; i < itemLineDataList.size(); i++) {
            def itemLineData = itemLineDataList.get(i)
            def expectCFSData = itemLineExpectCFSList.get(i)

            if (itemLineData.lineid.toInteger() != 1) {
                sublist.setCurrentLine(itemLineData.lineid.toInteger())
            }
            def item = itemLineData.itemold
            sublist.setFieldWithText("item", item)
            sublist.setFieldWithText("location", 'CN_BJ')
            if (itemLineData.itemold.contains("Lot Numbered") || itemLineData.itemold.contains("Serialized")) {
                //aut.closePopUp();
                sublist.setFieldWithValue("serialnumbers", "1")
            }
            this.checkCFS(language, item, expectCFSData.cfsold, sublist.getFieldText("custcol_cseg_cn_cfi"))

            //change item to itemnew if not null
            if (itemLineData.itemnew != null) {
                item = itemLineData.itemnew
                sublist.setCurrentLine(itemLineData.lineid.toInteger())
                sublist.setFieldWithText("item", item)
            }

            //Change CFS item if not null
            if (itemLineData.cfsnew != null) {
                String label = ""
                if (!itemLineData.cfsnew.isEmpty()) {
                    if (language.equals("en_US")) {
                        label = itemLineData.cfsnew
                    } else {
                        label = CashFlowEnum.getCnLabel(itemLineData.cfsnew)
                    }
                }
                sublist.setCurrentLine(itemLineData.lineid.toInteger())
                sublist.setFieldWithText("custcol_cseg_cn_cfi", label)
            }
            Thread.sleep(2000)
            //Verify CFS after item changed
            this.checkCFS(language, item, expectCFSData.cfsnew, sublist.getFieldText("custcol_cseg_cn_cfi"))

            sublist.add()
            //appCN.clickButton(ADD);
            Thread.sleep(2000)
        }
    }

    protected Boolean checkCFS(String language, String item, String expectCFS, String actualCFS) {
        if (expectCFS.isEmpty()) {
            checkTrue("CFS should default correctly for " + item, expectCFS.equals(actualCFS))
        } else if (language.equals("en_US")) {
            checkAreEqual("CFS should default correctly for " + item, actualCFS, expectCFS)
        } else {
            checkAreEqual("CFS should default correctly for " + item, actualCFS, CashFlowEnum.getCnLabel(expectCFS))
        }
    }

    boolean checkCFSResultsAvailable(long tranId) {
        def filter = [record.helper.filter('custrecord_cfs_pymt_tranid').is(tranId)]
        def column = [record.helper.column('internalid').create(), record.helper.column('custrecord_cfs_item').create()]
        def detailRecords = record.searchRecord('customrecord_cn_cashflow_record_detail', filter, column)
        println "Time:" + System.currentTimeMillis() + ",checkCFSResultsAvailable:" + detailRecords.size()
        context.log.info("Time:" + System.currentTimeMillis() + ",checkCFSResultsAvailable:" + detailRecords.size())
        return detailRecords.size() == 0 ? false : true
    }

    void checkCollectedCFS(String tranType, long tranId, def expectedResult) {
        String restletURL = ""
        try {
            restletURL = context.resolveRestletURL("customscript_rl_cn_controller", "customdeploy_rl_cn_controller")
        } catch (Exception e) {
            println("Cashflow Restlet handler is missing!!")
            Assert.assertTrue("Cashflow Restlet handler is missing!!", false)
            return
        }

        //wait until detail records generated completed
        def maxTimeWait = Integer.parseInt(aut.getContext().getProperty("testautomation.nsapp.default.maxdelay"))
        long endWaitTime = System.currentTimeMillis() + maxTimeWait * 1000
        boolean isConditionMet = false
        while (System.currentTimeMillis() < endWaitTime && !isConditionMet) {
            isConditionMet = checkCFSResultsAvailable(tranId)
            if (isConditionMet) {
                break
            } else {
                Thread.sleep(2000)
            }
        }

        String fullURL = restletURL + "&handlerName=cn_cashflow_handler&trantype=" + tranType + "&tranid=" + tranId
        context.navigateTo(fullURL)
        context.waitForPageToLoad()
        String responseStr = context.webDriver.getHtmlElementByLocator(Locator.xpath("html/body")).getAsText()
        //Keep page open to retrieve NS context
        context.navigateTo("https://system.na3.netsuite.com/app/center/card.nl?sc=-29&whence=")
        // Sample Response:
        // {"statusCode":0,"message":"Success.","result":[{"depositTran":" ","payingTran":"Journal #voucher1.6.1.1","paidTran":" ","postingperiod":"FY 2017 : Q1 2017 : Jan 2017","trandate":"1/6/2017","item":"Cash received from sales and services","amount":"117.00","subsidiary":"2"}]}
        def response = new JsonSlurper().parseText(responseStr)
        response = new JsonSlurper().parseText(responseStr)
        if (response.statusCode != 0) {
            Assert.assertTrue(responseStr, false)
            return
        }
        def actualResult = response.result
        Assert.assertTrue("Expected Lines are incorrect", actualResult.size() == expectedResult.size())
        for (int i = 0; i < expectedResult.size(); i++) {
            def expectedMatchFound = false
            for (int j = 0; j < actualResult.size(); j++) {
                expectedMatchFound = isMatched(expectedResult[i], actualResult[j])
                if (expectedMatchFound)
                    break
            }
            if (!expectedMatchFound) {
                context.log.info('CFS actual result' + response)
                Assert.assertTrue("Expected Line is not found, please check the execution log for collected detail. " + expectedResult + "actualResult : " + actualResult, false)
            }
        }
    }

    String getCollectedCFS(String tranType, long tranId) {
        String restletURL = ""
        try {
            restletURL = context.resolveRestletURL("customscript_rl_cn_controller", "customdeploy_rl_cn_controller")
        } catch (Exception e) {
            println("Cashflow Restlet handler is missing!!")
            Assert.assertTrue("Cashflow Restlet handler is missing!!", false)
            return
        }
        String fullURL = restletURL + "&handlerName=cn_cashflow_handler&trantype=" + tranType + "&tranid=" + tranId
        context.navigateTo(fullURL)
        context.waitForPageToLoad()
        String responseStr = context.webDriver.getHtmlElementByLocator(Locator.xpath("html/body")).getAsText()
        return responseStr
    }

    /**
     * @param expectedLine expected cfs line
     * @param actualLine actual collected cfs line
     * @return true if transaction date, posting period, cashflow item and amount are all the same. Otherwise return false.
     */
    boolean isMatched(def expectedLine, def actualLine) {
        def expectedItem = expectedLine.item
        if (LANGUAGE.equals(("zh_CN")))
            expectedItem = CashFlowEnum.getCnLabel(expectedItem)

        if (expectedLine.trandate != actualLine.trandate || expectedLine.postingperiod != actualLine.postingperiod || expectedLine.amount != actualLine.amount || expectedItem != actualLine.item)
            return false

        return true
    }

    private void checkCFSLine(def expectedLine, def actualLine) {
        Assert.assertEquals("Transaction Date is not correct", expectedLine.trandate, actualLine.trandate)
        Assert.assertEquals("Posting Period is not correct", expectedLine.postingperiod, actualLine.postingperiod)
        if (LANGUAGE.equals("en_US"))
            Assert.assertEquals("Cashflow Item is not correct", expectedLine.item, actualLine.item)
        else
            Assert.assertEquals("Cashflow Item is not correct", CashFlowEnum.getCnLabel(expectedLine.item), actualLine.item)
        Assert.assertEquals("Amount is not correct", expectedLine.amount, actualLine.amount)
    }

    protected CashFlowStatementPage openCashflowReport() {
        String url = context.resolveURL("customscript_sl_cn_cashflow", "customdeploy_sl_cn_cashflow")
        aut.navigateTo(url)
        return new CashFlowStatementPage(context)
    }

    /**
     * Format the currency string by removing currency symbol and seperator.
     * For example, if you input '¥234,167.00', it will return 234167.00.
     * @param currencyStr
     * @return
     */
    protected double formatCurrentStr(String currencyStr) {
        String regEx = '[`~$,￥¥%…（）’，、？\\s]'
        Pattern p = Pattern.compile(regEx)
        Matcher m = p.matcher(currencyStr)
        currencyStr = m.replaceAll("")
        double temp = Double.parseDouble(currencyStr)
        return Double.parseDouble(currencyStr)
    }

    def loadCFSTestData(def fileName, def caseId) {
        def testData = context.getFileContent(caseId, fileName)
        def testDataObj = new JsonSlurper().parseText(testData)
        return testDataObj
    }

    def createTransaction(String caseId, def data) {
        String url = context.resolveURL("customscript_sl_cn_ui_create_trans", "customdeploy_sl_cn_ui_create_trans")
        // TODO, need to handle the case that suitelet does not exist
        String fullUrlLink = context.getContext().getProperty("testautomation.nsapp.default.host") + url + "&caseid=" + caseId
        System.out.println("fullUrlLink: " + fullUrlLink)
        def dataStr = new groovy.json.JsonBuilder(data)
        String content = "{\"" + caseId + "\":{\"data\":" + dataStr + "}}"

        //go to CreateTransaction page and enter caseId/test data
        //context.navigateTo(fullUrlLink)
        context.navigateToNoWait(fullUrlLink)
        def btn_submit=elementHandler.waitForElementToBePresent(context.webDriver,".//*[@id='submitter']")
        Assert.assertNotNull("Create Transaction page not loaded!",btn_submit)
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='custpage_cn_case_id']")).sendKeys(caseId)
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='custpage_cn_data_file']")).sendKeys(content)
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='submitter']")).click()

        HtmlElementHandle handle = elementHandler.waitForElementToBePresent(context.webDriver, "xhtml:html/xhtml:body/xhtml:pre")
        Assert.assertNotNull("Create Transaction Failed!!", handle)
        String response = handle.getAsText()
        println('createTransaction response=' + response)
        return response
    }

    def deleteTransaction(String recordList) {
        String url = context.resolveURL("customscript_sl_cn_ui_delete_trans", "customdeploy_sl_cn_ui_delete_trans")
        String fullUrlLink = context.getContext().getProperty("testautomation.nsapp.default.host") + url;

        //go to DeleteTransaction page and enter caseId/test data
        context.navigateTo(url);
        HtmlElementHandle dataFile = elementHandler.waitForElementToBePresent(context.webDriver, ".//*[@id='custpage_cn_data_file']")
        dataFile.sendKeys(recordList)
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='submitter']")).click();

        HtmlElementHandle handle = elementHandler.waitForElementToBePresent(context.webDriver, "xhtml:html/xhtml:body/xhtml:pre")
        Assert.assertNotNull("Delete Transaction Failed!!", handle)
        String response = handle.getAsText()
        println('Delete Transaction response=' + response)
        return response
    }

    def deleteTransaction(String tranType, String internalId) {
        if (internalId != "null" && !StringUtils.isEmpty(internalId)) {
            record.deleteRecord(tranType, internalId)
        }
    }

    def getDataObj(String jsonPath, String caseId) {
        def dataString = context.getFileContent(caseId, jsonPath)
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId].data
        return dataObj
    }

    def getCaseObj(String filePath, String caseId) {
        def fileString = context.getFileContent(caseId, filePath)
        def jsonSlurper = new JsonSlurper()
        def fileObj = jsonSlurper.parseText(fileString)
        def caseObj = fileObj[caseId]
        return caseObj
    }

    //Get label on page according to current language
    def getLabel(String label) {
        if (isCurrentLanguageChinese()) {
            return CashFlowEnum.getCnLabel(label)
        } else if (isCurrentLanguageEnglish()) {
            return CashFlowEnum.getEnLabel(label)
        }
    }

    def assertTranIdNotNull(String tranId) {
        Assert.assertNotNull("The transaction is not saved successfully.", tranId)
    }

    def assertErrorMsg() {
        def expectedError = isTargetLanguageEnglish() ? VoucherEnum.VOUCHER_CFS_NEED_ENTER.getEnLabel() : VoucherEnum.VOUCHER_CFS_NEED_ENTER.getCnLabel()
        assertTrue("Should show error message.", context.isTextVisible(expectedError))
    }

    def assertNoErrorMsg() {
        def expectedError = isTargetLanguageEnglish() ? VoucherEnum.VOUCHER_CFS_NEED_ENTER.getEnLabel() : VoucherEnum.VOUCHER_CFS_NEED_ENTER.getCnLabel()
        checkFalse("Should not show error message", context.isTextVisible(expectedError))
    }

    def setTranCurrentRecord(String caseId, String fileName, String tranType) {
        def dataString = context.getFileContent(caseId, fileName)
        def jsonSlurper = new JsonSlurper()
        def caseObj = jsonSlurper.parseText(dataString)
        def dataObj = caseObj[caseId]

        //Wait for create transaction page to load
        context.navigateToNoWait(tranType)
        elementHandler.waitForElementToBePresent(context.webDriver,".//*[@id='_cancel']")
        context.acceptUpcomingConfirmation()
        currentRecord.setCurrentRecord(dataObj)
        context.clickSaveByAPI()
    }

    def deleteTransactionByTrantype(String internlId, String trantype) {
        if (internlId != "") {
            def toDelete = "[{\"internalid\":" + internlId + ",\"trantype\":\"" + trantype + "\"}]"
            deleteTransaction(toDelete)
        }
    }

    /**
     * @param subsidiaryName
     * @Author lisha.hao@oracle.com
     * @Description Enable checkbox 'China Cash Flow Item':
     *                   if OneWorld, go to 'Setup->Company->Subsidiaries->Subsidiary Page';
     *                   if SingleInstance, go to 'Setup->Company->Company Information'.
     */
    void enableCFSMandatory(String subsidiaryName) {
        if (isOW) {
            subsidirayPage.navigateToSubsidiaryEditPage(subsidiaryName)
            if (!subsidirayPage.isCheckedCFSMandatory()) {
                subsidirayPage.enableCFSMandatory()
                subsidirayPage.clickSave()
            }
        } else {
            companyInformationPage.navigateToCompanyInfoPage()
            if (!companyInformationPage.isCheckedSICFSMandatory()) {
                companyInformationPage.enableSICFSMandatory()
                companyInformationPage.clickSISave()
            }
        }
    }

    /**
     * @param subsidiaryName
     * @Author lisha.hao@oracle.com
     * @Description Disable checkbox 'China Cash Flow Item':
     *                   if OneWorld, go to 'Setup->Company->Subsidiaries->Subsidiary Page';
     *                   if SingleInstance, go to 'Setup->Company->Company Information'.
     */
    void disableCFSMandatory(String subsidiaryName) {
        if (isOW) {
            subsidirayPage.navigateToSubsidiaryEditPage(subsidiaryName)
            if (subsidirayPage.isCheckedCFSMandatory()) {
                subsidirayPage.disbleCFSMandatory()
                subsidirayPage.clickSave()
            }
        } else {
            companyInformationPage.navigateToCompanyInfoPage()
            if (companyInformationPage.isCheckedSICFSMandatory()) {
                companyInformationPage.disbleSICFSMandatory()
                companyInformationPage.clickSISave()
            }
        }
    }

    /**
     * @Author lisha.hao@oracle.com
     * @Description Below 7 methods are the assert methods for Invoice Sales Order results
     */
    def assertSubmitStatusonProcessStatus(String actualMsg) {
        def expectedComplete = isTargetLanguageEnglish() ? RecordErrorMsgEnum.RCRD_COMPLETE.enLabel : RecordErrorMsgEnum.RCRD_COMPLETE.cnLabel
        assertTrue("Submit status should be 'Complete'.", expectedComplete.equals(actualMsg))
    }

    def assertPercentCompleteonProcessStatus(actualMsg) {
        def expectedFail = isTargetLanguageEnglish() ? RecordErrorMsgEnum.RCRD_PERCENT_COMPLETE.enLabel : RecordErrorMsgEnum.RCRD_PERCENT_COMPLETE.cnLabel
        assertTrue("Percent Complete should be 100%.", expectedFail.equals(actualMsg))
    }

    def assert0ErrorMsgonProcessStatus(actualMsg) {
        def expectedError = isTargetLanguageEnglish() ? RecordErrorMsgEnum.RCRD_0ERROE_MSG.enLabel : RecordErrorMsgEnum.RCRD_0ERROE_MSG.cnLabel
        assertTrue("Message should be '0 Errors'.", expectedError.equals(actualMsg))
    }

    def assert1ErrorMsgonProcessStatus(actualMsg) {
        def expectedError = isTargetLanguageEnglish() ? RecordErrorMsgEnum.RCRD_1ERROE_MSG.enLabel : RecordErrorMsgEnum.RCRD_1ERROE_MSG.cnLabel
        assertTrue("Message should be '1 Error'.", expectedError.equals(actualMsg))
    }

    def assertFailRstonProcessRecords(actualMsg) {
        def expectedFail = isTargetLanguageEnglish() ? RecordErrorMsgEnum.RCRD_FAIL_MSG.enLabel : RecordErrorMsgEnum.RCRD_FAIL_MSG.cnLabel
        assertTrue("Should show error message.", expectedFail.equals(actualMsg))
    }

    def assertSuccessRstonProcessRecords(actualMsg) {
        def expectedFail = isTargetLanguageEnglish() ? RecordErrorMsgEnum.RCRD_SUCCESS_MSG.enLabel : RecordErrorMsgEnum.RCRD_SUCCESS_MSG.cnLabel
        assertTrue("Should show error message.", expectedFail.equals(actualMsg))
    }

    def assertErrorMsgProcess(actualMsg) {
        def expectedError = isTargetLanguageEnglish() ? VoucherEnum.VOUCHER_CFS_NEED_ENTER.getEnLabel() : VoucherEnum.VOUCHER_CFS_NEED_ENTER.getCnLabel()
        assertTrue("Should show error message.", expectedError.equals(actualMsg))
    }

    def createInvoiceSOviaAPI(def testData) {
        // Create Sales Order via API
        /*response = createTransaction(caseId, dataObj);
        // Navigate to Sales Order UI
        def responseObj = new JsonSlurper().parseText(response);
        def salesOrderId = responseObj[0].internalid;*/
        def returnResults = record.createRecord(testData)
        println("returnResults:" + returnResults)
    }




    /**
     * @Return salesOrderId
     * @Author lisha.hao@oracle.com
     * @Description Create Invoice Sales Order.
     *          1. New a Sales Order, save
     *          2. Approve
     *          3. Fulfill
     *          4. Go to Invoice sales order page, select the invoice created above
     *          5. Submit
     */
    def createInvoiceSO(def caseId, def dataObj) {
        // Create Sales Order via API
        switchToRole(getAccountant())
        response = createTransaction(caseId, dataObj);
        // Navigate to Sales Order UI
        def responseObj = new JsonSlurper().parseText(response);
        def salesOrderId = responseObj[0].internalid;
        context.navigateTo(CURL.HOME_CURL)
        switchToRole(administrator)

        // Approve
        salesOrderPage.navigateTo(salesOrderId)
        salesOrderPage.clickApprove()
        salesOrderPage.clickFulfill()
        fulfillPage.setFulfill()
        String orderNo = fulfillPage.getOrderNum()

        fulfillPage.clickSave()

        // Go to 'Invoice Sales Orders' page
        invoiceSalesOrder.navigateTo()
        invoiceSalesOrder.selectOrder(orderNo)
        processStatusPage.clickRefesh()
        return salesOrderId
    }
    /**
     * @Author lisha.hao@oracle.com
     * @Description Delete Invoice Sales Order:
     *          1. delete invoice
     *          2. delete sales order
     */
    def deleteInvoiceSO(def salesOrderId) {
        def response = "[{\"internalid\":" + salesOrderId + ",\"trantype\":\"invoice\"}," +
                "{\"internalid\":" + salesOrderId + ",\"trantype\":\"salesorder\"}]"
        deleteTransaction(response)
    }

    /**
     * @author lisha.hao
     * @param depositId - Bank Deposit internalid
     * @description Load saved search, add filter for deposit internalid, then execute search and return the results.
     */
    String executeSavedSearch(Long depositId) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("var search=nlapiLoadSearch('customrecord_cn_cashflow_record_detail','customsearch_cn_cf_detail');")
                .append("var newFilter=new nlobjSearchFilter('custrecord_cfs_deposit_tranid',null,'is',").append(depositId).append(");")
                .append("search.addFilter(newFilter);")
                .append("var results = search.runSearch().getResults(0,10) || [];")
                .append("var rtnResults = [];")
                .append("for(var i=0;i<results.length;i++) {")
                .append("  rtnResults.push({")
                .append("    'item': results[i].getText('custrecord_cfs_item'),")
                .append("    'amount': results[i].getValue('custrecord_cfs_amount'),")
                .append("    'depositId': results[i].getValue('custrecord_cfs_deposit_tranid'),")
                .append("    'pymtId': results[i].getValue('custrecord_cfs_pymt_tranid'),")
                .append("    'paidId': results[i].getValue('custrecord_cfs_paid_tranid')")
                .append("  });")
                .append("}")
                .append("return JSON.stringify(rtnResults);");
        String result = context.executeScript(sBuilder.toString());
        context.webDriver.waitForPageToLoad();
        return result;
    }

    /**
     * Accept customer payment from Transaction->Customer->Accept Customer Payment if Admin role.
     * @param {JSON object} [required] caseObj - The JSON object for case related data.
     * @param {JSON object} [required] uidata - The JSON object for ui input data.
     * @return { J S O N object} : [recordName: internlId, recordType: "customerpayment"]
     * @Author mia.wang@oracle.com
     */
    def acceptCustomerPayment(def caseObj, def uidata) {
        context.navigateTo(CURL.CUSTOMER_PAYMENT_CURL)
        context.waitForPageToLoad()

        String entityId = Utility.getEntityId(context, "customer", uidata.main.customer)
        context.setFieldWithValue("customer", entityId)
        context.waitForPageToLoad()

        //Set AR Account 11220103 应收账款 : 销售商品 : 设备T1
        context.setFieldWithText("aracct", uidata.main.aracct)
        context.waitForPageToLoad()

        //Set bank account
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='undepfunds_fs_inp'][@class='radio'][@value='F']")).scrollToView()
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='undepfunds_fs_inp'][@class='radio'][@value='F']")).click()
        context.setFieldWithText("account", uidata.main.account)
        context.waitForPageToLoad()

        //Set Transaction Date
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='trandate']")).clear()
        context.waitForPageToLoad()
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='trandate']")).sendKeys(uidata.main.trandate)
        context.setFieldWithText("postingperiod", uidata.main.postingperiod)

        context.waitForPageToLoad() //posting period will be refreshed according to transdate

        //Filter transaction by Transaction Date From/To
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='Transaction_TRANDATEfrom']")).clear()
        context.waitForPageToLoad()
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='Transaction_TRANDATEto']")).clear()
        context.waitForPageToLoad()
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='Transaction_TRANDATEfrom']")).sendKeys(uidata.main.transaction_trandatefrom + Keys.ENTER)
        context.waitForPageToLoad()
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='Transaction_TRANDATEto']")).sendKeys(uidata.main.transaction_trandate + Keys.ENTER)
        context.waitForPageToLoad()

        //Check apply list and amountXpath
        uidata.apply.each { transaction ->
            //transaction.refid
            def trantype = context.getRecordTrantype(caseObj, transaction.refid)
            def tranId = getTranidForRecordFromRefid(caseObj, transaction.refid, response)

            String checkBoxXpath = ""
            String amountXpath = ""
            if (trantype == "invoice") {
                checkBoxXpath = ".//*[@id='apply_div']/table[@id='apply_splits']/*/tr/td[text()='" + tranId + "']/../td[span[@class='checkbox_unck']]/span"
                amountXpath = ".//*[@id='apply_div']/table[@id='apply_splits']/*/tr/td[text()='" + tranId + "']/../td[span[contains(@id, 'apply_amount')]]/span/input"

            } else if (trantype == "creditmemo") {
                checkBoxXpath = ".//*[@id='credit_div']/table[@id='credit_splits']/*/tr/td[text()='" + tranId + "']/../td[span[@class='checkbox_unck']]/span"
                amountXpath = ".//*[@id='credit_div']/table[@id='credit_splits']/*/tr/td[text()='" + tranId + "']/../td[span[contains(@id, 'credit_amount')]]/span/input"
                context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='credittxt']")).scrollToView()
                context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='credittxt']")).click()
            } else if (trantype == "customerdeposit") {
                checkBoxXpath = ".//*[@id='deposit_div']/table[@id='deposit_splits']/*/tr/td[text()='" + tranId + "']/../td[span[@class='checkbox_unck']]/span"
                amountXpath = ".//*[@id='deposit_div']/table[@id='deposit_splits']/*/tr/td[text()='" + tranId + "']/../td[span[contains(@id, 'deposit_amount')]]/span/input"
                context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='deposittxt']")).scrollToView()
                context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='deposittxt']")).click()
            }

            //Check corresponding checkbox
            context.webDriver.getHtmlElementByLocator(Locator.xpath(checkBoxXpath)).scrollToView()
            context.webDriver.getHtmlElementByLocator(Locator.xpath(checkBoxXpath)).click()
            //Set payment amount
            context.webDriver.getHtmlElementByLocator(Locator.xpath(amountXpath)).clear()
            context.webDriver.getHtmlElementByLocator(Locator.xpath(amountXpath)).sendKeys(transaction.amount + Keys.ENTER)
            context.waitForPageToLoad()
        }

        //Submit changes
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='btn_secondarymultibutton_submitter']")).scrollToView()
        context.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='btn_secondarymultibutton_submitter']")).click()
        context.waitForPageToLoad()

        //last step
        //Get payment id
        def internlId = context.getParameterValueForFromQueryString("id")
        return [recordName: internlId, recordType: "customerpayment"]
    }

    def getTranidFromRespone(String response, String trantype) {
        def jsonslurper = new JsonSlurper()
        def responseObj = jsonslurper.parseText(response)
        def tranid = ""
        responseObj.any() { transaction ->
            if (transaction.trantype == trantype) {
                tranid = transaction.internalid
            }
        }
        return tranid
    }

    def getTranIdForRecord(String recordType, String internalId) {
        def recordObj = context.loadRecord(recordType, internalId)
        String tranid = recordObj.fields.tranid
        println "tranid: " + tranid
        return tranid
    }

    def getTranidForRecordFromRefid(def caseObj, def refid, def response) {
        def caseId = caseObj.keySet()[0].toString()
        def index = null
        for (def i = 0; i < caseObj[caseId].data.size(); i++) {
            if (caseObj[caseId].data.main.id.get(i) == refid) {
                index = i
                break
            }
        }
        def jsonSlurper = new JsonSlurper()
        def responseObj = jsonSlurper.parseText(response)
        if (index != null) {
            def record = responseObj[index]
            def id = context.getRecordTranid(record.trantype, record.internalid)
            return id
        } else {
            return null
        }
    }

    def getInternalIdForRecordFromRefid(def caseObj, def refid, def response) {
        def caseId = caseObj.keySet()[0].toString()
        def index = null
        for (def i = 0; i < caseObj[caseId].data.size(); i++) {
            if (caseObj[caseId].data.main.id.get(i) == refid) {
                index = i
                break
            }
        }
        def jsonSlurper = new JsonSlurper()
        def responseObj = jsonSlurper.parseText(response)
        if (index != null) {
            def record = responseObj[index]
            def id = record.internalid
            return id
        } else {
            return null
        }
    }

    def clickExpense() {
        if (context.doesFormSubTabExist(EXPENSETAB)) {
            context.clickFormSubTab(EXPENSETAB)
        }
    }

    def clickItem() {
        if (context.doesFormSubTabExist(ITEMTAB)) {
            context.clickFormSubTab(ITEMTAB)
        }
    }
}
