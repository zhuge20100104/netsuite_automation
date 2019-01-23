package com.netsuite.chinalocalization.page.Setup

import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.testautomation.html.Locator

class UserPreferencePage extends PageBaseAdapterCN {
	private static String URL = "/app/center/userprefs.nl"
	def list = ['PDFLANGUAGE','TIMEZONE', 'LOCATION', 'BASICCENTER',"DATEFORMAT"]

	// Set metaClass property to ExpandoMetaClass instance, so we
	// can add dynamic methods.
	UserPreferencePage() {
		def mc = new ExpandoMetaClass(UserPreferencePage, false, true)
		mc.initialize()
		this.metaClass = mc
	}

	def methodMissing(String name, args) {
		// Intercept method that starts with find.
		def field
		if (name.startsWith("get")) {
			field = list.find { it == name[3..-1] }
			if(!field) throw new MissingMethodException(name, this.class, args)
			// Add new method to class with metaClass.
			def result
			this.metaClass."$name" = { -> result = context.getFieldValue(field)}
			result
		}else if (name.startsWith("set")) {
			field = list.find { it == name[3..-1] }
			if(!field) throw new MissingMethodException(name, this.class, args)
			this.metaClass."$name" = {-> context.setFieldWithValue(field, args )}

		} else {

			throw new MissingMethodException(name, this.class, args)
		}
	}

	def navigateToURL() {
		context.navigateTo(URL)
	}

	def getOnlyShowLastSubAcct() {

		return context.getFieldValue("ONLYSHOWLASTSUBACCT");

	}

	def setOnlyShowLastSubAcct(String flag , boolean saveRecord = false) {
		if(!flag.equals(getOnlyShowLastSubAcct())){
		    context.setFieldWithValue("ONLYSHOWLASTSUBACCT", flag)
		    if (saveRecord) 	clickSave()
		}
	}

	def clickSave() {
		context.webDriver.getHtmlElementByLocator(Locator.xpath("/*//*[@id='submitter']")).scrollToView();
		context.webDriver.click(Locator.xpath("/*//*[@id='submitter']"));
		context.webDriver.waitForPageToLoad();
	}

	def setNumberFormat(String format, boolean saveRecord = false) {
		if(!format.equals(getNumberFomat())){
			context.setFieldWithText("NUMBERFORMAT", format)
			if (saveRecord) 	clickSave()
		}
	}
	def getNumberFomat() {

		return context.getFieldText("NUMBERFORMAT");

	}
	def setDateFormat(String format, boolean saveRecord = false) {
		if(format != (getDateFormat())){
			context.setFieldWithText("DATEFORMAT", format)
			if (saveRecord) 	clickSave()
		}
	}
	def getDateFormat() {

		return context.getFieldText("DATEFORMAT");

	}
}
