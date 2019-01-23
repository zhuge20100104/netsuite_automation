package com.netsuite.base.page.jsondriver.util.base

import com.netsuite.base.lib.NCurrentRecord
import com.netsuite.base.lib.NRecord
import com.netsuite.base.lib.NetSuiteAppBase
import com.netsuite.base.page.steps.beans.CheckerData
import com.netsuite.base.page.steps.checkers.IChecker
import com.netsuite.base.page.steps.checkers.fieldids.SubFieldChecker
import com.netsuite.base.page.steps.expect.ClassNameExpect
import com.netsuite.base.page.steps.expect.IExpect
import com.netsuite.base.page.steps.expect.IdExpect
import com.netsuite.base.page.steps.expect.NameExpect
import com.netsuite.base.page.steps.expect.XPathExpect
import com.netsuite.base.page.steps.expect.fieldids.MainFieldExpect
import com.netsuite.base.page.steps.expect.fieldids.SubFieldExpect

class JExpectUtility {


    static List<IExpect> getAllExpects() {
        List<IExpect> expects = new ArrayList<>()
        expects.add(new MainFieldExpect())
        expects.add(new SubFieldExpect())
        expects.add(new IdExpect())
        expects.add(new NameExpect())
        expects.add(new XPathExpect())
        expects.add(new ClassNameExpect())
        return expects
    }


    static void doExpect(CheckerData checkerData) {

        if(!checkerData.isChecked) {
            throw new Exception("No matched locator: ["+checkerData.key+"] is found,please checker your data!!")
        }

        List<IExpect> expects =  getAllExpects()
        for(IExpect expect in expects){
            expect.doExpect(checkerData)
            if(checkerData.isExpected) {
                break
            }
        }
    }


    static def expects(NRecord record, NCurrentRecord currentRecord, NetSuiteAppBase autBase, def data) {
        data.each {
            mainKey,mainValue ->

                if(!mainKey.startsWith("values")) {
                    CheckerData checkerData = JCheckUtility.doCheckForData(record,currentRecord,autBase,mainKey,mainValue)
                    doExpect(checkerData)
                }else{

                    String machineKey = ""
                    mainValue.each {

                        subKey,subValue ->

                            IChecker checker = null
                            CheckerData checkerData = new CheckerData()
                            checkerData.context = autBase
                            checkerData.currentRecord = currentRecord
                            checkerData.record = record

                            checkerData.isChecked = false
                            checkerData.isExpected= false

                            checkerData.isMainField = false
                            checkerData.isSubField = false

                            if(subKey.equals("id")) {
                                machineKey = subValue
                            }else if(subKey.startsWith("TAB")) {
                                checkerData.key = subKey
                                checkerData.value = subValue
                                checker = new  SubFieldChecker()
                                checker.doChecker(checkerData)
                                doExpect(checkerData)
                            } else if(subKey.equals("values")) {

                                subValue.each{
                                    singleValue ->
                                        singleValue.each {
                                            subListKey,subListValue ->
                                                checkerData.machineKey = machineKey

                                                checkerData.isChecked = false
                                                checkerData.isExpected = false

                                                checkerData.isMainField = false
                                                checkerData.isSubField = false


                                                checkerData = JCheckUtility.doCheckForSubData(checker,checkerData,subListKey,subListValue)
                                                doExpect(checkerData)
                                        }
                                }

                            }


                    }
                }



        }

    }

}
