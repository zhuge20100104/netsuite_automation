package com.netsuite.chinalocalization.extendreport

import com.google.inject.Inject
import com.netsuite.base.excel.ExcelUtil
import com.netsuite.base.lib.month.MonthUtil
import com.netsuite.chinalocalization.lib.HTMLTOExcel
import com.netsuite.chinalocalization.common.BaseAppTestSuite
import com.netsuite.chinalocalization.page.EnableFeaturesPage
import com.netsuite.chinalocalization.page.Extend.ExtendReportPage
import com.netsuite.chinalocalization.page.Setup.AccountPreferencePage
import com.netsuite.chinalocalization.page.Setup.CompanyInformationPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.chinalocalization.page.Setup.UserPreferencePage
import com.netsuite.testautomation.junit.runners.SuiteSetup
import com.netsuite.testautomation.junit.runners.SuiteTeardown
import groovy.json.JsonBuilder
import org.junit.After
import org.junit.Before


/**
 * @desc Base test suite implementation for Income Statement
 * @author Jianwei Liu
 */
class ExtendReportAppTestSuite extends BaseAppTestSuite {

    @Inject
    EnableFeaturesPage enableFeaPage
    @Inject
    ExtendReportPage extendReportPage
    @Inject
    AccountPreferencePage accountPrePage
    @Inject
    UserPreferencePage userPrePage
    @Inject
    CompanyInformationPage companyInfoPage
    @Inject
    SubsidiaryPage subsidiaryPage
    @Inject
    ExcelUtil excelUtil
    @Inject
    MonthUtil monthUtil

    def static labelData
    def static testData
    def testSuiteData
    def dirtyData

    def cust= [
            accountName                : "custpage_account_name",
            openBalanceDirection       :"custpage_open_balance_direction",
            openBalance_amount          :"custpage_open_balance_amount",
            debitAmount               : "custpage_debit_amount",
            creditAmount              :"custpage_credit_amount",
            closeBalanceDirection    : "custpage_close_balance_direction",
            closeBalanceAmount       : "custpage_close_balance_amount"
    ]

    def subledger = [
            account_name: "custpage_account_name",
            type:"custpage_type",
            document_number :"custpage_document_number",
            gl_number:    "custpage_gl_number",
            date :"custpage_date",
            memo :"custpage_memo",
            debit_amount: "custpage_debit_amount",
            credit_amount: "custpage_credit_amount",
            balance_direction : "custpage_balance_direction",
            balance_amount  : "custpage_balance_amount"
    ]
    def cashBankJournal = [
            account_name: "custpage_account_name",
            date :"custpage_date",
            type:"custpage_type",
            memo :"custpage_memo",
            document_number :"custpage_document_number",
            payment_method:"custpage_payment_method",
            gl_number:    "custpage_gl_number",
            debit_amount: "custpage_debit_amount",
            credit_amount: "custpage_credit_amount",
            balance_direction : "custpage_balance_direction",
            balance_amount  : "custpage_balance_amount"
    ]


    def pathToTestSuiteFile() {
    }

    def prepareSuitData() {
    }
   @SuiteSetup
    void setUpTestSuite() {
        //login()
        super.setUpTestSuite()
       //Get Test Data
       testData = data

        initLabelData()
        def path = pathToTestSuiteFile()
        if (path && doesFileExist(path)) {
            testSuiteData = context.asJSON(path: path)
        }


       prepareSuitData()
       // Enable Classification: Location, Department, Class in Suite setup

       // Enable PDF/HTML Advanced Feature
       if(!context.isFeatureEnabled("ADVANCEDPRINTING")){
           //switchToRole(administrator)
           enableFeaPage.navigateToURL()
           enableFeaPage.enableAdvancedPrinting()
           enableFeaPage.clickSave()

       }

    }


    def initLabelData() {
        def editPath= [
                "zhCN": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\labelData_zh_CN.json",
                "enUS": "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\labelData_en_US.json"

        ]
        if (isTargetLanguageChinese()) {
            if (doesFileExist(editPath.zhCN)) {
                labelData = context.asJSON(path: editPath.zhCN.replace("\\",SEP))
            }
        } else {
            if (doesFileExist(editPath.enUS)) {
                labelData = context.asJSON(path: editPath.get("enUS").replace("\\",SEP))
            }
        }
    }
    def getLabelData(){
        labelData
    }
    def getTestData(){
        testData
    }
    def getABlabel(){
        if (isTargetLanguageChinese()) {
            return    extendReportPage.ABlabel_CN
        }
        else{
            return    extendReportPage.ABlabel_EN
        }
    }
    def getSBlabel(){
        if (isTargetLanguageChinese()) {
            return    extendReportPage.SBlabel_CN
        }
        else{
            return    extendReportPage.SBlabel_EN
        }
    }
    def getCBJlabel(){
        if (isTargetLanguageChinese()) {
            return    extendReportPage.CBJlabel_CN
        }
        else{
            return    extendReportPage.CBJlabel_EN
        }
    }

    def methodMissing(String name, args) {
        // Intercept method that starts with find.
        def field
        if (name.startsWith("setUser")) {
            field = name[7..-1]
            def result = context.getPreference(field)
            if (!result)  throw new MissingMethodException(name, this.class, args)
            if (!(result ==~ /${args[0]}/)) {
                userPrePage.navigateToURL()
                context.setFieldWithValue(field, args[0] )
                userPrePage.clickSave()
            }
            return
        }
        if(name.startsWith("setFeature")){
            field = name[10..-1]
            def result = context.isFeatureEnabled(field)
            //if (!result)  throw new MissingMethodException(name, this.class, args)
            if (!(result ==~ /${args[0]}/)) {
                //switchToRole(administrator)
                enableFeaPage.navigateToURL()
                def statusString = args[0]? "T":"F"
                context.setFieldWithValue(field, statusString )
                enableFeaPage.clickSave()
                //switchToRole(accountant)
            }
            return
        }
            throw new MissingMethodException(name, this.class, args)

    }
    def switchWindow() {
        context.webDriver.executeScript("window.open('/app/center/card.nl?sc=-29','_blank');")
        def  currentHandle = context.webDriver.getWindowHandle()
        context.webDriver.closeWindow(currentHandle)
        context.switchToWindowWithURL('/app/center/card.nl?sc=-29')
    }

    @After
    void tearDown() {
        println("tearDown: cleaning up dirty data...")
        switchWindow()
    }

    @Before
    void setUp() {
        super.setUp()
        println("setUp: process init ...")
        //if (!testData){
            def path = pathToTestDataFiles()
            if (path) {
                if (isTargetLanguageChinese()) {
                    if (doesFileExist(path.zhCN)) {
                        testData = context.asJSON(path: path.zhCN.replace("\\",SEP))

                    }
                } else {
                    if (doesFileExist(path.enUS)) {
                        testData = context.asJSON(path: path.enUS.replace("\\",SEP))

                    }
                }

            }
        //}
        preCase()
    }
    def preCase(){

    }

    @SuiteTeardown
    void tearDownTestSuite() {
        println("tearDown in Test Suite: cleaning up dirty data...")
        userPrePage.with {
            navigateToURL()
            setOnlyShowLastSubAcct("T")
            setNumberFormat("1,000,000.00")
            setTIMEZONE("Asia/Hong_Kong")
            setDateFormat("M/D/YYYY")
            clickSave()
        }
        println("After usrePage")
        def result = context.isFeatureEnabled("glauditnumbering")

        // Enable Classification:Location, Department, Class
        if (result=="F"){
            //switchToRole(administrator)
            enableFeaPage.navigateToURL()
            enableFeaPage.setglauditnumbering("T")
            enableFeaPage.clickSave()
        }
        println("before super suite")
        super.tearDownTestSuite()
        println("After super suit")
    }
    def pathToTestDataFiles() {}

    def navigateToPortalPage() {
        extendReportPage.navigateToPortalPage()
    }
    def navigateToSubLedgerPage(){
        context.navigateTo(resolveSuiteletURL("customscript_sl_cn_sblg", "customdeploy_sl_cn_sblg"))
    }
    def navigateToCashAndBankJournalPage(){
        context.navigateTo(resolveSuiteletURL("customscript_sl_cn_cbjl", "customdeploy_sl_cn_cbjl"))
    }

    def trimText(text) {
        return text.trim().replaceAll("[\\u00A0]+", "")
    }
    def setSearchParams(filter) {
        if(filter) extendReportPage.setQuery(filter)
    }
    def checkPageName(){
        def pageName = testData.pageName
        def nameDisplayed = asText(".//*[@class='uir-record-type']")
        assertTrue("check the page name",pageName.equals(nameDisplayed))
        //assertTrue()
    }
    def checkButtonsDisplayed(boolean exportDisplayed){
        assertTrue("refresh button should be Displayed",extendReportPage.isExist("custpage_refresh"))
        assertAreEqual("refresh button should be Displayed",extendReportPage.isExist("custpage_export_excel"),exportDisplayed)
        assertAreEqual("refresh button should be Displayed",extendReportPage.isExist("custpage_export_pdf"),exportDisplayed)
    }
    def checkVisible(){
        extendReportPage.with{
            if (context.isOneWorld()) {
                assertTrue("Subsidiary should be visible",isExist(FIELD_ID_SUBSIDIARY))
            }
            assertTrue("DateFrom should be visible",isExist(FIELD_ID_DATEFROM))
            assertTrue("DateTo should be visible",isExist(FIELD_ID_DATETO))
            assertTrue("AccountFrom should be visible",isExist(FIELD_ID_ACCOUNTFROM))
            assertTrue("AccountTo should be visible", isExist(FIELD_ID_ACCOUNTTO))
            assertTrue("AccountLevel should be visible", isExist(FIELD_ID_ACCOUNTLEVEL))
        }
    }
    def checkHeader(){
        extendReportPage.with{
            assertAreEqual("Account Label rowspan is 2 ", asAttributeValue("${accountLabel}/..", "rowspan"), "2")
            assertAreEqual("Opening Balance Label colspan is 2 ", asAttributeValue("${openBalanceLabel}/..", "colspan"), "2")
            assertAreEqual("Current Period Label colspan is 2 ", asAttributeValue("${currentPeriodLabel}/..", "colspan"), "2")
            assertAreEqual("Closing Balance Label colspan is 2 ", asAttributeValue("${closeBalanceLabel}/..", "colspan"), "2")
            assertAreEqual("${labelData.header.account} should show", asText(accountLabel),labelData.header.account)
            assertAreEqual("${labelData.header.openBalance} should show", asText(openBalanceLabel),labelData.header.openBalance)
            assertAreEqual("${labelData.header.currentPeriod} should show", asText(currentPeriodLabel),labelData.header.currentPeriod)
            assertAreEqual("${labelData.header.closeBalance} should show", asText(closeBalanceLabel),labelData.header.closeBalance)
            assertAreEqual("${labelData.header.debitCredit} should show", asText(debitCreditLabel1),labelData.header.debitCredit)
            assertAreEqual("${labelData.header.amount} should show", asText(amountLabel1),labelData.header.amount)
            assertAreEqual("${labelData.header.debit} should show", asText(debitLabel),labelData.header.debit)
            assertAreEqual("${labelData.header.credit} should show", asText(creditLabel),labelData.header.credit)
            assertAreEqual("${labelData.header.debitCredit} should show", asText(debitCreditLabel2),labelData.header.debitCredit)
            assertAreEqual("${labelData.header.amount} should show", asText(amountLabel12),labelData.header.amount)
        }
    }

    def checkSLHeader(){
        SBlabel.header.each{ key, value ->
            assertAreEqual("${key}: ${value} should show", asText(extendReportPage.SBHeader.get(key)),value)

        }

    }
    def checkCBJHeader(){
        CBJlabel.header.each{ key, value ->
            checkAreEqual("${key}: ${value} should show", asText(extendReportPage.CBJHeader.get(key)),value)

        }

    }
    def sublistHelper =
        {String name, ...args->
            def nargs =["custpage_atbl_report_sublist"]
            if(args) {
                if (args[0] in cust) {
                    nargs.add(cust.get(args[0]))
                    args = args - args[0]
                    if (args.size() > 0) nargs.add(args)
                } else {
                    nargs.add(args)
                }
                return currentRecord.invokeMethod(name, nargs.flatten())
            }
            return currentRecord.invokeMethod(name, nargs[0])
        }
    def subLedgerHelper =
            {String name, ...args->
                def nargs =["custpage_report_sublist"]
                if(args) {
                    if (args[0] in subledger) {
                        nargs.add(subledger.get(args[0]))
                        args = args - args[0]
                        if (args.size() > 0) nargs.add(args)
                    } else {
                        nargs.add(args)
                    }
                    return currentRecord.invokeMethod(name, nargs.flatten())
                }
                return currentRecord.invokeMethod(name, nargs[0])
            }
    def cbjLedgerHelper =
            {String name, ...args->
                def nargs =["custpage_report_sublist"]
                if(args) {
                    if (args[0] in cashBankJournal) {
                        nargs.add(cashBankJournal.get(args[0]))
                        args = args - args[0]
                        if (args.size() > 0) nargs.add(args)
                    } else {
                        nargs.add(args)
                    }
                    return currentRecord.invokeMethod(name, nargs.flatten())
                }
                return currentRecord.invokeMethod(name, nargs[0])
            }
    def checkParamsSetting(filter){
        if (context.isOneWorld()) {
            if (filter.subsidiary) assertAreEqual("Subsidiary should equal", filter.subsidiary, trimText(context.getFieldText("custpage_subsidiary")))
        }
        if (filter.datefrom) assertAreEqual("Date from should equal",filter.datefrom,trimText(context.getFieldValue("custpage_datefrom")))
        if (filter.dateto) assertAreEqual("Date to should equal",filter.dateto, trimText(context.getFieldValue( "custpage_dateto")))
        if (filter.accountfrom) assertAreEqual("Account from should equal", filter.accountfrom, trimText(context.getFieldText("custpage_accountfrom")))
        if (filter.accountto) assertAreEqual("Account to should equal", filter.accountto, trimText(context.getFieldText("custpage_accountto")))
        if (filter.accountlevel) assertAreEqual("Account level should equal", filter.accountlevel, trimText(context.getFieldText("custpage_accountlevel")))
        if (filter.location) assertAreEqual("Location should equal", filter.location, trimText(context.getFieldText("custpage_location")))
        if (filter.department) assertAreEqual("department should equal", filter.department, trimText(context.getFieldText("custpage_department")))
        if (filter.get("class")) assertAreEqual("class should equal", filter.class, trimText(context.getFieldText("custpage_class")))
    }
    def checkContexts(data,helper = sublistHelper, lebalList = cust){

        def result =[]
        for(int i =0 ; i < helper("getLineCount"); i ++){

            lebalList.eachWithIndex { item, index ->

                def value = asText(".//*[@id='custpage_atbl_report_sublistrow${i}']/td[${index + 1}]")
                // Note: for Consolidation we cannot provide expected amount for parent subsidiary which has children subsidiary that has different currency
                if (data.get(i).get(item.key)) {
                    assertAreEqual("${item.key} : line ${i}", data.get(i).get(item.key),value)
                }
            }
        }
    }
    def checkSLContexts(data, lebalList = cust, file_name="01"){
        def value
        //def result =[]
        for(int i =1 ; i <=data.size(); i ++){
            //def curLine = [:]
            lebalList.eachWithIndex {item, index ->
                value = asText(".//table[@id=\"custpage_report_sublist_splits\"]/tbody/tr[${i +2}]/td[${index + 1 }]")
                //println(" line ${i}: ${item.key} ${value}：${data.get(i-1).get(item.key)}")
                //println(value ==~ data[i-1].get(item.key))
                if (index == 3){
                    if (value)  assertAreNotEqual("GL# ${value}",  "",value)
                }else {
                    assertAreEqual("${item.key} : line ${i}", data.get(i - 1).get(item.key), value)
                }
                //curLine.put(item.key, value)
            }
            //result.add(curLine)
        }
        //new File( "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\${file_name}.json").
        //        write(new JsonBuilder(result).toPrettyString())
    }


    def assertSortAccout(expect){
        def actual = extendReportPage.getAccoutFromList()
        def actualto = extendReportPage.getAccoutToList()

        //new File( "src\\test\\java\\com\\netsuite\\chinalocalization\\extendreport\\data\\10.json").
        //    write(new JsonBuilder(actual).toPrettyString())
        def start = actual.findIndexOf{it -> it == expect[0]}
        def end = actual.findIndexOf{it -> it == expect[-1]}

        assertAreEqual("Account quantity is match expected",   expect.size(),end -start +1 ,)
        assertAreEqual("Account sample are match expected",  expect, actual[start..end] )
        start = actualto.findIndexOf{it -> it == expect[0]}
        end = actualto.findIndexOf{it -> it == expect[-1]}

        assertAreEqual("Account quantity is match expected",  end -start +1 , expect.size())
        assertAreEqual("Account sample are match expected", actualto[start..end], expect )
    }

    def clickRefresh() {
        extendReportPage.clickRefresh()
    }
    def clickExportExcel() {
        extendReportPage.clickExpToExcel()
    }
    def clickExportPDF() {
        extendReportPage.clickExpToPDF()
    }
    def getErrMsg(){
        return extendReportPage.getErrorMessage()
    }
    def clickErrDialogOK(){
        extendReportPage.clickErrorMessageOkButton()
    }

    def setAdvancedPrint(boolean status){
        if(context.isFeatureEnabled("ADVANCEDPRINTING")!=status){
            //switchToRole(administrator)

            enableFeaPage.navigateToURL()
            status? enableFeaPage.enableAdvancedPrinting():enableFeaPage.disableAdvancedPrinting()
            enableFeaPage.clickSave()
        }
    }

    def setUseLastSubAccount(boolean status){
        def statusString = status? "T":"F"
        if(context.getPreference("ONLYSHOWLASTSUBACCT")!=statusString){
            userPrePage.navigateToURL()
            userPrePage.setOnlyShowLastSubAcct(statusString, true)

        }
    }
    def setDateFormat(String format, boolean saveRecord){

        def key = "DATEFORMAT"
        userPrePage.navigateToURL()
        if(context.getFieldText(key)!= format){
            //userPrePage.navigateToURL()
            userPrePage.setDateFormat(format, saveRecord)
        }
    }
    def setShowAccountNum(boolean status){
        def statusString = status? "T":"F"
        if(context.getPreference("ACCOUNTNUMBERS")!=statusString) {
            //switchToRole(administrator)
            accountPrePage.navigateToURL()
            accountPrePage.setAccountNumbers(statusString)
            accountPrePage.clickSave()
            //switchToRole(accountant)
        }
    }
    def getUserName(){
        def employeeType = "employee"
        def columns = [
                record.helper.column("firstname").create(),
                record.helper.column("middlename").create(),
                record.helper.column("lastname").create()
        ]

        def filters = [
                record.helper.filter("email").is(context.getContext().getProperty("testautomation.nsapp.default.user"))
        ]
        return record.searchRecord("${employeeType}", filters, columns)
    }
    def checkOptionsValue(expValue,String fieldToCheck){
        if(!context.isOneWorld()&&fieldToCheck.equals("subsidiary")){
            return
        }
        def actualValue = asDropdownList(["fieldId": "custpage_${fieldToCheck}"]).getOptions()
        def beginPos = actualValue.findIndexOf {it -> it == expValue[0]}
        def endPos = actualValue.findIndexOf {it -> it == expValue[-1 ]}
        assertAreEqual("$fieldToCheck options selected are match expected", actualValue[beginPos..endPos], expValue )
    }

    def setSubsidiaryFiscalCalendar(subsidiaryName,calendarName){
        //switchToRole(administrator)
        subsidiaryPage.navigateToSubsidiaryEditPage(subsidiaryName)
        subsidiaryPage.setFiscalCalendarTex(calendarName)
        subsidiaryPage.clickSave()
        //switchToRole(accountant)
    }
    def enableAllClassificationFeatures(){
        //switchToRole(administrator)
        enableFeaPage.enableAllCustomFilters()
        //switchToRole(accountant)
    }

	/**
	 *  check Cash & Bank Journal Ledger report
	 * @param data expected data
	 * @param labelList  Label fieldid map in CBJL
	 *      cashBankJournal
	 */

	def checkCBJContexts(data, labelList){
		def value
		checkAreEqual("Check Expected line number.",cbjLedgerHelper("getLineCount"), data.size())
		for(int i = 0 ; i < data.size(); i ++){

			def expLine = data[i]
			labelList.eachWithIndex {item, index ->
				value = asText(".//*[@id='custpage_report_sublistrow${i}']/td[${index + 1 }]")
				//println "Line[${i}]: Column[${item.value}] - ${value}"

				if (item.value != "custpage_gl_number"){
					// check each column except gl_number
					checkAreEqual("Check Column:${item.value} in line[${i}].", value, expLine.get(item.key))

				} else {
					// check gl number
					if (expLine.get("gl_number") != "") {
						// when expected gl number is not null
						if (context.isOneWorld()) {
							checkTrue("Check GL# vlaue in line[${i}].", value.contains(expLine.get("gl_number")))
						} else {
							// GL# is not null when in SI
							checkTrue("Check GL# vlaue in line[${i}].", value != "")
						}

					} else {
						// when expected gl number is null
						checkAreEqual("Check Column:${item.value} in line[${i}].", value, expLine.get(item.key))
					}
				}
			}
		}
	}

	/**
	 *  Set default user preference according json data
	 * @param defaultUserPreference
	 * @return
	 */
	def setNormalUserPreference(defaultUserPreference){

		userPrePage.navigateToURL()

		defaultUserPreference.each { key, value ->
			if (key == "ONLYSHOWLASTSUBACCT") {
				// set this with value
				context.setFieldWithValue(key, value)
			} else {
				// set  format with text
				context.setFieldWithText(key, value)
			}
		}
		userPrePage.clickSave()
	}

	def checkConsolidateOpt(expResult, String fieldid) {
		def expOpts = []
		def dupResult = []
		if ( fieldid in expResult) {
			expOpts = asDropdownList(fieldId: "custpage_${fieldid}").getOptions()
			expResult.get(fieldid).each { value ->
				assertTrue("Check option ${fieldid} value: ${value}", expOpts.contains(value))
				dupResult = expOpts.findAll { it-> it == value  }
				assertAreEqual("Check no duplicate value in : ${fieldid}",1,dupResult.size())
			}
		}
		if (expResult.hasnot && (fieldid in expResult.hasnot)) {
			expResult.hasnot.get(fieldid).each { value ->
				assertFalse("Check has not certain options in : ${fieldid}", expOpts.contains(value))
			}
		}
	}
}
