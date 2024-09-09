### md5 of the files

### external configuration 
 move to spring indepenent project, leave only sftp-related condigs

```bash
$ java -jar myproject.jar --spring.config.import=\
    classpath:datasource.properties,\
    classpath:mysql-properties.properties,\
    optional:file:./cloud-deployment.properties,\
    classpath:test-properties/
```

how to pass in integration test,
how to pass in docker composition!

docker composition: see in docker-compose.yaml
To build the project:
```bash
docker container rm cs-app && \
  docker image rm cs-app:latest && \
  mvn -o clean install && \
  mkdir -p target/sftp/testcontainers && \
  docker compose up

```

TODO - add files to sftp for testing purpose

### move all the configurations into spring config demo project, leave in this project only sftp-specific

### how to mount sftp to a local folder

### what is it SftpProgressMonitor

public void put(String src, String dst, SftpProgressMonitor monitor)

### spring provides its own sftp integration solution!

### sftp server logging

[Logging SFTP operations](https://github.com/atmoz/sftp/issues/86)

# Enable this for more logs
#LogLevel VERBOSE
https://github.com/atmoz/sftp/blob/master/files/sshd_config
