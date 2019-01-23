package com.netsuite.base.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFTextStripper
import org.faceless.pdf2.PDF
import org.faceless.pdf2.PDFParser
import org.faceless.pdf2.PDFReader
import org.faceless.pdf2.PageExtractor

import java.nio.charset.Charset

class PdfUtil {

    String retriveFilePath(String filePath) {
        if(filePath.contains("\\")) {
            filePath = filePath.replace("\\",File.separator)
        }

        if(filePath.contains("//")) {
            filePath = filePath.replace("//",File.separator)
        }
        return filePath
    }

    /**
     * Read pdf content, returns a list of strings,each string represent a page content
     * @param filePath
     * @return
     */
    List<String> readPdf(String filePath) {

        List<String> contents = new ArrayList<>()

        filePath = retriveFilePath(filePath)


        PDF pdf = new PDF(new PDFReader(new File(filePath)))
        PDFParser parser = new PDFParser(pdf)

        for (int i=0;i<pdf.getNumberOfPages();i++) {
            PageExtractor extractor = parser.getPageExtractor(i)
            String currentPageText = extractor.getTextAsStringBuffer().toString()
            contents.add(currentPageText)
        }

        return contents
    }


    List<String> getContentLines(String content) {
        List<String> ret = new ArrayList<>()
        BufferedReader bre = null
        String str = ""
        bre = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")))
        while ((str = bre.readLine())!= null) {
            ret.add(str)
        }
        return ret
    }

    /**
     * Read pdf content to lines, the outer list represents a page content list,such as the 1st page content, the 2nd page content ... etc
     * the inner list represents all of the lines in the current page, each string represents a line
     * @param filePath
     * @return
     */
    List<List<String>> readPdfLines(String filePath) {


        List<List<String>> listContentLines = new ArrayList<ArrayList<String>>()

        filePath = retriveFilePath(filePath)


        PDF pdf = new PDF(new PDFReader(new File(filePath)))
        PDFParser parser = new PDFParser(pdf)

        for (int i=0;i<pdf.getNumberOfPages();i++) {
            PageExtractor extractor = parser.getPageExtractor(i)
            String currentPageText = extractor.getTextAsStringBuffer().toString()
            List<String> contentLines = getContentLines(currentPageText)
            listContentLines.add(contentLines)
        }
        return listContentLines
    }
}
