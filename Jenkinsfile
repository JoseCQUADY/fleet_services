pipeline {
    agent any

    parameters {
        string(name: 'BASE_BRANCH', defaultValue: 'origin/main', description: 'Rama base para detectar cambios')
    }

    environment {
        SERVICES_STRING = 'ms-administrator-service ms-api-gateway ms-auth-service ms-driver-service ms-invitation-service ms-route-service ms-vehicle-service'
        DOCKER_HUB_USER = 'jcq12'
        IMAGE_TAG = 'latest'
        DOCKER_HUB_CREDENTIALS = 'docker-hub-credentials-id'
    }

    stages {
        stage('Detectar servicios cambiados') {
            steps {
                script {
                    bat 'git fetch origin'

                    def diffRaw = bat(script: "git diff --name-only ${params.BASE_BRANCH}", returnStdout: true).trim()
                    def diffFiles = diffRaw.tokenize('\n') // Maneja \r\n y \n

                    echo "Archivos modificados:\n${diffFiles.join('\n')}"

                    def services = env.SERVICES_STRING.split(' ')
                    def changedServices = []

                    for (service in services) {
                        if (diffFiles.any { it.startsWith("${service}/") }) { // Uso de '/' correcto para Git
                            changedServices << service
                        }
                    }

                    if (changedServices.isEmpty()) {
                        echo "No se detectaron cambios en microservicios."
                        writeFile file: 'changed_services.txt', text: ''
                    } else {
                        echo "Servicios modificados: ${changedServices.join(', ')}"
                        writeFile file: 'changed_services.txt', text: changedServices.join(' ')
                    }
                }
            }
        }

        stage('Construir y subir imágenes Docker') {
            when {
                expression {
                    return fileExists('changed_services.txt') && readFile('changed_services.txt').trim()
                }
            }
            steps {
                script {
                    def changedServices = readFile('changed_services.txt').trim().split(/\s+/)

                    withCredentials([usernamePassword(credentialsId: env.DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"

                        changedServices.each { service ->
                            echo "Iniciando construcción y subida para ${service}..."

                            dir(service) {
                                if (fileExists("gradlew.bat")) {
                                    bat "gradlew.bat build -x test"
                                } else {
                                    echo "No se encontró gradlew en ${service}, omitiendo generación de Dockerfile."
                                }
                            }

                            def dockerfilePath = "${service}/Dockerfile"
                            def dockerContext = "${service}"
                            def imageName = "${env.DOCKER_HUB_USER}/${service}:${env.IMAGE_TAG}"

                            if (fileExists(dockerfilePath)) {
                                bat """
                                    docker buildx build --platform linux/arm64 ^
                                      -t ${imageName} ^
                                      --push ^
                                      -f "${dockerfilePath}" "${dockerContext}"
                                """
                            } else {
                                echo "No se encontró Dockerfile en ${dockerfilePath}, se omite la construcción."
                            }
                        }

                        bat "docker logout"
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'changed_services.txt', fingerprint: true
        }

        failure {
            echo 'El pipeline falló. Verifica los logs para más detalles.'
        }

        success {
            echo 'Pipeline ejecutado exitosamente.'
        }
    }
}
