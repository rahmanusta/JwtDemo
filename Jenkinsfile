pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh '/opt/apache-maven-3.3.9/bin/mvn clean install'
      }
    }
  }
}