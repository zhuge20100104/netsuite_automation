package com.netsuite.base.page.actions

class ActionParams {


    private String paramStr

    ActionEntity paramEntity

    ActionParams(String paramStr) {
        this.paramStr = paramStr
        this.toParamEntity()
    }




    def toParamEntity() {
        paramEntity = new ActionEntity(paramStr)
    }



    def getParamStr() {
        return paramStr
    }

    def getParams() {
        return paramEntity.entities
    }

    def getParamSize() {
        return getParams().size()
    }


    def getParamAt(int index) {
        return this.paramEntity.entities.get(index)
    }

    static void main(String [] args) {
        def keyValuePair = new ActionParams("filter1:page1:true")
        println(keyValuePair.getParamStr())
        println(keyValuePair.getParams())
        println(keyValuePair.getParamAt(0))

    }
}
