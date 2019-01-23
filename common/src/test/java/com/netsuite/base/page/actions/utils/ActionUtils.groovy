package com.netsuite.base.page.actions.utils

import com.netsuite.base.lib.property.PropertyUtil
import com.netsuite.base.page.actions.global.GlobalValueCache
import com.netsuite.base.report.InjectClass

class ActionUtils {

    static def isDataObject(def data) {
        if(data instanceof Map || data instanceof List) {
            return true
        }

        return false
    }


    static def isGlobalDataObject(String value) {
        if(!value.contains(":") && value.startsWith("#")){
            String globalKey = value.replace("#","")
            def globalValue = GlobalValueCache.getValue(globalKey)
            return isDataObject(globalValue)
        }

        return false
    }

    static def isObjectAction(def actionData) {
        def isObjectAct = false

        for(Map.Entry<String,Object> entry: actionData) {
            String key = entry.getKey()
            def value = entry.getValue()
            if(!key.equals("ret")){
                if(isDataObject(value)) {
                    isObjectAct = true
                    break
                }else if(isGlobalDataObject(value)){
                    isObjectAct = true
                    break
                }
            }
        }

        return isObjectAct
    }


    static boolean isEnableReflectionError() {
        if (GlobalValueCache.getValue("isEnableReflectionError") == null) {

            PropertyUtil propertyUtil = InjectClass.getInstance(PropertyUtil.class)
            def isEnableReflectionError = propertyUtil.isEnableReflectionError()
            isEnableReflectionError = (isEnableReflectionError == null) ? false : Boolean.parseBoolean(isEnableReflectionError)
            GlobalValueCache.putValue("isEnableReflectionError", isEnableReflectionError)
        }

        return GlobalValueCache.getValue("isEnableReflectionError")
    }



    static Object invokeMethodIfEnableReflection(MetaMethod metaMethod, def invokePage, def actionParams) {


        Object  actRetValue = null
        boolean isEnableReflectionError = isEnableReflectionError()


        //If enable reflection error, invoke and throw reflection error
        if(isEnableReflectionError) {
            try {
                actRetValue = metaMethod.invoke(invokePage, actionParams)
                return actRetValue
            } catch (Exception ex) {
                throw new MethodInvokeException(metaMethod, invokePage, ex)
            }

            //If not enable reflection error, just invoke, not throw reflection error
        }else {
            actRetValue = metaMethod.invoke(invokePage, actionParams)
            return actRetValue
        }

    }


    static Object invokeMethod(MetaMethod metaMethod, def invokePage, def actionParams) {

        Object  actRetValue = null
        try {
            actRetValue = metaMethod.invoke(invokePage, actionParams)
            return actRetValue
        } catch (Exception ex) {
            throw new MethodInvokeException(metaMethod, invokePage, ex)
        }

    }
}
