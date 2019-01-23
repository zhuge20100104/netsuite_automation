package com.netsuite.base.page.actions.utils

import net.qaautomation.exceptions.SystemException

class MethodInvokeException extends SystemException {

    MethodInvokeException(MetaMethod metaMethod,def invokePage,Exception ex) {
        super("Invoke method failed: ["+ invokePage.getClass().getName()+"."+metaMethod.getName()+"]")
        ex.printStackTrace()
    }
}
