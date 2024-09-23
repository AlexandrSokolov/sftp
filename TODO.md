### external configuration in Spring, passing to docker container, in tests 
 move to spring indepenent project, leave only sftp-related condigs

```bash
$ java -jar myproject.jar --spring.config.import=\
    classpath:datasource.properties,\
    classpath:mysql-properties.properties,\
    optional:file:./cloud-deployment.properties,\
    classpath:test-properties/
```

how to pass separately in integration test,
how to pass separately in docker composition!

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

### timeout issue

Looks as it is entirely ignored:
I've set: `jschSession.setTimeout(100);`


In the test, it did not work.
Comments with exception,just possible exceptions that could be catched, but were not. 
```java
  @Test
  public void testSftpTimeout() throws InterruptedException {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertTrue(sftpApi.fileExists("/upload/testcontainers/file.txt"));
      TimeUnit.MINUTES.sleep(1);
      //java.net.SocketTimeoutException
      //Caused by: com.jcraft.jsch.JSchException: java.net.ConnectException: Connection timed out:
      //com.jcraft.jsch.JSchException: Session.connect: java.net.SocketTimeoutException: Read timed out
      var exception = Assertions.assertThrows(
        SftpException.class,
        () -> sftpApi.fileExists("/upload/testcontainers/file.txt"));
      Assertions.assertEquals(
        "fs",
        exception.getMessage()
      );
    }
  }
```