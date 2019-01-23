package com.netsuite.chinalocalization.page.voucher

import com.google.inject.Guice
import com.google.inject.Injector
import com.netsuite.base.lib.element.ElementHandler
import com.netsuite.chinalocalization.cashflow.CONSTANTS.VoucherEnum
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.chinalocalization.page.voucher.template.MultiCurrenciesTemplate
import com.netsuite.chinalocalization.page.voucher.template.SingleCurrencyTemplate
import com.netsuite.chinalocalization.page.voucher.template.TemplateModule
import com.netsuite.chinalocalization.page.voucher.template.VoucherTemplate;
import com.netsuite.testautomation.aut.pageobjects.BasePageObject
import com.netsuite.testautomation.html.HtmlElementHandle;
import com.netsuite.testautomation.html.Locator
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import javax.inject.Inject

/**
 * @Author mia.wang@oracle.com
 * @Created 2018-Mar-07
 */
class VoucherPrintPage extends BasePageObject<NetSuiteAppCN> {

    private static final String XPATH_REFRESH_BTN = "//input[@name='refresh']";
    private static final String XPATH_PRINT_BTN = "//input[@name='printAsPDF']";
    static final String XPATH_SEARCH_RESULT_MSG = "//pdf//td[@align = 'left']"

    //Dynamic bind template: MulitCurrenciesTemplate/SingleCurrencyTemplate;
    private static Injector injector = Guice.createInjector(new TemplateModule());
    private static VoucherTemplate template;

    private static final String XPATH_ERR = "//span[@class='uir-message-text']";
    private static final String XPATH_OK_BTN = "//button[@value='true']";
    private static final String XPATH_CANCEL_BTN = ".//button[@value='false']"

    //FieldId
    private static final String FID_SUBSIDIARY = "subsidiary";
    private static final String FID_VOUCHER_NO_OPERATOR = "glnumberoper";
    private static final String FID_VOUCHER_NO_VALUE = "glnumberval";
    private static final String FID_TEMPLATE = "template";
    private static final String FID_TRANS_DATE_FROM = "trandatefrom";
    private static final String FID_TRANS_DATE_TO = "trandateto";
    private static final String FID_PERIOD_FROM = "periodfrom";
    private static final String FID_PERIOD_TO = "periodto";
    private static final String FID_SELECT_PAGE = "selectpage";
    private static final String FID_TRANS_PER_PAGE = "transperpage";
    private static final String FID_PRINT_DEPARTMENT = "department"
    private static final String FID_PRINT_LOCATION = "location"
    private static final String FID_PRINT_CLASS = "class"

    private static boolean isMultiCurrency;

    //For Transaction Date From/To element, use XPATH to call sendKeys method to enter date
    //setFieldText not working
    private static final String XPATH_PERIOD_FROM = "//input[@name='" + FID_TRANS_DATE_FROM + "']";
    private static final String XPATH_PERIOD_TO = "//input[@name='" + FID_TRANS_DATE_TO + "']";

    private static final String XPATH_PRINT_DEPARTMENT = "//input[@name='" + FID_PRINT_DEPARTMENT + "']"
    private static final String XPATH_PRINT_CLASS = "//input[@name='" + FID_PRINT_CLASS + "']"
    private static final String XPATH_PRINT_LOCATION = "//input[@name='" + FID_PRINT_LOCATION + "']"

    private static final String XPATH_GL_NO_VALUE="//input[@name='" + FID_VOUCHER_NO_VALUE + "']"

    VoucherPrintPage(NetSuiteAppCN context, boolean multiCurrencies) {
        super(context, context.webDriver);
        isMultiCurrency = multiCurrencies;
        if (multiCurrencies == true) {
            template = injector.getInstance(MultiCurrenciesTemplate.class);
        } else {
            template = injector.getInstance(SingleCurrencyTemplate.class);
        }
    }

    VoucherPrintPage navigateToPage(String url) {
        ((NetSuiteAppCN) this.app).navigateTo(url);
        return this;
    }

    def navigateToURL() {
        NetSuiteAppCN context = (NetSuiteAppCN) this.app
        String url = context.resolveURL("customscript_sl_cn_voucher_print", "customdeploy_sl_cn_voucher_print")
        context.navigateTo(url)
        if (context.popUpPresent) {
            context.closePopUp()
        }
    }

    String getPeriodTo() {
        def periodTo = app.getFieldText(this.FID_PERIOD_TO);
        //unicode 160 is non-breaking space
        //in Chinese environment the space is rendered as non-breaking space that ASCII value is 160 (\u00A0)
        //in English environment it is normal space whose ASCII value is 32
        return periodTo.replace('\u00A0', ' ').trim();
    }

    String getPeriodFrom() {
        def periodFrom = app.getFieldText(this.FID_PERIOD_FROM)
        //unicode 160 is non-breaking space
        //in Chinese environment the space is rendered as non-breaking space that ASCII value is 160 (\u00A0)
        //in English environment it is normal space whose ASCII value is 32
        return periodFrom.replace('\u00A0', ' ').trim();
    }

    String getDateTo() {
        def dateTo = app.getFieldText(FID_TRANS_DATE_TO)
        //unicode 160 is non-breaking space
        //in Chinese environment the space is rendered as non-breaking space that ASCII value is 160 (\u00A0)
        //in English environment it is normal space whose ASCII value is 32
        return periodFrom.replace('\u00A0', ' ').trim();
    }

    String getDateFrom() {
        def dateTo = app.getFieldText(FID_TRANS_DATE_FROM)
        //unicode 160 is non-breaking space
        //in Chinese environment the space is rendered as non-breaking space that ASCII value is 160 (\u00A0)
        //in English environment it is normal space whose ASCII value is 32
        return periodFrom.replace('\u00A0', ' ').trim();
    }

    String getSelectPage() {
        def selectPage = app.getFieldText(this.FID_SELECT_PAGE);
        return selectPage;
    }

    String getTransPerPage() {
        def transPerPage = app.getFieldText(this.FID_TRANS_PER_PAGE);
        return transPerPage;
    }

    void clickRefreshBtn() {
        app.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_REFRESH_BTN)).click();

    }

    void clickPrintBtn() {
        app.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_PRINT_BTN)).click();
    }

    void setParameters(def params, def template) {
        if (params) {
            if (params.subsidiary) {
                if (app.isOneWorld()) {
                    app.withinDropdownlist("inpt_" + FID_SUBSIDIARY).selectItem((String) params.subsidiary)
                }
            }
            if (params.periodFrom) {
                app.withinDropdownlist("inpt_" + FID_PERIOD_FROM).selectItem((String) params.periodFrom)
            }
            if (params.periodTo) {
                app.withinDropdownlist("inpt_" + FID_PERIOD_TO).selectItem((String) params.periodTo)
            }
            if (params.glnumberOper) {
                app.withinDropdownlist("inpt_" + FID_VOUCHER_NO_OPERATOR).selectItem((String) params.glnumberOper)
            }
            if (params.glnumberVal) {
                app.setFieldWithText(FID_VOUCHER_NO_VALUE, params.glnumberVal)
            }
            if (params.tranDateFrom) {
                app.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_PERIOD_FROM)).clear()
                app.setTextInputByXpath(XPATH_PERIOD_FROM, params.tranDateFrom)
            }
            if (params.tranDateTo) {
                app.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_PERIOD_TO)).clear()
                app.setTextInputByXpath(XPATH_PERIOD_TO, params.tranDateTo)
            }
            if (params.tranDateTo) {
                app.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_PERIOD_TO)).clear()
                app.setTextInputByXpath(XPATH_PERIOD_TO, params.tranDateTo)
            }
            if (template) {
                this.changeTemplate(template)
            }
        }
    }


    void setParametersWithGLN(def params, def template, def opt,String glN) {
        setParameters(params, template)
        app.withinDropdownlist("inpt_" + FID_VOUCHER_NO_OPERATOR).selectItem(opt)
        HtmlElementHandle number=app.webDriver.getHtmlElementByLocator(Locator.xpath(XPATH_GL_NO_VALUE))
        number.clear()
        number.sendKeys(glN)
    }


    String getVoucherReportTitle(int index) {
        int actualIndex = 3 * index + 2;
        return (app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportTitleXpath(actualIndex)))).getAsText();
    }

    def getVoucherReportMain(int index) {
        int actualIndex = index + 1;
        def main = [:]

        Thread.sleep(10000)

        def subsidiary_locator=Locator.xpath(template.getReportSubsidiaryXpath(actualIndex))
        if(subsidiary_locator) {
            String subsidiary = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportSubsidiaryXpath(actualIndex))).getAsText()
            main["subsidiary"] = this.getFieldContentWithoutLabel(subsidiary)

            String date = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportDateXpath(actualIndex))).getAsText();
            main["date"] = this.getFieldContentWithoutLabel(date)

            String docNo = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportDocNumberXpath(actualIndex))).getAsText();
            main["docNo"] = this.getFieldContentWithoutLabel(docNo)

            String tranType = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportTransactionTypeXpath(actualIndex))).getAsText();
            main["tranType"] = this.getFieldContentWithoutLabel(tranType)

            String postingPeriod = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportPeriodXpath(actualIndex))).getAsText();
            main["postingPeriod"] = this.getFieldContentWithoutLabel(postingPeriod)

            String voucherNo = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportVoucherNumberXpath(actualIndex))).getAsText();
            main["voucherNo"] = this.getFieldContentWithoutLabel(voucherNo)

            String currency = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportCurrencyXpath(actualIndex))).getAsText();
            main["currency"] = this.getFieldContentWithoutLabel(currency)

            String page = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportPageXpath(actualIndex))).getAsText();
            main["page"] = this.getFieldContentWithoutLabel(page)
        }
        return main;
    }

    def getVoucherReportFooter(int index) {
        int actualIndex = index + 1;
        def footer = [:]

        String creator = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportCreatorXpath(actualIndex))).getAsText()
        footer["creator"] = this.getFieldContentWithoutLabel(creator)

        String approver = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportApproverXpath(actualIndex))).getAsText()
        footer["approver"] = this.getFieldContentWithoutLabel(approver)

        String poster = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportPosterXpath(actualIndex))).getAsText()
        footer["poster"] = this.getFieldContentWithoutLabel(poster)

        return footer
    }

    Map getVoucherReportTableRowCells(rowIndex, String[] cols) {
        Map colsLocator = template.getItemTableRowCells(rowIndex, cols)
        Map results = [:]
        colsLocator.each { k, v ->
            String colValue = app.withinHtmlElementIdentifiedBy(Locator.xpath(v)).getAsText()
            //results << ["${k}" : "${colValue}" ] // not good
            // results << ["${k}" : colValue ]      // not good
            // results.put(k,colValue)              // not good
            // results << [(k) : colValue ]         // not good
            results[k] = colValue                  // good

        }

        return results
    }

    private String getFieldContentWithoutLabel(String fieldContent) {
        if (fieldContent.contains(":")) {
            if (fieldContent.split(":").length > 1) {
                return fieldContent.split(":")[1].trim();
            } else {
                return "";
            }
        } else {
            return fieldContent;
        }
    }

    String getErrorMessage() {
        return (app.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_ERR))).getAsText();
    }

    void closeErrorMessage() {
        app.closePopUp()
    }

    void clickOKBtn() {
        app.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_OK_BTN)).click();
    }

    void clickCancelBtn() {
        app.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_CANCEL_BTN)).click()
    }

    void changeTemplate(String targetTemplateName) {
        app.withinDropdownlist(FID_TEMPLATE).selectItem(targetTemplateName);
        if (targetTemplateName.equals(VoucherEnum.VOUCHER_MULT_CURRENCIES_TEMPLATE.enLabel) || targetTemplateName.equals(VoucherEnum.VOUCHER_MULT_CURRENCIES_TEMPLATE.cnLabel)) {
            isMultiCurrency = true;
            template = injector.getInstance(MultiCurrenciesTemplate.class);
        } else if (targetTemplateName.equals(VoucherEnum.VOUCHER_SINGLE_CURRENCY_TEMPLATE.enLabel) || targetTemplateName.equals(VoucherEnum.VOUCHER_SINGLE_CURRENCY_TEMPLATE.cnLabel)) {
            isMultiCurrency = false;
            template = injector.getInstance(SingleCurrencyTemplate.class);
        } else {
            log.error("Invalid voucher template:" + targetTemplateName);
            println("Invalid voucher template:" + targetTemplateName);
        }
    }

    /**
     * Both Template are OK
     * @param reportIndex : starts from 0
     * @return{JSON object} Represent the report items on specified line
     */
    def getReportLineOnBody(def reportIndex, def expectedLineObj) {
        reportIndex = reportIndex + 1
        def lineIndex = expectedLineObj.lineNum
        def line = [:]

        //Items in common (both single ccy template and multi-ccy template
        if (expectedLineObj.lineNum) {
            line["lineNum"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Line No.", null, reportIndex, lineIndex))).getAsText()
        }
        if (expectedLineObj.lineDesp) {
            line["lineDesp"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Line Description", null, reportIndex, lineIndex))).getAsText()
        }
        if (expectedLineObj.acctDesp) {
            line["acctDesp"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Account and Description", null, reportIndex, lineIndex))).getAsText()
        }
        if (expectedLineObj.tranCcy) {
            line["tranCcy"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Transaction Currency", null, reportIndex, lineIndex))).getAsText()
        }
        if (expectedLineObj.rate) {
            line["rate"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Rate", null, reportIndex, lineIndex))).getAsText()
        }
        if (expectedLineObj.tranCcyDebit) {
            line["tranCcyDebit"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Transaction Currency", "Debit", reportIndex, lineIndex))).getAsText()
        }
        if (expectedLineObj.tranCcyCredit) {
            line["tranCcyCredit"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Transaction Currency", "Credit", reportIndex, lineIndex))).getAsText()
        }
        if (expectedLineObj.funcCcyDebit) {
            line["funcCcyDebit"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Functional Currency", "Debit", reportIndex, lineIndex))).getAsText()
        }
        if (expectedLineObj.funcCcyCredit) {
            line["funcCcyCredit"] = app.withinHtmlElementIdentifiedBy(Locator.xpath(template.getReportItemsOnLineXpath("Functional Currency", "Credit", reportIndex, lineIndex))).getAsText()
        }
        return line
    }

    def getListOfValueByFieldId(String fieldId) {
        def values = app.withinDropdownlist(fieldId).getOptions();
        return values;
    }

    def getValueByFieldId(String fieldId) {
        def value = app.withinDropdownlist(fieldId).getValue();
        return value;
    }

    def checkPrintDepartment() {
        app.checkNlsinglecheckbox(FID_PRINT_DEPARTMENT)
    }

    def unCheckPrintDepartment() {
        app.unCheckNlsinglecheckbox(FID_PRINT_DEPARTMENT)
    }

    def checkPrintLocation() {
        app.checkNlsinglecheckbox(FID_PRINT_LOCATION)
    }

    def unCheckPrintLocation() {
        app.unCheckNlsinglecheckbox(FID_PRINT_LOCATION)
    }

    def checkPrintClass() {
        app.checkNlsinglecheckbox(FID_PRINT_CLASS)
    }

    def unCheckPrintClass() {
        app.unCheckNlsinglecheckbox(FID_PRINT_CLASS)
    }

    def checkAllCustomField() {
        checkPrintLocation()
        checkPrintDepartment()
        checkPrintClass()
    }

    def unCheckAllCustomField() {
        unCheckPrintLocation()
        unCheckPrintDepartment()
        unCheckPrintClass()
    }


    private boolean isChecked(String FID) {
        return app.isFieldChecked(FID)
    }

    boolean isPrintDeparmentChecked() {
        return isChecked(FID_PRINT_DEPARTMENT)
    }

    boolean isPrintClassChecked() {
        return isChecked(FID_PRINT_CLASS)
    }

    boolean isPrintLocationChecked() {
        return isChecked(FID_PRINT_LOCATION)
    }

    private boolean isDisabled(String FID) {
        return app.isFieldDisabled(FID)
    }

    boolean isPrintDeparmentDisabled() {
        return isDisabled(FID_PRINT_DEPARTMENT)
    }

    boolean isPrintClassDisabled() {
        return isDisabled(FID_PRINT_CLASS)
    }

    boolean isPrintLocationDisabled() {
        return isDisabled(FID_PRINT_LOCATION)
    }

    boolean isPrintDepartmentExist() {
        return isExist(XPATH_PRINT_DEPARTMENT)
    }

    boolean isPrintLoacationExist() {
        return isExist(XPATH_PRINT_LOCATION)
    }

    boolean isPrintClassExist() {
        return isExist(XPATH_PRINT_CLASS)
    }

    boolean isExist(String XPATH) {
        return app.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH)) != null
    }

    def getSearchResultMsg() {
        return app.withinHtmlElementIdentifiedBy(Locator.xpath(XPATH_SEARCH_RESULT_MSG)).getAsText()
    }


}
