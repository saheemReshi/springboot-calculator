
pipeline {
    agent any

    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        IMAGE_REPO = 'saheemreshi/spring-boot-calculator'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B -ntp clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B -ntp test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t "$IMAGE_REPO:$BUILD_NUMBER" .'
            }
        }

        stage('Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x
                        export DOCKER_CONFIG="$(mktemp -d)"
                        trap 'rm -rf "$DOCKER_CONFIG"' EXIT

                        printf '%s' "$DOCKER_TOKEN" |
                            docker login -u "$DOCKER_USER" --password-stdin

                        docker push "$IMAGE_REPO:$BUILD_NUMBER"
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    export CALCULATOR_IMAGE="$IMAGE_REPO:$BUILD_NUMBER"

                    docker compose -p spe-calculator up \
                        -d --no-build --wait --wait-timeout 120

                    curl -fsS http://127.0.0.1:8081/actuator/health
                '''
            }
        }
    }

    post {
        success {
            mail to: 'saheem.reshi.cs@gmail.com',
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build successful: ${env.BUILD_URL}"
        }

        failure {
            mail to: 'saheem.reshi.cs@gmail.com',
                 subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Check build logs: ${env.BUILD_URL}"
        }
    }
}
