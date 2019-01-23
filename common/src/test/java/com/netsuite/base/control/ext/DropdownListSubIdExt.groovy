package com.netsuite.base.control.ext

import com.netsuite.testautomation.aut.GenericWebApp
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.NodeUtilities
import com.netsuite.testautomation.html.parsers.WebDocument
import com.netsuite.testautomation.utils.StringUtil
import net.qaautomation.exceptions.SystemException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.w3c.dom.NodeList

import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

public class DropdownListSubIdExt extends DropdownList {
    private static final Logger log = LoggerFactory.getLogger(DropdownList.class);
    private static final Locator arrowLocator = Locator.xpath(".//img[@class='i_dropdownarrow']");
    private static XPathFactory xPathFactory = XPathFactory.newInstance();
    private static XPath xPath;
    private sublistId
    private fieldId

    public DropdownListSubIdExt(GenericWebApp context, WebDriver webDriver, String suiteScriptID) {
        super(context, webDriver, suiteScriptID);
    }
    public DropdownListSubIdExt(GenericWebApp context, WebDriver webDriver, String sublistId, String fieldId) {
        super(context, webDriver, Locator.xpath(String.format("//form[@id='%s_form']//*[@name='%s' and @id]/..//span[contains(@class,'ddarrowSpan')]/ancestor::div[1]",sublistId,fieldId)))
        this.sublistId=sublistId;
        this.fieldId=fieldId;
    }


    public DropdownListSubIdExt(GenericWebApp context, WebDriver webDriver, Locator parentLocator) {
        super(context, webDriver, parentLocator);
    }

    public List<String> getDisplayedOptions() {
        this.clickArrow(true);
        List<String> items = new ArrayList();
        LinkedHashMap<String, Locator> options = this.parseTableRowLocator("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div[contains(@style, 'display: block')]");
        Iterator var3 = options.keySet().iterator();

        while(var3.hasNext()) {
            String option = (String)var3.next();
            items.add(option);
        }

        this.clickArrow(false);
        return items;
    }
    public List<String> getSublistFieldDisplayedOptions() {
        if(sublistId){
            String columnHeader=this.webDriver.executeScript("return (nlapiGetLineItemField('" + sublistId +"','" +fieldId+"',0).getLabel());");
            List<HtmlElementHandle> elements= this.webDriver.getHtmlElementsByLocator(Locator.xpath(String.format("//table[@id='%s_splits']//tr[contains(@class,'uir-machine-headerrow')]/td",sublistId)));
            int postion=-1;
            for(int i=0;i<elements.size();i++){
                if(elements.get(i).getAsText().toUpperCase()==columnHeader.toUpperCase()){
                    postion= i+1;
                    break;
                }
            }
            if(postion==-1){
                return null;
            }
            this.webDriver.getHtmlElementByLocator(Locator.xpath(String.format("//table[@id='%s_splits']//tr[contains(@class,'uir-machine-row-focused')]/td[%d]",sublistId,postion))).click()
        }
        this.clickArrow(true);
        List<String> items = new ArrayList();
        LinkedHashMap<String, Locator> options = this.parseTableRowLocator("//div[@class='dropdownDiv' and contains(@style, 'block')]/div[not(@style) or contains(@style, 'block')]");
        Iterator var3 = options.keySet().iterator();

        while(var3.hasNext()) {
            String option = (String)var3.next();
            items.add(option);
        }

        this.clickArrow(false);
        return items;
    }

    public void clickArrow(boolean expectPopup) {
        HtmlElementHandle parentElement = this.webDriver.getHtmlElementByLocator(this.widgetLocator);
        if (parentElement == null) {
            throw this.getExceptionFactory().createNSAppException(String.format("Element '%s' is not available!", this.widgetLocator));
        } else {
            HtmlElementHandle arrowElement = parentElement.getElementByLocator(arrowLocator);
            if (arrowElement == null) {
                throw this.getExceptionFactory().createNSAppException(String.format("Arrow element '%s' is not available!", arrowLocator));
            } else {
                HtmlElementHandle popup = this.webDriver.getHtmlElementByLocator(Locator.xpath("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div"));
                int retryCount;
                if (expectPopup) {
                    for(retryCount = 3; popup == null && retryCount > 0; --retryCount) {
                        arrowElement.click(false);

                        try {
                            popup = this.webDriver.getHtmlElementByLocator(Locator.xpath("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div"));
                            if (popup == null) {
                                popup = this.webDriver.waitForHtmlElement(Locator.xpath("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div"), 2000, true);
                            }
                        } catch (SystemException var8) {
                            log.warn("Element with locator [//div[@class='dropdownDiv' and contains(@style, 'visible')]/div] is not visible.");
                        }
                    }
                } else {
                    for(retryCount = 3; popup != null && retryCount > 0; --retryCount) {
                        arrowElement.click(false);

                        try {
                            this.webDriver.waitForHtmlElement(Locator.xpath("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div"), 2000, false);
                            popup = null;
                        } catch (SystemException var7) {
                            log.warn("Element with locator [//div[@class='dropdownDiv' and contains(@style, 'visible')]/div] is still visible.");
                        }
                    }
                }

            }
        }
    }

    public LinkedHashMap<String, Locator> parseTableRowLocator(String tableRowsXPathIterator) {
        LinkedHashMap rows = new LinkedHashMap();

        try {
            String text = this.webDriver.getPageAsXml();
            WebDocument webDocument = new WebDocument(this.webDriver);
            Document domDocument = webDocument.readXmlSource(text);
            XPathExpression labelsXPath = xPath.compile(tableRowsXPathIterator);
            NodeList nodeList = (NodeList)labelsXPath.evaluate(domDocument, XPathConstants.NODESET);

            for(int i = 0; i < nodeList.getLength(); ++i) {
                if (nodeList.item(i).getNodeType() == 1) {
                    NodeList childs = nodeList.item(i).getChildNodes();
                    if (childs != null && nodeList.item(i).getNodeType() == 1) {
                        String label = StringUtil.trim(NodeUtilities.getText(nodeList.item(i)));
                        rows.put(label, Locator.xpath(String.format("%s[%d]", tableRowsXPathIterator, i + 1)));
                    }
                }
            }

            return rows;
        } catch (Exception var11) {
            throw new SystemException(var11.getMessage() + StringUtil.getStackTrace(var11));
        }
    }

    static {
        xPath = xPathFactory.newXPath();
    }
}
