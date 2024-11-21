pipeline {
    agent any
      
    environment {
        REGISTRY = 'user19.azurecr.io'
        SERVICES = 'order,delivery,product' // fix your microservices
        AKS_CLUSTER = 'user19-aks'
        RESOURCE_GROUP = 'user19-rsrcgrp'
        AKS_NAMESPACE = 'default'
        AZURE_CREDENTIALS_ID = 'Azure-Cred'
        TENANT_ID = '29d166ad-94ec-45cb-9f65-561c038e1c7a'
        GIT_USER_NAME = 'kyusooK'
        GIT_USER_EMAIL = '{Your-GitHub-Email}'
        GITHUB_CREDENTIALS_ID = 'Github-Cred'
        GITHUB_REPO = 'github.com/${GIT_USER_NAME}/12stmall-demo.git'
        GITHUB_BRANCH = 'main' // 업로드할 브랜치
    }
    stages {
        stage('Clone Repository') {
            steps {
                checkout scm
            }
        }
 
        stage('Build and Deploy Services') {
            steps {
                script {
                    def services = SERVICES.tokenize(',') // Use tokenize to split the string into a list
                    for (int i = 0; i < services.size(); i++) {
                        def service = services[i] // Define service as a def to ensure serialization
                        dir(service) {
                            stage("Maven Build - ${service}") {
                                withMaven(maven: 'Maven') {
                                    sh 'mvn package -DskipTests'
                                }
                            }

                            stage("Docker Build - ${service}") {
                                def image = docker.build("${REGISTRY}/${service}:v${env.BUILD_NUMBER}")
                            }

                            stage('Azure Login') {
                                withCredentials([usernamePassword(credentialsId: env.AZURE_CREDENTIALS_ID, usernameVariable: 'AZURE_CLIENT_ID', passwordVariable: 'AZURE_CLIENT_SECRET')]) {
                                    sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant ${TENANT_ID}'
                                }
                            }

                            stage("Push to ACR - ${service}") {
                                sh "az acr login --name ${REGISTRY.split('\\.')[0]}"
                                sh "docker push ${REGISTRY}/${service}:v${env.BUILD_NUMBER}"
                            }

                            stage("Deploy to AKS - ${service}") {
                                
                                sh "az aks get-credentials --resource-group ${RESOURCE_GROUP} --name ${AKS_CLUSTER}"

                                sh 'pwd'
                                
                                sh """
                                sed -i 's|image: \"${REGISTRY}/${service}:.*\"|image: \"${REGISTRY}/${service}:v${env.BUILD_ID}\"|' kubernetes/deployment.yaml
                                cat kubernetes/deployment.yaml
                                """
                            }
                            stage('Commit and Push to GitHub') {
                                withCredentials([usernamePassword(credentialsId: GITHUB_CREDENTIALS_ID, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                                    sh """
                                        git config --global user.email "your-email@example.com"
                                        git config --global user.name "Jenkins CI"
                                        git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@${GITHUB_REPO} repo
                                        cp kubernetes/deployment.yaml repo/${service}/kubernetes/deployment.yaml
                                        cd repo
                                        git add ${service}kubernetes/deployment.yaml
                                        git commit -m "Update deployment.yaml with build ${env.BUILD_NUMBER}"
                                        git push origin ${GITHUB_BRANCH}
                                        cd ..
                                        rm -rf repo
                                    """
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('CleanUp Images') {
            steps {
                script {
                    def services = SERVICES.tokenize(',') // Use tokenize to split the string into a list
                    for (int i = 0; i < services.size(); i++) {
                        def service = services[i] // Define service as a def to ensure serialization
                        sh "docker rmi ${REGISTRY}/${service}:v${env.BUILD_NUMBER}"
                    }
                }
            }
        }
    }
}
