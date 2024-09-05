package com.example.sftp;

import com.jcraft.jsch.JSch;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.List;

@TestConfiguration
@Testcontainers
public class TestSftpContainer {

  public static final String SFTP_LOGIN = "foo";
  public static final String SFTP_PASSWORD = "pass";
  public static final String SFTP_HOME_PATH = "/upload";
  public static final String TEST_CONTAINERS_FOLDER = SFTP_HOME_PATH + "/testcontainers";
  public static final String NEW_FOLDER_PATH = "/f1/f2/f3";
  public static final String FOLDER_2_MOVE = TEST_CONTAINERS_FOLDER + "/folder1";
  public static final String FOLDER_2 = TEST_CONTAINERS_FOLDER + "/folder2";
  public static final String FOLDER_2_MOVE_NEW_PATH = TEST_CONTAINERS_FOLDER + "/folder2/folderMoved";
  public static final String FILE_NAME = "file.txt";
  public static final String FILE_NAME_4_UPLOADING = "file2upload.txt";
  public static final String FILE_NAME_4_MOVING_1 = "file2move_1.txt";
  public static final String FILE_NAME_4_MOVING_2 = "file2move_2.txt";
  public static final String FILE_NAME_4_MOVING_3 = "file2move_3.txt";
  public static final String NEW_FILE_NAME = "file_new.txt";
  public static final int SFTP_PORT = 22;

  @Container
  public static GenericContainer<?> sftp = new GenericContainer<>("atmoz/sftp:alpine-3.7")
    //the users can't create new files directly under their own home directory!
    //we can create only in `/upload/testcontainers` on sftp
    //copy `testcontainers` folder from classpath resource into `/home/foo/upload/testcontainers` on sftp
    .withCopyFileToContainer(
      MountableFile.forClasspathResource("testcontainers/", 0777),
      "/home/foo/upload/testcontainers")
    .withCopyFileToContainer(
      MountableFile.forClasspathResource("ssh/id_ed25519_sftp_container", 600),
      "/etc/ssh/ssh_host_ed25519_key")
    .withCopyFileToContainer(
      MountableFile.forClasspathResource("ssh/id_ed25519_sftp_container.pub", 600),
      String.format("/home/%s/.ssh/keys/id_ed25519_client.pub", SFTP_LOGIN))
    .withExposedPorts(SFTP_PORT) //only for documentation purpose
    //user "foo" with password "pass" can login with sftp and upload files to a folder called "upload"
    .withCommand(String.format("%s:%s:::%s", SFTP_LOGIN, SFTP_PASSWORD, SFTP_HOME_PATH));

  static {
    JSch.setConfig("StrictHostKeyChecking", "no");
    sftp.setPortBindings(List.of("22:22"));
    sftp.start();
  }

  @Bean
  public SftpConfiguration sftpConfiguration() {
    return new SftpConfiguration() {
      @Override
      public String host() {
        return sftp.getHost();
      }

      @Override
      public Integer port() {
        return sftp.getMappedPort(SFTP_PORT);
      }

      @Override
      public String sftpUser() {
        return SFTP_LOGIN;
      }

      @Override
      public String sftpPassword() {
        return SFTP_PASSWORD;
      }
    };
  }

}
