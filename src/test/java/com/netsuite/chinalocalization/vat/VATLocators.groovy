package com.netsuite.chinalocalization.vat

import groovy.transform.Immutable

@Immutable
class VATLocators {
    String title = "//*[@id=\"main_form\"]/table/tbody/tr[1]/td/div[1]/div[2]/h1"
    String invoiceType = "//*[@id=\"custpage_vatinvoicetype_fs\"]"
    String subsidiary = "//*[@id=\"custpage_subsidiary_fs\"]"
    String enableSalesList = "//*[@id=\"custpage_saleslist_fs\"]"
    String refresh = "//input[@id='custpage_refresh']"
    String edit = "//input[@id='custpage_edit']"
    String export = "//input[@id='custpage_export']"
    String customer ="//*[@id=\"custpage_customer_fs\"]"

    String maxInvoiceAmountLabel = "//*[@id=\"custrecord_cn_vat_max_invoice_amount_fs_lbl\"]"
    String maxInvoiceAmountField = "//*[@id=\"custrecord_cn_vat_max_invoice_amount_formattedValue\"]"
    String importButton = "//input[@id='custpage_import']"
    String importSubmit = "//*[@id='submitter']"
    String importAlert ="//*[@id=\"div__alert\"]"
    String importFileBrowser = "//input[@name='custpage_file_browser']"
    String returnButton = "//input[@id='custpage_back']"
    String splitRule = "//*[@id=\"custbody_cn_vat_split_rule_fs\"]"
    //String splitRuleInputMemo = "//*[@id=\"inpt_custbody_cn_vat_split_rule33\"]"
    String splitRuleInputMemo = "//*[@name=\"inpt_custbody_cn_vat_split_rule\"]"
    String splitRuleInputRefund = "//*[@name=\"inpt_custbody_cn_vat_split_rule\"]"
    String splitRuleInputInvoice = "//*[@name=\"inpt_custbody_cn_vat_split_rule\"]"
    String splitRuleInputCaseSales = "//*[@name=\"inpt_custbody_cn_vat_split_rule\"]"
    String vatInvoiceType = "//*[@id=\"custbody_cn_vat_invoice_type_fs\"]"
    //String vatInvoiceTypeInputRefund = "//*[@id=\"inpt_custbody_cn_vat_invoice_type32\"]"
    String vatInvoiceTypeInputRefund = "//*[@name=\"inpt_custbody_cn_vat_invoice_type\"]"
    String vatInfoSheetNumber = "//*[@id=\"custbody_cn_info_sheet_number\"]"
    String vatInvoiceStatusLabel = "//*[@id=\"custpage_cn_vat_invoicesheader\"]/td[1]"
    //String vatInvoiceStatus = "//*[@id=\"custpage_cn_vat_invoices_custpage_cn_vat_status1_fs\"]"
    String vatInvoiceStatus = "//*[@id=\"custpage_cn_vat_invoices_custpage_cn_vat_status1_fs\"]/div[2]"
    String vatInvoiceCode = "//*[@id=\"custpage_cn_vat_invoicesrow0\"]/td[2]"
    String vatInvoiceNumber = "//*[@id=\"custpage_cn_vat_invoicesrow0\"]/td[3]"
    String vatInvoiceDate = "//*[@id=\"custpage_cn_vat_invoicesrow0\"]/td[4]"
    String vatInvoiceTaxAmount = "//*[@id=\"custpage_cn_vat_invoicesrow0\"]/td[5]"
    String vatInvoiceTaxExclusiveAmount = "//*[@id=\"custpage_cn_vat_invoicesrow0\"]/td[6]"
    String vatCreatedFrom = "//*[@id=\"custbody_cn_vat_createdfrom\"]"



    //Save button for wrong Credit card
    String saveCreditBtn = ".//input[@id='btn_secondarymultibutton_submitter']"
    String errorCreditMsg = ".//[@class='uir-message-text']"

    String customerDropdown = ".//*[@id='entity_fs']"
    String locationDropdown = ".//*[@id='location_fs']"
    String itemsDropdown = ".//*[@id='item_item_fs']"
    String addItemBtn = ".//input[@id='item_addedit']"
    String internalIdInput = ".//input[@id='id'][type='hidden']"
    String infoSheetNumberInput = ".//input[@id='custbody_cn_info_sheet_number']"

    String custPageDateFromInput = ".//input[@id='custpage_datefrom']"
    String custPageDateToInput = ".//input[@id='custpage_dateto']"
    String firstTransTable = "//*[@id='vat_report']/table[2]"

    String okButton = "//*[@class='uir-message-popup']//button"




}
