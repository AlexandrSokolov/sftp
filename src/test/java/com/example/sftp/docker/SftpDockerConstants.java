package com.example.sftp.docker;

//constant values are taken from `src/test/resources/external.config.yaml`
public interface SftpDockerConstants {

  int SFTP_CONTAINER_PORT = 22;
  int SFTP1_HOST_PORT = 22;
  int SFTP2_HOST_PORT = 23;
  int SFTP3_HOST_PORT = 24;

  String SFTP_LOGIN = "foo";
  String SFTP_PASSWORD = "pass";

  String SFTP_HOME_PATH = "/upload";

  String TEST_CONTAINERS_FOLDER = SFTP_HOME_PATH + "/testcontainers";
  String NEW_FOLDER_PATH = "/f1/f2/f3";
  String FOLDER_2_MOVE = TEST_CONTAINERS_FOLDER + "/folder1";
  String FOLDER_2 = TEST_CONTAINERS_FOLDER + "/folder2";
  String FOLDER_2_MOVE_NEW_PATH = TEST_CONTAINERS_FOLDER + "/folder2/folderMoved";
  String FILE_NAME = "file.txt";
  String FILE_NAME_4_UPLOADING = "file2upload.txt";
  String FILE_NAME_4_MOVING_1 = "file2move_1.txt";
  String FILE_NAME_4_MOVING_2 = "file2move_2.txt";
  String FILE_NAME_4_MOVING_3 = "file2move_3.txt";
  String NEW_FILE_NAME = "file_new.txt";

}
