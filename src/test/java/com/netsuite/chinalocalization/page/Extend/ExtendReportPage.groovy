package com.netsuite.chinalocalization.page.Extend

import com.google.inject.Inject
import com.netsuite.chinalocalization.common.pages.PageBaseAdapterCN
import com.netsuite.chinalocalization.lib.NFormat
import com.netsuite.testautomation.aut.pageobjects.DropdownList
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser


class ExtendReportPage extends PageBaseAdapterCN {

    @Inject
    NFormat format

    //BUTTON
    String XPATH_BTN_REFRESH = ".//*[@id='custpage_refresh']"
    String XPATH_BTN_EXP_EXCEL = ".//*[@id='custpage_export_excel']"
    String XPATH_BTN_EXP_PDF = ".//*[@id='custpage_export_pdf']"
    String XPATH_PARAM_SUBSIDIARY = "//*[@id=\"custpage_subsidiary_fs\"]"
    String XPATH_PARAM_ACCOUNTFROM = "//*[@id='custpage_accountfrom_fs']"
    String XPATH_PARAM_ACCOUNTTO ="//*[@id='custpage_accountto_fs']"

    String XPATH_BTN_ERROR_DIALOG = "//button[@value='true']"

    String XPATH_ERROR_DIALOG =  "//span[@class='uir-message-text']"

    //FieldIds
    String FIELD_ID_EXP_EXCEL = "custpage_export_excel"
    String FIELD_ID_SUBSIDIARY = "custpage_subsidiary"
    String FIELD_ID_PERIODFROM = "custpage_datefrom"
    String FIELD_ID_PERIODTO = "custpage_dateto"
    String FIELD_ID_ACCOUNTFROM = "custpage_accountfrom"
    String FIELD_ID_ACCOUNTTO = "custpage_accountto"
    String FIELD_ID_ACCOUNTLEVEL = "custpage_accountlevel"
    String FIELD_ID_DATEFROM = "custpage_datefrom"
    String FIELD_ID_DATETO = "custpage_dateto"
    String FIELD_ID_LOCATION = "custpage_location"
    String FIELD_ID_DEPARTMENT = "custpage_department"
    String FIELD_ID_CLASS = "custpage_class"
    //Input Search condition


    //Report Header
    def accountLabel = ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[1]/th[1]/div"
    def openBalanceLabel = ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[1]/th[2]/div"
    def currentPeriodLabel = ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[1]/th[3]/div"
    def closeBalanceLabel = ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[1]/th[4]/div"

    def debitCreditLabel1 = ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[2]/th[1]/div"
    def amountLabel1 =      ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[2]/th[2]/div"
    def debitLabel =  ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[2]/th[3]/div"
    def creditLabel =  ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[2]/th[4]/div"
    def debitCreditLabel2 = ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[2]/th[5]/div"
    def amountLabel12 =  ".//table[@id=\"custpage_atbl_report_sublist_splits\"]/tbody/tr[2]/th[6]/div"

    // Map key for report table
    def ABlabel_CN =[
        header:[
            account: "科目",
            openBalance:"期初余额",
            currentPeriod:"本期发生额",
            closeBalance:"期末余额",
            debitCredit:"方向",
            amount:"金额",
            debit:"借方",
            credit:"贷方"],
        expectedResults: [
             loadingMsg: "数据加载中..."
             ]
    ]
    def ABlabel_EN =[
        header:
        [
           account:"Account",
           openBalance:"Opening Balance",
           currentPeriod:"Current Period",
           closeBalance:"Closing Balance",
           debitCredit:"Debit/Credit",
           amount:"Amount",
           debit:"Debit",
           credit:"Credit"],
        expectedResults: [
                           loadingMsg:"Loading..."
                       ]
    ]
    def SBlabel_CN =[
        paramLable:[
            subsidiary:"子公司",
            datefrom:"日期自",
            dateto:"至",
            accountfrom:"科目自",
            accountto:"至"
            ],
        header:[
            account_name: "科目",
            type:"类型",
            document_number:"文件号码",
            gl_number:"GL#",
            date:"日期",
            memo:"摘要",
            debit_amount:"借记",
            credit_amount:"贷记",
            balance:"余额",
            balance_direction:"方向",
            balance_amount:"金额"],
        expectedResults: [
            loadingMsg: "数据加载中..."
         ]
    ]
    def SBHeader = [
    account_name: ".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[1]/div",
    type:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[2]/div",
    document_number:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[3]/div",
    gl_number:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[4]/div",
    date:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[5]/div",
    memo:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[6]/div",
    debit_amount:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[7]/div",
    credit_amount:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[8]/div",
    balance:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[9]/div",
    balance_direction:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[2]/th[1]/div",
    balance_amount:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[2]/th[2]/div"]

    def SBlabel_EN =[
        paramLable:[
            subsidiary:"Subsidiary",
            datefrom:"Date From",
            dateto:"To",
            accountfrom:"Account From",
            accountto:"To"
        ],
        header:[
                account_name: "Account",
                type: "Type",
                document_number: "Document Number",
                gl_number:"GL#",
                date:"Date",
                memo:"Memo",
                debit_amount:"Debit",
                credit_amount:"Credit",
                balance:"Balance",
                balance_direction:"Debit/Credit",
                balance_amount:"Amount"],

        expectedResults: [
             loadingMsg: "Loading..."
        ]
    ]

    def CBJlabel_CN =[
            paramLable:[
                    subsidiary:"子公司",
                    datefrom:"期间自",
                    dateto:"至",
                    accountfrom:"科目自",
                    accountto:"至"
            ],
            header:[
                    account_name: "科目",
                    date:"日期",
                    type:"类型",
                    memo:"摘要",
                    document_number:"文件号码",
                    payment_method:"支付方式",
                    gl_number:"GL#",
                    debit_amount:"借记",
                    credit_amount:"贷记",
                    balance:"余额",
                    balance_direction:"方向",
                    balance_amount:"金额"],
            expectedResults: [
                    loadingMsg: "数据加载中..."
            ]
    ]

    def CBJlabel_EN =[
            paramLable:[
                    subsidiary:"Subsidiary",
                    datefrom:"Date From",
                    dateto: "To",
                    accountfrom:"Account From",
                    accountto:"To"
            ],
            header:[
                    account_name: "Account",
                    date:"Date",
                    type: "Type",
                    memo:"Memo",
                    document_number: "Document Number",
                    payment_method:"Payment Method",
                    gl_number:"GL#",
                    debit_amount:"Debit",
                    credit_amount:"Credit",
                    balance:"Balance",
                    balance_direction:"Debit/Credit",
                    balance_amount:"Amount"],

            expectedResults: [
                    loadingMsg: "Loading..."
            ]
    ]
    // XPath for Cash&Bank Journal Report sublist header
    // need to change in sprint 16
    def CBJHeader = [
            account_name: ".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[1]/div",
            date:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[2]/div",
            type:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[3]/div",
            memo:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[4]/div",
            document_number:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[5]/div",
            payment_method:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[6]/div",
            gl_number:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[7]/div",
            debit_amount:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[8]/div",
            credit_amount:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[9]/div",
            balance:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[1]/th[10]/div",
            balance_direction:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[2]/th[1]/div",
            balance_amount:".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[2]/th[2]/div"]


    def navigateToPortalPage() {
        context.navigateTo(resolveSuiteletURL("customscript_sl_cn_atbl", "customdeploy_sl_cn_atbl"))
    }
    def clickRefresh() {
        asElement(XPATH_BTN_REFRESH).click()
    }

    def getRefreshButtonStatus(){
        return asAttributeValue(XPATH_BTN_REFRESH,"disabled")
    }

    def clickErrorMessageOkButton(){
        asElement(XPATH_BTN_ERROR_DIALOG).click()
    }

    def getErrorMessage(){
       return asText(XPATH_ERROR_DIALOG)
    }

    def clickExpToExcel() {
        asElement(XPATH_BTN_EXP_EXCEL).click()
    }
    def clickExpToPDF() {
        asElement(XPATH_BTN_EXP_PDF).click()
    }
    def getAccoutFromList(){
        context.getCNdropdownOptions(FIELD_ID_ACCOUNTFROM)
    }
    def getAccoutToList(){
        context.getCNdropdownOptions(FIELD_ID_ACCOUNTTO)
    }

    def setQuery(options=[: ] ){
        def items =[: ]
        if (context.isOneWorld()) {
            if (options.subsidiary) asDropdownList(locator:XPATH_PARAM_SUBSIDIARY).selectItem(options.subsidiary)
        }
        if (options.datefrom) context.setFieldWithValue(FIELD_ID_DATEFROM, options.datefrom)
        if (options.dateto) context.setFieldWithValue(FIELD_ID_DATETO, options.dateto)
        if (options.accountfrom) asDropdownList(locator:XPATH_PARAM_ACCOUNTFROM).selectItem(options.accountfrom)
        if (options.accountto) asDropdownList(locator:XPATH_PARAM_ACCOUNTTO).selectItem(options.accountto)
       /*
        if (options.accountfrom) items.put(FIELD_ID_ACCOUNTFROM, options.accountfrom)
        if (options.accountto) items.put(FIELD_ID_ACCOUNTTO, options.accountto)
        */
        if (options.accountlevel) items.put(FIELD_ID_ACCOUNTLEVEL, options.accountlevel)
        if (options.location) items.put(FIELD_ID_LOCATION, options.location)
        if (options.department) items.put(FIELD_ID_DEPARTMENT, options.department)
        if (options.class) items.put(FIELD_ID_CLASS, options.class)
        currentRecord.setText(items)
    }
    boolean isExist(String fieldId){
        return context.doesFieldExist(fieldId)
    }
    def getLableValue(String lable){

        String rtn
        if(lable.equals("subsidiary")){
            rtn = asLabel(FIELD_ID_SUBSIDIARY)
        }else if(lable.equals("datefrom")){
            rtn = asLabel(FIELD_ID_DATEFROM)
        }else if(lable.equals("dateto")){
            rtn = asLabel(FIELD_ID_DATETO)
        }else if(lable.equals("accountfrom")){
            rtn = asLabel(FIELD_ID_ACCOUNTFROM)
        }else if(lable.equals("accountto")){
            rtn = asLabel(FIELD_ID_ACCOUNTTO)
        }else if(lable.equals("accountlevel")){
            rtn = asLabel(FIELD_ID_ACCOUNTLEVEL)
        }
        return rtn
    }
    def getAllLableValues(){
        def lableTexts = [:]
        def lableIDs=["subsidiary","datefrom","dateto","accountfrom","accountto","accountlevel"]
        lableIDs.each {lable->lableTexts."${lable}"=getLableValue(lable)}
        return lableTexts
    }
    def getSubsidiaryValue(){
        return asFieldText(FIELD_ID_SUBSIDIARY)
    }
    def getAccountFromValue(){
        return asFieldText(FIELD_ID_ACCOUNTFROM).trim().replace(" ",'')
    }
    def getAccountToValue(){
        return asFieldText(FIELD_ID_ACCOUNTTO).trim().replace(" ",'')
    }
    def getAccountLevelValue(){
        return asFieldText(FIELD_ID_ACCOUNTLEVEL)
    }
    def getSubsidiaryOptions(){
        return asDropdownList(locator: XPATH_PARAM_SUBSIDIARY).getOptions()
    }

    def getAccountFromOptions(){
        return asDropdownList(fieldId: FIELD_ID_ACCOUNTFROM).getOptions()
    }
    def getAccountToOptions(){
        return asDropdownList(fieldId: FIELD_ID_ACCOUNTTO).getOptions()
    }
    def getAccountLevelOptions(){
        return asDropdownList(fieldId: FIELD_ID_ACCOUNTLEVEL).getOptions()
    }
}
