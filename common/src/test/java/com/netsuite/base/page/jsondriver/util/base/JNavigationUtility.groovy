package com.netsuite.base.page.jsondriver.util.base

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.jsondriver.util.base.JUtility
import net.qaautomation.exceptions.SystemException

class JNavigationUtility {
    static void navigateToPage(NetSuiteAppBase context,String url) {
        context.navigateTo(url)
    }


    static void navigateToPage(NetSuiteAppBase context,String type,String scriptId,String deployId){
        if(type.equals("suitelet")) {
            context.navigateTo(JUtility.getSuiteletURL(context,scriptId,deployId))
        }else if(type.equals("restlet")) {
            context.navigateTo(JUtility.getRestletURL(context,scriptId,deployId))
        }else {
            throw new SystemException("Not supported suitelet type, please check your parameters!!")
        }
    }
}
