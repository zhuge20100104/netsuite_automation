package com.netsuite.chinalocalization.balancesheet

import com.netsuite.base.pdf.ParsePDF
import com.netsuite.chinalocalization.page.BalanceSheetPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.common.CN
import com.netsuite.common.OW
import com.netsuite.common.P1
import com.netsuite.common.P2
import com.netsuite.common.P3
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.junit.TestOwner
import org.junit.Test
import org.junit.experimental.categories.Category

import javax.inject.Inject

/**
 * @Author: qin.w.wang@oracle.com
 * @Date: 2018/5/23
 * @Description:
 */
@TestOwner ("qin.w.wang@oracle.com")
class BalanceSheetCheckReportPdfTest extends BalanceSheetAppTestSuite{
    @Inject
    private BalanceSheetPage bsPage
    @Inject
    private SubsidiaryPage subsidiaryPage

    //@Override
    //def getDefaultRole() {
    //    return getAdministrator() //getAccountant()
    //}

    /**
     * @Description
     *   Check PDF Report Table Layout
     */
    @Test
    @Category([OW.class, P3.class, CN.class])
    void test_case_4_3_1_2_1(){
        def caseData = data.test_case_4_3_1_2_1
        def caseDataFilter = caseData.filter
        // if exist, delete the old file
        def pdfName= "data\\downloads\\${caseData.expected.filename}".replace("\\",File.separator)
        cleanDownloadFolder()
        if(!context.isOneWorld()){
            return
        }
        navigateToPortalPage()
        //set up filter
        setBsFilter(caseDataFilter)

        //click the ok button in Error dialog
        okButtonInAlertMessage().click()
        waitForPageToLoad()


        //click Expert Excel button
        waitForElement(bsPage.XPATH_BTN_EXPORT_PDF)
        bsPage.exportPDF()

        assertFileExist("Shoud generate a new pdf file",pdfName)

        //ParsePDF pdfPaser = new ParsePDF(pdfName)
        ParsePDF pdfPaser = new ParsePDF()
        def pdfcontent = pdfPaser.pdfFileContents[0]
        def updateContent = PdfPaserUpdate(pdfcontent)
        checkPdfContents(caseData.expected,updateContent )
    }

    /**
     * @Description
     *  Check Exported PDF file when Unit = Ten Thousand
     */
    @Test
    @Category([OW.class, P3.class, CN.class])
    void test_case_6_3_1_2_2(){
        if(!context.isOneWorld()){
            return
        }
        def caseData = data.test_case_6_3_1_2_2
        def caseDataFilter = caseData.filter

        // if exist, delete the old file
        def pdfName= "data\\downloads\\${caseData.expected.filename}".replace("\\",File.separator)
        //if (new File(pdfName).exists()) new File(pdfName).delete()
        cleanDownloadFolder()
        navigateToPortalPage()
        //set up filter
        setBsFilter(caseDataFilter)

        // if exist, delete the old file
        //click Expert Excel button
        waitForElement(bsPage.XPATH_BTN_EXPORT_PDF)
        bsPage.exportPDF()

        assertFileExist("Shoud generate a new pdf file",pdfName)
        //ParsePDF pdfPaser = new ParsePDF(pdfName)
        ParsePDF pdfPaser = new ParsePDF()
        def pdfcontent = pdfPaser.pdfFileContents[0]
        def updateContent = PdfPaserUpdate(pdfcontent)
        //printlist(updateContent)
        // Checking the PDF content
//        2018/8/26 the expected data should be got from saved report, inseat of given data in json. need to update
//        checkPdfContents(caseData.expected,updateContent )
        //context.webDriver.closeBrowser()
    }

    void cleanDownloadFolder(){
        def DowanlodFolder= "data\\downloads\\"
        File f = new File(DowanlodFolder)
        File[] files = f.listFiles()
        for (file in files) {
           file.delete()
        }
    }

    void setBsFilter(caseFilterData){

        bsPage.fillBalanceSheetName(caseFilterData.reportName);
        subsidiaryField().selectItem(caseFilterData.subsidiary)
        asofField().selectItem(caseFilterData.asof)
        unitField().selectItem(caseFilterData.unit)
        clickRefresh()
        waitForPageToLoad()
    }

    /**
     * This method is to check the pdf pasered contect is same with expected.
     * @param expetedData: The list of expected data
     * @param PdfContents: from PdfPaserUpdate()
     */
    void checkPdfContents(expetedData, PdfContents){
        def expectTableData = expetedData.tabledata
        def ReportTableList = PdfContents[4..-1]
        // Checking the report header
        checkPdfList(expetedData.reportheader, PdfContents[0..2])

        // Checking the report table header
        checkPdfLine(PdfContents[3],expetedData.tableheader)

        println(ReportTableList.size())
        println(expectTableData.size())
        assertTrue("Checking pdf paser table lines: ", ReportTableList.size()==expectTableData.size())
        checkPdfList(expetedData.tabledata, PdfContents[4..-1])

    }

    /**
     * This method is to checking pdf line list
     * @param expectlist
     * @param pdfinfolist
     */
    void checkPdfList(expectlist, pdfinfolist){
        pdfinfolist.eachWithIndex {def entry, int i ->
            //println(expectlist[i])
            //println(entry)
            checkPdfLine(entry, expectlist[i])
        }
    }

    /**
     * This method is to checking one pdf line
     * @param expect
     * @param pdfinfo
     */
    void checkPdfLine(expect, pdfinfo){
        def pdfLineUpdate = getNewList(pdfinfo)
        pdfLineUpdate.each { value ->
            //println(":${value}:")
            if (expect.contains(value)) {
                checkTrue("success", true)
            } else {
                def tmpexpect= ""
                expect.each{v ->
                    tmpexpect += v
                }
                println(tmpexpect)
                checkTrue("Checking pdf content: " + value[1..-1], tmpexpect.contains(value[1..-1]))
            }
        }
    }


    /**
     * This method is to transfer the content of the pdf paser as below:
     * from raw parsed list into another list which mix the swap content and put the content into the end of the line list.
     *
     * Note:
     */
    def PdfPaserUpdate(content){
        def newlist = []
        def linelist = []
        def updatecontent = []
        for (line in content){
            if (line ==""){
                linelist = []
            }else{
                linelist.add(line)
                newlist.add(linelist)
            }
        }
        def newlist2 = newlist.unique()
        newlist2.eachWithIndex { entry, i ->
            def updatedline = getUpdatelist(entry)
            updatecontent.add(updatedline)
        }
        return updatecontent
    }

    /**
     * This method is to transfer the content list.
     * Note:
     * 1. When meet content.size() > 1, (meet wrap line), will add a space " " to connect the wrap line when language is eng.
     * 2. "25" in below is the estimate max size of one cell. you may need to update it.
     * listMainTmp = line.toString().split(" {2,25}")
     *
     * @param content
     * @return
     */
    def getUpdatelist(content){
        if (content.size() > 1){
            def formlist = []
            content.each{ line ->
                def listMainTmp = line.toString().split(" {2,25}")
                formlist.add(listMainTmp)
            }
            def mixlist = []
            def templist0 = getNewList(formlist[0])
            def templist2 = getNewList(formlist[2])
            def templist = [templist0, templist2]
            for ( def i=0; i < templist0.size(); i++){
                def mixline = ""
                templist.each {line ->
                    if (isTargetLanguageChinese()){
                        mixline = mixline.trim() + line[i].trim()
                    }else{
                        mixline = mixline.trim() + " " + line[i].trim()
                    }
                }
                mixlist.add(mixline)
            }
            return formlist[1] + mixlist
        }else{
            def listTmp = content[0].toString().split(" {2,25}")
            return listTmp
        }
    }

    /**
     * This method is to remove the "" in list.
     * @param list
     * @return
     */
    def getNewList(list) {
        def updatelist = []
        for (i in list) {
            if (i != "") {
                if (i == "()"){
                    updatelist.add(i.toString())
                }else {
                    updatelist.add(i.toString().trim())
                }
            }
        }
        return updatelist
    }

    void  printlist(lists){
        for (l in lists) {
            //println(l.size())
            println(l)
        }
    }


    def pathToTestDataFiles() {
        return [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\zh_CN\\BalanceSheetCheckReportPdfTest_zh_CN.json".replace("\\",SEP),
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\balancesheet\\data\\en_US\\BalanceSheetCheckReportPdfTest_en_US.json".replace("\\",SEP)
        ]
    }
}
