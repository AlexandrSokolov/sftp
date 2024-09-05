- iterate within root and its subfolders (with maxDepth) (display even empty folders)
- moving files within sftp to new not existing folders
- files removal
- uploading files to sftp

### describe use cases in readme



### how iteration with files work:

try (Stream<Path> paths = Files.find("/some/folder/path", /*maxDepth*/ 1) {
  paths.map(Path::toAbsolutePath)
    .map(Path::toString)
    .max(String.CASE_INSENSITIVE_ORDER) //take the last file
    .ifPresent(filePath -> handleFile(filePath));
}


### iterating among files in sftp 

If you do not want to expose sftp library specific types, like `ChannelSftp`
We cannot pass InputStream(s) to the caller, they must be closed and it could be an issue.
Sftp logic - gets InputStream, accepts its consumer, and after using it by consumer, closes the stream(s)


### retry logic if connection failed during iteration/processing

###

Transfer of CSV Reports from SFTP to IKEA's Google Cloud

To function properly ITO must configure:
1. Sftp to mount it to `/sftp` folder. From the system itself we must be able files under `/sftp/reporting`

   See [[IKEA] sftp configuration](https://jira-brandmaker.atlassian.net/browse/BMSUPPORT-24163)

### sftp library:

```xml
    <!-- https://mvnrepository.com/artifact/com.github.mwiede/jsch -->
    <dependency>
      <groupId>com.github.mwiede</groupId>
      <artifactId>jsch</artifactId>
      <version>0.2.18</version>
    </dependency>
```

the new version, just a new repo for its old originator that is not supported anymore:
```xml
    <dependency>
      <groupId>com.jcraft</groupId>
      <artifactId>jsch</artifactId>
      <version>0.1.55</version>
    </dependency>
```

### sftp testing

https://medium.com/whozapp/sftp-test-implem-of-jsch-with-kotlin-testcontainers-and-spring-boot-native-537f624da895
https://medium.com/whozapp/testing-with-kotest-and-testcontainers-8e3cfce96a0b
https://github.com/testcontainers/testcontainers-java/blob/main/examples/sftp/src/test/java/org/example/SftpContainerTest.java


### [docker composition with sftp](https://github.com/atmoz/sftp?tab=readme-ov-file#using-docker-compose)

### https://stackoverflow.com/questions/68459632/how-to-use-atmoz-sftp
https://github.com/atmoz/sftp

### what is it SftpProgressMonitor

public void put(String src, String dst, SftpProgressMonitor monitor)

### spring provides its own sftp integration solution!

### sftp server logging

[Logging SFTP operations](https://github.com/atmoz/sftp/issues/86)

# Enable this for more logs
#LogLevel VERBOSE
https://github.com/atmoz/sftp/blob/master/files/sshd_config

### another option to install sftp on docker

[Easy to use SFTP server from atmoz/sftp](https://hub.docker.com/r/atmoz/sftp/dockerfile)