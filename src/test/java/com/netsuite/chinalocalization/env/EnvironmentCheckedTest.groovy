package com.netsuite.chinalocalization.env

import com.google.inject.Inject
import com.netsuite.chinalocalization.cashflow.CashflowBaseTest
import com.netsuite.chinalocalization.common.CONSTANTS.CURL
import com.netsuite.chinalocalization.page.Setup.AccountPreferencePage
import com.netsuite.chinalocalization.page.Setup.GeneralPreferencePage
import com.netsuite.common.*
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.validation.constraints.AssertTrue

@TestOwner("jingzhou.wang")
class EnvironmentCheckedTest extends CashflowBaseTest {

    @Inject
    protected GeneralPreferencePage generalPreferencePage
    @Inject
    protected AccountPreferencePage accountPreferencePage

    def script_create_trans = 'customscript_sl_cn_ui_create_trans'
    def deploy_create_trans = 'customdeploy_sl_cn_ui_create_trans'

    def script_delete_trans = 'customscript_sl_cn_ui_delete_trans'
    def deploy_delete_trans = 'customdeploy_sl_cn_ui_delete_trans'

    def script_cfs_collector = 'customscript_rl_cn_controller'
    def deploy_cfs_collector = 'customdeploy_rl_cn_controller'

    def getDefaultRole() {
        return getAdministrator()
    }

    @Before
    void setUp() {
        super.setUp()
    }

    @After
    void tearDown() {
        super.tearDown()
    }

    @SuiteTeardown
    void tearDownTestSuite() {
        super.tearDownTestSuite()
    }

    /**
     * @author Jingzhou.wang
     * @lastUpdateBy
     * @caseID
     * @desc
     * Check enviroment cases
     *          1. Check deployment for automation work
     *              a. customdeploy_sl_cn_ui_create_trans
     *              b. customdeploy_sl_cn_ui_delete_trans
     *              c. customdeploy_rl_cn_controller
     *          2. Set Default preference
     *              a. Drop Down List max to 500
     *              b. Show Internal Id
     *          3. UnCheck CFS Mandatory for "China BU"
     *          4. Check "Void Journal by reversing journal"
     */
    @Category([OWAndSI, P0])
    @Test
    void env_1_check_environment() {

        //1. Check deployments work
        checkCreateDeleteDeploy(script_create_trans, deploy_create_trans)
        checkCreateDeleteDeploy(script_delete_trans, deploy_delete_trans)
        checkCFSCollectDeploy(script_cfs_collector, deploy_cfs_collector)


        Assert.assertTrue("TRUE", true)

    }

    @Category([OWAndSI, P0])
    @Test
    void env_2_set_preference() {

        //2. Set Drop Down List max to 500
        generalPreferencePage.navigateToURL()
        generalPreferencePage.setMaxDropDownSize(500, false)

        //3. UnCheck CFS Mandatory for "China BU"
        disableCFSMandatory("China BU")

        //4. Check "Void Journal by reversing journal"
        accountPreferencePage.navigateToURL()
        accountPreferencePage.enableReversalVoiding()

        Assert.assertTrue("TRUE", true)

    }

    //Check create and delete trans deployment works well
    def checkCreateDeleteDeploy(String scriptId, String deployId) {
        String url = context.resolveURL(scriptId, deployId)
        // TODO, need to handle the case that suitelet does not exist
        String fullUrlLink = context.getContext().getProperty("testautomation.nsapp.default.host") + url

        //go to CreateTransaction page
        context.navigateToNoWait(fullUrlLink)
        def btn_submit = elementHandler.waitForElementToBePresent(context.webDriver, ".//*[@id='submitter']")

        checkAreNotEqual(scriptId + " is not ready", btn_submit, null)
        context.navigateToNoWait(CURL.HOME_CURL)
        elementHandler.waitForElementToBePresent(context.webDriver, "//*[@id='ns-dashboard-heading-panel']")
    }

    //Check CFS collectiong deployment works well
    def checkCFSCollectDeploy(String scriptId, String deployId) {
        String restletURL = ""
        try {
            restletURL = context.resolveRestletURL(scriptId, deployId)
        } catch (Exception e) {
            Assert.assertTrue("CFS Collect Restlet handler is not ready!!", false)
        }
        context.navigateToNoWait(CURL.HOME_CURL)
        elementHandler.waitForElementToBePresent(context.webDriver, "//*[@id='ns-dashboard-heading-panel']")
    }


}