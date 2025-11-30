# [mini-doodle-v1] API

## 🚀 Quick Start
1. Make sure jave 21 installed -> `java --version`
2. Maven installed -> `mvn --version`
3. Docker and docker-compose installed -> `docker --version && docker-compose --version`
3. Execute `mvn clean install` to build the project
4. Execute `docker build -t mini-doodle-v1` to build application image
5. Execute `docker-compose up -d` to start the application sandbox
6. Run `curl --location --request GET 'http://localhost:8080/actuator/health'` to make sure service is UP
7. To stop sandbox run `docker-compose down`

You can also access Swagger by the link `http://localhost:8080/swagger-ui/index.html`