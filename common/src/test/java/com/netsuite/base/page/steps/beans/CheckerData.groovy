package com.netsuite.base.page.steps.beans

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NRecord
import com.netsuite.base.lib.NetSuiteAppBase

class CheckerData {
    NetSuiteAppBase context
    NCurrentRecord currentRecord
    NRecord record
    String machineKey=""
    String key
    def value
    boolean isChecked = false
    boolean isStepHandled = false
    boolean isExpected = false
    boolean isMainField = false
    boolean isSubField = false
    SelType selType = SelType.UNKNOWN
}
