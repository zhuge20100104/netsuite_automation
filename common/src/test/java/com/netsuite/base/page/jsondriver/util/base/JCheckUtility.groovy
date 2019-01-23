package com.netsuite.base.page.jsondriver.util.base

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.checkers.ClassNameChecker
import com.netsuite.base.page.steps.checkers.IChecker
import com.netsuite.base.page.steps.checkers.IdChecker
import com.netsuite.base.page.steps.checkers.NameChecker
import com.netsuite.base.page.steps.checkers.XPathChecker
import com.netsuite.base.page.steps.checkers.fieldids.MainFieldChecker
import com.netsuite.base.page.steps.checkers.fieldids.SubFieldChecker
import com.netsuite.base.page.utility.PageLoadWait
import org.apache.commons.lang3.StringUtils

class JCheckUtility {


    static CheckerData doCheckForData(NRecord record, NCurrentRecord currentRecord, NetSuiteAppBase autBase, String mainKey, def mainValue) {
        PageLoadWait.waitForPageReady(autBase.webDriver)

        IChecker checker = null
        CheckerData checkerData = new CheckerData()
        checkerData.context = autBase
        checkerData.currentRecord = currentRecord
        checkerData.record = record
        checkerData.key = mainKey
        checkerData.value = mainValue

        if(mainKey.contains("//")) {
            checker = new XPathChecker()
            checker.doChecker(checkerData)

        }else {
            List<IChecker> checkers = new ArrayList<>()
            checkers.add(new MainFieldChecker())
            checkers.add(new IdChecker())
            checkers.add(new NameChecker())
            checkers.add(new ClassNameChecker())

            for(IChecker innerChecker in checkers) {
                innerChecker.doChecker(checkerData)
                if(checkerData.isChecked) {
                    break
                }
            }
        }

        return checkerData
    }


    static CheckerData  doCheckForSubData(IChecker checker,CheckerData checkerData, String subListKey,Object subListValue) {

        PageLoadWait.waitForPageReady(checkerData.context.webDriver)

        checkerData.key = subListKey
        checkerData.value = subListValue
        if(subListKey.contains("//")) {
            checker = new XPathChecker()
            checker.doChecker(checkerData)

            if(checkerData.isChecked) {
                return checkerData
            }

        }else if(!StringUtils.isEmpty(checkerData.machineKey)) {
            checker = new SubFieldChecker()
            checker.doChecker(checkerData)
            if(checkerData.isChecked) {
                return checkerData
            }
        }

        List<IChecker> checkers = new ArrayList<>()
        checkers.add(new IdChecker())
        checkers.add(new NameChecker())
        checkers.add(new ClassNameChecker())

        for(IChecker innerChecker in checkers) {
            innerChecker.doChecker(checkerData)
            if(checkerData.isChecked) {
                break
            }
        }

        return checkerData
    }

}
