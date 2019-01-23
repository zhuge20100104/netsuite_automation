package com.netsuite.base.control.ext

import com.netsuite.testautomation.aut.GenericWebApp
import com.netsuite.testautomation.aut.pageobjects.DTOs.MultiSelectItem
import com.netsuite.testautomation.aut.pageobjects.MultiSelect
import com.netsuite.testautomation.driver.BrowserActions
import com.netsuite.testautomation.driver.WebDriver
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.DTOs.ListRow
import org.openqa.selenium.Keys

/**
 * Created by stepzhou on 2/1/2018.
 */
class MultiSelectExt extends MultiSelect {
    private Locator identityLocator = Locator.xpath("./descendant-or-self::table[@nlmultidropdown]");
    private String tableRowsXPathIteratorTemplate = "//div[@class='dropdownDiv' and @nlmultidropdown='%s']//table//tr/td/a";

    MultiSelectExt(GenericWebApp context, WebDriver webDriver, Locator widgetLocator) {
        super(context, webDriver, widgetLocator);
    }

    @Override
    void setValues(List<String> values) {
        List<MultiSelectItem> availableOptions = this.parseOptions();
        BrowserActions actions = this.webDriver.getBrowserActionsInstance();
        actions.keyDown(Keys.CONTROL);
        for (String value : values) {
            for (MultiSelectItem availableItem : availableOptions) {
                HtmlElementHandle itemElement = this.webDriver.getHtmlElementByLocator(availableItem.getRowLocator());
                if (value.equalsIgnoreCase(itemElement.getAsText())) {
                    actions.mouseDown(itemElement).mouseUp(itemElement);
                    break;
                }
            }
        }
        actions.keyUp(Keys.CONTROL).build().perform();
    }

    void setValues(String... values) {
        this.setValues(Arrays.asList(values));
    }

    private List<MultiSelectItem> parseOptions() {
        String tableRowsXPathIterator = this.getTableRowXPath();
        List<MultiSelectItem> list = new ArrayList<MultiSelectItem>();
        List<ListRow> rows = this.listParser.parseList(tableRowsXPathIterator, this.rowParser);

        for (Object row : rows) {
            list.add((MultiSelectItem) row);
        }

        return list;
    }

    private String getTableRowXPath() {
        String pairingAttributeValue = this.getContainerElement().getElementByLocator(this.identityLocator).getAttributeValue("nlmultidropdown");
        String tableRowsXPathIteratorTemplate = "//div[@class='dropdownDiv' and @nlmultidropdown='%s']//table//tr";
        return String.format(tableRowsXPathIteratorTemplate, pairingAttributeValue);
    }

    void selectItems(List<String> values) {
        BrowserActions actions = this.webDriver.getBrowserActionsInstance();
        actions.keyDown(Keys.CONTROL).build().perform();

        List<HtmlElementHandle> items = this.parseItems();
        for (String value : values) {
            for (HtmlElementHandle item : items) {
                if (value.equalsIgnoreCase(item.getAsText())) {
                    item.click();
                    break;
                }
            }
        }

        actions.keyUp(Keys.CONTROL).build().perform();
    }

    void selectItems(String... values) {
        this.selectItems(Arrays.asList(values));
    }

    private List<HtmlElementHandle> parseItems() {
        String pairingAttributeValue = this.getContainerElement().getElementByLocator(this.identityLocator).getAttributeValue("nlmultidropdown");
        String path = String.format(tableRowsXPathIteratorTemplate, pairingAttributeValue);
        return this.webDriver.getHtmlElementsByLocator(Locator.xpath(path));
    }
}

