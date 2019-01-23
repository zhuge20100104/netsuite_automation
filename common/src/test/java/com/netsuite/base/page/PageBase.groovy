package com.netsuite.base.page

import com.google.inject.Inject
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser
import net.qaautomation.common.HumanReadableDuration

/**
 * Created by stepzhou on 2/7/2018.
 * Move the common methods to this class.
 * The common means: It's not related with India/China teams, not related with specified features.
 * */
abstract class PageBase {

    @Inject
    NetSuiteAppBase context

    String pageURL

    def getContext() {
        return context
    }

    def navigateToPage(){
        context.navigateTo(pageURL)
    }

    def resolveSuiteletURL(scriptId, deploymentId) {
        return getContext().executeScript("return nlapiResolveURL('SUITELET', '" + scriptId + "', '" + deploymentId + "');")
    }

    def resolveRestletURL(scriptId, deploymentId) {
        return getContext().executeScript("return nlapiResolveURL('RESTLET', '" + scriptId + "', '" + deploymentId + "');")
    }

    def asElement(expression) {
        return getContext().withinHtmlElementIdentifiedBy(Locator.xpath(expression))
    }

    def asElements(expression) {
        return getContext().webDriver.getHtmlElementsByLocator(Locator.xpath(expression))
    }

    def asText(expression) {
        return asElement(expression).getAsText() // see context.getTextIdentifiedBy
    }

    def asFieldText(fieldId) {
        return getContext().getFieldText(fieldId)
    }

    def asLabel(fieldId) {
        return getContext().getFieldLabel(fieldId)
    }

    def asClick(expression) {
        asElement(expression).click()
    }

    def asDropdownList(options) {
        if (options.fieldId) {
            return getContext().withinDropdownlist(options.fieldId)
        } else if (options.locator) {
            return new DropdownList(getContext(), getContext().webDriver, Locator.xpath(options.locator))
        }
    }

    def asMultiSelectField(fieldId) {
        return getContext().withinMultiSelectField(fieldId)
    }

    def asScrollToView(expression) {
        asElement(expression).scrollToView()
    }

    def asAttributeValue(expression, attributeName) {
        asElement(expression).getAttributeValue(attributeName)
    }

    def asSublist(sublistId) {
        return getContext().withinEditMachine(sublistId)
    }

    def asTable() {
        return new TableParser(getContext().webDriver)
    }

    def waitForElementToDisappear(expression, timeout = "3 min") {
        getContext().waitForElementIdentifiedByToDisappear(Locator.xpath(expression), HumanReadableDuration.parse(timeout))
    }

    def waitForElement(expression) {
        getContext().waitForElementIdentifiedBy(Locator.xpath(expression))
    }

    def waitForPageToLoad() {
        getContext().waitForPageToLoad()
    }

    def rejectUpcomingConfirmationDialog() {
        getContext().webDriver.rejectUpcomingConfirmationDialog()
    }

    def acceptUpcomingConfirmationDialog() {
        getContext().webDriver.acceptUpcomingConfirmationDialog()
    }

    def ignoreUpcomingConfirmationDialog() {
        getContext().webDriver.ignoreUpcomingConfirmationDialog()
    }

}