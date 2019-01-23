package com.netsuite.base.report.rerun.utils

import com.netsuite.base.report.rerun.bean.AnnotationFile
import com.netsuite.base.report.rerun.file.FileOperator

import java.util.regex.Matcher
import java.util.regex.Pattern

class AddResolver implements IAddResolver {


    List<String> srcFileList = new ArrayList<>()
    List<String> dstFileList = new ArrayList<>()
    int deltaIndex = 0
    int importPackageLineIndex = 0
    AnnotationFile mAnnotationFile
    boolean isCategoryMatched = false

    @Override
    void startFile(AnnotationFile file) {
        println("Start parse file: " + file.getFile().getAbsolutePath())
        this.mAnnotationFile = file
    }

    @Override
    void startPackageLine(String line,int index) {
        //Remember the import package line index
        importPackageLineIndex = index

        srcFileList.add(line)
        dstFileList.add(line)
    }

    @Override
    void startCategoryLine(String line, int index) {
        this.isCategoryMatched = true
        srcFileList.add(line)
        dstFileList.add(line)
    }

    @Override
    void startClassLine(String line, int index) {
        // Not import junit category class, add import for category class
        if(!isCategoryMatched) {
            dstFileList.add("import org.junit.experimental.categories.Category")
            deltaIndex++
        }

        srcFileList.add(line)
        dstFileList.add(line)
    }

    def getCategoryLineIndex(List<String> lines, int index) {
        String categoryMatchStr = "\\s*@Category\\(.*"
        if(lines[index-1].matches(categoryMatchStr)){
            return index-1
        }else if(lines[index-2].matches(categoryMatchStr)) {
            return index-2
        }else if(lines[index-3].matches(categoryMatchStr)) {
            return index-3
        }

        return -1
    }


    def addErrorClassInArrayCategory(String categoryLine, String annotationClassName) {
        String [] categoryLineArr = categoryLine.split("\\,")
        String retCategoryStr = ""
        for(int i = 0;i<categoryLineArr.length;i++) {
            if(i!=categoryLineArr.length-1) {
                retCategoryStr+=categoryLineArr[i]+","
            }else {
                retCategoryStr+=annotationClassName+".class,"
                retCategoryStr+=categoryLineArr[i]
            }
        }
        return  retCategoryStr
    }

   def addErrorClassInCategory(String categoryLine, String annotationClassName) {
        Pattern p=Pattern.compile("(.*@Category)\\((.*[class]?)\\).*")
//        Pattern p=Pattern.compile("(.*@Category)\\((.*class).*")
        Matcher m=p.matcher(categoryLine)
        String categoryPrefix = ""
        String firstClassName = ""
        if(m.find()) {
            categoryPrefix = m.group(1)
            firstClassName = m.group(2)
        }

        return categoryPrefix +"([" + firstClassName +"," + annotationClassName +".class])"
    }
    
    boolean isAnnotationClassInCategory(String categoryLine,String annotationClassName) {
        return categoryLine.contains(annotationClassName)
    }


    @Override
    void startMethodLine(String line,int index) {

        int categoryIndex = getCategoryLineIndex(srcFileList,index)


        if(categoryIndex != -1) {
            String categoryLine = srcFileList[categoryIndex]

            if(!isAnnotationClassInCategory(categoryLine,mAnnotationFile.getAnnotationClass())) {
                if (categoryLine.indexOf("[") != -1) {
                    dstFileList.set(categoryIndex + deltaIndex, addErrorClassInArrayCategory(categoryLine, mAnnotationFile.getAnnotationClass()))
                } else {
                    dstFileList.set(categoryIndex + deltaIndex, addErrorClassInCategory(categoryLine, mAnnotationFile.getAnnotationClass()))
                }
            }

        }else {
            String addCategoryStr = "    @Category("+mAnnotationFile.getAnnotationClass()+".class)"
            dstFileList.add(addCategoryStr)
            deltaIndex ++
        }

        srcFileList.add(line)
        dstFileList.add(line)
    }

    @Override
    void startCommonLine(String line,int index) {
        String packageImportStr = "import " + mAnnotationFile.getAnnotationFullName()

        if(index == importPackageLineIndex+1 && !line.contains(packageImportStr)) {
            dstFileList.add(packageImportStr)
            deltaIndex ++
        }

        srcFileList.add(line)
        dstFileList.add(line)
    }

    @Override
    void endFile(AnnotationFile file) {
        println("End parse file: " + file.getFile().getAbsolutePath())
        FileOperator.writeToFile(file.getFile(),dstFileList)
    }
}
