This project shows how sftp could be used by web application and provides sftp connection checks logic.

## Requirements:

Build and **install** the sftp library:
```bash
cd ..
mvn clean install
```

### Run the web application without sftp servers 
Run the application:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.import=file:./src/test/resources/app.external.config.yaml
```
You get `"isConnectionValid" : false` for all configured sftp: 
```bash
curl -i -X GET -w "\n" http://localhost:8080/rest/connections
```

### Run the web application with sftp servers, via docker compose:

Initial run:
```bash
mvn clean install && docker compose up
```
To rebuild and rerun:
```bash
./clearAndStart.sh
```
You get `"isConnectionValid" : true` for 2 of 3 configured sftp:
```bash
curl -i -X GET -w "\n" http://localhost:8080/rest/connections
```
```json
{
  "sftpServers" : [ {
    "sftpServer" : {
      "host" : "sftp",
      "port" : 22,
      "user" : "foo"
    },
    "isConnectionValid" : true
  }, {
    "sftpServer" : {
      "host" : "sftp2",
      "port" : 22,
      "user" : "foo"
    },
    "isConnectionValid" : true
  }, {
    "sftpServer" : {
      "host" : "localhost",
      "port" : 24,
      "user" : "foo"
    },
    "isConnectionValid" : false
  } ]
}
```
