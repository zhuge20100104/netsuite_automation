import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

account_ow = 'PSG_QA - DB9002_CN_Release_Main Line Testing_OW auto'
account_si = 'PSG_QA - DB9002_CN_Release_Main Line Testing_SI auto'

//branch='developer'
branch = 'jz'
P0 = 'com.netsuite.common.P0'
P1 = 'com.netsuite.common.P1'

CFS = 'com.netsuite.chinalocalization.cashflow.*Test'
VL = 'com.netsuite.chinalocalization.voucher.*Test'
BLS = 'com.netsuite.chinalocalization.balancesheet.BalanceSheetSavedReportTest'
All = CFS + ',' + BLS + ',' + VL

title = 'Netsuite China Localization Automation Test Report'

String getReportUrl(jobName, buildNum) {
    return 'http://hudsonci.oraclecorp.com/jenkins/oardc-jenkins/job/' + jobName + '/' + buildNum + '/Netsuite_China_Localization_Automation_Report/'
}

String getAttachmentColor(result) {
    def color
    if (result == "SUCCESS") {
        color = "good"
    } else if (result == "FAILURE") {
        color = "danger"
    } else if (result == "ABORTED") {
        color = "warning"
    } else {
        color = "warning"
    }
    return color
}

String buildJob(jobAndParam) {
    def ret = build(jobAndParam)
    String jname = ret.getProjectName()
    String jnumber = ret.getNumber().toString()
    String jresult = ret.getResult()
    JJob job = new JJob(jname, jnumber, jresult)
    return job
}

JSONObject compostSlackHeaderAttachemt(String title, String accountType, String account) {
    JSONObject attach_header = new JSONObject();
    attach_header.put('title', title + ' ----------> ' + accountType);
    attach_header.put('color', '#439FE0');  //blue

    JSONArray fields = new JSONArray();
    // Account field
    JSONObject fld = new JSONObject();
    fld.put('title', 'Account');
    fld.put('value', account);
    fld.put('short', false);
    fields.add(fld);
    attach_header.put('fields', fields);
    return attach_header
}

JSONObject compostSlackJobAttachemnt(String feature, String priority, JJob job) {
    JSONObject attachment = new JSONObject();

    //Compost feature field
    JSONArray fields = new JSONArray();
    JSONObject field_fea = new JSONObject();
    field_fea.put('title', 'Feature');
    field_fea.put('value', feature);
    field_fea.put('short', true);
    fields.add(field_fea);
    //Compost priority field
    JSONObject field_pri = new JSONObject();
    field_pri.put('title', 'Priority');
    field_pri.put('value', priority);
    field_pri.put('short', true);
    fields.add(field_pri);

    def url = getReportUrl(job.jname, job.jnumber)
    def projname = job.jname
    def result = job.jresult

    attachment.put('title', '<' + url + '|' + projname + '>');
    attachment.put('fields', fields);
    attachment.put('color', getAttachmentColor(result));
    return attachment
}



class JJob {
    String jname
    String jnumber
    String jresult

    JJob(String name, String number, String result) {
        this.jname = name
        this.jnumber = number
        this.jresult = result
    }
}

def job3, job4, job6, job7, job8

pipeline {
    agent any
    stages {
        stage('Groovy Automation Test') {
            parallel {
                //Run case on OW
                stage('NS_Automation_Groovy_Team1_China_03') {
                    steps {
                        script {
                            echo 'Run P0 Cases:'
                            job3 = buildJob(job: 'NS_Automation_Groovy_Team1_China_03', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: All), string(name: 'GROUP', value: P0), string(name: 'EXCLUDE_GROUP', value: 'com.netsuite.common.SI'), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: '155358150@qq.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            job3 = buildJob(job: 'dummy_1')
                        }
                    }
                }
                stage('NS_Automation_Groovy_Team1_China_04') {
                    steps {
                        script {
                            echo 'Run BLS&VL P1 Cases:'
                            job4 = buildJob(job: 'NS_Automation_Groovy_Team1_China_04', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: BLS + ',' + VL), string(name: 'GROUP', value: P1), string(name: 'EXCLUDE_GROUP', value: 'com.netsuite.common.SI'), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: 'toys416@hotmail.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            job4 = buildJob(job: 'dummy_1')
                        }
                    }
                }
                stage('NS_Automation_Groovy_Team1_China_06') {
                    steps {
                        script {
                            echo 'Run CFS P1 Cases:'
                            job6 = buildJob(job: 'NS_Automation_Groovy_Team1_China_06', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: CFS), string(name: 'GROUP', value: P1), string(name: 'EXCLUDE_GROUP', value: 'com.netsuite.common.SI'), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: 'toys416@gmail.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            job6 = buildJob(job: 'dummy_2')
                            sleep 30
                        }
                    }
                    post('Send Result to Channel for OW') {
                        always {
                            //Send message for OW
                            script {
                                JSONArray attachments = new JSONArray();

                                //Add header attachment
                                JSONObject attach_header = new JSONObject();
                                attach_header.put('title', title + ' ----------> OW');
                                attach_header.put('color', '#439FE0');

                                JSONArray fields_header = new JSONArray();
                                // Account field
                                JSONObject field_account = new JSONObject();
                                field_account.put('title', 'Account');
                                field_account.put('value', account_ow);
                                field_account.put('short', false);

                                fields_header.add(field_account);

                                attach_header.put('fields', fields_header);
                                attachments.add(attach_header);

                                def feature
                                def priority
                                def url
                                def field_title
                                def result
                                //Add job attachment
                                for (int i = 0; i < 3; i++) {
                                    if (i == 0) {
                                        feature = "CFS & BLS & VL"
                                        priority = "P0"
                                        url = getReportUrl(job3.jname, job3.jnumber)
                                        field_title = 'NS_Automation_Groovy_Team1_China_03'
                                        result = job3.jresult
                                    } else if (i == 1) {
                                        feature = "BLS & VL"
                                        priority = "P1"
                                        url = getReportUrl(job4.jname, job4.jnumber)
                                        field_title = 'NS_Automation_Groovy_Team1_China_04'
                                        result = job4.jresult
                                    } else if (i == 2) {
                                        feature = "CFS"
                                        priority = "P1"
                                        url = getReportUrl(job6.jname, job6.jnumber)
                                        field_title = 'NS_Automation_Groovy_Team1_China_06'
                                        result = job6.jresult
                                    }
                                    JSONObject attachment = new JSONObject();
                                    JSONArray fields = new JSONArray();
                                    JSONObject field_fea = new JSONObject();
                                    field_fea.put('title', 'Feature');
                                    field_fea.put('value', feature);
                                    field_fea.put('short', true);
                                    fields.add(field_fea);

                                    JSONObject field_pri = new JSONObject();
                                    field_pri.put('title', 'Priority');
                                    field_pri.put('value', priority);
                                    field_pri.put('short', true);
                                    fields.add(field_pri);

                                    attachment.put('title', '<' + url + '|' + field_title + '>');
                                    attachment.put('fields', fields);
                                    attachment.put('color', getAttachmentColor(result));

                                    attachments.add(attachment);
                                }

                                echo attachments.toString()
                                slackSend channel: "#bottest", attachments: attachments.toString()
                            }
                        }
                    }
                }
                //Run case on SI
                stage('NS_Automation_Groovy_Team1_China_07') {
                    steps {
                        script {
                            echo 'Run P0 Cases on SI:'
                            job7 = buildJob(job: 'NS_Automation_Groovy_Team1_China_07', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: All), string(name: 'GROUP', value: P0), string(name: 'EXCLUDE_GROUP', value: 'com.netsuite.common.OW'), string(name: 'ACCOUNT', value: account_si), string(name: 'USERNAME', value: '11241527@qq.com'), string(name: 'PASSWORD', value: 'Ly19790426!'), string(name: 'LANGUAGE', value: 'en_US'), string(name: 'RERUN_TIME', value: '0')])
//                            job7 = buildJob(job: 'dummy_1')
                        }
                    }
                }
                stage('NS_Automation_Groovy_Team1_China_08') {
                    steps {
                        script {
                            echo 'Run P1 cases on SI:'
                            job8 = buildJob(job: 'NS_Automation_Groovy_Team1_China_08', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: All), string(name: 'GROUP', value: P1), string(name: 'EXCLUDE_GROUP', value: 'com.netsuite.common.OW'), string(name: 'ACCOUNT', value: account_si), string(name: 'USERNAME', value: 'bsky135@hotmail.com'), string(name: 'PASSWORD', value: 'ns1A2nb3c4d!!'), string(name: 'LANGUAGE', value: 'en_US'), string(name: 'RERUN_TIME', value: '0')])
//                            job8 = buildJob(job: 'dummy_3')
                            sleep 10
                        }
                    }
                    post('Send Result to Channel for SI') {
                        always {
                            //Send message for SI
                            script {
                                JSONArray attachments = new JSONArray();

                                //Add header attachment
                                JSONObject attch_header = new JSONObject();
                                attch_header.put('title', title + ' ----------> SI');
                                attch_header.put('color', '#439FE0');

                                JSONArray fields_header = new JSONArray();
                                // Account field
                                JSONObject field_account = new JSONObject();
                                field_account.put('title', 'Account');
                                field_account.put('value', account_si);
                                field_account.put('short', false);

                                fields_header.add(field_account);

                                attch_header.put('fields', fields_header);
                                attachments.add(attch_header);

                                def feature
                                def priority
                                def url
                                def field_title
                                def result
                                //Add job attachment
                                for (int i = 0; i < 2; i++) {
                                    if (i == 0) {
                                        feature = "CFS & BLS & VL"
                                        priority = "P0"
                                        url = getReportUrl(job7.jname, job7.jnumber)
                                        field_title = 'NS_Automation_Groovy_Team1_China_07'
                                        result = job7.jresult
                                    } else if (i == 1) {
                                        feature = "CFS & BLS & VL"
                                        priority = "P1"
                                        url = getReportUrl(job8.jname, job8.jnumber)
                                        field_title = 'NS_Automation_Groovy_Team1_China_08'
                                        result = job8.jresult
                                    }
                                    JSONObject attachment = new JSONObject();
                                    JSONArray fields = new JSONArray();
                                    JSONObject field_fea = new JSONObject();
                                    field_fea.put('title', 'Feature');
                                    field_fea.put('value', feature);
                                    field_fea.put('short', true);
                                    fields.add(field_fea);

                                    JSONObject field_pri = new JSONObject();
                                    field_pri.put('title', 'Priority');
                                    field_pri.put('value', priority);
                                    field_pri.put('short', true);
                                    fields.add(field_pri);

                                    attachment.put('title', '<' + url + '|' + field_title + '>');
                                    attachment.put('fields', fields);
                                    attachment.put('color', getAttachmentColor(result));

                                    attachments.add(attachment);
                                }
                                echo attachments.toString()
                                slackSend channel: "#bottest", attachments: attachments.toString()
                            }
                        }
                    }
                }
            }
        }
    }
//    post('Send Result to Channel') {
//        always {
//            //Send message for OW
//            script {
//                JSONArray attachments = new JSONArray();
//
//                //Add header attachment
//                JSONObject attch_header = new JSONObject();
//                attch_header.put('title', title + ' ----------> OW');
//                attch_header.put('color', '#439FE0');
//
//                JSONArray fields_header = new JSONArray();
//                // Account field
//                JSONObject field_account = new JSONObject();
//                field_account.put('title', 'Account');
//                field_account.put('value', account_ow);
//                field_account.put('short', false);
//
//                fields_header.add(field_account);
//
//                attch_header.put('fields', fields_header);
//                attachments.add(attch_header);
//
//                def feature
//                def priority
//                def url
//                def field_title
//                def result
//                //Add job attachment
//                for (int i = 0; i < 3; i++) {
//                    if (i == 0) {
//                        feature = "CFS & BLS & VL"
//                        priority = "P0"
//                        url = getReportUrl(job3.name, job3.number)
//                        field_title = 'NS_Automation_Groovy_Team1_China_03'
//                        result = job3.result
//                    } else if (i == 1) {
//                        feature = "BLS & VL"
//                        priority = "P1"
//                        url = getReportUrl(job4.name, job4.number)
//                        field_title = 'NS_Automation_Groovy_Team1_China_04'
//                        result = job4.result
//                    } else if (i == 2) {
//                        feature = "CFS"
//                        priority = "P1"
//                        url = getReportUrl(job6.name, job6.number)
//                        field_title = 'NS_Automation_Groovy_Team1_China_06'
//                        result = job6.result
//                    }
//                    JSONObject attachment = new JSONObject();
//                    JSONArray fields = new JSONArray();
//                    JSONObject field_fea = new JSONObject();
//                    field_fea.put('title', 'Feature');
//                    field_fea.put('value', feature);
//                    field_fea.put('short', true);
//                    fields.add(field_fea);
//
//                    JSONObject field_pri = new JSONObject();
//                    field_pri.put('title', 'Priority');
//                    field_pri.put('value', priority);
//                    field_pri.put('short', true);
//                    fields.add(field_pri);
//
//                    attachment.put('title', '<' + url + '|' + field_title + '>');
//                    attachment.put('fields', fields);
//                    attachment.put('color', getAttachmentColor(result));
//
//                    attachments.add(attachment);
//                }
//
//                echo attachments.toString()
//                slackSend channel: "#bottest", attachments: attachments.toString()
//            }
//            //Send message for SI
//            script {
//                JSONArray attachments = new JSONArray();
//
//                //Add header attachment
//                JSONObject attch_header = new JSONObject();
//                attch_header.put('title', title + ' ----------> SI');
//                attch_header.put('color', '#439FE0');
//
//                JSONArray fields_header = new JSONArray();
//                // Account field
//                JSONObject field_account = new JSONObject();
//                field_account.put('title', 'Account');
//                field_account.put('value', account_si);
//                field_account.put('short', false);
//
//                fields_header.add(field_account);
//
//                attch_header.put('fields', fields_header);
//                attachments.add(attch_header);
//
//                def feature
//                def priority
//                def url
//                def field_title
//                def result
//                //Add job attachment
//                for (int i = 0; i < 2; i++) {
//                    if (i == 0) {
//                        feature = "CFS & BLS & VL"
//                        priority = "P0"
//                        url = getReportUrl(job7.name, job7.number)
//                        field_title = 'NS_Automation_Groovy_Team1_China_07'
//                        result = job7.result
//                    } else if (i == 1) {
//                        feature = "CFS & BLS & VL"
//                        priority = "P1"
//                        url = getReportUrl(job8.name, job8.number)
//                        field_title = 'NS_Automation_Groovy_Team1_China_08'
//                        result = job8.result
//                    }
//                    JSONObject attachment = new JSONObject();
//                    JSONArray fields = new JSONArray();
//                    JSONObject field_fea = new JSONObject();
//                    field_fea.put('title', 'Feature');
//                    field_fea.put('value', feature);
//                    field_fea.put('short', true);
//                    fields.add(field_fea);
//
//                    JSONObject field_pri = new JSONObject();
//                    field_pri.put('title', 'Priority');
//                    field_pri.put('value', priority);
//                    field_pri.put('short', true);
//                    fields.add(field_pri);
//
//                    attachment.put('title', '<' + url + '|' + field_title + '>');
//                    attachment.put('fields', fields);
//                    attachment.put('color', getAttachmentColor(result));
//
//                    attachments.add(attachment);
//                }
//                echo attachments.toString()
//                slackSend channel: "#bottest", attachments: attachments.toString()
//            }
//        }
//    }
}
