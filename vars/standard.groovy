def call(String goToolName = 'go-1.12', String golangCiVersion = 'v1.59.1') {
    pipeline {
        agent any
        tools {
            go "$goToolName"
        }
        environment {
            GO111MODULE = 'on'
            GOPATH = '/usr/local/go'
            TAG_VERSION = "v1.0.${env.BUILD_NUMBER}"
        }
        stages {
            stage('Compile') {
                steps {
                    sh 'go build'
                }
            }
            stage('Test') {
                steps {
                    sh 'go test ./... -coverprofile=coverage.txt'
                    //sh "curl -s https://codecov.io/bash | bash -s -"
                }
            }
            stage('Code Analysis') {
                steps {
                    sh 'curl -sSfL https://raw.githubusercontent.com/golangci/golangci-lint/master/install.sh | bash -s -- -b $(go env GOPATH)/bin $golangCiVersion'
                    sh 'golangci-lint run'
                    //sh 'echo "BUILD_NUMBER: $BUILD_NUMBER"'
                    sh 'git tag $TAG_VERSION'
                     withCredentials([string(credentialsId: 'GITHUB_TOKEN', variable: 'test')]) {
                        sh '''
                                # modify some files
                                #git add .
                                #git commit -m "register work"
                                #git push
                                echo "Hello world!"
                        '''
        }
                    sh 'echo "git describe --tags"'
                    sh 'git describe --tags'
                }
            }
            stage('Release') {
                when {
                    buildingTag()
                }
                environment {
                    GITHUB_TOKEN = credentials('GITHUB_TOKEN')
                }
                steps {
                    sh 'curl -sL https://git.io/goreleaser | bash'
                }
            }
        }
    }
}