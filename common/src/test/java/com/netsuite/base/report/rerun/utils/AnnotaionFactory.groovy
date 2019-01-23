package com.netsuite.base.report.rerun.utils

enum OP {
    ADD,
    REMOVE
}

class AnnotaionFactory {
    static IAnnotationOp getOp(OP op) {
        IAnnotationOp annotationOp = null
        switch (op) {
                case OP.ADD:
                    annotationOp = new AnnotationAdd()
                    break
                case OP.REMOVE:
                    annotationOp = new AnnotationRemove()
                    break
                default:
                    throw new Exception("Not supported operate method for annotation,please check it!!")
                    break

        }
        return annotationOp
    }
}
