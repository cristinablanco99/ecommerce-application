pipeline {
  agent any
  tools { jdk 'jdk17' maven 'maven3' }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Build & Test') {
      steps {
        sh 'mvn -B -DskipTests=false clean verify'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
          jacoco execPattern: '**/target/jacoco.exec', classPattern: '**/target/classes', sourcePattern: '**/src/main/java'
        }
      }
    }
    stage('Package') {
      steps { sh 'mvn -B -DskipTests package' }
    }
  }
  post {
    success { echo 'Build OK ✅' }
    failure { echo 'Build FAILED ❌' }
  }
}
