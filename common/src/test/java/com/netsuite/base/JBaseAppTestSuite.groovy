package com.netsuite.base


import com.netsuite.base.page.actions.CommonAction
import com.netsuite.base.page.actions.IAction
import com.netsuite.base.page.actions.concrete.specific.ImportAction
import com.netsuite.base.page.actions.concrete.specific.KeyDefineAction
import com.netsuite.base.page.actions.concrete.specific.ObjectAction
import com.netsuite.base.page.actions.global.GlobalValueCache
import com.netsuite.base.page.actions.methods.MethodKey
import com.netsuite.base.page.actions.utils.ActionUtils
import com.netsuite.base.page.jsondriver.JPageBase
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import org.junit.After

class JBaseAppTestSuite extends BaseAppTestSuite{



    protected JPageBase mPage

    protected Map<MethodKey,MetaMethod>  mMethodMap = new HashMap<>()
//  Need update mMethodMap
    /* key methodName
       Value {method：method,
              paramsize: 3
         }
        or value[paramsize,method]
    * */
    private void initMethodKeys(JPageBase pageBase) {
        pageBase.metaClass.methods.each { method ->
            MethodKey mKey = new MethodKey(method,pageBase)
            mMethodMap.put(mKey,method)
        }
    }



    public void setExtendPage(JPageBase pageBase) {
        this.mPage = pageBase
    }

    void executeCase(caseData) {

        GlobalValueCache.clearCache()
        initMethodKeys(mPage)

        if(caseData["recordType"]!=null) {
            this.mPage.setRecordType(caseData["recordType"])
        }

        if(null != caseData["steps"])   {
            def stepsData=caseData["steps"]

            stepsData.each{  entry ->
                executeAction(entry)
            }
        }
    }




    protected void executeAction(def action) {
        IAction actionStep = null

        action.each{
            actionKey,actionData ->
                if(actionKey.equals("import")) {
                    actionStep = new ImportAction(mPage,mMethodMap,getTestDataPrefix(),actionKey,actionData)
                }else if(actionKey.equals("define")) {
                    actionStep = new KeyDefineAction(actionKey,actionData)
                } else if(actionKey.startsWith("obj_")) {
                    String newKey = actionKey.replace("obj_","")
                    actionStep = new ObjectAction(mMethodMap,newKey,actionData)
                }else if(ActionUtils.isObjectAction(actionData)){
                    actionStep = new ObjectAction(mMethodMap,actionKey,actionData)
                } else {
                    actionStep = new CommonAction(mMethodMap,actionKey,actionData)
                }
        }

        if(actionStep != null) {
            actionStep.execute()    
        }
    }



    @SuiteTeardown
    void baseTearDownTestSuite() {
        println "In Suite teardown---->"
        boolean closeBrowserOnSuiteEnd = propertyUtil.isCloseBrowserOnSuiteEnd()
        if(closeBrowserOnSuiteEnd) {
            context.webDriver.closeBrowser()
        }
    }

    @After
    void tearDown() {
        println("tearDown: cleaning up dirty data...")

        def dirtyData = GlobalValueCache.getValue("dirtyData")

        if (dirtyData) {
            if(!getDefaultRole().equals(loginUtil.getAdministrator())) {
                loginUtil.switchToRole(loginUtil.administrator)
            }

            backEndRecordUtil.cleanupDirtyData(dirtyData,cleanAll)
        }

        boolean isCloseBrowserOnTeardown = propertyUtil.isCloseBrowserOnTeardown()
        if(isCloseBrowserOnTeardown) {
            context.webDriver.closeBrowser()
        }

        context.switchToHome()
    }
}
