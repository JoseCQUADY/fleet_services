pipeline {
    agent any

    parameters {
        string(name: 'BASE_BRANCH', defaultValue: 'origin/main', description: 'Rama base para detectar cambios')
    }

    environment {
        SERVICES = """ms-administrator-service
                      ms-api-gateway
                      ms-auth-service
                      ms-driver-service
                      ms-invitation-service
                      ms-route-service
                      ms-vehicle-service""".split()
    }

    stages {
        stage('Detectar cambios en servicios') {
            steps {
                bat '''
                echo Haciendo fetch de la rama base...
                git fetch origin

                echo Detectando archivos modificados desde %BASE_BRANCH%...
                FOR /F "tokens=*" %%i IN ('git diff --name-only %BASE_BRANCH%') DO (
                    echo %%i>> diff.txt
                )

                set CHANGED_SERVICES=
                for %%S in (%SERVICES%) do (
                    findstr /B "%%S/" diff.txt >nul && set CHANGED_SERVICES=!CHANGED_SERVICES! %%S
                )

                if not defined CHANGED_SERVICES (
                    echo No se detectaron cambios en microservicios.
                    exit /b 1
                )

                echo Servicios modificados: %CHANGED_SERVICES%
                echo %CHANGED_SERVICES% > changed_services.txt
                '''
            }
        }

        stage('Construir y testear servicios modificados') {
            steps {
                bat '''
                set /p CHANGED_SERVICES=< changed_services.txt

                for %%S in (%CHANGED_SERVICES%) do (
                    echo Construyendo %%S
                    cd %%S

                    if exist gradlew (
                        call gradlew.bat clean test build
                    ) else if exist build.gradle (
                        call gradle clean test build
                    ) else if exist pom.xml (
                        call mvn clean test package
                    ) else (
                        echo ⚠️ No se encontró archivo de build en %%S, omitiendo...
                    )

                    cd ..
                )
                '''
            }
        }
    }
}
