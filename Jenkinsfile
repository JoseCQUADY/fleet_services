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
                bat '''
                @echo off
                setlocal enabledelayedexpansion

                git fetch origin

                > diff.txt (
                    for /f "delims=" %%i in ('git diff --name-only %BASE_BRANCH%') do (
                        echo %%i
                    )
                )

                set CHANGED_SERVICES=
                for %%S in (%SERVICES_STRING%) do (
                    findstr /B "%%S/" diff.txt >nul
                    if !errorlevel! == 0 (
                        set CHANGED_SERVICES=!CHANGED_SERVICES! %%S
                    )
                )

                if not defined CHANGED_SERVICES (
                    echo No se detectaron cambios en microservicios.
                    exit /b 0
                )

                echo Servicios modificados: !CHANGED_SERVICES!
                echo !CHANGED_SERVICES! > changed_services.txt
                endlocal
                '''
            }
        }

        stage('Construir y subir imágenes Docker') {
            steps {
                script {
                    def changedServices = readFile('changed_services.txt')
                        .trim()
                        .split(/\s+/)
                        .findAll { it } 

                    if (changedServices.isEmpty()) {
                        echo "No hay microservicios que construir."
                        return
                    }

                    withCredentials([usernamePassword(credentialsId: env.DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"

                        changedServices.each { servicio ->
                            echo "Iniciando construcción y subida para ${servicio}..."

                            // 1. Genera el Dockerfile
                            dir("${servicio}") {
                                bat "gradlew dockerfile --no-daemon"
                            }

                            // 2. Construye y sube la imagen con buildx (linux/amd64)
                            def dockerfilePath = "${servicio}/build/docker/Dockerfile"
                            def contextPath = "${servicio}/build/docker"

                            bat """
                                docker buildx build --platform linux/amd64 `
                                  -t ${env.DOCKER_HUB_USER}/${servicio}:${env.IMAGE_TAG} `
                                  --push `
                                  -f "${dockerfilePath}" "${contextPath}"
                            """
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
    }
}
