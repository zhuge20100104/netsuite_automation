package com.netsuite.base.page.steps.jsteps.fieldids

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.expect.handles.FileExpectHandler
import com.netsuite.base.page.steps.jsteps.ISteps
import com.netsuite.base.page.steps.jsteps.handles.JWindowHandler
import org.apache.log4j.Logger

class MainFieldStep implements ISteps{
    private static final Logger log = Logger.getLogger(MainFieldStep.class)

    @Override
    void doStep(CheckerData checkerData) {

        SelType selType = checkerData.selType
        NetSuiteAppBase context = checkerData.context

        NCurrentRecord currentRecord = checkerData.currentRecord

        String value = checkerData.value
        boolean isMainField = checkerData.isMainField

        if(selType.equals(SelType.TAB) && isMainField) {
            log.info("Start to execute: clickFormTab: params: ["+value+"]")
            context.clickFormTab(value)
            checkerData.isStepHandled = true
            return
        }

        if(selType.equals(SelType.SLEEP) && isMainField) {
            log.info("Start to execute: sleep: params: ["+value+"]")
            long secs = Integer.parseInt(value)
            Thread.sleep(secs * 1000)
            checkerData.isStepHandled = true
            return
        }


        if(selType.equals(SelType.WAIT_FOR_LOAD) && isMainField) {
            log.info("Start to execute: waitforload")
            println("wait for page to load")
            context.waitForPageToLoad()
            checkerData.isStepHandled = true
            return
        }


        if(selType.equals(SelType.SWITCH_WINDOW) && isMainField) {
            log.info("Start to execute: switchwindow: params:["+value+"]")
            JWindowHandler.switchWindow(context,value)
            checkerData.isStepHandled = true
            return
        }

        if(selType.equals(SelType.DELETE_DIR) && isMainField) {
            log.info("Start to execute: deletefile: params:["+value+"]")
            FileExpectHandler.deleteFile(context,value)
            checkerData.isStepHandled = true
            return
        }

        if(selType.equals(SelType.CHECK_REPORT_EXIST) && isMainField) {
            log.info("Start to execute: checkreportfile: params:["+value+"]")
            FileExpectHandler.checkReportFile(context,value)
            checkerData.isStepHandled = true
            return
        }

        String key = checkerData.key

        if(selType.equals(SelType.MAIN_FIELDID) && isMainField) {
            log.info("Start to execute: setmainfield: params:["+key+ "---->" +value+"]")
            currentRecord.setValue(key,value)
            checkerData.isStepHandled = true
        }

    }
}
