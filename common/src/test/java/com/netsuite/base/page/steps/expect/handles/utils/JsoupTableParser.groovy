package com.netsuite.base.page.steps.expect.handles.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class JsoupTableParser {

    static List<Map<String,String>> parseTable(String tableId,String htmlStr,String headerTrClass, String tableTrClass) {
        List<Map<String,String>> tableData = new ArrayList<>();
        List<String> headers = new ArrayList<>();

        Document document = Jsoup.parse(htmlStr);
        Elements tblElements  = document.select("#"+tableId);
        Elements thrElements = tblElements.get(0).select(headerTrClass);
        Elements thElements = thrElements.get(0).select("td");

        for(Element th:thElements) {
            String header = th.text().replace(" "," ").trim().toLowerCase()
            headers.add(header);
        }

        Elements trElements = tblElements.get(0).select(tableTrClass);

        for(Element tr: trElements) {
            Elements tdElements = tr.select("td");
            HashMap<String,String> aLine = new HashMap<>();
            for(int i=0;i<tdElements.size();i++) {
                String value = tdElements.get(i).text().replace(" "," ").trim()
                aLine.put(headers.get(i),value);
            }
            tableData.add(aLine);
        }

        return tableData;
    }

}
