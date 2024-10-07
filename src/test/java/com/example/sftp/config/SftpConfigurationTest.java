package com.example.sftp.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.example.sftp.config.SftpConfiguration.DEFAULT_SFTP_HOME;

public class SftpConfigurationTest {

  @Test
  public void testSftpConfigurationRecord() {
    var sftpConfiguration = new SftpConfiguration(
      "abc.com",
      22,
      "foo",
      "pass",
      null,
      null, //home
      null);
    Assertions.assertEquals(
      DEFAULT_SFTP_HOME,
      sftpConfiguration.home()
    );
  }
}
