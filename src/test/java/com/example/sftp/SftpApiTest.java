package com.example.sftp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.example.sftp.TestSftpContainer.SFTP_HOME_PATH;

@SpringBootTest
@ContextConfiguration(classes = {
  SftpDiConfiguration.class,
  TestSftpContainer.class})
@Testcontainers
public class SftpApiTest {

  private static final Logger logger = LogManager.getLogger(SftpApiTest.class.getName());

  @Autowired
  private SftpConfiguration sftpConfiguration;

  @Test
  public void test() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
     sftpApi.filesStream(SFTP_HOME_PATH)
       .forEach(entry -> {
         logger.debug("file name = " + entry.getKey());
         try(InputStream inputStream = entry.getValue().get()) {
           logger.debug("file content:\n");
           logger.debug(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
         } catch (IOException e) {
           throw new IllegalStateException(e);
         }
       });
    };
  }
}
