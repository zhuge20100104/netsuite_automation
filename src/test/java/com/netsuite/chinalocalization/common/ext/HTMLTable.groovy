package com.netsuite.chinalocalization.common.ext

import com.netsuite.testautomation.aut.NetSuiteApp

/**
 * Created by stepzhou on 3/30/2018.
 */
class HTMLTable {
    NetSuiteApp context
    String tableLocator

    private int rowLength
    private int columnLength

    HTMLTable(NetSuiteApp context, String tableLocator) {
        this.context = context
        this.tableLocator = tableLocator
        rowLength = getRowLength()
        columnLength = getColumnLength()
    }

    int getRowLength() {
        return Integer.parseInt(context.executeScript("return document.getElementById('" + tableLocator + "').getElementsByTagName('tbody')[0].getElementsByTagName('tr').length"))
    }

    int getColumnLength() {
        return Integer.parseInt(context.executeScript("return document.getElementById('" + tableLocator + "').getElementsByTagName('tbody')[0].getElementsByTagName('tr')[0].getElementsByTagName('td').length"))
    }

    int getColumnIndex(String columnName) {
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < columnLength; j++) {
                String cellValue = context.executeScript("return document.getElementById('" + tableLocator + "').getElementsByTagName('tbody')[0].getElementsByTagName('tr')[" + i + "].getElementsByTagName('td')[" + j + "].textContent")
                if (cellValue.equalsIgnoreCase(columnName))
                    return j
            }
        }
        return -1
    }

    int getRowIndex(String rowContent, int columnIndex) {
        for (int i = 0; i < rowLength; i++) {
            String cellContent = context.executeScript("return document.getElementById('rptcolheader').getElementsByTagName('tbody')[0].getElementsByTagName('tr')[" + i + "].getElementsByTagName('td')[" + columnIndex + "].textContent")
            if (cellContent.contains(rowContent))
                return i
        }
        return -1
    }

    String getCellContent(int row, int column) {
        return context.executeScript("return document.getElementById('rptcolheader').getElementsByTagName('tbody')[0].getElementsByTagName('tr')[" + row + "].getElementsByTagName('td')[" + column + "].textContent")
    }
}