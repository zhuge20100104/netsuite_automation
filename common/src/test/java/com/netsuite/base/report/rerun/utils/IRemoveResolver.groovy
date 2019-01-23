package com.netsuite.base.report.rerun.utils

import com.netsuite.base.report.rerun.bean.AnnotationFile

interface IRemoveResolver {
    void startFile(AnnotationFile file)
    void startImportLine(String line,int index)
    void startErrorLine(String line,int index)
    void startCommonLine(String line,int index)
    void endFile(AnnotationFile file)
}