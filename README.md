**Notes:**

An alternative approach to work with sftp is to mount sftp to the local folder and access it as a usual local folder.

* **Pros:** It simplifies development and access
* **Cons:** It might make maintenance and problems investigation more difficult. 

### Sftp issues and solutions:

* [How to copy this project into your own](new.sftp.project.md)

* [Iterate via all files. Filtering. Reading the filtered files](#iterate-via-all-files-filtering-reading-the-filtered-files)
* [Iterate via all folders. Get all the mappings between folder and the files, it contains](#iterate-via-all-sftp-folders)
* [Sftp file/directory commands](#sftp-filedirectory-commands)
* [Get MD5 hash (not reliable)](#get-md5-hash-of-the-file)

* [Sftp timeouts settings](#sftp-timeouts-settings)
* [Ssh auth logging for sftp](#ssh-auth-logging-for-sftp)
* [Sftp logging](#sftp-logging)
* [Sftp host key](#sftp-host-key)


* [Maven dependency for `jsch`](#maven-dependency-for-jsch)
* [Official documentation](#official-documentation)


### Official documentation

- [Official documentation of `atmoz/sftp` docker image](https://github.com/atmoz/sftp)
- [sftp container configuration, see `TestSftpContainer`](src/test/java/com/example/sftp/TestSftpContainer.java)
- [Sftp codes](https://winscp.net/eng/docs/sftp_codes)
- [jsch `ChannelSftp` API](https://epaul.github.io/jsch-documentation/javadoc/com/jcraft/jsch/ChannelSftp.html)

Examples:
- [Efficient SFTP Testing with JSch, Kotlin, Testcontainers, and Spring Boot Native](https://medium.com/whozapp/sftp-test-implem-of-jsch-with-kotlin-testcontainers-and-spring-boot-native-537f624da895)
- [Testing with Kotest and TestContainers](https://medium.com/whozapp/testing-with-kotest-and-testcontainers-8e3cfce96a0b)
- [testcontainers-java `SftpContainerTest.java`](https://github.com/testcontainers/testcontainers-java/blob/main/examples/sftp/src/test/java/org/example/SftpContainerTest.java)

### Iterate via all files. Filtering. Reading the filtered files

```java
  @Test
  public void testFilesStream() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
     sftpApi.filesStream(SFTP_HOME_PATH)
       .filter(entry -> entry.getKey().endsWith("file.txt"))
       .forEach(entry -> {
         try(InputStream inputStream = entry.getValue().get()) {
           Assertions.assertEquals(
             "Testcontainers",
             new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
         } catch (IOException e) {
           throw new IllegalStateException(e);
         }
       });
    }
  }
```

### Iterate via all sftp folders

Iterate via all folders. Get all the mappings between folder and the files, it contains
```java
  @Test
  public void testDirsStream() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Map<String, List<String>> result = sftpApi.dirsStream(SFTP_HOME_PATH).collect(
        LinkedHashMap::new,
        (m, v)-> m.put(v.getKey(), v.getValue()),
        Map::putAll);
      Assertions.assertEquals(4, result.size());
      Assertions.assertTrue(result.get(SFTP_HOME_PATH).isEmpty());
      Assertions.assertEquals(2, result.get(TEST_CONTAINERS_FOLDER).size());

      try(InputStream inputStream = sftpApi.fileDownload(
        TEST_CONTAINERS_FOLDER + "/" + result.get(TEST_CONTAINERS_FOLDER)
          .getFirst())) {
        Assertions.assertEquals(
          "file 2 move",
          new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
  }
```

### Sftp file/directory commands

For these commands you know in advance the exact sftp path of the file/directory.

1. Check if file exists
    ```java
      @Test
      public void testFileExists() {
        var filePath = String.format("%s%s/%s", SFTP_HOME_PATH, WRITE_PERMISSION_FOLDER, FILE_NAME);
        try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
          Assertions.assertTrue(sftpApi.fileExists(filePath));
        };
      }
    ```
2. Get file by path
    ```java
      @Test
      public void testFileDownload() {
        var filePath = String.format("%s%s/%s", SFTP_HOME_PATH, WRITE_PERMISSION_FOLDER, FILE_NAME);
        try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
          try(InputStream inputStream = sftpApi.fileDownload(filePath)) {
            Assertions.assertEquals(
              "Testcontainers",
              new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
          } catch (IOException e) {
            throw new IllegalStateException(e);
          }
        };
      }
    ```
3. Upload file
    ```java
      @Test
      public void testFileUpload() {
        //upload `file2upload.txt` from resources into /upload/testcontainers/file_new.txt
        var filePath = String.format("%s%s/%s", SFTP_HOME_PATH, WRITE_PERMISSION_FOLDER, NEW_FILE_NAME);
        try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
          Assertions.assertFalse(sftpApi.fileExists(filePath));
          try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME_4_UPLOADING);) {
            sftpApi.fileUpload(filePath, inputStream);
            Assertions.assertTrue(sftpApi.fileExists(filePath));
          } catch (IOException e) {
            throw new IllegalStateException(e);
          }
        };
      }
    ```
4. Check if directory exists
    ```java
      @Test
      public void testDirExists() {
        try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
          Assertions.assertTrue(sftpApi.dirExists(SFTP_HOME_PATH));
        };
      }
    ```
5. Create new directory, if it does not exist
   ```java
     @Test
     public void testMkdir() {
       try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
         sftpApi.mkdir(emptyFolderPath);
       }
     }
   ```
6. Remove file
   ```java
     @Test
     public void testRemoveFile() {
       var filePath = String.format("%s%s/%s", SFTP_HOME_PATH, WRITE_PERMISSION_FOLDER, "temp.txt");
       try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
         sftpApi.remove(filePath);
       }
     }
   ```
7. Remove directory
   ```java
     @Test
     public void testRemoveDir() {
       var dirPath = String.format("%s%s%s", SFTP_HOME_PATH, WRITE_PERMISSION_FOLDER, "/tmp");
       try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
         sftpApi.removeDir(dirPath);
       }
     }
   ```
8. Move file
   ```java
     @Test
     public void testMoveFile() {
       var originalFilePath = String.format("%s%s/%s", SFTP_HOME_PATH, WRITE_PERMISSION_FOLDER, FILE_NAME_4_MOVING);
       var newFilePath = String.format("%s%s/%s", SFTP_HOME_PATH, WRITE_PERMISSION_FOLDER, "ww" + FILE_NAME_4_MOVING);
       //var newFilePath = String.format("%s%s%s", SFTP_HOME_PATH, WRITE_PERMISSION_FOLDER, "/new/path/moved.txt");
       try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
         sftpApi.mvFile(originalFilePath, newFilePath);
       }
     }
   ```
9. Move directory
   ```java
     @Test
     public void testMoveDir() {
       try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
         sftpApi.mvDir(FOLDER_2_MOVE, FOLDER_2_MOVE_NEW_PATH);
       }
     }
   ```
10. Get access to `ChannelSftp`.

    Use `SftpService` instead of `SftpApi`. It is hidden by purpose from the interface. 
    It is supposed to used in exceptional situations, when there are no helper methods, defined in `SftpApi`. 
   ```java
     @Test
     public void testChannelSftpAccess() {
       try (SftpService sftpService = (SftpService) SftpService.instance(sftpConfiguration)) {
         //do not close `sftpChannel`, channel will be closed by SftpService.close()
         var sftpChannel = sftpService.sftpChannel();
       }
     }
   ```
11. Set permissions:
   ```java
     @Test
     public void testSetPermissions() throws SftpException {
       try (SftpService sftpService = (SftpService) SftpService.instance(sftpConfiguration)) {
         //do not close `sftpChannel`, channel will be closed by SftpService.close()
         var sftpChannel = sftpService.sftpChannel();
         //(octal) 0666 = 6*8^2 + 6*8 + 6 = 438 (decimal)
         sftpChannel.chmod(Integer.parseInt("666",8), FOLDER_2);
       }
     }
   ```

### Get MD5 hash of the file

SFTP protocol does not allow to get MD5 (or other) hash of the file.

`jsch` still allows to run commands via ssh, so you could execute `md5sum` linux command to get this hash value,
passing the real file path to this command.

The problem is that sftp file path, does not mean the real file path. 

They might be the same if user's home folder entirely is mounted via sftp, but it is not a rule.

Also depending on linux command does not make the solution generic.

As a result, it is possible to implement, but it will be an exception and cannot be part of the sftp "library".

### Sftp timeouts settings

Note: I could not catch timeout exception with `atmoz/sftp`. 
Setting timeouts to 30 seconds, and waiting for 1 minute between sftp commands, did not trigger timeouts exception.
Need to be investigated.

Setting timeouts:
1. Per session (default is 0 - meaning infinite): `jschSession.setTimeout(SFTP_TIMEOUT);`
2. Per connection (default - 20 seconds): `jschSession.connect(SFTP_TIMEOUT);`
3. Using timeout socket factory:
   ```java
   import com.jcraft.jsch.SocketFactory;
   
   import static com.example.sftp.SftpApi.SFTP_TIMEOUT_MILLIS;
   
   public class SocketFactoryWithTimeout implements SocketFactory {
   
      public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
         var socket = new Socket();
         socket.connect(new InetSocketAddress(host, port), SFTP_TIMEOUT_MILLIS);
         return socket;
      }
   
      public InputStream getInputStream(Socket socket) throws IOException {
         return socket.getInputStream();
      }
   
      public OutputStream getOutputStream(Socket socket) throws IOException {
         return socket.getOutputStream();
      }
   }
   ```
   And set it as: `jschSession.setSocketFactory(new SocketFactoryWithTimeout());`

Related issues:
- [JSch session timeout limit](https://stackoverflow.com/questions/35009009/jsch-session-timeout-limit)
- [Can I set timeout explicitely by purpose?](https://github.com/atmoz/sftp/issues/417)
- [What is the default timeout for Session and Channel in JSch library in java](https://stackoverflow.com/questions/28038747/what-is-the-default-timeout-for-session-and-channel-in-jsch-library-in-java)

### Ssh auth logging for sftp

SSH authentication logging is implemented via `SftpDebugLogger`:
```java
  private static class SftpDebugLogger implements com.jcraft.jsch.Logger {

    private static final Logger logger = LogManager.getLogger(com.example.sftp.SftpService.class.getName());

    @Override
    public boolean isEnabled(int ignored) {
      return logger.isDebugEnabled();
    }

    @Override
    public void log(int ignored, String message) {
      logger.debug(() -> message);
    }
  }
```
that is enabled as:
```java
JSch.setLogger(new SftpDebugLogger());
```

### Sftp logging

1. On the server side - depends on the sftp server.
2. On the client side - not supported by `jSch`, you could log only ssh authentication connection logic

Related topics:
[Logging SFTP operations](https://github.com/atmoz/sftp/issues/86)
[JSch logger - where can I configure the level - **DOES NOT log** log sftp commands](https://stackoverflow.com/a/29603041/511804)
[Set #LogLevel VERBOSE](https://github.com/atmoz/sftp/blob/master/files/sshd_config)


### Sftp host key

By default, docker sftp container each time generates a new ssh host key.
To be able to use docker container with the same host, ssh host private and public keys were generated as:
```bash
ssh-keygen -t ed25519
```
and saved as [`id_ed25519_sftp_container`](src/test/resources/ssh/id_ed25519_sftp_container) and [`id_ed25519_sftp_container.pub`](src/test/resources/ssh/id_ed25519_sftp_container.pub)

[Those files were copied to container:](src/test/java/com/example/sftp/TestSftpContainer.java)
```java
    .withCopyFileToContainer(
      MountableFile.forClasspathResource("ssh/id_ed25519_sftp_container", 600),
      "/etc/ssh/ssh_host_ed25519_key")
    .withCopyFileToContainer(
      MountableFile.forClasspathResource("ssh/id_ed25519_sftp_container.pub", 600),
      String.format("/home/%s/.ssh/keys/id_ed25519_client.pub", SFTP_LOGIN))
```

Note: for testing and only for testing we set `StrictHostKeyChecking=no` via: `JSch.setConfig("StrictHostKeyChecking", "no");`


### Maven dependency for `jsch`
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