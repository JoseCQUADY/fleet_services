pipeline {
    agent any

    parameters {
        string(name: 'BASE_BRANCH', defaultValue: 'origin/main', description: 'Rama base para detectar cambios')
    }

    environment {
        SERVICES_STRING = 'ms-administrator-service ms-api-gateway ms-auth-service ms-driver-service ms-invitation-service ms-route-service ms-vehicle-service'
    }

    stages {
        stage('Detectar cambios en servicios') {
            steps {
                bat '''
                @echo off
                setlocal enabledelayedexpansion

                echo Haciendo fetch de la rama base...
                git fetch origin

                echo Detectando archivos modificados desde %BASE_BRANCH%...
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

        stage('Construir servicios modificados (sin tests)') {
            steps {
                bat '''
                @echo off
                setlocal enabledelayedexpansion

                set /p CHANGED_SERVICES=< changed_services.txt

                for %%S in (!CHANGED_SERVICES!) do (
                    echo ===============================
                    echo Construyendo %%S (sin tests)
                    echo ===============================
                    cd %%S

                    if exist gradlew.bat (
                        call gradlew.bat clean build -x test
                    ) else if exist build.gradle (
                        call gradle clean build -x test
                    ) else if exist pom.xml (
                        call mvn clean package -DskipTests
                    ) else (
                        echo ⚠️ No se encontró archivo de build en %%S, omitiendo...
                    )

                    cd ..
                )
                endlocal
                '''
            }
        }
    }
}
