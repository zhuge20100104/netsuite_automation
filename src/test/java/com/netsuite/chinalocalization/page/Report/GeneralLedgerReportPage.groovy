package com.netsuite.chinalocalization.page.Report

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.aut.pageobjects.Report
import com.netsuite.testautomation.aut.pageobjects.reporting.FilterOperator
import com.netsuite.testautomation.aut.pageobjects.reporting.ReportCustomFilter
import com.netsuite.testautomation.html.HtmlElementHandle
import com.netsuite.testautomation.html.Locator
import org.junit.Assert
import java.util.regex.Matcher
import java.util.regex.Pattern

class GeneralLedgerReportPage extends PageBaseAdapterCN {

	/**
	 * Due to the bug in testautomation framework, getReportData only works in English language.
	 * So, if your case need to run in Chinese, please change the language preference to English first.
	 */
	def getReportData(def reportObj) {
		String reportName = reportObj.name;
		String id = context.getSavedReportId(reportName);
		Assert.assertNotNull(reportName + " does not exist", id);
		context.navigateTo("/app/reporting/reportrunner.nl?cr=" + id + "&reload=T&whence=");

		Report report = new Report(context, context.webDriver)
		report.footer.resetFilters()

		reportObj.standardFilters.each { standardFilter ->
			if (report.footer.getFilterValue(standardFilter.label)) {
				if (standardFilter.label == "As of") {
					def dropdownList = new DropdownList(getContext(), getContext().webDriver, Locator.xpath("//*[@id=\"crit_3_to_fs\"]"))
					dropdownList.selectItem(0)
					dropdownList.selectItem((String) standardFilter.text)
				} else {
					report.footer.setFilterValue(standardFilter.label, standardFilter.text)
				}
			}
		}

		reportObj.customFilters.each { customFilter ->
			def filterOperator = FilterOperator.valueOf(customFilter.filterOperator)
			if (FilterOperator.IN.equals(filterOperator)) {
				def multiSelectCustomFilter = report.footer.getCustomFilter(customFilter.label, filterOperator)
				this.setCustomMultiSelectFilterWithValue(multiSelectCustomFilter, customFilter.text)
			}
		}
		report.refresh()

		List reportDataList = context.webDriver.getHtmlElementsByLocator(Locator.xpath(".//*[@id='rptdataarea']/tbody/tr"));
		double balance = 0.00;
		// The first 3 trs are hidden, not data lines
		for (int i = 4; i <= reportDataList.size(); i++) {
			// The last td with index 5 is balance column
			String loc = String.format(".//*[@id='rptdataarea']/tbody/tr[%s]/td[5]", i);
			String balanceStr = context.webDriver.getHtmlElementByLocator(Locator.xpath(loc)).getAsText();
			balance = balance + formatCurrentStr(balanceStr);
		}
		return balance;
	}

	void setCustomMultiSelectFilterWithValue(ReportCustomFilter filter, List<String> values) {
		if (!filter)
			return

		HtmlElementHandle popupIcon = context.webDriver.getHtmlElementByLocator(Locator.id(filter.id + "_popupIcon"))
		popupIcon.click(false)

		context.withinMultiSelectField(filter.id).setValues(values)
		popupIcon.click(false)
	}

	protected double formatCurrentStr(String currencyStr) {
		String regEx = '[`~$,￥¥%…（）’，、？\\s]'
		Pattern p = Pattern.compile(regEx)
		Matcher m = p.matcher(currencyStr)
		currencyStr = m.replaceAll("")
		return Double.parseDouble(currencyStr)
	}
}
