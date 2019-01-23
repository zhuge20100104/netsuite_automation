package com.netsuite.base.report.rerun.utils

import com.netsuite.base.report.rerun.bean.AnnotationFile

interface IAnnotationOp {
    def operate(AnnotationFile annotationFile)
}
