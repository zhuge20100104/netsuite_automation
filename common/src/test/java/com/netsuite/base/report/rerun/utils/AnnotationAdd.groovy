package com.netsuite.base.report.rerun.utils

import com.netsuite.base.report.rerun.bean.AnnotationFile

class AnnotationAdd implements IAnnotationOp{

    IAddResolver fAddResolver
    AnnotationFile mAnnotationFile

    void setAddResolver(IAddResolver addResolver) {
        this.fAddResolver = addResolver
    }

    IAddResolver getAddResolver() {
        return this.fAddResolver
    }

    void startFile(AnnotationFile file) {
        if(this.fAddResolver!=null) {
            fAddResolver.startFile(file)
        }
    }

    void startPackageLine(String line,int index){
        if(this.fAddResolver!=null) {
            fAddResolver.startPackageLine(line,index)
        }
    }


    void startCategoryLine(String line,int index) {
        if(this.fAddResolver!=null) {
            fAddResolver.startCategoryLine(line,index)
        }
    }


    void startClassLine(String line,int index) {
        if(this.fAddResolver!=null) {
            fAddResolver.startClassLine(line,index)
        }
    }

    void startMethodLine(String line,int index){
        if(this.fAddResolver!=null) {
            fAddResolver.startMethodLine(line,index)
        }
    }

    void startCommonLine(String line,int index){
        if(this.fAddResolver!=null) {
            fAddResolver.startCommonLine(line,index)
        }
    }


    void endFile(AnnotationFile file) {
        if(this.fAddResolver!=null) {
            fAddResolver.endFile(file)
        }
    }


//    public static void main(String[] args) {
//        String methodMatcher = ".*void\\s+"+"\"?"+"Case_1_36_3_4"+"\"?"+"\\(.*"
//        println("public void Case_1_36_3_4() {".matches(methodMatcher))
//    }

    @Override
    def operate(AnnotationFile annotationFile) {
        this.mAnnotationFile = annotationFile
        File file = annotationFile.getFile()
//        String methodMatcher = "\\s*void\\s*"+annotationFile.getTestMethodName()+"\\(.*"

        String methodMatcher = ".*void\\s+"+"\"?"+annotationFile.getTestMethodName()+"\"?"+"\\(.*"
        String classMatcher = ".*class\\s+.*Test.*"

        this.setAddResolver(new AddResolver())

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
        String line = null


        this.startFile(annotationFile)
        int index = 0

        while((line=br.readLine())!=null){
            if(line.indexOf("package")>-1) {
                this.startPackageLine(line,index)
            }else if(line.indexOf("org.junit.experimental.categories.Category")>-1) {
                this.startCategoryLine(line,index)
            }else if(line.matches(classMatcher)){
                this.startClassLine(line,index)
            }else if(line.matches(methodMatcher)) {
                this.startMethodLine(line,index)
            }else {
                this.startCommonLine(line,index)
            }

            index ++
        }

        this.endFile(annotationFile)
        br.close()
    }
}
