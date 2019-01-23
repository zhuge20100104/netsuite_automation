package com.netsuite.test

import com.netsuite.base.report.rerun.op.ErrorAnnotationAdder
import com.netsuite.common.NSError



import com.google.inject.Inject
import com.netsuite.base.BaseAppTestSuite
import com.netsuite.common.IssueTests
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.test.page.GenerateVATPage
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import org.junit.Test
import org.junit.experimental.categories.Category

class VATAppTestSuite extends BaseAppTestSuite {


    public static void main(String[] args) {
        String s ="a,b,c,d,"
        ErrorAnnotationAdder.getFileList("data/testrun/","fail_watch_file_latest.txt")
        String [] arr = s.split(",")
        println arr.length
    }


    @Inject
    GenerateVATPage vatPage

    def getDefaultRole() {
        return loginUtil.getAdministrator()
    }


    def getTestDataPrefix() {
        return "src\\test\\java\\com\\netsuite\\test\\data\\"
    }

//    @Test
    @Category([IssueTests.class,NSError.class])
    void test_pdf() {
//        List<List<String>> contentLines = pdfUtil.readPdf("src\\test\\java\\com\\netsuite\\data\\180413212207.pdf")
//        contentLines.each {
//            println "Start a page"
//            println it
//            println "End a page"
//        }


        List<List<String>> contentLines = pdfUtil.readPdfLines("src\\test\\java\\com\\netsuite\\data\\180413212207.pdf")
        contentLines.each {
            lines ->
                println "Start a page"
                lines.each {
                    line ->
                        print "Before Line---->"
                        print line
                        println "<----End line"
                }
                println "End a page"
        }
    }



    @Category([P1.class,NSError.class, OW.class])
//    @Test
    void test_excel() {
        List<List<String>> excelContents = excelUtil.getExcelContents("src\\test\\java\\com\\netsuite\\data\\1.xls", 0)
        excelContents.each {
            println "Start a row"

            it.each {
                print it
                print "  "
            }
            println ""

            println "End a row"
        }

//        List<Map<String,String>> excelContents = excelUtil.getExcelKeyAndContents("src\\test\\java\\com\\netsuite\\data\\1.xlsx",1)
//        excelContents.each {
//            println "Start a row"
//
//            it.each {
//                print it.key +"--->" + it.value
//                print "  "
//            }
//            println ""
//
//            println "End a row"
//        }
    }

    @Test
    void test_create_record() {


        login()

        def tData = cData.data("test_5_2_1")
        //get the invoice rec data
        def invRecData = tData.record().invoice
        //create an invoice rec

//        dirtyData = record.createRecord(invRecData)

        navigateToUpdateRevenueArrangementPage()


    }



    @Test
    void test_5_3_3(){

        login()
        navigateToGenerateChinaVATPage()

        HtmlElementHandle handle =context.webDriver.getHtmlElementByLocator(Locator.name("custpage_transactiontype"))

        assertTrue("Handle is not null!!!",handle !=null)
    }



//    @Test
    @Category([IssueTests.class,NSError.class])
    void test_5_2_1() {

//
//        List<List<String>> contentLines = pdfUtil.readPdfLines("src\\test\\java\\com\\netsuite\\data\\180413212207.pdf")
//        contentLines.each {
//            lines ->
//                println "Start a page"
//                lines.each {
//                    line ->
//                        print "Before Line---->"
//                        print line
//                        println "<----End line"
//                }
//                println "End a page"
//        }

//        int i = 0
//        contents.each {
//            content ->
//                i++
//                println("start content:"+i)
//                println(content)
//                println("end content:"+i)
//        }


        login()

//        navigateToUpdateRevenueArrangementPage()

//        navigateToGenerateChinaVATPage()
//        def tData = cData.data( "test_5_2_1")
//        def labels = tData.labels()
//        def expect = tData.expect()
//
//
//        vatPage.enterValueInDateFromInGenerateVAT(labels.fromDate)
//        vatPage.enterValueInDateToInGenerateVAT(labels.toDate)
//        vatPage.clickRefreshButton()
//        assertTextVisible("Start date not more than end date",expect.errorStartDateMoreThanEndDateMsg)
    }
}
