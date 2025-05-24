stage('Construir y subir imágenes Docker') {
    steps {
        script {
            def services = ["ms-administrator-service", "ms-api-gateway", "ms-auth-service", "ms-driver-service", "ms-invitation-service", "ms-route-service", "ms-vehicle-service"]
            def dockerUsername = "jcq12"

            withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"

                for (service in services) {
                    echo "Iniciando construcción y subida para ${service}..."

                    dir("${service}") {
                        bat "gradlew dockerfile --no-daemon"
                    }

                    def dockerfilePath = "${service}/build/docker/Dockerfile"
                    def dockerContext = "${service}/build/docker"
                    def imageName = "${dockerUsername}/${service}:latest"

                    bat "docker buildx build --platform linux/amd64 -t ${imageName} --push -f \"${dockerfilePath}\" \"${dockerContext}\""
                }
            }
        }
    }
}
