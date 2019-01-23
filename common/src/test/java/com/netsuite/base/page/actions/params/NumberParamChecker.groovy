package com.netsuite.base.page.actions.params

import com.netsuite.base.page.actions.concrete.param.ParamParser

class NumberParamChecker implements IParamChecker<Number> {


    private static final String [] NUMBER_ARR = ["int","float","long","double"]


    private boolean isNumericParamValue(Object paramValue) {
        return (paramValue instanceof Integer) ||
                (paramValue instanceof Float) ||
                (paramValue instanceof Long) ||
                (paramValue instanceof Double)
    }


    private Number getNumericReturnValue(String paramType,strParamValue) {

        switch (paramType) {
            case "int":
                return Integer.parseInt(strParamValue)
            case "float":
                return Float.parseFloat(strParamValue)
            case "double":
                return Double.parseDouble(strParamValue)
            case "long":
                return Long.parseLong(strParamValue)
        }

    }

    @Override
    void check(ParamContext<Number> context) {
        String paramType = context.paramType
        String strParam = context.strParam
        Object paramValue = ParamParser.getActualParamValue(strParam)


        //Is a numeric param type
        if(NUMBER_ARR.contains(paramType)) {

            if(isNumericParamValue(paramValue)) {
                context.retParam = paramValue
                context.isChecked = true
            }else if(paramValue instanceof String) {
                String strParamValue = paramValue.toString()

                if(strParamValue.isNumber()) {
                    context.retParam = getNumericReturnValue(paramType,strParamValue)
                    context.isChecked = true
                }
            }
        }

    }

    static void main(String[] args) {
        float a = 100.00f
        Number b = a
        println b
    }
}
