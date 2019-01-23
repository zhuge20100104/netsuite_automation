package com.netsuite.base.excel

import com.monitorjbl.xlsx.StreamingReader
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelUtil {

    String retriveFilePath(String filePath) {
        if(filePath.contains("\\")) {
            filePath = filePath.replace("\\",File.separator)
        }

        if(filePath.contains("//")) {
            filePath = filePath.replace("//",File.separator)
        }
        return filePath
    }


    def getOrCreateSheet(Workbook workbook,String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName)
        if(sheet == null) {
            sheet = workbook.createSheet(sheetName)
        }
        return sheet
    }

    def getOrCreateWorkbook(String filePath) {
        Workbook workbook
        File file = new File(filePath)
        if(filePath.endsWith(".xls")){
            if(file.exists()) {
                InputStream inputStream = new FileInputStream(filePath)
                workbook = new HSSFWorkbook(inputStream)
            }else {
                workbook = new HSSFWorkbook()
            }
        }else if(filePath.endsWith(".xlsx")){
            if(file.exists()) {
                InputStream inputStream = new FileInputStream(filePath)
                workbook = new XSSFWorkbook(inputStream)
            }else {
                workbook = new XSSFWorkbook()
            }
        }else {
            throw new Exception("Not supported Excel write format!!!")
        }

        return workbook
    }

    def writeExcelContent(String filePath, String sheetName, List<List<String>> contents) {

        filePath = retriveFilePath(filePath)
        Workbook workbook = getOrCreateWorkbook(filePath)
        Sheet sheet = getOrCreateSheet(workbook,sheetName)

        int num = sheet.getLastRowNum()==0? sheet.getLastRowNum():sheet.getLastRowNum()+1

        for(int i = num; i< num+contents.size();i++) {
            def row = sheet.createRow(i)
            def content = contents.get(i-num)
            for(int j=0;j<content.size();j++) {
                row.createCell(j).setCellValue(content.get(j))
            }
        }

        FileOutputStream fos = new FileOutputStream(filePath)
        workbook.write(fos)
        fos.close()
    }





    List<Map<String,String>>  getExcelKeyAndContents(String filePath,int sheetIndex) {
        List<Map<String,String>>  results = new ArrayList<Map<String,String>>()

        filePath = retriveFilePath(filePath)

        InputStream inputStream = new FileInputStream(filePath)

        Workbook workbook

        if(filePath.endsWith(".xls")){
            workbook = new HSSFWorkbook(inputStream)
        }else {
            workbook = StreamingReader.builder()
                    .sstCacheSize(100)
                    .open(inputStream)
        }



        Sheet sheet = workbook.getSheetAt(sheetIndex)
        int rows = sheet.getLastRowNum()
        if(rows < 1) {
            return results
        }



        DataFormatter formatter = new DataFormatter()

        int i = 0
        def columnNameArr = []

        for(Row row : sheet) {
            Map<String,String> resultRowMap = new HashMap<>()
            int j = 0
            for(Cell cell : row) {
                String text = formatter.formatCellValue(cell)
                if(i==0) {
                    columnNameArr.add(text)
                }else {

                    if(j<columnNameArr.size()) {
                        def columnName = columnNameArr.get(j)
                        resultRowMap.put(columnName,text)
                    }
                    j++
                }
            }

            i++
            if(resultRowMap.size()>0) {
                results.add(resultRowMap)
            }
        }

        return results
    }




    List<List<String>> getExcelContents(String filePath,int sheetIndex) {
        List<List<String>> results = new ArrayList<List<String>>()
        filePath = retriveFilePath(filePath)

        InputStream inputStream = new FileInputStream(filePath)

        Workbook workbook

        if(filePath.endsWith(".xls")){
            workbook = new HSSFWorkbook(inputStream)
        }else {
            workbook = StreamingReader.builder()
                    .sstCacheSize(100)
                    .open(inputStream)
        }

        Sheet sheet = workbook.getSheetAt(sheetIndex)

        DataFormatter formatter = new DataFormatter()
        for(Row row : sheet) {
            List<String> resultRowList = new ArrayList<>()
            for(Cell cell : row) {
                String text = formatter.formatCellValue(cell)
                resultRowList.add(text)
            }
            results.add(resultRowList)
        }

        return results
    }


}
