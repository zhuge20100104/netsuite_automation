package com.netsuite.chinalocalization.vat.components

import com.netsuite.testautomation.aut.GenericWebApp
import com.netsuite.testautomation.aut.pageobjects.AbstractPopupWidget
import com.netsuite.testautomation.aut.pageobjects.Interfaces.ICommonControl
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.NodeUtilities
import com.netsuite.testautomation.html.parsers.WebDocument
import com.netsuite.testautomation.junit.reporting.ReportMe
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

public class XDropdownList extends AbstractPopupWidget implements ICommonControl {
    private static final Logger log = LoggerFactory.getLogger(com.netsuite.testautomation.aut.pageobjects.DropdownList.class);
    private static XPathFactory xPathFactory = XPathFactory.newInstance();
    private static XPath xPath;
    private static final String popupContainer = "//div[@class='dropdownDiv' and contains(@style, 'visible')]/div";
    private static final Locator arrowLocator = Locator.xpath(".//img[@class='i_dropdownarrow']");
    private static final Locator textboxLocator = Locator.xpath(".//input[@id and @type='text']");
    private static final Locator actNew = Locator.xpath("//a[@id='partner_popup_new']");
    private static final Locator actOpen = Locator.xpath("//a[@id='partner_popup_link']");
    private static final String itemNew = "- New -";

    public XDropdownList(GenericWebApp context, WebDriver webDriver, String suiteScriptID) {
        super(context, webDriver, Locator.xpath(String.format("//*[@name='%s' and @id]/..//span[contains(@class,'ddarrowSpan')]/ancestor::div[1]", suiteScriptID)));
        this.setPredicateOptions(arrowLocator, (String)null);
    }

    public XDropdownList(GenericWebApp context, WebDriver webDriver, Locator parentLocator) {
        super(context, webDriver, parentLocator);
        this.setPredicateOptions(arrowLocator, (String)null);
    }

    @ReportMe
    public String getValue() {
        HtmlElementHandle parentElement = this.webDriver.getHtmlElementByLocator(this.widgetLocator);
        if (parentElement == null) {
            throw this.getExceptionFactory().createNSAppException(String.format("Element '%s' is not available!", this.widgetLocator));
        } else {
            HtmlElementHandle textbox = parentElement.getElementByLocator(textboxLocator);
            if (textbox == null) {
                throw this.getExceptionFactory().createNSAppException(String.format("Element '%s' is not available!", textboxLocator));
            } else {
                return StringUtil.trim(textbox.getAttributeValue("value"));
            }
        }
    }

    @ReportMe
    public void setValue(String value) {
        this.selectItem(value);
    }

    @ReportMe
    public void selectItem(String name) {

        def itemLowerCase = this.getValue().toLowerCase()
        def nameLowerCase = name.toLowerCase()

        if (itemLowerCase.contains(nameLowerCase)) {
            log.info(name + " already selected in dropdown " + this.widgetLocator);
            this.clickArrow(false);
        } else {
            this.clickArrow(true);
            String xpath = String.format("%s[normalize-space(text())='%s']", "//div[@class='dropdownDiv' and contains(@style, 'visible')]/div", name);
            HtmlElementHandle option = this.webDriver.getHtmlElementByLocator(Locator.xpath(xpath));
            if (option != null) {

                itemLowerCase = StringUtil.trim(option.getAsText()).toLowerCase()
                nameLowerCase = name.toLowerCase()

                if (itemLowerCase.contains(nameLowerCase)) {
                    option.scrollToView();
                    option.click(false);
                }

            } else {
                HashMap<String, Locator> items = this.parseTableRowLocator("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div");
                Iterator var5 = items.keySet().iterator();

                while(var5.hasNext()) {
                    String item = (String)var5.next();

                    itemLowerCase = item.toLowerCase()
                    nameLowerCase = name.toLowerCase()

                    if (itemLowerCase.contains(nameLowerCase)) {
                        if (name.equalsIgnoreCase("- New -")) {
                            this.webDriver.click((Locator)items.get(item));
                            this.webDriver.waitForPageToLoad();
                        } else {
                            this.webDriver.click((Locator)items.get(item));
                        }
                        break;
                    }
                }
            }

        }
    }

    @ReportMe
    public void selectItem(int itemIndex) {
        this.clickArrow(true);
        List<Locator> items = this.parseTableRowOccurrence("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div");
        this.webDriver.click((Locator)items.get(itemIndex));
    }

    @ReportMe
    public void selectNewFromDropdownMenu() {
        this.selectItem("- New -");
    }

    @ReportMe
    public List<String> getOptions() {
        this.clickArrow(true);
        List<String> items = new ArrayList();
        LinkedHashMap<String, Locator> options = this.parseTableRowLocator("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div");
        Iterator var3 = options.keySet().iterator();

        while(var3.hasNext()) {
            String option = (String)var3.next();
            items.add(option);
        }

        this.clickArrow(false);
        return items;
    }

    public void clear() {
    }

    public void blur() {
    }

    void clickArrow() {
        this.clickArrow(true);
    }

    void clickArrow(boolean expectPopup) {
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

    public void waitForOptionsToDisappear() {
        try {
            this.webDriver.waitForHtmlElement(Locator.xpath("//div[@class='dropdownDiv' and contains(@style, 'visible')]/div"), 5000, false);
        } catch (SystemException var2) {
            log.warn("Element with locator [//div[@class='dropdownDiv' and contains(@style, 'visible')]/div] is still visible.");
        }

    }

    private LinkedHashMap<String, Locator> parseTableRowLocator(String tableRowsXPathIterator) {
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

    private List<Locator> parseTableRowOccurrence(String tableRowsXPathIterator) {
        ArrayList rows = new ArrayList();

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
                        rows.add(Locator.xpath(String.format("%s[%d]", tableRowsXPathIterator, i + 1)));
                    }
                }
            }

            return rows;
        } catch (Exception var10) {
            throw new SystemException(var10.getMessage() + StringUtil.getStackTrace(var10));
        }
    }

    static {
        xPath = xPathFactory.newXPath();
    }
}