import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

account_ow = 'PSG_QA - DB9002_CN_Release_Main Line Testing_OW auto'
account_si = 'PSG_QA - DB9002_CN_Release_Main Line Testing_SI auto'

//branch='developer'
branch = 'jz'
slackChannel = '#bottest'
P0 = 'com.netsuite.common.P0'
P1 = 'com.netsuite.common.P1'

ENV = 'com.netsuite.chinalocalization.env.*Test'
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
    attach_header.put('pretext', "Groovy Automation Test Report for 'MAINLINE'");

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

def job1, job2, job3, job4, job6, job7, job8

pipeline {
    agent any
    stages {
        stage('Check Environment') {
            parallel {
                stage('Check OW Env') {
                    steps {
                        script {
                            job1 = buildJob(job: 'NS_Automation_Groovy_Team1_China_03', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: ENV), string(name: 'GROUP', value: P0), string(name: 'EXCLUDE_GROUP', value: ''), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: '155358150@qq.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
                        }
                    }
                    post {
                        always {
                            script {
                                def url = getReportUrl(job1.jname, job1.jnumber)
                                slackSend channel: slackChannel, tokenCredentialId: "netsuite_ow", message: "Env check for <" + url + "|" + account_ow + ">"
                            }
                        }
                    }

                }
                stage('Check SI Env') {
                    steps {
                        script {
                            job2 = buildJob(job: 'NS_Automation_Groovy_Team1_China_07', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: ENV), string(name: 'GROUP', value: P0), string(name: 'EXCLUDE_GROUP', value: ''), string(name: 'ACCOUNT', value: account_si), string(name: 'USERNAME', value: '11241527@qq.com'), string(name: 'PASSWORD', value: 'Ly19790426!'), string(name: 'LANGUAGE', value: 'en_US'), string(name: 'RERUN_TIME', value: '0')])
                        }
                    }
                    post {
                        always {
                            script {
                                def url = getReportUrl(job2.jname, job2.jnumber)
                                slackSend channel: slackChannel, tokenCredentialId: "netsuite_si", message: "Env check for <" + url + "|" + account_si + ">"
                            }
                        }
                    }
                }
            }
        }
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
                            sleep 60 * 20
                            job4 = buildJob(job: 'NS_Automation_Groovy_Team1_China_04', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: BLS + ',' + VL), string(name: 'GROUP', value: P1), string(name: 'EXCLUDE_GROUP', value: 'com.netsuite.common.SI'), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: 'toys416@hotmail.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            job4 = buildJob(job: 'dummy_2')
                        }
                    }
                }
                stage('NS_Automation_Groovy_Team1_China_06') {
                    steps {
                        script {
                            echo 'Run CFS P1 Cases:'
                            sleep 60 * 60
                            job6 = buildJob(job: 'NS_Automation_Groovy_Team1_China_06', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: CFS), string(name: 'GROUP', value: P1), string(name: 'EXCLUDE_GROUP', value: 'com.netsuite.common.SI'), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: 'toys416@gmail.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            sleep 10
//                            job6 = buildJob(job: 'dummy_3')

                        }
                    }
                    post('Send Result to Channel for OW') {
                        always {
                            //Send message for OW
                            script {
                                JSONArray attachments = new JSONArray();
                                //Add header attachment
                                JSONObject attach_header = compostSlackHeaderAttachemt(title, "OW", account_ow)
                                attachments.add(attach_header);

                                //Add job attachment
                                JSONObject attach_job = new JSONObject();
                                for (int i = 0; i < 3; i++) {
                                    if (i == 0) {
                                        attach_job = compostSlackJobAttachemnt("CFS & BLS & VL", "P0", job3)
                                    } else if (i == 1) {
                                        attach_job = compostSlackJobAttachemnt("BLS & VL", "P1", job4)
                                    } else if (i == 2) {
                                        attach_job = compostSlackJobAttachemnt("CFS", "P1", job6)
                                    }
                                    attachments.add(attach_job);
                                }

                                echo attachments.toString()
                                slackSend channel: "#bottest", tokenCredentialId: "netsuite_ow", attachments: attachments.toString()
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
//                            sleep 10
//                            job7 = buildJob(job: 'dummy_1')
                        }
                    }
                }
                stage('NS_Automation_Groovy_Team1_China_08') {
                    steps {
                        script {
                            echo 'Run P1 cases on SI:'
                            sleep 60 * 15
                            job8 = buildJob(job: 'NS_Automation_Groovy_Team1_China_08', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: All), string(name: 'GROUP', value: P1), string(name: 'EXCLUDE_GROUP', value: 'com.netsuite.common.OW'), string(name: 'ACCOUNT', value: account_si), string(name: 'USERNAME', value: 'bsky135@hotmail.com'), string(name: 'PASSWORD', value: 'ns1A2nb3c4d!!'), string(name: 'LANGUAGE', value: 'en_US'), string(name: 'RERUN_TIME', value: '0')])
//                            job8 = buildJob(job: 'dummy_2')

                        }
                    }
                    post('Send Result to Channel for SI') {
                        always {
                            //Send message for SI
                            script {
                                JSONArray attachments = new JSONArray();
                                //Add header attachment
                                JSONObject attach_header = compostSlackHeaderAttachemt(title, "SI", account_si)
                                attachments.add(attach_header);

                                //Add job attachment
                                JSONObject attach_job = new JSONObject();
                                for (int i = 0; i < 2; i++) {
                                    if (i == 0) {
                                        attach_job = compostSlackJobAttachemnt("CFS & BLS & VL", "P0", job7)
                                    } else if (i == 1) {
                                        attach_job = compostSlackJobAttachemnt("CFS & BLS & VL", "P1", job8)
                                    }
                                    attachments.add(attach_job);
                                }
                                echo attachments.toString()
                                slackSend channel: "#bottest", tokenCredentialId: "netsuite_si", attachments: attachments.toString()
                            }
                        }
                    }
                }
            }
        }
    }
}
