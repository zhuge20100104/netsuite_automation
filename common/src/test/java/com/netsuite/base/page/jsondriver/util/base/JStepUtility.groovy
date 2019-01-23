package com.netsuite.base.page.jsondriver.util.base

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.checkers.IChecker
import com.netsuite.base.page.steps.checkers.fieldids.SubFieldChecker
import com.netsuite.base.page.steps.jsteps.ClassNameStep
import com.netsuite.base.page.steps.jsteps.ISteps
import com.netsuite.base.page.steps.jsteps.IdStep
import com.netsuite.base.page.steps.jsteps.NameStep
import com.netsuite.base.page.steps.jsteps.XPathStep
import com.netsuite.base.page.steps.jsteps.fieldids.MainFieldStep
import com.netsuite.base.page.steps.jsteps.fieldids.SubFieldStep
import org.apache.commons.lang3.StringUtils

class JStepUtility {

    static List<ISteps> getAllSteps() {
        List<ISteps> steps = new ArrayList<>()
        steps.add(new MainFieldStep())
        steps.add(new SubFieldStep())
        steps.add(new IdStep())
        steps.add(new NameStep())
        steps.add(new XPathStep())
        steps.add(new ClassNameStep())
        return steps
    }

    static void doStep(CheckerData checkerData) {

        if(!checkerData.isChecked) {
            println("<----------- Non check data key,value--------------->")
            println(checkerData.key)
            println(checkerData.value)
            println("<----------- Non check data key,value--------------->")
            throw new Exception("No matched locator is found,please checker your data!!")
        }


        List<ISteps> steps = getAllSteps()
        for(ISteps step in steps){
            step.doStep(checkerData)
            if(checkerData.isStepHandled) {
                break
            }
        }


    }



    static def removeAllLinesFromSubList(NetSuiteAppBase context, NCurrentRecord currentRecord, String sublistId) {
        int lineCount = currentRecord.getLineCount(sublistId).toInteger()
        for(int i=lineCount; i>=0; i--){
            //remove lines from active line to the end of the list
            def toExecuteScript = "nlapiRemoveLineItem('${sublistId}','${i}');"
            context.executeScript(toExecuteScript)
        }

        def toExecuteScript = "nlapiSelectNewLineItem('${sublistId}');"
        context.executeScript(toExecuteScript)
    }



    static def actions(NRecord record, NCurrentRecord currentRecord, NetSuiteAppBase autBase, def data) {
        data.each {
            mainKey,mainValue ->

                if(!mainKey.startsWith("values")) {
                    CheckerData checkerData = JCheckUtility.doCheckForData(record,currentRecord,autBase,mainKey,mainValue)
                    doStep(checkerData)
                }else{//start with Values is sublist

                    String machineKey = ""
                    mainValue.each {

                        subKey,subValue ->

                            IChecker checker = null
                            CheckerData checkerData = new CheckerData()
                            checkerData.context = autBase
                            checkerData.currentRecord = currentRecord
                            checkerData.record = record

                            checkerData.isChecked = false
                            checkerData.isStepHandled = false
                            checkerData.isMainField = false
                            checkerData.isSubField = false

                            if(subKey.equals("id")) {
                                machineKey = subValue
                            }else if(subKey.startsWith("TAB")) {
                                checkerData.key = subKey
                                checkerData.value = subValue
                                checker = new  SubFieldChecker()
                                checker.doChecker(checkerData)
                                doStep(checkerData)
                            } else if(subKey.equals("values")) {

                                if(!StringUtils.isEmpty(machineKey)) {
                                    removeAllLinesFromSubList(autBase, currentRecord, machineKey)
                                }

                                subValue.each{
                                    singleValue ->
                                        singleValue.each {
                                            subListKey,subListValue ->
                                                checkerData.machineKey = machineKey
                                                checkerData.isChecked = false
                                                checkerData.isStepHandled = false

                                                checkerData.isMainField = false
                                                checkerData.isSubField = false
                                                checkerData = JCheckUtility.doCheckForSubData(checker,checkerData,subListKey,subListValue)
                                                doStep(checkerData)
                                        }



                                        if(!StringUtils.isEmpty(machineKey)) {
                                            autBase.withinEditMachine(machineKey).add()
                                            currentRecord.selectNewLine(machineKey)
                                        }
                                }

                            }


                    }
                }



        }

    }


}
