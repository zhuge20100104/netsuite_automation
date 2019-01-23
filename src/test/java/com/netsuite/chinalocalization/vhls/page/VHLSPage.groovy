package com.netsuite.chinalocalization.vhls.page

import com.google.inject.Inject

import com.netsuite.base.lib.url.URLUtil

import com.netsuite.base.page.actions.global.GlobalValueCache
import com.netsuite.base.page.jsondriver.JPageBase


class VHLSPage extends JPageBase {
    @Inject
    VHLSPage() {
    }


    def getDirtyDataInternalId(int index) {
        def dirtyData = GlobalValueCache.getValue("dirtyData")
        return dirtyData[0].internalid
    }

 /*   def custpage_sublist(Object data){
        data.each{ it ->
            it.each{ k,v-> println("${k}:${v}")


            }
        }
    }

*/

    def selectInvoiceCheckBox(String tranId) {
        this.waitForPageToLoad()
        def row = currentRecord.findSublistLineWithValue("apply","refnum",tranId)
        def elementXPath = String.format("//*[@id='apply%d_fs']",row)
        click("xpath",elementXPath)
    }


    def navigateToClearDataPage(URLUtil urlUtil, List<String> transactionIds, String subsidiaryId) {
        // /app/site/hosting/restlet.nl?script=92&deploy=1
        String navigateToStr = ""
        def baseURL = urlUtil.resolveRestletURL("customscript_rl_emea_controller","customdeploy_rl_emea_controller")
        navigateToStr += baseURL
        navigateToStr += "&"
        navigateToStr += "transactionInternalId="

        transactionIds.each {
            navigateToStr += it
            navigateToStr += "|"
        }

        navigateToStr = navigateToStr.substring(0,navigateToStr.length()-1)
        navigateToStr += "&"
        navigateToStr += "subsidiaryId="
        navigateToStr += subsidiaryId
        println navigateToStr
        autBase.navigateTo(navigateToStr)
        this.waitForPageToLoad()
    }

}
