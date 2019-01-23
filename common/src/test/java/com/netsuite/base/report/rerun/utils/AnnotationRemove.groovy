package com.netsuite.base.report.rerun.utils

import com.netsuite.base.report.rerun.bean.AnnotationFile

class AnnotationRemove implements IAnnotationOp{
    IRemoveResolver fRemoveResolver
    AnnotationFile mAnnotationFile

    IRemoveResolver getRemoveResolver() {
        return fRemoveResolver
    }

    void setRemoveResolver(IRemoveResolver fRemoveResolver) {
        this.fRemoveResolver = fRemoveResolver
    }



    void startFile(AnnotationFile file) {
        if(fRemoveResolver != null) {
            fRemoveResolver.startFile(file)
        }
    }

    void startImportLine(String line,int index){
        if(fRemoveResolver != null) {
            fRemoveResolver.startImportLine(line,index)
        }
    }
    void startErrorLine(String line,int index){
        if(fRemoveResolver != null) {
            fRemoveResolver.startErrorLine(line,index)
        }
    }

    void startCommonLine(String line,int index) {
        if(fRemoveResolver != null) {
            fRemoveResolver.startCommonLine(line,index)
        }
    }

    void endFile(AnnotationFile file){
        if(fRemoveResolver != null) {
            fRemoveResolver.endFile(file)
        }
    }


    def isErrorAnnotationFile(AnnotationFile annotationFile) {
        boolean isErrorFile = false
        String matchString = "import(\\s)+"+ annotationFile.getAnnotationFullName()


        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(annotationFile.getFile())))
        String line = null
        while((line=br.readLine())!=null) {
            if(line.matches(matchString)) {
                isErrorFile = true
                break
            }
        }

        br.close()
        return isErrorFile
    }

    @Override
    def operate(AnnotationFile annotationFile) {
        this.mAnnotationFile = annotationFile

        if(!isErrorAnnotationFile(annotationFile)) {
            return
        }


        File file = annotationFile.getFile()
        this.setRemoveResolver(new RemoveResolver())

        String annotationCategory = annotationFile.getAnnotationClass() +".class"
        String matchString = "import(\\s)+"+ annotationFile.getAnnotationFullName()

        this.startFile(annotationFile)

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
        String line = null
        int index = 0

        while((line=br.readLine()) != null) {
            boolean isNSErrorLine = !line.trim().startsWith("//") && line.matches(".*@Category.*"+annotationCategory+".*")

            if(line.matches(matchString)) {
                startImportLine(line,index)
            }else if(isNSErrorLine) {
                startErrorLine(line,index)
            }else {
                startCommonLine(line,index)
            }

            index ++
        }

        this.endFile(annotationFile)
        br.close()
    }
}
