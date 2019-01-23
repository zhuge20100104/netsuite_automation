package com.netsuite.chinalocalization.common.pages

import com.google.inject.Inject
import com.netsuite.chinalocalization.lib.NCurrentRecord
import com.netsuite.chinalocalization.lib.NetSuiteAppCN

/**
 * Created by stepzhou on 2/8/2018.
 * This adapter class is used to distinguish India, China or other Localization teams in the future.
 * 1). Inject the Localization related engine such as: NetSuiteAppCN, NetSuiteAppIndia
 * 2). Add Localization common functions here.
 * 3). All Page objects should inherit from this class.
 */
class PageBaseAdapterCN extends PageBase {
    @Inject
    NetSuiteAppCN context
    @Inject
    NCurrentRecord currentRecord

}
