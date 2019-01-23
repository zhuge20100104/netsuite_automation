package com.netsuite.base.page.actions

class ReturnValue {

    private  ActionEntity retValues

    ReturnValue(String returnStr) {
        retValues = new ActionEntity(returnStr)
    }

    def getReturnValues() {
        return retValues.entities
    }

    def getValueAt(int index) {
        return retValues.entities.get(index)
    }
}
