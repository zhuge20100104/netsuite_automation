package com.netsuite.chinalocalization.lib

import com.google.inject.Inject
import com.netsuite.chinalocalization.cashflow.CONSTANTS.RecordErrorMsgEnum
import groovy.transform.Memoized

class NRecord {
    @Inject
    NetSuiteAppCN context
    @Inject
    SearchHelper helper
    @Inject
    NFormat format
    static final List<String> SELECT_NEW_LINES_SUBLISTS =
            ["item",
             "promotions",
             "expense",
             "currency",
             "line",
             "member",
             "discounteditems",
             "items",
             "itemvendor",
             "inventory",
             "earning"
            ]

    /**
     * @desc Initializes new records and returns an array containing all generated records information.
     * @param {Object} [required] recordData - It includes one object or an array of objects.
     * The data structure, refers to data block of vat/data/VATExportTest_zh_CN.json
     * @return{Array} An array containing all generated records.
     * The data structure should be [{internalid:***, trantype:***,tranid:***}, ....]
     */
    def createRecord(recordData) {
        def returnResults = []
        def preCreatedRecords = [:]
        recordData.each { tranEntry ->
            def toExecuteScript
            if (tranEntry.main.createdfrom) {
                toExecuteScript = transformRecord([tranEntry: tranEntry, preCreatedRecords: preCreatedRecords])
            } else {
                toExecuteScript = "var newRecord = nlapiCreateRecord('${tranEntry.main.trantype}', {recordmode: 'dynamic'});"
            }
            toExecuteScript += fillMainEntry(tranEntry.main)
            toExecuteScript += applySublist(tranEntry, preCreatedRecords)

            def content = """
                try {
                    var internalId = nlapiSubmitRecord(newRecord);
                } catch (ex) {
                    return JSON.stringify({
                        name: ex.name,
                        message: ex.message
                    });
                }
                var recordLoaded = nlapiLoadRecord("${tranEntry.main.trantype}", internalId);
                return JSON.stringify({
                    internalid: internalId,
                    trantype: "${tranEntry.main.trantype}",
                    tranid: recordLoaded.getFieldValue("tranid")
                });
            """
            toExecuteScript += content
            def returnResult = context.asJSON(text: context.executeScript(toExecuteScript))
            returnResult = handleResult(returnResult, tranEntry)
            returnResults.add(returnResult)
            if (tranEntry.main.id) {
                preCreatedRecords[tranEntry.main.id] = returnResult
            }
        }
        return returnResults
    }

    private transformRecord(params) {
        def tranMain = params.tranEntry.main
        def fromEntry = null
        params.preCreatedRecords.each { k, v ->
            if (tranMain.createdfrom == k) {
                fromEntry = v
                return
            }
        }
        return "var newRecord = nlapiTransformRecord('${fromEntry.trantype}', ${fromEntry.internalid}, '${tranMain.trantype}');"
    }

    private fillMainEntry(mainEntry) {
        def mainScript = ""
        mainEntry.each { k, v ->
            v = getInternalIdByText(k, v, mainEntry)
            if (k != "id" && k != "createdfrom" && k != "trantype" && v != null) {
                mainScript += "newRecord.setFieldValue('${k}', '${v}');"
            }
        }
        return mainScript
    }

    private applySublist(tranEntry, preCreatedEntries) {
        def sublistScript = ""
        tranEntry.each { sublistId, subTranEntries ->
            if (sublistId == "main") {
                return
            }
            if (!needSelectNewLine(sublistId)) {
                sublistScript += "newRecord = nlapiLoadRecord('${tranEntry.main.trantype}', nlapiSubmitRecord(newRecord), {recordmode: 'dynamic'});"
            }
            subTranEntries.each { subTranEntry ->
                sublistScript += doSubTranEntry([
                        preCreatedEntries: preCreatedEntries,
                        sublistId        : sublistId,
                        subTranEntry     : subTranEntry
                ])
            }
        }
        return sublistScript
    }

    def getInternalIdByText(fieldId, text, tranEntry) {
        if (fieldId == "subsidiary") {
            if (context.isOneWorld()) {
                return getIdByName([
                        fieldId: "name",
                        value  : text,
                        entity : "subsidiary"
                ])
            } else {
                return null
            }
        } else if (fieldId == "entity" || fieldId == "customer") {
            if (tranEntry && tranEntry.trantype == "vendorbill") {
                return getIdByName([
                        fieldId: "entityid",
                        value  : text,
                        entity : "vendor"
                ])
            }
            return getIdByName([
                    fieldId: "entityid",
                    value  : text,
                    entity : "customer"
            ])
        } else if (fieldId == "location") {
            return getIdByName([
                    fieldId: "name",
                    value  : text,
                    entity : "location"
            ])
        } else if (fieldId == "postingperiod") {
            return getIdByName([
                    fieldId: "periodname",
                    value  : text,
                    entity : "accountingperiod"
            ])
        } else if (fieldId == "trandate") {
            return format.formatDate(text)
        } else if (fieldId == "shipmethod") {
            return getIdByName([
                    fieldId: "itemid",
                    value  : text,
                    entity : "shipitem"
            ])
        } else if (fieldId == "discountitem" || fieldId == "discounteditem") {
            return getIdByName([
                    fieldId: "itemid",
                    value  : text,
                    entity : "discountitem"
            ])
        } else if (fieldId == "taxcode" || fieldId == "shippingtaxcode" || fieldId == "handlingtaxcode") {
            return getIdByName([
                    fieldId: "itemid",
                    value  : text,
                    entity : "salestaxitem"
            ])
        } else if (fieldId == "currency") {
            return getIdByName([
                    fieldId: "name",
                    value  : text,
                    entity : "currency"
            ])
        } else if (fieldId == "account" || fieldId == "accounthandling" || fieldId == "incomeaccount") {
            return getIdByName([
                    fieldId: "number",
                    value  : text,
                    entity : "account"
            ])
        } else if (fieldId == "custbody_cseg_cn_cfi" || fieldId == "custitem_cseg_cn_cfi" || fieldId == "custcol_cseg_cn_cfi") {
            return getIdByName([
                    fieldId: "name",
                    value  : text,
                    entity : "customrecord_cseg_cn_cfi"
            ])
        } else if (fieldId == "item") {
            return getIdByName([
                    fieldId: "itemid",
                    value  : text,
                    entity : "item"
            ])
        } else if (fieldId == "price") {
            if (text == "Custom") {
                return -1
            } else {
                return getIdByName([
                        fieldId: "name",
                        value  : text,
                        entity : "pricelevel"
                ])
            }
        } else if (fieldId == "department") {
            return getIdByName([
                    fieldId: "name",
                    value  : text,
                    entity : "department"
            ])
        } else if (fieldId == "class") {
            return getIdByName([
                    fieldId: "name",
                    value  : text,
                    entity : "classification"
            ])
        } else if (fieldId == "taxschedule" || fieldId == "taxschedulehandling") {
            return getIdByName([
                    fieldId: "name",
                    value  : text,
                    entity : "taxschedule"
            ])
        }
        return text
    }

    /**
     * @desc Loads an existing record from the system and update it.
     * @param {string} [required] type - The record internal ID name. This parameter is case-insensitive.
     * @param {int} [required] id - internalId for the record.
     * @param {Map} [required] data - {fieldId: newValue}
     */
    def updateRecord(type, id, data) {
        def toExecuteScript = "var record = nlapiLoadRecord('${type}', ${id});"
        data.each { k, v ->
            v = getInternalIdByText(k, v, [trantype: type])
            toExecuteScript += "record.setFieldValue('${k}', '${v}');"
        }
        toExecuteScript += "return JSON.stringify(nlapiSubmitRecord(record));"
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

    /**
     * Delete an existing record.
     * @param {string} [required] type - The record internal ID name.
     * @param {int} [required] id - The internalId for the record.
     * @return void
     */
    def deleteRecord(type, id) {
        context.executeScript("nlapiDeleteRecord('${type}', ${id})")
    }

    /**
     * Delete an existing record recursively.
     * @param {string} [required] type - The record internal ID name.
     * @param {int} [required] id - The internalId for the record.
     * @return boolean : true - delete success, false - delete failed
     */
    def deleteRecordRecurssive = { type, id ->
        def status = true;
        if (type != null) {
            //Looking for linked record
            def filter = [helper.filter('internalid').is(id)];
            def column = [helper.column('internalid').create(), helper.column('type').create(), helper.column('applyingtransaction').create()];
            def resultRecord = this.searchRecord('transaction', filter, column);
            resultRecord.each { linkedRecord ->
                if (linkedRecord.applyingtransaction != "") {
                    return deleteRecordRecurssive(null, linkedRecord.applyingtransaction);
                }
            }
        } else {
            //Get record ype from internalid and deleted
            def filters = [helper.filter('internalid').is(id), helper.filter('mainline').is('T')];
            def columns = [helper.column("recordtype").create()];
            type = searchRecord('transaction', filters, columns)[0];
            if (type != null) {
                type = type.recordtype;
            }

        }
        try {
            deleteRecord(type, id);
        } catch (ex) {
            def cause = "";
            def m;
            if ((m = ex.cause.toString() =~ /org.openqa.selenium.WebDriverException:\s+([^\s]+)./)) {
                cause = m.group(1);
            }
            if (cause != RecordErrorMsgEnum.RCRD_DSNT_EXIST.getCnLabel() && cause != RecordErrorMsgEnum.RCRD_DSNT_EXIST.getEnLabel()) {
                status = false;
            }
        }
        return status;
    }

    /** Performs a search using a set of criteria (your search filters) and columns (the results).
     * Results are limited to 1000 rows.
     * @param {string} [required] type - The record internal ID of the record type you are searching.
     * @param {Array} [optional] filters - Array containing strings of creating a single nlobjSearchFilter object
     * - or - creating an array of nlobjSearchFilter objects
     * - or - a search filter expression.
     * @param {Array} [optional] columns - Array containing strings of creating a single nlobjSearchColumn(name, join, summary) object
     * - or - creating an array of nlobjSearchColumn(name, join, summary) objects.
     * - or - a search column expression.
     * @return{Array} An array of objects corresponding to the searched records.
     */
    def searchRecord(type, filters, columns) {
        def toExecuteScript = "var filters = "
        toExecuteScript += filters ? " [" + filters.join(",") + "];\n" : "null;\n"
        toExecuteScript += "var columns = "
        toExecuteScript += columns ? " [" + columns.join(",") + "];\n" : "null;\n"

        toExecuteScript += """
            var search = nlapiCreateSearch('${type}', filters, columns);
            var results = search.runSearch();
            var resultColumns = results.getColumns();
            var returnResults = [];
            results.forEachResult(function(element) {
                var result = {};
                for (var i = 0; i < resultColumns.length; i++) {
                    var columnName = resultColumns[i].getName();
                    result[columnName] = element.getValue(columnName);
                }
                returnResults.push(result);
                return true;
            });
            return JSON.stringify(returnResults);
        """
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

    private doSubTranEntry(params) {
        def subTranEntry = params.subTranEntry
        def sublistScript = ""

        if (needSelectNewLine(params.sublistId)) { // add new line
            sublistScript += "newRecord.selectNewLineItem('${params.sublistId}');"
        } else { // selects an existing line in a sublist
            def referencedEntry = null
            params.preCreatedEntries.each { k, v ->
                if (k == subTranEntry.refid) { // get referenced transaction
                    referencedEntry = v
                    return
                }
            }
            sublistScript += getLineNum([
                    referencedEntry: referencedEntry,
                    sublistId      : params.sublistId
            ])
            sublistScript += "newRecord.selectLineItem('${params.sublistId}', lineNum);"
        }

        sublistScript += fillSublistLineItem([
                subTranEntry: subTranEntry,
                sublistId   : params.sublistId
        ])

        sublistScript += "newRecord.commitLineItem('${params.sublistId}');"
        return sublistScript
    }

    private fillSublistLineItem(params) {
        def subTranEntry = params.subTranEntry
        def lineItemScript = ""

        subTranEntry.each { fieldId, value ->
            if (fieldId == "refid") {
                return
            }
            value = getInternalIdByText(fieldId, value, null)
            lineItemScript += "newRecord.setCurrentLineItemValue('${params.sublistId}', '${fieldId}', '${value}');\n"
        }
        return lineItemScript
    }

    private getLineNum(params) {
        def referencedEntry = params.referencedEntry
        return """
            var lineNum = newRecord.findLineItemValue('${params.sublistId}', 'internalid', '${
                referencedEntry.internalid
            }');
            if (lineNum < 0) {
                lineNum = newRecord.findLineItemValue('${params.sublistId}', 'id', '${referencedEntry.internalid}');
            }
            if (lineNum < 0) {
                lineNum = newRecord.findLineItemValue('${params.sublistId}', 'refnum', '${referencedEntry.tranid}');
            }
        """
    }

    private needSelectNewLine(sublistId) {
        return SELECT_NEW_LINES_SUBLISTS.contains(sublistId)
    }

    /**
     * @desc Get internal id by name.
     * @param {string} [required] name
     * @return{Number} internal id
     */
    @Memoized
    private getIdByName(params) {
        def columns = [helper.column("internalid").create()]
        def filters = [helper.filter(params.fieldId).is(params.value)]
        def ids = searchRecord(params.entity, filters, columns)
        if (ids) {
            return ids[0].internalid
        }
    }

    /**
     * @desc Handle exception
     * Now only handle duplicate transaction record exception. More can be added.
     * @param {Map} [required] result - response from NS
     * @param {Map} [Optional] tran - transaction entry
     * @return {Map} or throw exception
     */
    private handleResult(result, tran) {
        if (result.name || result.message) { // netsuite response is an exception
            if (result.name.indexOf('DUP') != -1) { // error is duplicate record
                def columns = [
                        helper.column("internalid").create()
                ]
                if (tran.main.trantype.toLowerCase() != "customer") {
                    columns.add(helper.column("tranid").create())
                }
                def filters = [
                        helper.filter("externalid").is(tran.main.externalid)
                ]
                def duplicateRecords = searchRecord(tran.main.trantype, filters, columns)
                if (duplicateRecords) {
                    return [
                            "internalid": duplicateRecords[0].internalid,
                            "trantype"  : tran.main.trantype,
                            "tranid"    : duplicateRecords[0].tranid
                    ]
                }
            }
            throw new Exception("cannot create record. code:${result.name} message:${result.message}")
        }
        return result
    }

    private static class SearchHelper {
        def columnName, columnJoin, summary
        def filterName, filterJoin

        def create() {
            def searchColumn = "new nlobjSearchColumn('${columnName}'"
            searchColumn += this.columnJoin ? ",'${this.columnJoin}'" : ",null"
            searchColumn += this.summary ? ",'${this.summary}')" : ",null)"
            return searchColumn
        }

        def column(name) {
            this.columnName = name
            return this
        }

        def reference(targetTable) {
            this.columnJoin = targetTable
            return this
        }

        def group() {
            this.summary = 'group'
            return this
        }

        def count() {
            this.summary = 'count'
            return this
        }

        def sum() {
            this.summary = 'sum'
            return this
        }

        def min() {
            this.summary = 'min'
            return this
        }

        def max() {
            this.summary = 'max'
            return this
        }

        def avg() {
            this.summary = 'avg'
            return this
        }

        def summary(summaryType) {
            this.summary = summaryType
            return this
        }

        //TODO: add sort, more filters

        // USED FOR search.Filter
        def filter(columnName) {
            this.filterName = columnName
            return this
        }

        def join(targetTable) {
            this.filterJoin = targetTable
            return this
        }

        def is(values) {
            if (values) {
                return "new nlobjSearchFilter('${this.filterName}', ${this.filterJoin}, 'is', '${values}')"
            }
            return "new nlobjSearchFilter('${this.filterName}', ${this.filterJoin}, 'is', 'null')"
        }

        def isnot(values) {
            if (values) {
                return "new nlobjSearchFilter('${this.filterName}', ${this.filterJoin}, 'isnot', '${values}')"
            }
            return "new nlobjSearchFilter('${this.filterName}', ${this.filterJoin}, 'isnot', 'null')"
        }

        def startswith(values) {
            return "new nlobjSearchFilter('${this.filterName}', ${this.filterJoin}, 'startswith', '${values}')"
        }

        def anyof(values) {
            return "new nlobjSearchFilter('${this.filterName}', ${this.filterJoin}, 'anyof', ${values})"
        }

        def contains(values) {
            return "new nlobjSearchFilter('${this.filterName}', ${this.filterJoin}, 'contains', '${values}')"
        }

        def haskeywords(values) {
            return "new nlobjSearchFilter('${this.filterName}', ${this.filterJoin}, 'haskeywords', '${values}')"
        }
    }
    def errorPathPrefix


    def setErrorPathPrefix(String errorPathPrefix) {
        this.errorPathPrefix = errorPathPrefix
    }

    /**
     * @author Jingzhou.wang
     * @desc   On Transaction view or edit page, this function could get the internal id of a transaction
     * @return The integer value of the record whose form the user is currently on
     */
    def getRecordId() {
        context.executeScript("return nlapiGetRecordId()")
    }
}
