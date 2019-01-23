package com.netsuite.base

import com.google.inject.Inject
import com.netsuite.base.beans.CaseData
import com.netsuite.base.excel.ExcelUtil
import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.lib.login.LoginUtil
import com.netsuite.base.lib.os.OSUtil
import com.netsuite.base.lib.property.PropertyUtil
import com.netsuite.base.lib.record.BackEndRecordUtil
import com.netsuite.base.lib.url.URLUtil
import com.netsuite.base.pdf.PdfUtil
import com.netsuite.testautomation.junit.NetSuiteAppTest
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before

import javax.validation.groups.Default

class BaseAppTestSuite extends NetSuiteAppTest {
    private static Logger log = Logger.getLogger(BaseAppTestSuite.class)
    def SEP = File.separator



    @Inject
    NetSuiteAppBase context
    @Inject
    PropertyUtil propertyUtil
    @Inject
    LoginUtil loginUtil
    @Inject
    BackEndRecordUtil backEndRecordUtil
    @Inject
    URLUtil urlUtil
    @Inject
    NRecord record

    @Inject
    PdfUtil pdfUtil

    @Inject
    ExcelUtil excelUtil

    @Inject
    NCurrentRecord currentRecord

    @Inject
    OSUtil osUtil

    CaseData cData

    def cleanAll

    def dirtyData
    def miniRole


    def  getRolePathPrefix() {
        return propertyUtil.getRolePathPrefix()
    }



    def getTestDataPrefix() {
        return ""
    }


    def getDefaultRole() {
        if(miniRole) return loginUtil.getRole(miniRole)
        return loginUtil.getAdministrator()
    }



    def pathToTestDataFile() {
        String language = propertyUtil.getCurrentLanguage()
        def testDataFileName = getTestDataPrefix() + this.getClass().getSimpleName()+"_"+language+".json"
        return testDataFileName
    }


    @SuiteSetup
    void setUpTestSuite() {
        login()
    }


    @SuiteTeardown
    void baseTearDownTestSuite() {
        println "In Suite teardown---->"
//        osUtil.closeBrowser()
    }


    @Before
    void setUp() {

        boolean isCloseBrowserOnTeardown = propertyUtil.isCloseBrowserOnTeardown()

        dirtyData = null
        prepareData()

        if(context.webDriver.isBrowserClosed() || isCloseBrowserOnTeardown ) {
            login()
        }

        cleanAll = true
    }


    @After
    void tearDown() {
        println("tearDown: cleaning up dirty data...")

        if (dirtyData) {
            backEndRecordUtil.cleanupDirtyData(dirtyData,cleanAll)
        }



/*
        if(!getDefaultRole().equals(loginUtil.getAdministrator())) {
            loginUtil.switchToRole(loginUtil.administrator)
        }
*/




/*        if(!getDefaultRole().equals(loginUtil.getAdministrator())) {
            loginUtil.switchToRole(loginUtil.accountant)
        }*/
    }



    def  prepareData() {
        def path = pathToTestDataFile()

        if (!path || path.isEmpty()) {
            return
        }

        cData = new CaseData(path)
    }

    def login() {
        loginUtil.setRolePathPrefix(getRolePathPrefix())
        record.setErrorPathPrefix(getRolePathPrefix())
        def role = getDefaultRole()
        loginUtil.loginAsRole(role)
    }


    //region  assert related methods
    def assertTextVisible(String assertMsg,String errorMsg) {
        assertTrue(assertMsg,context.isTextVisible(errorMsg))
    }

    def assertTextContains(String assertMsg,String srcText,String containsText) {
        assertTrue(assertMsg,srcText.contains(containsText))
    }

    def assertTextEquals(String assertMsg,String srcText,String equalsText) {
        assertTrue(assertMsg,srcText.equals(equalsText))
    }
    //endregion assert related methods



    //region  page navigation part
    def navigateToGenerateChinaVATPage() {
//        context.navigateTo("/app/site/hosting/scriptlet.nl?script=317&deploy=1&compid=4803899&whence=")
        context.navigateTo(urlUtil.resolveSuiteletURL("customscript_sl_cn_vat", "customdeploy_sl_cn_vat"))
    }


    def navigateToRefundCashSalesPage() {
        context.navigateTo("/app/accounting/transactions/cashrfnd.nl")
    }


    //navigate to update revenue arrangement and revenue plans page
    def navigateToUpdateRevenueArrangementPage() {
        context.navigateTo("/app/accounting/bulkprocessing/managearrangementsandplansstatus.nl")
    }
    //endregion

}
