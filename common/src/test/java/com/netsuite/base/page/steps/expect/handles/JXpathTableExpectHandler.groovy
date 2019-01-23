package com.netsuite.base.page.steps.expect.handles

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.expect.handles.utils.JsoupTableParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.junit.Assert

class JXpathTableExpectHandler {

    //Parse table for table customized from Standard table control in Netsuite
    static List<Map<String,String>> parseTable(String tableId,String htmlStr) {
        return JsoupTableParser.parseTable(tableId,htmlStr,"tr.uir-list-headerrow","tr.uir-list-row-tr")
    }


    static boolean tableContainsRow(List<Map<String,String>> tableData,List<String> names,List<String> values) {
        boolean isContains = false

        List<List<String>> actValueList = new ArrayList<>();

        for(Map<String,String> aLine: tableData) {
            List<String> aValueLine = new ArrayList<>()
            for(String name: names) {
                aValueLine.add(aLine.get(name.toLowerCase()))
            }
            actValueList.add(aValueLine)
        }

        for(List<String>  actValueLine: actValueList) {
            actValueLine = actValueLine.sort()
            values = values.sort()
            if(actValueLine.equals(values)) {
                isContains = true
                break
            }
        }

        return isContains
    }

    static void handleTableExp(NetSuiteAppBase context, String key, def value) {
        String recordName = value.recordName
        List<String> names = value.names
        List<String> values = value.values

        String pageXML = context.webDriver.getPageAsXml()
        List<Map<String,String>> tableData = parseTable(recordName,pageXML)
        boolean isTableContains = tableContainsRow(tableData,names,values)

        Assert.assertTrue("Table:["+recordName+"]  not contains row: "+values,isTableContains)

    }



    //Parse table for netsuite Team4 custom table
    static List<Map<String,String>> parseTableNew(String tableId,String htmlStr) {
        return JsoupTableParser.parseTable(tableId,htmlStr,"tr.uir-machine-headerrow","tr.uir-machine-row")
    }




    static boolean tableContainsRows(List<Map<String,String>> tableData,List<String> names,List<List<String>> values) {
        boolean isContains = true


        List<List<String>> actValueList = new ArrayList<>();

        for(Map<String,String> aLine: tableData) {
            List<String> aValueLine = new ArrayList<>()
            for(String name: names) {
                aValueLine.add(aLine.get(name.toLowerCase()))
            }
            aValueLine.sort()
            actValueList.add(aValueLine)
        }


        for(List<String> expValue: values) {
            expValue.sort()
            if(!actValueList.contains(expValue)){
                isContains = false
            }
        }

        return isContains
    }



    static void handleTableRowsExp(NetSuiteAppBase context, String key, def value) {
        String recordName = value.recordName
        List<String> names = value.names
        List<List<String>> values = value.values

        String pageXML = context.webDriver.getPageAsXml()
        List<Map<String,String>> tableData = parseTableNew(recordName,pageXML)
        boolean isTableContains = tableContainsRows(tableData,names,values)

        Assert.assertTrue("Table:["+recordName+"]  not contains row: "+values,isTableContains)

    }
}
