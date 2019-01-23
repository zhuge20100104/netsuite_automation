package com.netsuite.base.page.actions.concrete.specific

import com.netsuite.base.page.actions.IAction
import com.netsuite.base.page.actions.global.GlobalValueCache

class KeyDefineAction  implements IAction {


    String actionName
    def actionData


    KeyDefineAction(String actionKey, def actionData) {
        this.actionName = actionKey
        this.actionData = actionData
    }

    private String getDefineKey(String retKey) {
        return retKey.replace("#","")
    }

    @Override
    void execute() {

        actionData.each {
            key,value ->
                String actualKey = getDefineKey(key)
                GlobalValueCache.putValue(actualKey,value)
        }
    }
}
