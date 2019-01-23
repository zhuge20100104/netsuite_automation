package com.netsuite.base.page.steps.checkers

import com.netsuite.base.page.steps.beans.CheckerData

interface IChecker {
    void doChecker(CheckerData data)
}
