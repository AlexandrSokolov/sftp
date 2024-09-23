package com.example.sftp;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface SftpApi extends AutoCloseable {

  Integer SFTP_TIMEOUT_MILLIS = (int) TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);

  /**
   * Iterates only via files, providing to the caller a simple way to access file path and its InputStream
   *
   * @param sftpPath
   * @return
   */
  Stream<Map.Entry<String, Supplier<InputStream>>> filesStream(String sftpPath);

  /**
   * Iterates via all folders, including folders with no files in it, providing to the caller mappings between folder path and its files.
   * The caller by itself, decide what to do with those files using `SftpApi` api
   *
   * @param sftpPath
   * @return
   */
  Stream<Map.Entry<String, List<String>>> dirsStream(String sftpPath);

  boolean fileExists(String sftpPath);

  InputStream fileDownload(String sftpPath);

  void fileUpload(String sftpPath, InputStream inputStream);

  boolean dirExists(String sftpPath);

  /**
   * Creates the full dir path. If there is an empty folder /1/2 and you want to create /1/2/3/4/5
   *
   * Just pass the whole `/1/2/3/4/5` path. 3 additional folders: `3`, `4`, `5` will be created
   *
   * You can create dirs in subfolder, only if it has write permissions
   *
   * Example:
   * sftp> ls -lh
   * drwxr-xr-x  ... upload
   * You cannot write into `upload` folder directly. If you try to create `/upload/new/dir` you get `Permission denied`
   *
   * sftp> cd upload/
   * sftp> ls -lh
   * drwxrwxrwx  ... testcontainers
   * `testcontainers` has write permissions. You can create folder under it as `/upload/testcontainers/new/dir`
   *
   * @param sftpPath
   */
  void mkdir(String sftpPath);

  void remove(String sftpPath);

  void removeDir(String sftpPath);

  void mvFile(String sourcePath, String destinationPath);

  void mvDir(String sourcePath, String destinationPath);

  void close();
}
