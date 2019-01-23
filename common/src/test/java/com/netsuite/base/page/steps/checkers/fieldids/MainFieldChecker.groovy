package com.netsuite.base.page.steps.checkers.fieldids

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.beans.SelType
import com.netsuite.base.page.steps.checkers.IChecker
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import org.apache.commons.lang3.StringUtils

class MainFieldChecker implements IChecker {

    @Override
    void doChecker(CheckerData data) {
        NetSuiteAppBase context = data.context
        NCurrentRecord currentRecord = data.currentRecord

        String key = data.key

        if(!context.isOneWorld()) {
            if(key.equals("subsidiary")) {
                data.isChecked = true
                data.isMainField = true
                data.selType = SelType.UNKNOWN
                return
            }
        }


        if(key.equals("TAB")) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.TAB
            return
        }


        if(key.equals("menus")) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.MENUS
            return
        }


        if(key.equals("dialogs")) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.DIALOG
            return
        }

        if(key.equals("sleep")) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.SLEEP
            return
        }

        if(key.equals("waitforload")) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.WAIT_FOR_LOAD
            return
        }


        if(key.equals("switchwindow")) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.SWITCH_WINDOW
            return
        }


        if(key.equals("deletefile")) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.DELETE_DIR
            return
        }


        if(key.equals("checkreportfile")) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.CHECK_REPORT_EXIST
            return
        }

        String retVal = currentRecord.getMainField(key)

        if(!StringUtils.isEmpty(retVal)) {
            data.isChecked = true
            data.isMainField = true
            data.selType = SelType.MAIN_FIELDID
        }
    }
}
