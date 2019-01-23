package com.netsuite.base.report.rerun.op

import com.netsuite.base.report.rerun.bean.AnnotationFile
import com.netsuite.base.report.rerun.file.FileOperator
import com.netsuite.base.report.rerun.utils.AnnotaionFactory
import com.netsuite.base.report.rerun.utils.IAnnotationOp
import com.netsuite.base.report.rerun.utils.OP

class ErrorAnnotationRemover {


    static List<AnnotationFile> getFileList(String baseDir) {
        List<File> files = FileOperator.listTestFiles(baseDir)
        List<AnnotationFile> annotationFiles = new ArrayList<>()
        for(File f : files) {
            AnnotationFile file = new AnnotationFile()
            file.setFile(f)
            file.setAnnotationClass("NSError")
            file.setAnnotationFullName("com.netsuite.common.NSError")
            annotationFiles.add(file)
        }

        return annotationFiles
    }


    static void removeAllErrorAnnotations(String baseDir) {
        List<AnnotationFile> annotationFiles = getFileList(baseDir)
        IAnnotationOp op = null

        for(AnnotationFile file : annotationFiles) {
            op = AnnotaionFactory.getOp(OP.REMOVE)
            op.operate(file)
        }
    }
}
