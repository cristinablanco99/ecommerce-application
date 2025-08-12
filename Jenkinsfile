pipeline {
  agent any
  tools { jdk 'jdk17'; maven 'maven3' }

  stages {
    stage('Build') {
      steps {
        bat 'mvn -v'
        bat 'mvn -B -DskipTests clean package'
      }
    }
  }

  post {
    success { echo 'Build OK ✅' }
    failure { echo 'Build FAILED ❌' }
  }
}
