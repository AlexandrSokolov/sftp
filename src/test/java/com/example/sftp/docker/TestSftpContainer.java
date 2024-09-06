package com.example.sftp.docker;

import com.jcraft.jsch.JSch;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.List;

import static com.example.sftp.docker.SftpDockerConstants.*;

@TestConfiguration
@Testcontainers
public class TestSftpContainer {

  //All keys from `/home/foo/.ssh/keys` are automatically appended to .ssh/authorized_keys
  @Container
  public static GenericContainer<?> sftp = new GenericContainer<>("atmoz/sftp:alpine-3.7")
    //the users can't create new files directly under their own home directory!
    //we can create only in `/upload/testcontainers` on sftp
    //copy `testcontainers` folder from classpath resource into `/home/foo/upload/testcontainers` on sftp
    .withCopyFileToContainer(
      MountableFile.forClasspathResource("testcontainers/", 0777),
      "/home/foo/upload/testcontainers")
    .withCopyFileToContainer(
      MountableFile.forClasspathResource("ssh/id_ed25519_sftp1_container", 600),
      "/etc/ssh/ssh_host_ed25519_key")
    .withCopyFileToContainer(
      MountableFile.forClasspathResource("ssh/id_ed25519_sftp1_container.pub", 600),
      String.format("/home/%s/.ssh/keys/id_ed25519_client.pub", SFTP_LOGIN))
    .withExposedPorts(SFTP_CONTAINER_PORT) //only for documentation purpose
    //user "foo" with password "pass" can login with sftp and upload files to a folder called "upload"
    .withCommand(String.format("%s:%s:::%s", SFTP_LOGIN, SFTP_PASSWORD, SFTP_HOME_PATH));

  static {
    JSch.setConfig("StrictHostKeyChecking", "no");
    sftp.setPortBindings(List.of(SFTP1_HOST_PORT + ":" + SFTP_CONTAINER_PORT));
    sftp.start();
  }

}
