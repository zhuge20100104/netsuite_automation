package com.netsuite.base.control.ext

import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser

/**
 *  Created by Fredriczhu on 4/11/2018.
 *  This tableParserExt class can let you get one single td element in a table
 */
class TableParserExt extends TableParser{

    TableParserExt(WebDriver webDriver) {
        super(webDriver)
    }


    HtmlElementHandle getHeaderElement(String tableLocator, int col) {
        HtmlElementHandle tableHandle = webDriver.getHtmlElementByLocator(Locator.xpath(tableLocator))
        String headerColumnLocator = String.format(".//tr[%d]/th[%d]",1,col)
        HtmlElementHandle headerColumnHandle = tableHandle.getElementByLocator(Locator.xpath(headerColumnLocator))
        return headerColumnHandle
    }

    String getHeaderElementXML(String tableLocator,int col) {
        HtmlElementHandle headerColumnHandle = getHeaderElement(tableLocator,col)
        return headerColumnHandle.getAttributeValue("innerHTML")
    }

    String getHeaderElementText(String tableLocator,int col) {
        HtmlElementHandle headerColumnHandle = getHeaderElement(tableLocator,col)
        return headerColumnHandle.getAsText()
    }


    HtmlElementHandle getBodyElement(String tableLocator,int row,int col) {
        HtmlElementHandle tableHandle = webDriver.getHtmlElementByLocator(Locator.xpath(tableLocator))
        String columnLocator = String.format(".//tr[%d]/td[%d]",row,col)
        println columnLocator
        HtmlElementHandle columnHandle = tableHandle.getElementByLocator(Locator.xpath(columnLocator))
        return columnHandle
    }

    String getBodyElementXML(String tableLocator,int row,int col) {
        HtmlElementHandle columnHandle = getBodyElement(tableLocator,row,col)
        return  columnHandle.getAttributeValue("innerHTML")
    }

    String getBodyElementText(String tableLocator,int row,int col) {
        HtmlElementHandle columnHandle = getBodyElement(tableLocator,row,col)
        return  columnHandle.getAsText()
    }


    /**
     *
     * @param tableLocator  Table locator  of the table to locate
     * @param textToSearch  text to search
     * @return  an array, first array element is row, second array element is col
     */
    def  getBodyElementRowCol(String tableLocator,String textToSearch) {

        String rowsIteratorXPath = "//tbody//tr"
        List<HashMap<String, String>> tableValueMap = this.parseTable(tableLocator, null, rowsIteratorXPath)

        for (int i = 1; i < tableValueMap.size(); i++) {

            HashMap<String, String> row = tableValueMap.get(i)
            int rowSize = row.size()

            for(int j = 0; j<rowSize;j++) {
                if(row.get(j+"").contains(textToSearch)) {
                    return ["row":i+1,"col":j+1]
                }
            }

        }
        return ["row":-1,"col":-1]
    }


    def getTableRange(String tableXPath,String headerItemsIteratorXPath, String rowsIteratorXPath,rowRange,colRange) {
        List<HashMap<String, String>> lists = this.parseTable(tableXPath,headerItemsIteratorXPath,rowsIteratorXPath)

        int rowStart = rowRange.start
        int rowEnd = rowRange.end
        int colStart = colRange.start
        int colEnd = colRange.end


        int rowIndex = 0
        int colIndex = 0


        def result = []
        lists.each {
            itMap ->

                if(rowIndex>=rowStart && rowIndex<=rowEnd) {
                    colIndex = 0
                    def innerResult = []
                    itMap.each {
                        if (colIndex >= colStart && colIndex <= colEnd) {
                            innerResult.add(it.getValue())
                        }
                        colIndex++
                    }

                    result.add(innerResult)
                }
                rowIndex ++
        }

        return result
    }


    def getTableRange(List<HashMap<String, String>> lists,rowRange,colRange) {
        int rowStart = rowRange.start
        int rowEnd = rowRange.end
        int colStart = colRange.start
        int colEnd = colRange.end


        int rowIndex = 0
        int colIndex = 0


        def result = []
        lists.each {
            itMap ->

                if(rowIndex>=rowStart && rowIndex<=rowEnd) {
                    colIndex = 0
                    def innerResult = []
                    itMap.each {
                        if (colIndex >= colStart && colIndex <= colEnd) {
                            innerResult.add(it.getValue())
                        }
                        colIndex++
                    }

                    result.add(innerResult)
                }
                rowIndex ++
        }

        return result
    }

}