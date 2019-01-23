package com.netsuite.base.excel

import org.apache.commons.io.FileUtils

/**
 * Created by stepzhou on 5/7/2018.
 */
class ParseExcel {
    private static final String DOWNLOADPATH = "data//downloads"
    private List<String> excelFileContents
    private String downloadedFile

    // Use this constructor to manually parse one Excel file.
    ParseExcel(String excelFilePath) {
        excelFileContents = parseExcelFile(new File(excelFilePath))
    }

    // Use this constructor to automatically pick up the latest one from the specified path.
    ParseExcel() {
        File latestDownloadFile = lastFileModified(DOWNLOADPATH)
        downloadedFile = latestDownloadFile.getPath()
        excelFileContents = parseExcelFile(latestDownloadFile)
    }

    String getDownloadedFile(){
        return downloadedFile
    }

    List<String> getFileContents() {
        return excelFileContents
    }

    List<String> getLinesWithKey(String key) {
        List<String> tarLines = new LinkedList<>()
        for (String line : excelFileContents) {
            if (line.contains(key))
                tarLines.add(line)
        }
        return tarLines
    }

    boolean isKeyValueCorrect(String key, String value) {
        for (String line : excelFileContents) {
            if (line.contains(key) && line.contains(value)) {
                return true
            }
        }
        return false
    }

    private static List<String> parseExcelFile(File file) {
        return FileUtils.readLines(file)
    }

    private static File lastFileModified(String dir) {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            @Override
            boolean accept(File file) {
                return file.isFile();
            }
        });

        long lastMod = Long.MIN_VALUE;
        File choice = null;
        assert files != null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }
}
