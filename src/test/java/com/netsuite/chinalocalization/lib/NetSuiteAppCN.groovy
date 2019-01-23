package com.netsuite.chinalocalization.lib

import com.google.inject.Inject
import com.google.common.collect.ImmutableSet
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.cashflow.CONSTANTS.CashFlowEnum
import com.netsuite.chinalocalization.cashflow.CONSTANTS.RecordErrorMsgEnum
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.common.ext.MultiSelectExt;
import com.netsuite.testautomation.aut.NetSuiteApp
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.chinalocalization.common.control.CNDropdownList
import com.netsuite.testautomation.aut.system.NSCredentials
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.enums.Page
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.reporting.ReportMe
import com.netsuite.testautomation.utils.AutomationContext
import com.netsuite.testautomation.utils.TestProperties
import groovy.json.JsonSlurper
import net.qaautomation.common.Analyzer
import net.qaautomation.exceptions.SystemException
import net.qaautomation.exceptions.TestCaseExecutionException
import org.apache.log4j.Logger
import org.junit.Assert

import javax.annotation.Nonnull
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Method


public class NetSuiteAppCN extends NetSuiteApp {

    @Inject
    NRecord record
    @Inject
    ElementHandler elementHandler

    private static final Logger log = Logger.getLogger(NetSuiteAppCN.class)

    public NetSuiteAppCN() {
        this(new AutomationContext())
    }

    @Inject
    public NetSuiteAppCN(AutomationContext context) {
        super(context)
        this.webDriver.registerPageLoadCondition(new NetSuiteAppCN.PageLoadJSCompletedCondition())
    }

    void navigateToAndView(def url, def internalId) {
        this.navigateTo(url + "?id=" + internalId + "&whence=")
    }

    /***
     * get Head Field Type By field Id
     * @param fieldId
     * @return Head Field Type
     */
    public String getHeadFieldType(String fieldId) {
        return this.executeScript("return (nlapiGetField('" + fieldId + "').getType());")
    }


    boolean postDeleteCheck(String response) {
        def jsonSlurper = new JsonSlurper();
        def object = jsonSlurper.parseText(response);
        def status = true;
        object.any {
            def record = this.loadRecord(it.trantype, it.internalid);
            if (!record.equals(RecordErrorMsgEnum.RCRD_DSNT_EXIST.getEnLabel()) &&
                    !record.equals(RecordErrorMsgEnum.RCRD_DSNT_EXIST.getCnLabel()))
                status = false;
        }
        return status;
    }

    class PageLoadJSCompletedCondition implements Analyzer<WebDriver> {
        public float getMaxRank() {
            return 1.0F;
        }

        public float rank(WebDriver webDriver) {
            String readyState = NetSuiteAppCN.this.webDriver.executeScript("return window.NLScriptId");
            return readyState == null ? 0.0F : 1.0F;
        }

        public String toString() {
            return "Page Execute JS Complete Condition";
        }
    }

    public String getUserLanguage() {
        String userLanguage = this.webDriver.executeScriptIgnoreAlert("return nlapiGetContext().getPreference('language');");
        return userLanguage;
    }

    public void setDefaultLanguageToChinese() {
        String language = this.executeScript("return nlapiGetContext().getPreference('language');");
        if (!language.equals("zh_CN")) {
            this.navigateTo("/app/center/userprefs.nl");
            this.withinDropdownlist("LANGUAGE").selectItem("简体中文");
            this.webDriver.getHtmlElementByLocator(Locator.xpath("/*//*[@id='submitter']")).scrollToView();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.webDriver.click(Locator.xpath("/*//*[@id='submitter']"));
            this.waitForPageToLoad();
        }
    }

    public String resolveURL(String scriptId, String deployId) {
        String url = this.executeScript("try { return nlapiResolveURL('SUITELET', '" + scriptId + "', '" + deployId + "', null)} catch(ex) {return ex.message}");
        return url;
    }

    String resolveRestletURL(String scriptId, String deploymentId) {
        return this.executeScript("return nlapiResolveURL('RESTLET', '" + scriptId + "', '" + deploymentId + "')");
    }

    String createTransaction(String caseId, String dataFileName, String url) {
        String content = getFileContent(caseId, dataFileName);
        String fullUrlLink = this.getContext().getProperty("testautomation.nsapp.default.host") + url + "&caseid=" + caseId;

        //go to CreateTransaction page and enter caseId/test data
        //this.navigateTo(fullUrlLink);
        this.navigateToNoWait(fullUrlLink)
        def btn_submit=elementHandler.waitForElementToBePresent(this.webDriver,".//*[@id='submitter']")
        Assert.assertNotNull("Create Transaction page not loaded!",btn_submit)
        this.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='custpage_cn_case_id']")).sendKeys(caseId);
        this.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='custpage_cn_data_file']")).sendKeys(content);
        this.webDriver.getHtmlElementByLocator(Locator.xpath(".//*[@id='submitter']")).click();
        String xpath = "xhtml:html/xhtml:body/xhtml:pre"
        HtmlElementHandle element = elementHandler.waitForElementToBePresent(this.webDriver, xpath)
        Assert.assertNotNull("Create Transacion Failed!!!", element)
        String response = element.getAsText()
        return response
    }

    boolean deleteTransaction(String recordList) {
        def list = new JsonSlurper().parseText(recordList);
        def status = true;
        for (int i = list.size(); i > 0; i--) {
            def type = list[i - 1].trantype;
            def internalid = list[i - 1].internalid;
            status = record.deleteRecordRecurssive(type, internalid);
        }
        return status;

    }

    boolean deleteTransaction(String recordType, String internalId) {
        def status = true;
        String script = "nlapiDeleteRecord('" + recordType + "', " + internalId + ")";
        try {
            this.executeScript(script);
            Thread.sleep(2000)
        } catch (Exception ex) {
            log.error("Delete record id " + internalId + " of type " + recordType + " failed due to \"" + ex.message + "\"");
            status = false;
        }
        return status;

    }

    def loadRecord(def type, def internalId) {
//        String script = "try { return nlapiLoadRecord('" + type + "'," + internalId + "); } catch(err) { return err.message;}";
        String script = "try {var record = nlapiLoadRecord('" + type + "'," + internalId + ");\n var fields = record.fields; return fields;} catch(err) { return err.message;}";
        println script;
        String result = this.executeScript(script);
        Thread.sleep(2000)
        return result;
    }

    def getRecordTranid(def type, def internalid) {
        String script = "try {var record = nlapiLoadRecord('" + type + "'," + internalid + ");\n var tranid = record.fields.tranid; return tranid;} catch(err) { return err.message;}";
        String result = this.executeScript(script);
        Thread.sleep(2000)
        return result;
    }

    def getRecordTrantype(def caseObj, def refid) {
        def trantype = null;
        caseObj[caseObj.keySet()[0]].data.any { data ->
            if (data.main.id == refid)
                trantype = data.main.trantype;
        }
        return trantype;
    }

    def getTranidForRecordFromRefid(def caseObj, def refid, def response) {
        def caseId = caseObj.keySet()[0].toString();
        def index = null;
        for (def i = 0; i < caseObj[caseId].data.size(); i++) {
            if (caseObj[caseId].data.main.id.get(i) == refid) {
                index = i;
                break;
            }
        }
        def jsonSlurper = new JsonSlurper();
        def responseObj = jsonSlurper.parseText(response);
        if (index != null) {
            def record = responseObj[index];
            def id = getRecordTranid(record.trantype, record.internalid);
            return id;
        } else {
            return null;
        }
    }

    public String getFileContent(String caseId, String fileName) {
        String content = new File('data//' + fileName).getText('UTF-8')
        //CFS label change between en_US and zh_CN if necessary
        String language = this.getUserLanguage();
        if (language.equals("zh_CN")) {
            def m = content =~ /cust(col|body)_cseg_cn_cfi[^:]:[^"]"([^"].+)"/;
            (0..<m.count).each {
                //print m[it][0] + '\n';
                def label = m[it][2];
                if (label != null) {
                    def cnlabel = CashFlowEnum.getCnLabel(label);
                    content = content.replaceAll(label, cnlabel);
                }
            }
        }

        def jsonSlurper = new JsonSlurper();
        def object = jsonSlurper.parseText(content);
        def targetcase = new groovy.json.JsonBuilder(object[caseId]);
        return "{\"" + caseId + "\":" + targetcase + "}";

    }

    def getUIDataObj(def caseObj, def id) {
        def resutObj = null;
        caseObj[caseObj.keySet()[0]].ui.any { uidata ->
            if (uidata.main.id == id)
                resutObj = uidata;
        }
        return resutObj;
    }

    def getExpectedObj(def caseObj, def id) {
        def expectedObj = null;
        caseObj[caseObj.keySet()[0]].expected.any { expect ->
            if (expect.keySet()[0] == id)
                expectedObj = expect;
        }
        return expectedObj;
    }

    @Override
    public void setDefaultLanguageToEnglish() {
        String language = this.executeScript("return nlapiGetContext().getPreference('language');");
        if (!language.equals("en_US")) {
            this.navigateTo("/app/center/userprefs.nl");
            this.withinDropdownlist("LANGUAGE").selectItem("English (U.S.)");
            this.webDriver.getHtmlElementByLocator(Locator.xpath("/*//*[@id='submitter']")).scrollToView();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.webDriver.click(Locator.xpath("/*//*[@id='submitter']"));
            this.waitForPageToLoad();
        }
    }

    public void forceSetDefaultLanguageToEnglish() {
        this.navigateTo("/app/center/userprefs.nl");
        this.withinDropdownlist("LANGUAGE").selectItem("English (U.S.)");
        this.webDriver.getHtmlElementByLocator(Locator.xpath("/*//*[@id='submitter']")).scrollToView();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.webDriver.click(Locator.xpath("/*//*[@id='submitter']"));
        this.waitForPageToLoad();
    }

    public void forceSetDefaultLanguageToChinese() {
        this.navigateTo("/app/center/userprefs.nl");
        this.withinDropdownlist("LANGUAGE").selectItem("简体中文");
        this.webDriver.getHtmlElementByLocator(Locator.xpath("/*//*[@id='submitter']")).scrollToView();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.webDriver.click(Locator.xpath("/*//*[@id='submitter']"));
        this.waitForPageToLoad();
    }

    public String getSavedReportId(String reportName) {
        return this.executeScript("var searchResult=nlapiSearchGlobal('page:" + reportName + "');for(var i=0;searchResult&&i<searchResult.length;i++){if (searchResult[i].getValue('name').toLowerCase().trim() === '" + reportName + "'.toLowerCase().trim()) {return searchResult[i].id.replace(/REPO_/, '');}}");
    }

    public List<String> getCNdropdownOptions(String fieldId) {
        CNDropdownList dropdownList = this.withinCNDropdownlist(fieldId);
        return dropdownList.getOptions();
    }

    public CNDropdownList withinCNDropdownlist(String fieldId) {
        return new CNDropdownList(this, this.webDriver, fieldId);
    }


    @ReportMe
    public EditMachineCN withinEditMachine(String machineForm) {
        return new EditMachineCN(this, this.webDriver, machineForm);
    }

    @ReportMe
    public MultiSelectExt withinMultiSelectField(@Nonnull String fieldId) {
        return new MultiSelectExt(this, this.webDriver, Locator.xpath("//input[@name='" + fieldId + "']/ancestor::*[@class='uir-field']"));
    }

    def navigateToHomePage() {
        navigateToNoWait(CURL.HOME_CURL)
        elementHandler.waitForElementToBePresent(context.webDriver,"//*[@id='ns-dashboard-heading-panel']")
    }

    def navigateToCustomerNewPage() {
        navigateTo("/app/common/entity/custjob.nl")
    }

    def navigateToCustomerEditPage(internalID) {
        navigateTo("/app/common/entity/custjob.nl?id=" + internalID + "&e=T&stage=Customer")
    }

    def navigateToSubsidiaryNewPage() {
        navigateTo("/app/common/otherlists/subsidiarytype.nl")
    }

    def navigateToCompanyInformationPage() {
        navigateTo("/app/common/otherlists/company.nl")
    }

    def navigateToPrepareEstimatesPage() {
        navigateTo("/app/accounting/transactions/estimate.nl")
    }

    def navigateToEnterSalesOrdersPage() {
        navigateTo("/app/accounting/transactions/salesord.nl")
    }

    def navigateToPrepareEstimatesEditPage(internalId) {
        navigateTo("/app/accounting/transactions/estimate.nl?id=" + internalId + "&e=T")
    }

    def navigateToEnterSalesOrdersEditPage(internalId) {
        navigateTo("/app/accounting/transactions/salesord.nl?id=" + internalId + "&e=T")
    }

    def navigateToCreateInvoicePage() {
        navigateTo("/app/accounting/transactions/custinvc.nl")

    }

    def navigateToEnterCashSalesPage() {
        navigateTo("/app/accounting/transactions/cashsale.nl")

    }

    def navigateToIssueCreditMemosPage() {
        navigateTo("/app/accounting/transactions/custcred.nl")

    }

    def navigateToRefundCashSalesPage() {
        navigateTo("/app/accounting/transactions/cashrfnd.nl")


    }

    def navigateToInvoiceEditPage(internalId) {
        navigateTo("/app/accounting/transactions/custinvc.nl?id=" + internalId + "&e=T")
    }

    def navigateToCashRefundEditPage(internalId) {
        navigateTo("/app/accounting/transactions/cashrfnd.nl?id=" + internalId + "&e=T")
    }

    def navigateToCashSaleEditPage(internalId) {
        navigateTo("/app/accounting/transactions/cashsale.nl?id=" + internalId + "&e=T")
    }

    def navigateToDiscountItemNewPage() {
        navigateTo("/app/common/item/item.nl?itemtype=Discount&subtype=&isserialitem=F&islotitem=F")
    }

    def navigateToInventoryItemNewPage() {
        navigateTo("/app/common/item/item.nl?itemtype=InvtPart&subtype=&isserialitem=F&islotitem=F")
    }

    def navigateToLotNumberedNewPage() {
        navigateTo("/app/common/item/item.nl?itemtype=InvtPart&subtype=&isserialitem=F&islotitem=T")
    }

    def navigateToItemGroupNewPage() {
        navigateTo("/app/common/item/item.nl?itemtype=Group&subtype=&isserialitem=F&islotitem=F")
    }

    def navigateToKitPackageNewPage() {
        navigateTo("/app/common/item/item.nl?itemtype=Kit&subtype=&isserialitem=F&islotitem=F")
    }

    def navigateToNonInventoryItemNewPage() {
        navigateTo("/app/common/item/item.nl?itemtype=NonInvtPart&subtype=Sale&isserialitem=F&islotitem=F")
    }

    def navigateToOtherChargeItemNewPage() {
        navigateTo("/app/common/item/item.nl?itemtype=OthCharge&subtype=Resale&isserialitem=F&islotitem=F")
    }

    def navigateToServiceItemNewPage() {
        navigateTo("/app/common/item/item.nl?itemtype=Service&subtype=Purchase&isserialitem=F&islotitem=F")
    }

    def navigateToItemEditPage(internalId) {
        navigateTo("/app/common/item/item.nl?id=" + internalId + "&e=T")
    }

    def isInvoiceEditPage(internalId) {
        return getPageUrl().contains("/app/accounting/transactions/custinvc.nl?id=${internalId}&e=T")
    }

    def isSetupManagerPage() {
        return getPageUrl().contains("/app/setup/mainsetup.nl?")
    }

    def isOneWorld() {
        return Boolean.parseBoolean(this.executeScript("return nlapiGetContext().getFeature('SUBSIDIARIES');"))
    }

    def isSingleInstance() {
        return !isOneWorld()
    }

    def isCloseBrowserOnTeardown() {
        return Boolean.parseBoolean(this.context.getProperty("testautomation.selenium.close_browser_on_teardown"))
    }

    def isCloseBrowserOnSuiteEnd() {
        return Boolean.parseBoolean(this.context.getProperty("testautomation.selenium.close_browser_on_suite_end"))
    }

    def asJSON(options) {
        if (options.path) {
            return new JsonSlurper().parseText(new File(options.path.replace("\\", File.separator)).getText('UTF-8'))
        } else if (options.text) {
            return new JsonSlurper().parseText(options.text)
        }
    }

    def downloadFilePath() {
        return getContext().getProperty("testautomation.file_download_path") + "/"
    }

    boolean logInViaForm(NSCredentials credentials) {
        return this.logInViaForm(credentials, (String) null);
    }


    boolean logInViaForm(NSCredentials credentials, String url) {
        String navUrl = url == null ? Page.LOGIN_JSP.getUrl() : url;
        this.navigateTo(navUrl);
        log.info("Navigated to: " + this.webDriver.getPageUrl());

        try {
            this.setFieldIdentifiedByWithValue(Locator.xpath("//input[@name='email']"), credentials.getEmail());
            HtmlElementHandle inputField = this.webDriver.getHtmlElementByLocator(Locator.name("password"));
            this.webDriver.setInputField(inputField, credentials.getPassword());
            HtmlElementHandle submitter = this.webDriver.getHtmlElementByLocator(Locator.id("Submit"));
            if (submitter != null) {
                submitter.click();
            } else {
                submitter = this.getButton((HtmlElementHandle) null, "login", 1);
                if (submitter != null) {
                    submitter.click();
                }
            }
            if (submitter == null) {
                this.clickImageLink("login.gif");
            }
        } catch (Exception var6) {
            throw this.getExceptionFactory().createNSAppException("Could not log in", var6, true);
        }
        return this.isLoginSuccessful();
    }

    def navigateToCreditMemosEditPage(internalId) {
        navigateTo("/app/accounting/transactions/custcred.nl?id=" + internalId + "&e=T")
    }

    def isFeatureEnabled(feature) {
        def Script = "return nlapiGetContext().getFeature('" + feature + "');"
        return Boolean.parseBoolean(this.executeScript(Script))
    }

    def getPreference(preference) {
        def Script = "return nlapiGetContext().getPreference('" + preference + "');"
        return this.executeScript(Script)
    }

    def isAllClassificationEnabled() {
        if ((isFeatureEnabled("LOCATIONS")) && (isFeatureEnabled("DEPARTMENTS")) && (isFeatureEnabled("CLASSES"))) {
            return true
        } else {
            return false
        }
    }

    /***
     * get Item Field Type By sublist/field Id
     * @param sublistId
     * @param fieldId
     * @return Item Field Type
     */
    String getItemFieldType(String sublistId, String fieldId) {
        if (sublistId == "deposit") {
            return this.executeScript("return (nlapiGetLineItemField('" + sublistId + "','" + fieldId + "',1).getType());");
        } else {
            return this.executeScript("return (nlapiGetLineItemField('" + sublistId + "','" + fieldId + "',0).getType());");
        }
    }

    def clickSaveByAPI() {
        Thread.sleep(2000)
        this.executeScript("NLInvokeButton(getButton('submitter'))")
        Thread.sleep(5000)
    }

    def clickVoidByAPI() {
        this.executeScript("NLInvokeButton(getButton('void'))")
    }

    def getRecordIdbyAPI() {
        return this.executeScript("return nlapiGetRecordId()")
    }

    @ReportMe
    public void navigateToNoWait(String url) {
        if (url.startsWith("/")) {
            url = this.getRelativeBaseUrl() + url
        } else if (!url.startsWith("http") && !url.startsWith("www")) {
            if (url.startsWith("file") && url.startsWith("/")) {
                if (!url.startsWith("file")) {
                    url = "file://" + url;
                }
            } else {
                url = "file://" + TestProperties.getAbsolutePath(url);
            }
        }
        try {
            Field f = this.webDriver.getClass().getDeclaredField("browser")
            f.setAccessible(true)
            org.openqa.selenium.WebDriver seleniumDriver = (org.openqa.selenium.WebDriver) f.get(this.webDriver)
            seleniumDriver.get(url)
            //suppress alert is exists
            Method method = this.webDriver.getClass().getDeclaredMethod("suppressAlerts")
            method.setAccessible(true)
            method.invoke(this.webDriver)
            log.info("Browser url set to: " + url);
        } catch (RuntimeException var3) {
            if (var3 instanceof TestCaseExecutionException) {
                ((TestCaseExecutionException) var3).setAsFatal(true);
                throw var3;
            } else {
                throw new SystemException("Could not navigate to '" + url + "'", var3, true);
            }
        }
    }

/*    private String getRelativeBaseUrl() {

        String baseUrl;
        if (this.webDriver.isBrowserClosed()) {
            baseUrl = this.getBaseUrl();
        } else {
            String pageUrl = this.webDriver.getPageUrl();
            String[] pageUrlPart = pageUrl.split("/");
            Set<String> parts = ImmutableSet.copyOf(pageUrlPart);
            if (parts.contains("home.shtml") && parts.contains("portal")) {
                baseUrl = "https://" + pageUrlPart[2];
            } else {
                baseUrl = pageUrlPart[0] + "//" + pageUrlPart[2];
            }
        }

        return baseUrl;
    }*/
}
