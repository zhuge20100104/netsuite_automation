package com.netsuite.base.page.steps.jsteps.fieldids

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.expect.handles.FileExpectHandler
import com.netsuite.base.page.steps.jsteps.ISteps
import com.netsuite.base.page.steps.jsteps.handles.JWindowHandler
import org.apache.log4j.Logger

class SubFieldStep implements ISteps{

    private static final Logger log = Logger.getLogger(SubFieldStep.class)

    @Override
    void doStep(CheckerData checkerData) {
        SelType selType = checkerData.selType

        NetSuiteAppBase context = checkerData.context
        NCurrentRecord currentRecord = checkerData.currentRecord
        String value = checkerData.value
        boolean isSubField = checkerData.isSubField
        if(selType.equals(SelType.TAB) && isSubField) {
            log.info("Start to execute: clickFormTab: params: ["+value+"]")
            context.clickFormTab(value)
            checkerData.isStepHandled = true
            return
        }

        if(selType.equals(SelType.TAB1) && isSubField){
            log.info("Start to execute: clickFormSubTab: params: ["+value+"]")
            context.clickFormSubTab(value)
            checkerData.isStepHandled = true
            return
        }

        if(selType.equals(SelType.SLEEP) && isSubField) {
            log.info("Start to execute: sleep: params: ["+value+"]")
            long secs = Integer.parseInt(value)
            Thread.sleep(secs * 1000)
            checkerData.isStepHandled = true
            return
        }


        if(selType.equals(SelType.WAIT_FOR_LOAD) && isSubField) {
            log.info("Start to execute: waitforload ")
            context.waitForPageToLoad()
            checkerData.isStepHandled = true
            return
        }



        if(selType.equals(SelType.SWITCH_WINDOW) && isSubField) {
            log.info("Start to execute: switchwindow: params: ["+value+"]")
            JWindowHandler.switchWindow(context,value)
            checkerData.isStepHandled = true
            return
        }

        if(selType.equals(SelType.DELETE_DIR) && isSubField) {
            log.info("Start to execute: deletefile : params: ["+value+"]")
            FileExpectHandler.deleteFile(context,value)
            checkerData.isStepHandled = true
            return
        }

        if(selType.equals(SelType.CHECK_REPORT_EXIST) && isSubField) {
            log.info("Start to execute: checkreportfile : params: ["+value+"]")
            FileExpectHandler.checkReportFile(context,value)
            checkerData.isStepHandled = true
            return
        }



        String machineKey = checkerData.machineKey
        String key = checkerData.key

        if(selType.equals(SelType.SUB_FIELDID) && isSubField) {

            log.info("Start to execute: setSubFieldValue : params: ["+key+"------>"+value+"]")

            if(value!=null) {
                if (context.getItemFieldType(machineKey, key) == "select") {
                    context.withinEditMachine(machineKey).setFieldWithText(key, value)
                } else {
                    context.withinEditMachine(machineKey).setFieldWithValue(key, value)
                }
            }

            checkerData.isStepHandled = true
        }

    }
}
