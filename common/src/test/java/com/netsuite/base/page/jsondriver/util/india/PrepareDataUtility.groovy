package com.netsuite.base.page.jsondriver.util.india

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NetSuiteAppBase

class PrepareDataUtility {


    static void  setSublistValue(NetSuiteAppBase autBase,sublistId,values){
        if(values["subtabName"]){
            autBase.clickFormTab(values["subtabName"]);
        }
        if(values["formSubTabName"]){
            autBase.clickFormSubTab(values["formSubTabName"]);
        }
        List<String> sublistValues = (List<String>) values["values"];
        for (int i = 0; i <sublistValues.size(); i++) {

            sublistValues.get(i).each { k, v ->
                if (v != null) {
                    if (autBase.getItemFieldType(sublistId, k) == "select") {
                        autBase.withinEditMachine(sublistId).setFieldWithText(k, v);
                    } else {
                        autBase.withinEditMachine(sublistId).setFieldWithValue(k, v);
                    }
                }
            }
            if(sublistValues.size()>=1){
                autBase.withinEditMachine(sublistId).add();
            }

        }
    }



    static void  prepareData(NCurrentRecord currentRecord,NetSuiteAppBase autBase , def prepareData) {
        println(prepareData)

        prepareData.each{
            k,v ->
                println "key: "+k+"   value: "+v
        }

        if(null != prepareData){
            //input main data
            prepareData["main"].each { k, v ->
                currentRecord.setValue(k,v)
            }
            //input sublist data
            prepareData.each { k, v ->
                if (k != "main" && k != "actions") {
                    setSublistValue(autBase,k, v)
                }
            }
        }
    }

}
