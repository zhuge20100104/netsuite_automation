import net.sf.json.JSONArray;
import net.sf.json.JSONObject

account_ow = 'PSG_QA - DB9002_CN_Release_Feature Testing_OW auto'
account_si = 'PSG_QA - DB9002_CN_Release_Feature Testing_SI auto'

//branch='developer'
branch = 'jz'
slackChannel='bottest'
P0 = 'com.netsuite.common.P0'
P1 = 'com.netsuite.common.P1'
P2 = 'com.netsuite.common.P2'
P0_P1 = P0 + ',' + P1
P1_P2 = P1 + ',' + P2
AllPriority = P0 + ',' + P1 + ',' + P2

CFSMandatory = 'com.netsuite.common.CFSMandatory'
OW = 'com.netsuite.common.OW'
SI = 'com.netsuite.common.SI'
CFSMandatory_SI = CFSMandatory + ',' + SI
CFSMandatory_OW = CFSMandatory + ',' + OW


CFS = 'com.netsuite.chinalocalization.cashflow.*Test'
VL = 'com.netsuite.chinalocalization.voucher.*Test'
BLS = 'com.netsuite.chinalocalization.balancesheet.BalanceSheetSavedReportTest'
BLS_VL = BLS + ',' + VL
AllFeature = CFS + ',' + BLS + ',' + VL

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
    attach_header.put('pretext', "Groovy Automation Test Report for 'FEATURE'");

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

def job3, job4, job5, job6_1, job6_2, job7, job8_1, job8_2

pipeline {
    agent any
    stages {
        stage('Check Environment') {
            parallel {
                stage('Check OW Env') {
                    steps {
                        job1 = buildJob(job: 'NS_Automation_Groovy_Team1_China_03', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: ENV), string(name: 'GROUP', value: P0), string(name: 'EXCLUDE_GROUP', value: ''), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: '155358150@qq.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
                    }
                    post {
                        script {
                            slackSend channel: slackChannel, tokenCredentialId: "netsuite_ow", message: "Environment check for " + account_ow
                        }
                    }
                }
                stage('Check SI Env') {
                    steps {
                        job2 = buildJob(job: 'NS_Automation_Groovy_Team1_China_07', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: ENV), string(name: 'GROUP', value: P0), string(name: 'EXCLUDE_GROUP', value: ''), string(name: 'ACCOUNT', value: account_si), string(name: 'USERNAME', value: '11241527@qq.com'), string(name: 'PASSWORD', value: 'Ly19790426!'), string(name: 'LANGUAGE', value: 'en_US'), string(name: 'RERUN_TIME', value: '0')])
                    }
                    post {
                        script {
                            slackSend channel: slackChannel, tokenCredentialId: "netsuite_si", message: "Environment check for " + account_si
                        }
                    }
                }
            }
        }
        stage('Groovy Automation Test') {
            parallel {
                //Run case on OW
                stage('Run P0 Cases') {
                    steps {
                        script {
                            echo 'Run P0 Cases:'
                            job3 = buildJob(job: 'NS_Automation_Groovy_Team1_China_03', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: AllFeature), string(name: 'GROUP', value: P0), string(name: 'EXCLUDE_GROUP', value: SI), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: '155358150@qq.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            job3 = buildJob(job: 'dummy_1')
                        }
                    }
                }
                stage('Run BLS,VL P1+P2 Cases') {
                    steps {
                        script {
                            echo 'Run BLS,VL P1+P2 Cases:'
                            job4 = buildJob(job: 'NS_Automation_Groovy_Team1_China_04', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: BLS_VL), string(name: 'GROUP', value: P1_P2), string(name: 'EXCLUDE_GROUP', value: CFSMandatory_SI), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: 'toys416@hotmail.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            job4 = buildJob(job: 'dummy_2')
                        }
                    }
                }
                stage('Run CFS P1 Cases ') {
                    steps {
                        script {
                            echo 'Run CFS P1 Cases:'
                            job5 = buildJob(job: 'NS_Automation_Groovy_Team1_China_05', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: CFS), string(name: 'GROUP', value: P1), string(name: 'EXCLUDE_GROUP', value: CFSMandatory_SI), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: 'ytjtcdc@163.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            job5 = buildJob(job: 'dummy_3')
                        }
                    }
                }
                stage('Run CFS P2 Cases ') {
                    steps {
                        script {
                            echo 'Run CFS P2 Cases:'
                            job6_1 = buildJob(job: 'NS_Automation_Groovy_Team1_China_06', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: CFS), string(name: 'GROUP', value: P2), string(name: 'EXCLUDE_GROUP', value: CFSMandatory_SI), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: 'toys416@gmail.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
//                            job5 = buildJob(job: 'dummy_3')
                        }
                    }
                }
                stage('Run CFS Mandatory case') {
                    steps {
                        script {
                            echo 'Run CFS Mandatory Case:'
                            while ((job3 == null) || (job4 == null) || (job5 == null) || (job6_1 == null)) {
                                sleep 60 * 5
                                echo 'job6 is waiting for job3,4,5,6_1 ready'
                            }
                            job6_2 = buildJob(job: 'NS_Automation_Groovy_Team1_China_06', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: CFS), string(name: 'GROUP', value: CFSMandatory), string(name: 'EXCLUDE_GROUP', value: SI), string(name: 'ACCOUNT', value: account_ow), string(name: 'USERNAME', value: 'toys416@gmail.com'), string(name: 'PASSWORD', value: 'ns1A2b3c4d!!'), string(name: 'LANGUAGE', value: 'zh_CN'), string(name: 'RERUN_TIME', value: '0')])
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
                                for (int i = 0; i < 5; i++) {
                                    if (i == 0) {
                                        attach_job = compostSlackJobAttachemnt("CFS & BLS & VL", "P0", job3)
                                    } else if (i == 1) {
                                        attach_job = compostSlackJobAttachemnt("BLS & VL", "P1+P2", job4)
                                    } else if (i == 2) {
                                        attach_job = compostSlackJobAttachemnt("CFS", "P1", job5)
                                    } else if (i == 3) {
                                        attach_job = compostSlackJobAttachemnt("CFS", "P2", job6_1)
                                    } else if (i == 4) {
                                        attach_job = compostSlackJobAttachemnt("CFS Mandatory", "P0+P1+P2", job6_2)
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
                stage('Run BLS,VL P0+P1+P2 Cases on SI') {
                    steps {
                        script {
                            echo 'Run P0+P1 Cases on SI:'
                            job7 = buildJob(job: 'NS_Automation_Groovy_Team1_China_07', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: BLS_VL), string(name: 'GROUP', value: AllPriority), string(name: 'EXCLUDE_GROUP', value: OW), string(name: 'ACCOUNT', value: account_si), string(name: 'USERNAME', value: '11241527@qq.com'), string(name: 'PASSWORD', value: 'Ly19790426!'), string(name: 'LANGUAGE', value: 'en_US'), string(name: 'RERUN_TIME', value: '0')])
//                            sleep 10
//                            job7 = buildJob(job: 'dummy_2')
                        }
                    }
                }
                stage('Run CFS P0+P1+P2 Cases on SI') {
                    steps {
                        script {
                            echo 'Run CFS Regugar on SI:'
                            job8_1 = buildJob(job: 'NS_Automation_Groovy_Team1_China_08', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: CFS), string(name: 'GROUP', value: AllPriority), string(name: 'EXCLUDE_GROUP', value: CFSMandatory_OW), string(name: 'ACCOUNT', value: account_si), string(name: 'USERNAME', value: 'bsky135@hotmail.com'), string(name: 'PASSWORD', value: 'ns1A2nb3c4d!!'), string(name: 'LANGUAGE', value: 'en_US'), string(name: 'RERUN_TIME', value: '0')])
                        }
                    }
                }
                stage('Run CFS_Mandatory P0+P1+P2 Cases on SI') {
                    steps {
                        script {
                            echo 'Run CFS Mandatory cases on SI:'
                            while ((job7 == null) || (job8_1 == null)) {
                                sleep 60 * 5
                                echo 'job8_2 is waiting for job7,job8_1 ready'
                            }
                            job8_2 = buildJob(job: 'NS_Automation_Groovy_Team1_China_08', parameters: [string(name: 'BRANCH_OR_TAG', value: branch), string(name: 'TESTS_TO_RUN', value: AllFeature), string(name: 'GROUP', value: CFSMandatory), string(name: 'EXCLUDE_GROUP', value: OW), string(name: 'ACCOUNT', value: account_si), string(name: 'USERNAME', value: 'bsky135@hotmail.com'), string(name: 'PASSWORD', value: 'ns1A2nb3c4d!!'), string(name: 'LANGUAGE', value: 'en_US'), string(name: 'RERUN_TIME', value: '0')])
//                            job8 = buildJob(job: 'dummy_3')

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
                                for (int i = 0; i < 3; i++) {
                                    if (i == 0) {
                                        attach_job = compostSlackJobAttachemnt("BLS & VL", "P0+P1+P2", job7)
                                    } else if (i == 1) {
                                        attach_job = compostSlackJobAttachemnt("CFS", "P0+P1+P2", job8_1)
                                    } else if (i == 2) {
                                        attach_job = compostSlackJobAttachemnt("CFS Mandatory", "P0+P1+P2", job8_2)
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
