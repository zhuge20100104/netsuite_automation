package com.netsuite.base.report.rerun

import com.netsuite.base.lib.property.PropertyUtil
import com.netsuite.base.report.InjectClass
import com.netsuite.base.report.rerun.op.ErrorAnnotationAdder
import com.netsuite.base.report.rerun.op.ErrorAnnotationRemover

class ReRun {



    static void main(String[] args) {
        String failWatchFile = ""

        if(args.length == 0) {
            failWatchFile = "fail_watch_file.txt"
        }else {
            failWatchFile = args[0]
        }

        println("Fail Watch file *****:["+failWatchFile+"]")


        PropertyUtil propertyUtil = InjectClass.getInstance(PropertyUtil.class)
        String reRunTestDir = propertyUtil.getRerunTestDir()

        String annotationFilePath = propertyUtil.getReportExcelPath()
        String errorDetailFile = annotationFilePath + File.separator + failWatchFile
        File f = new File(errorDetailFile)
        if(!f.exists()) {
            println("Error file does not exist,Do nothing! return!")
            return
        }


        ErrorAnnotationRemover.removeAllErrorAnnotations(reRunTestDir)
        ErrorAnnotationAdder.addErrorAnnotations(annotationFilePath,failWatchFile)
    }
}
