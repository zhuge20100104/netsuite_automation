package com.netsuite.base.report.rerun.file

class FileOperator {

    static List<File> listTestFiles(String baseDir) {
        File  baseDirFile = new File(baseDir)

        List<File> testFiles = new ArrayList<>()

        baseDirFile.eachFileRecurse {
            if(it.isFile() && it.getName().endsWith("Test.groovy")) {
                testFiles.add(it)
            }
        }

        for(File f: testFiles) {
            println(f.getAbsolutePath())
        }

        return testFiles
    }

    static List<File> listAllFiles(String baseDir) {
        File  baseDirFile = new File(baseDir)

        List<File> allFiles = new ArrayList<>()

        baseDirFile.eachFileRecurse {
            if(it.isFile()) {
                allFiles.add(it)
            }
        }

        for(File f: allFiles) {
            println(f.getAbsolutePath())
        }

        return allFiles
    }


    static boolean deleteAllFiles(String baseDir) {
        try {
            List<File> allFiles = listAllFiles(baseDir)
            for (File f : allFiles) {
                f.delete()
            }
            return true
        }catch(Exception ex) {
            return false
        }
    }



   static boolean checkFilesContainsWord(String baseDir,String word) {
        try {
            List<File> allFiles = listAllFiles(baseDir)
            for (File f : allFiles) {
                if(f.getName().contains(word)) {
                    return true
                }
            }
            return false
        }catch(Exception ex) {
            return false
        }
    }

    public static void main(String[] args) {
        println checkFilesContainsWord("uitest/data/downloads","AccountingVoucher" )
    }

    static void writeToFile(File file,List<String> contents) {
        FileWriter fileWriter =new FileWriter(file)
        fileWriter.write("")
        for(String line: contents) {
            fileWriter.write(line)
            fileWriter.write("\r\n")
        }
        fileWriter.flush()
        fileWriter.close()
    }

}
