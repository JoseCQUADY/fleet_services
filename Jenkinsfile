pipeline {
    agent any

    parameters {
        string(name: 'BASE_BRANCH', defaultValue: 'origin/main', description: 'Rama base para detectar cambios')
    }

    environment {
        SERVICES_STRING = 'ms-administrator-service ms-api-gateway ms-auth-service ms-driver-service ms-invitation-service ms-route-service ms-vehicle-service'
        DOCKER_HUB_USER = 'jcq12'
        IMAGE_TAG = 'latest' // Este será el tag que Gradle usará
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
                    REM Crear un archivo vacío para que la siguiente etapa lo maneje correctamente
                    echo. > changed_services.txt 
                    exit /b 0
                )

                echo Servicios modificados: !CHANGED_SERVICES!
                REM Eliminar espacios al inicio/final antes de escribir al archivo
                set CHANGED_SERVICES=!CHANGED_SERVICES:~1!
                echo !CHANGED_SERVICES! > changed_services.txt

                endlocal
                '''
            }
        }

        stage('Construir y subir imágenes Docker') {
            steps {
                script {
                    def changedServicesFile = 'changed_services.txt'
                    if (!fileExists(changedServicesFile)) {
                        echo "Archivo changed_services.txt no encontrado. No hay servicios para procesar."
                        currentBuild.result = 'SUCCESS' 
                        return
                    }

                    def changedServicesContent = readFile(changedServicesFile).trim()
                    if (changedServicesContent.isEmpty()) {
                        echo "No se detectaron cambios en microservicios (archivo vacío). Saliendo..."
                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    def changedServices = changedServicesContent.split(/\s+/).findAll { it }

                    if (changedServices.isEmpty()) {
                        echo "No hay servicios válidos en la lista de cambiados después del filtrado. Saliendo..."
                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    withCredentials([usernamePassword(credentialsId: env.DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"

                        for (service in changedServices) {
                            def lowerService = service.toLowerCase()
                            echo "Iniciando construcción y subida para ${service}..."

                            // ASUNCIÓN CLAVE: Tu build.gradle para cada servicio está configurado para que
                            // la tarea 'dockerBuild' (o bootBuildImage, jib, etc.) cree una imagen llamada
                            // ${lowerService}:${env.IMAGE_TAG} localmente.
                            // Por ejemplo, para Spring Boot:
                            // bootBuildImage { imageName = "${project.name.toLowerCase()}:${System.getenv('IMAGE_TAG_FROM_JENKINS') ?: 'latest'}" }
                            // Y luego pasar IMAGE_TAG_FROM_JENKINS a Gradle.
                            // O si IMAGE_TAG es siempre 'latest', hardcodearlo en Gradle.
                            
                            // Ejemplo pasando IMAGE_TAG a Gradle (ajusta según tu plugin):
                            // def gradleCmd = ".\\gradlew -PimageTagForGradle=${env.IMAGE_TAG} :${service}:dockerBuild --no-daemon"
                            // Si tu Gradle ya usa env.IMAGE_TAG o está hardcodeado a 'latest':
                            def gradleCmd = ".\\gradlew :${service}:dockerBuild --no-daemon"


                            bat """
                            @echo off
                            echo Construyendo imagen Docker para ${service} como ${lowerService}:${env.IMAGE_TAG}...
                            ${gradleCmd}

                            echo Verificando imagen construida...
                            docker images ${lowerService}

                            echo Etiquetando imagen ${lowerService}:${env.IMAGE_TAG} para Docker Hub como ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG}...
                            docker tag ${lowerService}:${env.IMAGE_TAG} ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG}
                            
                            echo Subiendo imagen ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG}...
                            docker push ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG}
                            """
                        }
                        bat "docker logout"
                    }
                }
            }
        }
    }
}
