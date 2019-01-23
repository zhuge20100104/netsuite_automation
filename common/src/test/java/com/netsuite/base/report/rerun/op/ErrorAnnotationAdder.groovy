package com.netsuite.base.report.rerun.op

import com.netsuite.base.report.rerun.bean.AnnotationFile
import com.netsuite.base.report.rerun.utils.AnnotaionFactory
import com.netsuite.base.report.rerun.utils.IAnnotationOp
import com.netsuite.base.report.rerun.utils.OP

class ErrorAnnotationAdder {


    static List<AnnotationFile> getFileList(String baseDir,String fileName) {
        String errorDetailFile = baseDir + File.separator + fileName
        File f = new File(errorDetailFile)
        String  errorDetailsText  = f.getText("UTF-8")

        println("Start******")
        println(errorDetailsText)
        println("End********")
        String [] errorMethods = errorDetailsText.split(",")

        List<AnnotationFile> annotationFiles = new ArrayList<>()
        for(String errorMethodStr : errorMethods) {
            AnnotationFile annotationFile = new AnnotationFile()
            String [] errorClassAndMethod  = errorMethodStr.split("#")
            if(errorClassAndMethod.length!=2) {
                continue
            }

            String errorClass = errorClassAndMethod[0]
            String errorMethod = errorClassAndMethod[1]
            println(errorMethod)
            String errorPath = errorClass.replace(".",File.separator)

            errorPath = "src" + File.separator + "test" + File.separator + "java" + File.separator +
                    errorPath+".groovy"
            def file = new File(errorPath)
            if(!file.exists()) continue
            annotationFile.setFile(file)
            annotationFile.setAnnotationClass("NSError")
            annotationFile.setAnnotationFullName("com.netsuite.common.NSError")
            annotationFile.setTestMethodName(errorMethod)

            annotationFiles.add(annotationFile)
        }

        return annotationFiles
    }

    
    static void addErrorAnnotations(String baseDir,String fileName){
        List<AnnotationFile> annotationFiles = getFileList(baseDir,fileName)
        IAnnotationOp op = null

        for(AnnotationFile file : annotationFiles) {
            op = AnnotaionFactory.getOp(OP.ADD)
            op.operate(file)
        }
    }
}
