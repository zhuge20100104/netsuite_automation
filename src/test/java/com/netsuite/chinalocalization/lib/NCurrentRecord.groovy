package com.netsuite.chinalocalization.lib

import com.google.inject.Inject

class NCurrentRecord {
    @Inject
    NetSuiteAppCN context

    @Inject
    NRecord record

    /**
     * @desc Returns the values of specified fields.
     * @param {string | List} [required] fieldId - The internal ID of a standard or custom body field.
     * Also support getting value for multiple fields.
     * @return {number | Date | string | map | boolean true | false}
     */
    def getValue(fieldId) {
        def toExecuteScript
        if (fieldId instanceof List) {
            toExecuteScript = "var values = {};"
            fieldId.each { id ->
                toExecuteScript += "values['${id}'] = nlapiGetFieldValue('${id}');"
            }
            toExecuteScript += "return JSON.stringify(values);"
        } else {
            toExecuteScript = "return JSON.stringify(nlapiGetFieldValue('${fieldId}'));"
        }
        return context.asJSON(text : context.executeScript(toExecuteScript))
    }

    /**
     * @desc Returns the text representation of a field value.
     * @param {string | List} [required] fieldId - The internal ID of a standard or custom body field.
     * Also support getting value for multiple fields.
     * @return {string | map}
     */
    def getText(fieldId) {
        def toExecuteScript
        if (fieldId instanceof List) {
            toExecuteScript = "var values = {};"
            fieldId.each { id ->
                toExecuteScript += "values['${id}'] = nlapiGetFieldText('${id}');"
            }
            toExecuteScript += "return JSON.stringify(values);"
        } else {
            toExecuteScript = "return JSON.stringify(nlapiGetFieldText('${fieldId}'));"
        }
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

    /**
     * @desc Sets the value of a field.
     * Also support setting values for multiple fields.
     * @param {Map} [required] options - groovy map for fields
     * options.fieldId [required] - The internal ID of a standard or custom body field.
     * options.value [required] - The value to set the field to.
     * @return void
     */
    def setValue(options) {
        def toExecuteScript = ""
        options.each { fieldId, value ->
            toExecuteScript += "nlapiSetFieldValue('${fieldId}', '${value}');"
        }
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

    /**
     * @desc Sets the value of the field by a text representation.
     * Also support setting texts for multiple fields.
     * @param {Map} [required] options - groovy map for fields.
     * options.fieldId [required] - The internal ID of a standard or custom body field.
     * options.text [required] - The text or texts to change the field value to.
     * @return void
     */
    def setText(options) {
        def toExecuteScript = ""
        options.each { fieldId, value ->
            if (needConvertTextToValue(fieldId)) {
                def id = getInternalIdByText(fieldId, value, options["entitytype"])
                if(id) {
                    toExecuteScript += "nlapiSetFieldValue('${fieldId}', '${id}');"
                }
                else{
                    toExecuteScript += "nlapiSetFieldText('${fieldId}', '${value}');"
                }
            }else {
                toExecuteScript += "nlapiSetFieldText('${fieldId}', '${value}');"
            }
        }
        if(toExecuteScript.contains("subsidiary")) {
            if(context.isOneWorld()) {
                return context.asJSON(text: context.executeScript(toExecuteScript))
            }
        }else{
            return context.asJSON(text: context.executeScript(toExecuteScript))
        }
    }

    /**
     * @desc Returns the number of lines in a sublist.
     * @param {string} [required] sublistId - The internal ID of the sublist.
     * @return {int} number
     */
    def getLineCount(sublistId) {
        return Integer.parseInt(context.executeScript("return nlapiGetLineItemCount('${sublistId}');"))
    }

    /**
     * @desc Returns the line number of the currently selected line. Note that line indexing begins at 1.
     * @param {string} [required] sublistId - The internal ID of the sublist.
     * @return {int} number
     */
    def getCurrentSublistIndex(sublistId) {
        return Integer.parseInt(context.executeScript("return nlapiGetCurrentLineItemIndex('${sublistId}');"))
    }

    /**
     * @desc Returns a text representation of the field value in the currently selected line.
     * Also support getting texts for multiple fields in currently selected line.
     * @param {string} [required] sublistId - The internal ID of the sublist.
     * @params {string | list } [required] fieldId - The internal ID of a standard or custom sublist field.
     * @return {string | map }
     */
    def getCurrentSublistText(sublistId, fieldId) {
        def toExecuteScript
        if (fieldId instanceof List) {
            toExecuteScript = "var values = {};"
            fieldId.each { id ->
                toExecuteScript += "values['${id}'] = nlapiGetCurrentLineItemText('${sublistId}', '${id}');"
            }
            toExecuteScript += "return JSON.stringify(values);"
        } else {
            toExecuteScript = "return JSON.stringify(nlapiGetCurrentLineItemText('${sublistId}', '${fieldId}'));"
        }
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

    /**
     * @desc Returns the value of a sublist field on the currently selected sublist line.
     * Also support getting values for multiple fields in currently selected line.
     * @param {string} [required] sublistId - The internal ID of the sublist.
     * @params {string | list } [required] fieldId - The internal ID of a standard or custom sublist field.
     * @return {string | map }
     */
    def getCurrentSublistValue(sublistId, fieldId) {
        def toExecuteScript
        if (fieldId instanceof List) {
            toExecuteScript = "var values = {};"
            fieldId.each { id ->
                toExecuteScript += "values['${id}'] = nlapiGetCurrentLineItemValue('${sublistId}', '${id}');"
            }
            toExecuteScript += "return JSON.stringify(values);"
        } else {
            toExecuteScript = "return JSON.stringify(nlapiGetCurrentLineItemValue('${sublistId}', '${fieldId}'));"
        }
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

    /**
     * @desc Returns the value of a select field in a sublist.
     * Also support getting values for multiple fields in selected line.
     * @param {string} [required] sublistId - The sublist internal ID
     * @param {string | list} [required] fieldId - The internal ID of the field (line item) whose value is being returned.
     * @param {int} [required] line - The line number for this field.
     * Note the first line number on a sublist is 1 (not 0).
     * @return {string | map} The string value of a sublist line item.
     */
    def getSublistValue(sublistId, fieldId, line) {
        def toExecuteScript
        if (fieldId instanceof List) {
            toExecuteScript = "var values = {};"
            fieldId.each { id ->
                toExecuteScript += "values['${id}'] = nlapiGetLineItemValue('${sublistId}', '${id}', '${line}');"
            }
            toExecuteScript += "return JSON.stringify(values);"
        } else {
            toExecuteScript = "return JSON.stringify(nlapiGetLineItemValue('${sublistId}', '${fieldId}', '${line}'));"
        }
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

    /**
     * @desc Returns the value of a sublist field in a text representation.
     * Also support getting text for multiple fields in selected line.
     * @param {string} [required] sublistId - The sublist internal ID
     * @param {string | list} [required] fieldId - The internal ID of the field (line item) whose value is being returned.
     * @param {int} [required] line - The line number for this field.
     * Note the first line number on a sublist is 1 (not 0).
     * @return {string | map} The string value of a sublist line item.
     */
    def getSublistText(sublistId, fieldId, line) {
        def toExecuteScript
        if (fieldId instanceof List) {
            toExecuteScript = "var values = {};"
            fieldId.each { id ->
                toExecuteScript += "values['${id}'] = nlapiGetLineItemText('${sublistId}', '${id}', '${line}');"
            }
            toExecuteScript += "return JSON.stringify(values);"
        } else {
            toExecuteScript = "return JSON.stringify(nlapiGetLineItemText('${sublistId}', '${fieldId}', '${line}'));"
        }
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

    /**
     * @desc Returns the value of a sublist field in a text representation.
     * Also support getting text for multiple fields in selected line.
     * @param {string} [required] sublistId - The sublist internal ID
     * @param {string | list} [required] fieldId - The internal ID of the field (line item) whose value is being returned.
     * @param {int} [required] line - The line number for this field.
     * Note the first line number on a sublist is 1 (not 0).
     * @return {string | map} The string value of a sublist line item.
     */
    def getSublistLabel(sublistId, fieldId, line) {
        def toExecuteScript
        if (fieldId instanceof List) {
            toExecuteScript = "var values = {};"
            fieldId.each { id ->
                toExecuteScript += "values['${id}'] = nlapiGetLineItemLabel('${sublistId}', '${id}', '${line}');"
            }
            toExecuteScript += "return JSON.stringify(values);"
        } else {
            toExecuteScript = "return JSON.stringify(nlapiGetLineItemLabel('${sublistId}', '${fieldId}', '${line}'));"
        }
        return context.asJSON(text: context.executeScript(toExecuteScript))
    }

     /**
     * @desc Find the line number of a specific field in a sublist.
     * @param {string} [required] sublistId - The sublist internal ID.
     * @param  {string} [required] fieldId - The field internal ID.
     * @param {string} [required] value - The value of the field.
     * @return {int} The line number of a specific sublist field.
     */
    def findSublistLineWithValue(sublistId, fieldId, value) {
        return Integer.parseInt(context.executeScript("return nlapiFindLineItemValue('${sublistId}', '${fieldId}', '${value}');"))
    }

    /**
     * @desc Selects an existing line in a sublist.
     * @param {string} [required] sublistId - The sublist internal ID
     * @param {int} [required] line - The line number to select. Note the first line number on a sublist is 1 (not 0).
     * @return void
     */
    def selectLine(sublistId, line) {
        context.executeScript("nlapiSelectLineItem('${sublistId}', '${line}');")
    }

    /**
     * @desc Selects a new line at the end of a sublist.
     * @param {string} [required] sublistId - The internal ID of the sublist.
     * @return void
     */
    def selectNewLine(sublistId) {
        context.executeScript("nlapiSelectNewLineItem('${sublistId}');")
    }

    /**
     * @desc Sets the value for the field in the currently selected line.
     * Also supports setting values for multiple fields.
     * @param {string} [required] sublistId - The sublist internal ID
     * @param {map} [required] - The name and value of the field being set to
     * @return void
     */
    def setCurrentSublistValue(sublistId, field) {
        def toExecuteScript = ""
        field.each { fieldId, value ->
            toExecuteScript += "nlapiSetCurrentLineItemValue('${sublistId}', '${fieldId}', '${value}');"
        }
        context.executeScript(toExecuteScript)
    }

    /**
     * @desc Sets the value for the field in the currently selected line by a text representation.
     * @param {string} [required] sublistId - The sublist internal ID
     * @param {map} field - The name and text of the field being set
     * fieldId
     */
    def setCurrentSublistText(sublistId, field) {
        def toExecuteScript = ""
        field.each { fieldId, text ->
            if (needConvertTextToValue(fieldId)){
                def value = getInternalIdByText(fieldId, text)
                toExecuteScript += "nlapiSetCurrentLineItemValue('${sublistId}', '${fieldId}', '${value}');"
            } else {
                toExecuteScript += "nlapiSetCurrentLineItemText('${sublistId}', '${fieldId}', '${text}');"
            }

        }
        context.executeScript(toExecuteScript)
    }

    /**
     * @desc Sets the value of a sublist field on the specified line.
     * Also supports setting values for multiple fields in the specified line.
     * @param {string} [required] sublistId - The sublist internal ID
     * @param {map} [required] field - The name and value of the field being set
     * fieldId: The name of the field being set
     * value: The value the field is being set to
     * @param {int} [required] line - The line number for this field.
     * Note the first line number on a sublist is 1 (not 0).
     * @return void
     */
    def setSublistValue(sublistId, field, line) {
        def toExecuteScript = "nlapiSelectLineItem('${sublistId}', '${line}');"
        field.each { fieldId, value ->
            toExecuteScript += "nlapiSetCurrentLineItemValue('${sublistId}', '${fieldId}', '${value}');"
        }
        context.executeScript(toExecuteScript)
    }

    /**
     * @desc Sets the texts of a sublist field on the specified line.
     * Also supports setting texts for multiple fields in the specified line.
     * @param {string} [required] sublistId - The sublist internal ID
     * @param {map} [required] field - The name and value of the field being set
     * fieldId: The name of the field being set
     * text: The text the field is being set to
     * @param {int} [required] line - The line number for this field.
     * Note the first line number on a sublist is 1 (not 0).
     * @return void
     */
    def setSublistText(sublistId, field, line) {
        def toExecuteScript = "nlapiSelectLineItem('${sublistId}', '${line}');"
        field.each { fieldId, value ->
            if (needConvertTextToValue(fieldId)){
                def id = getInternalIdByText(fieldId, value)
                toExecuteScript += "nlapiSetCurrentLineItemValue('${sublistId}', '${fieldId}', '${id}');"
            } else {
                toExecuteScript += "nlapiSetCurrentLineItemText('${sublistId}', '${fieldId}', '${value}');"
            }
        }
        context.executeScript(toExecuteScript)
    }

    /**
     * @desc Saves/commits the changes to the current line in a sublist.
     * @param {string} [required] sublistId - The sublist internal ID
     * @return
     */
    def commitLine(sublistId) {
        context.executeScript("nlapiCommitLineItem('${sublistId}');")
    }

    /**
     * @desc fill all the main fields and sublist in the current page
     * Note: only field of select type can be set as text, other types are set by value
     * @param {map} [required] recordData - the main fields and sublist key-value pairs
     * e.g.{ "main": { fieldId1 : value1, fieldId2 : value2},
     *       "sublist_id1": [{ subFieldId1 : value1, subFieldId2 : value2}],
     *       "sublist_id2":[{ subFieldId1 : value1, subFieldId2 : value2}]
     *       }
     * @return void
     */
    def setCurrentRecord(recordData) {
        recordData.each { sublistId, subEntries ->
            if (sublistId == "main") {
                subEntries.each { k, v ->
                    if (k != "entitytype"){ //skip setting entitytype
                        if (context.getHeadFieldType(k) == "select"){
                            setText([(k): v, "entitytype": subEntries["entitytype"]])
                        } else {
                            setValue([(k): v])
                        }
                    }

                }
            } else {
                Thread.sleep(2000)
                subEntries.eachWithIndex { subEntry, line ->
                    subEntry.each { fieldId, value ->
                        def type
                        try{
                             type=context.getItemFieldType(sublistId, fieldId)
                        }
                        catch (Exception e){
                            println e.printStackTrace()
                        }
                        if (type == "select") {
                            setSublistText(sublistId, [(fieldId): value], line + 1)
                            Thread.sleep(2000)
                        } else if(type) {
                            setSublistValue(sublistId, [(fieldId): value], line + 1)
                        }
                    }
                    Thread.sleep(2000)
                    commitLine(sublistId)
                }
            }
        }
    }

    /**
     *
     * @param {String}[required] - fieldId
     * @return {boolean} true - need to convert
     *                    false - not need to convert
     */
    def needConvertTextToValue(fieldId) {
        def fieldIdList = ["subsidiary", "linesubsidiary","tosubsidiary","entity","customer"]
        return fieldIdList.contains(fieldId)
    }

    def getInternalIdByText(fieldId, text, entitytype=null) {
        if (fieldId == "subsidiary" || fieldId == "linesubsidiary" || fieldId == "tosubsidiary") {
            if (context.isOneWorld()) {
                return record.getIdByName([
                        fieldId: "namenohierarchy",
                        value  : text,
                        entity : "subsidiary"
                ])
            } else {
                return null
            }
        }else if(fieldId == "entity"||fieldId == "customer"||fieldId == "vendor"){
            if (entitytype == "customer"){
                return record.getIdByName([
                        fieldId: "entityid",
                        value  : text,
                        entity : "customer"
                ])
            }else {
                return record.getIdByName([
                        fieldId: "entityid",
                        value  : text,
                        entity : "vendor"
                ])
            }
        }
    }


}
