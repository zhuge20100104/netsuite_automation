package com.netsuite.base.page.steps.jsteps.handles

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.jsteps.handles.beans.EditTableAction
import com.netsuite.base.page.steps.jsteps.handles.beans.JTableColumn
import com.netsuite.base.page.steps.jsteps.handles.beans.JTableRow
import com.netsuite.base.page.steps.jsteps.handles.beans.SelectTableAction
import com.netsuite.testautomation.driver.SeleniumHtmlElementHandle
import com.netsuite.testautomation.html.Locator
import net.qaautomation.exceptions.SystemException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class JXpathTableHandler {

    static  Map<Element, Map<String,String>> parseTable(String tableId,String htmlStr) {
        Map<Element, Map<String,String>> tableData = new HashMap<>()
        List<String> headers = new ArrayList<>()

        Document document = Jsoup.parse(htmlStr)
        Elements tblElements  = document.select("#"+tableId)
        Elements thrElements = tblElements.get(0).select("tr.uir-list-headerrow")
        Elements thElements = thrElements.get(0).select("td")

        for(Element th:thElements) {
            String header = th.text().replace(" "," ").trim().toLowerCase()
            headers.add(header);
        }

        Elements trElements = tblElements.get(0).select("tr.uir-list-row-tr");

        for(Element tr: trElements) {
            Elements tdElements = tr.select("td");
            HashMap<String,String> aLine = new HashMap<>();
            for(int i=0;i<tdElements.size();i++) {
                String value = tdElements.get(i).text().replace(" "," ").trim()
                aLine.put(headers.get(i),value);
            }
            tableData.put(tr,aLine)
        }

        return tableData;
    }


    static void navigateToEditPage(NetSuiteAppBase context, Map<Element, Map<String,String>> tableData,List<String> names,List<String> values) {


        Element actNode = null

        for(Map.Entry<Element,Map<String,String>> elementAndLine: tableData) {
            boolean isCorrectLine = true

            for(int i=0;i<names.size();i++) {
                String expKey = names[i].toLowerCase()
                String expValue = values[i]

                Map<String,String> actLine = elementAndLine.value
                if(!actLine.get(expKey).equals(expValue)) {
                    isCorrectLine = false
                    break
                }
            }

            if(isCorrectLine) {
                actNode = elementAndLine.key
                break
            }
        }

        if(actNode == null) {
            throw new SystemException("Could not find correct line using given values: " +names +" ----> " + values)
        }

        Element firstTd = actNode.select("td").get(0)
        String editUrl = firstTd.select("a").get(0).attr("href")
        context.navigateTo(editUrl)
    }


    static void handleRowEdit(NetSuiteAppBase context, String key, def value) {
        String recordName = value.recordName
        List<String> names = value.names
        List<String> values = value.values

        String pageXML = context.webDriver.getPageAsXml()
        Map<Element, Map<String,String>> tableData = parseTable(recordName,pageXML)
        navigateToEditPage(context, tableData, names, values)
    }



    static  List<JTableRow>  parseTableRow(String tableId, String htmlStr) {

        List<JTableRow> tableRows = new ArrayList<>()

        List<String> headers = new ArrayList<>()

        Document document = Jsoup.parse(htmlStr)
        Elements tblElements  = document.select("#"+tableId)
        Elements thrElements = tblElements.get(0).select("tr.uir-machine-headerrow")
        Elements thElements = thrElements.get(0).select("td")

        for(Element th:thElements) {
            String header = th.text().replace(" "," ").trim().toLowerCase()
            headers.add(header);
        }

        Elements trElements = tblElements.get(0).select("tr.uir-machine-row");

        int rowIndex = 1;

        for(Element tr: trElements) {
            Elements tdElements = tr.select("td");
            JTableRow tableRow = new JTableRow()
            tableRow.setRow(rowIndex++)
            for(int i=0;i<tdElements.size();i++) {
                String value = tdElements.get(i).text().replace(" "," ").trim()
                JTableColumn tableColumn = new JTableColumn()
                tableColumn.setCol(i+1)
                tableColumn.setHeader(headers.get(i))
                tableColumn.setValue(value)

                tableRow.addColumn(tableColumn)
            }

            tableRows.add(tableRow)
        }

        return tableRows
    }


    static JTableRow findTableElementRow(NetSuiteAppBase context,def value) {
        String recordName = value.recordName
        List<String> names = value.names
        List<String> values = value.values

        String pageXML = context.webDriver.getPageAsXml()
        List<JTableRow> tableData = parseTableRow(recordName,pageXML)


        JTableRow matchedTableRow = null

        for(JTableRow tableRow: tableData) {
            boolean isCorrectLine = true

            for(int i=0;i<names.size();i++) {
                String expKey = names[i].toLowerCase()
                String expValue = values[i]


                if(!tableRow.getKeyValue(expKey).equals(expValue)) {
                    isCorrectLine = false
                    break
                }
            }

            if(isCorrectLine) {
                matchedTableRow = tableRow
                break
            }
        }

        if(matchedTableRow == null) {
            throw new SystemException("Could not find correct line using given values: " +names +" ----> " + values)
        }

        return matchedTableRow
    }




    static String generateXPath(String tableId, int row, int col) {
        return "//table[@id='"+tableId+"']/tbody/tr["+row+"]/td["+col+"]"
    }


    //Retry to avoid errors, when there is a selected line, this will result in an error
    static SeleniumHtmlElementHandle getEleHandle(NetSuiteAppBase context,String recordName,int row, int col) {
        def Ele = context.webDriver.getHtmlElementByLocator(Locator.xpath(generateXPath(recordName,row+1,col)))

        if(Ele == null) {
            Ele = context.webDriver.getHtmlElementByLocator(Locator.xpath(generateXPath(recordName,row+2,col)))
        }
        return Ele
    }

    static void handleColumnEdit(NetSuiteAppBase context, String key, def value) {

        JTableRow tableRow = findTableElementRow(context,value)
        String recordName = value.recordName
        List<String> editColumns = value.editColumns
        List<String> editValues = value.editValues



        String clickColumn = value.clickColumn.get(0)
        int clickRow = tableRow.getRow()
        int clickCol = tableRow.getGetKeyColumn(clickColumn)
        def endEditEle = getEleHandle(context,recordName,clickRow,clickCol)

        int i = 0
        for(String editColumn: editColumns) {
            int row = tableRow.getRow()
            int col = tableRow.getGetKeyColumn(editColumn)

            def editEle = getEleHandle(context, recordName, row, col)

            EditTableAction editTableAction = new EditTableAction(editEle,endEditEle)
            editTableAction.startEdit()
            editTableAction.edit(editValues.get(i++))
            editTableAction.endEdit()
        }

    }


    static void handleRowSelect(NetSuiteAppBase context, String key, def value) {
        JTableRow tableRow = findTableElementRow(context,value)
        String recordName = value.recordName


        String clickColumn = value.clickColumn.get(0)
        int clickRow = tableRow.getRow()
        int clickCol = tableRow.getGetKeyColumn(clickColumn)
        def clickEle = getEleHandle(context, recordName, clickRow, clickCol)

        SelectTableAction selectTableAction = new SelectTableAction(clickEle)
        selectTableAction.selectRow()
    }
}
