def call(String... args) {
  pipeline {
    agent any //all agents
    tools {
        //go 'go-1.22.4' //Installed latest version 
        go 'go-1.12' //Installed latest version 
    }
    parameters {
        string (name: 'GO_TOOL_NAME', defaultValue: 'go', description: 'Go lang tool name')
        string (name: 'GOLANG_CI_VERSION', defaultValue: 'v1.18.0', description: 'Go lang ci version')
    }
    environment {
        GO111MODULE = 'on' //Env variable required to be set to on
    }
    stages {
        stage('Build') {
            steps {
                sh '$GO_TOOL_NAME build' //Running go command defined previously
            }
        }
        stage('Hello') {
            steps {
                sh 'echo "Hello with version $GOLANG_CI_VERSION'
            }
        }
    }
  }
}