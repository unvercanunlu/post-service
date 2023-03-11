pipeline {
    agent any

    tools {
        maven 'M3'
    }

    stages {
        stage('Prepare') {
            steps {
                script {
                    echo 'Git will pull from main branch.'
                }

                git branch: 'main', url: 'https://github.com/unvercanunlu/post-service.git'

                script {
                    echo 'Git pulled from main branch.'
                }
            }
        }

        stage('Compile') {
            steps {
                script {
                    echo 'Maven will compile source code.'
                }

                sh 'mvn clean'
                sh 'mvn compile'

                script {
                    echo 'Maven compiled source code.'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo 'Maven will run tests.'
                }

                sh 'mvn test'

                script {
                    echo 'Maven run tests.'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Maven will build packages.'
                }

                sh 'mvn -DskipTests package'

                script {
                    echo 'Maven built packages.'
                }
            }
        }

        stage('Collect') {
            steps {
                junit '**/target/surefire-reports/TEST-*.xml'
                script {
                    echo 'Test results are collected.'
                }

                step( [ $class: 'JacocoPublisher' ] )
                script {
                    echo 'Code coverage report is prepared.'
                }

                archiveArtifacts '**/target/*.jar'
                script {
                    echo 'Jar packages are collected.'
                }
            }
        }
    }
}
