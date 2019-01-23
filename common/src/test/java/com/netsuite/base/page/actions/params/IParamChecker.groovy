package com.netsuite.base.page.actions.params

interface IParamChecker<T> {
    void check(ParamContext<T> context)
}
