pipeline {
    agent any
  
    environment {
        REGISTRY = 'user19.azurecr.io'
        SERVICES = ['order', 'delivery', 'product']
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

    stage('Maven Build') {
        steps {
            script {
                SERVICES.each { service ->
                    dir(service) {
                        withMaven(maven: 'Maven') {
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
        }
    }

    stage('Docker Build & Push') {
        steps {
            script {
                SERVICES.each { service ->
                    dir(service) {
                        def image = docker.build("${REGISTRY}/${service}:v${env.BUILD_NUMBER}")
                        sh "az acr login --name ${REGISTRY.split('\\.')[0]}"
                        image.push()
                        sh "docker rmi ${REGISTRY}/${service}:v${env.BUILD_NUMBER}"
                    }
                }
            }
        }
    }

    stage('Azure Login') {
        steps {
            script {
                withCredentials([usernamePassword(credentialsId: env.AZURE_CREDENTIALS_ID, usernameVariable: 'AZURE_CLIENT_ID', passwordVariable: 'AZURE_CLIENT_SECRET')]) {
                    sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant ${TENANT_ID}'
                }
            }
        }
    }

    stage('Commit and Push to GitHub') {
        steps {
            script {
                withCredentials([usernamePassword(credentialsId: GITHUB_CREDENTIALS_ID, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                    sh """
                    git config --global user.email "your-email@example.com"
                    git config --global user.name "Jenkins CI"
                    git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@${GITHUB_REPO} repo
                    cp kubernetes/deploy.yaml repo/kubernetes/deploy.yaml
                    cd repo
                    git add kubernetes/deploy.yaml
                    git commit -m "Update deploy.yaml with build ${env.BUILD_NUMBER}"
                    git push origin ${GITHUB_BRANCH}
                    cd ..
                    rm -rf repo
                    """
                }
            }
        }
    } 

    stage('Deploy to AKS') {
        steps {
            script {
                sh "az aks get-credentials --resource-group ${RESOURCE_GROUP} --name ${AKS_CLUSTER}"
                SERVICES.each { service ->
                    sh """
                    sed 's/latest/v${env.BUILD_NUMBER}/g' ${service}/kubernetes/deployment.yaml > ${service}/kubernetes/deployment.yaml
                    kubectl apply -f ${service}/kubernetes/deployment.yaml
                    kubectl apply -f ${service}/kubernetes/service.yaml
                    rm ${service}/kubernetes/deployment.yaml
                    """
                }
            }
        }
    }
}
