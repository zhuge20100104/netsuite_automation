package com.netsuite.base.page.steps.jsteps.handles.beans

class JTableRow {
    int row
    List<JTableColumn>  columns = new ArrayList<>()

    def addColumn(JTableColumn column) {
        columns.add(column)
    }

    def getColumn(int i) {
        return columns.get(i)
    }

    def getKeyValue(String key) {
        for(JTableColumn column: columns) {
            if(column.getHeader().equals(key)) {
                return column.getValue()
            }
        }
        return null
    }

    def getGetKeyColumn(String key) {
        for(JTableColumn column: columns) {
            if(column.getHeader().equals(key)) {
                return column.getCol()
            }
        }

        return -1
    }
}
