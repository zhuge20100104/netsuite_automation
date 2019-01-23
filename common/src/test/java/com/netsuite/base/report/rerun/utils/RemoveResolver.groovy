package com.netsuite.base.report.rerun.utils

import com.netsuite.base.report.rerun.bean.AnnotationFile
import com.netsuite.base.report.rerun.file.FileOperator

import java.util.regex.Matcher
import java.util.regex.Pattern

class RemoveResolver implements IRemoveResolver{

    AnnotationFile mAnnotationFile
    List<String> dstFileList = new ArrayList<>()

    @Override
    void startFile(AnnotationFile file) {
        println("Start Remove file: " + file.getFile().getAbsolutePath())
        this.mAnnotationFile = file
    }

    @Override
    void startImportLine(String line, int index) {
        //Do nothing here, do not need the import line in the new file
    }


    def getCommaAndClass(String originLine,String errorClass) {
        Pattern p=Pattern.compile(".*@Category.*(,\\s*"+errorClass+".class).*")
        Matcher m=p.matcher(originLine)
        while(m.find()){
            return m.group(1)
        }

        return ""
    }


    def getAppearNumber(String srcText, String findText) {
        int count = 0
        Pattern p = Pattern.compile(findText)
        Matcher m = p.matcher(srcText)
        while (m.find()) {
            count++
        }

        return count
    }

    @Override
    void startErrorLine(String line, int index) {
        if(line.indexOf("[") != -1) {
            // Multiple classes @Category([OW.class, SI.class, NSError.class])
            String commaAndClass = getCommaAndClass(line,mAnnotationFile.getAnnotationClass())
            String newLine = line.replace(commaAndClass,"")
            int classNum = getAppearNumber(newLine,".class")
            //@Category([OW.class]) , Only one class ,should remove brackets
            if(classNum == 1) {
                newLine = newLine.replace("[","")
                newLine = newLine.replace("]","")
            }

            dstFileList.add(newLine)
        }
    }

    @Override
    void startCommonLine(String line, int index) {
        dstFileList.add(line)
    }

    @Override
    void endFile(AnnotationFile file) {
        println("End Remove file: " + file.getFile().getAbsolutePath())
        FileOperator.writeToFile(file.getFile(),dstFileList)
    }
}
