package com.netsuite.chinalocalization.common

import com.google.inject.Inject
import com.netsuite.base.lib.login.LoginUtil
import com.netsuite.base.lib.os.OSUtil
import com.netsuite.base.lib.NConfig
import com.netsuite.base.report.CollectionWatcher
import com.netsuite.base.report.InjectClass
import com.netsuite.chinalocalization.lib.NCurrentRecord
import com.netsuite.chinalocalization.lib.NFormat
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.aut.system.NSCredentials
import com.netsuite.testautomation.enums.Page
import com.netsuite.testautomation.exceptions.NSAppException
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser
import com.netsuite.testautomation.junit.NetSuiteAppTest
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import com.netsuite.base.lib.property.PropertyUtil
import org.apache.commons.exec.OS
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.callsite.CallSite
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher


/**
 * @desc Base test suite implementation. Check with Jianwei before you make any change to this file.
 * @author Jianwei Liu
 *
 * @updater Stephen
 * @update on 2/8/2018
 *
 * All test should inherit from this base test class.
 * The recommend inheritance relationship: <Your Test Class> -> FunctionBaseTestSuite(Optional) -> BaseAppTestSuite -> NetSuiteAppTest
 * 1). <Your Test Class> refer to the specified test class.
 * 2). FunctionBaseTestSuite refer to some functions for instance 'VAT','Voucher' etc. If there's no common methods for this function suite. It's not required.
 * 3). BaseAppTestSuite encapsulate test common things here.
 * 4). NetSuiteAppTest comes from Core.
 */
class BaseAppTestSuite extends NetSuiteAppTest {
    private static Logger log = Logger.getLogger(BaseAppTestSuite.class)
    def SEP = File.separator
    @Inject
    NetSuiteAppCN context
    @Inject
    NRecord record
    @Inject
    NConfig nconfig
    @Inject
    NCurrentRecord currentRecord
    @Inject
    NFormat format
    @Inject
    OSUtil osUtil
    @Inject
    LoginUtil loginUtil
    @Rule
    public TestWatcher testWatcher = new CollectionWatcher(InjectClass.getInstance(PropertyUtil).getReportExcelPath().replace("/", File.separator))


    def static data

    def getData() {
        return data
    }

    def preCondition() {
        // TODO is there a mechanism to do some prerequisite check
    }

    //Get data from json file
    @SuiteSetup
    void setUpTestSuite() {
        login()
        prepareData()
    }


    def shouldLoginCondition() {
        if (context.webDriver != null) {
            return context.webDriver.getPageUrl() == null || context.webDriver.getPageUrl().contains("login")
        } else {
            return true
        }
    }


    @Before
    void setUp() {
        try {
            def homeUrl = Page.HOME.getUrl()
            context.navigateTo(homeUrl)
        } catch (Exception ex) {
        }
        if (context.isCloseBrowserOnTeardown() || shouldLoginCondition()) {
            login()
        }
    }

    @After
    //Update by christina 2018/07/02
    void tearDown() {
    }

    @SuiteTeardown
    void tearDownTestSuite() {
        def doesCloseBrowser = context.getContext().getProperty("testautomation.selenium.close_browser_on_suite_end")
        boolean isCloseBrowser = Boolean.parseBoolean(doesCloseBrowser)

        if (isCloseBrowser == true) {
            context.webDriver.closeBrowser()
        }
    }

    public String getRolePathPrefix() {
        return "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\"
    }

    /**
     * Encapsulate Login and Change Role logic in the base test class
     * */

    def login() {
        loginUtil.setRolePathPrefix(getRolePathPrefix())
        record.setErrorPathPrefix(getRolePathPrefix())
        loginUtil.loginAsRole(getDefaultRole())
    }

    boolean loginAsRole(role) {
        String userName = context.getContext().getProperty("testautomation.nsapp.default.user")
        String pwd = context.getContext().getProperty("testautomation.nsapp.default.password")
        NSCredentials userLogin = new NSCredentials([user: userName, password: pwd])
        boolean loginSuccess = ((NetSuiteAppCN) context).logInViaForm(userLogin)
        if (!loginSuccess)
            return loginSuccess
        loginSuccess = context.answerSecurityQuestion(["What was your childhood nickname?"                    : "nickname",
                                                       "In what city or town did your mother and father meet?": "town",
                                                       "In what city or town was your first job?"             : "city"])
        if (!loginSuccess)
            return loginSuccess

        if (role != null) {
            loginSuccess = switchToRole(role)
        }
        changeToTargetLanguage()

        if (!context.getPageTitle().startsWith("Home")) {
            context.navigateTo("/app/center/card.nl?sc=-29")
        }
        return loginSuccess
    }


    def getCurrentEnRole(role) {

        Map<String, String> cnRoles = getRoles().zhCN
        for (Map.Entry<String, String> entry in cnRoles) {
            if (role.equals(entry.getValue())) {
                return getRoles().enUS[entry.getKey()]
            }
        }
        return null
    }

    def switchToRole(role) {
        loginUtil.setRolePathPrefix(getRolePathPrefix())
        if (role != getAccountant()) {
            return loginUtil.switchToRole(role)
        } else {
            return true
        }
    }

    def roles = [:]
    def administrator
    def accountant
    def bookkeeper


    def getRoles() {
        if (roles?.isEmpty()) {
            roles = [
                    zhCN: context.asJSON(path: "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\roles_zh_CN.json".replace("\\", SEP)),
                    enUS: context.asJSON(path: "src\\test\\java\\com\\netsuite\\chinalocalization\\common\\data\\roles_en_US.json".replace("\\", SEP))
            ]
        }
        return roles
    }

    def getDefaultRole() {
        println("Get DefaultRole")
        getAdministrator()
    }

    def getAdministrator() {
        if (!administrator) {
            administrator = isTargetLanguageChinese() ? getRoles().zhCN.Administrator : getRoles().enUS.Administrator
        }
        return administrator
    }

    def getAccountant() {
        //if (!accountant) {
            accountant = isTargetLanguageChinese() ? getRoles().zhCN.ChinaAccountant : getRoles().enUS.ChinaAccountant
        //}
        println("Get China accountant")
        return accountant
    }

    def getBookkeeper() {
        if (!bookkeeper) {
            bookkeeper = isTargetLanguageChinese() ? getRoles().zhCN.Bookkeeper : getRoles().enUS.Bookkeeper
        }
        println("Get Bookkeeper")
        return bookkeeper
    }

    def getBaseRole() {
        //if (!accountant) {
        accountant = isTargetLanguageChinese() ? getRoles().zhCN.BaseRole : getRoles().enUS.BaseRole
        //}
        return accountant
    }

    def getNoChianRole() {
        //if (!accountant) {
        accountant = isTargetLanguageChinese() ? getRoles().zhCN.NoChina : getRoles().enUS.NoChina
        //}
        return accountant
    }

    def getAccountantNoPeriodRole() {
        //if (!accountant) {
        accountant = isTargetLanguageChinese() ? getRoles().zhCN.ChinaAccountantNoPeriod : getRoles().enUS.ChinaAccountantNoPeriod
        //}
        return accountant
    }

    def targetLanguage() {
        return context.getContext().getProperty("testautomation.nsapp.language")
    }

    def currentLanguage() {
        return context.getUserLanguage()
    }

    def isTargetLanguageEnglish() {
        return isLanguageEnglish(targetLanguage())
    }

    def isTargetLanguageChinese() {
        return isLanguageChinese(targetLanguage())
    }

    def isCurrentLanguageEnglish() {
        return isLanguageEnglish(currentLanguage())
    }

    def isCurrentLanguageChinese() {
        return isLanguageChinese(currentLanguage())
    }

    private static isLanguageEnglish(language) {
        return "en_US".equals(language)
    }

    private static isLanguageChinese(language) {
        return "zh_CN".equals(language)
    }

    def changeLanguageToEnglish() {
        if (!isCurrentLanguageEnglish()) {
            context.setDefaultLanguageToEnglish()
        }
    }

    def changeLanguageToChinese() {
        if (!isCurrentLanguageChinese()) {
            context.setDefaultLanguageToChinese()
        }
    }

    def changeToTargetLanguage() {
        if (!currentLanguage().equalsIgnoreCase(targetLanguage())) {
            if (isTargetLanguageEnglish()) {
                changeLanguageToEnglish()
            } else if (isTargetLanguageChinese()) {
                changeLanguageToChinese()
            }
        }
    }


    def resolveSuiteletURL(scriptId, deploymentId) {
        return context.executeScript("return nlapiResolveURL('SUITELET', '" + scriptId + "', '" + deploymentId + "');")
    }

    def resolveRestletURL(scriptId, deploymentId) {
        return context.executeScript("return nlapiResolveURL('RESTLET', '" + scriptId + "', '" + deploymentId + "');")
    }

    def pathToTestDataFiles() {}

    def prepareData() {
        println("in base ")
        def path = pathToTestDataFiles()

        if (path) {
            if (path.size() > 1) {
                if (isTargetLanguageChinese()) {
                    if (doesFileExist(path.zhCN)) {
                        data = context.asJSON(path: path.zhCN)
                    }
                } else {
                    if (doesFileExist(path.enUS)) {
                        data = context.asJSON(path: path.enUS)
                    }
                }
            } else if (path.size == 1) {
                data = context.asJSON(path: path.get(0))
            }
        }
    }

    def parseTestData(data) {
        if (data instanceof Map && data.size() == 2 && data.containsKey("zhCN") && data.containsKey("enUS")) {
            if (targetLanguage().equalsIgnoreCase("zh_CN")) {
                return data.get("zhCN")
            } else if (targetLanguage().equalsIgnoreCase("en_US")) {
                return data.get("enUS")
            }
        }
        return data
    }

    /**
     * Below are some general methods for Page/Page Elements.
     * Later these kind of method should be encapsulated into class 'PageBase'
     * Reserve these just to keep backwards compatible
     * */


    def asElement(expression) {
        return context.withinHtmlElementIdentifiedBy(Locator.xpath(expression))
    }

    def asElements(expression) {
        return context.webDriver.getHtmlElementsByLocator(Locator.xpath(expression))
    }

    def asText(expression) {
        return asElement(expression).getAsText() // see context.getTextIdentifiedBy
    }

    def asFieldText(fieldId) {
        return context.getFieldText(fieldId)
    }

    def asLabel(fieldId) {
        return context.getFieldLabel(fieldId)
    }

    def asClick(expression) {
        asElement(expression).click()
    }

    def asDropdownList(options) {
        if (options.fieldId) {
            return context.withinDropdownlist(options.fieldId)
        } else if (options.locator) {
            return new DropdownList(context, context.webDriver, Locator.xpath(options.locator))
        }
    }

    def asElementByXPath(expression) {
        return context.webDriver.getHtmlElementByLocator(Locator.xpath(expression));
    }


    def asMultiSelectField(fieldId) {
        return context.withinMultiSelectField(fieldId)
    }

    def asScrollToView(expression) {
        asElement(expression).scrollToView()
    }

    def asAttributeValue(expression, attributeName) {
        asElement(expression).getAttributeValue(attributeName)
    }

    def asSublist(sublistId) {
        return context.withinEditMachine(sublistId)
    }

    def asTable() {
        return new TableParser(context.webDriver)
    }

    def waitForElementToDisappear(expression, timeout = "3 min") {
        context.waitForElementIdentifiedByToDisappear(Locator.xpath(expression), HumanReadableDuration.parse(timeout))
    }

    def waitForElement(expression) {
        context.waitForElementIdentifiedBy(Locator.xpath(expression))
    }

    def waitForPageToLoad() {
        context.waitForPageToLoad()
    }

    def rejectUpcomingConfirmationDialog() {
        context.webDriver.rejectUpcomingConfirmationDialog()
    }

    def acceptUpcomingConfirmationDialog() {
        context.webDriver.acceptUpcomingConfirmationDialog()
    }

    def ignoreUpcomingConfirmationDialog() {
        context.webDriver.ignoreUpcomingConfirmationDialog()
    }

    def doesFileExist(pathToFile) {
        return (new File(pathToFile.replace("\\", File.separator))).exists()
    }

    def waitForElementToAppear(Closure checkElementExist, timeout = 60) {
        def waitTimes = timeout.intdiv(2)
        for (i in 0..waitTimes) {
            if (checkElementExist()) {
                return true
            }
            Thread.sleep(2 * 1000)
        }
        return false
    }

    def assertNull(String message, String value) {
        assertTrue(message, !value?.trim())
    }

    def assertFileExist(String message, String pathToFile) {
        assertTrue(message, doesFileExist(pathToFile))
    }

    def assertFileExist(String message, String pathToFile, boolean doesDeleteFile) {
        assertFileExist(message, pathToFile)
        if (doesDeleteFile) {
            new File(pathToFile).delete()
        }
    }
}