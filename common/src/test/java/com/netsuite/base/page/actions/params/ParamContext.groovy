package com.netsuite.base.page.actions.params

class ParamContext<T> {
    String paramType
    String strParam
    T retParam = null
    boolean isChecked = false
}
