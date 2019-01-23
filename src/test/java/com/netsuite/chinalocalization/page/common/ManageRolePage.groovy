package com.netsuite.chinalocalization.page.common

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator

/**
 * Created by stepzhou on 3/27/2018.
 */
class ManageRolePage extends PageBaseAdapterCN {
    private static String URL = "/app/setup/rolelist.nl?whence="

    void navigateToPage() {
        context.navigateTo(URL)
    }

    void editRole(role) {
        int rowCount = Integer.parseInt(context.executeScript("return document.getElementById('div__bodytab').getElementsByTagName('tr').length"))
        int targetRowIndex = 0

        for (int i = 1; i < rowCount; i++) {
            String cellContent = context.executeScript("return document.getElementById('div__bodytab').getElementsByTagName('tr')[" + i + "].getElementsByTagName('td')[1].textContent")
            if (cellContent.equalsIgnoreCase((String) role)) {
                targetRowIndex = i
                break
            }
        }

        Locator locator = Locator.xpath("//*[@id='div__bodytab']//tr[" + (targetRowIndex + 1) + "]/td[1]/a")
        context.webDriver.getHtmlElementByLocator(locator).click()
    }

    List<String> getSelectedSubsidiaries() {
        int selectedCount = Integer.parseInt(context.executeScript("return document.getElementsByClassName('dropdownSelected').length"))
        List<String> selectedSubsidiaries = new ArrayList<>()

        for (int i = 0; i < selectedCount; i++) {
            String subsidiary = context.executeScript("return document.getElementsByClassName('dropdownSelected')[" + i + "].textContent")
            selectedSubsidiaries.add(subsidiary)
        }

        return selectedSubsidiaries
    }
}