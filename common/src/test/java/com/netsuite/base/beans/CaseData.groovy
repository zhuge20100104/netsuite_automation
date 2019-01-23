package com.netsuite.base.beans

import com.google.inject.Inject
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.lib.json.JsonUtil

class CaseData {


    JsonUtil jsonUtil = new JsonUtil()

    def cData

    def CaseData(String path) {
        cData = jsonUtil.asJson(path:path)
    }

    TestData data(String testName) {
        return new TestData(cData[testName])
    }

    def getJson(String testName) {
        return cData[testName]
    }
}
