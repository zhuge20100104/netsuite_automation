package com.netsuite.base.page.actions

import com.netsuite.base.page.actions.global.GlobalValueCache
import com.netsuite.base.page.actions.handler.MethodParseHandler
import com.netsuite.base.page.actions.methods.MethodKey
import com.netsuite.base.page.actions.utils.ActionUtils
import net.qaautomation.exceptions.SystemException
import org.apache.log4j.Logger

class CommonAction implements IAction{

    private static final Logger log = Logger.getLogger(CommonAction.class)

    String actionName
    ActionParams actionParams =null
    ReturnValue returnValue=null
    def actionData
    Map<MethodKey,MetaMethod> mMethodMap
    MethodParseHandler  mParseHandler

    CommonAction(Map<MethodKey,MetaMethod> methodMap, String actionKey, def actionData) {
        this.actionName = actionKey
        this.actionData = actionData
        this.mMethodMap = methodMap
        this.parseAction(this.actionData)
        this.mParseHandler = new MethodParseHandler()
    }


    def parseAction(def actionData) {

        if(actionData.size()==0 || (actionData.size()==1 && actionData["ret"] != null)) {
            this.actionParams = new ActionParams("")
        }

        actionData.each{
            key,value ->
                //handle return values
                if(key.equals("ret")) {
                    this.returnValue = new ReturnValue(value)
                    //handle parameters
                }else {
                    this.actionParams = new ActionParams(value)
                }
        }
    }


    private String getReturnKey(String retKey) {
        return retKey.replace("#","")
    }

    @Override
    void execute() {

        log.info("Start to Execute Action: $actionName")

        Object actRetValue = null
        boolean isMethodMatch = false

        for(Map.Entry<MethodKey,MetaMethod> entry in mMethodMap) {
            MethodKey methodKey = entry.key
            MetaMethod method = entry.value
            Object invokePage = methodKey.page

            if(this.mParseHandler!=null) {
                mParseHandler.startMethod(methodKey,method,actionName,actionParams)
                boolean isNameAndParamSizeMatched = mParseHandler.getIsNameAndParamSizeMatched()
                if(isNameAndParamSizeMatched && methodKey.paramSize != 0) {
                    List<String> expectedParamTypes = methodKey.getReadableParamTypes()
                    List<String> stringParams = actionParams.params

                    for(int i=0;i<expectedParamTypes.size();i++) {
                        String paramType = expectedParamTypes.get(i)
                        String strParam = stringParams.get(i)
                        mParseHandler.startParam(paramType,strParam)
                    }
                }

                mParseHandler.endMethod(methodKey,method,actionName,actionParams)

                isMethodMatch = mParseHandler.getMethodMatchedCondition()
                if(isMethodMatch) {
                    List actParams = mParseHandler.getActualParams()

                    actRetValue = ActionUtils.invokeMethod(method,invokePage,actParams.toArray())

//                    actRetValue = method.invoke(invokePage, actParams.toArray())

                    break
                }
            }
        }

        if(isMethodMatch == true) {
            //Handle Non-null return values
            if(actRetValue!=null && returnValue != null) {
                String retKey = getReturnKey(returnValue.getValueAt(0))
                GlobalValueCache.putValue(retKey,actRetValue)
            }
            //No matched method found, throw an exception to notify users
        }else {
            throw new SystemException("No matched method found:[$actionName],please check your method and parameters and try again!!")
        }
    }

}
