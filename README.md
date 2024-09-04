### Documentation




### Configure and run sftp container

- [Official documentation of `atmoz/sftp` docker image](https://github.com/atmoz/sftp)
- [sftp container configuration, see `TestSftpContainer`](src/test/java/com/example/sftp/TestSftpContainer.java)

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

###