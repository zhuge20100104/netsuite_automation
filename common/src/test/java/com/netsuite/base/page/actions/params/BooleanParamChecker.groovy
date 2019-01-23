package com.netsuite.base.page.actions.params

import com.netsuite.base.page.actions.concrete.param.ParamParser

class BooleanParamChecker implements IParamChecker<Boolean> {

    private static final String [] BOOLEAN_ARR = ["true","false"]
    @Override
    void check(ParamContext<Boolean> context) {
        String paramType = context.paramType
        String strParam = context.strParam
        Object paramValue = ParamParser.getActualParamValue(strParam)

        if(paramType.equals("boolean")) {
            //Handle returned boolean params by previous functions
            if(paramValue instanceof Boolean) {
                context.retParam = paramValue
                context.isChecked = true
            }else if(paramValue instanceof String && BOOLEAN_ARR.contains(paramValue)) {
                context.retParam = Boolean.parseBoolean(paramValue)
                context.isChecked = true
            }
        }
    }

    static void main(String[] args) {
        println BOOLEAN_ARR.contains("fa")
    }
}
