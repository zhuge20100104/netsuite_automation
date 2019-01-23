package com.netsuite.base.page.actions.concrete.specific

import com.google.inject.Inject
import com.netsuite.base.lib.json.JsonUtil
import com.netsuite.base.lib.property.PropertyUtil
import com.netsuite.base.page.actions.CommonAction
import com.netsuite.base.page.actions.IAction
import com.netsuite.base.page.actions.global.GlobalValueCache
import com.netsuite.base.page.actions.methods.MethodKey
import com.netsuite.base.page.actions.utils.ActionUtils
import com.netsuite.base.page.jsondriver.JPageBase
import com.netsuite.base.report.InjectClass

class ImportAction implements IAction {


    JPageBase mPage
    String actionName
    def actionData

    private Map<MethodKey,MetaMethod> mMethodMap = new HashMap<>()

    def actualActions
    String importFile
    String function
    String testDataPrefix

    @Inject
    PropertyUtil propertyUtil = InjectClass.getInstance(PropertyUtil.class)


    private static final String IMPORT_DATA_PREFIX = "import_data"
    private static final String SEP = File.separator



    ImportAction(JPageBase base,def methodMap, String testDataPrefix, String actionKey, def actionData) {
        this.actionName = actionKey
        this.actionData = actionData
        this.testDataPrefix = testDataPrefix
        this.mPage = base
        this.mMethodMap = methodMap
        this.parseAction(actionData)
    }


    def getActualActionDatas() {
        String languageStr = propertyUtil.getCurrentLanguage()
        String  importPath = testDataPrefix + IMPORT_DATA_PREFIX + SEP + importFile + "_"+languageStr+".json"
        JsonUtil jsonUtil = new JsonUtil()
        return jsonUtil.asJson(path: importPath)
    }


    def parseAction(def actionData) {
        importFile = actionData["file"]
        function = actionData["fun"]
        actualActions = getActualActionDatas()
    }


    @Override
    void execute() {
        println("In Import Action:[" + importFile+" : "+ function +"]")
        def actionSteps = actualActions[function]

        if(actionSteps){
            List<String> steps = (List<String>) actionSteps
            for (int i = 0; i < steps.size(); i++) {
                executeAction(steps.get(i))
            }
        }

    }




    protected void executeAction(def action) {

        IAction actionStep = null

        action.each{
            actionKey,actionData ->
                if(actionKey.equals("define")) {
                    actionStep = new KeyDefineAction(actionKey,actionData)
                } else if(actionKey.startsWith("obj_")) {
                    String newKey = actionKey.replace("obj_","")
                    actionStep = new ObjectAction(mMethodMap,newKey,actionData)
                } else if(ActionUtils.isObjectAction(actionData)){
                    actionStep = new ObjectAction(mMethodMap,actionKey,actionData)
                } else {
                    actionStep = new CommonAction(mMethodMap,actionKey,actionData)
                }
        }

        if(actionStep != null) {
            actionStep.execute()
        }
    }
}
