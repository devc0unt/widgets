# Widgets
REST API server application for widgets with coordinates

## Documentation

Api documentation is available on swagger endpoint: http://localhost:8080/v2/api-docs

Also docs available as json in `swagger/swagger.json`  

## REST endpoints

#### Create widget
```
POST /api/v1/widgets
```
Example:
```
curl -X POST "http://localhost:8080/api/v1/widgets" -H "Content-Type: application/json" -d "{\"x\":1,\"y\":2,\"width\":3,\"height\":4}"
```
Output:
```
{"id":1,"width":3,"height":4,"modifiedAt":"2020-10-16T10:51:06.669523","x":1,"y":2,"z":1}
```

#### Get widget by id
```
GET /api/v1/widgets/{id}
```
`{id}` - id of widget

Example
```
curl -X GET "http://localhost:8080/api/v1/widgets/1"
```
Output:
```
{"id":1,"width":3,"height":4,"modifiedAt":"2020-10-16T10:51:06.669523","x":1,"y":2,"z":1}
```

#### Get widgets list
```
GET /api/v1/widgets
```
Params:

`limit` Integer - max number of widgets to return

`offset` Integer - offset for output

Example
```
curl -X GET "http://localhost:8080/api/v1/widgets?limit=1&offset=0"
```
Output:
```
[{"id":1,"width":3,"height":4,"modifiedAt":"2020-10-16T19:51:06.669523","x":1,"y":2,"z":1}]
```

#### Update widget
```
PUT /api/v1/widgets
```
Example
```
curl -X PUT "http://localhost:8080/api/v1/widgets" -H "Content-Type: application/json" -d "{\"id\":1,\"x\":1,\"y\":12,\"width\":3,\"height\":4}"
```
Output:
```
{"id":1,"width":3,"height":4,"modifiedAt":"2020-10-16T19:07:43.153376","x":12,"y":2,"z":2}
```

### Delete widget
```
DELETE /api/v1/widgets/{id}
```
`{id}` - id of widget

Example
```
curl -X DELETE "http://localhost:8080/api/v1/widgets/1"
```

## Installation

To build this application you will need:
* JDK 11
* Maven 3

The following steps are required to build and run the app:
1. Clean output and create jar file from sources by calling
    ```
    mvn clean package
    ```
2. To run the application execute command
   ```
   mvn spring-boot:run
   ```
   
## Run with Docker

The following steps are required to build and run the app with Docker (on Ubuntu):
1. Build Docker image
    ```
    docker build -t widgets:1.0 .
    ```
   Example output:
    ``` 
    Sending build context to Docker daemon  58.57MB
    Step 1/5 : FROM adoptopenjdk/openjdk11:alpine-jre
    alpine-jre: Pulling from adoptopenjdk/openjdk11
    df20fa9351a1: Already exists 
    ffa7158a1780: Pull complete 
    dce2455ca101: Pull complete 
    Digest: sha256:3706d705804e2149a9c876ed7aa432f3cb6dfb06061d237f948a185158c71a4c
    Status: Downloaded newer image for adoptopenjdk/openjdk11:alpine-jre
     ---> a7b99112d065
    Step 2/5 : ARG JAR_FILE=target/widgets-1.0.0.jar
     ---> Running in 7fd2d26a8ad3
    Removing intermediate container 7fd2d26a8ad3
     ---> 3f543c4e68aa
    Step 3/5 : WORKDIR /opt/app
     ---> Running in 01dbd1b7d614
    Removing intermediate container 01dbd1b7d614
     ---> 49945b322b77
    Step 4/5 : COPY ${JAR_FILE} app.jar
     ---> 4bf92331634c
    Step 5/5 : ENTRYPOINT ["java","-jar","app.jar"]
     ---> Running in 7f7cedd5660d
    Removing intermediate container 7f7cedd5660d
     ---> be553627b807
    Successfully built be553627b807
    Successfully tagged widgets:1.0
    ```
   You may need to use `sudo` for all docker commands if your user is not added to docker group or add user to docker group:
   ```
   sudo usermod -aG docker $USER
   ```
   
2. To run the application execute command
   ```
   docker run -d -p 8080:8080 -t widgets:1.0
   ```
   Application will be started on `localhost:8080`
   
3. To stop container - first find it with:
    ```
    docker ps
    ```
   Example output:
   ```
   CONTAINER ID        IMAGE               COMMAND               CREATED              STATUS              PORTS                    NAMES
   b0065b92a73d        widgets:1.0         "java -jar app.jar"   About a minute ago   Up About a minute   0.0.0.0:8080->8080/tcp   ecstatic_wing
   ```
   After that stop this container: 
   ```
   docker stop b0065b92a73d
   ```
  
## Coverage

Coverage report can be found in `coverage` directory - `index.html` file

![Coverage](coverage.png?raw=true "Coverage")
