pipeline {
    agent any

    stages {
        stage ('Compile Stage') {
            steps {
                withMaven(maven: 'maven_3_3_0') {
                    sh 'mvn clean compile'
                }
            }
         }
    }
}