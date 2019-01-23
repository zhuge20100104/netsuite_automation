package com.netsuite.chinalocalization.vat

import com.google.inject.Inject
import com.netsuite.chinalocalization.lib.EditMachineCN
import com.netsuite.chinalocalization.lib.NRecord
import com.netsuite.chinalocalization.page.Setup.CompanyInformationPage
import com.netsuite.chinalocalization.page.Setup.SubsidiaryPage
import com.netsuite.testautomation.driver.BrowserActions
import com.netsuite.testautomation.html.Locator
import com.netsuite.testautomation.html.parsers.TableParser
import groovy.json.JsonBuilder
import org.junit.Before
import org.openqa.selenium.Keys

import javax.validation.constraints.AssertFalse

class VATEditPageTestSuite extends VATAppTestSuite {

	@Inject
	VATEditLocators editLocators
	@Inject
	SubsidiaryPage subsidiaryPage
	@Inject
	CompanyInformationPage companyInformationPage
	def previewData
	def mainData
	def itemData
	def mergedTran //store experect  merged tran
	def mergedChildList // store expect merged child  [internalId: itemlist]
	def saleList  //store mereged result Item for salelist
	def	value, expect_value
	def clickMerge() {
		asClick(editLocators.MergeButton)
	}

	def clickUnmerge() {
		asClick(editLocators.unmergeButton)
	}
	def clickCancel() {
		asClick(editLocators.cancelButton)
	}
	def clickSave() {
		asClick(editLocators.saveButton)
		waitForPageToLoad()
	}
	def clickConfirmOk(){
		asClick(editLocators.confirmOk)
	}
	def clickConfirmCancel(){
		asClick(editLocators.confirmCancel)
	}
    def setQueryParams(casename, TransactionType=[], invoiceType="", isSaleslist="") {

		def caseFilter= testData.get(casename).filter
        if (context.isOneWorld()) {
            asDropdownList(locator: locators.subsidiary).selectItem(editData.filter.subsidiary)
        }
        invoiceType? asDropdownList(locator: locators.invoiceType).selectItem(invoiceType):
            asDropdownList(locator: locators.invoiceType).selectItem(editData.filter.invoicetype_s)
        TransactionType? asMultiSelectField("custpage_transactiontype").setValues(*TransactionType):
            asMultiSelectField("custpage_transactiontype").
                setValues(editData.tranType.CustInvc, editData.tranType.CashSale)
        if (caseFilter.datefrom) context.setFieldWithValue("custpage_datefrom", caseFilter.datefrom)
        if (caseFilter.dateto) context.setFieldWithValue("custpage_dateto", caseFilter.dateto)
        if (caseFilter.tranid) context.setFieldWithValue("custpage_documentnumberfrom", caseFilter.tranid)
        if (caseFilter.to_tranid) context.setFieldWithValue("custpage_documentnumberto", caseFilter.to_tranid)
		isSaleslist? context.setFieldWithValue("custpage_saleslist",isSaleslist):
                context.setFieldWithValue("custpage_saleslist",editData.saleslist.yes)
    }
    //from  Preview page to edit page
	def clickEditAndLoad(options=[:],timeout = 60){
		def waitTimes = timeout.intdiv(2)
		if (options.functionName && options.text)
			for (i in 0..waitTimes) {
				if (context.invokeMethod(options.functionName, options.text) ){return true}
				Thread.sleep(2 * 1000)
				if(options.buttonName){
					"${options.buttonName}"()
					waitForPageToLoad()
				}
			}
		return false
	}
	def trimText(text) {
		return text.trim().replaceAll("[\\u00A0]+", "")
	}

	def clickOKInErrMsgBox() {
		asClick(editLocators.okInErrMsgBox)
		sleep(2 * 1000)
	}
	def checkGroupSameItem() {
		asClick(editLocators.groupSameItemChkbox)
	}
	def asClickTransCheckbox(index) {
		asClick(".//input[@id='custpage_applied_${index}']")
	}
	def asClickTransaction(i) {
		asClick(".//*[@id='custpage_header_sublist_row_${i}']/td[3]")
	}
	def getSublistHelper(sublistId){
		def cust= [
				custpage_header_sublist:[
						internalId      : "custpage_internalid",
						customerName    : "custpage_customer",
						tranType        : "custpage_type",
						docNum          : "custpage_doc_number",
						status          : "custpage_status",
						docDate         : "custpage_doc_date",
						amount          : "custpage_tax_excl_amount",
						afterdiscountamt: "custpage_amount_after_discount"],
				custpage_item_sublist:[
						itemName: "custpage_item_name",
						uom: "custpage_item_uom",
						model: "custpage_item_model",
						quantity: "custpage_item_quantity",
						unitPrice: "custpage_unit_pirce",
						amount: "custpage_item_tax_exclusive_amount",
						taxRate: "custpage_item_tax_rate",
						discountAmount: "custpage_item_discount_amount"
				]

		]
		def simple =
				{String name, ...args->
					def nargs =[sublistId]
					//nargs.add(sublistId)
					if(args){
						if( args[0] in cust.get(sublistId) ){
							nargs.add(cust.get(sublistId).get(args[0]))
							args = args - args[0]
							if(args.size()>0) nargs.add(args)
						}
						else { nargs.add(args)}
					}
					currentRecord.invokeMethod(name, nargs.flatten())
				}
		//return simple
	}
	def headerHelper = getSublistHelper("custpage_header_sublist")
	def itemHelper  = getSublistHelper("custpage_item_sublist")
    def getSublistValue(sublistId){
        def cust= [
			custpage_header_sublist:[
			 internalId      : "custpage_internalid",
			 customerName    : "custpage_customer",
			 tranType        : "custpage_type",
			 docNum          : "custpage_doc_number",
			 status          : "custpage_status",
			 docDate         : "custpage_doc_date",
			 amount          : "custpage_tax_excl_amount",
			 afterdiscountamt: "custpage_amount_after_discount"],
			custpage_item_sublist:[
					itemName: "custpage_item_name",
					uom: "custpage_item_uom",
					model: "custpage_item_model",
					quantity: "custpage_item_quantity",
					unitPrice: "custpage_unit_pirce",
					amount: "custpage_item_tax_exclusive_amount",
					taxRate: "custpage_item_tax_rate",
					discountAmount: "custpage_item_discount_amount"
			]

		]
		def simple =
		{itemId, lineNum->
			currentRecord.getSublistValue(sublistId, cust.get(sublistId).get(itemId), lineNum)
		}
		//return simple
	}
	def itemTextInColumnOfRow = getSublistValue("custpage_item_sublist")
	def headerTextInColumnOfRow = getSublistValue("custpage_header_sublist")

	def getTranIndexByValue(customer, tranType, docNum) {
		TableParser table = new TableParser(context.webDriver)
		List<HashMap<String, String>> transTable = table.parseTable(editLocators.tablePrimaryInfoXPath, null, editLocators.transRowsIteratorXPath)
		def index=0
		for (int i = 1; i < transTable.size(); i++) {

			HashMap<String, String> row = transTable.get(i)
			if ((customer == trimText(row.get("2"))) & (tranType == trimText(row.get("3"))) && (docNum == trimText(row.get("4")))) {
				index = i
				break
			}

		}
		return index

	}
	def checkTransactionInformation(index, expResult) {

		TableParser table = new TableParser(context.webDriver)
		List<HashMap<String, String>> transTable = table.parseTable(editLocators.tablePrimaryInfoXPath,null,editLocators.transRowsIteratorXPath)
		HashMap<String, String> row = transTable.get(index)

		//assertAreEqual("Check Transaction Line: Select", trimText(row.get("0")), expResult.select)
		// need to change to external id
		def filters = [record.helper.filter('externalid').is(expResult.internalId)]
		def columns = [record.helper.column("internalid").create()]
		def internalIds = record.searchRecord(expResult.type, filters, columns)
		assertAreEqual("Check Transaction Line: internal ID", trimText(row.get("1")), internalIds.get(0).internalid)
		assertAreEqual("Check Transaction Line: Customer Name", trimText(row.get("2")), expResult.customer)
		assertAreEqual("Check Transaction Line: Transaction Type", trimText(row.get("3")), expResult.tranType)
		assertAreEqual("Check Transaction Line: Document Number", trimText(row.get("4")), expResult.docNum)
		assertAreEqual("Check Transaction Line: Status", trimText(row.get("5")), expResult.status)
		assertAreEqual("Check Transaction Line: Document Date", trimText(row.get("6")), expResult.docDate)
		assertAreEqual("Check Transaction Line: Tax Exclusive Amount", trimText(row.get("7")), expResult.taxExAmount)
		assertAreEqual("Check Transaction Line: Amount after discount", trimText(row.get("8")), expResult.amountAftDis)

	}

	def checkItemsList(itemlabel, items) {
		def itemSublist = asSublist(editLocators.itemList)
		def lines = itemSublist.getRowCount()
		if (items == null) {
			assertAreEqual("Check the number of Item lines",lines -1, 0)
		} else {
			assertAreEqual("Check the number of Item lines",lines -1, items.size())
			for( int i = 0 ; i < lines - 1 ; i++ ) {
				assertAreEqual("Check Item Line: Item Name in row [" + i + "]", itemSublist.getTextInColumnOfRow(itemlabel.itemName, i), items[i].itemName)
				assertAreEqual("Check Item Line: Model in row [" + i + "]", itemSublist.getTextInColumnOfRow(itemlabel.model, i), items[i].model)
				assertAreEqual("Check Item Line: UOM in row [" + i + "]", itemSublist.getTextInColumnOfRow(itemlabel.uom, i), items[i].uom)
				assertAreEqual("Check Item Line: Quantity in row [" + i + "]", itemSublist.getTextInColumnOfRow(itemlabel.quantity, i), items[i].quantity)
				assertAreEqual("Check Item Line: Unit Price in row [" + i + "]", itemSublist.getTextInColumnOfRow(itemlabel.unitPrice, i), items[i].unitprice)
				assertAreEqual("Check Item Line: Tax Exclusive Amount in row [" + i + "]", itemSublist.getTextInColumnOfRow(itemlabel.amount, i), items[i].taxExAmount)
				assertAreEqual("Check Item Line: Tax Rate in row [" + i + "]", itemSublist.getTextInColumnOfRow(itemlabel.taxRate, i), items[i].taxRate)
				assertAreEqual("Check Item Line: Discount Amount in row [" + i + "]", itemSublist.getTextInColumnOfRow(itemlabel.discountAmount, i), items[i].disAmount)
			}
		}

	}
	//On preview page click export button then delete the export recored make
	//to make this Transactionn can be reexportd

	/**
	 * parse preview all table and get all result table
	 *
	 * @return List < Map < String colname , String value > >
	 */
	def updatePreResults(result) {

		TableParser table = new TableParser(context.webDriver)
		def tableLocator = "//*[@id='vat_report']/table[_index]"
        def recordNum = getResultCount()
		assertAreEqual("UI record num should equal with expect",result.size(), recordNum)
        def tmp, tranType, docNum, internalId,item
		for (int i = 0; i < getResultCount(); i++) {
			def headerTable = table.parseTable(tableLocator.replace("_index", String.valueOf(i * 2 + 1)), null, "//tbody//tr")
			def dataTable = table.parseTable(tableLocator.replace("_index", String.valueOf(i * 2 + 2)), null, "//tbody//tr")
			tmp = headerTable.get(Eval.me('0')).get("0").split(":")
			tranType = tmp.size()>1 ? tmp[1].trim() : ""
			tmp =  headerTable.get(Eval.me('0')).get("1").split(":")
			docNum = tmp.size()>1 ? tmp[1].trim() : ""
			internalId = dataTable.get(Eval.me('dataTable',dataTable,'0')).get("0")
			assertFalse("internalId:${internalId} should not starsWith CON",internalId.startsWith("CON"))
			item = result.find{
				it.docNum == docNum && it.tranType== tranType
			}
			item.internalId = internalId
		}

		result.each{
			it.amount = bigDecRound(it.amount, 2)
			it.discountAmount = bigDecRound(it.discountAmount, 2)
			it.item.each{ i->
			   i.each { key, value ->
                   //i."${key}" =0
				   if (key in ["unitPrice", "amount", "taxRate", "discountAmount"] && value){
                       i."${key}" = bigDecRound(value, 2)
                   }

			   }
			}
		}
		return result

	}
	def getAllResults(filename) {
		def result = []
		TableParser table = new TableParser(context.webDriver)
		def tableLocator = "//*[@id='vat_report']/table[_index]"
		def tableStructure = [
				header: [
						docNum: [row: "0", col: "1"],
						tranType :[row: "0", col: "0"],
				],
				body  : [
						internalId: [row: "0", col: "0"],
						customerName:[row: "0", col: "3"],
						docDate : [row: "0", col: "1"],
                        lineQuantity : [row: "0", col: "2"],
						taxRegisNum: [ row: "0", col : "4"],
						addressAndPhone: [ row: "0", col : "5"],
						bankAccount: [ row: "0", col : "6"],
						remark: [ row: "0", col : "7"],
						itemNameForSale : [ row: "0", col : "8"],
						//saleListName: [ row: "0", col : "8"],
						message: [row: "dataTable.size() - 1", col: "1"]
				],
				//Get items from preView page
				item : [
						itemName: [ col: "0"],
						model: [ col: "2"],
						uom: [ col: "1"],
						quantity: [ col: "3"],
						unitPrice: [ col: "4"],
						amount: [ col: "5"],
						taxRate:[ col: "6"],
						discountAmount:[ col: "7"]
				]
		]

		for (int i = 0; i < getResultCount(); i++) {
			def curLine = [:]
			def headerTable = table.parseTable(tableLocator.replace("_index", String.valueOf(i * 2 + 1)), null, "//tbody//tr")
			def dataTable = table.parseTable(tableLocator.replace("_index", String.valueOf(i * 2 + 2)), null, "//tbody//tr")
			tableStructure.header.each { k, v ->
				String [] tmp= headerTable.get(Eval.me(v.row)).get(v.col).split(":")
				curLine.put(k, tmp.size()>1 ? tmp[1].trim() : "")
			}
			tableStructure.body.each { k, v ->
				def t = dataTable.get(Eval.me('dataTable',dataTable,v.row)).get(v.col)
				curLine.put(k, t?: "")
			}
			curLine.put("amount" ,0)
			curLine.put("afterdiscountamt",0 )
			curLine.lineQuantity = Integer.parseInt(curLine.lineQuantity)
			curLine.put("vatlineQuantity", curLine.lineQuantity)

			if (curLine.tranType){
				curLine.status = ""
			}else {curLine.status = editData.mergeStatus}
			curLine["item"]=[]
			def sumDiscount =0
			def lineCount = curLine.get("lineQuantity") + 2
			def m
			for(int j=2; j < lineCount; j++){
				def item = [:]
				tableStructure.item.each { k, v ->
					m = dataTable.get(Eval.me(j.toString())).get(v.col)
					item.put(k, m.isNumber() ? m.toDouble() : m )
					//println("${k}, ${m.isNumber() ? m.toDouble() : m}")
				}
				if(item.discountAmount){
					curLine.vatlineQuantity +=1
					item.discountAmount = item.discountAmount.round(2)
					sumDiscount += item.discountAmount
				}else item.discountAmount =0
				if(!item.taxRate) item.taxRate =0
				curLine["item"].add(item)
				if(item.amount) item.amount = item.amount.round(2)
				curLine.amount += item.amount

			}


			//To Do list check if  no item
			curLine.discountAmount =(sumDiscount !=0 )? sumDiscount.round(2): 0
			curLine.afterdiscountamt = curLine.amount - sumDiscount
			if (curLine.afterdiscountamt !=0){
				curLine.afterdiscountamt = curLine.afterdiscountamt.round(2)
			}
			if (curLine.amount !=0){
				curLine.amount = curLine.amount.round(2)
			}
            //curLine.amount = curLine.amount.round(2)
			result[i]= curLine
		}
		new File( "src\\test\\java\\com\\netsuite\\chinalocalization\\vat\\data\\${filename}.json").
				 write(new JsonBuilder(result).toPrettyString())
		return result
	}
	def getTranNum(String tranType){
		//	tranType.equals(String.valueOf(it.value))}?.key
		return editData.tranNum.get(tranType)
	}
	// Check the merged tran
    def prepareUnMerge(int mergedLineNum,boolean isSalesList){
		asClickTransCheckbox(mergedLineNum)
		/*
		currentRecord.selectLine("custpage_header_sublist", mergedLineNum)

		
		if(isSalesList) {
			itemlabels.each{ key, label ->
				value = itemTextInColumnOfRow(key, 1)
				if (key in ["discountAmount", "amount"]) {
					value = value? value.replaceAll(",", "").toDouble():0
				}
				expect_value = saleList.get(key)
				assertAreEqual("${label} expected is ${expect_value}", value, expect_value)
			}
			assertAreEqual(" Merge items size is 1", getItemCount(), 1)
		}
		else {
			itemData = mergedTran.get("item")
			assertAreEqual(" Merge items size is ${itemData.size()}", getItemCount(), itemData.size())

		}*/
	}
	def bigDecRound(double foo,int num) {
		return foo.round(num)
	}
	def setMergedTran(){
		if(mergedTran) {
			saleList.discountAmount += mainData.discountAmount
			saleList.amount += mainData.amount
			mergedTran.amount += mainData.amount
			mergedTran.afterdiscountamt += mainData.afterdiscountamt
		}else {
			mergedTran = [:]
			mergedChildList = [:]
			saleList = [:]
			mainData.each { k, v -> mergedTran.put(k, v) }
			mergedTran.with{
				internalId = ""
				docNum = ""
				tranType = ""
				docDate = new Date().format('yyyyMMdd').toString()
				itemNameForSale = ""
			}
			mergedTran.item = []
			mergedTran.possibleDoclist = []
			mergedTran.status = editData.mergeStatus
			saleList.itemName=editData.saleListName
			saleList.model=""
			saleList.uom=""
			saleList.quantity=""
			saleList.unitPrice=""
			saleList.taxRate=""
			saleList.discountAmount  = mainData.discountAmount
			saleList.amount = mainData.amount
		}

	}
	/*
	Check data display match what we get from preview page
	if enable saleslist items may change if line >8 will show saleslist
	 */
	
	def prepareMerge(docNumList= [], boolean isSalesList= true){
		//init merged Data
		int startMergeLine
		//Check edit page header part
		for(int i =1 ; i <=previewData.size();i++){
			headerlabels.each{ key, label ->
				value = headerTextInColumnOfRow(key, i).trim()
				if ("internalId" ==key) {
					mainData = getPreviewData(value)
					itemData = mainData.get("item")
					if (!startMergeLine){ startMergeLine = i}//
				}

                if( key in ["afterdiscountamt", "amount"]) {
                    value = value?  value.replaceAll(",","").toDouble() :0
                    mainData."${key}" =value

                }
				expect_value = mainData.get(key)
				assertAreEqual("${label} expected is ${expect_value}", value, expect_value)

			}
			//Only need check need merged tran ( in docNumList)
			if (docNumList && !docNumList.contains(mainData.docNum)){ break }
			//Generate merged Tran use for compare with DEV merged result
			asClickTransCheckbox(i)
            setMergedTran()
			setMergedItem()
			String tranNum = getTranNum( mainData.tranType)
			if (tranNum in ["1","2"]) mergedTran.remark =""

		}

		saleList.discountAmount = (saleList.discountAmount !=0)? saleList.discountAmount.round(2) : 0
        saleList.amount = (saleList.amount !=0 )?saleList.amount.round(2):0
        mergedTran.amount =(mergedTran.amount !=0 )?mergedTran.amount.round(2): 0
        mergedTran.afterdiscountamt = (mergedTran.afterdiscountamt !=0)? mergedTran.afterdiscountamt.round(2): 0
		return startMergeLine

	}
    /*
    check interal Id not null
     */

	def checkMergedPreview( boolean isSalesList){
		def tmpData = getAllResults()
		def comStr
		if (isSalesList) mergedTran.itemNameForSale = editData.saleListName
		previewData.each{ record -> 
			comStr= record.internalId + record.docNum
			if (record.internalId in mergedChildList.keySet()){
				assertFalse("Transaction :$record.internalId not in result", tmpData.any { line -> comStr.equals(String.valueOf(line.internalid+ line.docno)) })
			}else{
				assertTrue("Transaction :$record.internalId  in result", tmpData.any { line -> comStr.equals(String.valueOf(line.internalid+ line.docno)) })
			}
		}

		def tmpTran = tmpData.find{ line -> mergedTran.internalId== line.internalid }
        assertTrue("Merged internalId starts with CON", tmpTran!= null)
        assertTrue("Merged tran docNum is null :  in result", tmpTran.docno == "")

/*		tmpTran.each{ key, value ->
			if(key in editData.preViewHeader && !key.equals("message") ) {
				if (key.equals("internalId")) {
					assertAreNotEqual("Merged internalId not null", String.valueOf(value), "")
				}
			}
		}*/
		//Check merged items
		//assertTrue("Item size equals expect result", mergedTran.item.size()== tmpTran.item.size())
/*		def mergedItems = mergedTran.get("item")
		for( uiItem  in tmpTran.get("item")){
			assertTrue("Item:$uiItem.itemName unitPrice : $uiItem.unitPrice" +
					"taxRate: $uiItem.taxRate in merged result",
					mergedItems.any{ it-> it.itemName==uiItem.itemName && it.unitPrice == uiItem.unitPrice   \
					&& it.taxRate == uiItem.taxRate && it.quantity == uiItem.quantity \
				    && it.quantity == uiItem.quantity && it.amount == uiItem.amount
					})
		}*/

		return tmpData
	}

	def checkUnMergedPreview(){
		def tmpData = getAllResults()
		def comStr
		previewData.each{ record ->
			comStr= record.internalId + record.docNum
			assertTrue("Transaction :${record.internalId} in result", tmpData.any { line -> comStr.equals(String.valueOf(line.internalid+ line.docno)) })
		}
		assertFalse("Transaction :${mergedTran.docNum} not  in result", tmpData.any { line -> mergedTran.docNum.equals(String.valueOf(line.docno)) })
	}
	/*Check merged result on Edit page
	 @mergedLineNum  merged tran line number, this function will check this line use for  upcoming unmerge
	 @isSalesList   if true check sales list format
	 @is GroupItem if trun check group same item format
	 */
	def checkMergedResult(int mergedLineNum, boolean isSalesList, boolean isGroupItem = false){

		//Check merged tran on head line
		headerlabels.each { key, label ->
			value = headerTextInColumnOfRow(key, mergedLineNum).trim()
			expect_value = mergedTran.get(key)
			if( key in ["afterdiscountamt","amount"]) {
				value = value? value.replaceAll(",",""):0
			}
			/*get InternalId number*/
			if (key.equals("internalId")){
				assertTrue("$label is expect [$value] ",value.startsWith("CON"))
				expect_value =mergedTran.internalId = value
			}
			assertAreEqual("${label} expected is ${expect_value}", value, String.valueOf(expect_value))
		}
		//Get merged child  add to
		def tmpItems, internalid
		for(int i =0;i < mergedChildList.size(); i ++){
			internalid = headerTextInColumnOfRow("internalId", mergedLineNum + i +1 ).trim()
			assertTrue("InternalId : ${internalid} in childe list", mergedChildList.containsKey(internalid))
			tmpItems = mergedChildList.get(internalid)
			//if group by item will call groupItem function else simply  add to meregedTran item
			if(isGroupItem){
				groupItem(tmpItems)
				continue
			}

			println("Simple merge item ")
			mergedTran.item.add( tmpItems)
		}
		// set lineQuantity ( item lines), vatlineQuantity ( plus discount line )
		def discountList = mergedTran.item.findAll{ it.discountAmount!=0}
		// delete item where amount == 0
		mergedTran.item = mergedTran.item.flatten()
		mergedTran.item = mergedTran.item.findAll{ it.amount !=0 }
		mergedTran.lineQuantity = mergedTran.item.size()
		mergedTran.vatlineQuantity= mergedTran.lineQuantity +  (discountList?discountList.size():0)
		//Check perant's items
		println("Check merged item")

		headerHelper("selectLine", mergedLineNum)
		if(isSalesList){
			itemlabels.each{ key, label ->
				assertAreEqual( "${label} exist", label, itemHelper("getSublistLabel",key,1))
				value = itemTextInColumnOfRow(key, 1)
				expect_value = saleList.get(key)
				if( key in ["discountAmount","amount"]) {
					value =value?value.replaceAll(",","").toDouble().round(2): 0
				}
				assertAreEqual("${label} expected is" + expect_value, value, expect_value)
			}
		}else {
			itemData = mergedTran.get("item")
			int itemCount = getItemCount()
			assertAreEqual(" Merge items size is ${itemData.size()}",itemCount, itemData.size())
            def uiItem=[:]
			for (int j = 1; j <= itemCount; j++) {
				//Get item line
				itemlabels.each{ key, label ->
					value = itemTextInColumnOfRow(key, j)
					if (key in ["discountAmount", "amount", "quantity", "unitPrice", "taxRate"]) {
						value = value ? value.toDouble().round(2) : 0
					}
					uiItem.put(key,value)
				}
				//Check item line match expect result
				assertTrue("Item:$uiItem.itemName unitPrice : $uiItem.unitPrice" +
						"taxRate: $uiItem.taxRate match merged result",
						itemData.any { it -> it.itemName == uiItem.itemName && it.unitPrice == uiItem.unitPrice   \
					&& it.taxRate == uiItem.taxRate && it.discountAmount == uiItem.discountAmount \
                    && it.quantity == uiItem.quantity && it.amount == uiItem.amount
				})
			}
		}
	}
	/* put all the item to mergedChild List
	   before me'rge
	 */
	def setMergedItem(){
		for(item in itemData){
			item.unitDiscount =0
			if (item.quantity !=0 && item.discountAmount !=0)
			item.unitDiscount = (item.discountAmount/item.quantity).round(2)
		}
		mergedChildList.put(mainData.internalId, itemData)
	}
	// If find item already in mereged itemlist  add amount and discount quality to the item
	// else add the item to itemlist
	// for ["运费","手续费"]    quality =1 amount grouped
	def	groupItem(tmpItems){
		def k
		for(item in tmpItems) {
			if(mergedTran.item.size() ==0){
				mergedTran.item.add(item)
				continue
			}
			k =  mergedTran.get("item").find{ it ->
				it.itemName == item.itemName  && it.unitPrice == item.unitPrice   \
				&& it.taxRate == item.taxRate &&  it.unitDiscount == item.unitDiscount
			}
			// for ["运费","手续费"]  just check taxRate
			if (item.itemName in ["运费","手续费"]) {
				k = mergedTran.get("item").find{ it -> it.itemName == item.itemName && it.taxRate == item.taxRate }
			}
			if(k){

				k.amount += item.amount
				k.discountAmount += item.discountAmount
				if ( k.amount !=0) k.amount = k.amount.round(2)
				if (k.discountAmount !=0) k.discountAmount = k.discountAmount.round(2)
				if (k.itemName in ["运费","手续费"]) {
					k.unitPrice = Math.abs(k.amount)
					continue
				}
                println("In grouop merged: ${k.amount}: ${k.discountAmount}")
				k.quantity += item.quantity
			}else {
				mergedTran.item.add(item)

			}
		}
	}


	//check unmerged result
	// merged doc number not in head list  and original child in head list
	def checkUnMergedResult(int mergedLineNum){

        def internalid, tmpDoc
		for(int i =0;i < mergedChildList.size(); i++){
			internalid = headerTextInColumnOfRow("internalId", mergedLineNum + i)
			assertTrue("Child internalId :"+ internalid +"in list", mergedChildList.containsKey(internalid))
			tmpDoc =  headerTextInColumnOfRow("docNum", mergedLineNum + i)
			assertFalse("Merged tran has ben deleted", tmpDoc.equals(mergedTran.docNum))
		}

	}
	def getItemCount() {
		//def ItemRows = asElements("//tr[starts-with(@id, 'custpage_item_sublist_row_')]")
		//return ItemRows == null ? 0 : ItemRows.size()
		currentRecord.getLineCount("custpage_item_sublist").toInteger()
	}

	def getHeaderCount() {
		currentRecord.getLineCount("custpage_header_sublist").toInteger()
	}

	/*
	   Get a transaction on preview page by internalId
	 */
	def getPreviewData(String internalId){
		if (previewData) {
			return previewData.find { line ->internalId.equals(String.valueOf(line.internalId)) }
		}
		else{
			return null
		}
	}
	/*
	delete vat customized trans status in consolidated  export import
	 */
	def cleanVatTrans(trans){
		trans.each { dirtyRecord ->

			println dirtyRecord
			def vatRecordType = "customrecord_cn_vat_invoices"
			def columns = [
					record.helper.column("internalid").create(),
					record.helper.column("parent").create()
			]

			def filters = [
					record.helper.filter("custrecord_cn_invoice_type_fk_tran").isnot(null),
					record.helper.filter("custrecord_cn_invoice_type_fk_tran").is(dirtyRecord)
					//record.helper.filter("custrecord_cn_invoice_type_fk_tran").is(dirtyRecord)
			]
			def vatRecords = record.searchRecord("${vatRecordType}", filters, columns)
			if (!vatRecords) {
				filters = [
						record.helper.filter("custrecord_cn_vat_invoice_code").isnot(null),
						record.helper.filter("custrecord_cn_vat_invoice_code").is(dirtyRecord)
						//record.helper.filter("custrecord_cn_vat_invoice_code").is(dirtyRecord)
				]
				vatRecords = record.searchRecord("${vatRecordType}", filters, columns)
			}

			vatRecords.each { vatRecord ->
				println("VaRecord : ${vatRecord.internalid}")
				deleteItem(vatRecord.internalid)
				//delete children customrecord_cn_vat_invoices
				record.deleteRecord("${vatRecordType}", vatRecord.internalid)
				if(!isSiblingExisted(vatRecord.parent)){
					//delete parent customrecord_cn_vat_invoices
					deleteItem(vatRecord.parent)
					record.deleteRecord("${vatRecordType}", vatRecord.parent)
				}
			}
			//record.deleteRecord(dirtyRecord.trantype, dirtyRecord)
		}
	}

	/*
	Only delete consolidated tran on costom record
	 */

	def exportAndClear(){
		//switchToRole(administrator)

		previewData.each { Record ->
			def vatRecordType = "customrecord_cn_vat_invoices"
			def columns = [record.helper.column("internalid").create()]
			//def internalid = exportRecord

			def filters = [
					record.helper.filter("custrecord_cn_invoice_type_fk_tran").isnot(null),
					record.helper.filter("custrecord_cn_invoice_type_fk_tran").is(Record.internalId),
					record.helper.filter("custrecord_cn_vat_status").is(1)
			]
			def vatRecords = record.searchRecord("${vatRecordType}", filters, columns)
			if (!vatRecords) {
				filters = [
						record.helper.filter("custrecord_cn_vat_invoice_code").isnot(null),
						record.helper.filter("custrecord_cn_vat_invoice_code").is(Record.internalId)
				]
				vatRecords = record.searchRecord("${vatRecordType}", filters, columns)
			}
			vatRecords.
					each { vatRecord -> record.deleteRecord("${vatRecordType}", vatRecord.internalid) }

		}
		 //switchToRole(accountant)
	}

	def getConsolidatedTransIndex() {
		def index
		for(int i=1 ; i <= getHeaderCount(); i ++){
			if (headerTextInColumnOfRow("internalId", i).startsWith("CON")) {
				index = i
				break
			}
		}
		return index
	}

	def getIndexByTextInColumn(text) {
		def index
		for(int i=1 ; i <= getHeaderCount(); i ++){
			if (headerTextInColumnOfRow("docNum", i)== text) {
				index = i
				break
			}
		}
		return index
	}

	def clickEditWithWaitingForMerge() {
		int i=30
		while (!context.doesFieldExist("merge") && i>0){
			Thread.sleep(2 * 1000)
			clickEdit()
			waitForPageToLoad()
			i--
		}
		i=30
		while (!context.isTextVisible(editData.groupSameItem) && i >0){
			Thread.sleep(2 * 1000)
			i--
		}
		println("in edit page!!1")
	}

}
