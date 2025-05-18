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
                script {
                    sh "git fetch origin"

                    def diff = sh(script: "git diff --name-only ${params.BASE_BRANCH}", returnStdout: true).trim()
                    echo "Cambios detectados:\n${diff}"

                    changedServices = []

                    for (service in SERVICES) {
                        if (diff.split('\n').any { it.startsWith("${service}/") }) {
                            changedServices << service
                        }
                    }

                    if (changedServices.isEmpty()) {
                        echo "No se detectaron cambios en microservicios."
                        currentBuild.result = 'SUCCESS'
                        error('No hay servicios modificados. Deteniendo pipeline.')
                    } else {
                        echo "Servicios modificados: ${changedServices}"
                    }
                }
            }
        }

        stage('Construir y testear servicios modificados') {
            steps {
                script {
                    for (service in changedServices) {
                        dir(service) {
                            echo "Ejecutando build para ${service}"

                            if (fileExists('gradlew')) {
                                sh './gradlew clean test build'
                            } else if (fileExists('build.gradle')) {
                                sh 'gradle clean test build'
                            } else if (fileExists('pom.xml')) {
                                sh 'mvn clean test package'
                            } else {
                                echo "⚠️ No se encontró archivo de build en ${service}, omitiendo..."
                            }
                        }
                    }
                }
            }
        }
    }
}
