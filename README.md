# config-service
## Environment variables
Running the **config-service** microservice requires setting the below environment variables.
-  ENCRYPT_KEY_FILE: In Spring Cloud Config, the **encrypt.key** property (or **ENCRYPT_KEY** environment variable) is used to provide a symmetric encryption key that enables the Config Server to both encrypt and decrypt sensitive data stored in configuration files, such as passwords or API keys. The **ENCRYPT_KEY_FILE** environment variable in our context is assumed to be the path to a file containing the symmetric encryption key. The config-service microservice will read the later file at startup, and use its content to set the **encrypt.key** system property.
- GIT_PRIVATE_KEY_FILE: This environment variable is assumed to be the path to a file containing the private key used in SSH communications with Git platforms such as GitHub. The content of the later file will be used to set the **spring.cloud.config.server.git.privateKey** system property.
- SPRING_CLOUD_CONFIG_SERVER_GIT_URI: This is an environment variable or configuration property used to specify the URL of a Git repository that serves as the backend for a Spring Cloud Config Server. This tells the config server where to find externalized configuration files for client applications, enabling centralized management of application properties across a distributed system.
- SPRING_CLOUD_CONFIG_SERVER_GIT_SEARCHPATHS: The **spring.cloud.config.server.git.searchPaths** property (or **SPRING_CLOUD_CONFIG_SERVER_GIT_SEARCHPATHS** environment variable) is a configuration property in Spring Cloud Config Server that specifies additional folders within a remote Git repository where the server should search for configuration files, beyond the default root folder.
- SPRING_CLOUD_CONFIG_SERVER_GIT_DEFAULTLABEL: This environment variable is used in checking out the Git branch or tag.
- SPRING_PROFILES_ACTIVE: This environment variable should contain the Spring profiles to activate.
- EUREKA_URI: This environment variable should contain the URI of the Eureka service discovery endpoint for the default availability zone.
## Sample Environment Variable File
A sample environment variable file that can be used to run the service on a local development environment could contain the below content:
```
#!/bin/bash

ENVIRONMENT=local

SECRETS_DIR="$HOME"/Development/Projects/nguiland/.vscode/ostock/secrets/"$ENVIRONMENT"

mkdir -p "$SECRETS_DIR"

export ENCRYPT_KEY_FILE="$SECRETS_DIR"/encrypt.key
export GIT_PRIVATE_KEY_FILE="$SECRETS_DIR"/ssh-privatekey
export SPRING_CLOUD_CONFIG_SERVER_GIT_URI=git@github.com:biya-bi/ostock-configs.git
export SPRING_CLOUD_CONFIG_SERVER_GIT_SEARCHPATHS=*
export SPRING_CLOUD_CONFIG_SERVER_GIT_DEFAULTLABEL=develop
export SPRING_PROFILES_ACTIVE=default
export EUREKA_URI=http://localhost:9002/eureka
```
## Running on a local development terminal
Assuming the sample environment variable file is named **.config-service**, the below commands can be used to run the microservice on a local development terminal
```
source .config-service
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9001
```
