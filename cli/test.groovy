def runJob(name) {
    def job = build job: name
    def result = job.getResult()
    return result
}

def runJob2(name) {
    def job = build(job: name)
    def number = job.getNumber()
    return number.toString()
}


pipeline {
    agent {
        label 'Team1_Linux01'
    }
    stages {
        stage('dummy_1') {
            steps {
                script {
                    def s1 = runJob2('dummy_1')
                    echo s1
                }
            }
            post('slack after dummy1') {
                always {
                    slackSend channel: "#bottest", message: "message after dummy1", color: "warning"
                }
            }
        }
        stage('parallel jobs') {
            parallel {
                stage('dummy_2') {
                    steps {
                        script {
                            def s2 = runJob2('dummy_2')
                            echo s2
                        }
                    }
                }
                stage('dummy_3') {
                    steps {
                        script {
                            def s3 = runJob2('dummy_3')
                            echo s3
                        }
                    }
                }
            }
        }
    }
    post('Send Result to Channel') {
        always {
            slackSend channel: "#bottest", message: "message after all jobs", color: "good"
        }
    }
}

