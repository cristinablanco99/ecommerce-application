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
        // Windows -> usar bat; perfil de test activado
        bat 'mvn -B -Dspring.profiles.active=test -DskipTests=false clean test'
      }
      post {
        always {
          // Usa / en los globs (Ant-style, independ. del SO)
          // Publica Surefire y, si existieran, Failsafe
          junit testResults: 'target/surefire-reports/*.xml,target/failsafe-reports/*.xml'

          // Jacoco con / en las rutas
          jacoco execPattern: 'target/jacoco.exec',
                 classPattern: 'target/classes',
                 sourcePattern: 'src/main/java'
        }
      }
    }

    stage('Package') {
      steps {
        // Empaqueta usando el mismo perfil de test
        bat 'mvn -B -Dspring.profiles.active=test -DskipTests package'
      }
    }
  }

  post {
    success { echo 'Build OK ✅' }
    failure { echo 'Build FAILED ❌' }
  }
}
