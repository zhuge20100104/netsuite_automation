package com.netsuite.base.page.actions.params

import com.netsuite.base.page.actions.concrete.param.ParamParser

class ObjectParamChecker implements IParamChecker<Object> {

    @Override
    void check(ParamContext<Object> context) {
        String paramType = context.paramType
        String strParam = context.strParam

        if(paramType.equals("object")) {
            // Not a returned value by previous functions, can't pass object directly
            if(strParam.startsWith("#")){
                Object paramValue = ParamParser.getActualParamValue(strParam)
                context.retParam = paramValue
                context.isChecked = true
            }
        }
    }
}
