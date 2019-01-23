package com.netsuite.base.page.steps.expect.fieldids

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.expect.IExpect
import com.netsuite.base.page.steps.expect.handles.DialogExpectHandler
import com.netsuite.base.page.steps.expect.handles.FileExpectHandler
import com.netsuite.base.page.steps.expect.handles.MainFieldExpectHandler
import com.netsuite.base.page.steps.expect.handles.MenuExpectHandler
import com.netsuite.base.page.steps.jsteps.handles.JWindowHandler
import org.apache.log4j.Logger

class MainFieldExpect implements IExpect {

    private static final Logger log = Logger.getLogger(MainFieldExpect.class)

    @Override
    void doExpect(CheckerData checkerData) {

        SelType selType = checkerData.selType
        NetSuiteAppBase context = checkerData.context

        NCurrentRecord currentRecord = checkerData.currentRecord

        def value = checkerData.value
        boolean isMainField = checkerData.isMainField

        if(selType.equals(SelType.TAB) && isMainField) {
            log.info("Start to execute: clickFormTab: params: ["+value+"]")
            context.clickFormTab(value)
            checkerData.isExpected = true
            return
        }

        if(selType.equals(SelType.SLEEP) && isMainField) {
            log.info("Start to execute: sleep : params: ["+value+"]")
            long secs = Integer.parseInt(value)
            Thread.sleep(secs * 1000)
            checkerData.isExpected = true
            return
        }


        if(selType.equals(SelType.WAIT_FOR_LOAD) && isMainField) {
            log.info("Start to execute: waitforload ")
            context.waitForPageToLoad()
            checkerData.isExpected = true
            return
        }



        if(selType.equals(SelType.SWITCH_WINDOW) && isMainField) {
            log.info("Start to execute: switchwindow : params: ["+value+"]")
            JWindowHandler.switchWindow(context,value)
            checkerData.isExpected = true
            return
        }

        if(selType.equals(SelType.DELETE_DIR) && isMainField) {
            log.info("Start to execute: deletefile : params: ["+value+"]")
            FileExpectHandler.deleteFile(context,value)
            checkerData.isExpected = true
            return
        }

        if(selType.equals(SelType.CHECK_REPORT_EXIST) && isMainField) {
            log.info("Start to execute: checkreportfile : params: ["+value+"]")
            FileExpectHandler.checkReportFile(context,value)
            checkerData.isExpected = true
            return
        }


        if(selType.equals(SelType.MENUS) && isMainField) {
            log.info("Start to execute: menus : params: ["+value+"]")
            String key = checkerData.key
            MenuExpectHandler.handleMenusExp(context,key,value)
            checkerData.isExpected = true
            return
        }

        if(selType.equals(SelType.DIALOG) && isMainField) {
            log.info("Start to execute: dialogs : params: ["+value+"]")
            String key = checkerData.key
            DialogExpectHandler.handleDialogExp(context,key,value)
            checkerData.isExpected = true
            return
        }

        if(selType.equals(SelType.MAIN_FIELDID) && isMainField) {
            String key = checkerData.key
            log.info("Start to execute: expectMainField : params: ["+key+"---->"+value+"]")
            MainFieldExpectHandler.handleMainFieldExp(context,key,value)
            checkerData.isExpected = true
        }








    }
}
