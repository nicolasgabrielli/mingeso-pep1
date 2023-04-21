pipeline{
    agent any
    tools {
        maven "maven"
    }
    stages {
        stage("Build JAR"){
            steps {
                checkout scmGit(branches: [[name: "*/main"]], extensions: [], userRemoteConfigs: [[credentialsId: "jenkins-token", url: "https://github.com/nicolasgabrielli/mingeso-pep1"]])
                dir("backend"){
                    sh "ls"
                    sh "mvn clean install"    
                }
            }
        }
        stage("Build Backend Image"){
            steps {
                dir("backend"){
                    sh "docker build -t nicolasgabrielli/mingeso-pep1:api ."
                }
            }
        }
        stage("Push Backend Image"){
            steps {
                dir("backend"){
                    withCredentials([string(credentialsId: "dckrhubpassword", variable: "dckpass")]) {
                        sh "docker login -u nicolasgabrielli -p $dckpass"
                    }
                    sh "docker push nicolasgabrielli/mingeso-pep1:api"
                }
            }
        }
        stage("Test Backend"){
            steps {
                dir("backend"){
                    sh "mvn test"
                }
            }
        }
        stage("SonarQube Analysis"){
            steps{
                dir("backend"){
                    sh "mvn clean verify sonar:sonar -Dsonar.projectKey=pep1 -Dsonar.host.url=http://localhost:9000 -Dsonar.login=sqp_66d1255047c7afda8cb6a56c867bdaad93cfd530"
                }
            }
        }
    }
    post {
        always {
            dir("backend"){
                sh "docker logout"
        }
    }
}