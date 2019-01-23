package com.netsuite.base.page.actions.concrete.param

import com.netsuite.base.page.actions.global.GlobalValueCache
import net.qaautomation.exceptions.SystemException

import java.util.regex.Matcher
import java.util.regex.Pattern

class ParamParser {
    private static String sharpParserPattern = "^.*(\\#.*)\\]"

    static String getSharpPart(String locatorValue) {
        // Create a pattern object
        Pattern r = Pattern.compile(sharpParserPattern)
        // Create a matcher object
        Matcher m = r.matcher(locatorValue)

        if(m.find()) {
            return m.group(1)
        }
    }

    static void main(String [] args) {
        println(getSharpPart("//[text=#internalId]"))
    }


    static Object getActualParamValue(String paramValue) {
        if(paramValue.startsWith("#")) {
            String paramValueKey =  paramValue.replace("#","")
            if(GlobalValueCache.containsKey(paramValueKey)) {
                Object actualParamValue = GlobalValueCache.getValue(paramValueKey)
                return actualParamValue
            }else {
                throw new SystemException("Can't find key: ["+paramValueKey+"] in Global Value Cache!!")
            }

            //[text=#internalId]
        }else if (paramValue.contains("#")){
            String sharpPart = ParamParser.getSharpPart(paramValue)
            String replaceParamPart = sharpPart.replace("#","")

            if(GlobalValueCache.containsKey(replaceParamPart)) {
                String actualParamValue = GlobalValueCache.getValue(replaceParamPart).toString()
                paramValue = paramValue.replace(sharpPart,actualParamValue)
                return paramValue
            }else  {
                throw new SystemException("Can't find key: ["+paramValue+"] in Global Value Cache!!")
            }

        }else {
            return paramValue
        }
    }


}
