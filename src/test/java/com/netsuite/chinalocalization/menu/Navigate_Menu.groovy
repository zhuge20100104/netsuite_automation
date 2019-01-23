package com.netsuite.chinalocalization.menu

import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.extendreport.ExtendReportAppTestSuite
import com.netsuite.common.OW
import com.netsuite.common.OWAndSI
import com.netsuite.common.P1
import com.netsuite.testautomation.driver.SeleniumHtmlElementHandle
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestName

@TestOwner("christina.chen@oracle.com")
class Navigate_Menu extends ExtendReportAppTestSuite{
    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\menu\\data\\Navigate_Menu_Test_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\menu\\data\\Navigate_Menu_Test_en_US.json"
        ]
    }
    @Rule
    public  TestName name = new TestName()
    def caseData
    def caseFilter
    def expResult
    def initData(){

        caseData = testData.get(name.getMethodName())
        //caseFilter = caseData.filter
        //if ("expectedResult" in caseData) expResult= caseData.expectedResult
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * Check account show in order, start with account number - if set
     * won't show account's parent
     * @case 8.1
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_8_1() {

       // .//ul[@id="ns-header-menu-main"]/li[@data-title="报表"]/ul/li[@data-title="财务"/ul/li[@data-title="中国科目余额表"]
        initData()
        def mainMenu
        def curMenu, curXpath

        def mainXpath
        String menuDashBoardIcon = ".//*[@id='ns-header-menu-home']//a[@aria-label='${testData.label}']"

        caseData.each{key,value->
            print( key+":"+value)
            if (key != testData.defaultRole ){switchToRole(key) }//
            context.webDriver.reloadBrowser()
            waitForPageToLoad()
            value.each{ item->
                context.webDriver.reloadBrowser()
                waitForPageToLoad()
                println item
                mainXpath= ".//ul[@id='ns-header-menu-main']/li[@data-title='${item[0]}']"
                mainMenu = asElement(mainXpath)
                assertAreNotEqual("Could find menu ${item[0]}", null, mainMenu)
                mainMenu.mouseOver()
                sleep(2 * 1000)
                for( int i=1; i<item.size();i++){
                    if (item[i] in List){
                        item[i].each{it->
                            curXpath=mainXpath + "/ul/li[@data-title='${it}']"
                            curMenu = getCurrentMenu(curXpath,mainXpath)
                            assertAreNotEqual("Could find menu ${it}", null, curMenu)
                            //curMenu.mouseOver()
                            //sleep(2 * 1000)
                        }
                    }
                    else{
                        curXpath=mainXpath + "/ul/li[@data-title='${item[i]}']"
                        curMenu =getCurrentMenu(curXpath,mainXpath)
                        assertAreNotEqual("Could find menu ${item[i]}", null, curMenu)
                        mainXpath= curXpath
                        curMenu.mouseOver()
                        sleep(2 * 1000)
                    }
                }
                /*
                item.each{ it->
                    if (mainXpath){
                        curXpath=mainXpath + "/ul/li[@data-title='${it}']"
                        curMenu = asElement(curXpath)
                        //handle could not find Menu
                        while(!curMenu){
                            tmpMenu = asElement(mainXpath + "/span[@class=\"ns-submenu-triangle\"]")
                            if(tmpMenu) { tmpMenu.click()}
                            curMenu = asElement(curXpath)
                            if(!curMenu) {
                                tmpMenu = asElement(mainXpath + "/ul/li[@class='ns-scroll-button-li']/a[@class='ns-scroll-button ns-scroll-button-bottom']")
                                if (tmpMenu) {
                                    tmpMenu.click()
                                }
                                curMenu = asElement(curXpath)
                            }
                        }

                        assertAreNotEqual("Could find menu ${it}", null, curMenu)
                        if(curMenu){
                            mainMenu.mouseOver()
                            sleep(2 * 1000)
                        }
                        mainXpath +="/ul/li[@data-title='${it}']"
                    }
                    if(!mainXpath){
                        mainXpath= ".//ul[@id='ns-header-menu-main']/li[@data-title='${it}']"
                        mainMenu = asElement(mainXpath)
                        checkAreNotEqual("Could find menu ${it}", null, mainMenu)
                        mainMenu.mouseOver()
                        sleep(2 * 1000)
                    }
                    println mainXpath
                }
                */
            }
        }

        //Alert alert = AlertHandler.waitForAlertToBePresent(com.netsuite.testautomation.driver.SeleniumDrive)
        //alert.accept()

    }

     def getCurrentMenu(curXpath,mainXpath){
         print curXpath

        def curMenu = asElement(curXpath)
        def tmpMenu = asElement(mainXpath + "/span[@class=\"ns-submenu-triangle\"]")
        //handle could not find Menu
        int i =0
        while(!curMenu&& i<15){

            if(tmpMenu) {
                tmpMenu.click()
                curMenu = asElement(curXpath)
            }
            if(!curMenu) {
                tmpMenu = asElement(mainXpath + "/ul/li[@class='ns-scroll-button-li']/a[@class='ns-scroll-button ns-scroll-button-bottom']")
                if (tmpMenu) {
                    tmpMenu.click()
                    curMenu = asElement(curXpath)
                }
            }
            i++
        }
        return curMenu

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     * Check account show in order, start with account number - if set
     * show account's parent  separate with :
     * @case 4.1.2
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_1_2() {
        setShowAccountNum(true)
        setUseLastSubAccount(false)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        clickRefresh()
        //Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver)
        //alert.accept()
                //switchTo().alert().accept()

    }
    /* @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: false
     * Check account show in order, start without account number
     * won't show account's parent
     * @case 4.1.3
     * @author Christina Chen
     */
    @Category([P1.class,OW.class])
    //@Test
    void test_case_4_1_3() {
        setShowAccountNum(false)
        setUseLastSubAccount(true)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        //context.webDriver.acceptUpcomingConfirmationDialog()
        //Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver)
        //alert.accept()
        clickRefresh()
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: false
     * Check account show in order, start without account number
     * show account's parent separated with :
     * @case 4.1.4
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    //@Test
    void test_case_4_1_4() {

        setShowAccountNum(false)
        setUseLastSubAccount(false)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        //Alert alert = alertHandler.waitForAlertToBePresent(context.webDriver)
        //alert.accept()
        clickRefresh()
    }
    /**
     * @desc Check header and contexts after press refresh button
     * @case 2.2.2*
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_2_2_2() {
        setShowAccountNum(true)
        setUseLastSubAccount(true)

        navigateToPortalPage()
        waitForPageToLoad()
        initData()
        setSearchParams(caseFilter)
        clickRefresh()
        //println("test1 ${asAttributeValue(extendReportPage.XPATH_BTN_REFRESH, "disabled")}")
        //Thread.sleep(2 * 1000)
        //assertTrue("Show loading message", context.isTextVisible(labelData.expectedResults.loadingMsg))
        //assertAreEqual("Refresh button disabled", "true", asAttributeValue(extendReportPage.XPATH_BTN_REFRESH, "disabled"))

        waitForPageToLoad()
        checkHeader()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)
    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: true
     * USE ACCOUNT NUMBERS: true
     * check after press the button refresh  the context,header and  parameter changes
     * @case 4.1.5
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_1_5() {
        setShowAccountNum(true)
        setUseLastSubAccount(true)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        clickRefresh()
        waitForPageToLoad()
        checkHeader()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)

    }
    /**
     * @desc Base on :
     * ONLY SHOW LAST SUBACCOUNT: false
     * USE ACCOUNT NUMBERS: true
     * Check after press the button refresh  the context,header and  parameter changes
     * @case 4.1.6
     * @author Christina Chen
     */
    @Category([P1.class,OWAndSI.class])
    @Test
    void test_case_4_1_6() {
        setShowAccountNum(true)
        setUseLastSubAccount(false)
        navigateToPortalPage()
        initData()
        checkVisible()
        setSearchParams(caseFilter)
        assertSortAccout(expResult.account)
        clickRefresh()
        waitForPageToLoad()
        checkHeader()
        checkParamsSetting(caseFilter)
        checkContexts(expResult.data)
    }
}
