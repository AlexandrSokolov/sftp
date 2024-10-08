package com.example.sftp;

import com.example.sftp.config.SftpConfiguration;
import com.example.sftp.config.TestSftpConfiguration;
import com.example.sftp.config.TestsConfiguration;
import com.example.sftp.docker.TestSftpContainer;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.example.sftp.docker.SftpDockerConstants.*;

@SpringBootTest
@ContextConfiguration(classes = {
  TestsConfiguration.class,
  TestSftpContainer.class})
@Testcontainers
public class SftpApiTest {

  private static final Logger logger = LogManager.getLogger(SftpApiTest.class.getName());


  @Autowired
  private TestSftpConfiguration sftpServersConfiguration;

  private SftpConfiguration sftpConfiguration;

  @BeforeEach
  public void init() {
    Assertions.assertNotNull(sftpServersConfiguration);
    Assertions.assertNotNull(sftpServersConfiguration.sftpServers());
    sftpConfiguration = sftpServersConfiguration
      .sftpServers()
      .getFirst();
  }

  @Test
  public void testFilesStream() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
     sftpApi.filesStream(SFTP_HOME_PATH)
       .filter(entry -> entry.getKey().endsWith("file.txt"))
       .forEach(entry -> {
         Assertions.assertEquals(
           String.format("%s/%s", TEST_CONTAINERS_FOLDER, FILE_NAME),
           entry.getKey());
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

  @Test
  public void testDirsStream() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Map<String, List<String>> result = sftpApi.dirsStream(SFTP_HOME_PATH).collect(
        LinkedHashMap::new,
        (m, v)-> m.put(v.getKey(), v.getValue()),
        Map::putAll);
      Assertions.assertEquals(4, result.size());
      Assertions.assertTrue(result.get(SFTP_HOME_PATH).isEmpty());
      Assertions.assertEquals(4, result.get(TEST_CONTAINERS_FOLDER).size());

      try(InputStream inputStream = sftpApi.fileDownload(
        TEST_CONTAINERS_FOLDER + "/" + result.get(TEST_CONTAINERS_FOLDER).stream()
          .max(String::compareTo)
          .orElseThrow(IllegalStateException::new))) {
        Assertions.assertEquals(
          "file 2 move 3",
          new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  @Test
  public void testDirsFromHomeStream() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Map<String, List<String>> result = sftpApi.dirsStream(
          sftpConfiguration.home())
        .collect(
          LinkedHashMap::new,
          (m, v)-> m.put(v.getKey(), v.getValue()),
          Map::putAll);
      Assertions.assertEquals(7, result.size());
      Assertions.assertTrue(result.get(SFTP_HOME_PATH).isEmpty());
      Assertions.assertEquals(4, result.get(TEST_CONTAINERS_FOLDER).size());
    }
  }

  //sometimes, the path can be configured as "./":
  @Test
  public void testDirsFromCurrentStream() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Map<String, List<String>> result = sftpApi.dirsStream(
          "./")
        .collect(
          LinkedHashMap::new,
          (m, v)-> m.put(v.getKey(), v.getValue()),
          Map::putAll);
      Assertions.assertEquals(7, result.size());
      Assertions.assertTrue(result.get("." + SFTP_HOME_PATH).isEmpty());
      Assertions.assertEquals(4, result.get("." + TEST_CONTAINERS_FOLDER).size());
    }
  }

  @Test
  public void testFileExists() {
    var filePath = String.format("%s/%s", TEST_CONTAINERS_FOLDER, FILE_NAME);
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertTrue(sftpApi.fileExists(filePath));
    }
  }

  @Test
  public void testFileDownload() {
    var filePath = String.format("%s/%s", TEST_CONTAINERS_FOLDER, FILE_NAME);
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      try(InputStream inputStream = sftpApi.fileDownload(filePath)) {
        Assertions.assertEquals(
          "Testcontainers",
          new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  @Test
  public void testFileUpload() {
    //upload `file2upload.txt` from resources into /upload/testcontainers/file_new.txt
    var filePath = String.format("%s/%s", TEST_CONTAINERS_FOLDER, NEW_FILE_NAME);
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertFalse(sftpApi.fileExists(filePath));
      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME_4_UPLOADING)) {
        sftpApi.fileUpload(filePath, inputStream);
        Assertions.assertTrue(sftpApi.fileExists(filePath));
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  @Test
  public void testDirExists() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertTrue(sftpApi.dirExists(SFTP_HOME_PATH));
    }
  }

  @Test
  public void testDirExists4NotExisting() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertFalse(sftpApi.dirExists("/some/not/existing/path"));
    }
  }

  @Test
  public void testMkdir() {
    var emptyFolderPath = TEST_CONTAINERS_FOLDER + NEW_FOLDER_PATH;
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      //folder does not exist yet
      Assertions.assertFalse(sftpApi.dirExists(emptyFolderPath));
      sftpApi.mkdir(emptyFolderPath);
      //folder created
      Assertions.assertTrue(sftpApi.dirExists(emptyFolderPath));
      //folder has no files in it
      Assertions.assertTrue(sftpApi.filesStream(emptyFolderPath).toList().isEmpty());

      //delete the created folder, to avoid side effects with the other tests:
      sftpApi.removeEmptyDir(TEST_CONTAINERS_FOLDER + NEW_FOLDER_PATH);
      sftpApi.removeEmptyDir(TEST_CONTAINERS_FOLDER + "/f1/f2");
      sftpApi.removeEmptyDir(TEST_CONTAINERS_FOLDER + "/f1");
    }
  }

  @Test
  public void testMkDirPermissionDenied() {
    var newFolder = SFTP_HOME_PATH + NEW_FOLDER_PATH;
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      var e = Assertions.assertThrows(
        IllegalStateException.class, () -> sftpApi.mkdir(newFolder)
      );
      Assertions.assertEquals(
        "Permission denied. Sftp path: '" + newFolder + "' Permissions for existing paths: {/upload=drwxr-xr-x}",
        e.getMessage());
    }
  }

  @Test
  public void testRemoveFile() {
    var filePath = String.format("%s/%s", TEST_CONTAINERS_FOLDER, "temp.txt");
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertFalse(sftpApi.fileExists(filePath));
      sftpApi.fileUpload(filePath, new ByteArrayInputStream("".getBytes() ));
      Assertions.assertTrue(sftpApi.fileExists(filePath));

      sftpApi.remove(filePath);
      Assertions.assertFalse(sftpApi.fileExists(filePath));
    }
  }

  @Test
  public void testRemoveEmptyDir() {
    var dirPath = String.format("%s%s", TEST_CONTAINERS_FOLDER, "/tmp");
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertFalse(sftpApi.dirExists(dirPath));
      sftpApi.mkdir(dirPath);
      Assertions.assertTrue(sftpApi.dirExists(dirPath));

      sftpApi.removeEmptyDir(dirPath);
      Assertions.assertFalse(sftpApi.dirExists(dirPath));
    }
  }

  @Test
  public void testMoveFileWithinTheSameFolder() {
    var originalFilePath = TEST_CONTAINERS_FOLDER + "/" + FILE_NAME_4_MOVING_1;
    var newFilePath = TEST_CONTAINERS_FOLDER + "/moved_" + FILE_NAME_4_MOVING_1;
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertFalse(sftpApi.fileExists(newFilePath));

      sftpApi.mvFile(originalFilePath, newFilePath);
      Assertions.assertTrue(sftpApi.fileExists(newFilePath));
      Assertions.assertFalse(sftpApi.fileExists(originalFilePath));
    }
  }

  @Test
  public void testMoveFile2ExistingFolder() {
    var originalFilePath = TEST_CONTAINERS_FOLDER + "/" + FILE_NAME_4_MOVING_2;
    // `/upload/testcontainers/folder1` preexisting folder
    var newFilePath = TEST_CONTAINERS_FOLDER + "/folder2/moved_" + FILE_NAME_4_MOVING_2;
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertFalse(sftpApi.fileExists(newFilePath));

      sftpApi.mvFile(originalFilePath, newFilePath);
      Assertions.assertTrue(sftpApi.fileExists(newFilePath));
      Assertions.assertFalse(sftpApi.fileExists(originalFilePath));

      //to avoid side effects:
      sftpApi.mvFile(newFilePath, originalFilePath);
    }
  }

  @Test
  public void testMoveFile2NotExistingFolder() {
    var originalFilePath = TEST_CONTAINERS_FOLDER + "/" + FILE_NAME_4_MOVING_3;
    var newFilePath = TEST_CONTAINERS_FOLDER + "/new/path/moved.txt";
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertFalse(sftpApi.fileExists(newFilePath));

      sftpApi.mvFile(originalFilePath, newFilePath);
      Assertions.assertTrue(sftpApi.fileExists(newFilePath));
      Assertions.assertFalse(sftpApi.fileExists(originalFilePath));

      //to avoid side effects with the other tests:
      sftpApi.mvFile(newFilePath, originalFilePath);
      sftpApi.removeEmptyDir(TEST_CONTAINERS_FOLDER + "/new/path");
      sftpApi.removeEmptyDir(TEST_CONTAINERS_FOLDER + "/new");
    }
  }

  @Test
  public void testMoveDir() {
    try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
      Assertions.assertTrue(sftpApi.dirExists(FOLDER_2_MOVE));
      //check that file exists in the original folder
      Assertions.assertEquals(1, sftpApi.filesStream(FOLDER_2_MOVE).toList().size());
      Assertions.assertFalse(sftpApi.dirExists(FOLDER_2_MOVE_NEW_PATH));

      sftpApi.mvDir(FOLDER_2_MOVE, FOLDER_2_MOVE_NEW_PATH);

      Assertions.assertFalse(sftpApi.dirExists(FOLDER_2_MOVE));
      Assertions.assertTrue(sftpApi.dirExists(FOLDER_2_MOVE_NEW_PATH));
      //check that file exists in the new folder
      Assertions.assertEquals(1, sftpApi.filesStream(FOLDER_2_MOVE_NEW_PATH).toList().size());
    }
  }

  @Test
  public void testSetPermissions() throws SftpException {
    try (SftpService sftpService = (SftpService) SftpService.instance(sftpConfiguration)) {
      //do not close `sftpChannel`, channel will be closed by SftpService.close()
      var sftpChannel = sftpService.sftpChannel();
      var originalPermissions = Optional.ofNullable(sftpChannel.stat(FOLDER_2))
        .map(SftpATTRS::getPermissionsString)
        .orElseThrow(() -> new IllegalStateException("Could not get stat for " + FOLDER_2));
      Assertions.assertEquals("drwxrwxrwx", originalPermissions);
      //(octal) 0666 = 6*8^2 + 6*8 + 6 = 438 (decimal)
      sftpChannel.chmod(Integer.parseInt("666",8), FOLDER_2);

      var updatedPermissions = Optional.ofNullable(sftpChannel.stat(FOLDER_2))
        .map(SftpATTRS::getPermissionsString)
        .orElseThrow(() -> new IllegalStateException("Could not get stat for " + FOLDER_2));
      Assertions.assertEquals("drw-rw-rw-", updatedPermissions);
    }
  }

  @Test
  public void testChannelSftpAccess() {
    try (SftpService sftpService = (SftpService) SftpService.instance(sftpConfiguration)) {
      //do not close `sftpChannel`, channel will be closed by SftpService.close()
      var sftpChannel = sftpService.sftpChannel();
      Assertions.assertNotNull(sftpChannel);
      Assertions.assertTrue(sftpChannel.isConnected());
    }
  }
}
