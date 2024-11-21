pipeline {
    agent any

    environment {
        REGISTRY = 'user19.azurecr.io'
        SERVICES = 'order,inventory,product' // List of services as a comma-separated string
        AKS_CLUSTER = 'user19-aks'
        RESOURCE_GROUP = 'user19-rsrcgrp'
        AKS_NAMESPACE = 'default'
        AZURE_CREDENTIALS_ID = 'Azure-Cred'
        TENANT_ID = '29d166ad-94ec-45cb-9f65-561c038e1c7a'
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
                                sh 'ls -R'
                                
                                sh """
                                sed 's/latest/v${env.BUILD_ID}/g' ${service}/kubernetes/deployment.yaml > output.yaml
                                cat output.yaml
                                kubectl apply -f output.yaml
                                kubectl apply -f ${service}/kubernetes/service.yaml
                                rm output.yaml
                                """
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
