package com.example.sftp;

import com.example.sftp.config.AppExternalConfiguration;
import com.example.sftp.config.SftpDiConfiguration;
import com.example.sftp.docker.TestMultipleSftpContainers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ContextConfiguration(classes = {
  SftpDiConfiguration.class,
  TestMultipleSftpContainers.class})
@Testcontainers
@PropertySource("classpath:external.config.yaml")
public class MultipleSftpServersTest {

  @Autowired
  private AppExternalConfiguration appExternalConfiguration;

  @Test
  public void testMultipleSftpServers() {
    appExternalConfiguration.getSftpServers()
      .forEach(sftpServerConfig -> {
        try (SftpService sftpService = (SftpService) SftpService.instance(sftpServerConfig)) {
          Assertions.assertTrue(sftpService.sftpChannel().isConnected(),
          () -> "Could not connect to '= " + sftpServerConfig.getHost() + "' sftp");
        }
      });
  }
}
