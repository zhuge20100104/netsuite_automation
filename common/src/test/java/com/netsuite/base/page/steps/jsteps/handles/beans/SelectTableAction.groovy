package com.netsuite.base.page.steps.jsteps.handles.beans

import com.netsuite.testautomation.html.HtmlElementHandle

class SelectTableAction {
    HtmlElementHandle clickEle

    SelectTableAction(HtmlElementHandle clickEle) {
        this.clickEle = clickEle
    }

    def selectRow() {
        clickEle.click()
    }
}
