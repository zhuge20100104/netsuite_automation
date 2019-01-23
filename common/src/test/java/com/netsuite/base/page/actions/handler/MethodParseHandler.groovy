package com.netsuite.base.page.actions.handler

import com.netsuite.base.page.actions.ActionParams
import com.netsuite.base.page.actions.methods.MethodKey
import com.netsuite.base.page.actions.params.BooleanParamChecker
import com.netsuite.base.page.actions.params.IParamChecker
import com.netsuite.base.page.actions.params.NumberParamChecker
import com.netsuite.base.page.actions.params.ObjectParamChecker
import com.netsuite.base.page.actions.params.ParamContext
import com.netsuite.base.page.actions.params.StringParamChecker

class MethodParseHandler {

    // Param name and size is matched
    boolean isNameAndParamSizeMatch = true
    // One of the param type matched
    boolean isParamTypeMatch = true
    // whole method has matched
    boolean isMethodChecked = false
    // Returned actual parameters
    private List retActParams = new ArrayList()



    //region Start start method methods
    private void initParseStatus() {
        this.retActParams.clear()
        this.isNameAndParamSizeMatch = true
        this.isParamTypeMatch = true
        this.isMethodChecked = false
    }


    private boolean isNameAndParamSizeMatched(MethodKey methodKey,String actionName,ActionParams actionParams) {
        return actionName.equals(methodKey.methodName) &&
                actionParams.getParamSize()==methodKey.paramSize
    }


    def startMethod(MethodKey methodKey, MetaMethod method, String actionName, ActionParams actionParams) {
        this.initParseStatus()

        if(isNameAndParamSizeMatched(methodKey,actionName,actionParams)) {
            // No Params
            if(methodKey.paramSize == 0) {
                isMethodChecked = true
            }
        }else {
            isNameAndParamSizeMatch = false
        }
    }
    //endregion End start method method




    //region Start start param methods
    private List<IParamChecker> getParamCheckList() {
        List<IParamChecker> checkerList = new ArrayList<>()
        checkerList.add(new StringParamChecker())
        checkerList.add(new BooleanParamChecker())
        checkerList.add(new NumberParamChecker())
        checkerList.add(new ObjectParamChecker())
        return checkerList
    }


    // Not need to verify the next parameters
    private boolean shouldReturnCondition() {
        // name and param size not match | void method, whole method matched | one of the param type not match
        return (isNameAndParamSizeMatch == false) || (isMethodChecked == true) || (isParamTypeMatch == false)
    }

    def startParam(String paramType,String strParam) {
        if(shouldReturnCondition()) {
            return
        }

        List<IParamChecker> paramCheckers = getParamCheckList()
        ParamContext context = new ParamContext()
        context.paramType = paramType
        context.strParam = strParam

        for(IParamChecker checker in paramCheckers) {
            checker.check(context)
            if(context.isChecked) {
                retActParams.add(context.retParam)
                break
            }
        }

        //All parameters checker not match
        if(context.isChecked == false) {
            isParamTypeMatch = false
        }
    }
    //endregion end start param method



    //region end method start
    def endMethod(MethodKey methodKey, MetaMethod method, String actionName, ActionParams actionParams) {

    }
    //endregion end method end




    //region Start outside functions
    // Determine whether the current method is matched
    public boolean getMethodMatchedCondition() {
        return (isMethodChecked || (isNameAndParamSizeMatch && isParamTypeMatch))
    }

    // Get the last actual params
    public List getActualParams() {
        return retActParams
    }

    // Get whether name and param size matched
    public boolean getIsNameAndParamSizeMatched() {
        return  this.isNameAndParamSizeMatch
    }
    //endregion end outside functions
}
