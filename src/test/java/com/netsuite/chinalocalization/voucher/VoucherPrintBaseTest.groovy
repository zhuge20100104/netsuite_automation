package com.netsuite.chinalocalization.voucher

import com.google.inject.Inject
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.cashflow.CONSTANTS.CashFlowEnum
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.chinalocalization.page.InvoicePage
import com.netsuite.chinalocalization.page.Setup.CompanyInformationPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.chinalocalization.page.VendorBillPage
import com.netsuite.chinalocalization.page.voucher.GLnumberingPage
import com.netsuite.chinalocalization.page.voucher.VoucherPrintPage
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Assert
import org.junit.Before
import com.netsuite.testautomation.html.HtmlElementHandle

/**
 * @desc Base test class for VoucherPrint
 * @author jingzhou.wang@oracle.com
 * @Update:
 * Add MULTICURRENCY and URL
 * 		Move navigation method to VoucherPrintPage
 * 		mia.wang@oracle.com
 * 		2018-Mar-07
 */
class VoucherPrintBaseTest extends BaseAppTestSuite {

    @Inject
    GLnumberingPage gLnumberingPage
    @Inject
    InvoicePage invoicePage
    @Inject
    NetSuiteAppCN netSuiteAppCN
    @Inject
    ElementHandler elementHandler
    @Inject
    CompanyInformationPage companyInformationPage;
    @Inject
    SubsidiaryPage subsidirayPage;

    protected String ADD;
    protected String CANCEL;
    protected String ITEMTAB;
    protected String EXPENSETAB;
    protected String PROMOTIONTAB;
    protected String LANGUAGE;
    protected String CREDIT;
    protected String SAVE;
    protected String DELETE
    protected String REFNO;
    public static String URL;
    public VoucherPrintPage voucherPrintPage;
    public static boolean MULTICURRENCY;
    static String SINGLE_CURRENCY = "Single Currency"
    static String MULTI_CURRENCIES = "Multiple Currencies"
    protected static boolean isOW = true

    @Before
    void setUp() {
        super.setUp()
        URL = resolveSuiteletURL("customscript_sl_cn_voucher_print", "customdeploy_sl_cn_voucher_print");
        def script = "return nlapiGetContext().getFeature('MULTICURRENCY');"
        MULTICURRENCY = context.executeScript(script).toBoolean();
        voucherPrintPage = new VoucherPrintPage(context, MULTICURRENCY);
        afterSetup()
    }

    @After
    void tearDown() {
        switchWindow()
    }

    def afterSetup() {
        try {
            isOW = context.isOneWorld()
            //Make sure CHina BU is not CFS mandatory
            disableCFSMandatory("China BU")
        } catch (Exception e) {
        }
        if (isTargetLanguageEnglish()) {
            ADD = "Add";
            ITEMTAB = "Items";
            EXPENSETAB = "Expenses";
            PROMOTIONTAB = "Promotions"
            LANGUAGE = "en_US";
            CANCEL = "Cancel";
            CREDIT = "Credit";
            SAVE = "Save"
            DELETE = "Delete"
            REFNO = "Ref No.";
        } else {
            ADD = "添加";
            ITEMTAB = "货品";
            EXPENSETAB = "费用";
            PROMOTIONTAB = "促销"
            LANGUAGE = "zh_CN";
            CANCEL = "取消";
            CREDIT = "贷记";
            SAVE = "保存"
            DELETE = "删除"
            REFNO = "参考编号";
        }
        SINGLE_CURRENCY = getLabel(SINGLE_CURRENCY)
        MULTI_CURRENCIES = getLabel(MULTI_CURRENCIES)
    }

    def switchWindow() {
        context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
        def currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
    }

    def getCaseObj(String filePath, String caseId) {
        String fileString = new File("data/" + filePath).getText('UTF-8')
        def jsonSlurper = new JsonSlurper()
        def fileObj = jsonSlurper.parseText(fileString)
        def caseObj = fileObj[caseId]
        return caseObj
    }

    String createTransaction(String fileName, String caseId) {
        String url = context.resolveURL("customscript_sl_cn_ui_create_trans", "customdeploy_sl_cn_ui_create_trans")
        Assert.assertFalse(url.equals("You have provided an invalid script id or internal id: customscript_sl_cn_ui_create_trans"))
        Assert.assertFalse(url.equals("您提供了无效的脚本 ID 或内部 ID: customscript_sl_cn_ui_create_trans"))
        println "Create trans url:" + url
        String responseFromSuitelet = context.createTransaction(caseId, fileName, url)
        context.navigateTo(CURL.HOME_CURL)
        return responseFromSuitelet
    }

    //Get label on page according to current language
    def getLabel(String label) {
        if (isCurrentLanguageChinese()) {
            return VoucherMsgEnum.getCnLabel(label)
        } else if (isCurrentLanguageEnglish()) {
            return VoucherMsgEnum.getEnLabel(label)
        }
    }

    def deleteTransaction(String tranType, String internalId) {
        if (internalId != "null") {
            record.deleteRecord(tranType, internalId)
        }
    }

    def setTranCurrentRecord(def dataObj, String tranType) {
        context.navigateTo(tranType)
        context.acceptUpcomingConfirmation()
        currentRecord.setCurrentRecord(dataObj)
        context.clickSaveByAPI()
    }

    void verifyReportHeader(def expect, def actualHeader, String baseglNumber) {
        def expectedHeader = expect.expectedHeader
        def siCompanyName = context.getContext().getProperty("testautomation.nsapp.default.account")
        if (context.isOneWorld()) {
            checkAreEqual("Please check Subsidiary", actualHeader.subsidiary, expectedHeader.subsidiary)
        } else {
            checkAreEqual("Please check Subsidiary", actualHeader.subsidiary, siCompanyName)
        }
        checkAreEqual("Please check Transaction Type", actualHeader.tranType, getLabel(expectedHeader.tranType))
        checkAreEqual("Please check TranDate", actualHeader.date, expectedHeader.trandate)
        checkAreEqual("Please check glNumber", actualHeader.voucherNo, baseglNumber)
    }

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


    void verifyReportContent(def expectedDataObj) {
        if (expectedDataObj.expectedBody) {
            for (def expectedLineObj : expectedDataObj.expectedBody) {
                def reportLineObj = voucherPrintPage.getReportLineOnBody(0, expectedLineObj)
                if (expectedLineObj.lineDesp) {
                    checkAreEqual("Line should show correctly", reportLineObj.acctDesp, expectedLineObj.acctDesp)
                }
                if (expectedLineObj.acctDesp) {
                    checkAreEqual("Account should show correctly", reportLineObj.acctDesp, expectedLineObj.acctDesp)
                }
                if (expectedLineObj.funcCcyCredit) {
                    checkAreEqual("Credit value should show correctly", reportLineObj.funcCcyCredit, expectedLineObj.funcCcyCredit)
                }
                if (expectedLineObj.funcCcyDebit) {
                    checkAreEqual("Debit value should show correctly", reportLineObj.funcCcyDebit, expectedLineObj.funcCcyDebit)
                }
            }
        }
    }

    def composeGLNumber(String prefix, int leastDigit, int number) {
        String ret = number
        int d = number.toString().size()
        for (int i = 0; i < leastDigit - d; i++) {
            ret = "0" + ret
        }
        ret = prefix + ret
        return ret
    }

    def loadCFSTestData(def fileName, def caseId) {
        def testData = context.getFileContent(caseId, fileName)
        def testDataObj = new JsonSlurper().parseText(testData)
        return testDataObj
    }

    /**
     * @author lisha.hao
     * @param lineNum start from 1
     * @param fieldId - "quantity"
     * @param fieldText
     * @desc
     */
    def setItemLineValue(int lineNum, String fieldId, String fieldText) {
        if (isTargetLanguageEnglish()) {
            ADD = "OK"
        } else {
            ADD = "确定"
        }
        EditMachineCN sublist = context.withinEditMachine("item")
        sublist.setCurrentLine(lineNum)
        sublist.setFieldWithValue(fieldId, fieldText)
        context.clickButton(ADD)
    }

    /**
     * @author lisha.hao
     * @param context
     * @param lineNum - start from 1
     * @param fieldId
     * @param fieldText
     * @return
     * @desc
     */
    def editInvoiceZero(def context, int lineNum, String fieldId, String fieldText) {
        setItemLineValue(lineNum, fieldId, fieldText)
        invoicePage.clickSave()
        def internlId = context.getParameterValueForFromQueryString("id");
        return [internalid: internlId, trantype: "invoice"];
    }

    /**
     * @author yang.liu
     * @param lineNum start from 1
     * @param fieldId - "quantity"
     * @param fieldText
     * @desc
     */
    def setExpenseLineValue(int lineNum, String fieldId, String fieldText) {
        if (isTargetLanguageEnglish()) {
            ADD = "OK"
        } else {
            ADD = "确定"
        }
        EditMachineCN sublist = context.withinEditMachine("expense")
        sublist.setCurrentLine(lineNum)
        sublist.setFieldWithValue(fieldId, fieldText)
        context.clickButton(ADD)
    }

    /**
     * @author yang.liu
     * @param context
     * @param lineNum - start from 1
     * @param fieldId
     * @param fieldText
     * @return
     * @desc
     */
    def editBillAmountToZero(def context, int lineNum, String fieldId, String fieldText) {
        setExpenseLineValue(lineNum, fieldId, fieldText)
        netSuiteAppCN.clickSaveByAPI()
        def internlId = context.getParameterValueForFromQueryString("id")
        return [internalid: internlId, trantype: "vendorbill"]
    }
    /**
     * @author lisha.hao
     * @param caseObj
     * @param glNoBase
     * @param index - the expect Voucher number, start from 1
     * @param expectHeaders
     */
    def verifyGLNO(def caseObj, def glNoBase, def index, def expectHeaders) {
        def actualHeaders = []
        String prefix
        String GLNumber
        voucherPrintPage.navigateToURL()
        waitForPageToLoad()

        for (int i = 0; i < index; i++) {
            glNoBase++
            prefix = "GL2018Q1"
            GLNumber = composeGLNumber(prefix, 6, glNoBase)
            voucherPrintPage.setParametersWithGLN(caseObj.label, SINGLE_CURRENCY, getLabel("is"), GLNumber)
            voucherPrintPage.clickRefreshBtn()
            if(isReportShow()) {
                actualHeaders.add(voucherPrintPage.getVoucherReportMain(0))
                //Verify Header
                verifyReportHeader(expectHeaders[i], actualHeaders[i], GLNumber)
                //Verify Body
                verifyReportContent(expectHeaders[i])
            }
        }
    }

    //Check after click refresh button , if report show
    boolean isReportShow() {
        HtmlElementHandle report_header = elementHandler.waitForElementToBePresent(context.webDriver, "//table[@class='header']")
        //Assert.assertNotNull("Report is not show!",report_header)
        return (report_header != null)
    }

}