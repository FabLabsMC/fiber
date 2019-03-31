pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh "rm -rf build/libs/"
                sh "chmod +x gradlew"
                sh "./gradlew clean build --refresh-dependencies --full-stacktrace"
            }
        }

        stage('Check') {
            steps {
                sh "./gradlew check"
            }
        }

        stage('Archive artifacts') {
            when {
                branch 'master'
            }
            steps {
                sh "./gradlew publish"
            }
        }

        stage("counter") {
            steps {
                sh "./gradlew buildnumberIncrement"
            }
        }
    }
}