package com.netsuite.base.report

import org.junit.AssumptionViolatedException
import org.junit.experimental.categories.Category
import org.junit.rules.TestWatcher
import org.junit.runner.Description

import java.lang.annotation.Annotation

class CollectionWatcher extends TestWatcher {

    def watchFilePath
    def passWatchFileName
    FileGetter passFGetter


    def failWatchFileName
    FileGetter failFGetter


    def passDetailFileName
    FileGetter passDGetter

    def failDetailFileName
    FileGetter failDGetter

    CollectionWatcher(String watchFilePath) {
        this.watchFilePath = watchFilePath

        this.passWatchFileName = watchFilePath + "pass_watch_file.txt"
        passFGetter = new FileGetter.Builder().build(passWatchFileName)

        this.failWatchFileName = watchFilePath + "fail_watch_file.txt"
        failFGetter = new FileGetter.Builder().build(failWatchFileName)

        this.passDetailFileName = watchFilePath + "pass_detail_file.txt"
        this.passDGetter = new FileGetter.Builder().build(passDetailFileName)

        this.failDetailFileName = watchFilePath + "fail_detail_file.txt"
        this.failDGetter = new FileGetter.Builder().build(failDetailFileName)
    }

    @Override
    protected void succeeded(Description description) {
        println(description.getDisplayName() + " Succeed")




        def caseInfo = description.getTestClass().getName()+"#"+description.getMethodName()
        println(description.getTestClass().getName()+"#"+description.getMethodName())
        passDGetter.append(caseInfo).append("|")



        //Append case info to pass_watch_file.txt
        passFGetter.append(caseInfo).append(",")



        Annotation category = description.getAnnotation(Category.class)
        if(category!=null) {
            println("Start print category info")
            println(category.toString())
            passDGetter.append(CategoryParser.parseCategory(category.toString())).append("|")
            passDGetter.append("Pass")
            println("End print category info")
        }else {
            passDGetter.append("NoGroup").append("|")
            passDGetter.append("Pass")
        }

        passDGetter.append("\r\n")


    }

    @Override
    protected void failed(Throwable e, Description description) {
        println(description.getDisplayName() + " Fail")
        Annotation category = description.getAnnotation(Category.class)

        println("Start print needed info")
        println(description.getTestClass().getName()+"#"+description.getMethodName())

        def caseInfo = description.getTestClass().getName()+"#"+description.getMethodName()
        failDGetter.append(caseInfo).append("|")

        //Append case info to fail_watch_file.txt
        failFGetter.append(caseInfo).append(",")

        println("End print needed info")

        if(category!=null) {
            println("Start print category info")
            println(category.toString())

            failDGetter.append(CategoryParser.parseCategory(category.toString())).append("|")
            failDGetter.append("Fail")
            println("End print category info")
        }else {
            failDGetter.append("NoGroup").append("|")
            failDGetter.append("Fail")
        }

        failDGetter.append("\r\n")
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        println(description.getDisplayName() + " Skipped")
    }

    @Override
    protected void starting(Description description) {
        println(description.getDisplayName() + " Started")
    }

    @Override
    protected void finished(Description description) {
        println(description.getDisplayName() + " finished")
    }
}
