package com.netsuite.base.page.steps.checkers.fieldids

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.checkers.IChecker
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import org.apache.commons.lang3.StringUtils

class SubFieldChecker implements IChecker{

    @Override
    void doChecker(CheckerData data) {
        NetSuiteAppBase context = data.context
        NCurrentRecord currentRecord = data.currentRecord
        String key = data.key
        if(key.equals("values")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.TAB
            return
        }

        if(key.equals("TAB")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.TAB

            return
        }

        if(key.equals("TAB1")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.TAB1
            return
        }


        if(key.equals("sleep")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.SLEEP
            return
        }

        if(key.equals("waitforload")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.WAIT_FOR_LOAD
            return
        }

        if(key.equals("switchwindow")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.SWITCH_WINDOW
            return
        }

        if(key.equals("deletefile")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.DELETE_DIR
            return
        }


        if(key.equals("checkreportfile")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.CHECK_REPORT_EXIST
            return
        }

        if(key.equals("_row")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.ROW
            return
        }


        if(key.equals("_recordrow")) {
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.RECORD_ROW
            return
        }


        String machineKey = data.machineKey
        String retVal = currentRecord.getSubListField(machineKey,key)

        if(StringUtils.isEmpty(retVal)){
            retVal = currentRecord.getSublistValue(machineKey,key,1)
            if(retVal){
                data.isChecked = true
                data.isSubField = true
                data.selType = SelType.SUB_FIELDID
            }
        }else{
            data.isChecked = true
            data.isSubField = true
            data.selType = SelType.SUB_FIELDID
        }

    }
}
