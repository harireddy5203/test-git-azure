# Project Details

## Project Overview
Provide a brief overview about the project. (TODO)

## Architecture
Cover the overall architecture of the project. (TODO)

## Build Instructions
Follow the below commands to build service.

* If you do not plan to use `Docker` to run service, run the below commands:
    * Navigate to `Codebase Root` folder.
    * Type the below commands:
        * `mvn clean install -DskipTests -Ddockerfile.build.skip=true`
    * The above command should build all the artifacts.
* If you plan to use `Docker` to run service, run the below sequence of commands. These
  commands build the `Docker` images and uses [dockerfile-maven-plugin](https://github.com/spotify/dockerfile-maven)
  from Spotify.
    * Here we are trying to run database as container and connect to this database from service. So before building image for application service we need to check DB_HOST environment variable is properly configured or not. we can use below environment variable to set the database host.
        * `export DB_HOST={DB_SERVICE_NAME}`.
          here DB_SERVICE_NAME value will be database service name in docker-compose.yml file which is located at <code base root folder>/docker folder.
    * Navigate to `<Codebase Root>` folder and run the below command:
        * `mvn clean install -DskipTests -P dockerLocal`
    * This should build all the artifacts including docker image of the service (which will be available in the local docker
      repository).

## Deployment Instructions

## Run Without Docker
If you want to run application without `Docker`, there is need to follow below steps

* Make sure database is installed and runnig in your machine with same account details which you gave while configuring database settings at the time of data modelling.
* Check the host name in `<Codebase Root>/services/{application-service}/src/main/resources/application.yml' file whether it is localhost or not. As we are running the service with out docker and trying to connect to the local db then it should be localhost. If not, you can use below environment variable to set the database host.
  `export DB_HOST={DB_HOST_NAME}`.

Start the service

You can start the service from the IDE or from the command line.
To run service from command line:
* Open a Git Bash shell.
* Navigate to `<Codebase Root>/services/{application-service} Run below command.
  mvn spring-boot:run
  

## With Docker

You do not need to make any changes if you want to run the services using `Docker`. Follow the below steps
to run using the `Docker Compose` yml file.

* Open Git Bash shell
* Navigate to `<Codebase Root>/docker` folder.
* Run the below commands to launch database and application service.
    * `docker-compose -f docker-compose.yml up -d` (wait until it starts -
      Use `docker logs -f <container id>` for logs)
      
To see the api documentation please visit
http://{host}:{service-port}/swagger-ui.html 
