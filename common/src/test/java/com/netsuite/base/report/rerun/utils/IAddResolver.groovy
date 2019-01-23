package com.netsuite.base.report.rerun.utils

import com.netsuite.base.report.rerun.bean.AnnotationFile

interface IAddResolver {

    void startFile(AnnotationFile file)
    void startPackageLine(String line,int index)
    void startCategoryLine(String line,int index)
    void startClassLine(String line,int index)
    void startMethodLine(String line,int index)
    void startCommonLine(String line,int index)
    void endFile(AnnotationFile file)
}