package com.netsuite.base.report

import com.netsuite.base.excel.ExcelUtil
import com.netsuite.base.lib.property.PropertyUtil

class ConvertReportToExcel {
    static class ReadFile{
        static FileGetter fGetter

        static boolean isFileExist(String fileName) {
            fGetter = new FileGetter(fileName)
            return fGetter.isFileExist()
        }

        static List<String> readLines(String fileName) {
            fGetter = new FileGetter(fileName)
            String text = fGetter.toText().text
            String[] texts = text.split("\\r\\n")
            return texts.toList()
        }

        static List<String> getFileHeader() {
            return ["Case Name","Groups","Status"]
        }

        static List<List<String>> readContents(String fileName){
            List<String> lines = this.readLines(fileName)

            def retContents = new ArrayList<ArrayList<String>>()
            retContents.add(getFileHeader())
            lines.each {
                List<String> lineContent = new ArrayList<String>()
                String [] contentArr = it.split("\\|")
                contentArr.each {
                    content ->
                        lineContent.add(content)
                }

                retContents.add(lineContent)
            }

            return  retContents

        }
    }




    static void writeExcelFile(String excelPath,String textFileName,String sheetName) {
        String filePath = (excelPath + textFileName).replace("/",File.separator)

        if(ReadFile.isFileExist(filePath)) {
            List<List<String>> retContents = ReadFile.readContents(filePath)
            ExcelUtil excelUtil = new ExcelUtil()
            excelUtil.writeExcelContent((excelPath + "detail_excel.xlsx").replace("/",File.separator),sheetName,retContents)
        }
    }


    static void main(String[] args) {


        PropertyUtil propertyUtil = InjectClass.getInstance(PropertyUtil.class)
        String excelPath = propertyUtil.getReportExcelPath()
        println excelPath

        writeExcelFile(excelPath,"pass_detail_file.txt","pass_sheet")
        writeExcelFile(excelPath,"fail_detail_file.txt","fail_sheet")
    }
}
