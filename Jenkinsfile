pipeline {
    agent any

    parameters {
        string(name: 'BASE_BRANCH', defaultValue: 'origin/main', description: 'Rama base para detectar cambios')
    }

    environment {
        SERVICES_STRING = 'ms-administrator-service ms-api-gateway ms-auth-service ms-driver-service ms-invitation-service ms-route-service ms-vehicle-service'
        DOCKER_HUB_USER = 'jcq12'
        IMAGE_TAG = 'latest' // El tag que se usará para Docker Hub y el que se espera de Gradle localmente.
        DOCKER_HUB_CREDENTIALS = 'docker-hub-credentials-id'
    }

    stages {
        stage('Detectar servicios cambiados') {
            steps {
                bat '''
                @echo off
                setlocal enabledelayedexpansion

                echo [INFO] Iniciando deteccion de servicios cambiados...
                git fetch origin

                echo [INFO] Generando diff con la rama base: %BASE_BRANCH%
                > diff.txt (
                    for /f "delims=" %%i in ('git diff --name-only %BASE_BRANCH%') do (
                        echo %%i
                    )
                )

                echo [DEBUG] Contenido de diff.txt:
                type diff.txt
                echo.

                set CHANGED_SERVICES_TEMP=
                for %%S in (%SERVICES_STRING%) do (
                    echo [DEBUG] Verificando servicio: %%S
                    findstr /B "%%S/" diff.txt >nul
                    if !errorlevel! == 0 (
                        echo [DEBUG] Servicio %%S detectado como cambiado.
                        set CHANGED_SERVICES_TEMP=!CHANGED_SERVICES_TEMP! %%S
                    ) else (
                        echo [DEBUG] Servicio %%S no detectado como cambiado.
                    )
                )

                if not defined CHANGED_SERVICES_TEMP (
                    echo [INFO] No se detectaron cambios en microservicios.
                    REM Crear un archivo vacío para que la siguiente etapa lo maneje correctamente
                    echo. > changed_services.txt 
                    exit /b 0
                )

                REM Eliminar espacio inicial si existe
                set "FINAL_CHANGED_SERVICES=!CHANGED_SERVICES_TEMP:~1!"
                echo [INFO] Servicios modificados: !FINAL_CHANGED_SERVICES!
                echo !FINAL_CHANGED_SERVICES! > changed_services.txt

                echo [DEBUG] Contenido de changed_services.txt:
                type changed_services.txt
                echo.

                endlocal
                '''
            }
        }

        stage('Construir y subir imágenes Docker') {
            steps {
                script {
                    def changedServicesFile = 'changed_services.txt'
                    if (!fileExists(changedServicesFile)) {
                        echo "[ERROR] Archivo changed_services.txt no encontrado. No hay servicios para procesar."
                        currentBuild.result = 'FAILURE' // O 'UNSTABLE' 
                        return
                    }

                    def changedServicesContent = readFile(changedServicesFile).trim()
                    if (changedServicesContent.isEmpty()) {
                        echo "[INFO] No se detectaron cambios en microservicios (archivo changed_services.txt está vacío). Saliendo de esta etapa."
                        currentBuild.result = 'SUCCESS' // Opcional, o dejar que continúe si no hay error
                        return
                    }

                    // Split y filtra cadenas vacías que podrían surgir si hay múltiples espacios
                    def changedServices = changedServicesContent.split(/\s+/).findAll { it.trim() }

                    if (changedServices.isEmpty()) {
                        echo "[INFO] No hay servicios válidos en la lista de cambiados después del filtrado. Saliendo de esta etapa."
                        currentBuild.result = 'SUCCESS' // Opcional
                        return
                    }

                    echo "[INFO] Servicios a procesar: ${changedServices}"

                    withCredentials([usernamePassword(credentialsId: env.DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        // Login a Docker Hub
                        bat """
                        @echo off
                        echo [INFO] Iniciando login a Docker Hub como %DOCKER_USER%...
                        docker login -u %DOCKER_USER% -p %DOCKER_PASS%
                        echo [DEBUG] Resultado del comando docker login: %errorlevel%
                        if %errorlevel% neq 0 (
                            echo [ERROR] Fallo en docker login. Abortando.
                            exit /b %errorlevel%
                        )
                        """

                        for (service in changedServices) {
                            if (service.trim().isEmpty()) { // Doble verificación por si acaso
                                echo "[WARN] Servicio vacío encontrado en la lista, omitiendo."
                                continue
                            }
                            def lowerService = service.toLowerCase()
                            echo "[INFO] Iniciando construcción y subida para el servicio: ${service} (lower: ${lowerService})"

                            bat """
                            @echo off
                            echo.
                            echo [STAGE] PROCESANDO SERVICIO: ${service}
                            echo [DEBUG] Nombre del servicio en minúsculas: ${lowerService}
                            echo [DEBUG] Usuario Docker Hub: ${env.DOCKER_HUB_USER}
                            echo [DEBUG] Tag de la imagen: ${env.IMAGE_TAG}
                            echo.

                            echo [CMD] Construyendo imagen Docker para ${service} con Gradle...
                            SET GRADLE_CMD=.\\gradlew :${service}:dockerBuild --no-daemon
                            echo [DEBUG] Ejecutando: %GRADLE_CMD%
                            %GRADLE_CMD%
                            SET GRADLE_ERRORLEVEL=%errorlevel%
                            echo [DEBUG] Resultado del comando gradlew (%%errorlevel%%): %GRADLE_ERRORLEVEL%
                            if %GRADLE_ERRORLEVEL% neq 0 (
                                echo [ERROR] Fallo en la construccion de Gradle para ${service}. Código de error: %GRADLE_ERRORLEVEL%
                                exit /b %GRADLE_ERRORLEVEL%
                            )
                            echo.

                            echo [CMD] Verificando imágenes Docker locales para ${lowerService}:${env.IMAGE_TAG} despues de Gradle...
                            docker images ${lowerService}:${env.IMAGE_TAG}
                            echo.

                            echo [CMD] Etiquetando imagen ${lowerService}:${env.IMAGE_TAG} como ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG}...
                            SET DOCKER_TAG_CMD=docker tag ${lowerService}:${env.IMAGE_TAG} ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG}
                            echo [DEBUG] Ejecutando: %DOCKER_TAG_CMD%
                            %DOCKER_TAG_CMD%
                            SET TAG_ERRORLEVEL=%errorlevel%
                            echo [DEBUG] Resultado del comando docker tag (%%errorlevel%%): %TAG_ERRORLEVEL%
                            if %TAG_ERRORLEVEL% neq 0 (
                                echo [ERROR] Fallo en docker tag para ${service}. Código de error: %TAG_ERRORLEVEL%
                                exit /b %TAG_ERRORLEVEL%
                            )
                            echo.
                            
                            echo [CMD] Verificando imágenes Docker locales para ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG} despues del tag...
                            docker images ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG}
                            echo.

                            echo [CMD] Subiendo imagen ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG} a Docker Hub...
                            SET DOCKER_PUSH_CMD=docker push ${env.DOCKER_HUB_USER}/${lowerService}:${env.IMAGE_TAG}
                            echo [DEBUG] Ejecutando: %DOCKER_PUSH_CMD%
                            %DOCKER_PUSH_CMD%
                            SET PUSH_ERRORLEVEL=%errorlevel%
                            echo [DEBUG] Resultado del comando docker push (%%errorlevel%%): %PUSH_ERRORLEVEL%
                            if %PUSH_ERRORLEVEL% neq 0 (
                                echo [ERROR] Fallo en docker push para ${service}. Código de error: %PUSH_ERRORLEVEL%
                                exit /b %PUSH_ERRORLEVEL%
                            )
                            echo.

                            echo [SUCCESS] Imagen para ${service} construida, etiquetada y subida exitosamente.
                            echo.
                            """
                        } // Fin del bucle for

                        // Logout de Docker Hub
                        bat """
                        @echo off
                        echo [INFO] Realizando logout de Docker Hub...
                        docker logout
                        echo [DEBUG] Resultado del comando docker logout: %errorlevel%
                        """
                    } // Fin de withCredentials
                } // Fin de script
            } // Fin de steps
        } // Fin de stage 'Construir y subir imágenes Docker'
    } // Fin de stages
    
    post {
        always {
            echo '[INFO] Limpiando archivos temporales...'
            deleteDir() // Borra el workspace al final
            // O puedes ser más específico:
            // bat 'del /Q changed_services.txt diff.txt'
        }
        success {
            echo '[INFO] Pipeline finalizado con EXITO.'
        }
        failure {
            echo '[ERROR] Pipeline finalizado con FALLO.'
        }
        unstable {
            echo '[WARN] Pipeline finalizado como INESTABLE.'
        }
    }
}
