package com.netsuite.base.report.rerun.bean

class AnnotationFile {
    File file
    String annotationClass
    String annotationFullName
    String testMethodName

    String getTestMethodName() {
        return testMethodName
    }

    void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName
    }

    File getFile() {
        return file
    }

    void setFile(File file) {
        this.file = file
    }

    String getAnnotationClass() {
        return annotationClass
    }

    void setAnnotationClass(String annotationClass) {
        this.annotationClass = annotationClass
    }

    String getAnnotationFullName() {
        return annotationFullName
    }

    void setAnnotationFullName(String annotationFullName) {
        this.annotationFullName = annotationFullName
    }
}
