package com.netsuite.chinalocalization.voucher.model.p2

import com.netsuite.chinalocalization.lib.NetSuiteAppCN

class CaseData {

    def cData
    def CaseData(NetSuiteAppCN context, String path) {
        cData = context.asJSON(path:path)
    }

    def data(String testName) {
        return new TestData(cData[testName])
    }
}
