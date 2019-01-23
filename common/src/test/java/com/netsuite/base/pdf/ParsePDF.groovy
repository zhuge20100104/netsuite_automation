package com.netsuite.base.pdf

import com.netsuite.base.pdf.PdfUtil
import org.apache.commons.io.FileUtils
import org.faceless.pdf2.PDFParser

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.LinkedList
import java.util.List

/**
 * Created by stepzhou on 4/25/2018.
 */

public class ParsePDF {
    private static final String DOWNLOADPATH = "data//downloads"
    private PdfUtil pdfUtil = new PdfUtil()
    private List<List<String>> pdfFileContents = new ArrayList<>()
    private String downloadedFile

    // Use this constructor to manually parse one PDF file.
    public ParsePDF(String pdfFilePath) {
        pdfFileContents = pdfUtil.readPdfLines(pdfFilePath)
    }

    // Use this constructor to automatically pick up the latest one from the specified path.
    public ParsePDF() {
        File latestDownloadFile = lastFileModified(DOWNLOADPATH)
        downloadedFile = latestDownloadFile.getPath()
        pdfFileContents = pdfUtil.readPdfLines(latestDownloadFile.getPath())
    }

    public List<List<String>> getRawContents() {
        return pdfFileContents
    }

    public String getDownloadedFile() {
        return downloadedFile
    }

    /**
     * Return a specified key's value. For instance: Page:1/1,
     * The 'Page' is the key. And its value is '1/1'.
     *
     * @param key The key you want to test.
     */
    public String getValueFromKey(String key) {
        for (List<String> items : pdfFileContents) {
            for (String lineContent : items) {
                if (lineContent.contains(key)) {
                    String[] lineItemsArr = lineContent.split(" {2,10}")
                    for (String lineItemsArrItem : lineItemsArr) {
                        if (lineItemsArrItem.contains(key)) {
                            return handleStr(lineItemsArrItem.substring(key.length()))
                        }
                    }
                }
            }
        }
        return ""
    }

    /**
     * Get the cell value from a specified line.
     *
     * @param key used to identify the unique line.
     * @param index the column index of this line.
     *              Please notice that when count the index value, ignore the empty column.
     */
    public String getValueFromLine(String key, int index) {
        for (List<String> items : pdfFileContents) {
            for (String lineContent : items) {
                if (lineContent.contains(key)) {
                    List<String> lineContents = getLineContents(lineContent)
                    return lineContents.get(index - 1)
                }
            }
        }
        return ""
    }

    /**
     * Get the cell value from a specified line.
     *
     * @param key used to identify the lines.
     * @param index the column index of this line.
     *              Please notice that when count the index value, ignore the empty column.
     */
    public ArrayList<String> getValuesFromLines(String key, int index) {
        List<String> ret=new ArrayList<String>()
        for (List<String> items : pdfFileContents) {
            for (String lineContent : items) {
                if (lineContent.contains(key)) {
                    List<String> lineContents = getLineContents(lineContent)
                    ret.add(lineContents.get(index - 1))
                    //return lineContents.get(index - 1)
                }
            }
        }
        return ret
    }


    /**
     * Return the whole line's value with a String.
     *
     * @param key used to identify the unique key.
     */
    public String getLineStringWithKey(String key) {

        for (List<String> items : pdfFileContents) {
            for (String lineContent : items) {
                if (lineContent.contains(key)) {
                    return lineContent
                }
            }
        }
        return ""
    }


    /**
     * Return the whole line's value with a String.
     * support dup key
     *
     * @param key used to identify the unique key.
     */
    public String getLineStringsWithKey(String key) {
        ArrayList<String> ret=new ArrayList<String>()
        for (List<String> items : pdfFileContents) {
            for (String lineContent : items) {
                if (lineContent.contains(key)) {
//                    return lineContent
                    ret.add(lineContent)
                }

            }
        }
        return ret
    }


    /**
     * Return the whole line's value with a List.
     *
     * @param key used to identify the unique key.
     */
    public List<String> getLineListWithKey(String key) {

        for (List<String> items : pdfFileContents) {
            for (String lineContent : items) {
                if (lineContent.contains(key)) {
                    return getLineContents(lineContent)
                }
            }
        }

        return new ArrayList<>()
    }

    /**
     * Get a key's value from one specified table.
     *
     * @param beginKey used to identify the table beginning.
     * @param endKey used to identify the table ending.
     * @param tKey used to identify one table from tables.
     * @param vKey the key which you want to get its value.
     */
    public String getValueFromTable(String beginKey, String endKey, String tKey, String vKey) {
        List<List<String>> tables = getDataFromTable(beginKey, endKey)
        List<String> tarTable = new LinkedList<>()
        for (List<String> table : tables) {
            for (String line : table) {
                if (line.contains(tKey)) {
                    tarTable.addAll(table)
                }
            }
        }

        if (tarTable.size() > 0) {
            for (String line : tarTable) {
                if (line.contains(vKey)) {
                    List<String> lineContents = getLineContents(line)
                    for (String item : lineContents) {
                        if (item.contains(vKey)) {
                            return item.substring(vKey.length() + 2)
                        }
                    }
                }
            }
        }

        return ""
    }

    /**
     * This method will return all the data(table header, table itself, table footer).
     * If the PDF only include one table OR you only care the first table, you can use this method.
     *
     * @return data
     * Sample to use this method:
     * data.get(0).get(1)
     * refer to the value of first line, the second column
     */
    public List<List<String>> getDataFromTable() {
        List<String> firstList = pdfFileContents.get(0)
        List<List<String>> table = new LinkedList<>()
        for (String lineContent : firstList) {
            List<String> listItem = getLineContents(lineContent)
            if (listItem.size() > 0)
                table.add(listItem)
        }
        return table
    }


    public List<List<String>> getDataFromTable(String beginKey, String endKey, String tKey) {
        List<List<String>> tables = getDataFromTable(beginKey, endKey)

        List<String> tarTable = new LinkedList<>()
        for (List<String> table : tables) {
            for (String line : table) {
                if (line.contains(tKey)) {
                    tarTable.addAll(table)
                }
            }
        }

        List<List<String>> results = new LinkedList<>()
        for (String lineContent : tarTable) {
            List<String> lineList = getLineContents(lineContent)
            if (lineList.size() > 0)
                results.add(getLineContents(lineContent))
        }

        return results
    }

    /**
     * Get all the tables in one PDF doc. The table is distinguished by 'beginKey' and 'endKey'.
     *
     * @param beginKey Refer to the first line of a table for instance 'Subsidiary : China BU'.
     * @param endKey Refer to the last line of a table for instance 'Created by: '
     *                 <p>
     *                 Please notice that: No need to copy the whole line here, just part of the line is suffice.
     */
    public List<List<String>> getDataFromTable(String beginKey, String endKey) {

        List<List<String>> tables = new LinkedList<>()
        List<String> tabList = new LinkedList<>()
        boolean isTableEnd = false

        for (List<String> items : pdfFileContents) {
            for (String lineContent : items) {
                if (lineContent.contains(beginKey)) {
                    tabList = new LinkedList<>()
                    tabList.add(lineContent)
                    isTableEnd = false
                } else if (!lineContent.contains(endKey) && !isTableEnd) {
                    tabList.add(lineContent)
                } else if (lineContent.contains(endKey)) {
                    tabList.add(lineContent)
                    isTableEnd = true
                    tables.add(tabList)
                }
            }
        }
        return tables
    }

    /**
     * Clean the download folder.
     * You can use it before or after your downloaded file validation.
     */
    public static void cleanDownloadFolder() {
        try {
            FileUtils.cleanDirectory(new File(DOWNLOADPATH))
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    public void resetPDFContent(String pdfFilePath) {
        pdfFileContents = pdfUtil.readPdfLines(pdfFilePath)
    }

    private void retrievePDFContent(String pdfFilePath) {
        if (pdfFileContents.size() == 0)
            pdfFileContents = pdfUtil.readPdfLines(pdfFilePath)
    }

    private List<String> getLineContents(String line) {
        String[] arr = line.split(" {2,10}")
        List<String> lineArr = new LinkedList<>()
        for (String item : arr) {
            if (item.length() > 0)
                lineArr.add(item)
        }
        return lineArr
    }

    private String handleStr(String input) {
        input = input.substring(input.indexOf(":") + 1)
        return input.trim()
    }

    private static File lastFileModified(String dir) {
        File fl = new File(dir)
        File[] files = fl.listFiles(new FileFilter() {
            @Override
            boolean accept(File file) {
                return file.isFile()
            }
        })

        long lastMod = Long.MIN_VALUE
        File choice = null
        assert files != null
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file
                lastMod = file.lastModified()
            }
        }
        return choice
    }

    /**
     * Provide some helpful samples.
     */
    public static void main(String[] args) {
        //String pdfFilePath = "data//downloads//" + "AccountingVoucher_180413170153.pdf"
        //String pdfFilePath = "data//downloads//" + "Balance Sheet (1).pdf"
        ParsePDF pdfParser=new ParsePDF()
       //def contentsss=pdfParser.getLineStringWithKey()

        List<List<String>> rawList =  pdfParser.getRawContents()
        for(List<String> lines: rawList){
            for(String row: lines){
                println row
            }
        }


        def values

        try {
            values=pdfParser.getLineStringsWithKey(" Total 1110 - 現金及び預金")
        }
        catch (Exception e)
        {
            println e.stackTrace
            values=""
        }

        println values

        //File latestDownloadFile = lastFileModified(DOWNLOADPATH)

        //ParsePDF parsePDF = new ParsePDF()

//        parsePDF.getValueFromKey("Subsidiary")
//        parsePDF.getValueFromKey("页码")
//
//        parsePDF.getLineListWithKey("4")
        //parsePDF.getValueFromLine( "【Current Assets】", 2)
        //parsePDF.getValueFromTable("Subsidiary : China BU", "Created by:", "Document No. : 20031", "Page")

//        List<List<String>> data = parsePDF.getDataFromTable("Subsidiary : China BU", "Created by:", "Document No. : 20031")
//        List<List<String>> data = parsePDF.getDataFromTable("公司 : CN Sprint Auto Testing - SI", "记账:", "文档编号 : 109")
//
//        List<List<String>> data = parsePDF.getDataFromTable()
//        List<List<String>> data = parsePDF.getDataFromTable("Subsidiary : China BU", "Created by")

        cleanDownloadFolder()
    }

    /**
     * This method will return all the data(table header, table itself, table footer).
     * If the PDF only include one table OR you only care the first table, you can use this method.
     *
     * @return data
     * Sample to use this method:
     * data.get(0).get(1)
     * refer to the value of first line, the second column
     */
    public List<List<String>> getAllDataFromTable() {

        List<String> contentList = new ArrayList<String>()
        List<String> list = new ArrayList<String>()
        for (int i=0;i<pdfFileContents.size();i++) {
            contentList = pdfFileContents.get(i)
            list.addAll(contentList)
        }


        List<List<String>> table = new LinkedList<>()
        for (String lineContent : list) {
            List<String> listItem = getLineContents(lineContent)
            if (listItem.size() > 0)
                table.add(listItem)
        }
        return table
    }
}