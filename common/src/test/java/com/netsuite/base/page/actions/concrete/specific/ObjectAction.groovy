package com.netsuite.base.page.actions.concrete.specific

import com.netsuite.base.page.actions.IAction
import com.netsuite.base.page.actions.ReturnValue
import com.netsuite.base.page.actions.global.GlobalValueCache
import com.netsuite.base.page.actions.methods.MethodKey
import com.netsuite.base.page.actions.utils.ActionUtils
import net.qaautomation.exceptions.SystemException
import org.apache.log4j.Logger

class ObjectAction  implements IAction{

    private static final Logger log = Logger.getLogger(ObjectAction.class)

    String actionName
    def actionParams =null
    ReturnValue returnValue=null

    def actionData

    Map<MethodKey,MetaMethod> mMethodMap

    ObjectAction(Map<MethodKey,MetaMethod> methodMap, String actionKey, def actionData) {
        this.actionName = actionKey
        this.actionData = actionData
        this.mMethodMap = methodMap
        this.parseAction(this.actionData)
    }


    def parseAction(def actionData) {


        actionData.each{
            key,value ->
                //handle return values
                if(key.equals("ret")) {
                    this.returnValue = new ReturnValue(value)
                    //handle parameters
                }else {
                    this.actionParams = getActionObject(value)
                }
        }
    }


    boolean isNameAndParamSizeMatched(MethodKey methodKey) {
        return this.actionName.equals(methodKey.methodName) &&
                1 == methodKey.paramSize
    }




    private String getReturnKey(String retKey) {
        return retKey.replace("#","")
    }

    def getActionObject(value) {
        if(value instanceof String && value.startsWith("#")) {
            String globalKey = value.replace("#","")
            return GlobalValueCache.getValue(globalKey)
        }

        return value
    }

    @Override
    void execute() {
        log.info("Start to Execute Action: $actionName")

        Object actRetValue = null
        boolean isMatched = false

        mMethodMap.each{methodKey,metaMethod->
            Object invokePage = methodKey.page
            //println("Check methodName:${methodKey.methodName}")
            if(isNameAndParamSizeMatched(methodKey)) {
                String paramType = methodKey.readableParamTypes.get(0)
                if(paramType.equals("object")) {
                    isMatched = true
                    //println("start to invoke:${methodKey.methodName}, params:${actionParams}")

                    actRetValue = ActionUtils.invokeMethod(metaMethod,invokePage,actionParams)

//                    actRetValue = metaMethod.invoke(invokePage, actionParams)

//                    try {
//                        actRetValue = metaMethod.invoke(invokePage, actionParams)
//                        return true
//                    }catch(Exception ex) {
//                        println ex.getStackTrace()
//                    }


                }
            }
        }

        if(isMatched == true) {
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
