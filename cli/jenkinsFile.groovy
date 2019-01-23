import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//slave = 'Team1_Linux_JZ_01'
account = 'PSG_QA - DB9002_CN_Release_Feature Testing_OW auto'
title = 'Netsuite China Localization Automation Test Report'

url_0 = "https://hudsonci.oraclecorp.com/jenkins/oardc-jenkins/job/NS_Automation_Groovy_Team1_China_03/Netsuite_China_Localization_Automation_Report/"
url_1 = "https://hudsonci.oraclecorp.com/jenkins/oardc-jenkins/job/NS_Automation_Groovy_Team1_China_04/Netsuite_China_Localization_Automation_Report/"
url_2 = "https://hudsonci.oraclecorp.com/jenkins/oardc-jenkins/job/NS_Automation_Groovy_Team1_China_05/Netsuite_China_Localization_Automation_Report/"
url_3 = "https://hudsonci.oraclecorp.com/jenkins/oardc-jenkins/job/NS_Automation_Groovy_Team1_China_06/Netsuite_China_Localization_Automation_Report/"
thumb_url='https://upload.wikimedia.org/wikipedia/en/thumb/c/cd/NetSuite_Logo.PNG/330px-NetSuite_Logo.PNG'

pipeline {
    agent any
    stages {
        stage('Groovy Automation Test') {
            parallel {
                stage('NS_Automation_Groovy_Team1_China_03') {
                    steps {
                        sleep 2
                    }
                }
                stage('NS_Automation_Groovy_Team1_China_04') {
                    steps {
                        sleep 2
                    }
                }
            }
        }
    }
    post('Send Result to Channel') {
        always {
            script {
                JSONArray attachments = new JSONArray();

                //Add header attachment
                JSONObject attch_header = new JSONObject();
                attch_header.put('pretext', title);
                attch_header.put('color', '#439FE0');

                JSONArray fields_header = new JSONArray();
                // Account field
                JSONObject field_account = new JSONObject();
                field_account.put('title', 'Account');
                field_account.put('value', account);
                field_account.put('short', true);
                //Language field
                JSONObject field_language = new JSONObject();
                field_language.put('title', 'Language');
                field_language.put('value', 'zh_CN');
                field_language.put('short', true);


                fields_header.add(field_language);
                fields_header.add(field_account);

                attch_header.put('fields', fields_header);
                attachments.add(attch_header);

                def feature
                def priority
                def url
                //Add job attachment
                for (int i = 0; i < 4; i++) {
                    if (i == 0) {
                        feature = "CFS & BLS & VL"
                        priority = "P0"
                        url = url_0
                    } else if (i == 1) {
                        feature = "CFS & BLS & VL"
                        priority = "P1"
                        url = url_1
                    } else if (i == 2) {
                        feature = "BLS & VL"
                        priority = "P2"
                        url = url_2
                    } else if (i == 3) {
                        feature = "CFS"
                        priority = "P2"
                        url = url_3
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

                    attachment.put('title', '<' + url + '|NS_Automation_Groovy_Team1_China_0' + (i + 3) + '>');
                    attachment.put('fields', fields);

                    attachments.add(attachment);
                }

                slackSend channel: "#bottest", attachments: attachments.toString()
            }
        }
    }
}

