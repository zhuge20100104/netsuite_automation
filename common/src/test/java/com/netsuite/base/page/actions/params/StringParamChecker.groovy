package com.netsuite.base.page.actions.params

import com.netsuite.base.page.actions.concrete.param.ParamParser

class StringParamChecker implements IParamChecker<String> {

    @Override
    void check(ParamContext<String> context) {
        String paramType = context.paramType
        String strParam = context.strParam

        if(paramType.equals("string"))  {
            Object paramValue = ParamParser.getActualParamValue(strParam)
            context.retParam = paramValue.toString()
            context.isChecked = true
        }
    }
}
