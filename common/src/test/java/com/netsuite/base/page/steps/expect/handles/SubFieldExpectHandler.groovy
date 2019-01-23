package com.netsuite.base.page.steps.expect.handles

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NetSuiteAppBase
import net.qaautomation.exceptions.SystemException
import org.junit.Assert

class SubFieldExpectHandler {

    static boolean checkSubListColumnContainsValues(NetSuiteAppBase context, NCurrentRecord currentRecord, String machineKey, String key, def subValueExp) {
        List<String> values = new ArrayList<>()
        int lineCount = currentRecord.getLineCount(machineKey)
        for (int i = 1; i <= lineCount; i++) {
            context.withinEditMachine(machineKey).setCurrentLine(i)

            String value = ""
            if (context.getItemFieldType(machineKey, key) == "select") {
                value = context.withinEditMachine(machineKey).getFieldText(key)
            } else {
                value = context.withinEditMachine(machineKey).getFieldValue(key)
            }

            values.add(value)
        }



        boolean isContains = true
        for (String s : subValueExp) {
            if (!values.contains(s)) {
                isContains = false
            }
        }

        return isContains
    }

    static boolean checkViewSubListColumnContainsValues(NetSuiteAppBase context, NCurrentRecord currentRecord, String machineKey, String key, def subValueExp) {
        List<String> values = new ArrayList<>()
        int lineCount = currentRecord.getLineCount(machineKey)
        for (int i = 1; i <= lineCount; i++) {
            String value = currentRecord.getSublistValue(machineKey, key, i)
            values.add(value)
        }



        boolean isContains = true
        for (String s : subValueExp) {
            if (!values.contains(s)) {
                isContains = false
            }
        }

        return isContains
    }


    static boolean checkSubListRecordRowContainsValues(NetSuiteAppBase context, NCurrentRecord currentRecord, String id, String recordName, String machineKey, List<String> keys, List<String> values) {

        String getSubListLengthScript = "return nlapiLoadRecord('"+recordName+"','"+id+"').lineitems."+machineKey+".length";
        int lineCount = Integer.parseInt(context.executeScript(getSubListLengthScript));


        List<List<String>> allValues = new ArrayList<>()

        for (int i = 1; i <lineCount; i++) {

            List<String> actValues = new ArrayList<>()
            for (int j = 0; j < keys.size(); j++) {
                String key = keys.get(j)

                String getValueOfKey = "return nlapiLoadRecord('"+recordName+"','"+id+"').lineitems."+machineKey+"["+i+"]."+key
                String value = context.executeScript(getValueOfKey)
                actValues.add(value)
            }

            allValues.add(actValues)
        }



        for (List<String> actValues : allValues) {
            actValues = actValues.sort()
            values = values.sort()

            if (actValues.equals(values)) {
                return true
            }
        }


        return false

    }


    static boolean checkSubListOptionsContains(NetSuiteAppBase context, String machineKey, String key, def checkValue) {
        List<String> actualList = context.getSublistDropdownDisplayedOptions(machineKey, key)
        boolean isContains = true
        for (String singleCV : checkValue) {
            if (!actualList.contains(singleCV)) {
                isContains = false
                break
            }
        }
        return isContains
    }


    static boolean checkSubListOptionsNotContains(NetSuiteAppBase context, String machineKey, String key, def checkValue) {
        List<String> actualList = context.getSublistDropdownDisplayedOptions(machineKey, key)
        boolean isNotContains = true
        for (String singleCV : checkValue) {
            if (actualList.contains(singleCV)) {
                isNotContains = false
                break
            }
        }
        return isNotContains
    }






    static String getIdByUrl(String url) {
        String [] arr = url.split("&");
        String idStr="";

        for(String s: arr) {
            if(s.contains("id=")) {
                idStr = s;
                break;
            }
        }
        String id =  idStr.split("=")[1];
        System.out.println(id);
        return id;
    }



    static void handleSubFieldRecordRowExp(NetSuiteAppBase context, NCurrentRecord currentRecord, String machineKey, String key, def subListValue) {

        String url = context.getPageUrl()
        String id = getIdByUrl(url)

        //Handle single row compare
        String recordName =  subListValue.recordName
        List<String> names = subListValue.names
        List<String> values = subListValue.values

        boolean isContains = checkSubListRecordRowContainsValues(context, currentRecord, id,recordName,machineKey, names, values)
        Assert.assertTrue("Check sublist contains row failed!! sublist id: " + machineKey
                , isContains)
    }




    static boolean  checkSubListRowContainsValues(NetSuiteAppBase context,NCurrentRecord currentRecord,String machineKey, List<String> keys, List<String> values) {

        int lineCount = currentRecord.getLineCount(machineKey)
        List<List<String>> allValues = new ArrayList<>()

        for(int i=1;i<=lineCount;i++) {
            context.withinEditMachine(machineKey).setCurrentLine(i)

            List<String> actValues = new ArrayList<>()
            for(int j=0;j<keys.size();j++) {
                String key = keys.get(j)

                String value = ""
                if (context.getItemFieldType(machineKey,key) == "select") {
                    value = context.withinEditMachine(machineKey).getFieldText(key)
                }else {
                    value = context.withinEditMachine(machineKey).getFieldValue(key)
                }
                actValues.add(value)
            }

            allValues.add(actValues)
        }

        for(List<String> actValues : allValues) {
            actValues = actValues.sort()
            values = values.sort()

            if(actValues.equals(values)) {
                return true
            }
        }


        return false

    }


    static void handleSubFieldRowExp(NetSuiteAppBase context, NCurrentRecord currentRecord, String machineKey, String key, def subListValue) {
        //Handle single row compare
        List<String> names = subListValue.names
        List<String> values = subListValue.values

        boolean isContains = checkSubListRowContainsValues(context,currentRecord,machineKey,names,values)
        Assert.assertTrue("Check sublist contains row failed!! sublist id: " + machineKey
                , isContains)
    }

    //TODO: Need to refract, too many if else
    static void handleSubFieldExp(NetSuiteAppBase context, NCurrentRecord currentRecord, String machineKey, String key, def subListValue) {

        //Handle field compare
        subListValue.each {
            subListKey, subValueExp ->
                if (subListKey.equals("header")) {
                    Assert.assertTrue("check column exist,sublistId：" + machineKey + " Column: " + subValueExp
                            , context.withinEditMachine(machineKey).doesColumnExist(subValueExp))
                } else if (subListKey.equals("disabled")) {
                    Assert.assertEquals("Check Field Disable, FieldId: " + key, context.withinEditMachine(machineKey).isFieldDisabled(key), subValueExp)
                } else if (subListKey.equals("type")) {
                    Assert.assertEquals("Check Field Type, FieldId: " + key, context.getItemFieldType(machineKey, key), subValueExp)
                } else if (subListKey == "view_values") {
                    boolean isContains = checkViewSubListColumnContainsValues(context, currentRecord, machineKey, key, subValueExp)
                    Assert.assertTrue("check contains values, column：[" + key + "]", isContains)
                } else if (subListKey.equals("values")) {
                    boolean isContains = checkSubListColumnContainsValues(context, currentRecord, machineKey, key, subValueExp)
                    Assert.assertTrue("check contains values, column：[" + key + "]", isContains)
                } else if (subListKey.equals("required")) {
                    Assert.assertEquals("Check Field Required, FieldId: " + key, context.isSublistFieldMandory(machineKey, key), subValueExp)
                } else if (subListKey.equals("options")) {
                    def OptionalList = context.getSublistDropdownDisplayedOptions(machineKey, key)
                    Assert.assertEquals("Check Field Optional list Size, FieldId: " + key, OptionalList.size(), subValueExp.size())
                    Assert.assertEquals("Check Field Optional list, FieldId: " + key, OptionalList.sort(), subValueExp.sort())
                } else if (subListKey.equals("optioncontains")) {
                    boolean isContains = checkSubListOptionsContains(context, machineKey, key, subValueExp)
                    Assert.assertTrue("Check options contains failed,FieldId: " + key, isContains)
                } else if (checkKey.equals("optionnotcontains")) {
                    boolean isNotContains = checkSubListOptionsNotContains(context, machineKey, key, checkValue)
                    Assert.assertTrue("Check options not contains failed,FieldId: " + key, isNotContains)
                } else {
                    throw new SystemException("Not suported Expected type exception!!!,The only supported expected types" +
                            "are: [header, values, readonly, display, required], please check your json checkers!!!")
                }
        }

    }
}
