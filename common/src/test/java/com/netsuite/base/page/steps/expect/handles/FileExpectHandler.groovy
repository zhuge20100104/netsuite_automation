package com.netsuite.base.page.steps.expect.handles

import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.report.rerun.file.FileOperator
import org.apache.commons.lang.StringUtils
import org.junit.Assert

class FileExpectHandler {
    static void deleteFile(NetSuiteAppBase context,String dirName=null) {
        if(StringUtils.isEmpty(dirName)) {
            dirName = context.context.getProperty("testautomation.file_download_path")
        }

        FileOperator.deleteAllFiles(dirName)
    }

    static void checkReportFile(NetSuiteAppBase context,String reportFileName) {
        String reportDirName = context.context.getProperty("testautomation.file_download_path")
        boolean fileExist = FileOperator.checkFilesContainsWord(reportDirName,reportFileName)
        Assert.assertTrue("Report file doesn't exist!!",fileExist)
    }
}
