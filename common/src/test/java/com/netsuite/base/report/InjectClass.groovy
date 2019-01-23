package com.netsuite.base.report

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module

class InjectClass {
     static  <T> T getInstance(Class<T> tClass) {
        Injector inject = Guice.createInjector(new Module(){
            @Override
            public void configure(Binder arg0) {
            }
        })
        return inject.getInstance(tClass)
    }
}