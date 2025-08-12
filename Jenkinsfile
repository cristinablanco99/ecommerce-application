pipeline {
  agent any
  tools { jdk 'jdk17'; maven 'maven3' }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test') {
      steps {
        bat 'mvn -B -Dspring.profiles.active=test -DskipTests=false clean test jacoco:report'
      }
      post {
        always {
          junit testResults: 'target/surefire-reports/*.xml'

         recordCoverage tools: [[
            $class: 'JacocoReportAdapter',
            path: 'target/site/jacoco/jacoco.xml'
          ]]
        }
      }
    }

    stage('Package') {
      steps {
        bat 'mvn -B -Dspring.profiles.active=test -DskipTests package'
      }
    }
  }

  post {
    success { echo 'Build OK ✅' }
    failure { echo 'Build FAILED ❌' }
  }
}
